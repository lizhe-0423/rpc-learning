package com.lizhe.bhrpcprotocol;

import com.lizhe.bhrpcprotocol.header.RpcHeader;
import com.lizhe.bhrpcprotocol.header.RpcHeaderFactory;
import com.lizhe.bhrpcprotocol.request.RpcRequest;

/**
 * RpcProtocolTest
 * {@code @description} Rpc协议测试类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/12 下午3:07
 * @version 1.0
 */
public class RpcProtocolTest {
    /**
     * 创建并返回一个包含RpcRequest的RpcProtocol对象
     * <p>
     * 该方法首先通过RpcHeaderFactory获取一个RpcHeader实例，该实例表示请求的头部信息
     * 然后创建一个RpcRequest对象，设置其各项属性，包括调用的服务信息、方法信息、参数等
     * 最后将头部和请求对象分别设置到RpcProtocol对象中，并返回该对象
     *
     * @return RpcProtocol<RpcRequest> 包含RpcRequest的RpcProtocol对象
     */
    public static RpcProtocol<RpcRequest> getRpcProtocol() {
        // 获取RpcHeader实例，表示请求的头部信息
        RpcHeader header = RpcHeaderFactory.getRpcRequestHeader("jdk");

        // 创建一个RpcRequest对象
        RpcRequest body = new RpcRequest();
        // 设置请求为非单向调用，即需要接收返回结果
        body.setOneway(false);
        // 设置请求为非异步调用
        body.setAsync(false);
        // 设置需要调用的服务的全限定类名
        body.setClassName("com.lizhe.bhrpcrotocol.RpcProtocol");
        // 设置需要调用的方法名
        body.setMethodName("hello");
        // 设置调用的服务的分组信息
        body.setGroup("binghe");
        // 设置调用的方法的参数
        body.setParameters(new Object[]{"binghe"});
        // 设置调用的方法的参数类型
        body.setParameterTypes(new Class[]{String.class});
        // 设置调用的服务的版本号
        body.setVersion("1.0.0");

        // 创建一个RpcProtocol对象
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        // 设置RpcProtocol对象的主体部分，即RpcRequest对象
        protocol.setBody(body);
        // 设置RpcProtocol对象的头部部分，即RpcHeader对象
        protocol.setHeader(header);

        // 返回包含RpcRequest的RpcProtocol对象
        return protocol;
    }
}
