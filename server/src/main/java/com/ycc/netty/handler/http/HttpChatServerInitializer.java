package com.ycc.netty.handler.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class HttpChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;

    public HttpChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //编解码http请求
        pipeline.addLast(new HttpServerCodec());
        //写文件内容
        pipeline.addLast(new ChunkedWriteHandler());
        //聚合解码HttpRequest/HttpContent/LastHttpContent到FullHttpRequest
        //保证接收的Http请求的完整性
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        //处理FullHttpRequest
        pipeline.addLast(new HttpRequestHandler("/ws"));
        //处理其他的WebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        //处理TextWebSocketFrame
        pipeline.addLast(new TextWebSocketFrameHandler(group));


    }
}
