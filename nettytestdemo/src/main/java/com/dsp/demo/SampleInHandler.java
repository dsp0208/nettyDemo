package com.dsp.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Arrays;

/**
 * @author:dingshangping
 * @time:2019/12/30 上午9:45
 */
public class SampleInHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户进入群聊");

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String message = new String(bytes);
        System.out.println(message);
        String[] s = message.split("_");
        if(s.length>1 && s[0].equals("auth")){
            Server.channelMap.put(s[1],ctx.channel());
        }else if(s.length>1 && Server.channelMap.containsKey(s[0])){
            Server.channelMap.get(s[0]).writeAndFlush(s[1]);
        }else{
            Server.channelMap.values().stream().forEach(t-> {
                t.writeAndFlush(s[0]);
            });
        }

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf byteBuf = Unpooled.buffer(1024);
        byteBuf.writeBytes(((String)msg).getBytes());
        ctx.writeAndFlush(byteBuf);
    }


}