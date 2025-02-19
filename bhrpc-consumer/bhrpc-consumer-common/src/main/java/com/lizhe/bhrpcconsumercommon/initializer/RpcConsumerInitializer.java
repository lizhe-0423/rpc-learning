package com.lizhe.bhrpcconsumercommon.initializer;

import com.lizhe.bhrpccodec.RpcDecoder;
import com.lizhe.bhrpccodec.RpcEncoder;
import com.lizhe.bhrpcconsumercommon.handle.RpcConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * RpcConsumerInitializer
 * {@code @description} Rpc消费者
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/19 上午9:42
 * @version 1.0
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * 初始化Channel，配置ChannelPipeline中的处理器
     * 该方法在Channel被注册到EventLoop后调用，用于设置Channel的处理器链
     *
     * @param channel Netty的SocketChannel实例
     * @throws Exception 初始化过程中可能发生的异常
     */
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline cp = channel.pipeline();
        cp.addLast(new RpcEncoder()); // 添加RPC编码器，负责将请求对象编码为二进制数据
        cp.addLast(new RpcDecoder()); // 添加RPC解码器，负责将二进制数据解码为响应对象
        cp.addLast(new RpcConsumerHandler()); // 添加RPC消费者处理器，负责处理RPC调用的核心逻辑
    }
}
