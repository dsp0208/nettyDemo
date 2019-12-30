package com.dsp.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class ClientA {

    public static Channel channel = null;
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(worker);
            client.channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress("localhost",8889)).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new ClientSampleInHandlerA());

                }
            });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        String line = "";
                        try {
                            line = reader.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(channel != null){
                            channel.writeAndFlush(line);
                        }
                    }
                }
            }).start();
            ChannelFuture f = client.connect().sync();
            f.channel().closeFuture().sync();
        } finally {
            worker.shutdownGracefully();
        }
    }


}
