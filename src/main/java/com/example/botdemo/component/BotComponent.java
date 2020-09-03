package com.example.botdemo.component;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactoryJvm;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lin945
 * @date 2020/8/30 14:05
 * 机器人模块
 */
@Component
@ConfigurationProperties(prefix = "bot.qq")
public class BotComponent {
    public Long user;
    private String password;
    private  int init = 0;
    private static Bot bot;

    public  void login() {
        init = 1;
        bot= BotFactoryJvm.newBot(user, password, new BotConfiguration() {
            {
                //保存设备信息到文件jar文件同一目录
                fileBasedDeviceInfo("deviceInfo.json");
                // setLoginSolver();xynw
                // setBotLoggerSupplier();
            }
        });
            bot.login();
    }
    public static Bot getBot() {
        return bot;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
