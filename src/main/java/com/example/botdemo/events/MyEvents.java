package com.example.botdemo.events;

import com.example.botdemo.dao.DataBaseDao;
import com.example.botdemo.entity.ResponsePage;
import com.example.botdemo.service.ApiService;
import com.example.botdemo.utils.HashString;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.ServiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author lin945
 * @date 2020/8/31 22:37
 */
@Component
public class MyEvents {
    @Autowired
    private ApiService apiService;
    public  void addEvent(Bot bot) {
        Events.registerEvents(bot, new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) throws MalformedURLException {
                String msgString = event.getMessage().contentToString();
                if (event.getGroup().getId()==790264329){
                    event.getGroup().sendMessage(msgString);
                    if(msgString.startsWith("/xml")){
                        String content =msgString.substring(5);
                        ServiceMessage serviceMessage = new ServiceMessage(1,content);
                        event.getGroup().sendMessage(serviceMessage);
                    } else if ("一言".equals(msgString)) {
                        Map<String, String> oneText = apiService.getOneText();
                        event.getGroup().sendMessage(oneText.get("hitokoto")+"\n --"+oneText.get("from"));
                    }else if (msgString.startsWith("二维码")){
                        String url = apiService.getQrCode(msgString.substring(4));
                        final Image image = event.getGroup().uploadImage(new URL(url));
                        event.getGroup().sendMessage(image); // 发送图片
                    }else if (msgString.startsWith("搜图")){
                        String url = "https://pixiv.cat/";
                        String text = msgString.substring(3);
                        ResponsePage response = apiService.searchImage(text);
                        if (response.getState().equals("200")){
                            List<Integer> list = (List<Integer>)response.getData();
                            URL url1 = new URL(url + list.get(new Random().nextInt(list.size())
                            ) + ".jpg");
                            HttpURLConnection connection = null;
                            try {
                                connection = (HttpURLConnection) url1.openConnection();
                                //设置请求方式
                                connection.setRequestMethod("GET");
                                connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                                //连接
                                connection.connect();
                                System.out.println(connection.getResponseCode());
                                final Image image = event.getGroup().uploadImage(connection.getInputStream());
                                event.getGroup().sendMessage(image); // 发送图片
                            } catch (IOException e) {

                            }

                        }
                    }//

                }
                return ListeningStatus.LISTENING;
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
