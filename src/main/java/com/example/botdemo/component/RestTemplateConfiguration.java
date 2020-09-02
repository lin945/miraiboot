package com.example.botdemo.component;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author lin945
 * @date 2020/9/1 17:48
 */
@Configuration
public class RestTemplateConfiguration {
    //http请求
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
