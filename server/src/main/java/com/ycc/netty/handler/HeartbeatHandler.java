package com.ycc.netty.handler;

import com.alibaba.fastjson.JSON;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.bean.SendMsg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private Logger log = LoggerFactory.getLogger(HeartbeatHandler.class);
    private static final ByteBuf HEARTBEAT_SEQUENCE;

    static {
        SendMsg sendMsg = new SendMsg();
        sendMsg.setChatMsg("心跳检测");
        NotifyChannel notifyChannel = new NotifyChannel();
        notifyChannel.setMethod(NotifyChannel.HEARTBEAT);
        notifyChannel.setSendMsg(sendMsg);
        HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(JSON.toJSONString(notifyChannel) + "\n", CharsetUtil.UTF_8));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            log.info("连接超时未发送任何内容;");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
