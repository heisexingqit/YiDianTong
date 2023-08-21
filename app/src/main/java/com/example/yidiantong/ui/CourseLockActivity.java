package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CourseLockActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageView fiv_ls_ma;
    private TextView ftv_ls_user;
    private String ip;
    private Bitmap imgBitmap = null;
    private ImageView fiv_cls_look;
    private String stunum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_look_screen);

        ip = getIntent().getStringExtra("ip");
        String stuname = getIntent().getStringExtra("stuname");
        stunum = getIntent().getStringExtra("stunum");
        ftv_ls_user = findViewById(R.id.ftv_ls_user);
        ftv_ls_user.setText(stuname + "(" + stunum + ")");
        fiv_cls_look = findViewById(R.id.fiv_cls_look);
        fiv_ls_ma = findViewById(R.id.fiv_ls_ma);
        fiv_ls_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadItems_Net();
            }
        });
        changeImage();
    }

    private void changeImage() {
        String action = getIntent().getStringExtra("action");
        // 听讲状态
        if(action.equals("lock")){
            fiv_cls_look.setImageResource(R.drawable.qing1);
        }// 准备抢答
        else if(action.equals("readyResponder")){
            fiv_cls_look.setImageResource(R.drawable.readyqiangda);
        }// 抢答
        else if(action.equals("startResponder")){
            fiv_cls_look.setImageResource(R.drawable.qiangdabutton);
            fiv_cls_look.setEnabled(true);
            fiv_cls_look.setOnClickListener(this);
            fiv_cls_look.setOnTouchListener(this);
        }// 停止抢答
        else if(action.equals("stopQiangDa")){
            fiv_cls_look.setImageResource(R.drawable.qiangdagray);
            fiv_cls_look.setEnabled(false);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                requestWebPhotoBitmap((String) message.obj);
            }else if(message.what == 101){
                if((int)message.obj == 1){
                    Log.e("成功","");
                }
            }
        }
    };
    // 二维码图片
    private void requestWebPhotoBitmap(String imageurl) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL bitmapUrl = new URL(imageurl);
                connection = (HttpURLConnection) bitmapUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // 判断是否请求成功
                if (connection.getResponseCode() == 200) {
                    Message hintMessage = new Message();
                    hintMessage.what = 100;
                    hintHandler.sendMessage(hintMessage);

                    InputStream inputStream = connection.getInputStream();
                    imgBitmap = BitmapFactory.decodeStream(inputStream);

                    Message message = showHandler.obtainMessage();
                    showHandler.sendMessage(message);
                } else {
                    Message hintMessage = new Message();
                    hintMessage.what = 101;
                    hintHandler.sendMessage(hintMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private final Handler hintHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 100)
                Toast.makeText(CourseLockActivity.this, "获取图片中，请稍等", Toast.LENGTH_SHORT).show();
            else if(msg.what == 101)
                Toast.makeText(CourseLockActivity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
        }
    };

    private ImageView fiv_erweima;
    @SuppressLint("HandlerLeak")
    private final Handler showHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 弹窗
            View inflater = LayoutInflater.from(CourseLockActivity.this).inflate(R.layout.course_stu_erweima, null);
            PopupWindow popupWindow = new PopupWindow(inflater,
                    ViewGroup.LayoutParams.WRAP_CONTENT ,
                    ViewGroup.LayoutParams.WRAP_CONTENT,false);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f; //0.0-1.0
            getWindow().setAttributes(lp);
            fiv_erweima = inflater.findViewById(R.id.fiv_erweima);
            fiv_erweima.setImageBitmap(imgBitmap); //填充控件
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(fiv_cls_look, Gravity.CENTER,0,0);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f; //0.0-1.0
                    getWindow().setAttributes(lp);
                }
            });
        }
    };

    private void loadItems_Net() {
        String mRequestUrl = ip + Constant.GET_QRCODE_URL ;
        Log.e("mReq_stu",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String imageurl = json.getString("url");
                Log.e("imageurl", ""+imageurl);

                Message message = Message.obtain();
                message.obj = imageurl;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fiv_cls_look:
                qiangda();
                break;
        }
    }

    private void qiangda() {
        String mRequestUrl = ip + Constant.RESPONDER_FROM_APP + "?userName=" + stunum ;
        Log.e("mReq_stu",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                Message msg = Message.obtain();
                if (isSuccess) {
                    msg.obj = 1;
                } else {
                    msg.obj = 0;
                }
                msg.what = 101;
                handler.sendMessage(msg);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.fiv_cls_look){
            if(event.getAction() == MotionEvent.ACTION_UP){
                fiv_cls_look.setImageResource(R.drawable.qiangdabutton);
            }
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                fiv_cls_look.setImageResource(R.drawable.qiangdagray);
            }
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        changeImage();
    }

    public void onBackPressed(){
        Intent intent = new Intent(CourseLockActivity.this, CourseLookActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}