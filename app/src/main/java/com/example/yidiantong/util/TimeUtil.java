package com.example.yidiantong.util;

public class TimeUtil {
    // 输入一个int为秒，输出一个字符串，用‘表示分，’‘表示秒
    public static String getRecordTime(int time) {
        if(time < 60){
            return time + "”";
        }else if(time % 60 == 0){
            return time / 60 + "’";
        }else{
            return time / 60 + "’ " + time % 60 + "”";
        }
    }
    public static String getRecordTimeCharacter(int time) {
        if(time < 60){
            return time + "秒";
        }else if(time % 60 == 0){
            return time / 60 + "分";
        }else{
            return time / 60 + "分 " + time % 60 + "秒";
        }
    }
}
