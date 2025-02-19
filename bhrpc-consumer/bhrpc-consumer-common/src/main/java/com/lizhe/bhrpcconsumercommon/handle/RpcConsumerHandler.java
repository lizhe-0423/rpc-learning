package com.lizhe.bhrpcconsumercommon.handle;

import com.alibaba.fastjson.JSON;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
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
    private Map<Long, CompletableFuture<RpcProtocol<RpcResponse>>> pendingResponse = new ConcurrentHashMap<>();
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
        // 根据请求ID获取并移除对应的Future
        CompletableFuture<RpcProtocol<RpcResponse>> future = pendingResponse.remove(requestId);
        if (future != null) {
            // 设置Future的结果，这将唤醒在sendRequest方法中等待的线程
            future.complete(rpcResponseRpcProtocol);
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
    public Object sendRequest(RpcProtocol<RpcRequest> rpcRequestRpcProtocol) {
        logger.info("服务消费者发送的数据===>>>{}", JSON.toJSONString(rpcRequestRpcProtocol));
        RpcHeader header = rpcRequestRpcProtocol.getHeader();
        long requestId = header.getRequestId();

        // 创建CompletableFuture用于异步接收响应
        CompletableFuture<RpcProtocol<RpcResponse>> resultFuture = new CompletableFuture<>();
        pendingResponse.put(requestId, resultFuture);

        // 发送请求，并处理发送失败的情况
        channel.writeAndFlush(rpcRequestRpcProtocol).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                resultFuture.completeExceptionally(future.cause());
                pendingResponse.remove(requestId);
            }
        });

        try {
            // 等待响应，支持超时控制
            RpcProtocol<RpcResponse> responseRpcProtocol = resultFuture.get(30, TimeUnit.SECONDS);
            return responseRpcProtocol.getBody().getResult();
        } catch (Exception e) {
            // 发生异常时，清理资源并抛出运行时异常
            pendingResponse.remove(requestId);
            throw new RuntimeException("调用服务失败", e);
        }
    }

    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
