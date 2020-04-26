package com.ycc.netty.simulation.aop;

import com.ycc.chat.abst.LayoutController;
import com.ycc.netty.simulation.handler.ChatClientHandler;

import java.util.HashMap;
import java.util.Map;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public enum RegisterCallBackFc {
    /**
     *
     */
    INSTANCE;
    public static Map<String, LayoutController> callBackClMap = new HashMap<>();

    private static ChatClientHandler chatClientHandler;

    static {
        chatClientHandler = new ChatClientHandler();
    }

    public static RegisterCallBackFc getInstance() {
        return INSTANCE;
    }


    public static void registerCallBack(String callBackName, LayoutController controller) {
        callBackClMap.put(callBackName, controller);
    }

    public static ChatClientHandler getChatClientHandler() {
        return chatClientHandler;
    }
}
