package io.github.pigeonmuyz.jxremake.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;
import snw.jkook.message.component.card.module.*;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CardTool{

    /**
     * 备注模块
     */
    static ContextModule context;
    static ObjectMapper mapper = new ObjectMapper();
    /**
     * 图片数组
     */
    static List<ImageElement> imagesList = new ArrayList<>();
    /**
     * 时间格式
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
    static JsonNode rootNode;
    static JsonNode dataNode;
    static CardBuilder cb;
    /**
     * 卡片集合
     */
    static List<MultipleCardComponent> card = new ArrayList<>();
    public static List<MultipleCardComponent> singleCommand(String command, String userID, String channelID, String server) {
        try {
            switch(command){
                //region 日常
                case "日常":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/daily?server="+server));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            /*
                                将日常拆分开
                             */
                            cb = new CardBuilder()
                                    .setTheme(Theme.PRIMARY)
                                    .setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("PVE日常", false)));
                            cb.addModule(new SectionModule(new PlainTextElement("秘境日常："+dataNode.get("war").asText()), null, null));
                            cb.addModule(new SectionModule(new PlainTextElement("公共日常："+dataNode.get("team").get(0).asText()), null, null));
                            cb.newCard().setTheme(Theme.DANGER).setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("PVP日常", false)));
                            cb.addModule(new SectionModule(new PlainTextElement("矿车：跨服•烂柯山"), null, null));
                            cb.addModule(new SectionModule(new PlainTextElement("战场："+dataNode.get("battle").asText()), null, null));
                            cb.newCard().setTheme(Theme.INFO).setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("PVX日常", false)));
                            if (dataNode.get("draw").asText().isEmpty() || dataNode.get("draw").asText().equals("null")){
                                cb.addModule(new SectionModule(new PlainTextElement("美人图：无"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("美人图："+dataNode.get("draw").asText()), null, null));
                            }
                            cb.addModule(new SectionModule(new PlainTextElement("门派事件："+dataNode.get("school").asText()), null, null));
                            cb.addModule(new SectionModule(new PlainTextElement(String.format("福源宠物：%s%s%s",dataNode.get("luck").get(0).asText(),dataNode.get("luck").get(1).asText(),dataNode.get("luck").get(2).asText())), null, null));
                            cb.newCard().setTheme(Theme.SUCCESS).setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("PVE周常", false)));
                            cb.addModule(new SectionModule(new PlainTextElement("五人秘境："+dataNode.get("team").get(1).asText()), null, null));
                            cb.addModule(new SectionModule(new PlainTextElement("十人秘境："+dataNode.get("team").get(2).asText()), null, null));
                            cb.newCard().setTheme(Theme.NONE).setSize(Size.LG);
                            cb.addModule(new ContextModule.Builder().add(new PlainTextElement("今天是"+dataNode.get("date").asText()+" 星期"+dataNode.get("week").asText(), false)).build());
                            cb.addModule(DividerModule.INSTANCE);
                            cb.addModule(context);
                            card.add(cb.build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 金价
                case "金价":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/trade/demon?server="+server));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 花价
                case "花价":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/home/flower?server="+server));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 团队招募
                case "招募":
                case "团队招募":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/teamactivity?server="+server));
                    System.out.println(rootNode.toString());
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 楚天行侠
                case "楚天行侠":
                case "楚天社":
                case "行侠":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/celebrities"));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            cb = new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new HeaderModule(new PlainTextElement("楚天社事件来咯！", false)))
                                    .newCard()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("现在正在进行："+dataNode.get("event").get(0).get("desc").asText(), false)));
                            cb.addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",dataNode.get("event").get(0).get("map_name"),dataNode.get("event").get(0).get("site"))),null,null));
                            cb.addModule(new SectionModule(new PlainTextElement("开始时间："+dataNode.get("event").get(0).get("time")), null, null));
                            cb.newCard()
                                    .setTheme(Theme.INFO)
                                    .setSize(Size.LG);
                            cb.addModule(new HeaderModule(new PlainTextElement("下一次将要进行："+dataNode.get("event").get(1).get("desc").asText(), false)));
                            cb.addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",dataNode.get("event").get(1).get("map_name"),dataNode.get("event").get(1).get("site"))),null,null));
                            cb.addModule(new SectionModule(new PlainTextElement("开始时间："+dataNode.get("event").get(1).get("time")), null, null));
                            cb.addModule(DividerModule.INSTANCE);
                            cb.addModule(new HeaderModule(new PlainTextElement(dataNode.get("event").get(2).get("desc").asText(), false)));
                            cb.addModule(new SectionModule(new PlainTextElement(String.format("地点：%s · %s",dataNode.get("event").get(2).get("map_name"),dataNode.get("event").get(2).get("site"))),null,null));
                            cb.addModule(new SectionModule(new PlainTextElement("开始时间："+dataNode.get("event").get(2).get("time")), null, null));
                            card.add(cb.build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 百战
                case "百战":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/active/monster"));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //TODO: 日志未实现
                //region 更新日志
                case "日志":
                case "更新日志":

                    break;
                //endregion
                //TODO: 帮助未实现
                //region 帮助

                //endregion
                //region 服务器开服查询
                case "开服":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/serverCheck?server="+server));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            cb = new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG);

                            if (rootNode.get("data").get("status").asInt() >0){
                                cb.addModule(new HeaderModule(new PlainTextElement(server+" 当前状态：开启", false)));
                            }else{
                                cb.addModule(new HeaderModule(new PlainTextElement(server+" 当前状态：维护", false)));
                            }

                            cb.newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(DividerModule.INSTANCE)
                                    .addModule(context);
                            card.add(cb.build());
                        break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 公告
                case "公告":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/web/news/announce"));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
            }
            imagesList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
        return card;
    }

    public static List<MultipleCardComponent> multiCommand(String[] command,String userID,String guildID,String server) {
        try{
            switch (command[0]){
                //region 绑定服务器
                case "绑定":
                    if (guildID != null){
                        JsonNode jn = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKChannelID="+guildID));
                        if (jn.get("code").asInt() == 200){
                            //频道已经绑定过了，走频道更新流程
                            HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&server="+command[1]);
                        }else{
                            HttpTool.getData("http://localhost:25555/user/add?KOOKChannelID="+guildID+"&server="+command[1]);
                        }
                    }

                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKID="+userID));
                    if (rootNode.get("code").asInt() == 200 && rootNode.get("data").get(0).get("KOOKID") != null){
                        if (guildID == null && rootNode.get("data").get(0).get("server") != null){
                            //用户已经绑定过了，走用户更新流程
                            HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&server="+command[1]);
                        }
                    }else{
                        //用户未绑定，走用户绑定流程
                        HttpTool.getData("http://localhost:25555/user/add?KOOKID="+userID+"&server="+command[1]);
                    }
                    card.add(new CardBuilder()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG)
                            .addModule(new SectionModule(new PlainTextElement("绑定成功！"),null,null))
                            .build());
                    break;
                //endregion
                //region 功能开关
                case "开启":
                case "关闭":
                    boolean status;
                    if(command[0].equals("开启")){
                        status = true;
                    }else{
                        status = false;
                    }
                    switch (command[1]){
                        case "版本更新":
                            if (guildID != null){
                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&VersionUpdate="+status);
                            }else{
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&VersionUpdate="+status);
                            }
                            if (status){
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已开启！"),null,null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已关闭！"),null,null));
                            }
                            card.add(cb.build());
                            break;
                        case "开服监控":
                            if (guildID != null){
                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&ServerStatus="+status);
                            }else{
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&ServerStatus="+status);
                            }
                            if (status){
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已开启！"),null,null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已关闭！"),null,null));
                            }
                            card.add(cb.build());
                            break;
                        case "新闻监控":
                            if (guildID != null){
                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&News="+status);
                            }else{
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&News="+status);
                            }
                            if (status){
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已开启！"),null,null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已关闭！"),null,null));
                            }
                            card.add(cb.build());
                            break;
                        case "818监控":
                            if (guildID != null){
                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&forumPost="+status);
                            }else{
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&forumPost="+status);
                            }
                            if (status){
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已开启！"),null,null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已关闭！"),null,null));
                            }
                            card.add(cb.build());
                            break;
                        case "先锋测试":
                            if (guildID != null){
                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&bossRefresh="+status);
                            }else{
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&bossRefresh="+status);
                            }
                            cb = new CardBuilder()
                                    .setTheme(Theme.SUCCESS)
                                    .setSize(Size.LG);
                            if (status){
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已开启！"),null,null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement(command[1]+"已关闭！"),null,null));
                            }
                            card.add(cb.build());
                            break;
//                        case "云从事件":
//                            if (guildID != null){
//                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&bossRefresh="+status);
//                            }else{
//                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&bossRefresh="+status);
//                            }
//                            break;
//                        case "关隘预告":
//                            if (guildID != null){
//                                HttpTool.getData("http://localhost:25555/user/update?KOOKChannelID="+guildID+"&bossRefresh="+status);
//                            }else{
//                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&bossRefresh="+status);
//                            }
                        case "手机通知":
                            if (guildID != null){
                                card.add(new CardBuilder()
                                        .setTheme(Theme.DANGER)
                                        .setSize(Size.LG)
                                        .addModule(new SectionModule(new PlainTextElement("请使用私聊机器人尝试此开关，Bark的唯一提示符请不要发给除机器人以外的人，谨防不法之徒用来电信诈骗"),null,null))
                                        .build());
                            }else{
                                rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKID="+userID));
                                HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&BarkNotify="+status);
                                if (rootNode.get("code").asInt() == 200 && rootNode.get("data").get(0).get("barkKey") != null){
                                    HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&barkNotify="+status);
                                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKID="+userID));
                                    CardBuilder cb = new CardBuilder()
                                            .setTheme(Theme.SUCCESS)
                                            .setSize(Size.LG);
                                    if (rootNode.get("data").get(0).get("barkNotify").asBoolean()){
                                        cb.addModule(new SectionModule(new PlainTextElement("Bark推送已经开启"),null,null));
                                    }else{
                                        cb.addModule(new SectionModule(new PlainTextElement("Bark推送已经关闭"),null,null));

                                    }
                                }else{
                                    card.add(new CardBuilder()
                                            .setTheme(Theme.DANGER)
                                            .setSize(Size.LG)
                                            .addModule(new SectionModule(new PlainTextElement("请先绑定BarkKey"),null,null))
                                            .build());
                                }
                            }
                            break;
                    }
                    break;
                //endregion
                //region 状态查询
                case "当前状态":
                case "功能状态":
                case "服务":
                    CardBuilder cb = new CardBuilder()
                            .setTheme(Theme.SUCCESS)
                            .setSize(Size.LG);
                    if (guildID != null){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKChannelID="+guildID));
                        if (rootNode.get("data").isEmpty() || rootNode.get(0).get("KOOKChannelID") == null) {
                            cb.addModule(new SectionModule(new PlainTextElement("当前频道未绑定服务器并开启任何功能"), null, null));
                        }else{
                            cb.addModule(new SectionModule(new PlainTextElement("当前频道功能开启状态："), null, null));
                            if (rootNode.get(0).get("VersionUpdate").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("版本更新：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("版本更新：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("ServerStatus").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("开服监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("开服监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("News").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("新闻监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("新闻监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("forumPost").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("818监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("818监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("bossRefresh").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("先锋测试：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("先锋测试：关闭"), null, null));
                            }
                        }
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKID="+userID));
                        if (rootNode.get("data").isEmpty() || rootNode.get(0).get("KOOKID") == null) {
                            cb.addModule(new SectionModule(new PlainTextElement("当前用户未绑定服务器，并开启任何功能"), null, null));
                        }else{
                            cb.addModule(new SectionModule(new PlainTextElement("当前用户功能开启状态："), null, null));
                            if (rootNode.get(0).get("VersionUpdate").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("版本更新：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("版本更新：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("ServerStatus").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("开服监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("开服监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("News").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("新闻监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("新闻监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("forumPost").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("818监控：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("818监控：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("bossRefresh").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("先锋测试：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("先锋测试：关闭"), null, null));
                            }
                            if (rootNode.get(0).get("barkNotify").asBoolean()){
                                cb.addModule(new SectionModule(new PlainTextElement("手机推送：开启"), null, null));
                            }else{
                                cb.addModule(new SectionModule(new PlainTextElement("手机推送：关闭"), null, null));
                            }
                        }
                    }
                    card.add(cb.build());
                    break;
                //endregion
                //region 外观
                case "外观":
                case "万宝楼":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/trade/record?name="+command[1]));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region Bark绑定
                case "Bark":
                case "消息推送密钥":
                case "消息推送":
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/user/get?KOOKID="+userID));
                    if (rootNode.get("code").asInt() == 200 && rootNode.get("data").get(0).get("KOOKID") != null){
                        if (guildID == null && rootNode.get("data").get(0).get("barkKey") != null){
                            //用户已经绑定过了，走用户更新流程
                            HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&barkKey="+command[1]+"&barkNotify=true");
                            card.add(new CardBuilder()
                                    .setTheme(Theme.SUCCESS)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("更改成功！将开启推送！"),null,null))
                                    .build());
                        }
                    }else{
                        //用户未绑定，走用户绑定流程
                        HttpTool.getData("http://localhost:25555/user/add?KOOKID="+userID+"&barkKey="+command[1]);
                        HttpTool.getData("http://localhost:25555/user/update?KOOKID="+userID+"&barkNotify=true");
                        card.add(new CardBuilder()
                                .setTheme(Theme.SUCCESS)
                                .setSize(Size.LG)
                                .addModule(new SectionModule(new PlainTextElement("绑定成功！默认将开启推送！"),null,null))
                                .build());
                    }

                    break;
                //endregion
                //region 奇遇
                case "奇遇":
                    initSaohua();
                    if (command.length>=3){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/luck/adventure?server="+command[1]+"&name="+command[2]+"&filter=1"));
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/luck/adventure?server="+server+"&name="+command[1]+"&filter=1"));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                break;
                //endregion
                //region JJC战绩
                case "名剑":
                case "战绩":
                case "JJC":
                    initSaohua();
                    if (command.length >= 4){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/match/recent?server="+command[1]+"&name="+command[2]+"&robot=剑三咕咕"+"&mode="+command[3]));
                    }else {
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/match/recent?server="+server+"&name="+command[1]+"&mode="+command[2]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 金价
                case "金价":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/trade/demon?server="+command[1]));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 团队招募
                case "招募":
                case "团队招募":
                    initSaohua();
                    if (command.length>=3){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/member/recruit?server="+command[1]+"&keyword="+command[2]));
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/member/recruit?server="+command[1]));
                        if (rootNode.get("code").asInt() != 200){
                            rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/member/recruit?server="+server+"&keyword="+command[1]));
                        }
                    }
                    if (command.length >= 4){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/match/recent?server="+command[1]+"&name="+command[2]+"&robot=剑三咕咕"+"&mode="+command[3]));
                    }else if (command.length <4){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/match/recent?server="+server+"&name="+command[1]+"&mode="+command[2]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 装备查询
                case "查询":
                    initSaohua();
                    if (command.length>=3){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/role/attribute?server="+command[1]+"&name="+command[2]));
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/role/attribute?server="+server+"&name="+command[1]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 魔盒文章索引
                case "魔盒":
                case "搜索":
                case "文章":
                case "魔盒文章":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/jx3box/search?keyword="+command[1]));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            cb = new CardBuilder()
                                    .setTheme(Theme.SUCCESS)
                                    .setSize(Size.LG);
                            if (rootNode.get("data").size()>0 || !rootNode.get("data").isEmpty()){
                                    cb.addModule(new HeaderModule(new PlainTextElement("以下是关于" + command[1] + "的魔盒搜索结果")));

                                for (int i = 0; i < rootNode.get("data").size(); i++) {
                                    cb.addModule(new SectionModule(new MarkdownElement("["+rootNode.get("data").get(i).get("postTitle").asText()+"]("+rootNode.get("data").get(i).get("url").asText()+")")));
                                }
                            }else{
                                    cb.addModule(new HeaderModule(new PlainTextElement("没有在魔盒的职业攻略、副本攻略及工具专区找到有关的信息")));

                            }
                            card.add(cb.build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 副本记录
                case "副本击杀":
                case "副本进度":
                case "击杀记录":
                case "进度":
                    initSaohua();
                    if (command.length>=3){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/role/teamCdList?server="+command[1]+"&name="+command[2]));
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/role/teamCdList?server="+server+"&name="+command[1]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 服务器开服查询
                case "开服":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/serverCheck?server="+command[1]));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            cb = new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG);

                            if (rootNode.get("data").get("status").asInt() >0){
                                cb.addModule(new HeaderModule(new PlainTextElement(server+" 当前状态：开启", false)));
                            }else{
                                cb.addModule(new HeaderModule(new PlainTextElement(server+" 当前状态：维护", false)));
                            }

                            cb.newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(DividerModule.INSTANCE)
                                    .addModule(context);
                            card.add(cb.build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 烟花
                case "烟花":
                    initSaohua();
                    if (command.length>=3){
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/watch/record?server="+command[1]+"&name="+command[2]));
                    }else{
                        rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/watch/record?server="+server+"&name="+command[1]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 沙盘
                case "沙盘":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/image/api/server/sand?server="+command[1]+"&desc=我只是个平凡的鸽鸽罢了"));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 成就相关实现
                case "成就":
                    initSaohua();
                    if (command.length >= 4){
                        rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/image/api/role/achievement?server="+command[1]+"&role="+command[2]+"&name="+command[3]));
                    }else {
                        rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/image/api/role/achievement?server="+server+"&role="+command[1]+"&name="+command[2]));
                    }
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            imagesList.add(new ImageElement(dataNode.get("url").asText(),"剑三咕咕",false));
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new ImageGroupModule(imagesList))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            card.add(new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null))
                                    .build());
                            break;
                    }
                    break;
                //endregion
                //region 宏
                case "宏":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/macros/jx3api?kungfu="+command[1]));
                    switch (rootNode.get("code").asInt()){
                        case 200:
                            dataNode = rootNode.path("data");
                            card.add(new CardBuilder()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement(dataNode.get("context").asText())))
                                    .newCard()
                                    .setTheme(Theme.NONE)
                                    .setSize(Size.LG)
                                    .addModule(context)
                                    .build());
                            break;
                        default:
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion

            }
            imagesList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
        return card;
    }

    static void initSaohua(){
        try{
            JsonNode rootNode = mapper.readTree(HttpTool.getData("http://localhost:25555/api/saohua"));
            JsonNode dataNode = rootNode.path("data");
            //简单的随机判断
            if (Math.random()>0.5){
                context = new ContextModule.Builder().add(new PlainTextElement("如果你觉得好用的话，可以输入：捐赠  来支持我继续运营哦")).build();
            }else{
                context = new ContextModule.Builder().add(new PlainTextElement(dataNode.get("text").asText())).build();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}