package com.example.yidiantong.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogUtils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public static void writeLogToFile(String fileName, String logContent, boolean isLog, Context mContext) {
        // 获取外部存储目录，这里假设你的应用有WRITE_EXTERNAL_STORAGE权限
        File externalStorageDir;

        if (Build.VERSION.SDK_INT > 29) {
            externalStorageDir = mContext.getExternalFilesDir(null);
        } else {
            externalStorageDir = Environment.getExternalStorageDirectory();
        }

        File logFile = new File(externalStorageDir, fileName);

        if(!isLog){
            // 判断文件是否存在，存在则删除
            if (logFile.exists()) {
                logFile.delete();
            }
        }

        try {
            // 创建文件输出流
            FileWriter writer = new FileWriter(logFile, true);

            // 日志模式
            if (isLog) {
                String currentTime = dateFormat.format(new Date());
                // 写入日志内容（带时间戳）
                writer.write(currentTime + " - " + logContent + "\n");
            }
            // 长字符串输出模式
            else {
                // 写入日志内容（带时间戳）
                writer.write(logContent);
            }

            // 关闭文件输出流
            writer.close();

            // 打印日志，标识文件写入成功
            Log.e("LOG", "Log written to file: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e("LOG", "Log written to file: " + e);
        }
    }
}
