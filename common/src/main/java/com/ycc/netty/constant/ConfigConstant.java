package com.ycc.netty.constant;

/**
 * @author MysticalYcc
 * @version 1.0
 * @date 2019/1/21 14:18
 * update_1 2020年4月29日
 */
public class ConfigConstant {
    /**
     * ssl认证密匙存放路径
     */
    public final static String KEY_STORE_FILE_PATH = "";
    /**
     * ssl认证对应密钥
     */
    public final static String KEY_STORE_PASSWORD = "";
    /**
     * 服务开启关闭key
     */
    public final static String CHAT_ACTIVE_COTL = "LAL_CHAT_ACTIVE_CROL";
    /**
     * 发送消息key
     */
    public final static String CHAT_MSG = "LAL_CHAT_MSG";
    /**
     * 接收消息key
     */
    public final static String CHAT_RETURN_MSG = "LAL_CHAT_RETURN_MSG";

    /**
     * 服务器默认端口
     */
    public final static int CLIENT_SERVER_PORT = 8082;
    /**
     * 服务器默认地址
     */
    public final static String SERVER_HOST = "localhost";

    /**
     * 消息分隔符
     */
    public final static String MSG_SPLIT = "\n";

}
