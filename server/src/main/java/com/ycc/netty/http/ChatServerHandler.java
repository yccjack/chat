package com.ycc.netty.http;


import com.alibaba.fastjson.JSON;
import com.ycc.netty.bean.NotifyChannel;
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
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private Logger log = LoggerFactory.getLogger(ChatServerHandler.class);

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 获取客户端发送的消息并向所有的客户端推送消息
     *
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        log.info("ChatServerHandler->channelRead0：[]", msg);
        Channel channel = channelHandlerContext.channel();
        for (Channel ch : channels) {
            if (ch != channel) {
                String name = NameUtil.getName(channel.remoteAddress().toString());
                ch.writeAndFlush("[ " + name + " ]" + msg + "\n");
            } else {
                String name = NameUtil.getName(channel.remoteAddress().toString());
                channel.writeAndFlush("[you:" + name + " ]" + msg + "\n");
            }
        }
    }


    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        String name = NameUtil.getName(channel.remoteAddress().toString());
        /**
         * 推送列表
         */
        for (Channel ch : channels) {
            if (ch != channel) {
                NotifyChannel chatBean = new NotifyChannel();
                chatBean.setAddChatPerson(name);
                chatBean.setAddChatRemote(channel.remoteAddress().toString());
                chatBean.setMethod(NotifyChannel.METHOD_ADD);
                ch.writeAndFlush(JSON.toJSONString(chatBean) + "\n");
            } else {
                NotifyChannel chatBean = new NotifyChannel();
                chatBean.setMethod(NotifyChannel.METHOD_INIT);
                chatBean.setChatList(NameUtil.nameMap);
                ch.writeAndFlush(JSON.toJSONString(chatBean) + "\n");
            }
        }
        log.info("ChatServerHandler:" + channel.remoteAddress() + "在线");
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        notifyChatListRemove(channel);
        log.info("ChatServerHandler:" + channel.remoteAddress() + "[" + NameUtil.nameMap.get(channel.remoteAddress().toString()) + ":" + channel.remoteAddress().toString() + "] 掉线");
        NameUtil.remove(channel.remoteAddress().toString());
    }

    /**
     * 掉线或者客户端异常通知其他客户端掉线信息
     *
     * @param channel
     */
    private void notifyChatListRemove(Channel channel) {
        for (Channel ch : channels) {
            if (ch != channel.remoteAddress()) {
                NotifyChannel chatBean = new NotifyChannel();
                chatBean.setAddChatPerson(NameUtil.getName(channel.remoteAddress().toString()));
                chatBean.setAddChatRemote(channel.remoteAddress().toString());
                chatBean.setMethod(NotifyChannel.METHOD_REMOVE);
                ch.writeAndFlush(JSON.toJSONString(chatBean) + "\n");
            }
        }
    }

    /**
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String addr = channel.remoteAddress().toString();
        NameUtil.remove(addr);
        notifyChatListRemove(channel);
        log.info("ChatServerHandler" + addr + "异常!");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : channels) {
            ch.writeAndFlush("[SERVER] - " + NameUtil.getName(channel.remoteAddress().toString()) + " 进入聊天室\n");
        }
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        for (Channel ch : channels) {
            ch.writeAndFlush("[SERVER] - " + NameUtil.getName(channel.remoteAddress().toString()) + " 离开聊天室\n");
        }
        channels.remove(ctx.channel());
    }


}
