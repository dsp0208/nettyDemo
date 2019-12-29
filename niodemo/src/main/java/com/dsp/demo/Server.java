package com.dsp.demo;

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
                    System.out.println("连接建立");
                    SocketChannel socketChannel = serverChannle.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }
                if(key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    int i = channel.read(allocate);
                    if(i!=-1){
                        String msg = new String(allocate.array()).trim();
                        System.out.println(msg);
                        channel.write(ByteBuffer.wrap(msg.getBytes()));
                    }
                }
                iterator.remove();

            }

        }

    }
}
