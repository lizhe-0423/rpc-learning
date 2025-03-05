package com.lizhe.bhrpcconsumercommon.future;

import com.lizhe.bhrpcconsumercommon.callback.AsyncRPCCallback;
import com.lizhe.bhrpcconsumercommon.threadpool.ClientThreadPool;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RpcFuture
 * {@code @description} 自定义的Future实现，用于RPC框架中的异步转同步操作
 * 继承CompletableFuture并使用AQS实现线程同步，主要功能包括：
 * 1. 管理RPC请求的完整生命周期
 * 2. 提供超时控制机制
 * 3. 支持响应结果的异步获取
 * 4. 监控请求响应时间
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/20 上午9:24
 * @version 1.0
 */
public class RPCFuture extends CompletableFuture<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RPCFuture.class);
    // 使用AQS实现的同步器，用于控制请求完成状态
    private final Sync sync;
    // 线程锁，用于控制请求的并发访问
    private final ReentrantLock lock = new ReentrantLock();
    // 待处理的异步回调函数列表
    private final List<AsyncRPCCallback> pendingCallbacks = new ArrayList<AsyncRPCCallback>();
    // 原始RPC请求协议
    private final RpcProtocol<RpcRequest> requestRpcProtocol;
    // RPC响应协议
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    // 请求开始时间，用于计算响应耗时
    private final long startTime;

    public RPCFuture(RpcProtocol<RpcRequest> requestRpcProtocol) {
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 判断任务是否已完成
     * <p>
     * 此方法用于检查当前任务是否已经完成它通过调用同步组件的isDone方法来实现
     *
     * @return boolean 返回任务是否完成的布尔值
     */
    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    /**
     * 获取RPC调用结果，会阻塞直到请求完成或发生异常
     * 通过AQS的acquire方法实现线程等待
     */
    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.responseRpcProtocol != null) {
            return this.responseRpcProtocol.getBody().getResult();
        } else return null;
    }

    /**
     * 支持超时的RPC调用结果获取方法
     * 在指定时间内未得到响应则抛出超时异常
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 调用结果
     */
    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.responseRpcProtocol != null) {
                return this.responseRpcProtocol.getBody().getResult();
            } else return null;
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId() + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName() + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }

    // 不支持取消操作
    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    // 不支持取消操作
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    /**
     * 完成RPC调用，设置响应结果并释放等待线程
     * 同时记录响应时间，如果超过阈值则记录警告日志
     *
     * @param responseRpcProtocol RPC响应协议
     */
    public void done(RpcProtocol<RpcResponse> responseRpcProtocol) {
        this.responseRpcProtocol = responseRpcProtocol;
        sync.release(1);
        invokeCallbacks();
        //计算并检查响应时间
        long responseTime = System.currentTimeMillis() - startTime;
        // 响应时间阈值（毫秒），超过此值将记录警告日志
        long responseTimeThreshold = 5000;
        if (responseTime > responseTimeThreshold) {
            LOGGER.warn("Service response time is too slow. Request id = {}. Response Time = {}ms", responseRpcProtocol.getHeader().getRequestId(), responseTime);
        }
    }

    /**
     * 执行所有挂起的回调方法
     * 此方法主要用于处理所有待处理的异步RPC回调，它会在一个安全的同步环境中执行每个回调
     * 使用锁机制确保线程安全，避免并发问题
     */
    private void invokeCallbacks() {
        // 加锁，确保线程安全
        lock.lock();
        try {
            // 遍历所有挂起的回调方法
            for (final AsyncRPCCallback callback : pendingCallbacks) {
                // 执行当前回调方法
                runCallback(callback);
            }
        } finally {
            // 无论try块中发生什么，最终都要解锁，以确保不会发生死锁
            lock.unlock();
        }
    }


    /**
     * 为异步RPC调用添加回调对象
     * 此方法允许在RPC调用进行中或完成后执行特定操作
     *
     * @param callback 实现了AsyncRPCCallback接口的回调对象，用于在调用完成时执行
     * @return 返回当前的RPCFuture对象，支持链式调用
     */
    public RPCFuture addCallback(AsyncRPCCallback callback) {
        // 加锁以确保线程安全
        lock.lock();
        try {
            // 检查RPC调用是否已经完成
            if (isDone()) {
                // 如果已经完成，则直接执行回调
                runCallback(callback);
            } else {
                // 如果尚未完成，则将回调添加到待处理列表中
                this.pendingCallbacks.add(callback);
            }
        } finally {
            // 无论如何都要释放锁
            lock.unlock();
        }
        // 返回当前的RPCFuture对象，使得可以进行链式调用
        return this;
    }

    /**
     * 在客户端线程池中异步执行回调
     * 此方法用于处理异步RPC调用的结果，并在客户端线程池中执行回调方法
     * 如果响应没有错误，调用onSuccess方法并传入结果；如果有错误，创建一个RuntimeException并调用onException方法
     *
     * @param callback 异步RPC调用的回调接口，用于处理调用结果
     */
    private void runCallback(final AsyncRPCCallback callback) {
        // 获取RPC响应的主体
        final RpcResponse res = this.responseRpcProtocol.getBody();

        // 提交一个任务到客户端线程池中执行
        ClientThreadPool.submit(() -> {
            // 判断响应是否没有错误
            if (!res.isError()) {
                // 如果没有错误，调用回调的onSuccess方法并传入结果
                callback.onSuccess(res.getResult());
            } else {
                // 如果有错误，创建一个RuntimeException并调用回调的onException方法
                callback.onException(new RuntimeException("Response error", new Throwable(res.getError())));
            }
        });
    }


    /**
     * 自定义同步器实现
     * 使用AQS框架实现的同步器，用于控制RPC请求的完成状态
     * - state=0 表示请求处理中（pending）
     * - state=1 表示请求已完成（done）
     */
    static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = 1L;

        //定义Future状态
        private final int done = 1;

        /**
         * 尝试获取同步状态
         * 只有当state为done(1)时才能获取成功
         */
        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        /**
         * 尝试释放同步状态
         * 将state从pending(0)改为done(1)
         */
        @Override
        protected boolean tryRelease(int releases) {
            int pending = 0;
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 判断请求是否完成
         */
        public boolean isDone() {
            getState();
            return getState() == done;
        }
    }
}
