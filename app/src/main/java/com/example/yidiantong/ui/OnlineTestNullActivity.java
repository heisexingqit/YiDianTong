package com.example.yidiantong.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;


public class OnlineTestNullActivity extends AppCompatActivity {
    private static final String TAG = "OnlineTestNullActivity";
    private TextView ftv_title;
    private ImageView fiv_auto_study_null;
    private ImageView fiv_up_null;
    private Button btn_test;

    private String userName;//用户名
    private String unitId;//学校id
    private String xueduan="";//学段
    private String subjectId;  //学科id
    private String banben="";  //版本
    private String jiaocai="";  //教材
    private String courseName;  //课程名称
    private String flag;  //标记

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shiti_null);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        userName = getIntent().getStringExtra("userName");
        unitId = getIntent().getStringExtra("unitId");
        subjectId = getIntent().getStringExtra("xuekeId");
        courseName = getIntent().getStringExtra("courseName");
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("自主学习")) {
            xueduan = getIntent().getStringExtra("xueduanId");
            banben = getIntent().getStringExtra("banbenId");
            jiaocai = getIntent().getStringExtra("jiaocaiId");
        }

        ftv_title = findViewById(R.id.ftv_title);
        if (flag.equals("智能检测")) {
            ftv_title.setText("智能检测");
            fiv_up_null = findViewById(R.id.fiv_up_null);
            fiv_up_null.setVisibility(View.VISIBLE);
            //顶栏返回按钮
            findViewById(R.id.fiv_back).setOnClickListener(v -> {
                finish();
            });
        } else {
            ftv_title.setText("自主学习");
            fiv_auto_study_null = findViewById(R.id.fiv_auto_study_null);
            fiv_auto_study_null.setVisibility(View.VISIBLE);
            //顶栏返回按钮
            findViewById(R.id.fiv_back).setOnClickListener(v -> {
                Intent intent = new Intent(this, AutoStudyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(v -> {
            //跳转到在线测试页面
            Intent intent = new Intent(this, OnlineTestDetailActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("courseName", courseName);
            intent.putExtra("flag", flag);
            intent.putExtra("unitId", unitId);
            intent.putExtra("xueduanId", xueduan);
            intent.putExtra("banbenId", banben);
            intent.putExtra("jiaocaiId", jiaocai);
            startActivity(intent);
        });
    }
}