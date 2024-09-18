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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ShowStuAnsAdapter;
import com.example.yidiantong.bean.HomeworkAnswerBatchEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeworkSubmitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HomeworkSubmitActivity";

    //参数
    private String[] stuAnswer;//答题内容
    private String username;
    private String learnPlanId;//作业Id
    private String[] questionIds;//问题Id数组
    private Boolean isNew;
    private String[] questionTypes;
    private RelativeLayout rl_submitting;
    private RelativeLayout rl_loading;
    private boolean isSubmitting;
    // 20240906 批量提交数据
    private ArrayList<HomeworkAnswerBatchEntity> stuAnswerBatchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_submit);
        ((MyApplication) getApplication()).checkAndHandleGlobalVariables(this);
        String title = getIntent().getStringExtra("title");
        stuAnswer = getIntent().getStringArrayExtra("stuAnswer");
        username = getIntent().getStringExtra("username");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        questionIds = getIntent().getStringArrayExtra("questionIds");
        isNew = getIntent().getBooleanExtra("isNew", true);
        questionTypes = getIntent().getStringArrayExtra("questionTypes");
        stuAnswerBatchList = (ArrayList<HomeworkAnswerBatchEntity>) getIntent().getSerializableExtra("stuAnswerBatchList");


        //标题设置
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        //ListView设置
        ListView lv_show_stuAns = findViewById(R.id.lv_show_stuAns);
        ShowStuAnsAdapter adapter = new ShowStuAnsAdapter(this, stuAnswer);
        lv_show_stuAns.setAdapter(adapter);

        lv_show_stuAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                intent.putExtra("currentItem", i);
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
                if (!isSubmitting) {
                    submit();
                }
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
                JSONObject obj = (JSONObject) message.obj;
                Log.e("wen0907", "handleMessage: " + obj);
                boolean success;
                String msg;
                try {
                    success = obj.getBoolean("success");
                    msg = obj.getString("message");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (!success) {
                    Toast.makeText(HomeworkSubmitActivity.this, msg, Toast.LENGTH_SHORT).show();
                    isSubmitting = false;
                    return;
                }

                Toast.makeText(HomeworkSubmitActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        isSubmitting = true;
//        java.util.Date day = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String date = sdf.format(day);

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
                    submit_request();
                }
            });
            builder.setNegativeButton("取消", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
        } else {
            submit_request();
        }
    }

    private void submit_request() {
//        String mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER_FINAL + "?answerTime=" + date + "&paperId=" + learnPlanId + "&userName=" + username +
//                "&status=" + (isNew ? 1 : 3) + "&noAnswerQueId=" + submitZero;
//
//        StringRequest request = new StringRequest(mRequestUrl, response -> {
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//                //结果信息
//                Boolean isSuccess = json.getBoolean("success");
//                Message msg = Message.obtain();
//                if (isSuccess) {
//                    msg.obj = 1;
//                } else {
//                    msg.obj = 0;
//                }
//                msg.what = 100;
//                handler.sendMessage(msg);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//            Log.e("volley", "Volley_Error: " + error.toString());
//
//        });

        // 20240906批量请求方式
        String mRequestUrl = Constant.API + Constant.HOMEWORK_SUBMIT_BATCH;

        // 修复提交BatchJson中的时间为空的情况，改为当前时间
        for (HomeworkAnswerBatchEntity entity : stuAnswerBatchList) {
            if (entity.getAnswerTime() == null || entity.getAnswerTime().length() == 0) {
                entity.setAnswerTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }
        }
        // 使用 Gson 将 stuAnswerBatchList 转换为 JSON 字符串
        Gson gson = new Gson();
        String answerJsonString = gson.toJson(stuAnswerBatchList);
        Map<String, String> params = new HashMap<>();

        // 定义要传输的参数
        try {
            params.put("answerJson", URLEncoder.encode(answerJsonString, "UTF-8"));
            params.put("paperId", learnPlanId);
            params.put("userName", username);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            JSONObject json = null;
            try {
                json = JsonUtils.getJsonObjectFromString(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 处理服务器返回的 JSON 响应
            Message msg = Message.obtain();
            msg.obj = json;
            msg.what = 100;
            handler.sendMessage(msg);

        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };


        MyApplication.addRequest(request, TAG);
        rl_submitting.setVisibility(View.VISIBLE);
    }
}