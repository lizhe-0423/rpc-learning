package com.lizhe.bhrpcconstants;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * RpcThreadPoolConstants
 * {@code @description} Rpc线程池常量
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/13 下午4:33
 * @version 1.0
 */
public class RpcThreadPoolConstants {
    /**
     * 默认线程池大小
     */
    public static final int DEFAULT_CORE_POOL_SIZE = 10;
    /**
     * 默认最大线程池大小
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 100;
    /**
     * 默认线程池中线程空闲时间
     */
    public static final int DEFAULT_KEEP_ALIVE_TIME = 1000;

    /**
     * 默认线程空闲等待时间单位
     */
    public static final TimeUnit DEFAULT_KEEP_ALIVE_TIME_UNIT = TimeUnit.MILLISECONDS;
    /**
     * 默认阻塞队列容量
     */
    public static final ArrayBlockingQueue<Runnable> DEFAULT_BLOCKING_QUEUE_CAPACITY =  new ArrayBlockingQueue<Runnable>(65536);
    /**
     * 默认队列容量
     */
    public static final int DEFAULT_QUEUE_SIZE = 100;
    /**
     * 默认队列超时时间
     */
    public static final int DEFAULT_QUEUE_TIMEOUT = 1000;
    /**
     * 默认线程优先级
     */
    public static final int DEFAULT_THREAD_PRIORITY = 5;
    /**
     * 默认线程名前缀
     */
    public static final String DEFAULT_THREAD_NAME_PREFIX = "Rpc-Thread-Pool-";
    /**
     * 默认线程名后缀
     */
    public static final String DEFAULT_THREAD_NAME_SUFFIX = "-Thread";
    /**
     * 默认线程组名
     */
    public static final String DEFAULT_THREAD_GROUP_NAME = "Rpc-Thread-Group";
}
