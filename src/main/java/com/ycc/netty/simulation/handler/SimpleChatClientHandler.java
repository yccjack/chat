package com.ycc.netty.simulation.handler;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.util.RedisUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:21
 */
public class SimpleChatClientHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        RedisUtil.set(ConfigConstant.chat_return_msg.getValue(), s);
        System.out.println(s);
    }
}
