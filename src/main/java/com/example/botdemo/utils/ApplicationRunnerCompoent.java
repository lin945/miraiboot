package com.example.botdemo.utils;

import com.example.botdemo.dao.BotDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author lin945
 * @date 2020/8/30 22:05
 */
@Component
public class ApplicationRunnerCompoent implements ApplicationRunner {
    @Autowired
    BotDao botDao;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //初始化机器人
        botDao.login();
        //添加事件
       botDao.addEvent();
    }
}