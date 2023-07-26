package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler_run.postDelayed(this, 200 );
        }
        void update() {
            loadItems_Net();
        }
    };
    private List<CourseLookEntity.queList> queList;
    private int quemode = 0;
    private String action;
    private String resPath;
    private String ip;
    private String stunum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_look);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

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
            if(queList.get(0).getImgSource().contains(".jpg")){
                imagePath = ip + "/html" + queList.get(0).getLinks() + "Show.html";
            }else {
                imagePath = resPath + queList.get(0).getLinks() + queList.get(0).getId() + ".jpg";
            }
            Intent intent= new Intent(this, CourseQuestionActivity.class);
            intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
            intent.putExtra("stunum",stunum);
            intent.putExtra("ip",ip);
            intent.putExtra("imagePath",imagePath);
            intent.putExtra("questionTypeName",queList.get(0).getQuestionTypeName());
            intent.putExtra("imgSource",queList.get(0).getImgSource());
            intent.putExtra("questionValueList",queList.get(0).getQuestionValueList());
            startActivity(intent);
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
                Intent intent= new Intent(this, CourseLockActivity.class);
                intent.putExtra("stuname" , getIntent().getStringExtra("stuname"));
                Log.e("学生姓名",""+getIntent().getStringExtra("stuname"));
                intent.putExtra("stunum",moreList.get(0).getTarget());
                intent.putExtra("ip",moreList.get(0).getResRootPath());
                startActivity(intent);
            }else if(moreList.get(0).getAction().equals("read-lock")){
                action = "read-lock";
                resPath = moreList.get(0).getResPath();
                ip = moreList.get(0).getResRootPath();
                stunum = moreList.get(0).getTarget();
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        super.onDestroy();
    }
}