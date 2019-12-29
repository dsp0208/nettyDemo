package com.dsp.demo;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerUtils {

    public static int index = 0;

    public static List<SocketChannel> channels = new ArrayList<>();

    public static Map<String,SocketChannel> channelMap = new HashMap<>();

    public static Map<String, SocketChannel> getChannelMap() {
        return channelMap;
    }

    public static void setChannelMap(Map<String, SocketChannel> channelMap) {
        ServerUtils.channelMap = channelMap;
    }

    public static List<SocketChannel> getChannels() {
        return channels;
    }

    public static void setChannels(List<SocketChannel> channels) {
        ServerUtils.channels = channels;
    }
}
