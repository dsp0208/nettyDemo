package com.dsp.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

public class ClientA {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap client = new Bootstrap();
            client.group(worker);
            client.channel(NioServerSocketChannel.class).remoteAddress(new InetSocketAddress("localhost",8889)).handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new SampleInHandler());

                }
            });
            ChannelFuture f = client.connect().sync();
            f.channel().closeFuture().sync();
            while(true){
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String line = reader.readLine();
                if(ServerUtils.getChannelMap().containsKey("0")){
                    ServerUtils.getChannelMap().get("0").write(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static class SampleInHandler extends ChannelDuplexHandler{

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("开始群聊！");
            ServerUtils.getChannelMap().put(""+ServerUtils.index,ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String s = new String(bytes);
            System.out.println(s);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            ctx.writeAndFlush((String)msg);
        }
    }


}
