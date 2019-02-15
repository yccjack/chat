package com.ycc.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 8:55
 */
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    private Logger log = LoggerFactory.getLogger(ChatServerInitializer.class);

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast("decoder", new StringDecoder())
                .addLast("encoder", new StringEncoder())
                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                .addLast(new HeartbeatHandler())
                .addLast("handler", new ChatServerHandler());
        log.debug("ChatServerInitializer:" + socketChannel.remoteAddress() + "连接上");
    }
}
