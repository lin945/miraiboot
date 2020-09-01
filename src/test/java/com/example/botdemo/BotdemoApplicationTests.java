package com.example.botdemo;

import com.example.botdemo.dao.DataBaseDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BotdemoApplicationTests {
    @Autowired
    DataBaseDao dataBaseDao;
    @Test
    void contextLoads() {
        System.out.println(dataBaseDao.getUserMap("ffffe03f"));
    }

}
