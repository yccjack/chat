package com.ycc.chat.abst;

import com.alibaba.fastjson.JSON;
import com.ycc.Main;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.bean.SendMsg;
import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.aop.RegisterCallBackFc;
import com.ycc.netty.simulation.server.ChatClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public abstract class LayoutController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ChatClient client;

    protected Main appMain;

    protected String remoteAddr;

    protected String remoteName;
    /**
     * 记录自己获取的姓名
     */
    protected static String myName;
    /**
     * key：姓名；value：地址
     */
    protected Map<String, String> remoteAddrMap = new HashMap<>();
    /**
     * 聊天人员列表：key：地址；value：姓名
     */
    protected static NotifyChannel notifyChannel = new NotifyChannel();

    /**
     * 服务端回调
     */
    public void callBack() {
        String returnMsg = RedisProxy.get(ConfigConstant.chat_return_msg.getValue());
        logger.debug(returnMsg);
        NotifyChannel notifyChannel = JSON.parseObject(returnMsg, NotifyChannel.class);
        try {
            if (notifyChannel != null && notifyChannel.getMethod() != null) {
                String callBackMethod = notifyChannel.getMethod();
                Class<? extends LayoutController> aClass;
                if (callBackMethod.equalsIgnoreCase("heartbeat")) {
                    this.heartbeat();
                } else {
                    if (!callBackMethod.equalsIgnoreCase("p2pChat")) {
                        aClass = RegisterCallBackFc.callBackClMap.get("rootLayoutController").getClass();
                    } else {
                        aClass = this.getClass();
                    }
                    Method method = aClass.getDeclaredMethod(notifyChannel.getMethod(), NotifyChannel.class);
                    method.invoke(this, notifyChannel);
                }
            } else {
                logger.error("服务器回调客户端函数参数错误；" + returnMsg);
            }

        } catch (Exception e) {
            logger.error("RootLayoutController.callBack();服务器回调出错" + e);
        }
    }

    /**
     * 处理服务器返回数据拼接成字符串
     *
     * @param notifyChannel 传递的bean
     * @return 拼接消息字符串
     */
    protected String handlerMsg(NotifyChannel notifyChannel) {
        SendMsg sendMsg = notifyChannel.getSendMsg();
        Map<String, String> chatList = LayoutController.notifyChannel.getChatList();
        LocalTime sendTime = sendMsg.getSendTime();
        String msg;
        if (sendMsg.getName() == null) {
            if (chatList.get(sendMsg.getSendFrom()) == null) {
                msg = "";
            } else {
                msg = chatList.get(sendMsg.getSendFrom()).equalsIgnoreCase(myName) ? "[you]" : chatList.get(sendMsg.getSendFrom());
            }
        } else {
            msg = sendMsg.getName().equalsIgnoreCase(myName) ? "[you]" : sendMsg.getName();
        }
        return msg + "[" + sendTime + "] : " + sendMsg.getChatMsg();
    }

    public void heartbeat() {
        logger.info("心跳检测");
    }

    public abstract void p2pChat(NotifyChannel notifyChannel);

    public void setClient(ChatClient client) {
        this.client = client;
    }

    public void setAppMain(Main appMain) {
        this.appMain = appMain;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }
}
