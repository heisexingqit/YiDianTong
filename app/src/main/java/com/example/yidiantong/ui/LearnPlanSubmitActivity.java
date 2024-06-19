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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ShowStuAnsAdapter2;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LearnPlanSubmitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LearnPlanSubmitActivity";

    //参数
    private String[] stuAnswer;//答题内容
    private String username;
    private String learnPlanId;//作业Id
    private List<String> questionIds;//问题Id数组
    private List<Integer> questionIdx;
    private Boolean isNew;
    private RelativeLayout rl_submitting;
    private RelativeLayout rl_loading;
    private String title;
    private boolean isSubmitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_plan_submit);

        title = getIntent().getStringExtra("title");
        stuAnswer = getIntent().getStringArrayExtra("stuAnswer");
        username = getIntent().getStringExtra("username");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        questionIds = (ArrayList<String>) getIntent().getSerializableExtra("questionIds");
        isNew = getIntent().getBooleanExtra("isNew", true);
        questionIdx = (ArrayList<Integer>) getIntent().getSerializableExtra("questionIdx");
        String type = getIntent().getStringExtra("type");
        Button btn_submit = findViewById(R.id.btn_submit);
        isSubmitting = false;
        if (type.equals("learnPlan")) {
            btn_submit.setText("交导学案");
        } else if (type.equals("weike")) {
            btn_submit.setText("交微课");
        }

        //标题设置
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        //ListView设置
        ListView lv_show_stuAns = findViewById(R.id.lv_show_stuAns);
        ShowStuAnsAdapter2 adapter = new ShowStuAnsAdapter2(this, stuAnswer, questionIdx);
        lv_show_stuAns.setAdapter(adapter);

        lv_show_stuAns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                Log.d(TAG, "onItemClick: " + questionIdx.get(i));
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
        tv_submitting.setText("导学案提交中...");
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
                     submitFinal();
                }
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

    private void submitFinal() {
        isSubmitting = true;
        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String date = sdf.format(day);
        String mRequestUrl;
        StringRequest request;
        mRequestUrl = Constant.API + Constant.LEARNPLAN_SUBMIT_FIN_ITEM + "?answerTime=" + date + "&learnPlanId=" + learnPlanId + "&userName=" + username +
                "&status=" + (isNew ? 1 : 3) + "&learnPlanName=" + title + "&userCn=" + MyApplication.cnName;
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
                    Toast.makeText(LearnPlanSubmitActivity.this, "提交失败！", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}