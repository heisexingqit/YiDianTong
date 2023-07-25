package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.SelectCourseEntity;
import com.example.yidiantong.bean.SelectDetailEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

public class SelectDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView ftv_sd_tag;
    private TextView ftv_sd_name;
    private TextView ftv_sd_time;

    private Button fbtn_sd_confirm;
    private String username;
    private String mode;
    private String taskId;
    private LinearLayout fll_sd_course;
    private int answer;
    private RadioButton[] btn;
    private int answer_o = -2;
    private String answer_text;
    private String taskName;
    private String composeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_detail);
        TextView ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("高考选科");

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        ftv_sd_tag = findViewById(R.id.ftv_sd_tag);
        ftv_sd_name = findViewById(R.id.ftv_sd_name);
        ftv_sd_time = findViewById(R.id.ftv_sd_time);
        fll_sd_course = findViewById(R.id.fll_sd_course);
        fbtn_sd_confirm = findViewById(R.id.fbtn_sd_confirm);

        // 展示选科具体信息
        loadItems_Net();

    }



    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                init((List< SelectDetailEntity>) message.obj);
            }
            else if(message.what == 101){
                select_course((List<SelectDetailEntity.courseList>) message.obj);

            }else{
                    int f = (int) message.obj;
                    if (f == 0) {
                        //Toast.makeText(getContext(), "已读状态修改失败", Toast.LENGTH_SHORT).show();
                    }else {
                        //Toast.makeText(getContext(), "已读状态修改成功", Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };



    private void init(List<SelectDetailEntity> itemList) {
        taskName = itemList.get(0).getTaskName();
        composeId = itemList.get(0).getComposeId();
        ftv_sd_tag.setText(itemList.get(0).getDes());
        ftv_sd_name.setText(taskName);
        ftv_sd_time.setText(itemList.get(0).getStartTimeStr() + "至" + itemList.get(0).getEndTimeStr());
    }

    private void select_course(List<SelectDetailEntity.courseList> courseList) {
        int choiceLen = courseList.size();
        String subjectComposeName = this.getIntent().getStringExtra("subjectComposeName");
        answer = -1;
        if(subjectComposeName.equals("未选科")){
            answer = -2;
        }
        btn = new RadioButton[choiceLen];
        int btnLenmode = -1;
        for(int i=0; i<choiceLen/2+1;i++){
            View v = LayoutInflater.from(this).inflate(R.layout.sd_btn, fll_sd_course, false);
            for(int j=0;j<2;j++){
                if(i*2+j == choiceLen & j!=0){
                    break;
                }else if(i*2+j == choiceLen & j==0){
                    btnLenmode = 1;
                    break;
                }else{
                    if(j == 0){
                        v.findViewById(R.id.frb_sd_1).setVisibility(View.VISIBLE);
                        btn[i*2+j] = v.findViewById(R.id.frb_sd_1);
                    }else if(j == 1){
                        v.findViewById(R.id.frb_sd_2).setVisibility(View.VISIBLE);
                        btn[i*2+j] = v.findViewById(R.id.frb_sd_2);
                    }
                }
            }
            if(btnLenmode == 1){
                break;
            }
            fll_sd_course.addView(v);
        }

        // 设置按钮点击事件
        View.OnClickListener myListener = v -> {
            int id = Integer.valueOf((Integer) v.getTag());
            for(int i=0; i<choiceLen ;i++){
                if(i == id){
                    btn[i].setButtonDrawable(R.drawable.bell_time_select);
                    answer = i;
                    answer_text = courseList.get(i).getName();
                }else {
                    btn[i].setButtonDrawable(R.drawable.bell_time_unselect);
                }
            }
        };


        for(int i=0; i<choiceLen ;i++){
            btn[i].setText(courseList.get(i).getName());
            // 判断是否有已选项
            if(subjectComposeName.equals(courseList.get(i).getName())){
                btn[i].setButtonDrawable(R.drawable.bell_time_select);
                answer_o = i; // 原始选择
                answer_text = subjectComposeName;
            }

            btn[i].setOnClickListener(myListener);
            btn[i].setTag(i);
        }

        fbtn_sd_confirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fbtn_sd_confirm:

                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                AlertDialog dialog = builder.create();
                //自定义title样式
                TextView tv = new TextView(this);

                if(answer == -2){
                    tv.setText("请先选择科目");    //内容
                    builder.setPositiveButton("ok",null);
                }else if((answer_o == answer | answer == -1) & (answer != -2) ){
                    tv.setText("结果未改变");    //内容
                    builder.setPositiveButton("确定",null);
                }else{
                    tv.setText("选科成功！");    //内容
                    builder.setMessage("你选择的组合为：" + answer_text);

                    builder.setPositiveButton("ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save_subject();
                            finish();
                    }
                });
                }
                tv.setTextSize(17);//字体大小
                tv.setPadding(30, 40, 30, 40);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv);
                //禁止返回和外部点击
                builder.setCancelable(false);
                //对话框弹出
                builder.show();
                break;
        }
    }

    private void save_subject() {
        // 学生姓名加密
        String userCn = this.getIntent().getStringExtra("userCn");
        String userCn_en = Uri.encode(userCn);
        Log.e("userCn_en",""+userCn_en);

        // 任务名称加密
        String taskName_en = Uri.encode(taskName);
        Log.e("taskName_en",""+taskName_en);

        // 科目组合名称加密
        String composeName_en = Uri.encode(answer_text);
        String composeName_en_new = composeName_en.replace("%2B","+");
        Log.e("composeName_en",""+composeName_en);
        // url加密链接
        String mRequestUrl = Constant.API + Constant.SAVE_SELECT_SUBJECT + "?userId=" + username + "&userCn=" + userCn +"&taskId=" + taskId + "&taskName=" + taskName + "&composeId=" + answer + "&composeName=" + answer_text + "&type=newApp";
        Log.e("加密链接",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
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
                msg.what = 102;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

    }

    //加载消息条目
    private void loadItems_Net() {
        username = this.getIntent().getStringExtra("username");
        mode = this.getIntent().getStringExtra("mode");
        taskId = this.getIntent().getStringExtra("taskId");
        String mRequestUrl = Constant.API + Constant.GET_SELECT_COURSE_TASK_DETIAL + "?userId=" + username + "&mode=" + mode + "&taskId=" + taskId;
        Log.e("GET_SELECT_COURSE_TASK_DETIAL",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                // 定位到“list”列表
                String data=json.getString("data");
                Log.e("data",""+data);
                JSONObject data_obj=new JSONObject(data);
                Log.e("data_obj",""+data_obj);
                JSONArray list=data_obj.getJSONArray("list");
                String courseString = ",\"list\":" + list.toString();
                Log.e("courseString",""+courseString);
                String itemdetailString = "[" + itemString.replace(courseString,"") + "]";

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<SelectDetailEntity> itemList = gson.fromJson(itemdetailString, new TypeToken<List<SelectDetailEntity>>() {}.getType());
                List<SelectDetailEntity.courseList> courseList = gson.fromJson(list.toString(), new TypeToken<List<SelectDetailEntity.courseList>>() {}.getType());
                Log.e("itemList",""+itemList);
                Log.e("courseList",""+courseList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;
                //标识线程
                message.what = 100;
                handler.sendMessage(message);

                //封装消息，传递给主线程
                Message message1 = Message.obtain();
                message1.obj = courseList;
                //标识线程
                message1.what = 101;
                handler.sendMessage(message1);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}