package com.ycc.netty.http;


import com.ycc.netty.util.NameUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 8:43
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> {

    private Logger log = LoggerFactory.getLogger(SimpleChatServerHandler.class);

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        log.info("SimpleChatServerHandler->channelRead0：[]", s);
        Channel channel = channelHandlerContext.channel();
        for (Channel ch : channels) {
            if (ch != channel) {
                String name = NameUtil.getName(channel.remoteAddress().toString());
                ch.writeAndFlush("[ " + name + " ]" + s + "\n");
            } else {
                String name = NameUtil.getName(channel.remoteAddress().toString());
                channel.writeAndFlush("[you:" + name + " ]" + s + "\n");
            }
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("SimpleChatClient:" + channel.remoteAddress() + "在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("SimpleChatClient:" + channel.remoteAddress() + "[" + NameUtil.nameMap.get(channel.remoteAddress().toString()) + ":" + channel.remoteAddress().toString() + "] 掉线");
        NameUtil.remove(channel.remoteAddress().toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String addr = channel.remoteAddress().toString();
        NameUtil.remove(addr);
        log.info("SimpleChatClient" + addr + "异常!");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : channels) {
            ch.writeAndFlush("[SERVER] - " + channel.remoteAddress() + " incoming\n");

        }
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : channels) {
            ch.writeAndFlush("[SERVER] - " + channel.remoteAddress() + " lived\n");

        }
        channels.remove(ctx.channel());
    }

}
