package com.ycc.netty.handler;

import com.ycc.netty.constant.ConfigConstant;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import java.io.FileInputStream;
import java.security.KeyStore;


/**
 * @author ycc
 * @version 1.0
 * @date 2019/1/21 14:03
 * description : https  ssl验证类
 */
public class ChannelInitializerSSL extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;

    protected ChannelInitializerSSL(SocketChannel socketChannel) throws Exception {

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(ConfigConstant.keyStoreFilePath.getValue()), ConfigConstant.keyStorePassword.getValue().toCharArray());
            KeyManagerFactory keyStoreFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyStoreFactory.init(keyStore, ConfigConstant.keyStorePassword.getValue().toCharArray());
            sslContext = SslContextBuilder.forServer(keyStoreFactory).build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        SSLEngine sslEngine = sslContext.newEngine(socketChannel.alloc());
        pipeline.addLast(new SslHandler(sslEngine))
                .addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                .addLast("handler", new FullHttpHandler());
    }
}
