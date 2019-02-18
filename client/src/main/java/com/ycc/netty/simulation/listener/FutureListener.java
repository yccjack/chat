package com.ycc.netty.simulation.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FutureListener implements ChannelFutureListener {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            log.info("连接成功");
        } else {
            Throwable cause = future.cause();
            if (cause == null) {
                log.error("连接失败,原因未知");
            } else {
                cause.printStackTrace();
            }
        }
    }
}
