package com.lizhe.bhrpcannotation;

/**
 * ConsumerDemo
 * {@code @description} 消费者示例
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/8 上午10:14
 * @version 1.0
 */
public class ConsumerDemo {

    @RpcReference(registryType = "zookeeper", registryAddress = "127.0.0.1:2181", loadBalanceType = "zkconsistenthash",
            version = "1.0.0", group = "binghe", serializationType = "protostuff", proxy = "cglib", timeout = 30000, async = true, oneway=false)
    private ProviderDemo providerDemo;
}
