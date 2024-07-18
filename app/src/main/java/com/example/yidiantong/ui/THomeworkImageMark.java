package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.CustomDraw;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    private static final int CHUNK_SIZE = 1024 * 1024 * 2; // 2MB 分块大小
    private RadioButton btn_border1;
    private RadioButton btn_border2;
    private RadioButton btn_border3;
    private RadioButton btn_border4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_image_mark);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);

        customDraw = findViewById(R.id.CustomDraw);
        customDraw.makeSquare();
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


//        ImageButton zoomInButton = findViewById(R.id.zoomInButton);
//        ImageButton zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageButton btnUndo = findViewById(R.id.btnUndo);
        ImageButton btnClear = findViewById(R.id.btnClear);
        ImageButton btnRotate =findViewById(R.id.btnRotate);
        Button image_cancel = findViewById(R.id.image_cancel);
        Button image_save = findViewById(R.id.image_save);
        ImageButton toggleDrawingButton = findViewById(R.id.toggleDrawingButton);
        ImageButton moveButton = findViewById(R.id.moveButton);
        FrameLayout frameLayout = findViewById(R.id.fl_ColorAndStroke);
        ImageButton close_button = findViewById(R.id.close_button);
        RadioGroup radioGroupBorder = findViewById(R.id.radio_group_border);
        RadioGroup radioGroupColor = findViewById(R.id.radio_group_color);
        RadioButton redButton = findViewById(R.id.btn_color_red);
        RadioButton yellowButton = findViewById(R.id.btn_color_yellow);
        RadioButton blueButton = findViewById(R.id.btn_color_blue);
        RadioButton greenButton = findViewById(R.id.btn_color_green);
        RadioButton blackButton = findViewById(R.id.btn_color_black);
        RelativeLayout rl_main = findViewById(R.id.rl_ColorAndStroke);
        LinearLayout ll_ColorAndStroke = findViewById(R.id.ll_ColorAndStroke);
        btn_border1 = findViewById(R.id.btn_border1);
        btn_border2 = findViewById(R.id.btn_border2);
        btn_border3 = findViewById(R.id.btn_border3);
        btn_border4 = findViewById(R.id.btn_border4);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 获取LinearLayout的位置信息
                int[] linearLayoutLocation = new int[2];
                ll_ColorAndStroke.getLocationOnScreen(linearLayoutLocation);

                // 获取LinearLayout的宽度和高度
                int linearLayoutWidth = ll_ColorAndStroke.getWidth();
                int linearLayoutHeight = ll_ColorAndStroke.getHeight();

                // 判断触摸点是否在LinearLayout之外
                if (event.getRawX() < linearLayoutLocation[0] ||
                        event.getRawX() > (linearLayoutLocation[0] + linearLayoutWidth) ||
                        event.getRawY() < linearLayoutLocation[1] ||
                        event.getRawY() > (linearLayoutLocation[1] + linearLayoutHeight)) {
                    // 触摸点在LinearLayout之外，隐藏FrameLayout
                    frameLayout.setVisibility(View.GONE);
                    return true; // 消费这个事件，防止其他触摸事件被触发
                } else {
                    return false; // 不消费事件，让LinearLayout可以处理触摸
                }
            }
        });
        //绘画按钮
        toggleDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customDraw.getDrawingEnabled()) {
                    frameLayout.setVisibility(View.VISIBLE);
                } else {
                    // 如果按钮未选中，则改为选中状态
                    customDraw.setDrawingEnabled(true);
                    toggleDrawingButton.setBackgroundResource(R.drawable.image_edit_pen_on);
                    customDraw.setMoveEnabled(false);
                    moveButton.setBackgroundResource(R.drawable.image_edit_move);
                }

            }
        });
        //移动按钮
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!customDraw.getMoveEnabled()) {
                    //取消绘画
                    customDraw.setDrawingEnabled(false);
                    toggleDrawingButton.setBackgroundResource(R.drawable.image_edit_pen);
                    //开启移动
                    customDraw.setMoveEnabled(true);
                    moveButton.setBackgroundResource(R.drawable.image_edit_move_on);
                    //提示消息
                    Toast toast = Toast.makeText(THomeworkImageMark.this,"您可以通过双指来实现图片的放大与缩小！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

            }
        });



//        //放大
//        zoomInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customDraw.zoomIn();
//            }
//        });
//        //缩小
//        zoomOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customDraw.zoomOut();
//            }
//        });

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
        //取消按钮
        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //保存按钮
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveImageTask().execute(); // 启动异步任务
            }
        });
        //关闭画笔选择框
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.setVisibility(View.GONE);
            }
        });
        //画笔粗细选择
        radioGroupBorder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.btn_border1:
                        customDraw.setPenStrokeWidth(1); // 设置画笔粗细为1
                        btn_border1.setChecked(true);
                        btn_border2.setChecked(false);
                        btn_border3.setChecked(false);
                        btn_border4.setChecked(false);
                        break;
                    case R.id.btn_border2:
                        customDraw.setPenStrokeWidth(3); // 设置画笔粗细为3
                        btn_border1.setChecked(false);
                        btn_border2.setChecked(true);
                        btn_border3.setChecked(false);
                        btn_border4.setChecked(false);
                        break;
                    case R.id.btn_border3:
                        customDraw.setPenStrokeWidth(5); // 设置画笔粗细为5
                        btn_border1.setChecked(false);
                        btn_border2.setChecked(false);
                        btn_border3.setChecked(true);
                        btn_border4.setChecked(false);
                        break;
                    case R.id.btn_border4:
                        customDraw.setPenStrokeWidth(10);
                        btn_border1.setChecked(false);
                        btn_border2.setChecked(false);
                        btn_border3.setChecked(false);
                        btn_border4.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });
        //画笔颜色选择
        radioGroupColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                int color = 0;
                // 设置所有按钮为未选中状态
                switch (checkedId) {
                    case R.id.btn_color_red:
                        redButton.setChecked(true);
                        yellowButton.setChecked(false);
                        blueButton.setChecked(false);
                        greenButton.setChecked(false);
                        blackButton.setChecked(false);
                        color = Color.RED; // 设置画笔颜色为红色
                        break;
                    case R.id.btn_color_yellow:
                        redButton.setChecked(false);
                        yellowButton.setChecked(true);
                        blueButton.setChecked(false);
                        greenButton.setChecked(false);
                        blackButton.setChecked(false);
                        color = Color.YELLOW; // 设置画笔颜色为黄色
                        break;
                    case R.id.btn_color_blue:
                        redButton.setChecked(false);
                        yellowButton.setChecked(false);
                        blueButton.setChecked(true);
                        greenButton.setChecked(false);
                        blackButton.setChecked(false);
                        color = Color.BLUE; // 设置画笔颜色为蓝色
                        break;
                    case R.id.btn_color_green:
                        redButton.setChecked(false);
                        yellowButton.setChecked(false);
                        blueButton.setChecked(false);
                        greenButton.setChecked(true);
                        blackButton.setChecked(false);
                        color = Color.GREEN; // 设置画笔颜色为绿色
                        break;
                    case R.id.btn_color_black:
                        redButton.setChecked(false);
                        yellowButton.setChecked(false);
                        blueButton.setChecked(false);
                        greenButton.setChecked(false);
                        blackButton.setChecked(true);
                        color = Color.BLACK; // 设置画笔颜色为黑色
                        break;
                    default:
                        break;
                }
                // 在这里调用CustomDraw的setPenColor方法来设置画笔的颜色
                customDraw.setPenColor(color);
            }
        });

    }

    private class SaveImageTask extends AsyncTask<Void, Void, String> {
        private  String base64;
        @Override
        protected String doInBackground(Void... voids) {
            Bitmap drawnBitmap = customDraw.getDrawnBitmap(); // 获取绘制后的图片
            base64 = Bitmap2StrByBase64(drawnBitmap);

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            OutputStream outputStream = null;
            String newUrl = null;

            try {
                // 创建一个URL对象，替换为您要请求的URL
                //URL url = new URL("http://www.cn901.net:8111/AppServer/ajax/userManage_saveCanvasImageFromRn.do");
                URL url = new URL(Constant.API+"/AppServer/ajax/teacherApp_saveCanvasImageFromRn.do");
                Log.d("HSK0517","url:"+url);
                // 打开连接
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                // 构建请求参数
                //String params_json = "type=save&imagePath=" + URLEncoder.encode(filePath, "UTF-8") + "&base64=" + URLEncoder.encode(base64, "UTF-8");
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
            } catch (Exception e) {
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
                //returnIntent.putExtra("img_length",base64.length());
                setResult(RESULT_OK, returnIntent);
            }
            // 显示加载页面
            showSubmittingLayout();
            float delay_time = Math.min(((float) base64.length()/20000),5);
            Log.d("hsk0523", "delay_time: "+(long)delay_time*1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 隐藏加载页面
                    hideSubmittingLayout();
                    // 清空绘制路径列表，以便下次保存
                    finish();
                }
            }, (long)delay_time*1000); // 设置延迟时间
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            // 将 Bitmap 压缩为 JPEG 格式，并写入 ByteArrayOutputStream
            image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

            // 将 ByteArrayOutputStream 转换为 byte 数组
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // 将 byte 数组编码为 Base64 字符串
            return Base64.encodeToString(byteArray, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    public String Bitmap2StrByBase64(Bitmap image) {
//        // 获取应用的缓存目录
//        File filesDir = this.getFilesDir();
//
//        // 在缓存目录中创建一个临时文件
//        File file = new File(filesDir, "marked.jpg");
//        Log.e("debug0116", "image.getByteCount(): " + image.getByteCount());
//        try {
//            FileOutputStream outputStream = new FileOutputStream(file);
//
//            // 将 Bitmap 压缩为 JPEG 格式，并写入文件
//            image.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
//            Log.e("debug0116", "image.getByteCount(): " + image.getByteCount());
//            Log.e("debug0116", "临时修改文件路径: " + file.getAbsolutePath());
//            // 关闭输出流
//            outputStream.close();
//        }  catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(file.exists() && file.length() > 0){
//            Log.e("debug0116", "OK了: " + file.length());
//        }
//
//        return ImageUtils.Bitmap2StrByBase64(this, file);
//    }
}