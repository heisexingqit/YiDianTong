package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ShowStuAnsAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeworkSubmitActivity extends AppCompatActivity implements View.OnClickListener {

    //参数
    private String[] stuAnswer;//答题内容
    private String username;
    private String learnPlanId;//作业Id
    private String[] questionIds;//问题Id数组
    private Boolean isNew;
    private String[] questionTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_submit);

        String title = getIntent().getStringExtra("title");
        stuAnswer = getIntent().getStringArrayExtra("stuAnswer");
        username = getIntent().getStringExtra("username");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        questionIds = getIntent().getStringArrayExtra("questionIds");
        isNew = getIntent().getBooleanExtra("isNew", true);
        questionTypes = getIntent().getStringArrayExtra("questionTypes");

        //标题设置
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        //ListView设置
        ListView lv_show_stuAns = findViewById(R.id.lv_show_stuAns);
        ShowStuAnsAdapter adapter = new ShowStuAnsAdapter(this, stuAnswer, questionTypes);
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
        //返回按钮
        findViewById(R.id.iv_back).setOnClickListener(this);

        //提交按钮
        findViewById(R.id.btn_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_submit:
                submit();
        }
    }

    private int submitSum = 0;
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
                    Toast.makeText(HomeworkSubmitActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                } else {
                    submitSum++;
                }
                if (submitSum == stuAnswer.length + 1) {
                    Intent intent = new Intent();
                    intent.putExtra("currentItem", -1);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        }
    };

    //提交代码
    private void submit() {

        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(day);

        RequestQueue queue = Volley.newRequestQueue(this);
        String mRequestUrl;
        StringRequest request;
        for (int i = 0; i < stuAnswer.length; ++i) {
            if (stuAnswer[i].length() == 0) {
                submitSum++;
                if (isF) {
                    isF = false;
                } else {
                    submitZero += ",";
                }
                submitZero += questionIds[i];
                continue;
            }
            mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER + "?learnPlanId=" + learnPlanId +
                    "&stuId=" + username + "&questionId=" + questionIds[i] + "&answer=" + stuAnswer[i] + "&answerTime=" + date;
            request = new StringRequest(mRequestUrl, response -> {
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
                    msg.what = 100;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            });

            queue.add(request);
        }
        if (submitZero.length() == 0) {
            submitZero = "-1";
        }
        mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER_FINAL + "?answerTime=" + date + "&paperId=" + learnPlanId + "&userName=" + username +
                "&status=" + (isNew ? 1 : 3) + "&noAnswerQueId=" + submitZero;

        request = new StringRequest(mRequestUrl, response -> {
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
                msg.what = 100;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });

        queue.add(request);

    }
}