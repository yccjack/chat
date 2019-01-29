package com.ycc;

import com.ycc.chat.abst.LayoutController;
import com.ycc.chat.controller.ConnectLayoutController;
import com.ycc.netty.bean.NotifyChannel;
import com.ycc.netty.simulation.aop.RegisterCallBackFc;
import com.ycc.netty.simulation.server.ChatClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * @author MysticalYcc
 */
public class Main extends Application {

    private Stage primaryStage;

    private static ChatClient client;

    public void initRootLayout(String host, int port) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getClassLoader().getResource("root.fxml"));
        try {
            AnchorPane load = loader.load();
            LayoutController rootLayoutController = loader.getController();
            intClient(host, port);
            rootLayoutController.setClient(client);
            rootLayoutController.setAppMain(this);
            /**
             * 注册回调函数
             */
            RegisterCallBackFc.registerCallBack("rootLayoutController", rootLayoutController);
            RegisterCallBackFc.registerCallBack("realController", rootLayoutController);
            Scene scene = new Scene(load);
            primaryStage.setTitle("Chat");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        initConnectLayout();

    }

    /**
     * 初始化netty客户端
     *
     * @param host 服务器地址
     * @param port 端口号
     */
    private void intClient(String host, int port) {
        try {
            if (host != null && port != 0) {
                client = new ChatClient(host, port);
            } else {
                client = new ChatClient();
            }
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
        try {
            client.destroyClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage dialogPrimary;

    /**
     * 初始化启动connect.fxml
     */
    private void initConnectLayout() {
        dialogPrimary = new Stage();
        dialogPrimary.setTitle("Connect");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getClassLoader().getResource("connect.fxml"));
        try {
            AnchorPane load = loader.load();
            ConnectLayoutController connectLayoutController = loader.getController();
            connectLayoutController.setAppMain(this);
            Scene scene = new Scene(load);
            dialogPrimary.setScene(scene);
            dialogPrimary.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭模组
     */
    public void closeDialog() {
        dialogPrimary.close();
    }

    public void initP2P(String remoteAddr, String remoteName, NotifyChannel notifyChannel) {
        Stage dialogP2P = new Stage();
        dialogP2P.setTitle("P2P");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getClassLoader().getResource("P2P.fxml"));
        try {
            AnchorPane load = loader.load();
            LayoutController chatP2P = loader.getController();
            chatP2P.setRemoteAddr(remoteAddr);
            chatP2P.setRemoteName(remoteName);
            /**
             * 注册回调函数
             */
            RegisterCallBackFc.registerCallBack("realController", chatP2P);
            RegisterCallBackFc.registerCallBack("chatP2P", chatP2P);
            chatP2P.setClient(client);
            Scene scene = new Scene(load);
            dialogP2P.setScene(scene);
            dialogP2P.show();
            if (notifyChannel != null) {
                chatP2P.p2pChat(notifyChannel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
