package com.lizhe.bhrpctestprovider;

import com.lizhe.bhrpcprovidernative.RpcSingleServer;
import org.junit.Test;


/**
 * DemoServiceTest
 * {@code @description} 服务提供者测试类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 上午9:46
 * @version 1.0
 */
public class RpcSingleServiceTest {
    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880", "com.lizhe.test.provider");
        singleServer.startNettyServer();
    }
}
