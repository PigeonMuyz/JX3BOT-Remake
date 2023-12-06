package io.github.pigeonmuyz.jxremake.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import snw.jkook.message.component.card.CardBuilder;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.Size;
import snw.jkook.message.component.card.Theme;
import snw.jkook.message.component.card.element.ImageElement;
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
                    rootNode = mapper.readTree(HttpTool.getData("http://pigeon-server-developer:25555/api/daily?server="+server));
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
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 金价
                case "金价":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://pigeon-server-developer:25555/image/api/trade/demon?server="+server));
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
                //region 花价
                case "花价":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/image/api/home/flower?server="+server));
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
                //region 团队招募
                case "招募":
                case "团队招募":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/api/teamactivity?server="+server));
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
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 楚天行侠相关实现
                case "楚天行侠":
                case "楚天社":
                case "行侠":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://pigeon-server-developer:25555/api/celebrities"));
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
                            cb = new CardBuilder()
                                    .setTheme(Theme.DANGER)
                                    .setSize(Size.LG)
                                    .addModule(new SectionModule(new PlainTextElement("服务器响应异常，请联系管理或者核对参数后再次重试"),null,null));
                            break;
                    }
                    break;
                //endregion
                //region 百战
                case "百战":
                    initSaohua();
                    rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/image/api/active/monster"));
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
            }
            imagesList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
        return card;
    }

    static void initSaohua(){
        try{
            JsonNode rootNode = mapper.readTree(HttpTool.getData("http://api.muyz.xyz:25555/api/saohua"));
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