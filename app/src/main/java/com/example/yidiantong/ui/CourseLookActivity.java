package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.CourseLookEntity;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.bean.TKeTangListEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CourseLookActivity extends AppCompatActivity {

    private static final String TAG = "CourseLookActivity";

    private TextView ftv_cl_classname;
    private TextView ftv_cl_teaname;
    private TextView ftv_cl_stuname;
    private TextView ftv_cl_exit;

    private Handler handler_run = new Handler();
    private int runTime = 500;
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler_run.postDelayed(this, runTime);
        }
        void update() {
            loadItems_Net();
        }
    };
    private List<CourseLookEntity.queList> queList;
    private int quemode = 0;
    private String action = "";
    private String resPath;
    private String ip;
    private String stunum;
    private String desc;
    private String learnPlanId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_look);

        ftv_cl_classname = findViewById(R.id.ftv_cl_classname);
        ftv_cl_teaname = findViewById(R.id.ftv_cl_teaname);
        ftv_cl_stuname = findViewById(R.id.ftv_cl_stuname);
        ftv_cl_exit = findViewById(R.id.ftv_cl_exit);

        ftv_cl_classname.setText("课堂：" + this.getIntent().getStringExtra("classname"));
        ftv_cl_teaname.setText("教师：" + this.getIntent().getStringExtra("teaname"));
        ftv_cl_stuname.setText("学生：" + this.getIntent().getStringExtra("stuname"));

        ftv_cl_exit.setOnClickListener(v -> {
            finish();
        });

        loadItems_Net();
        handler_run.postDelayed(runnable, 2000);
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                listenclass((List<CourseLookEntity>) message.obj);
            } else if (message.what == 101) {
                hudongclass((List<CourseLookEntity.queList>) message.obj);
            }
        }
    };

    private void hudongclass(List<CourseLookEntity.queList> queList) {
        if (queList.get(0).getType().contains("question")) {
            String imagePath = null;
            if (queList.get(0).getLinks().contains("bag")) {
                imagePath = ip + "/html" + queList.get(0).getLinks() + "/" + queList.get(0).getQuestionId() + "Show.html";
            } else {
                imagePath = ip + "/html" + queList.get(0).getLinks() + queList.get(0).getQuestionId() + "Show.html";
            }
            Intent intent = new Intent(this, CourseQuestionActivity.class);
            intent.putExtra("action", action);
            intent.putExtra("queList", String.valueOf(queList));
            intent.putExtra("stuname", getIntent().getStringExtra("stuname"));
            intent.putExtra("stunum", stunum);
            intent.putExtra("ip", ip);
            intent.putExtra("imagePath", imagePath);
            intent.putExtra("learnPlanId", learnPlanId);
            intent.putExtra("resourceID", queList.get(0).getResourceId());
            intent.putExtra("questionScore", queList.get(0).getQuestionScore());
            intent.putExtra("interactionType", queList.get(0).getQuestionType());
            intent.putExtra("questionAnswer", queList.get(0).getQuestionAnswerStr());
            intent.putExtra("questionTypeName", queList.get(0).getQuestionTypeName());
            intent.putExtra("questionType", queList.get(0).getQuestionType());
            intent.putExtra("questionValueList", queList.get(0).getQuestionValueList());
            intent.putExtra("learnPlanName", this.getIntent().getStringExtra("classname"));
            intent.putExtra("answerTime", desc);
            startActivity(intent);
            action = "";
        } else if (queList.get(0).getType().equals("document")) {
            if (queList.get(0).getLinks().contains(".ppt") | queList.get(0).getLinks().contains(".pptx")) {

            } else if (queList.get(0).getLinks().contains(".doc") | queList.get(0).getLinks().contains(".docx")) {

            } else {
                Intent intent = new Intent(this, CourseLockActivity.class);
                intent.putExtra("stuname", getIntent().getStringExtra("stuname"));
                intent.putExtra("stunum", stunum);
                intent.putExtra("ip", ip);
                startActivity(intent);

            }
        }
    }

    private void listenclass(List<CourseLookEntity> moreList) {
        if (moreList.get(0).getAction().equals("lock")) {
            if (quemode == -1) {
                Intent intent = new Intent(this, CourseLockActivity.class);
                intent.putExtra("stuname", getIntent().getStringExtra("stuname"));
                intent.putExtra("stunum", moreList.get(0).getTarget());
                intent.putExtra("ip", moreList.get(0).getResRootPath());
                String action = "lock";
                intent.putExtra("action", action);
//                    PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                startActivity(intent);
            } else {
                action = "lock";
            }

        } else if (moreList.get(0).getAction().equals("read-lock")) {
            if (action.equals("lock")) {
                action = "lock";
            } else {
                action = "read-lock";
            }
            Log.e("action", "" + action);
            resPath = moreList.get(0).getResPath();
            ip = moreList.get(0).getResRootPath();
            stunum = moreList.get(0).getTarget();
            desc = moreList.get(0).getDesc();
            learnPlanId = moreList.get(0).getLearnPlanId();
        } else if (moreList.get(0).getAction().equals("readyResponder")) {
            runTime = 100;
            Intent intent = new Intent(this, CourseLockActivity.class);
            intent.putExtra("stuname", getIntent().getStringExtra("stuname"));
            intent.putExtra("stunum", moreList.get(0).getTarget());
            intent.putExtra("ip", moreList.get(0).getResRootPath());
            String action = "readyResponder";
            intent.putExtra("action", action);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            startActivity(intent);

        } else if (moreList.get(0).getAction().equals("startResponder")) {
            Intent intent1 = new Intent(this, CourseLockActivity.class);
            intent1.putExtra("stuname", getIntent().getStringExtra("stuname"));
            intent1.putExtra("stunum", moreList.get(0).getTarget());
            intent1.putExtra("ip", moreList.get(0).getResRootPath());
            String action1 = "startResponder";
            intent1.putExtra("action", action1);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            startActivity(intent1);
        } else if (moreList.get(0).getAction().equals("stopQiangDa") || moreList.get(0).getAction().equals("stopResponder")) {
            runTime = 500;
            Intent intent1 = new Intent(this, CourseLockActivity.class);
            intent1.putExtra("stuname", getIntent().getStringExtra("stuname"));
            intent1.putExtra("stunum", moreList.get(0).getTarget());
            intent1.putExtra("ip", moreList.get(0).getResRootPath());
            String action1 = "stopQiangDa";
            intent1.putExtra("action", action1);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            startActivity(intent1);

        } else if (moreList.get(0).getAction().equals("toScan")) {
            Toast.makeText(this, "老师已下课!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 课堂互动 未完成
    private void loadItems_Net() {
        String username = getIntent().getStringExtra("username");
        String ip = getIntent().getStringExtra("ip");
        String mRequestUrl = "http://" + ip + ":8901" + Constant.GET_MESSAGE_LIST_BY_STU + "?userId=" + username;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.e("0202", "loadItems_Net: " + json.getJSONArray("messageList"));
                // 主体“messageList”中第一个JsonObject【改为最后一个，因为前面可能是垃圾消息】
                int length = json.getJSONArray("messageList").length();
                JSONObject data_obj = json.getJSONArray("messageList").getJSONObject(length - 1);
                // 定位到“period”列表
                String courseString = data_obj.getString("period");
                String newString = ",\"period\":" + courseString;
                courseString = "[" + courseString + "]"; // 将题面制作为JSONArray，后续解析为List

                // 去除题目信息的部分，制作为JSONArray，后续解析为List【注意去除前面的垃圾消息，不然可能闪退】
                String itemdetailString = "[" + data_obj.toString().replace(newString, "") + "]";
//                LogUtils.writeLogToFile("CourseLookEntity.txt", itemdetailString, false, this);

                Gson gson = new Gson();
                // 将除去题目信息部分解析为列表对象，对象包括多个动作参数
                List<CourseLookEntity> moreList = gson.fromJson(itemdetailString, new TypeToken<List<CourseLookEntity>>() {
                }.getType());
                // 判断是否有题目
                if (courseString.equals("[null]")) {
                    quemode = -1;
                } else {
                    queList = gson.fromJson(courseString, new TypeToken<List<CourseLookEntity.queList>>() {
                    }.getType());
                    quemode = 1;
                }

                //封装消息，传递给主线程
                Message message1 = Message.obtain();
                message1.obj = moreList;

                Message message2 = Message.obtain();
                message2.obj = queList;

                //标识线程
                message1.what = 100;
                if (moreList.size() == 0) {
                    // 无效信息返回
                    return;
                }

                handler.sendMessage(message1); // 动作处理100
                if (quemode == 1) {
                    message2.what = 101;
                    handler.sendMessage(message2); // 题目处理101
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });

        MyApplication.addRequest(request, TAG);
    }

    // 按返回键，不传回上一界面
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler_run.removeCallbacks(runnable); //停止刷新
        // 发送广播通知子Activity结束
        Intent broadcastIntent = new Intent("finish_activities");
        sendBroadcast(broadcastIntent);
    }
}