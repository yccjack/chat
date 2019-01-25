package com.ycc.netty.simulation.handler;

import com.ycc.chat.controller.RootLayoutController;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;


/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/22 9:27
 */
public class SimpleChatClientInitializer extends ChannelInitializer<SocketChannel> {

    private SimpleChatClientHandler simpleChatClientHandler;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        simpleChatClientHandler = new SimpleChatClientHandler();
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8196, Delimiters.lineDelimiter()))
                .addLast("decoder", new StringDecoder())
                .addLast("encoder", new StringEncoder())
                .addLast("handler", simpleChatClientHandler);
    }

    /**
     * 控制回调
     */
    public void setSimpleChatClientHandlerBack(RootLayoutController rootLayoutController) {
        simpleChatClientHandler.setRootLayoutController(rootLayoutController);
    }
}
