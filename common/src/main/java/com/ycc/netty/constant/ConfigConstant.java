package com.ycc.netty.constant;

/**
 * @author shihongkui
 * @version 1.0
 * @date 2019/1/21 14:18
 */
public enum ConfigConstant {
    /**
     * ssl认证密匙存放路径
     */
    keyStoreFilePath(""),
    /**
     * ssl认证对应密钥
     */
    keyStorePassword(""),
    /**
     * 服务开启关闭key
     */
    chat_active_cotl("YCC_CHAT_ACTIVE_CROL"),
    /**
     * 发送消息key
     */
    chat_msg("YCC_CHAT_MSG"),
    /**
     * 接收消息key
     */
    chat_return_msg("YCC_CHAT_RETURN_MSG"),

    /**
     * 服务器默认端口
     */
    serverPort("8081"),
    /**
     * 服务器默认地址
     */
    serverHost("192.168.6.211");
    private String value;

    ConfigConstant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
