package com.example.yidiantong.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageUtils {

    public static Bitmap drawableToBitmap(Drawable drawable) {// drawable 转换成bitmap
        int width = drawable.getIntrinsicWidth();// 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;// 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);// 建立对应bitmap
        Canvas canvas = new Canvas(bitmap);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);// 把drawable内容画到画布中
        return bitmap;
    }

    public static String Bitmap2StrByBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int q = 100;
        int maxSize = 120;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > maxSize) {
            baos.reset(); // 重置baos即清空baos
            q -= 5;
            image.compress(Bitmap.CompressFormat.JPEG, q, baos);
        }
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static Bitmap compressBitmap(Bitmap bitmap, float scale) {
        // 获取原始图片的宽度和高度
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        // 创建Matrix对象，并设置缩放比例
        final Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // 使用Matrix对象对原始图片进行缩放
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        // 回收原始图片
        bitmap.recycle();

        return resizedBitmap;
    }


}
