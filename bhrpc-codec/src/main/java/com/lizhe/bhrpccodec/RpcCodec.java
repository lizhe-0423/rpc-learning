package com.lizhe.bhrpccodec;

import com.lizhe.bhrpcserializationjdk.JdkSerialization;
import com.lizhe.bhrpcserialzationapi.Serialization;

/**
 * RpcCodec
 * {@code @description} Rpc编解码接口
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午5:16
 * @version 1.0
 */
public interface RpcCodec {
    default Serialization getJdkSerialization(){
        return new JdkSerialization();
    }
}
