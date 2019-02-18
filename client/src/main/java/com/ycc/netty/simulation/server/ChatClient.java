package com.ycc.netty.simulation.server;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.handler.ChatClientInitializer;
import com.ycc.netty.simulation.listener.FutureListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;


/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:15
 */
public class ChatClient {

    private final int port;
    private final String host;
    private EventLoopGroup group;

    private Channel channel;

    public ChatClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    /**
     * default host:192.168.6.211
     * port:8081
     */
    public ChatClient() {
        this.port = Integer.parseInt(ConfigConstant.serverPort.getValue());
        this.host = ConfigConstant.serverHost.getValue();
    }

    /**
     * 建立与服务器TCP连接
     *
     * @throws Exception
     */
    public void run() throws Exception {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChatClientInitializer());
        ChannelFuture sync = b.connect(host, port).sync();
        sync.addListener(new FutureListener());
        channel = sync.channel();
        RedisProxy.set(ConfigConstant.chat_active_cotl.getValue(), "true");
        start();
    }


    /**
     * 发送消息
     *
     * @throws Exception
     */
    public void start() throws Exception {
        String msg = RedisProxy.get(ConfigConstant.chat_msg.getValue());
        if (!StringUtil.isNullOrEmpty(msg)) {
            channel.writeAndFlush(msg + "\n");
            RedisProxy.del(ConfigConstant.chat_msg.getValue());
        }
        if (!"true".equalsIgnoreCase(RedisProxy.get(ConfigConstant.chat_active_cotl.getValue()))) {
            destroyClient();
        }

    }


    /**
     * 中断与服务器的连接
     *
     * @throws Exception
     */
    public void destroyClient() throws Exception {
        channel.closeFuture();
        channel.close();
        group.shutdownGracefully().sync();
    }


    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            String host = args[0];
            new ChatClient(host, 8081).run();
        } else {
            ChatClient simpleChatClient = new ChatClient("192.168.6.211", 8081);
            for (int i = 0; i < 10; i++) {
                RedisProxy.set(ConfigConstant.chat_msg.getValue(), i + "");
                if (i == 0) {
                    simpleChatClient.run();
                } else {
                    simpleChatClient.start();
                }
                Thread.sleep(500);
            }
            RedisProxy.set(ConfigConstant.chat_active_cotl.getValue(), "false");
            simpleChatClient.start();
        }
    }
}
