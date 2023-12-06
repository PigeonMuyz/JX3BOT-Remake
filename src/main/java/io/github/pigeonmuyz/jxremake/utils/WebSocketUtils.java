package io.github.pigeonmuyz.jxremake.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pigeonmuyz.jxremake.tools.HttpTool;
import org.glassfish.tyrus.client.ClientManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.*;
import java.net.URI;
import java.net.http.WebSocket;
import java.util.*;

@ClientEndpoint
public class WebSocketUtils implements WebSocket.Listener {
    private Session session;
    private URI uri;

    private boolean wssStatus = false;

    public WebSocketUtils(URI uri){
        this.uri = uri;
        connect();
    }

    private void connect() {
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(this, uri);
            wssStatus = true;
        } catch (Exception e) {
            System.out.println("WebSocket连接失败: " + e.getMessage());
            wssStatus = false;
            scheduleReconnect(); // 连接失败后，开始定时重连
        }
    }

    private void scheduleReconnect() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connect(); // 定时器触发时，进行重连
                if (wssStatus){
                    timer.cancel();
                }
            }
        }, 5000); // 重连间隔5秒
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WSS服务已经开启");
        System.out.println("当前SessionID："+session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WSS服务已经关闭");
        this.session = null;
        wssStatus = false;
        scheduleReconnect();
    }
    //TODO: 机器人的消息方法还没写，仅实现了推送给iPhone订阅用户的消息
    @OnMessage
    public void onMessage(String message) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message);
        JsonNode dataNode = rootNode.get("data");
        int code = rootNode.get("action").asInt();

        switch (code){
            case 2001:
                if (dataNode.get("status").asInt() == 1 && dataNode.get("server").asText().equals("飞龙在天")){
                    HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"开服辣！","快去清cd辣！"));
                }else{
                    HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"开始维护了！","还是继续睡大觉吧！"));
                }
                break;
            case 2002:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s&url=%s",code,dataNode.get("type"),dataNode.get("title").asText(),dataNode.get("url").asText()));
                break;
            case 2003:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"有新版本！版本号："+dataNode.get("new_version").asText(),"补丁大小："+dataNode.get("package_size")));
                break;
            case 2004:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s&url=%s",code,dataNode.get("title").asText()+"吧","来自 "+dataNode.get("name"),dataNode.get("url").asText()));
                break;
            case 2005:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&body=%s&url=%s",code,dataNode.get("server").asText()+"的"+dataNode.get("castle").asText()+"即将刷新",dataNode.get("url").asText()));
                break;
            case 2006:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,dataNode.get("name").asText() +" 即将开启！","地点："+dataNode.get("site").asText()));
                break;
            default:

                break;
        }

    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }

    public void close() {
        try {
            this.session.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
