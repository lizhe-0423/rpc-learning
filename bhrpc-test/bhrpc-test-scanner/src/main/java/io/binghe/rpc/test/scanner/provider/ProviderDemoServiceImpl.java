/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.binghe.rpc.test.scanner.provider;

import com.lizhe.bhrpcannotation.RpcService;

import io.binghe.rpc.test.scanner.service.DemoService;

/**
 * @author binghe
 * @version 1.0.0
 * @description DemoService实现类
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "io.binghe.rpc.test.scanner.service.DemoService", version = "1.0.0", group = "binghe")
public class ProviderDemoServiceImpl implements DemoService {

}
