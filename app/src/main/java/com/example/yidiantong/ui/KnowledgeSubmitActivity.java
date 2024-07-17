package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ShowKnowAnsAdapter;
import com.example.yidiantong.adapter.ShowStuAnsAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class KnowledgeSubmitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "KnowledgeSubmitActivity";

    //参数
    private String[] stuAnswer;//答题内容
    private String userName;
    private String subjectId;  //学科ID
    private String course_name;
    private String zhishidian;
    private String zhishidianId;
    private String xueduan;
    private String banben;
    private String jiaocai;
    private String unitId;
    private String message;

    private String learnPlanId;//作业Id
    private String[] questionIds;//问题Id数组
    private Boolean isNew;
    private String[] questionTypes;
    private RelativeLayout rl_submitting;
    private RelativeLayout rl_loading;
    private boolean isSubmitting;
    List<BookExerciseEntity> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge_submit);

        itemList = (List<BookExerciseEntity>) getIntent().getSerializableExtra("itemList");
        userName = getIntent().getStringExtra("userName");
        subjectId = getIntent().getStringExtra("subjectId");
        course_name = getIntent().getStringExtra("courseName");
        zhishidian = getIntent().getStringExtra("zhishidian");
        zhishidianId = getIntent().getStringExtra("zhishidianId");
        xueduan = getIntent().getStringExtra("xueduanId");
        banben = getIntent().getStringExtra("banbenId");
        jiaocai = getIntent().getStringExtra("jiaocaiId");
        unitId = getIntent().getStringExtra("unitId");
        message = getIntent().getStringExtra("message");

        //标题设置
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("自主学习作答情况");

        //ListView设置
        ListView lv_show_stuAns = findViewById(R.id.lv_show_stuAns);
        //将itemList中的stuInput转换为String数组
        stuAnswer = new String[itemList.size()];
        int[] type = new int[itemList.size()];
        Arrays.fill(type, 0);
        for (int i = 0; i < itemList.size(); i++) {
            stuAnswer[i] = itemList.get(i).getStuInput();
            if (itemList.get(i).getAccType() != 0) {
                type[i] = itemList.get(i).getAccType();
            }
        }
        ShowKnowAnsAdapter adapter = new ShowKnowAnsAdapter(this, stuAnswer, type);
        lv_show_stuAns.setAdapter(adapter);

        lv_show_stuAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("currentItem", i + 1);
                System.out.println("currentItem: " + i + 1);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        // 设置分割线
        Drawable divider = ContextCompat.getDrawable(this, R.drawable.divider_deep);
        lv_show_stuAns.setDivider(divider);

        //返回按钮
        findViewById(R.id.iv_back).setOnClickListener(this);

        //提交按钮
        findViewById(R.id.btn_submit).setOnClickListener(this);

        //遮蔽
        rl_submitting = findViewById(R.id.rl_submitting);
        TextView tv_submitting = findViewById(R.id.tv_submitting);
        tv_submitting.setText("作业提交中...");
        rl_loading = findViewById(R.id.rl_loading);
        rl_loading.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_submit:
                Intent intent = new Intent(this, KnowledgeStudyChangeActivity.class);
                System.out.println("KnowledgeSubmitActivity: " + userName);
                intent.putExtra("userName", userName);
                intent.putExtra("subjectId", subjectId);
                intent.putExtra("courseName", course_name);
                intent.putExtra("zhishidian", zhishidian);
                intent.putExtra("zhishidianId", zhishidianId);
                intent.putExtra("xueduanId", xueduan);
                intent.putExtra("banbenId", banben);
                intent.putExtra("jiaocaiId", jiaocai);
                intent.putExtra("unitId", unitId);
                intent.putExtra("message", message);
                startActivity(intent);
                finish();
        }
    }

    private String submitZero = "";
    private Boolean isF = true;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                int f = (int) message.obj;
                if (f == 0) {
                    Toast.makeText(KnowledgeSubmitActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                intent.putExtra("currentItem", -1);
                setResult(Activity.RESULT_OK, intent);
                rl_submitting.setVisibility(View.GONE);
                finish();
            }
        }
    };

    //提交代码
    private void submit() {
        isSubmitting=true;
        Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sdf.format(day);

        for (int i = 0; i < stuAnswer.length; ++i) {
            if (stuAnswer[i].length() == 0) {
                if (isF) {
                    isF = false;
                } else {
                    submitZero += ",";
                }
                submitZero += questionIds[i];
                continue;
            }

        }
        if (submitZero.length() == 0) {
            submitZero = "-1";
        }

        if (!submitZero.equals("-1")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("有题目未作答,是否提交?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    submit_request(date);
                }
            });
            builder.setNegativeButton("取消", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
        }else{
            submit_request(date);
        }
    }

    private void submit_request(String date) {

        rl_submitting.setVisibility(View.VISIBLE);
    }
}