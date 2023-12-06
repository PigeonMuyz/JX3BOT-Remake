package io.github.pigeonmuyz.jxremake;

import io.github.pigeonmuyz.jxremake.event.MessageEvent;
import io.github.pigeonmuyz.jxremake.utils.WebSocketUtils;
import snw.jkook.plugin.BasePlugin;

import java.net.URI;

public class Main extends BasePlugin {
    public static WebSocketUtils client = new WebSocketUtils(URI.create("wss://socket.nicemoe.cn"));
    @Override
    public void onLoad() {
        super.onLoad();
    }


    @Override
    public void onEnable() {
        System.out.println("注册消息监听器");
        getCore().getEventManager().registerHandlers(this,new MessageEvent());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
