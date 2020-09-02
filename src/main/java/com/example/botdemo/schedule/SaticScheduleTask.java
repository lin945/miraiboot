package com.example.botdemo.schedule;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author lin945
 * @date 2020/8/30 16:24
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {
    //3.添加定时任务
    @Scheduled(cron = "0 0 0/5 * * ?")
    //或直接指定时间间隔，例如：5小时
    //@Scheduled(fixedRate=5000)
    private void configureTasks() {
//        BotDao.getBot().getGroup(0).sendMessage(new Date().toString());
    }
}