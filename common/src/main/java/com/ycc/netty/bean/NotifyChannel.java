package com.ycc.netty.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Map;

/**
 * @author MysticalYcc
 * @date 2019/1/26 17:16
 */
public class NotifyChannel implements Serializable {
    private static final long serialVersionUID = 1;
    private String method;
    private Map<String, String> chatList;
    private String addChatPerson;
    private String removeChatPerson;
    private String addChatRemote;
    private String removeChatRemote;

    private SendMsg sendMsg;

    @JSONField(serialize = false)
    public transient final static String METHOD_ADD = "add";
    @JSONField(serialize = false)
    public transient final static String METHOD_REMOVE = "remove";
    @JSONField(serialize = false)
    public transient final static String METHOD_INIT = "init";
    @JSONField(serialize = false)
    public transient final static String METHOD_P2PCHAT = "p2pChat";
    @JSONField(serialize = false)
    public transient final static String METHOD_GROUP_CHAT = "groupChat";
    @JSONField(serialize = false)
    public transient final static String HEARTBEAT = "heartbeat";

    @JSONField(serialize = false)
    public transient final static String METHOD_GROUP_NOTICE = "notice";

    public String getAddChatRemote() {
        return addChatRemote;
    }

    public void setAddChatRemote(String addChatRemote) {
        this.addChatRemote = addChatRemote;
    }

    public String getRemoveChatRemote() {
        return removeChatRemote;
    }

    public void setRemoveChatRemote(String removeChatRemote) {
        this.removeChatRemote = removeChatRemote;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getChatList() {
        return chatList;
    }

    public void setChatList(Map<String, String> chatList) {
        this.chatList = chatList;
    }

    public String getAddChatPerson() {
        return addChatPerson;
    }

    public void setAddChatPerson(String addChatPerson) {
        this.addChatPerson = addChatPerson;
    }

    public String getRemoveChatPerson() {
        return removeChatPerson;
    }

    public void setRemoveChatPerson(String removeChatPerson) {
        this.removeChatPerson = removeChatPerson;
    }

    public SendMsg getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(SendMsg sendMsg) {
        this.sendMsg = sendMsg;
    }
}
