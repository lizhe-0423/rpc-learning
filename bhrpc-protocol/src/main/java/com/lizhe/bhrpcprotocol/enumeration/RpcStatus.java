package com.lizhe.bhrpcprotocol.enumeration;

/**
 * RpcStatus
 * {@code @description} Rpc结果枚举
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/13 下午4:28
 * @version 1.0
 */
public enum RpcStatus {
    SUCCESS(0),
    FAIL(1);

    private final int status;

    RpcStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
