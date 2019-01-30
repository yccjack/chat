package com.ycc.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ycc.chat.abst.LayoutController;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.bean.SendMsg;
import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.server.ChatClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author MysticalYcc
 */
public class RootLayoutController extends LayoutController {

    @FXML
    public TextField sendMsgId;
    @FXML
    public TextArea chatHisId;
    @FXML
    public Button sendId;


    @FXML
    public ListView<String> chatHumanId;

    /**
     * 发送消息
     */
    public void sendMsg() {
        SendMsg sendMsg = new SendMsg();
        sendMsg.setChatMsg(sendMsgId.getText());
        RedisProxy.set(ConfigConstant.chat_msg.getValue(), JSON.toJSONString(sendMsg));
        try {
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMsgId.clear();
    }

    public void clickListViewToP2P() {
        String select = chatHumanId.getSelectionModel().getSelectedItems().get(0);
        if (select.contains("[you]")) {
            return;
        }
        appMain.initP2P(remoteAddrMap.get(select), select, null);
    }

    public void setClient(ChatClient client) {
        this.client = client;
    }


    /**
     * 初次连接初始化聊天列表
     *
     * @param notifyChannel 传输的bean
     */
    public void init(NotifyChannel notifyChannel) {
        Map<String, String> chatList = notifyChannel.getChatList();
        List<String> chatListView = new ArrayList<>();
        for (Map.Entry<String, String> entry : chatList.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(notifyChannel.getAddChatRemote())) {
                chatListView.add("[you]" + entry.getValue());
                LayoutController.myName = entry.getValue();
            } else {
                chatListView.add(entry.getValue());
            }
            remoteAddrMap.put(entry.getValue(), entry.getKey());
        }
        LayoutController.notifyChannel.setChatList(chatList);
        ObservableList<String> strList = FXCollections.observableArrayList(chatListView);
        chatHumanId.setItems(strList);

    }

    /**
     * 新连接增加聊天人员
     *
     * @param notifyChannel 传输的bean
     */
    public void add(NotifyChannel notifyChannel) {
        Map<String, String> chatList = LayoutController.notifyChannel.getChatList();
        remoteAddrMap.put(notifyChannel.getAddChatPerson(), notifyChannel.getAddChatRemote());
        chatList.put(notifyChannel.getAddChatRemote(), notifyChannel.getAddChatPerson());
        Platform.runLater(() -> chatHumanId.getItems().add(notifyChannel.getAddChatPerson()));
    }

    /**
     * 离线或者异常删除对应聊天人员
     *
     * @param notifyChannel 传输的bean
     */
    public void remove(NotifyChannel notifyChannel) {
        Map<String, String> chatList = LayoutController.notifyChannel.getChatList();
        remoteAddrMap.remove(notifyChannel.getAddChatPerson());
        chatList.remove(notifyChannel.getAddChatRemote(), notifyChannel.getAddChatPerson());
        Platform.runLater(() -> chatHumanId.getItems().remove(notifyChannel.getAddChatPerson()));
    }

    /**
     * 控制javaFX群聊GUI
     *
     * @param notifyChannel 传输的bean
     */
    public void groupChat(NotifyChannel notifyChannel) {
        String msg = handlerMsg(notifyChannel);
        chatHisId.appendText(msg + "\n");
        logger.debug(notifyChannel.toString());
    }

    /**
     * 唤醒私聊窗口
     *
     * @param notifyChannel 传输的bean
     */
    @Override
    public void p2pChat(NotifyChannel notifyChannel) {
        SendMsg msg = notifyChannel.getSendMsg();
        Platform.runLater(() -> appMain.initP2P(msg.getSendFrom(), LayoutController.notifyChannel.getChatList().get(msg.getSendFrom()), notifyChannel));
    }

}
