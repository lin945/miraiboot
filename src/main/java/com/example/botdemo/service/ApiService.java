package com.example.botdemo.service;

import com.example.botdemo.entity.ResponsePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lin945
 * @date 2020/9/1 19:20
 */
@Service
public class ApiService {
    private final RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 搜图
     * @param word 关键词
     * @return resp
     */
    public ResponsePage searchImage(String word) {
        List<Integer> list = new ArrayList<Integer>();
        ObjectMapper mapper = new ObjectMapper();
        String pixivApiUrl = "https://api.imjad.cn/pixiv/v2/?type=search&word=";
        String body = restTemplate.getForEntity(pixivApiUrl +word, String.class).getBody();
        if(body != null && body.length() != 0){
            try {
                JsonNode jsonNode = mapper.readTree(body).get("illusts");
                for (JsonNode j : jsonNode) {
                    list.add(j.get("id").intValue());
                }
                return new ResponsePage().ok(list);
            } catch (JsonProcessingException e) {
                return new ResponsePage().failed(e.getMessage());
            }
        }
        return new ResponsePage().failed("err");
    }

    /**
     * 获取一言
     * @return map
     */
    public Map<String,String> getOneText() {
        ObjectMapper mapper = new ObjectMapper();
        String hitokoto = "https://v1.hitokoto.cn/";
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

    /**
     * 二维码
     * @param text 关键词
     * @return str
     */
    public String getQrCode(String text) {
        ObjectMapper mapper = new ObjectMapper();
        String api = "https://api.imjad.cn/qrcode/?text={text}&encode=json";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("text", text);
        String body = restTemplate.getForEntity(api , String.class,paramMap).getBody();
        if(body != null && body.length() != 0){
            try {
                JsonNode jsonNode = mapper.readTree(body);
                return jsonNode.get("url").textValue();
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return null;
    }
}
