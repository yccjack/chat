package com.ycc.netty.simulation.server;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.handler.SimpleChatClientInitializer;
import com.ycc.netty.util.RedisUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:15
 */
public class SimpleChatClient {

    private final int port;
    private final String host;
    private EventLoopGroup group;

    private Channel channel;

    public SimpleChatClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void run() throws Exception {
        group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new SimpleChatClientInitializer());
        channel = b.connect(host, port).sync().channel();
        RedisUtil.set(ConfigConstant.chat_active_cotl.getValue(), "true");
        start();
    }


    public void start() throws Exception {
        String msg = RedisUtil.get(ConfigConstant.chat_msg.getValue());
        if (!StringUtil.isNullOrEmpty(msg)) {
            channel.writeAndFlush(msg + "\n");
            RedisUtil.del(ConfigConstant.chat_msg.getValue());
        }

        if (!"true".equalsIgnoreCase(RedisUtil.get(ConfigConstant.chat_active_cotl.getValue()))) {
            destroyClient();
        }

    }

    public static void main(String[] args) throws Exception {
        if (args.length > 1) {
            String host = args[0];
            new SimpleChatClient(host, 8081).run();
        } else {
            SimpleChatClient simpleChatClient = new SimpleChatClient("192.168.6.211", 8081);
            for (int i = 0; i < 10; i++) {
                RedisUtil.set(ConfigConstant.chat_msg.getValue(), i + "");
                if (i == 0) {
                    simpleChatClient.run();
                } else {
                    simpleChatClient.start();
                }

                Thread.sleep(5000);
            }
            RedisUtil.set(ConfigConstant.chat_active_cotl.getValue(), "false");

        }
    }

    /**
     * 中断与服务器的连接
     * @throws Exception
     */
    private void destroyClient() throws Exception {
        channel.closeFuture();
        channel.close();
        group.shutdownGracefully().sync();
    }

}
