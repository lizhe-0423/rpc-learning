package com.lizhe.bhrpcprotocol.base;

import java.io.Serializable;

/**
 * RpcMessage
 * {@code @description} Rpc基础消息类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午2:15
 * @version 1.0
 */
public class RpcMessage implements Serializable {
    //单向发送
    private boolean oneway;
    //异步调用
    private boolean async;

    public boolean getOneway() {
        return oneway;
    }

    public void setOneway(boolean oneway) {
        this.oneway = oneway;
    }

    public boolean getAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }
}
