package com.lizhe.bhrpcprotocol.enumeration;

/**
 * Enum
 * {@code @description} 协议类型
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午12:08
 * @version 1.0
 */
public enum RpcType {
    //请求消息
    REQUEST(1),
    //响应消息
    RESPONSE(2),
    //心跳消息
    HEARTBEAT(3);

    private final int type;

    RpcType(int type) {
        this.type = type;
    }

    public static RpcType findByType(int type) {
        for (RpcType rpcType : values()) {
            if (rpcType.type == type) {
                return rpcType;
            }
        }
        return null;
    }

    public int getType() {
        return type;
    }
}
