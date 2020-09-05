package top.gardel.translate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import top.gardel.translate.tokens.GoogleTokenGenerator;
import top.gardel.translate.tokens.TokenProvider;

public class GoogleTranslate {
    protected ObjectMapper objectMapper;
    protected RestTemplate client;
    protected String source;
    protected String target;
    protected String lastDetectedSource;
    protected String url = "https://translate.google.cn/translate_a/single";
    protected String urlQuery = "client=webapp&hl=en&dt=t&dt=bd&dt=at&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&sl=%s&tl=%s&q=%s&ie=UTF-8&oe=UTF-8&multires=1&otf=0&pc=1&trs=1&ssel=0&tsel=0&kc=1&tk=%s";
    protected TokenProvider tokenProvider;

    public GoogleTranslate(String target, String source, TokenProvider tokenProvider, ObjectMapper objectMapper, RestTemplate restTemplate) {
        Objects.requireNonNull(target, "target == null");
        this.objectMapper = objectMapper;
        this.client = restTemplate;
        this.tokenProvider = tokenProvider == null ? new GoogleTokenGenerator() : tokenProvider;
        setSource(source);
        this.target = target;
    }

    public GoogleTranslate setTarget(String target) {
        this.target = target;
        return this;
    }

    public GoogleTranslate setSource(String source) {
        this.source = source == null || source.isEmpty() ? "auto" : source;
        return this;
    }

    public GoogleTranslate setUrl(String url) {
        this.url = url;
        return this;
    }

    public GoogleTranslate setTokenProvider(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider == null ? new GoogleTokenGenerator() : tokenProvider;
        return this;
    }

    public String getLastDetectedSource() {
        return lastDetectedSource;
    }

    public String translate(String str) {
        Objects.requireNonNull(str, "str == null");
        if (Objects.equals(source, target)) return str;
        try {
            String response = getResponse(str);
            if (!response.startsWith("[")) return response.trim();
            response = response.replaceAll(",+", ",").replaceAll("\\[,", "[");
            ArrayNode responseArray = objectMapper.readValue(response, ArrayNode.class);
            if (responseArray.size() == 0 ||
                responseArray.get(0).isNull()) return "";
            JsonNode firstElement = responseArray.get(0);
            if (firstElement.isArray() && firstElement.size() == 0) return "";
            ArrayList<JsonNode> detectedLanguages = new ArrayList<>();
            responseArray.forEach(item -> {
                if (item.isValueNode()) detectedLanguages.add(item);
            });
            if (responseArray.size() - 2 >= 0) {
                JsonNode element = responseArray.get(responseArray.size() - 2);
                if (element.isArray() && element.size() > 0) {
                    JsonNode element1 = element.get(0);
                    if (element1.isArray() && element1.size() > 0) {
                        detectedLanguages.add(element1.get(0));
                    }
                }
            }
            lastDetectedSource = null;
            detectedLanguages.stream()
                .filter(JsonNode::isValueNode)
                .map(JsonNode::asText)
                .filter(this::isValidLocale)
                .findFirst()
                .ifPresent(s -> lastDetectedSource = s);
            if (firstElement.isValueNode()) return firstElement.asText().trim();
            StringBuilder sb = new StringBuilder();
            firstElement.forEach(item -> {
                JsonNode element = item.get(0);
                if (element.isValueNode()) sb.append(element.asText()).append(" ");
            });
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return str.trim();
        }
    }

    public String getResponse(String str) {
        String url;
        try {
            url = this.url + "?" + String.format(urlQuery, source, target, URLEncoder.encode(str, "UTF-8"), tokenProvider.generateToken(source, target, str));
        } catch (UnsupportedEncodingException ignored) {
            url = this.url + "?" + String.format(urlQuery, source, target, str, tokenProvider.generateToken(source, target, str));
        }

        try {
            String content = client.getForObject(url, String.class);
            if (content == null) return "";
            return content;
        } catch (RestClientException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected boolean isValidLocale(String lang) {
        return lang.matches("^([a-z]{2,3})(-[A-Z]{2,3})?$");
    }
}
