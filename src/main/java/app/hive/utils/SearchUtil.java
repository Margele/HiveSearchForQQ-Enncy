package app.hive.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import java.io.IOException;

/**
 * Copyright (c) 2022. Jason Wang (wxw126mail@126.com)
 * Title: SearchUtil
 * Description: 插件工具类
 *
 * @author: 王晓文
 * @date: 2022/7/26 22:53
 */
public class SearchUtil implements Constant{
    public static String searchReason(String askQuestion, String token) {
        String url = String.format("https://tk.enncy.cn/query?token=%s&title=%s&more=true", token, askQuestion);
        String body;
        StringBuilder result = new StringBuilder();
        try {
            body = Jsoup.connect(url).ignoreContentType(true).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return "请求出错，请联系作者！";
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = mapper.readTree(body);
        } catch (JsonProcessingException e) {
            return "请求出错，请联系作者！";
        }

        if (jsonNode.get("code").asInt() != 1) {
            return "请求出错，请联系作者！";
        }

        JsonNode data = jsonNode.get("data");
        JsonNode results = data.get("results");

        if (results.size() == 0) {
            return null;
        }

        int resultCount = 0;

        for (JsonNode node : results) {
            String question = node.get("question").asText();
            String reason = node.get("answer").asText();
            String resultSingle = String.format(SINGLE_RESULT, question, reason);
            result.append(resultSingle);
            resultCount++;
            if (resultCount == 3) break;
        }

        return String.format("共匹配到%d组答案\n", resultCount)
                .concat(result.toString())
                .concat("Info: 支持模糊搜索，题目复制越完整越准确");
    }
}
