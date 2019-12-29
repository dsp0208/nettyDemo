package com.dsp.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverChannle = ServerSocketChannel.open();
        serverChannle.configureBlocking(false);
        serverChannle.socket().bind(new InetSocketAddress("localhost",8888));
        Selector selector = Selector.open();
        serverChannle.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isAcceptable()){
                    SocketChannel socketChannel = serverChannle.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    System.out.println("欢迎"+ServerUtils.index+"进入直播间");
                    ServerUtils.getChannelMap().put(""+ServerUtils.index,socketChannel);
                    ServerUtils.index++;
                }
                if(key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    int i = channel.read(allocate);
                    if(i!=-1){
                        final String msg = new String(allocate.array()).trim();
                        System.out.println(msg);
                        String[] s = msg.split("_");
                        if(s.length>1 && ServerUtils.getChannelMap().containsKey(s[0])){
                            SocketChannel socketChannel = ServerUtils.getChannelMap().get(s[0]);
                            socketChannel.write(ByteBuffer.wrap(s[1].getBytes()));
                        }else{
                            ServerUtils.getChannelMap().values().stream().forEach(t-> {
                                try {
                                    t.write(ByteBuffer.wrap(msg.getBytes()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }

                    }
                }
                iterator.remove();

            }

        }

    }
}
