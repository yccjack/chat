package com.ycc.netty.handler.http;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.RandomAccessFile;

/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    /**
     * websocket标识
     */
    private final String wsUri;

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //如果是websocket请求，请求地址uri等于wsuri
        if (wsUri.equalsIgnoreCase(msg.uri())) {
            //将消息转发到下一个ChannelHandler
            ctx.fireChannelRead(msg.retain());
        } else {//如果不是websocket请求
            if (HttpUtil.is100ContinueExpected(msg)) {
                //如果HTTP请求头部包含Expect: 100-continue，
                //则响应请求
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
                ctx.writeAndFlush(response);
            }
            //获取index.html的内容响应给客户端
            RandomAccessFile file = new RandomAccessFile(
                    System.getProperty("user.dir") + "/index.html", "r");
            HttpResponse response = new DefaultHttpResponse(
                    msg.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set("Content-Type",
                    "text/html; charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            //如果http请求保持活跃，设置http请求头部信息
            //并响应请求
            if (keepAlive) {
                response.headers().set("Content-Length",
                        file.length());
                response.headers().set("Connection",
                        "keep-alive");
            }
            ctx.write(response);
            //如果不是https请求，将index.html内容写入通道
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file
                        .length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }
            //标识响应内容结束并刷新通道
            ChannelFuture future = ctx
                    .writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                //如果http请求不活跃，关闭http连接
                future.addListener(ChannelFutureListener.CLOSE);
            }
            file.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
