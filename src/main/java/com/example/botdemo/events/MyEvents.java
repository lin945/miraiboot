package com.example.botdemo.events;

import com.example.botdemo.dao.DataBaseDao;
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
import net.mamoe.mirai.message.data.ServiceMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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
            public ListeningStatus onGroupMessage(GroupMessageEvent event) {
                String msgString = event.getMessage().contentToString();
                if (event.getGroup().getId()==790264329){
                    event.getGroup().sendMessage(msgString);
                    if(msgString.startsWith("/xml")){
                        String content =msgString.substring(5);
                        ServiceMessage serviceMessage = new ServiceMessage(1,content);
                        event.getGroup().sendMessage(serviceMessage);
                    } else if (msgString.equals("一言")) {
                        Map<String, String> oneText = apiService.getOneText();
                        event.getGroup().sendMessage(oneText.get("hitokoto")+"\n ------"+oneText.get("from"));
                    }

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
