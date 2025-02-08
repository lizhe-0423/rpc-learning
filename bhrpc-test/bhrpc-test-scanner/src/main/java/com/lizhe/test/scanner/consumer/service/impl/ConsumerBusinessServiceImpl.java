package com.lizhe.test.scanner.consumer.service.impl;

import com.lizhe.bhrpcannotation.RpcReference;
import com.lizhe.test.scanner.consumer.ConsumerBusinessService;
import com.lizhe.test.scanner.service.DemoService;

/**
 * ConsumerBusinessServiceImpl
 * {@code @description} 服务消费者业务逻辑实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 下午1:53
 * @version 1.0
 */
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "test")
    private DemoService demoService;
}
