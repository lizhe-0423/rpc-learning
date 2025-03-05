package com.lizhe.bhrpcconsumercommon.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ClientThreadPool
 * {@code @description} 服务消费者线程池
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/26 上午9:20
 * @version 1.0
 */
public class ClientThreadPool {

    private static final ThreadPoolExecutor threadPoolExecutor;

    static {
        // 线程池初始化
        threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    }


    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public static void shutdown(){
        threadPoolExecutor.shutdown();
    }
}
