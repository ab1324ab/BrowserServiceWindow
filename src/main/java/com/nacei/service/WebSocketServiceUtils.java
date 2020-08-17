package com.nacei.service;

import com.nacei.view.MainView;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import javax.swing.*;
import java.util.*;

public class WebSocketServiceUtils {

    private static final Map<String,WebSocketService> WebSocketMap = new HashMap<String,WebSocketService>();

    /**
     * 通过connKey 获取其对应的websocket连接
     * @param connKey
     * @return
     */
    public static WebSocketService getWebSocketConn(String connKey) {
        return WebSocketMap.get(connKey);
    }

    /**
     * 向连接池中添加连接
     */
    public static void addWebSocketConn(String connKey, WebSocketService conn) {
        WebSocketMap.put(connKey, conn); // 添加连接
    }

    /**
     * 获取所有连接池中的连接，因为set是不允许重复的，所以可以得到无重复的user数组
     * @return
     */
    public static Collection<WebSocketService> getAllWebSocketConn() {
        List<WebSocketService> socketList = new ArrayList<WebSocketService>();
        Collection<WebSocketService> socketColl = WebSocketMap.values();
        for (WebSocketService webSocketService : socketColl) {
            socketList.add(webSocketService);
        }
        return socketList;
    }

    /**
     * 移除连接池中的连接
     */
    public static void removeWebSocketConn(String conn) {
        WebSocketMap.remove(conn); // 移除连接
    }

    /**
     * 向所有的用户发送消息
     *
     * @param message
     */
    public static void sendMessageToAll(String message) {
        Set<String> keySet = WebSocketMap.keySet();
        synchronized (keySet) {
            for (String connKey : keySet) {
                WebSocketService conn = WebSocketMap.get(connKey);
                conn.onMessage(message);
            }
        }
    }

}
