package com.dsp.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author:dingshangping
 * @time:2019/12/30 上午9:45
 */
public class ClientSampleInHandlerA extends ChannelDuplexHandler{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("开始群聊！"+0+"进入！");
        ctx.channel().writeAndFlush("auth_0");
        ClientA.channel = ctx.channel();
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
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes(((String)msg).getBytes());
        ctx.writeAndFlush(byteBuf);
    }
}