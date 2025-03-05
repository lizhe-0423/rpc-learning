package com.lizhe.bhrpcconsumercommon.callback;

/**
 * AsyncRPCCallback
 * {@code @description} 异步回调方法接口
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/26 上午9:18
 * @version 1.0
 */
public interface AsyncRPCCallback {
    /**
     * 成功回调方法
     *
     * @param result 返回结果
     */
    void onSuccess(Object result);

    /**
     * 异常回调方法
     *
     * @param e 异常
     */
    void onException(Exception e);
}
