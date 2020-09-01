package com.example.botdemo.controller;


import com.example.botdemo.ResponsePage;
import com.example.botdemo.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @author lin945
 * @date 2020/8/30 14:09
 */
@Validated
@RestController
public class BotController {
    @Autowired
    private BotService botService;

    @RequestMapping("/get")
    public String getAll() {
        return botService.getNick();
    }

    @RequestMapping("/getfriends")
    public ResponsePage getFriends() {
        return new ResponsePage().ok(botService.etFriends());
    }

    @RequestMapping("/send")
    public ResponsePage send(@NotBlank(message = "token不能为空！")String token,@NotBlank(message = "消息不能为空！") String text) {
        botService.sendMessage(token, text);
        return new ResponsePage().ok("ok");
    }

//    @RequestMapping("/zc")
//    public String zhuce() {
//
//    }
}

