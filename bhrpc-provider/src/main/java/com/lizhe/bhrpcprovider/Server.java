package com.lizhe.bhrpcprovider;

/**
 * Server
 * {@code @description} 服务提供接口
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 下午2:51
 * @version 1.0
 */
public interface Server {

    void startNettyServer(int port);
}
