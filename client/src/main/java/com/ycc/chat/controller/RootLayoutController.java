package com.ycc.chat.controller;

import com.alibaba.fastjson.JSON;
import com.ycc.Main;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.constant.ConfigConstant;
import com.ycc.netty.simulation.aop.RedisProxy;
import com.ycc.netty.simulation.server.ChatClient;
import io.netty.util.internal.StringUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author MysticalYcc
 */
public class RootLayoutController {

    private Logger logger = LoggerFactory.getLogger(RootLayoutController.class);
    private ChatClient client;
    @FXML
    public TextField sendMsgId;
    @FXML
    public TextArea chatHisId;
    @FXML
    public Button sendId;

    @FXML
    public ListView<String> chatHumanId;

    private Map<String, String> remoteAddrMap = new HashMap<>();
    /**
     * 聊天人员列表
     */
    private NotifyChannel notifyChannel = new NotifyChannel();
    private Main appMain;

    /**
     * 发送消息
     */
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

    public void clickListViewToP2P() {
        String s = chatHumanId.getSelectionModel().getSelectedItems().get(0);
        System.out.println(s);
        appMain.initP2P(s, remoteAddrMap.get(s));
    }

    public void setClient(ChatClient client) {
        this.client = client;
    }

    public void callBack() {
        String returnMsg = RedisProxy.get(ConfigConstant.chat_return_msg.getValue());
        if (!StringUtil.isNullOrEmpty(returnMsg) && returnMsg.contains("{") && returnMsg.contains("}")) {
            NotifyChannel notifyChannel = JSON.parseObject(returnMsg, NotifyChannel.class);

            try {
                /**
                 * 通过服务器端指定的method处理服务器端的聊天列表
                 */
                Method method = this.getClass().getDeclaredMethod(notifyChannel.getMethod(), NotifyChannel.class);
                method.invoke(this, notifyChannel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            chatHisId.appendText(returnMsg + "\n");
        }
    }


    public void init(NotifyChannel notifyChannel) {
        Map<String, String> chatList = notifyChannel.getChatList();
        List<String> chatListView = new ArrayList<>();
        for (Map.Entry<String, String> entry : chatList.entrySet()) {
            chatListView.add(entry.getValue());
            remoteAddrMap.put(entry.getValue(), entry.getKey());
        }
        this.notifyChannel.setChatList(chatList);
        ObservableList<String> strList = FXCollections.observableArrayList(chatListView);
        chatHumanId.setItems(strList);

    }

    public void add(NotifyChannel notifyChannel) {
        Map<String, String> chatList = this.notifyChannel.getChatList();
        remoteAddrMap.put(notifyChannel.getAddChatPerson(), notifyChannel.getAddChatRemote());
        chatList.put(notifyChannel.getAddChatRemote(), notifyChannel.getAddChatPerson());
        Platform.runLater(() -> chatHumanId.getItems().add(notifyChannel.getAddChatPerson()));
    }

    public void remove(NotifyChannel notifyChannel) {
        Map<String, String> chatList = this.notifyChannel.getChatList();
        remoteAddrMap.remove(notifyChannel.getAddChatPerson());
        chatList.remove(notifyChannel.getAddChatRemote(), notifyChannel.getAddChatPerson());
        Platform.runLater(() -> chatHumanId.getItems().remove(notifyChannel.getAddChatPerson()));

    }

    public void setAppMain(Main appMain) {
        this.appMain = appMain;
    }
}
