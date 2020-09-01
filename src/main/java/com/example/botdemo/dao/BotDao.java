package com.example.botdemo.dao;

import com.example.botdemo.utils.HashString;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.Events;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lin945
 * @date 2020/8/30 14:05
 */
@Component
public class BotDao {
    @Autowired
    private DataBaseDao dataBaseDao;
    private  int init = 0;
    private final static Bot bot = BotFactoryJvm.newBot(123455, "", new BotConfiguration() {
        {
            //保存设备信息到文件
            fileBasedDeviceInfo("deviceInfo1.json");
            // setLoginSolver();xynw
            // setBotLoggerSupplier();
        }
    });

    public  void login() {
            bot.login();
    }
    public static Bot getBot() {
        return bot;
    }
    public  void addEvent() {
        init = 1;
        Bot bot = getBot();
        bot.login();
        Events.registerEvents(bot, new SimpleListenerHost() {
            @EventHandler
            public ListeningStatus onGroupMessage(GroupMessageEvent event) {
                String msgString = event.getMessage().toString();
//                if (msgString.contains("reply")) {
//                    // 引用回复
//                    final QuoteReply quote = new QuoteReply(event.getSource());
//                    event.getGroup().sendMessage(quote.plus("引用回复"));
//                }
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

            @EventHandler
            public ListeningStatus onFriendMessageEvent(FriendMessageEvent event) {
                String msgString = event.getMessage().contentToString();
                if ("注册".equals(msgString)) {
                    long qq = event.getSender().getId();
                    String token = new String(Integer.toHexString(HashString.toHash("" + qq)));
                    if (dataBaseDao.getUserMap(token)==null) {
                        event.getFriend().sendMessage("你的密钥："+token+"\napi：localhost:8088/send?token={token}&text={text}");
                        dataBaseDao.addUser(token, qq);
                    }else {
                        event.getFriend().sendMessage("你的密钥："+token);
                    }
                }
                return ListeningStatus.LISTENING;

            }
        });
    }
}
