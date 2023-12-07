package io.github.pigeonmuyz.jxremake.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.pigeonmuyz.jxremake.tools.HttpTool;
import snw.jkook.JKook;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.event.pm.PrivateMessageReceivedEvent;
import snw.jkook.message.component.card.MultipleCardComponent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import io.github.pigeonmuyz.jxremake.tools.CardTool;

public class MessageEvent implements Listener {

    /**
     * 时间格式
     */
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    List<MultipleCardComponent> cardMessage;

    String server = "飞龙在天";

    @EventHandler
    public void channelMessage(ChannelMessageEvent cme){
        String[] commands = cme.getMessage().getComponent().toString().split(" ");
        if (commands.length >= 2 && commands[0].equalsIgnoreCase(".send")&& cme.getMessage().getSender().getId().equals("1787060816")){
            switch (commands[1]){
                case "频道":
                    TextChannel tc = (TextChannel) JKook.getHttpAPI().getChannel(commands[2]);
                    tc.sendComponent(commands[3]);
                    break;
                case "用户":
                    User user = JKook.getHttpAPI().getUser(commands[2]);
                    user.sendPrivateMessage(commands[3]);
                    break;
            }
        }
            /*
              判断指令
             */
        if (cme.getMessage().getComponent().toString().split(" ").length >=2){
                /*
                  如果用户发送指令，例：花价 xxxx
                  则代表当前指令为多层
                  则进入该方法体
                 */

//            if (commands[1].equals("绑定")){
//                cme.getMessage().reply("如果没有看到报错的消息就是绑定成功了！！");
//                cme.getMessage().sendToSource("绑定的提示消息被飞龙的臭鸽子吃了！");
//            }
            cardMessage = CardTool.multiCommand(cme.getMessage().getComponent().toString().split(" "),cme.getMessage().getSender().getId(),cme.getChannel().getGuild().getId(),server);
            if (cardMessage.isEmpty()){
                return;
            }
            for (MultipleCardComponent card:
                    cardMessage) {
                cme.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }else{
                /*
                  如果用户发送指令，例：花价
                  则代表当前指令为单层
                  则进入该方法体
                 */
            cardMessage = CardTool.singleCommand(cme.getMessage().getComponent().toString(),cme.getMessage().getSender().getId(),cme.getChannel().getId(),server);
            for (MultipleCardComponent card:
                    cardMessage) {
                cme.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }

    }

    @EventHandler
    public void privateMessage(PrivateMessageReceivedEvent pmre){
        if (!pmre.getMessage().getSender().getId().equals("3107210249")){
            System.out.println("["+sdf.format(new Date(pmre.getTimeStamp()))+"]"+pmre.getMessage().getSender().getName()+"："+pmre.getMessage().getComponent().toString());
        }
        if (pmre.getMessage().getComponent().toString().split(" ").length >=2){
                /*
                  如果用户发送指令，例：花价 xxxx
                  则代表当前指令为多层
                  则进入该方法体
                 */
            cardMessage = CardTool.multiCommand(pmre.getMessage().getComponent().toString().split(" "),pmre.getMessage().getSender().getId(),null,server);
            for (MultipleCardComponent card:
                    cardMessage) {
                pmre.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }else{
                /*
                  如果用户发送指令，例：花价
                  则代表当前指令为单层
                  则进入该方法体
                 */
            cardMessage = CardTool.singleCommand(pmre.getMessage().getComponent().toString(),pmre.getMessage().getSender().getId(),null,server);
            for (MultipleCardComponent card:
                    cardMessage) {
                pmre.getMessage().sendToSource(card);
            }
            cardMessage.clear();
        }
    }
}
