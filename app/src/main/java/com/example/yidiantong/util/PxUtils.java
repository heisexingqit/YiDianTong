package com.example.yidiantong.util;

import android.content.Context;
import android.util.Log;

public class PxUtils {
    public static int dip2px(Context context, float dpValue) {
        // 获取当前手机的像素密度（1个dp对应几个px）
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }
    public static int px2dip(Context context, float pxValue) {

        float density = context.getResources().getDisplayMetrics().density; // 获取设备的屏幕密度
        return (int) (pxValue / density); // 四舍五入取整
    }
}
