package com.example.yidiantong.util;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtils {

    //将服务器返回的String类型转化为JsonObject类型
    //将服务器返回的String类型转化为JsonObject类型

    public static JSONObject getJsonObjectFromString(String str) throws JSONException {
        // 使用正则表达式提取括号中的内容
        Pattern pattern = Pattern.compile("ha\\((.*)\\)");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            str = matcher.group(1); // 提取括号中的内容
            return new JSONObject(str);
        }
        //去除转义字符
        str = StringEscapeUtils.unescapeJava(str);
        //去除前后双引号
        str = str.substring(1, str.length() - 1);
        return new JSONObject(str);
    }

    public static String clearString(String str) {
        //去除转义字符
        return StringEscapeUtils.unescapeJava(str);
    }

}
