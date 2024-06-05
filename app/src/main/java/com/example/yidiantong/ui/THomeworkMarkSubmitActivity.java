package com.example.yidiantong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class THomeworkMarkSubmitActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "THomeworkMarkSubmitActivity";

    // 组件和参数
    private FlexboxLayout fb_mark;
    private String stuScore;
    private String scoreCount;
    private List<String> statusList;
    private boolean canMark = true;
    private String mRequestUrl;
    private String taskId;
    private String userName;
    private String teacherName;
    private String stuUserName;
    private String type;
    private List<String> stuScoresList;
    private List<String> questionIdList;
    private List<ClickableTextView> tvNumList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_mark_submit);

        // 布局调整
        findViewById(R.id.iv_eye).setVisibility(View.INVISIBLE);

        // 获取参数
        Intent intent = getIntent();
        stuScore = intent.getStringExtra("stuScore");
        scoreCount = intent.getStringExtra("scoreCount");
        statusList = (List<String>) intent.getSerializableExtra("status");
        canMark = intent.getBooleanExtra("canMark", true);
        taskId = intent.getStringExtra("taskId");
        userName = MyApplication.username;
        teacherName = MyApplication.cnName;
        stuUserName = intent.getStringExtra("stuUserName");
        stuScoresList = (List<String>) intent.getSerializableExtra("stuScoresList");
        Log.e("wen0605", "onCreate: " + stuScoresList);
        questionIdList = (List<String>) intent.getSerializableExtra("questionIdList");
        type = intent.getStringExtra("type");
        String name = intent.getStringExtra("name");

        // 组件获取
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        Button btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        fb_mark = findViewById(R.id.fb_mark);
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(name);

        if (!canMark) {
            btn_submit.setText("结束预览");
        }

        for (int i = 0; i < statusList.size(); ++i) {
            String status = statusList.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_mark_submit, fb_mark, false);
            ClickableTextView tv_num = view.findViewById(R.id.tv_num);
            tvNumList.add(tv_num);
            tv_num.setText(String.valueOf(i + 1));
            tv_num.setTag(i);
            switch (status) {
                case "error":
                    tv_num.setBackgroundResource(R.drawable.error_bg);
                    break;
                case "correct":
                    tv_num.setBackgroundResource(R.drawable.correct_bg);
                    break;
                case "half_correct":
                    tv_num.setBackgroundResource(R.drawable.half_correct_bg);
                    break;
                case "no_answer":
                    tv_num.setBackgroundResource(R.drawable.no_answer_bg);
                    break;
                case "hand_error":
                    tv_num.setBackgroundResource(R.drawable.error_hand_bg);
                    break;
                case "hand_no_answer":
                    tv_num.setBackgroundResource(R.drawable.no_answer_hand_bg);
                    break;
                case "hand_correct":
                    tv_num.setBackgroundResource(R.drawable.correct_hand_bg);
                    break;
                case "hand_half_correct":
                    tv_num.setBackgroundResource(R.drawable.half_correct_hand_bg);
                    break;
                default:
            }

            // 点击跳转回去
            tv_num.setOnClickListener(view1 -> {
                Intent intent2 = new Intent();
                intent2.putExtra("currentItem", (int) view1.getTag());
                setResult(Activity.RESULT_OK, intent2);
                finish();
            });

            fb_mark.addView(view);
        }
        fb_mark.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 在这里获取fb_mark的宽度并设置给tv_num
                for (int i = 0; i < tvNumList.size(); ++i) {
                    ClickableTextView tv_num = tvNumList.get(i);
                    int width = fb_mark.getWidth();
                    ViewGroup.LayoutParams layoutParams = tv_num.getLayoutParams();
                    layoutParams.width = width / 4 - PxUtils.dip2px(THomeworkMarkSubmitActivity.this, 30) - 1;
                    layoutParams.height = width / 4 - PxUtils.dip2px(THomeworkMarkSubmitActivity.this, 30) - 1;
                    tv_num.setLayoutParams(layoutParams);

                    // 不要忘记移除监听器以避免多次调用
                    fb_mark.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });


        TextView tv_stu_scores = findViewById(R.id.tv_stu_scores);
        TextView tv_full_scores = findViewById(R.id.tv_full_scores);
        tv_stu_scores.setText("学生得分: " + stuScore);
        tv_full_scores.setText("满分: " + scoreCount);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if (!canMark) {
                    // 仅关闭
                    Intent intent2 = new Intent();
                    intent2.putExtra("currentItem", -2);
                    setResult(Activity.RESULT_OK, intent2);
                    finish();
                } else {
                    // 存储下标的List
                    List<Integer> indexes = new ArrayList<>();

                    // 查找值为"-1.0"的下标
                    for (int i = 0; i < stuScoresList.size(); i++) {
                        if ("-1.0".equals(String.format("%.1f", stuScoresList.get(i)))) {
                            indexes.add(i + 1);
                        }
                    }

                    // 将下标组合为用逗号隔开的字符串
                    StringBuilder result = new StringBuilder();
                    for (int i = 0; i < indexes.size(); i++) {
                        result.append(indexes.get(i));
                        if (i < indexes.size() - 1) {
                            result.append(",");
                        }
                    }
                    Log.e("wen0605", "stuScoresList: " + stuScoresList);
                    Log.e("wen0605", "indexes: " + indexes);
                    Log.e("wen0605", "result: " + result);
                    if (result.length() == 0) {
                        // 提交批改结果
                        sbumitMarkResult();
                    } else {
                        // 有题目未批改
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("请批改 " + result + " 题");
                        builder.setPositiveButton("确定", null);
                        builder.show();
                    }
                }
                break;
        }
    }

    private void sbumitMarkResult() {

        String jsonStr = "[";
        for (int i = 0; i < questionIdList.size(); ++i) {
            jsonStr += "{\"stuscore\":\"" + String.format("%.1f", stuScoresList.get(i)) + "\",questionID:\"" + questionIdList.get(i) + "\"}";
            if (i != questionIdList.size() - 1) {
                jsonStr += ",";
            }
        }
        jsonStr += "]";
        String encodedJsonStr = null;
        try {
            encodedJsonStr = URLEncoder.encode(jsonStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_MARK_SUBMIT + "?taskId=" + taskId + "&userName=" + userName + "&type=" + type + "&teacherName=" + teacherName + "&stuUserName=" + stuUserName + "&stuScoreCount=" + stuScore + "&scoreCount=" + scoreCount + "&jsonStr=" + encodedJsonStr;

        Log.d("wen", "loadItems_Net: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.d("wen", "sbumitMarkResult: " + json);
                boolean flag = json.getBoolean("success");

                if (flag) {
                    Intent intent2 = new Intent();
                    intent2.putExtra("currentItem", -1);
                    setResult(Activity.RESULT_OK, intent2);
                    finish();
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
}