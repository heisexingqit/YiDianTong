package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.TBellNoticeEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NoticeLookActivity extends AppCompatActivity {
    private static final String TAG = "NoticeLookActivity";
    private String type;
    private TextView ftv_title;
    private TextView ftv_nl_title;
    private TextView ftv_nl_user;
    private TextView ftv_nl_time;
    private TextView ftv_nl_content;
    private String noticetime;
    private String noticeAuthor;
    private String noticeTitle;
    private String noticecotent;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_look);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        type = getIntent().getStringExtra("noticetype");
        ftv_title = findViewById(R.id.ftv_title);
        switch (type){
            case "通知":
                ftv_title.setText("通知");
                break;
            case "公告":
                ftv_title.setText("公告");
                break;
        }

        ftv_nl_title = findViewById(R.id.ftv_nl_title);
        ftv_nl_user = findViewById(R.id.ftv_nl_user);
        ftv_nl_time = findViewById(R.id.ftv_nl_time);
        ftv_nl_content = findViewById(R.id.ftv_nl_content);

        // 从上一个页面获取
        noticetime = getIntent().getStringExtra("noticetime");
        ftv_nl_time.setText(noticetime);
        noticeAuthor = getIntent().getStringExtra("noticeAuthor");
        noticeTitle = getIntent().getStringExtra("noticeTitle");
        noticecotent = getIntent().getStringExtra("noticecotent");

        // 设置对应文本
        ftv_nl_title.setText(noticeTitle);
        ftv_nl_user.setText(noticeAuthor);
        ftv_nl_content.setText(noticecotent);

    }
}