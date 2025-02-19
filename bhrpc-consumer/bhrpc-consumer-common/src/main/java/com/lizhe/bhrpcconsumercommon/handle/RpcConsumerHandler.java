package com.lizhe.bhrpcconsumercommon.handle;

import com.alibaba.fastjson.JSON;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * RpcConsumerHandles
 * {@code @description} RpcConsumer处理器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/19 上午9:43
 * @version 1.0
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);

    private volatile Channel channel;
    private SocketAddress remotePeer;

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) {
        logger.info("服务消费者接收到的数据===>>>{}", JSON.toJSONString(rpcResponseRpcProtocol));
    }

    /**
     * 发送RPC请求到服务提供者
     *
     * @param rpcRequestRpcProtocol RPC请求协议对象，包含了请求的具体信息
     *                              如：接口名称、方法名、参数类型、参数值等
     *                              数据流向：
     *                              1. 将请求对象序列化为JSON格式并记录日志
     *                              2. 通过Netty的Channel将请求发送给服务提供者
     */
    public void sendRequest(RpcProtocol<RpcRequest> rpcRequestRpcProtocol) {
        logger.info("服务消费者发送的数据===>>>{}", JSON.toJSONString(rpcRequestRpcProtocol));
        channel.writeAndFlush(rpcRequestRpcProtocol);
    }

    /**
     * 优雅关闭当前RPC消费者的Channel连接
     * 实现原理：
     * 1. 通过writeAndFlush发送一个空的ByteBuf到远程节点
     * 2. 添加ChannelFutureListener.CLOSE监听器，确保数据发送完成后自动关闭Channel
     * 3. 这种方式可以确保：
     * - 所有待处理的数据都被发送完成
     * - 远程节点收到关闭信号
     * - 相关的资源被正确释放
     */
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
