package com.ycc.netty.bean;

import java.io.Serializable;
import java.time.LocalTime;

public class SendMsg implements Serializable {
    private static final long serialVersionUID = 1L;
    private String msg;
    private LocalTime sendTime;
    private String sendTarget;


    public static class Builder {
        private String msg;
        private LocalTime sendTime;
        private String sendTarget;

        public Builder sendMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public Builder sendTime(LocalTime sendTime) {
            this.sendTime = sendTime;
            return this;
        }

        public Builder sendTarget(String sendTarget) {
            this.sendTarget = sendTarget;
            return this;
        }


        public SendMsg builder() {
            return new SendMsg(this);
        }
    }

    private SendMsg(Builder builder) {
        this.msg = builder.msg;
        this.sendTarget = builder.sendTarget;
        this.sendTime = builder.sendTime;
    }

}
