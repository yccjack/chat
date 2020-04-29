package com.ycc.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ycc.chat.abst.AbstractLayoutController;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.bean.SendMsg;
import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
/**
 * @author :MysticalYcc
 * @date :11:12 2019/2/20
 */
public class ChatP2P extends AbstractLayoutController {
    @FXML
    public TextField sendMsgId;
    @FXML
    public TextArea chatHisId;
    @FXML
    public Button sendId;

    /**
     * 发送点对点消息
     */
    public void sendMsg() {
        SendMsg sendMsg = new SendMsg();
        sendMsg.setChatMsg(sendMsgId.getText());
        sendMsg.setSendTarget(remoteAddr);
        RedisProxy.set(ConfigConstant.chat_msg.getValue(), JSON.toJSONString(sendMsg));
        try {
            client.start();
        } catch (Exception e) {
            logger.error("ChatP2P.sendMsg();私聊功能出错："+ e);
        }
        sendMsgId.clear();

    }

    @Override
    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    @Override
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }


    @Override
    public void p2pChat(NotifyChannel notifyChannel) {
        String msg = handlerMsg( notifyChannel);
        chatHisId.appendText(msg + "\n");
        logger.debug(notifyChannel.toString());
    }
}
