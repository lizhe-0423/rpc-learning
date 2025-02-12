package com.lizhe.test.provider.service.impl;

import com.lizhe.bhrpcannotation.RpcService;
import com.lizhe.test.provider.service.DemoService;

/**
 * ProviderDemoServiceImpl
 * {@code @description} 服务提供者接口实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 上午9:49
 * @version 1.0
 */
@RpcService(interfaceClass = DemoService.class,interfaceClassName = "com.lizhe.test.provider.service.DemoService",version="1.0.0",group="test")
public class ProviderDemoServiceImpl {
}
