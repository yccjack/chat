package com.ycc.netty.server;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.handler.http.HttpChatServerInitializer;
import com.ycc.netty.handler.tcp.ChatServerInitializer;
import com.ycc.netty.listener.ServerBoundListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:00
 */
public class ChatServer {
    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private int port;
    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup worker = new NioEventLoopGroup();

    public ChatServer(int port) {
        this.port = port;

    }

    /**
     * javafx客户端服务器
     *
     * @throws Exception
     */
    public void run() throws Exception {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer(group))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            serverStart(b);
        } finally {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
            log.info("服务关闭");
        }
    }

    /**
     * websocket服务器
     *
     * @throws Exception
     */
    private void websocket() throws Exception {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpChatServerInitializer(group));
            serverStart(b);
        } finally {
            worker.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
            log.info("服务关闭");
        }
    }

    private void serverStart(ServerBootstrap b) throws InterruptedException {
        log.info("服务启动");
        ChannelFuture f = b.bind(port).sync();
        f.addListener(new ServerBoundListener());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> f.channel().close()));
        f.channel().closeFuture().syncUninterruptibly();
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = ConfigConstant.CLIENT_SERVER_PORT;
        }
        new ChatServer(port).run();
    }
}
