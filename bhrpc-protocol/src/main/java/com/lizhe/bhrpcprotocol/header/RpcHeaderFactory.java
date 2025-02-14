package com.lizhe.bhrpcprotocol.header;

import com.lizhe.bhrpccommon.id.IdFactory;
import com.lizhe.bhrpcconstants.RpcConstants;
import com.lizhe.bhrpcprotocol.enumeration.RpcType;

/**
 * RpcHeaderFactory
 * {@code @description} Rpc请求头工厂
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午2:49
 * @version 1.0
 */
public class RpcHeaderFactory {
    public static RpcHeader getRpcRequestHeader(String serializationType) {
        RpcHeader header = new RpcHeader();
        long requestId = IdFactory.getId();
        header.setRequestId(requestId);
        header.setMagic(RpcConstants.MAGIC);
        header.setMsgType((byte) RpcType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
