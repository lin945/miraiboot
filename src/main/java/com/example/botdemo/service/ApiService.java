package com.example.botdemo.service;

import com.example.botdemo.entity.ResponsePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lin945
 * @date 2020/9/1 19:20
 */
@Service
public class ApiService {
    @Autowired
    private RestTemplate restTemplate;
    private String pixivApiUrl = "https://api.imjad.cn/pixiv/v2/?type=search&word=";
    private String hitokoto = "https://v1.hitokoto.cn/";
    public ResponsePage searchImage(String word) {
        ObjectMapper mapper = new ObjectMapper();
        String body = restTemplate.getForEntity(pixivApiUrl+word, String.class).getBody();
        if(body != null && body.length() != 0){
            try {
                JsonNode jsonNode = mapper.readTree(body).get("illusts");

            } catch (JsonProcessingException e) {
                return new ResponsePage().failed(e.getMessage());
            }
        }
        return null;
    }

    public Map<String,String> getOneText() {
        ObjectMapper mapper = new ObjectMapper();
        String body = restTemplate.getForEntity(hitokoto, String.class).getBody();
        try {
            JsonNode jsonNode = mapper.readTree(body);
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("hitokoto", jsonNode.get("hitokoto").textValue());
            map.put("from", jsonNode.get("from").textValue());
            return map;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
