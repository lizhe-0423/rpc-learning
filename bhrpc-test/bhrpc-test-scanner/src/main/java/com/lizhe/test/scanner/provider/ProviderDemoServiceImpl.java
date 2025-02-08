package com.lizhe.test.scanner.provider;

import com.lizhe.bhrpcannotation.RpcService;
import com.lizhe.test.scanner.service.DemoService;

/**
 * ProviderDemoServiceImpl
 * {@code @description} DemoService实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 下午1:44
 * @version 1.0
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.lizhe.test.scanner.service.DemoService", version = "1.0.0", group = "test")
public class ProviderDemoServiceImpl implements DemoService {
}
