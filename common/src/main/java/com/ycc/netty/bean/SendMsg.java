package com.ycc.netty.bean;

import java.io.Serializable;
import java.time.LocalTime;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class SendMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private String chatMsg;
    private LocalTime sendTime =LocalTime.now().withNano(0);
    private String sendTarget;
    private String sendFrom;

    private String name;

    public String getChatMsg() {
        return chatMsg;
    }

    public void setChatMsg(String chatMsg) {
        this.chatMsg = chatMsg;
    }

    public LocalTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalTime sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendTarget() {
        return sendTarget;
    }

    public void setSendTarget(String sendTarget) {
        this.sendTarget = sendTarget;
    }

    public String getSendFrom() {
        return sendFrom;
    }

    public void setSendFrom(String sendFrom) {
        this.sendFrom = sendFrom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
