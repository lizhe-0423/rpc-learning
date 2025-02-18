package com.lizhe.bhrpcprovidercommon.server.base;
/**
 * Copyright 2020-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.lizhe.bhrpccodec.RpcDecoder;
import com.lizhe.bhrpccodec.RpcEncoder;
import com.lizhe.bhrpcprovidercommon.handler.RpcProviderHandler;
import com.lizhe.bhrpcprovidercommon.server.api.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseServer
 * {@code @description} 启动Netty服务实现类
 *
 * @author lizhe@joysuch.com
 * {@code @date} 2025/2/10 下午1:34
 * @version 1.0
 */
public class BaseServer implements Server {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServer.class);

    protected String host = "127.0.0.1";

    protected int port = 27110;
    //存储的是实体类关系
    protected Map<String, Object> handlerMap = new HashMap<>();

    private String reflectType;

    public BaseServer(String serverAddress,String reflectType) {
        if (StringUtils.hasText(serverAddress)) {
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
            this.reflectType = reflectType;
        }
    }

    /**
     * 启动Netty服务器
     * 本方法负责初始化和启动Netty服务器，用于处理网络请求
     * 通过配置ServerBootstrap，设置线程组、通道和处理器来实现
     */
    @Override
    public void startNettyServer() {
        // 创建线程组 负责接收客户端连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 负责处理连接后的IO操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        // 初始化服务器引导类
        ServerBootstrap bootstrap = new ServerBootstrap();

        try {
            // 配置引导类，设置线程组、通道类型和通道处理器
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            // 初始化通道的处理管道
                            channel.pipeline()
                                    .addLast(new RpcDecoder())
                                    // 添加字符串编码器，将字符串转换为字节发送
                                    .addLast(new RpcEncoder())
                                    // 添加自定义的处理器处理接收到的消息
                                    .addLast(new RpcProviderHandler(reflectType,handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 绑定服务器地址和端口，同步等待绑定完成
            ChannelFuture future = bootstrap.bind(host, port).sync();
            // 记录服务器启动日志
            LOGGER.info("Server start on {}:{}", host, port);
            // 同步等待服务器通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            // 记录服务器启动错误日志
            // 恢复中断状态
            Thread.currentThread().interrupt();
            LOGGER.error("RPC Server start interrupted", e);
        } finally {
            // 关闭工作线程组
            workerGroup.shutdownGracefully();
            // 关闭老板线程组
            bossGroup.shutdownGracefully();
        }
    }

}
