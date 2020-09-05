package com.example.botdemo.events;

import com.example.botdemo.entity.ResponsePage;
import com.example.botdemo.service.ApiService;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.ServiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.gardel.howdoi.Answer;
import top.gardel.motd.Motd;

/**
 * @author lin945
 * @date 2020/8/31 22:37
 */
@Component
public class MyEvents {
    @Autowired
    private ApiService apiService;

    public void addEvent(Bot bot) {
        Events.registerEvents(bot, new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) throws MalformedURLException {
                String msgString = event.getMessage().contentToString();
                if (event.getGroup().getId() == 790264329) {
                    event.getGroup().sendMessage(msgString);
                }
                if (msgString.startsWith("/xml")) {
                    String content = msgString.substring(5);
                    ServiceMessage serviceMessage = new ServiceMessage(1, content);
                    event.getGroup().sendMessage(serviceMessage);
                } else if ("一言".equals(msgString)) {
                    Map<String, String> oneText = apiService.getOneText();
                    event.getGroup().sendMessage(oneText.get("hitokoto") + "\n --" + oneText.get("from"));
                } else if (msgString.startsWith("二维码")) {
                    String url = apiService.getQrCode(msgString.substring(4));
                    final Image image = event.getGroup().uploadImage(new URL(url));
                    event.getGroup().sendMessage(image); // 发送图片
                } else if (msgString.startsWith("搜图")) {
                    String url = "https://pixiv.cat/";
                    String text = msgString.substring(3);
                    ResponsePage response = apiService.searchImage(text);
                    if (response.getState().equals("200")) {
                        List<Integer> list = (List<Integer>) response.getData();
                        if (list != null) {
                            Integer id= list.get(new Random().nextInt(list.size())
                            );
                            String url1 = url + id + ".jpg";
                            String xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                                    "<msg serviceID=\"1\" action=\"web\" url=\""+url1+"\" brief=\"\">\n" +
                                    "<item>\n" +
                                    "<title>Id:"+id+"</title>\n" +
                                    "<picture cover=\""+url1+"\"/>\n" +
                                    "</item>\n" +
                                    "</msg>\n";
                            ServiceMessage serviceMessage = new ServiceMessage(1, xml);
                            event.getGroup().sendMessage(serviceMessage);
//                            HttpURLConnection connection = null;
//                            try {
//                                connection = (HttpURLConnection) url1.openConnection();
//                                //设置请求方式
//                                connection.setRequestMethod("GET");
//                                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//                                //连接
//                                connection.connect();
//                                System.out.println(connection.getResponseCode());
//                                final Image image = event.getGroup().uploadImage(connection.getInputStream());
//                                event.getGroup().sendMessage(image); // 发送图片
//                            } catch (IOException e) {
//
//                            }

                        }
                    }
                } else if (msgString.startsWith("trans#")) {
                    String arg = msgString.substring(msgString.indexOf('#'));
                    if (arg.isEmpty()) return ListeningStatus.LISTENING;
                    String query = arg.indexOf('#') != -1 ? arg.substring(arg.indexOf('#') + 1) : arg;
                    String locale = arg.indexOf('#') > 0 ? arg.substring(0, arg.indexOf('#')) : ":en";
                    String[] localeSplit = locale.split(":");
                    String langRegex = "^([a-z]{2,3})(-[A-Z]{2,3})?$";
                    String targetLanguage;
                    if (localeSplit.length > 1 && localeSplit[1].matches(langRegex))
                        targetLanguage = localeSplit[1];
                    else if (localeSplit.length == 1 && localeSplit[0].matches(langRegex))
                        targetLanguage = localeSplit[0];
                    else targetLanguage = "en";
                    String sourceLanguage = localeSplit.length > 1 && localeSplit[0].matches(langRegex) ? localeSplit[0] : "auto";
                    Message replyMessage = new QuoteReply(event.getSource()).plus(apiService.trans(query, targetLanguage, sourceLanguage));
                    event.getSubject().sendMessage(replyMessage);
                } else if (msgString.startsWith("howdoi#")) {
                    String arg = msgString.substring(msgString.indexOf('#'));
                    if (arg.isEmpty()) return ListeningStatus.LISTENING;
                    String query = apiService.trans(arg, "en");
                    Message replyMessage = new QuoteReply(event.getSource()).plus("正在为您搜索问题：" + query);
                    event.getSubject().sendMessage(replyMessage);
                    List<Answer> answers = apiService.howDoI(query, 1, 3);
                    event.getSubject().sendMessage(String.format("找到 %d 个回答", answers.size()));
                    delay(500);
                    for (Answer answer : answers) {
                        event.getSubject().sendMessage(String.format("标题：\n%s\n\n问题：\n%s", answer.getTitle(), answer.getQuestion()));
                        delay(kotlin.random.Random.Default.nextLong(1000, 2000));
                        event.getSubject().sendMessage(String.format("最佳回答：\n%s", answer.getAnswer()));
                        delay(kotlin.random.Random.Default.nextLong(1000, 2000));
                    }
                } else if (msgString.trim().equals("迫害")) {
                    QuoteReply quoteMessage = event.getMessage().first(QuoteReply.Key);
                    if (quoteMessage == null) {
                        event.getSubject().sendMessage("请回复被迫害的消息");
                        return ListeningStatus.LISTENING;
                    }
                    MessageSource source = quoteMessage.getSource();
                    String str = source.contentToString();
                    Member member = event.getGroup().get(source.getFromId());
                    String from = member.getNameCard();
                    if (from.isEmpty()) from = member.getNick();
                    if (str.isEmpty()) return ListeningStatus.LISTENING;
                    if (apiService.addMotd(str, from)) {
                        event.getSubject().sendMessage("迫害成功");
                    }
                } else if (msgString.startsWith("名言#")) {
                    String arg = msgString.substring(msgString.indexOf('#'));
                    Motd motd = apiService.getMotd(arg);
                    String reply = motd == null ? "暂无" : String.format("%s\n    --- %s", motd.getText(), motd.getFrom());
                    event.getSubject().sendMessage(reply);
                }//els



                return ListeningStatus.LISTENING;
            }

            public void delay(long ms) {
                try { Thread.sleep(ms); } catch (InterruptedException ignored) { }
            }

            @EventHandler
            public ListeningStatus onJoin(MemberJoinRequestEvent event) {
                event.accept();
                return ListeningStatus.LISTENING;
            }

            @EventHandler
            public ListeningStatus onNewFriendRequestEvent(NewFriendRequestEvent event) {
                event.accept();
                return ListeningStatus.LISTENING;
            }

        });
    }
}
