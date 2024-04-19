package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.yidiantong.R;
import com.example.yidiantong.View.CustomDraw;
import com.xinlan.imageeditlibrary.editimage.JsonUtils;
import com.xinlan.imageeditlibrary.editimage.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class THomeworkImageMark extends AppCompatActivity {
    private CustomDraw customDraw;
    public String filePath;// 需要编辑图片路径
    private String newUrl;
    private ProgressDialog progressDialog;
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";

    public static final String IMAGE_IS_EDIT = "image_is_edit";
    private RelativeLayout rl_submitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_image_mark);
        customDraw = findViewById(R.id.CustomDraw);
        rl_submitting = findViewById(R.id.rl_submitting);
        //Bitmap backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.preview);
       // customDraw.setBackgroundBitmap(backgroundBitmap);
        // 获取传递过来的图片 URL 地址
        Intent intent = getIntent();
        if (intent != null) {
            filePath = intent.getStringExtra("imageUrl");
        }
        // 加载网络图片作为背景
        if (filePath != null && !filePath.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(filePath)
                    .skipMemoryCache(true) // 禁用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁盘缓存
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            customDraw.setBackgroundBitmap(resource);

                        }
                    });
        }



        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        ImageButton zoomInButton = findViewById(R.id.zoomInButton);
        ImageButton zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageButton btnUndo = findViewById(R.id.btnUndo);
        ImageButton btnClear = findViewById(R.id.btnClear);
        ImageButton btnRotate =findViewById(R.id.btnRotate);
        Button image_cancel = findViewById(R.id.image_cancel);
        Button image_save = findViewById(R.id.image_save);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.toggleDrawingButton) {
                    customDraw.setDrawingEnabled(true);
                    customDraw.setMoveEnabled(false);
                    // 如果选择了 Toggle Drawing 按钮
                    // 在这里执行相应的操作
                } else if (checkedId == R.id.moveButton) {
                    customDraw.setDrawingEnabled(false);
                    customDraw.setMoveEnabled(true);
                    // 如果选择了 Move 按钮
                    // 在这里执行相应的操作
                }
            }
        });
        //放大
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDraw.zoomIn();
            }
        });
        //缩小
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDraw.zoomOut();
            }
        });

        //撤销
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDraw.undo();
            }
        });
        //清屏
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDraw.clear();
            }
        });
        //旋转图片
        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDraw.rotateBackground();
            }
        });
        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveImageTask().execute(); // 启动异步任务
            }
        });

    }
    private class SaveImageTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Bitmap drawnBitmap = customDraw.getDrawnBitmap(); // 获取绘制后的图片
            String base64 = Bitmap2StrByBase64(drawnBitmap);

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            OutputStream outputStream = null;
            String newUrl = null;

            try {
                // 创建一个URL对象，替换为您要请求的URL
                URL url = new URL("http://www.cn901.net:8111/AppServer/ajax/userManage_saveCanvasImageFromRn.do");
                // 打开连接
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
               // Log.d("HSK", "filePath:" + filePath);
                // 构建请求参数
                String params_json = "type=save&imagePath=" + URLEncoder.encode(filePath, "UTF-8") + "&baseData=" + URLEncoder.encode(base64, "UTF-8");
                // 获取输出流，用于发送请求数据
                outputStream = connection.getOutputStream();
                outputStream.write(params_json.getBytes("UTF-8"));
                outputStream.flush();

                // 获取输入流，用于读取响应数据
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                StringBuilder response = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                // 输出响应数据
                String responseData = response.toString();
                JSONObject jsonObject = JsonUtils.getJsonObjectFromString(responseData);
                newUrl = jsonObject.getString("url");
                Log.d("HSK","image");
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                // 关闭连接和输入流
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return newUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("newUrl", result);
                //Log.d("HSK", "返回的newUrl:" + result);
                setResult(RESULT_OK, returnIntent);
            }
            // 显示加载页面
            showSubmittingLayout();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 隐藏加载页面
                    hideSubmittingLayout();
                    // 清空绘制路径列表，以便下次保存
                    finish();
                }
            }, 1000); // 设置延迟时间为1.5秒
        }
    }
    // 显示加载页面
    private void showSubmittingLayout() {
        rl_submitting.setVisibility(View.VISIBLE);
    }

    // 隐藏加载页面
    private void hideSubmittingLayout() {
        rl_submitting.setVisibility(View.GONE);
    }

    public String Bitmap2StrByBase64(Bitmap image) {
        // 获取应用的缓存目录
        File filesDir = this.getFilesDir();

        // 在缓存目录中创建一个临时文件
        File file = new File(filesDir, "marked.jpg");
        Log.e("debug0116", "临时修改文件路径: " + file.getAbsolutePath());
        try {
            FileOutputStream outputStream = new FileOutputStream(file);

            // 将 Bitmap 压缩为 JPEG 格式，并写入文件
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            // 关闭输出流
            outputStream.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        if(file.exists() && file.length() > 0){
            Log.e("debug0116", "OK了: " + file.length());
        }
        return ImageUtils.Bitmap2StrByBase64(this, file);
    }
}