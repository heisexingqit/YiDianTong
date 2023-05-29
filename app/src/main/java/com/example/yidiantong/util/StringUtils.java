package com.example.yidiantong.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

public class StringUtils {

    // 字符串局部变色，包含start不包含end
    public static SpannableString getStringWithColor(String str, String color, int start, int end) {
        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(color)), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // 判断字符串中是否有空串
    public static boolean hasEmptyString(String... strings) {
        for (String str : strings) {
            if (str.length() == 0) {
                return true;
            }
        }
        return false;
    }
}
