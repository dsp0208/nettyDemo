package com.dsp.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {
    public static void main(String[] args) throws Exception {
        SocketChannel socketchannel = SocketChannel.open();
        socketchannel.configureBlocking(false);
        socketchannel.connect(new InetSocketAddress("localhost",8888));
        Selector selector = Selector.open();
        socketchannel.register(selector, SelectionKey.OP_CONNECT);
        while(true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                if(key.isConnectable()){
                    System.out.println("连接成功");
                    final SocketChannel channel = (SocketChannel) key.channel();
                    channel.register(selector, SelectionKey.OP_READ);
                    new Thread(new Runnable() {
                        ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
                        @Override
                        public void run() {
                            try{
                                if(!channel.isConnected()){
                                    channel.finishConnect();
                                }
                                while(true){
                                    writeBuffer.clear();
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                                    String line = reader.readLine();
                                    writeBuffer.put(line.getBytes());
                                    writeBuffer.flip();
                                    channel.write(writeBuffer);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }if(key.isReadable()){
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer allocate = ByteBuffer.allocate(1024);
                    int read = channel.read(allocate);
                    if(read > 1)
                    System.out.println(new String(allocate.array(),0,read));
                }
            }
            iterator.remove();
        }
    }
}
