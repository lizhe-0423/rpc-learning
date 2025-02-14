package com.lizhe.test.provider.service.impl;

import com.lizhe.bhrpcannotation.RpcService;
import com.lizhe.bhrpctestapi.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ProviderDemoServiceImpl
 * {@code @description} 服务提供者接口实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 上午9:49
 * @version 1.0
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.lizhe.bhrpctestapi.DemoService"
        , version = "1.0.0", group = "test")
public class ProviderDemoServiceImpl implements DemoService {

    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String sayHello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "Hello " + name;
    }
}
