# chat
搭建一个聊天的服务器

# problem
在javafx->TextFile中输入发送信息后直接处理textArea域,会因为发送到服务器这段延迟致使textArea域一直是上一次发送的数据;

![回调](image/huidiao.gif)
可以在image下面观看动图;

在MainApp中增加回调  `client.setRootLayoutCallBack(rootLayoutController);`

(```)
  public void callBack(){
        chatHisId.setText(RedisProxy.get(ConfigConstant.chat_return_msg.getValue()));
    }
(```)
