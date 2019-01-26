package com.ycc;

import com.ycc.chat.controller.ConnectLayoutController;
import com.ycc.chat.controller.RootLayoutController;
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
        loader.setLocation(this.getClass().getClassLoader().getResource("./root.fxml"));
        try {
            AnchorPane load = loader.load();
            RootLayoutController rootLayoutController = loader.getController();
            intClient(host, port);
            rootLayoutController.setClient(client);
            /**
             * 控制回调
             */
            client.setRootLayoutCallBack(rootLayoutController);
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

    public void initConnectLayout() {
        dialogPrimary = new Stage();
        dialogPrimary.setTitle("Connect");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getClassLoader().getResource("./connect.fxml"));
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

    public void closeDialog() {
        dialogPrimary.close();
    }

}
