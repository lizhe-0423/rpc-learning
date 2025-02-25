package future;

import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

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
    private Sync sync;
    // 原始RPC请求协议
    private final RpcProtocol<RpcRequest> requestRpcProtocol;
    // RPC响应协议
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    // 请求开始时间，用于计算响应耗时
    private final long startTime;
    // 响应时间阈值（毫秒），超过此值将记录警告日志
    private final long responseTimeThreshold = 5000;

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
        //计算并检查响应时间
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            LOGGER.warn("Service response time is too slow. Request id = {}. Response Time = {}ms", responseRpcProtocol.getHeader().getRequestId(), responseTime);
        }
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
        private final int pending = 0;

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
