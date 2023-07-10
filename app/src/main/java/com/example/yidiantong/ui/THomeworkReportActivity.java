package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.THomeItemEntity;
import com.example.yidiantong.bean.THomeworkReportEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.zip.Inflater;

public class THomeworkReportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "THomeworkReportActivity";

    // 传入参数
    private String username;
    private String taskId;
    private String type;

    // 请求相关
    String mRequestUrl;

    // 组件相关
    private TextView tv_avg;
    private TextView tv_max;
    private TextView tv_min;
    private TextView tv_correcting;
    private TextView tv_noCorrecting;
    private TextView tv_noSubmit;
    private ClickableImageView iv_avg;
    private ClickableImageView iv_max;
    private ClickableImageView iv_min;
    private ClickableImageView iv_correcting;
    private ClickableImageView iv_noCorrecting;
    private ClickableImageView iv_noSubmit;

    private int showPos = -1;
    private FlexboxLayout fb_max;
    private FlexboxLayout fb_min;
    private FlexboxLayout fb_correcting;
    private FlexboxLayout fb_noCorrecting;
    private FlexboxLayout fb_noSubmit;



    THomeworkReportEntity homeworkReport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_report);

        // 获取参数
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        taskId = intent.getStringExtra("taskId");
        type = intent.getStringExtra("type");

        // 获取组件
        tv_avg = findViewById(R.id.tv_avg);
        tv_max = findViewById(R.id.tv_max);
        tv_min = findViewById(R.id.tv_min);
        tv_correcting = findViewById(R.id.tv_correcting);
        tv_noCorrecting = findViewById(R.id.tv_noCorrecting);
        tv_noSubmit = findViewById(R.id.tv_noSubmit);
        iv_avg = findViewById(R.id.iv_avg);
        iv_max = findViewById(R.id.iv_max);
        iv_min = findViewById(R.id.iv_min);
        iv_correcting = findViewById(R.id.iv_correcting);
        iv_noCorrecting = findViewById(R.id.iv_noCorrecting);
        iv_noSubmit = findViewById(R.id.iv_noSubmit);
        iv_avg.setOnClickListener(this);
        iv_max.setOnClickListener(this);
        iv_min.setOnClickListener(this);
        iv_correcting.setOnClickListener(this);
        iv_noCorrecting.setOnClickListener(this);
        iv_noSubmit.setOnClickListener(this);

        // 父容器
        fb_max = findViewById(R.id.fb_max);
        fb_min = findViewById(R.id.fb_min);
        fb_correcting = findViewById(R.id.fb_correcting);
        fb_noCorrecting = findViewById(R.id.fb_noCorrecting);
        fb_noSubmit = findViewById(R.id.fb_noSubmit);

        findViewById(R.id.iv_back).setOnClickListener(v-> finish());

        loadItems_Net();

    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                homeworkReport = (THomeworkReportEntity) message.obj;
                tv_avg.setText(homeworkReport.getAvg());
                tv_max.setText(homeworkReport.getMax());
                tv_min.setText(homeworkReport.getMin());
                tv_correcting.setText(homeworkReport.getCorrecting());
                tv_noCorrecting.setText(homeworkReport.getNoCorrecting());
                tv_noSubmit.setText(homeworkReport.getNoSubmit());

            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_REPORT + "?userName=" + username + "&taskId=" + taskId + "&type=" + type;

        Log.d("wen", "home: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Log.d("wen", "loadItems_Net: " + itemString);
                Gson gson = new Gson();
                THomeworkReportEntity homeworkReport = gson.fromJson(itemString, THomeworkReportEntity.class);

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = homeworkReport;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_avg:
                showList(0);
                break;
            case R.id.iv_max:
                showList(1);
                break;
            case R.id.iv_min:
                showList(2);
                break;
            case R.id.iv_correcting:
                showList(3);
                break;
            case R.id.iv_noCorrecting:
                showList(4);
                break;
            case R.id.iv_noSubmit:
                showList(5);
                break;
        }
    }

    public void showList(int newPos){
        View view = null;
        int len;
        // 关闭showPos部分视图
        switch (showPos){
            case 0:
                break;
            case 1:
                fb_max.removeAllViews();
                iv_max.setImageResource(R.drawable.down_icon);
                break;
            case 2:
                fb_min.removeAllViews();
                iv_min.setImageResource(R.drawable.down_icon);
                break;
            case 3:
                fb_correcting.removeAllViews();
                iv_correcting.setImageResource(R.drawable.down_icon);
                break;
            case 4:
                fb_noCorrecting.removeAllViews();
                iv_noCorrecting.setImageResource(R.drawable.down_icon);
                break;
            case 5:
                fb_noSubmit.removeAllViews();
                iv_noSubmit.setImageResource(R.drawable.down_icon);
                break;
            default:
                break;
        }

        if(newPos != showPos){
            // 展示newPos部分视图，关闭showPos部分视图
            switch (newPos){
                case 0:

                    break;
                case 1:
                    len = homeworkReport.getMaxList().size();
                    for(int i = 0; i < len; ++i){
                        String name = homeworkReport.getMaxList().get(i);
                        view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_report_student, fb_max, false);
                        TextView tv_name = view.findViewById(R.id.tv_name);
                        tv_name.setText(name);
                        ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                        params.width = fb_max.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                        tv_name.setLayoutParams(params);
                        fb_max.addView(view);
                    }
                    iv_max.setImageResource(R.drawable.up_icon);
                    break;
                case 2:
                    len = homeworkReport.getMinList().size();
                    for(int i = 0; i < len; ++i){
                        String name = homeworkReport.getMinList().get(i);
                        view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_report_student, fb_min, false);
                        TextView tv_name = view.findViewById(R.id.tv_name);
                        tv_name.setText(name);
                        ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                        params.width = fb_max.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                        tv_name.setLayoutParams(params);
                        fb_min.addView(view);
                    }
                    iv_min.setImageResource(R.drawable.up_icon);
                    break;
                case 3:
                    len = homeworkReport.getCorrectingList().size();
                    for(int i = 0; i < len; ++i){
                        String name = homeworkReport.getCorrectingList().get(i);
                        view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_report_student, fb_correcting, false);
                        TextView tv_name = view.findViewById(R.id.tv_name);
                        tv_name.setText(name);
                        ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                        params.width = fb_max.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                        tv_name.setLayoutParams(params);
                        fb_correcting.addView(view);
                    }
                    iv_correcting.setImageResource(R.drawable.up_icon);
                    break;
                case 4:
                    len = homeworkReport.getNoCorrectingList().size();
                    for(int i = 0; i < len; ++i){
                        String name = homeworkReport.getNoCorrectingList().get(i);

                        view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_report_student, fb_noCorrecting, false);
                        TextView tv_name = view.findViewById(R.id.tv_name);
                        tv_name.setText(name);
                        ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                        params.width = fb_max.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                        tv_name.setLayoutParams(params);
                        fb_noCorrecting.addView(view);
                    }
                    iv_noCorrecting.setImageResource(R.drawable.up_icon);
                    break;
                case 5:
                    len = homeworkReport.getNoSubmitList().size();
                    Log.d(TAG, "showList: " + len);
                    for(int i = 0; i < len; ++i){
                        String name = homeworkReport.getNoSubmitList().get(i);
                        Log.d(TAG, "showList: " + i + " " + name);
                        view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_report_student, fb_noSubmit, false);
                        TextView tv_name = view.findViewById(R.id.tv_name);
                        tv_name.setText(name);
                        ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                        params.width = fb_max.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                        tv_name.setLayoutParams(params);
                        fb_noSubmit.addView(view);
                    }
                    iv_noSubmit.setImageResource(R.drawable.up_icon);
                    break;
                default:
                    break;
            }

            showPos = newPos;
        }else{
            // 同时showPos为-1
            showPos = -1;
        }
    }
}