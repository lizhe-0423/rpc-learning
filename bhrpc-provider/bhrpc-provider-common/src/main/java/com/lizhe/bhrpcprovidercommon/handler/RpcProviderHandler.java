package com.lizhe.bhrpcprovidercommon.handler;

import com.lizhe.bhrpccommon.helper.RpcServiceHelper;
import com.lizhe.bhrpccommon.threadpool.ServerThreadPool;
import com.lizhe.bhrpcconstants.RpcConstants;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.enumeration.RpcStatus;
import com.lizhe.bhrpcprotocol.enumeration.RpcType;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * RpcProviderHandle
 * {@code @description} RPC服务提供者处理器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/11 下午1:25
 * @version 1.0
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class.getName());

    //存储服务名称#版本号#分组与对象实例的映射关系
    private final Map<String, Object> handlerMap;

    //调用采用哪种类型调用真实方法
    private final String reflectType;

    public RpcProviderHandler(String reflectType, Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
        this.reflectType = reflectType;
    }

    /**
     * 重写channelRead0方法以处理接收到的消息
     * 此方法在接收到消息时被调用，它记录了接收到的数据以及当前处理器映射的内容
     * 然后将接收到的数据直接返回给发送者
     *
     * @param ctx      上下文处理器，用于处理通道的读取事件
     * @param protocol 接收到的消息对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ServerThreadPool.submit(() -> {
            RpcHeader header = protocol.getHeader();
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            RpcRequest request = protocol.getBody();
            LOGGER.debug("Receive request {}", header.getRequestId());
            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.getAsync());
                response.setOneway(request.getOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getStatus());
            } catch (Throwable t) {
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getStatus());
                LOGGER.error("RPC Server handle request error", t);
            }
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);
            ctx.writeAndFlush(responseRpcProtocol).addListener((ChannelFutureListener) channelFuture -> LOGGER.debug("Send response for request {}", header.getRequestId()));
        });
    }


    private Object handle(RpcRequest request) throws Throwable {
        String builtServiceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(builtServiceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }
        String methodName = request.getMethodName();
        Class<?> serviceBeanClass = serviceBean.getClass();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        LOGGER.debug(serviceBeanClass.getName());
        LOGGER.debug(methodName);
        if (parameterTypes != null) {
            for (Class<?> parameterType : parameterTypes) {
                LOGGER.debug(parameterType.getName());
            }
        }

        if (parameters != null) {
            for (Object parameter : parameters) {
                LOGGER.debug(parameter.toString());
            }
        }
        return invokeMethod(serviceBean, serviceBeanClass, methodName, parameterTypes, parameters);
    }

    /**
     * 调用指定的方法
     *
     * @param serviceBean    服务的实例对象
     * @param serviceClass   服务的类
     * @param methodName     方法名
     * @param parameterTypes 方法参数类型数组
     * @param parameters     方法参数对象数组
     * @return 方法调用结果
     * @throws Throwable 如果方法调用过程中抛出异常
     */
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        // 根据反射类型选择合适的方法调用方式
        switch (this.reflectType) {
            case RpcConstants.REFLECT_TYPE_JDK:
                // 使用JDK反射调用方法
                return this.invokeJDKMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            case RpcConstants.REFLECT_TYPE_CGLIB:
                // 使用CGLIB反射调用方法
                return this.invokeCgLibMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
            default:
                // 如果反射类型不受支持，则抛出异常
                throw new IllegalArgumentException("not support reflect type");
        }
    }


    /**
     * 使用 Cglib 反射调用对象的方法。
     * <p>
     * 该方法通过 Cglib 的 FastClass 机制动态调用指定对象上的方法，相比传统反射效率更高。当需要频繁调用方法时，
     * 使用此方法可以显著减少反射带来的性能开销。
     *
     * @param serviceBean    要调用方法的目标对象。
     * @param serviceClass   目标对象的类，用于创建 FastClass。
     * @param methodName     要调用的方法名称。
     * @param parameterTypes 方法参数类型的数组，用于准确匹配方法签名。
     * @param parameters     传递给方法的参数值数组。
     * @return 方法执行的结果，返回为 Object 类型。
     * @throws InvocationTargetException 如果调用的方法抛出异常。
     * @throws IllegalAccessException    如果无法访问该方法。
     */
    private Object invokeCgLibMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws InvocationTargetException, IllegalAccessException {
        // Cglib 反射
        LOGGER.info("使用 Cglib 反射类型调用方法...");
        // 创建目标类的 FastClass 实例，用于高效的方法调用。
        FastClass serviceFastClass = FastClass.create(serviceClass);
        // 使用方法名和参数类型准确定位要调用的方法。
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        // 在目标对象上调用方法，并返回结果。
        return serviceFastMethod.invoke(serviceBean, parameters);
    }


    /**
     * Invokes a method on an object using JDK reflection.
     *
     * @param serviceBean    The object on which to invoke the method.
     * @param serviceClass   The Class object representing the class of the target object.
     * @param methodName     The name of the method to invoke.
     * @param parameterTypes An array of Class objects representing the parameter types of the method.
     * @param parameters     An array of objects representing the arguments to the method.
     * @return The result of invoking the method.
     * @throws NoSuchMethodException     If the method with the specified name and parameter types is not found.
     * @throws InvocationTargetException If the invoked method throws an exception.
     * @throws IllegalAccessException    If access to the method is not allowed.
     */
    private Object invokeJDKMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // JDK reflect
        LOGGER.info("use jdk reflect type invoke method...");
        // Get the Method object for the method to be invoked.
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        // Make the method accessible, bypassing Java's access control checks.
        method.setAccessible(true);
        // Invoke the method on the specified object with the specified parameters.
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
