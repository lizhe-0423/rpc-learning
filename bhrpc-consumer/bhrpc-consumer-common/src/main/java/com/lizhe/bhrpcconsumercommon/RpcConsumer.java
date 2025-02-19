package com.lizhe.bhrpcconsumercommon;

import com.lizhe.bhrpcconsumercommon.handle.RpcConsumerHandler;
import com.lizhe.bhrpcconsumercommon.initializer.RpcConsumerInitializer;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC消费者的核心实现类
 * 该类负责管理与RPC服务提供者的网络连接，并处理RPC请求的发送。
 * 采用单例模式确保全局唯一实例，使用Netty实现底层网络通信。
 * <p>
 * 主要功能：
 * 1. 维护与服务提供者的连接
 * 2. 管理RPC请求的发送
 * 3. 处理连接的建立与关闭
 * 4. 缓存与复用连接处理器
 *
 * @author lizhe@joysuch.com
 * @version 1.0
 */
public class RpcConsumer {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);

    /**
     * Netty客户端启动引导类，用于配置并启动客户端
     */
    private final Bootstrap bootstrap;

    /**
     * Netty的事件循环组，用于处理所有的网络事件
     */
    private final EventLoopGroup eventLoopGroup;

    /**
     * 缓存RPC消费者处理器
     * key: 服务地址和端口的组合，格式为：address_port
     * value: 对应的RPC消费者处理器实例
     */
    private static final Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * 使用静态内部类实现单例模式
     * 这种方式具有延迟加载和线程安全的特点
     */
    private static class SingletonHolder {
        private static final RpcConsumer INSTANCE = new RpcConsumer();
    }

    /**
     * 私有构造方法，初始化Netty客户端
     * 1. 创建Bootstrap实例用于客户端引导
     * 2. 创建EventLoopGroup，指定4个线程处理网络事件
     * 3. 配置Bootstrap的基本参数：
     * - 设置EventLoopGroup
     * - 指定Channel类型为NioSocketChannel
     * - 设置RpcConsumerInitializer作为处理器
     */
    private RpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    /**
     * 获取RpcConsumer的单例实例
     *
     * @return RpcConsumer的单例实例
     */
    public static RpcConsumer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 关闭RPC消费者，释放资源
     * 优雅地关闭EventLoopGroup，确保所有任务完成
     */
    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

    /**
     * 发送RPC请求到服务提供者
     *
     * @param rpcRequestRpcProtocol RPC请求协议对象，包含请求的具体信息
     * @throws InterruptedException 当连接过程中断时抛出
     */
    public Object sendRequest(RpcProtocol<RpcRequest> rpcRequestRpcProtocol) throws InterruptedException {
        //todo 暂时写死 后续引入到注册中心
        String serviceAddress = "127.0.0.1";
        int port = 27880;
        String key = serviceAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);

        // 处理三种情况：
        // 1. 缓存中没有handler，创建新的handler
        // 2. 缓存中的handler存在但不活跃，关闭旧handler并创建新的
        // 3. 缓存中有活跃的handler，直接使用
        if (handler == null) {
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        } else if (!handler.getChannel().isActive()) {
            handler.close();
            handler = getRpcConsumerHandler(serviceAddress, port);
            handlerMap.put(key, handler);
        }
        return handler.sendRequest(rpcRequestRpcProtocol);
    }

    /**
     * 获取RPC消费者处理器
     * 负责建立与服务提供者的连接，并返回对应的处理器实例
     *
     * @param serviceAddress 服务提供者地址
     * @param port           服务提供者端口
     * @return RPC消费者处理器实例
     * @throws InterruptedException 当连接过程中断时抛出
     */
    private RpcConsumerHandler getRpcConsumerHandler(String serviceAddress, int port) throws InterruptedException {
        ChannelFuture channelFuture = bootstrap.connect(serviceAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()) {
                logger.info("Connect rpc server {} on port {} success.", serviceAddress, port);
            } else {
                logger.error("Connect rpc server {} on port {} failed.", serviceAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }
}
