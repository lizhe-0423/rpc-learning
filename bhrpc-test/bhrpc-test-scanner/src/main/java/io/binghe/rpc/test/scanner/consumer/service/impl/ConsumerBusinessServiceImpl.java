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
package io.binghe.rpc.test.scanner.consumer.service.impl;


import com.lizhe.bhrpcannotation.RpcReference;
import io.binghe.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import io.binghe.rpc.test.scanner.service.DemoService;

/**
 * @author binghe
 * @version 1.0.0
 * @description 服务消费者业务逻辑实现类
 */
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", version = "1.0.0", group = "binghe")
    private DemoService demoService;

}
