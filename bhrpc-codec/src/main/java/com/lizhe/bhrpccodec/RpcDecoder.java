package com.lizhe.bhrpccodec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.ByteToMessageDecoder;

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

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List list) throws Exception {

    }
}
