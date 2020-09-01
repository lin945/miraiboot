package com.example.botdemo;



import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.example.botdemo.dao")
@SpringBootApplication
public class BotdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotdemoApplication.class, args);
    }

}
