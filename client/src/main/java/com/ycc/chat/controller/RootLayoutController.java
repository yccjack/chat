package com.ycc.chat.controller;

import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.server.SimpleChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalTime;

public class RootLayoutController {

    private SimpleChatClient client;
    @FXML
    public TextField sendMsgId;
    @FXML
    public TextArea chatHisId;
    @FXML
    public Button sendId;

    public void sendMsg() {
        //获取当前的时间
        LocalTime nowTime = LocalTime.now().withNano(0);
        String msg = nowTime + ": " + sendMsgId.getText();
        RedisProxy.set(ConfigConstant.chat_msg.getValue(), msg);
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMsgId.clear();
    }

    public void setClient(SimpleChatClient client) {
        this.client = client;
    }

    public void callBack(){
        chatHisId.setText(RedisProxy.get(ConfigConstant.chat_return_msg.getValue()));
    }
}
