package com.ycc.netty.simulation.handler;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.aop.RegisterCallBackFc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:21
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    private Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        RedisProxy.set(ConfigConstant.chat_return_msg.getValue(), s);
        RegisterCallBackFc.callBackClMap.get("realController").callBack();
        logger.debug(s);
    }

}
