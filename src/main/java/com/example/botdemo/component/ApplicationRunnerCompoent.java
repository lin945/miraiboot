package com.example.botdemo.component;

import com.example.botdemo.events.MyEvents;
import com.example.botdemo.events.MyFriendMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author lin945
 * @date 2020/8/30 22:05
 * springboot初始化时加载机器人
 */
@Component
public class ApplicationRunnerCompoent implements ApplicationRunner {
    private final BotComponent botComponent;
    private final MyFriendMessageEvent messageEvent;
    private final MyEvents events;
    public ApplicationRunnerCompoent(BotComponent botComponent, MyEvents events, MyFriendMessageEvent messageEvent) {
        this.botComponent = botComponent;
        this.events = events;
        this.messageEvent = messageEvent;
    }
    @Override
    public void run(ApplicationArguments args) {
        //初始化机器人
       botComponent.login();
        //添加事件
        events.addEvent(BotComponent.getBot());
        messageEvent.addEvent(BotComponent.getBot());
    }
}