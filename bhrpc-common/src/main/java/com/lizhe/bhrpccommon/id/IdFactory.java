package com.lizhe.bhrpccommon.id;

import java.util.concurrent.atomic.AtomicLong;

/**
 * IdFactory
 * {@code @description} 简易ID工厂类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午2:52
 * @version 1.0
 */
public class IdFactory {
    private static final AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static Long getId(){
        return REQUEST_ID_GEN.incrementAndGet();
    }
}
