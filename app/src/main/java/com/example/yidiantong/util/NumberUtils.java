package com.example.yidiantong.util;

import org.json.JSONException;
import org.json.JSONObject;

public class NumberUtils {

    public static String getFormatNumString(String str) {
        if (str.endsWith(".0")) {
            str = str.substring(0, str.length() - 2);
        }
        return str;
    }
}
