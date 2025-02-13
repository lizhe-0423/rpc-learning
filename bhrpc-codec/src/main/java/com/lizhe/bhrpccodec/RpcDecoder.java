package com.lizhe.bhrpccodec;

import com.lizhe.bhrpccommon.utils.SerializationUtils;
import com.lizhe.bhrpcconstants.RpcConstants;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.enumeration.RpcType;
import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import com.lizhe.bhrpcserialzationapi.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * RpcDecoder
 * {@code @description} Rpc解码器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午5:17
 * @version 1.0
 */
public class RpcDecoder extends ByteToMessageDecoder implements RpcCodec {

    /**
     * 解码方法，用于解析接收到的字节数据，将其转换为Java对象
     * 该方法首先检查数据的完整性，然后根据数据类型进行相应的处理
     *
     * @param ctx ChannelHandlerContext对象，提供了与通道相关的上下文信息
     * @param in  ByteBuf对象，包含待解码的字节数据
     * @param out List<Object>对象，用于存储解码后的Java对象
     * @throws Exception 如果解码过程中发生错误，则抛出异常
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 检查数据长度是否足够解析头部信息
        if (in.readableBytes() < RpcConstants.HEADER_TOTAL_LEN) return;
        //标记当前读取位置
        in.markReaderIndex();

        //读取魔数
        short magic = in.readShort();
        // 验证魔数是否符合预期，如果不符则抛出异常
        if (magic != RpcConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        // 读取消息类型、状态和请求ID
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        // 读取序列化类型，并转换为字符串
        ByteBuf serializationTypeByteBuf = in.readBytes(SerializationUtils.MAX_SERIALIZATION_TYPE_COUNR);
        String serializationType = SerializationUtils.subString(serializationTypeByteBuf.toString(CharsetUtil.UTF_8));

        // 读取数据长度，并检查数据完整性
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // 读取数据
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // 根据消息类型获取对应的枚举对象
        RpcType msgTypeEnum = RpcType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        // 创建并填充RpcHeader对象
        RpcHeader header = new RpcHeader();
        header.setMagic(magic);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setSerializationType(serializationType);
        header.setMsgLen(dataLength);
        //TODO Serialization是扩展点
        Serialization serialization = getJdkSerialization();
        // 根据消息类型进行不同的处理
        switch (msgTypeEnum) {
            case REQUEST:
                // 解序列化请求数据，并添加到输出列表中
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if (request != null) {
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                // 解序列化响应数据，并添加到输出列表中
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                // TODO 处理心跳消息
                break;
        }
    }
}
