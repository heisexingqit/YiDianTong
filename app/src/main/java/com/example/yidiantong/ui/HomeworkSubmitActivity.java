package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ShowStuAnsAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                // 报错再用
            }
        }
    };

    //提交代码
    private void submit() {
        submitSum = 0;
        rl_submitting.setVisibility(View.VISIBLE);
        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sdf.format(day);

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
            String jsonString = "";
            try {
                jsonString = URLEncoder.encode(stuAnswer[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER + "?learnPlanId=" + learnPlanId +
                    "&stuId=" + username + "&questionId=" + questionIds[i] + "&answer=" + jsonString + "&answerTime=" + date;
            Log.d(TAG, "submit: " + mRequestUrl);
            request = new StringRequest(mRequestUrl, response -> {
                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);
                    //结果信息
                    Boolean isSuccess = json.getBoolean("success");
                    if (isSuccess) {
                        submitSum++;
                        Log.d(TAG, "submit: 分");
                        if (submitSum == stuAnswer.length) {
                            submitFinal();
                        }
                    } else {
                        Toast.makeText(HomeworkSubmitActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            });

            MyApplication.addRequest(request, TAG);
        }
        if (submitZero.length() == 0) {
            submitZero = "-1";
        }
    }

    private void submitFinal() {
        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sdf.format(day);
        String mRequestUrl;
        StringRequest request;
        mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER_FINAL + "?answerTime=" + date + "&paperId=" + learnPlanId + "&userName=" + username +
                "&status=" + (isNew ? 1 : 3) + "&noAnswerQueId=" + submitZero;
        Log.d(TAG, "submit: " + mRequestUrl);
        request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    Log.d(TAG, "submit: 总");
                    Intent intent = new Intent();
                    intent.putExtra("currentItem", -1);
                    setResult(Activity.RESULT_OK, intent);
                    rl_submitting.setVisibility(View.GONE);
                    finish();
                } else {
                    Toast.makeText(HomeworkSubmitActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }
}