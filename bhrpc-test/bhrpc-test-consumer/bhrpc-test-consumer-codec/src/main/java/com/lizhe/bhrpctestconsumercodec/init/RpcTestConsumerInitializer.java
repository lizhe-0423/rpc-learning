package com.lizhe.bhrpctestconsumercodec.init;


import com.lizhe.bhrpccodec.RpcDecoder;
import com.lizhe.bhrpccodec.RpcEncoder;
import com.lizhe.bhrpctestconsumercodec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * RpcTestConsumerInitializer
 * {@code @description} 初始化
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/13 下午2:44
 * @version 1.0
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new RpcEncoder());
        pipeline.addLast(new RpcDecoder());
        pipeline.addLast(new RpcTestConsumerHandler());
    }
}
