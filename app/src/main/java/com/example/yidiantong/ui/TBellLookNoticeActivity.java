package com.example.yidiantong.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.TBellNoticeEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TBellLookNoticeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TBellLookNoticeActivity";
    private String classTimeId;
    private String type;
    private String mRequestUrl;
    private TextView ftv_title;
    private TextView ftv_bd_title;
    private TextView ftv_bd_user;
    private TextView ftv_bd_time;
    private TextView ftv_bd_content;
    private TextView ftv_bd_stuname;
    private TextView ftv_bd_read;
    private TextView ftv_bd_noread;
    private ImageView fiv_bd_noarrow;
    private ImageView fiv_bd_arrow;
    private LinearLayout fll_bd_noread;
    private LinearLayout fll_bd_read;
    private int read_state;
    private String readname_s;
    private String noreadname_s;
    private Button fb_bd_modify;
    private Button fb_bd_withdraw;
    private int modifymode = -1;
    private String noticetime;
    private int height_screen;
    private int height_tv;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbell_look_notice);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        classTimeId = getIntent().getStringExtra("classTimeId");
        type = getIntent().getStringExtra("noticetype");
        ftv_title = findViewById(R.id.ftv_title);
        switch (type){
            case "3":
                ftv_title.setText("通知");
                break;
            case "4":
                ftv_title.setText("公告");
                break;
        }

        ftv_bd_title = findViewById(R.id.ftv_bd_title);
        ftv_bd_user = findViewById(R.id.ftv_bd_user);
        ftv_bd_time = findViewById(R.id.ftv_bd_time);
        ftv_bd_content = findViewById(R.id.ftv_bd_content);
        ftv_bd_read = findViewById(R.id.ftv_bd_read);
        ftv_bd_noread = findViewById(R.id.ftv_bd_noread);
        ftv_bd_stuname = findViewById(R.id.ftv_bd_stuname);
        fiv_bd_noarrow = findViewById(R.id.fiv_bd_noarrow);
        fiv_bd_arrow = findViewById(R.id.fiv_bd_arrow);
        fll_bd_noread = findViewById(R.id.fll_bd_noread);
        fll_bd_read = findViewById(R.id.fll_bd_read);
        fb_bd_modify = findViewById(R.id.fb_bd_modify);
        fb_bd_withdraw = findViewById(R.id.fb_bd_withdraw);

        // 设置时间，从上一个页面获取
        noticetime = getIntent().getStringExtra("noticetime");
        ftv_bd_time.setText(noticetime);

        //查看已读/未读,初始化为-1
        read_state = -1;
        loadClass();

        fiv_bd_arrow.setOnClickListener(this);
        fiv_bd_noarrow.setOnClickListener(this);
        fb_bd_modify.setOnClickListener(this);
        fb_bd_withdraw.setOnClickListener(this);

        MovementMethod movementMethod = ScrollingMovementMethod.getInstance();
        ftv_bd_stuname.setMovementMethod(movementMethod);

    }

    private List<TBellNoticeEntity> tBellNoticeEntity;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                tBellNoticeEntity = (List<TBellNoticeEntity>) message.obj;
                showView(tBellNoticeEntity);
            }
        }
    };

    private void showView(List<TBellNoticeEntity> tBellNoticeEntity) {
        ftv_bd_title.setText(tBellNoticeEntity.get(0).getTitle());
        ftv_bd_user.setText(tBellNoticeEntity.get(0).getAuthor());
        ftv_bd_content.setText(tBellNoticeEntity.get(0).getContent());

        //已读染色
        int positionLen = String.valueOf(tBellNoticeEntity.get(0).getReadNum()).length();
        String questionNum = "已读学生(" + tBellNoticeEntity.get(0).getReadNum() + "/" + tBellNoticeEntity.get(0).getNum() + ")";
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 5, 5 + positionLen);
        ftv_bd_read.setText(spannableString);

        //未读染色
        int positionLen1 = String.valueOf(tBellNoticeEntity.get(0).getNoReadNum()).length();
        String questionNum1 = "未读学生(" + tBellNoticeEntity.get(0).getNoReadNum() + "/" + tBellNoticeEntity.get(0).getNum() + ")";
        SpannableString spannableString1 = StringUtils.getStringWithColor(questionNum1, "#6CC1E0", 5, 5 + positionLen1);
        ftv_bd_noread.setText(spannableString1);
        // 设置已读/未读姓名格式
        List readname = tBellNoticeEntity.get(0).getReadList();
        List noreadname = tBellNoticeEntity.get(0).getNoreadList();
        readname_s = ToString(readname);
        noreadname_s = ToString(noreadname);

        // 根据权限设置修改和撤回操作
        fb_bd_modify.setVisibility(View.VISIBLE);
        fb_bd_withdraw.setVisibility(View.VISIBLE);
        fb_bd_withdraw.setBackgroundResource(R.drawable.t_homework_add);
//        if(tBellNoticeEntity.get(0).getIsAuthor()){
//        }
        //tBellNoticeEntity.get(0).getIsUpdate()

        if(modifymode == -1){
            fb_bd_modify.setBackgroundResource(R.drawable.t_homework_add);
            modifymode = 1;
        }else{
            modifymode = 0;
        }

    }

    private String ToString(List readname) {
        String readname1 = readname.toString();
        String readname2 = readname1.replace("[","");
        String readname3 = readname2.replace("]","");
        return readname3;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadClass() {
        mRequestUrl = Constant.API + Constant.T_BELL_LOOK_NOTICE + "?userName=" + MyApplication.username + "&classTimeId=" + classTimeId + "&type=" + type;
        Log.d("", "loadclass: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                Log.e("data",""+data);
                String data1 = "["+data+"]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<TBellNoticeEntity> bellList =gson.fromJson(data1, new TypeToken<List<TBellNoticeEntity>>() {}.getType());
                Log.e("bellList",""+bellList);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = bellList;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fiv_bd_arrow:
                if(read_state == -1){
                    fiv_bd_arrow.setImageResource(R.drawable.top);
                    ftv_bd_stuname.setVisibility(View.VISIBLE);
                    ftv_bd_stuname.setText(readname_s);
                    // 设置文本高度(包含初始化)
                    textview_sethight();
                    fll_bd_read.setBackgroundResource(R.color.f_light_gray);
                    ftv_bd_stuname.setBackgroundResource(R.color.f_light_gray);
                    read_state = 0;
                }else if(read_state == 0){
                    fiv_bd_arrow.setImageResource(R.drawable.bot);
                    ftv_bd_stuname.setVisibility(View.GONE);
                    fll_bd_read.setBackgroundResource(R.color.white);
                    ftv_bd_stuname.setBackgroundResource(R.color.white);
                    read_state = -1;
                }else {
                    fiv_bd_noarrow.setImageResource(R.drawable.bot);
                    fll_bd_noread.setBackgroundResource(R.color.white);
                    ftv_bd_stuname.setVisibility(View.VISIBLE);
                    ftv_bd_stuname.setText(readname_s);
                    // 设置文本高度(包含初始化)
                    textview_sethight();
                    fiv_bd_arrow.setImageResource(R.drawable.top);
                    fll_bd_read.setBackgroundResource(R.color.f_light_gray);
                    ftv_bd_stuname.setBackgroundResource(R.color.f_light_gray);
                    read_state = 0;
                }
                break;

            case R.id.fiv_bd_noarrow:
                if(read_state == -1){
                    fiv_bd_noarrow.setImageResource(R.drawable.top);
                    ftv_bd_stuname.setVisibility(View.VISIBLE);
                    ftv_bd_stuname.setText(noreadname_s);
                    // 设置文本高度(包含初始化)
                    textview_sethight();
                    fll_bd_noread.setBackgroundResource(R.color.f_light_gray);
                    ftv_bd_stuname.setBackgroundResource(R.color.f_light_gray);
                    read_state = 1;

                }else if(read_state == 1){
                    fiv_bd_noarrow.setImageResource(R.drawable.bot);
                    ftv_bd_stuname.setVisibility(View.GONE);
                    fll_bd_noread.setBackgroundResource(R.color.white);
                    ftv_bd_stuname.setBackgroundResource(R.color.white);
                    read_state = -1;
                }else {
                    fiv_bd_arrow.setImageResource(R.drawable.bot);
                    fll_bd_read.setBackgroundResource(R.color.white);
                    ftv_bd_stuname.setVisibility(View.VISIBLE);
                    ftv_bd_stuname.setText(noreadname_s);
                    // 设置文本高度(包含初始化)
                    textview_sethight();
                    fiv_bd_noarrow.setImageResource(R.drawable.top);
                    fll_bd_noread.setBackgroundResource(R.color.f_light_gray);
                    ftv_bd_stuname.setBackgroundResource(R.color.f_light_gray);
                    read_state = 1;
                }
                break;

            case R.id.fb_bd_modify:
                if(modifymode == 1){
                    Intent intent = new Intent(TBellLookNoticeActivity.this,TBellNoticeUpdateActivity.class);
                    intent.putExtra("type", type);
                    intent.putExtra("noticeId", classTimeId);
                    startActivity(intent);
                    goLastPage();
                }
                break;

            case R.id.fb_bd_withdraw:
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                //自定义title样式
                TextView tv = new TextView(this);
                tv.setText("撤回成功");    //内容
                tv.setTextSize(17);//字体大小
                tv.setPadding(30, 40, 30, 40);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv);

                AlertDialog dialog = builder.create();
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        modify();
                        goLastPage();
                    }
                });
                //禁止返回和外部点击
                builder.setCancelable(false);
                //对话框弹出
                builder.show();
                break;
        }

    }

    private void textview_sethight() {
        // 设置文本高度自适应
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ftv_bd_stuname.setLayoutParams(layoutParams);
        // 获取并设置控件高度
        ViewTreeObserver vto = ftv_bd_stuname.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ftv_bd_stuname.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height_tv = ftv_bd_stuname.getMeasuredHeight();
                text_hight(height_tv);
            }
        });
    }

    // 设置姓名文本高度
    private void text_hight(int height_tv) {

        // 获取屏幕高度
        WindowManager wm = this .getWindowManager();
        height_screen = wm.getDefaultDisplay().getHeight();
        //Log.e("height_screen",""+ height_screen);
        int[] location = new int[2] ;
        //获取文本控件在当前窗口内的绝对坐标
        ftv_bd_noread.getLocationInWindow(location);
        ftv_bd_noread.getLocationOnScreen(location);

        ImageView fiv_back = findViewById(R.id.fiv_back);
        int[] location1 = new int[2] ;
        //获取返回按钮在当前窗口内的绝对坐标
        fiv_back.getLocationInWindow(location1);
        fiv_back.getLocationOnScreen(location1);

        // 计算剩余屏幕
        int hight = height_screen - location[1] - 152 - location1[1];
        //Log.e("tvhight",""+hight);
        if(height_tv >= hight){
            // 重新设置控件高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ftv_bd_stuname.getLayoutParams();
            params.height = hight;
            ftv_bd_stuname.setLayoutParams(params);
        }
    }

    private void goLastPage() {
        this.finish();
    }

    private final Handler handler2 = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                int f = (int) message.obj;
                if (f == 0) {
                    Toast.makeText(getApplicationContext(), "撤回失败", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "撤回成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void modify() {
        mRequestUrl = Constant.API + Constant.T_DELETE_NOTICE + "?noticeId=" + classTimeId;
        Log.d("", "撤回: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                Message message = Message.obtain();
                if(isSuccess){
                    message.obj = 1;
                }else{
                    message.obj = 0;
                }
                //标识线程
                message.what = 100;
                handler2.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

}