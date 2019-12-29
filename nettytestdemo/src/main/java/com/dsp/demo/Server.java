package com.dsp.demo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;


public class Server {
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

    public static class SampleInHandler extends ChannelDuplexHandler{

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("用户"+ServerUtils.index+"进入群聊");
            ServerUtils.getChannelMap().put(""+ServerUtils.index,ctx.channel());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String message = new String(bytes);
            System.out.println(message);
            String[] s = message.split("_");
            if(s.length>1 && ServerUtils.getChannelMap().containsKey(s[0])){
                ServerUtils.getChannelMap().get(s[0]).write(s[1]);
            }else{
                ServerUtils.getChannelMap().values().stream().forEach(t-> {
                    t.write(s[1]);
                });
            }

        }


    }


}
