package com.lizhe.bhrpccommon.helper;

/**
 * RpcServiceHelper
 * {@code @description} Rpc帮助实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/14 下午1:31
 * @version 1.0
 */
public class RpcServiceHelper {

    /**
     * 构建服务键
     * 用于根据服务名、服务版本和服务组创建一个唯一的服 务键
     * 选择使用 "#" 作为分隔符来连接这些属性，因为 它足够独特，不太可能在服务名、版本或组中自然出现
     * 这种方法提供了一种简单、标准化的方式来生成可 以用于快速查找和引用服务的键
     *
     * @param name    服务的名称，是服务的唯一标识之一
     * @param version 服务的版本，用于区分不同版本的同一服务
     * @param group   服务所属的组，用于对服务进行分组管理
     * @return 返回由服务名、服务版本和服务组组成的唯一服务键
     */
    public static String buildServiceKey(String name, String version, String group) {
        return String.join("#", name, version, group);
    }
}
