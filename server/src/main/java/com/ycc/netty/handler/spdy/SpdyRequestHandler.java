package com.ycc.netty.handler.spdy;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class SpdyRequestHandler extends SpdyHttpRequestHandler  {
    @Override
    protected String getContent() {
        return "This content is transmitted via SPDY\r\n";
    }
}
