package com.ycc.netty.server;

import com.ycc.netty.http.ChatServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:00
 */
public class ChatServer {

    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private int port;

    public ChatServer(int port) {
        this.port = port;

    }

    public void run() throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer())
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            log.info("服务启动");
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
            log.info("服务关闭");
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8082;
        }
        new ChatServer(port).run();
    }
}
