package client.rpc.config;
/*
 * @author uv
 * @date 2018/10/14 15:34
 * 全局配置
 */

import io.netty.channel.Channel;
import client.rpc.netty.client.ClientHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalConfig {

    //key为ip:port，存储channel，方便其他类获取channel
    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();
    //key为ip:port，存储handler，方便其他类获取handler
    private static Map<String, ClientHandler> handlerMap = new ConcurrentHashMap<>();

    public static Map<String, Channel> getChannelMap() {
        return channelMap;
    }

    public static Map<String, ClientHandler> getHandlerMap() {
        return handlerMap;
    }
}
