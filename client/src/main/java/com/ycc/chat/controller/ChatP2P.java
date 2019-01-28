package com.ycc.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ycc.netty.bean.SendMsg;
import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.server.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalTime;

public class ChatP2P {

    private ChatClient client;
    @FXML
    public TextField sendMsgId;
    @FXML
    public TextArea chatHisId;
    @FXML
    public Button sendId;

    private String remoteAddr;

    private String remoteName;

    /**
     * 发送点对点消息
     */
    public void sendMsg() {
        //获取当前的时间
        LocalTime nowTime = LocalTime.now().withNano(0);
        SendMsg sendMsg = new SendMsg.Builder()
                .sendMsg(sendMsgId.getText())
                .sendTime(nowTime)
                .sendTarget(remoteAddr)
                .builder();
        RedisProxy.set(ConfigConstant.chat_msg.getValue(), JSON.toJSONString(sendMsg));
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMsgId.clear();

    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public void setClient(ChatClient client) {
        this.client = client;
    }
}
