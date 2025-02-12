package com.lizhe.bhrpcprotocol;

import com.lizhe.bhrpcprotocol.header.RpcHeader;

import java.io.Serializable;

/**
 * RpcProtocol
 * {@code @description} Rpc协议类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午3:06
 * @version 1.0
 */
public class RpcProtocol<T> implements Serializable {
    private static final long serialVersionUID = 292789485166173277L;

    /**
     * 消息头
     */
    private RpcHeader header;
    /**
     * 消息体
     */
    private T body;

    public RpcHeader getHeader() {
        return header;
    }

    public void setHeader(RpcHeader header) {
        this.header = header;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
