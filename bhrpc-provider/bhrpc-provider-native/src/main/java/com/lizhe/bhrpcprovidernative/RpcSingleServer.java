package com.lizhe.bhrpcprovidernative;

import com.lizhe.bhrpccommon.scanner.service.RpcServiceScanner;
import com.lizhe.bhrpcprovidercommon.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RpcSingleServer
 * {@code @description} 以Java原生的方式启动RPC
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/11 下午5:48
 * @version 1.0
 */
public class RpcSingleServer extends BaseServer {

    public RpcSingleServer(String serverAddress, String scanPackage,String reflectType) {
        //调用父类构造方法
        super(serverAddress,reflectType);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);
            logger.error("RPC Server init error", e);
        }
    }
}
