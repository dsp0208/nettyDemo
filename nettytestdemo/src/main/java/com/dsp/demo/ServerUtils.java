package com.dsp.demo;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class ServerUtils {

    public static int index = 0;

    public static Map<String, Channel> channelMap = new HashMap<>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    public static void setChannelMap(Map<String, Channel> channelMap) {
        ServerUtils.channelMap = channelMap;
    }

}
