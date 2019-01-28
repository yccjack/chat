package com.ycc.chat.controller;

import com.ycc.Main;
import io.netty.util.internal.StringUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


/**
 * @author MysticalYcc
 */
public class ConnectLayoutController {
    private Main appMain;

    @FXML
    public TextField hostId;

    @FXML
    public TextField portId;

    @FXML
    public Button startId;


    public void setAppMain(Main appMain) {
        this.appMain = appMain;
    }


    public void start() {
        appMain.closeDialog();
        if (!StringUtil.isNullOrEmpty(portId.getText()) && !StringUtil.isNullOrEmpty(portId.getText())) {
            appMain.initRootLayout(hostId.getText(), Integer.parseInt(portId.getText()));
        } else {
            appMain.initRootLayout(null, 0);
        }
    }
}
