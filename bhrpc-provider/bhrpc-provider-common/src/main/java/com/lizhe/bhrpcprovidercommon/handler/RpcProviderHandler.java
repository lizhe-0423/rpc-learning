package com.lizhe.bhrpcprovidercommon.handler;

import com.lizhe.bhrpccommon.helper.RpcServiceHelper;
import com.lizhe.bhrpccommon.threadpool.ServerThreadPool;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.enumeration.RpcStatus;
import com.lizhe.bhrpcprotocol.enumeration.RpcType;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 重写channelRead0方法以处理接收到的消息
     * 此方法在接收到消息时被调用，它记录了接收到的数据以及当前处理器映射的内容
     * 然后将接收到的数据直接返回给发送者
     *
     * @param ctx 上下文处理器，用于处理通道的读取事件
     * @param protocol              接收到的消息对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        ServerThreadPool.submit(()->{
            RpcHeader header = protocol.getHeader();
            header.setMsgType((byte) RpcType.RESPONSE.getType());
            RpcRequest request = protocol.getBody();
            LOGGER.debug("Receive request {}" , header.getRequestId());
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
                LOGGER.error("RPC Server handle request error",t);
            }
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);
            ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    LOGGER.debug("Send response for request {}" , header.getRequestId());
                }
            });
        });
    }

    private static RpcProtocol<RpcResponse> getRpcResponseRpcProtocol(RpcProtocol<RpcRequest> protocol) {
        RpcHeader header = protocol.getHeader();
        RpcRequest request = protocol.getBody();

        //将消息类型设置为响应类型的消息
        header.setMsgType((byte) RpcType.RESPONSE.getType());
        //构建响应协议数据
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult("数据交互成功");
        rpcResponse.setAsync(request.getAsync());
        rpcResponse.setOneway(request.getOneway());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(rpcResponse);
        return responseRpcProtocol;
    }

    private Object handle(RpcRequest request) throws Throwable  {
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
        if (parameterTypes != null && parameterTypes.length > 0){
            for (int i = 0; i < parameterTypes.length; ++i) {
                LOGGER.debug(parameterTypes[i].getName());
            }
        }

        if (parameters != null && parameters.length > 0){
            for (int i = 0; i < parameters.length; ++i) {
                LOGGER.debug(parameters[i].toString());
            }
        }
        return invokeMethod(serviceBean, serviceBeanClass, methodName, parameterTypes, parameters);
    }

    //todo 目前使用JDK代理此处进行埋点
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("server caught exception", cause);
        ctx.close();
    }
}
