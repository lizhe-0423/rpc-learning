package com.lizhe.bhrpccodec;

import com.lizhe.bhrpccommon.utils.SerializationUtils;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcserialzationapi.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * RpcEncoder
 * {@code @description} Rpc编码器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午5:18
 * @version 1.0
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>>  implements RpcCodec{
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        RpcHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        String serializationType = header.getSerializationType();
        //TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes("UTF-8"));
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
