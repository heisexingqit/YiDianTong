package com.example.yidiantong.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class ImageUtils {
    private static final String TAG = "ImageUtils";

    public static String fileToBase64(File file) throws IOException {
        byte[] fileContent = new byte[(int) file.length()];
        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(fileContent);
        fileInputStream.close();
        String base64Encoder = Base64.encodeToString(fileContent, Base64.DEFAULT);

        return base64Encoder;
    }

    public static String Bitmap2StrByBase64(Context mContext, File image) {
        try {
            Log.e("wen0304", "Bitmap2StrByBase64: " + image.getAbsolutePath());

            File compressedImage = new Compressor(mContext)
                    .setQuality(80)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(image);

            // 将压缩后的图片文件转换为Base64编码
            return fileToBase64(compressedImage);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("wen0221", "Bitmap2StrByBase64: " + e);
            return null;
        }
    }
}
