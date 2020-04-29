package com.ycc.netty.simulation.handler;

import com.ycc.Main;
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
 * *2020年4月29日 读取服务器消息并调用回调函数处理。
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    private Logger logger = LoggerFactory.getLogger(ChatClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        RedisProxy.set(ConfigConstant.chat_return_msg.getValue(), msg);
        RegisterCallBackFc.callBackClMap.get("realController").callBack();
        logger.debug(msg);
    }

}
