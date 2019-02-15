package com.ycc.netty.handler;


import com.alibaba.fastjson.JSON;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.bean.SendMsg;
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
        log.debug("ChatServerHandler->channelRead0：" + msg);
        Channel channel = channelHandlerContext.channel();
        SendMsg sendMsg = JSON.parseObject(msg, SendMsg.class);
        if (validate(sendMsg)) {
            p2pChat(sendMsg, channel);
        } else {
            groupChat(sendMsg, channel);
        }
    }

    private boolean validate(SendMsg sendMsg) {
        return sendMsg.getSendTarget() != null;
    }

    /**
     * 群聊
     *
     * @param sendMsg 发送的消息
     * @param channel
     */
    private void groupChat(SendMsg sendMsg, Channel channel) {
        NotifyChannel notifyChannel = new NotifyChannel();
        notifyChannel.setMethod(NotifyChannel.METHOD_GROUP_CHAT);
        sendMsg.setName(NameUtil.getName(channel.remoteAddress().toString()));
        notifyChannel.setSendMsg(sendMsg);
        channels.forEach(ch -> ch.writeAndFlush(JSON.toJSONString(notifyChannel) + "\n"));
    }

    /**
     * 私聊
     *
     * @param sendMsg 发送的消息
     * @param channel
     */
    private void p2pChat(SendMsg sendMsg, Channel channel) {
        NotifyChannel notifyChannel = new NotifyChannel();
        notifyChannel.setMethod(NotifyChannel.METHOD_P2PCHAT);
        notifyChannel.setSendMsg(sendMsg);
        sendMsg.setSendFrom(channel.remoteAddress().toString());
        String returnMsg = JSON.toJSONString(notifyChannel);
        channels.forEach(ch -> {
            if (ch.remoteAddress().toString().equals(sendMsg.getSendTarget().trim()) || ch == channel) {
                ch.writeAndFlush(returnMsg + "\n");
            }
        });
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
        NotifyChannel chatBean = new NotifyChannel();
        chatBean.setAddChatPerson(name);
        chatBean.setAddChatRemote(channel.remoteAddress().toString());
        chatBean.setMethod(NotifyChannel.METHOD_ADD);
        channels.forEach(ch -> {
            if (ch != channel) {
                ch.writeAndFlush(JSON.toJSONString(chatBean) + "\n");
            } else {
                NotifyChannel chatBean1 = new NotifyChannel();
                chatBean1.setMethod(NotifyChannel.METHOD_INIT);
                chatBean1.setChatList(NameUtil.nameMap);
                chatBean1.setAddChatPerson(name);
                chatBean1.setAddChatRemote(channel.remoteAddress().toString());
                ch.writeAndFlush(JSON.toJSONString(chatBean1) + "\n");
            }
        });
        log.debug("ChatServerHandler:" + channel.remoteAddress() + "在线");
    }

    /**
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        notifyChatListRemove(channel);
        log.debug("ChatServerHandler:" + channel.remoteAddress() + "[" + NameUtil.nameMap.get(channel.remoteAddress().toString()) + ":" + channel.remoteAddress().toString() + "] 掉线");
    }

    /**
     * 掉线或者客户端异常通知其他客户端掉线信息
     *
     * @param channel
     */
    private void notifyChatListRemove(Channel channel) {
        channels.forEach(ch -> {
            if (ch != channel.remoteAddress()) {
                NotifyChannel chatBean = new NotifyChannel();
                chatBean.setAddChatPerson(NameUtil.getName(channel.remoteAddress().toString()));
                chatBean.setAddChatRemote(channel.remoteAddress().toString());
                chatBean.setMethod(NotifyChannel.METHOD_REMOVE);
                ch.writeAndFlush(JSON.toJSONString(chatBean) + "\n");
            }
        });
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
        log.error("ChatServerHandler" + addr + "异常!");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        AddOrRemove(channel, " 进入聊天室");
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        AddOrRemove(channel, " 离开聊天室");
        NameUtil.remove(channel.remoteAddress().toString());
        channels.remove(ctx.channel());
    }

    private void AddOrRemove(Channel channel, String s) {
        NotifyChannel notifyChannel = new NotifyChannel();
        notifyChannel.setMethod(NotifyChannel.METHOD_GROUP_CHAT);
        SendMsg sendMsg = new SendMsg();
        sendMsg.setChatMsg( NameUtil.getName(channel.remoteAddress().toString()) + s);
        notifyChannel.setSendMsg(sendMsg);
        channels.forEach(ch ->
                ch.writeAndFlush(JSON.toJSONString(notifyChannel)+"\n"));
    }
}
