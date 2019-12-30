package com.dsp.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;


public class Server {

    public static final Map<String, Channel> channelMap = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup acceptor = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap server = new ServerBootstrap();
            server.group(acceptor,worker);
            server.channel(NioServerSocketChannel.class).localAddress(new InetSocketAddress(8889)).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new SampleInHandler());
                }
            });
            ChannelFuture f = server.bind().sync();
            f.channel().closeFuture().sync();
        }finally {
            acceptor.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
