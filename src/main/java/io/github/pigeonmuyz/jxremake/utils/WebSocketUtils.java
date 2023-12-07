package io.github.pigeonmuyz.jxremake.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.pigeonmuyz.jxremake.tools.HttpTool;
import org.glassfish.tyrus.client.ClientManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import snw.jkook.JKook;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.module.SectionModule;

import javax.websocket.*;
import java.net.URI;
import java.net.http.WebSocket;
import java.util.*;

@ClientEndpoint
public class WebSocketUtils implements WebSocket.Listener {
    private Session session;
    private URI uri;

    private boolean wssStatus = false;

    List<String> channelList = new ArrayList<>();
    List<String> userList = new ArrayList<>();

    public WebSocketUtils(URI uri){
        this.uri = uri;
        connect();
        try{
            refreshSend();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Timer refresh = new Timer();
    private void connect() {
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(this, uri);
            wssStatus = true;
            //开始定时刷新CD

            refresh.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        refreshSend();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 1800000); // 间隔30分钟刷新数据
        } catch (Exception e) {
            System.out.println("WebSocket连接失败: " + e.getMessage());
            wssStatus = false;
            scheduleReconnect(); // 连接失败后，开始定时重连
        }
    }

    void refreshSend() throws Exception{
        JsonNode jn;
        jn = new ObjectMapper().readTree(HttpTool.getData("http://localhost:25555/user/getsenduser?isChannel=1"));
        List<String> tempChannelList = new ArrayList<>();
        switch (jn.get("code").asInt()){
            case 200:
                if (jn.get("data").size() != 0){
                    for (int i = 0; i < jn.get("data").size(); i++) {
                        tempChannelList.add(jn.get("data").get(i).get("kookchannelID").asText());
                    }
                }
                channelList = tempChannelList;
                break;
        }
        jn = new ObjectMapper().readTree(HttpTool.getData("http://localhost:25555/user/getsenduser?isChannel=0"));
        List<String> tempUserList = new ArrayList<>();
        switch (jn.get("code").asInt()){
            case 200:
                if (jn.get("data").size() != 0){
                    for (int i = 0; i < jn.get("data").size(); i++) {
                        tempUserList.add(jn.get("data").get(i).get("kookid").asText());
                    }
                }
                userList = tempUserList;
                break;
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
    @OnMessage
    public void onMessage(String message) throws Exception {
        MultipleCardComponent mcc = null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message);
        JsonNode dataNode = rootNode.get("data");
        int code = rootNode.get("action").asInt();
        List<String> deleteChannelList = new ArrayList<>();
        List<String> deleteUserList = new ArrayList<>();
        switch (code){
            case 2001:
                if (dataNode.get("status").asInt() == 1 && dataNode.get("server").asText().equals("飞龙在天")){
                    HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"开服辣！","快去清cd辣！"));
                    mcc = new CardBuilder()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("游戏现在开服了！！！"),null,null))
                            .build();
                }else{
                    HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"开始维护了！","还是继续睡大觉吧！"));
                    mcc = new CardBuilder()
                            .setTheme(Theme.WARNING)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("游戏现在开始维护了捏"),null,null))
                            .build();
                }
                break;
            case 2002:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s&url=%s",code,dataNode.get("type"),dataNode.get("title").asText(),dataNode.get("url").asText()));
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new SectionModule(new PlainTextElement(dataNode.get("type").asText()+"："+dataNode.get("title").asText()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("时间："+dataNode.get("date").asText()),null,null))
                        .addModule(new SectionModule(new MarkdownElement("[原文传送门🚪]("+dataNode.get("url").asText()+")")))
                        .build();
                break;
            case 2003:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,"有新版本！版本号："+dataNode.get("new_version").asText(),"补丁大小："+dataNode.get("package_size")));
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("客户端更新辣！！！")))
                        .addModule(new SectionModule(new PlainTextElement("原版本："+dataNode.get("old_version").asText()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("新版本："+dataNode.get("new_version").asText()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("更新补丁大小："+dataNode.get("package_size").asText()),null,null))
                        .build();
                break;
            case 2004:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s&url=%s",code,dataNode.get("title").asText()+"吧","来自 "+dataNode.get("name"),dataNode.get("url").asText()));
                mcc = new CardBuilder()
                        .setTheme(Theme.WARNING)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("贴吧更新咯！")))
                        .addModule(new SectionModule(new PlainTextElement(dataNode.get("title").asText()),null,null))
                        .addModule(new SectionModule(new PlainTextElement("来自 "+dataNode.get("name").asText()+"吧"),null,null))
                        .addModule(new SectionModule(new PlainTextElement("时间："+dataNode.get("date").asText()),null,null))
                        .addModule(new SectionModule(new MarkdownElement("[原文传送门🚪]("+dataNode.get("url").asText()+")   [仅看楼主快速🚪]("+dataNode.get("url").asText()+"?see_lz=1)")))
                        .build();
                break;
            case 2005:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&body=%s&url=%s",code,dataNode.get("server").asText()+"的"+dataNode.get("castle").asText()+"即将刷新",dataNode.get("url").asText()));
                break;
            case 2006:
                HttpTool.get(String.format("http://api.muyz.xyz:25555/user/bark?code=%s&title=%s&body=%s",code,dataNode.get("name").asText() +" 即将开启！","地点："+dataNode.get("site").asText()));
                break;
            default:
                mcc = new CardBuilder()
                        .setTheme(Theme.INFO)
                        .setSize(Size.LG)
                        .addModule(new HeaderModule(new PlainTextElement("该死的，又有新推送要改！！")))
                        .addModule(new SectionModule(new PlainTextElement(message),null,null))
                        .build();
                TextChannel tc = (TextChannel) JKook.getCore().getHttpAPI().getChannel("3715090900510071");
                tc.sendComponent(mcc);
                break;
        }
        if (mcc != null){
            for (String channel:
                    channelList) {
                try{
                    TextChannel tempTc = (TextChannel) JKook.getCore().getHttpAPI().getChannel(channel);
                    tempTc.sendComponent(mcc);
                }catch (Exception e){
                    deleteChannelList.add(channel);
                }
            }
            for (String user:
                    userList) {
                try {
                    User tempUser = (User) JKook.getCore().getHttpAPI().getUser(user);
                    tempUser.sendPrivateMessage(mcc);
                }catch (Exception e){
                    deleteUserList.add(user);
                }
            }
            mcc = null;
        }
        if (deleteChannelList.isEmpty()){

        }else{
            for (String channel:
                    deleteChannelList) {
                HttpTool.getData("http://api.muyz.xyz:25555/user/deletechannel?kookchannelID="+channel);
            }
        }
        if (deleteUserList.isEmpty()){

        }else{
            for (String user:
                    deleteUserList) {
                HttpTool.getData("http://api.muyz.xyz:25555/user/deleteuser?kookid="+user);
            }
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
