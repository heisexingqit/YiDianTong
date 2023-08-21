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
import com.example.yidiantong.R;
import com.example.yidiantong.bean.CourseLookEntity;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.bean.TKeTangListEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class CourseLookActivity extends AppCompatActivity {

    private TextView ftv_cl_classname;
    private TextView ftv_cl_teaname;
    private TextView ftv_cl_stuname;
    private TextView ftv_cl_exit;

    private Handler handler_run = new Handler();
    private int runTime = 500;
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler_run.postDelayed(this, runTime );
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
    private RequestQueue queue;
    private String learnPlanId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_look);

        ftv_cl_classname = findViewById(R.id.ftv_cl_classname);
        ftv_cl_teaname = findViewById(R.id.ftv_cl_teaname);
        ftv_cl_stuname = findViewById(R.id.ftv_cl_stuname);
        ftv_cl_exit = findViewById(R.id.ftv_cl_exit);

        ftv_cl_classname.setText("课堂："+ this.getIntent().getStringExtra("classname"));
        ftv_cl_teaname.setText("教师："+ this.getIntent().getStringExtra("teaname"));
        ftv_cl_stuname.setText("学生："+ this.getIntent().getStringExtra("stuname"));

        ftv_cl_exit.setOnClickListener(v -> {
            this.finish();
        });

        loadItems_Net();
        handler_run.postDelayed(runnable, 2000 );
    }



    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                listenclass((List<CourseLookEntity>) message.obj);
            }else if(message.what == 101){
                hudongclass((List<CourseLookEntity.queList>) message.obj);
            }
        }
    };

    private void hudongclass(List<CourseLookEntity.queList> queList) {
        if(queList.get(0).getType().equals("question")){
            String imagePath = null;
            if(queList.get(0).getLinks().contains("bag")){
                imagePath = ip + "/html" + queList.get(0).getLinks() +  "/" +queList.get(0).getQuestionId() + "Show.html";
            }else{
                imagePath = ip + "/html" + queList.get(0).getLinks()  + queList.get(0).getQuestionId() + "Show.html";
            }
            Log.e("imagePath",imagePath);
            Intent intent= new Intent(this, CourseQuestionActivity.class);
            intent.putExtra("action",action);
            intent.putExtra("queList", String.valueOf(queList));
            intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
            intent.putExtra("stunum",stunum);
            intent.putExtra("ip",ip);
            intent.putExtra("imagePath",imagePath);
            intent.putExtra("learnPlanId",learnPlanId);
            intent.putExtra("resourceID", queList.get(0).getResourceId());
            intent.putExtra("questionScore",queList.get(0).getQuestionScore());
            intent.putExtra("interactionType", queList.get(0).getQuestionType());
            intent.putExtra("questionAnswer",queList.get(0).getQuestionAnswerStr());
            intent.putExtra("questionTypeName",queList.get(0).getQuestionTypeName());
            intent.putExtra("questionValueList",queList.get(0).getQuestionValueList());
            intent.putExtra("learnPlanName", this.getIntent().getStringExtra("classname"));
            intent.putExtra("answerTime", desc);
            startActivity(intent);
            action = "";
        }else if(queList.get(0).getType().equals("document")){
            if(queList.get(0).getLinks().contains(".ppt") | queList.get(0).getLinks().contains(".pptx")){

            }else if(queList.get(0).getLinks().contains(".doc") | queList.get(0).getLinks().contains(".docx")){

            }else {
                Intent intent= new Intent(this, CourseLockActivity.class);
                intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                intent.putExtra("stunum",stunum);
                intent.putExtra("ip",ip);
                startActivity(intent);
            }
        }
    }

    private void listenclass(List<CourseLookEntity> moreList) {
            if(moreList.get(0).getAction().equals("lock")){
                if(quemode == -1){
                    Intent intent= new Intent(this, CourseLockActivity.class);
                    intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                    intent.putExtra("stunum",moreList.get(0).getTarget());
                    intent.putExtra("ip",moreList.get(0).getResRootPath());
                    String action = "lock";
                    intent.putExtra("action",action);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    startActivity(intent);
                }else{
                    action = "lock";
                }

            }else if(moreList.get(0).getAction().equals("read-lock")){
                if(action.equals("lock")){
                    action = "lock";
                }else{
                    action = "read-lock";
                }
                Log.e("action",""+action);
                resPath = moreList.get(0).getResPath();
                ip = moreList.get(0).getResRootPath();
                stunum = moreList.get(0).getTarget();
                desc = moreList.get(0).getDesc();
                learnPlanId = moreList.get(0).getLearnPlanId();
            }else if(moreList.get(0).getAction().equals("readyResponder")){
                runTime = 100;
                Intent intent= new Intent(this, CourseLockActivity.class);
                intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                intent.putExtra("stunum",moreList.get(0).getTarget());
                intent.putExtra("ip",moreList.get(0).getResRootPath());
                String action = "readyResponder";
                intent.putExtra("action",action);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                startActivity(intent);
            }else if(moreList.get(0).getAction().equals("startResponder")){
                Intent intent1= new Intent(this, CourseLockActivity.class);
                intent1.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                intent1.putExtra("stunum",moreList.get(0).getTarget());
                intent1.putExtra("ip",moreList.get(0).getResRootPath());
                String action1 = "startResponder";
                intent1.putExtra("action",action1);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                startActivity(intent1);
            }else if(moreList.get(0).getAction().equals("stopQiangDa")){
                runTime = 500;
                Intent intent1= new Intent(this, CourseLockActivity.class);
                intent1.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                intent1.putExtra("stunum",moreList.get(0).getTarget());
                intent1.putExtra("ip",moreList.get(0).getResRootPath());
                String action1 = "startResponder";
                intent1.putExtra("action",action1);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                startActivity(intent1);
            }

    }

    // 课堂互动 未完成
    private void loadItems_Net() {
        String username = getIntent().getStringExtra("username");
        String ip = getIntent().getStringExtra("ip");
        String mRequestUrl =  "http://" + ip + ":8901" + Constant.GET_MESSAGE_LIST_BY_STU +  "?userId=" + username ;
        Log.e("mReq_stu",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("messageList");
                Log.e("itemString", ""+itemString);

                // 定位到“period”列表
                String data=json.getString("messageList");
                data = data.replace("[","");
                data = data.replace("]","");

                JSONObject data_obj = new JSONObject(data);
                String courseString = data_obj.getString("period");
                String newString = ",\"period\":" + courseString;
                courseString = "[" + courseString + "]";
                String itemdetailString = itemString.replace(newString,"");

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<CourseLookEntity> moreList = gson.fromJson(itemdetailString, new TypeToken<List<CourseLookEntity>>() {}.getType());
                if(courseString.equals("[null]")){
                    quemode = -1;
                }else{
                    queList = gson.fromJson(courseString, new TypeToken<List<CourseLookEntity.queList>>() {}.getType());
                    quemode = 1;
                }

                //封装消息，传递给主线程
                Message message1 = Message.obtain();
                message1.obj = moreList;

                Message message2 = Message.obtain();
                message2.obj = queList;

                //标识线程
                message1.what = 100;
                if(moreList.size() == 0){
                    return;
                }else {
                    handler.sendMessage(message1);
                    if(quemode == 1){
                        message2.what = 101;
                        handler.sendMessage(message2);
                    }else {
                        return;
                    }
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });

        if(queue == null){
            queue = Volley.newRequestQueue(this);
            queue.add(request);
        }else{
            queue.add(request);
        }
        queue.add(request);
        //queue.getCache().clear();
    }

    // 按返回键，不传回上一界面
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        handler_run.removeCallbacks(runnable); //停止刷新
        super.onDestroy();
    }
}