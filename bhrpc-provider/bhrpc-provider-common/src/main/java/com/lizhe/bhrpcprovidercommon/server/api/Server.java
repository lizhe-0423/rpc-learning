package com.lizhe.bhrpcprovidercommon.server.api;

/**
 * Server
 * {@code @description} 服务提供者接口
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/10 下午1:33
 * @version 1.0
 */
public interface Server {
    /**
     * 启动Netty服务
     * 基于Netty框架实现的TCP服务器启动代码，用于实现远程调用服务端的启动。
     */
    void startNettyServer();
}
