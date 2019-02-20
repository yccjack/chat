package com.ycc.netty.handler.spdy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class DefaultSpdyOrHttpChooser extends ApplicationProtocolNegotiationHandler {
    /**
     * Creates a new instance with the specified fallback protocol name.
     *
     * @param fallbackProtocol the name of the protocol to use when
     *                         ALPN/NPN negotiation fails or the client does not support ALPN/NPN
     */
    protected DefaultSpdyOrHttpChooser(String fallbackProtocol) {
        super(fallbackProtocol);
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {

    }
}
