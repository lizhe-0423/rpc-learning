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

    public static String buildServiceKey(String ServiceName,String ServiceVersion,String ServiceGroup){
        return String.join("#",ServiceName,ServiceVersion,ServiceGroup);
    }
}
