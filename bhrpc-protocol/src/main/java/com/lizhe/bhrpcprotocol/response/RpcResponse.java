package com.lizhe.bhrpcprotocol.response;

import com.lizhe.bhrpcprotocol.base.RpcMessage;

/**
 * RpcResponse
 * {@code @description} Rpc请求响应类，对应的请求id在响应头中
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午2:34
 * @version 1.0
 */
public class RpcResponse extends RpcMessage {
    private static final long serialVersionUID = 425335064405584525L;

    /**
     * 错误信息
     */
    private String error;
    /**
     * 响应结果
     */
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
