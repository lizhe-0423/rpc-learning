package com.lizhe.bhrpcconsumercommon.handle;

import com.alibaba.fastjson.JSON;
import com.lizhe.bhrpcconsumercommon.context.RpcContext;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import future.RPCFuture;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

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
    //存储请求ID与RpcResponse协议的映射关系
    /**
     * 存储请求ID与RPC响应的映射关系
     * 使用ConcurrentHashMap保证线程安全，key为请求ID，value为对应的Future对象
     * CompletableFuture用于异步转同步，替代了传统的while循环轮询方式，具有以下优势：
     * 1. 非阻塞：不会占用CPU资源进行忙等待
     * 2. 超时控制：支持设置等待超时时间
     * 3. 异常处理：可以优雅地处理异步操作中的异常
     * 4. 链式调用：支持多个异步操作的组合
     */
    //存储请求ID与RpcResponse协议的映射关系
    //private Map<Long, RpcProtocol<RpcResponse>> pendingResponse = new ConcurrentHashMap<>();

    private final Map<Long, RPCFuture> pendingRPC = new ConcurrentHashMap<>();
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

    /**
     * 处理从服务提供者返回的响应数据
     * 当收到响应时，根据请求ID查找并完成对应的Future
     *
     * @param channelHandlerContext  Netty的通道处理上下文
     * @param rpcResponseRpcProtocol RPC响应协议对象
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) throws Exception {
        if (rpcResponseRpcProtocol == null) {
            return;
        }
        logger.info("服务消费者接收到的数据===>>>{}", JSON.toJSONString(rpcResponseRpcProtocol));
        RpcHeader header = rpcResponseRpcProtocol.getHeader();
        long requestId = header.getRequestId();
        RPCFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null){
            rpcFuture.done(rpcResponseRpcProtocol);
        }
    }

    /**
     * 发送RPC请求并等待响应
     * 实现了异步转同步的效果，避免了使用while循环等待的缺点：
     * 1. while循环会占用CPU资源，造成忙等待
     * 2. 难以实现精确的超时控制
     * 3. 代码结构复杂，异常处理困难
     *
     * @param rpcRequestRpcProtocol RPC请求协议对象
     * @return 服务提供者的响应结果
     */
    public RPCFuture sendRequest(RpcProtocol<RpcRequest> rpcRequestRpcProtocol,boolean async, boolean oneway) {
        logger.info("服务消费者发送的数据===>>>{}", JSON.toJSONString(rpcRequestRpcProtocol));
        return oneway ? sendRequestOneway(rpcRequestRpcProtocol) : async ? sendRequestAsync(rpcRequestRpcProtocol) : sendRequestSync(rpcRequestRpcProtocol);
    }

    private RPCFuture sendRequestSync(RpcProtocol<RpcRequest> rpcRequestRpcProtocol){
        RPCFuture rpcFuture = this.getRpcFuture(rpcRequestRpcProtocol);
        channel.writeAndFlush(rpcRequestRpcProtocol);
        return rpcFuture;
    }

    private RPCFuture sendRequestAsync(RpcProtocol<RpcRequest> rpcRequestRpcProtocol){
        RPCFuture rpcFuture = this.getRpcFuture(rpcRequestRpcProtocol);
        //如果是异步调用，则将RPCFuture放入RpcContext
        RpcContext.getContext().setRPCFuture(rpcFuture);
        channel.writeAndFlush(rpcRequestRpcProtocol);
        return null;
    }

    private RPCFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol) {
        channel.writeAndFlush(protocol);
        return null;
    }

    private RPCFuture getRpcFuture(RpcProtocol<RpcRequest> protocol) {
        RPCFuture rpcFuture = new RPCFuture(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }


    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
