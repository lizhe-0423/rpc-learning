package com.lizhe.bhrpctestconsumercodec.handler;

import com.alibaba.fastjson.JSON;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.header.RpcHeaderFactory;
import com.lizhe.bhrpcprotocol.request.RpcRequest;
import com.lizhe.bhrpcprotocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RpcTestConsumerHandler
 * {@code @description} RpcConsumerHandleTest
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/13 下午2:33
 * @version 1.0
 */
public class RpcTestConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {

    private static final Logger Logger = LoggerFactory.getLogger(RpcTestConsumerHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("发送数据开始...");
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRpcRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.lizhe.bhrpctestapi.DemoService");
        request.setGroup("test");
        request.setMethodName("sayHello");
        request.setParameters(new Object[]{"lizhe"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        Logger.info("服务消费者发送的数据===>>>{}", JSON.toJSONString(protocol));
        ctx.writeAndFlush(protocol);
        Logger.info("发送数据完毕...");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> rpcResponseRpcProtocol) {
        Logger.info("服务消费者接收到的数据===>>>{}", JSON.toJSONString(rpcResponseRpcProtocol));
    }
}
