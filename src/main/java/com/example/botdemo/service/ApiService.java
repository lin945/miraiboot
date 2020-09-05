package com.example.botdemo.service;

import com.example.botdemo.entity.ResponsePage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import top.gardel.howdoi.Answer;
import top.gardel.howdoi.HowDoI;
import top.gardel.motd.Motd;
import top.gardel.translate.GoogleTranslate;

/**
 * @author lin945
 * @date 2020/9/1 19:20
 */
@Service
public class ApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GoogleTranslate googleTranslate;

    public ApiService(RestTemplate restTemplate,
                      ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        googleTranslate = new GoogleTranslate("en", null, null, objectMapper, restTemplate);
    }

    /**
     * 搜图
     * @param word 关键词
     * @return resp
     */
    public ResponsePage searchImage(String word) {
        List<Integer> list = new ArrayList<Integer>();
        String pixivApiUrl = "https://api.imjad.cn/pixiv/v2/?type=search&word=";
        String body = restTemplate.getForEntity(pixivApiUrl +word, String.class).getBody();
        if(body != null && body.length() != 0){
            try {
                JsonNode jsonNode = objectMapper.readTree(body).get("illusts");
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
        String hitokoto = "https://v1.hitokoto.cn/";
        String body = restTemplate.getForEntity(hitokoto, String.class).getBody();
        try {
            JsonNode jsonNode = objectMapper.readTree(body);
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
        String api = "https://api.imjad.cn/qrcode/?text={text}&encode=json";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("text", text);
        String body = restTemplate.getForEntity(api , String.class,paramMap).getBody();
        if(body != null && body.length() != 0){
            try {
                JsonNode jsonNode = objectMapper.readTree(body);
                return jsonNode.get("url").textValue();
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取 stackoverflow 的问题
     *
     * @param question 问题（英文）
     * @param perQuestion 每个问题的回答数
     * @param numOfResult 问题的数量
     * @return 回答
     */
    public List<Answer> howDoI(@NonNull String question, int perQuestion, int numOfResult) {
        String searchUrl = "https://stackoverflow.com/search?tab=relevance&q={0}";
        String content;
        try {
            content = restTemplate.getForObject(searchUrl, String.class, question + " answers:5");
            if (content == null) return Collections.emptyList();
        } catch (RestClientException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        Document document = Jsoup.parse(content);
        List<String> links = HowDoI.getAnswerLinks(searchUrl.substring(0, searchUrl.lastIndexOf("/")), document, numOfResult);
        List<Answer> total = new ArrayList<>();
        for (String link : links) {
            List<Answer> answerList = HowDoI.getAnswer(restTemplate, link, perQuestion);
            total.addAll(answerList);
        }
        return total;
    }

    /**
     * 谷歌翻译
     *
     * @param str 文本
     * @param target 目标语言
     * @param source 源语言（null 或 "auto" 则自动检测）
     * @return 翻译后的文本
     */
    public String trans(@NonNull String str, @NonNull String target, @Nullable String source) {
        return googleTranslate.setTarget(target)
            .setSource(source)
            .translate(str);
    }

    public String trans(@NonNull String str, @NonNull String target) {
        return trans(str, target, null);
    }

    /**
     * 获取名言
     *
     * @param from 出处
     * @return Motd
     */
    public Motd getMotd(@Nullable String from) {
        if (from == null) from = "";
        try {
            return restTemplate.getForObject("https://gardel.top/api/mium-motd.php?output=json_no_color&from={0}", Motd.class, from);
        } catch (RestClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加名言
     *
     * @param text 文本
     * @param from 出处
     * @return 是否成功
     */
    public boolean addMotd(@NonNull String text, @NonNull String from) {
        try {
            @SuppressWarnings("unchecked") Map<String, ?> response = restTemplate.getForObject("https://gardel.top/api/mium-motd.php?action=add&text={0}&from={1}", Map.class, text, from);
            if (response != null && response.get("status") != null && (response.get("status").equals(0) || response.get("status").equals("0"))) return true;
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
