package com.example.botdemo.events;

import com.example.botdemo.dao.DataBaseDao;
import com.example.botdemo.utils.HashString;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lin945
 * @date 2020/9/2 13:37
 */
@Component
public class MyFriendMessageEvent {
    @Autowired
    private DataBaseDao dataBaseDao;
    public  void addEvent(Bot bot) {
        Events.registerEvents(bot, new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onFriendMessageEvent(FriendMessageEvent event) {
                String msgString = event.getMessage().contentToString();
                if ("注册".equals(msgString)) {
                    long qq = event.getSender().getId();
                    String token = new String(Integer.toHexString(HashString.toHash("" + qq)));
                    if (dataBaseDao.getUserMap(token) == null) {
                        event.getFriend().sendMessage("你的密钥：" + token + "\napi：localhost:8088/send?token={token}&text={text}");
                        dataBaseDao.addUser(token, qq);
                    } else {
                        event.getFriend().sendMessage("你的密钥：" + token);
                    }
                }
                return ListeningStatus.LISTENING;
            }
        }
        );
    }
}
