package cn.odboy.websocket.context;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketClientManager {
    /**
     * concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。（分布式必出问题）
     */
    private static final Map<String, WebSocketServer> CLIENT = new ConcurrentHashMap<>();

    public static void addClient(String sid, WebSocketServer webSocketServer) {
        // 如果存在就先删除一个，防止重复推送消息
        CLIENT.remove(sid);
        CLIENT.put(sid, webSocketServer);
    }

    public static void removeClient(String sid) {
        CLIENT.remove(sid);
    }

    public static Collection<WebSocketServer> getAllClient() {
        return CLIENT.values();
    }

    public static WebSocketServer getClientBySid(String sid) {
        return CLIENT.get(sid);
    }
}
