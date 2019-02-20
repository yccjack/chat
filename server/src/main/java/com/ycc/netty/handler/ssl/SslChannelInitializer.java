package com.ycc.netty.handler.ssl;

import com.ycc.netty.handler.http.HttpRequestHandler;
import com.ycc.netty.handler.http.TextWebSocketFrameHandler;
import com.ycc.netty.handler.tcp.ChatServerHandler;
import com.ycc.netty.handler.tcp.HeartbeatHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.util.concurrent.TimeUnit;

/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private SSLContext sslContext;
    private ChannelGroup group;

    public SslChannelInitializer(SSLContext sslContext, ChannelGroup group) {
        this.sslContext = sslContext;
        this.group = group;
    }


    @Override
    protected void initChannel(Channel ch) throws Exception {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        ch.pipeline().addFirst("ssl", new SslHandler(sslEngine, true))
                .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                .addLast("decoder", new StringDecoder())
                .addLast("encoder", new StringEncoder())
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(64 * 1024))
                .addLast(new HttpRequestHandler("/ws"))
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                .addLast(new TextWebSocketFrameHandler(group))
                .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                .addLast(new HeartbeatHandler())
                .addLast("handler", new ChatServerHandler(group));
        log.debug("SslChannelInitializer:" + ch.remoteAddress() + "连接上");
    }
}
