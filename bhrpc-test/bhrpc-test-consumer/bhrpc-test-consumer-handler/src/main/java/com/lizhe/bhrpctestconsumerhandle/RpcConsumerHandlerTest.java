package com.lizhe.bhrpctestconsumerhandle;

import com.lizhe.bhrpcconsumercommon.RpcConsumer;
import com.lizhe.bhrpcprotocol.RpcProtocol;
import com.lizhe.bhrpcprotocol.header.RpcHeaderFactory;
import com.lizhe.bhrpcprotocol.request.RpcRequest;

/**
 * RpcConsumerHandlerTest
 * {@code @description} RpcConsumer处理器测试
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/19 上午10:37
 * @version 1.0
 */
public class RpcConsumerHandlerTest {

    public static void main(String[] args) throws Exception {
        RpcConsumer consumer = RpcConsumer.getInstance();
        consumer.sendRequest(getRpcRequestProtocol());
        Thread.sleep(2000);
        consumer.close();
    }

    private static RpcProtocol<RpcRequest> getRpcRequestProtocol(){
        //模拟发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<RpcRequest>();
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
        return protocol;
    }
}
