package com.lizhe.bhrpcprovidercommon.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * RpcProviderHandle
 * {@code @description} RPC服务提供者处理器
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/11 下午1:25
 * @version 1.0
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcProviderHandler.class.getName());

    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 重写channelRead0方法以处理接收到的消息
     * 此方法在接收到消息时被调用，它记录了接收到的数据以及当前处理器映射的内容
     * 然后将接收到的数据直接返回给发送者
     *
     * @param channelHandlerContext 上下文处理器，用于处理通道的读取事件
     * @param object                接收到的消息对象
     * @throws Exception 如果处理消息时发生错误，则抛出异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        // 记录接收到的数据内容
        LOGGER.info("RPC提供者收到的数据为==== >>> {}", object.toString());

        // 打印当前处理器映射的内容，用于调试和验证绑定的处理器
        LOGGER.info("handlerMap中存放的数据如下所示：");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            LOGGER.info(entry.getKey() + " === {}", entry.getValue());
        }

        // 直接返回接收到的数据，这里没有进行任何处理，只是简单地回显
        channelHandlerContext.writeAndFlush(object);
    }
}
