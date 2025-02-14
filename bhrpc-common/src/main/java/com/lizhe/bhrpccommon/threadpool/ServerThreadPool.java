package com.lizhe.bhrpccommon.threadpool;

import com.lizhe.bhrpcconstants.RpcThreadPoolConstants;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ServerThreadPool
 * {@code @description} 服务提供者调用线程池
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/13 下午4:31
 * @version 1.0
 */
public class ServerThreadPool {

    private static final ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                RpcThreadPoolConstants.DEFAULT_CORE_POOL_SIZE,
                RpcThreadPoolConstants.DEFAULT_MAX_POOL_SIZE,
                RpcThreadPoolConstants.DEFAULT_KEEP_ALIVE_TIME,
                RpcThreadPoolConstants.DEFAULT_KEEP_ALIVE_TIME_UNIT,
                RpcThreadPoolConstants.DEFAULT_BLOCKING_QUEUE_CAPACITY
        );
    }

    /**
     * 将任务提交给线程池执行
     * @param task 任务
     */
    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }


    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
