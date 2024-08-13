package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blankj.utilcode.util.SPUtils;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "LoginActivity";
    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private ImageView iv_eye;
    private EditText et_pw;
    private ImageView iv_username;
    private ImageView iv_pw;
    private LinearLayout ll_username;
    private LinearLayout ll_pw;
    private EditText et_username;

    private String username;
    private String password;

    private SharedPreferences preferences;
    private LinearLayout ll_loading;

    //ip修改
    private EditText et_ip;
    private String ip;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取控件
        iv_username = findViewById(R.id.iv_username);
        iv_pw = findViewById(R.id.iv_pw);
        ll_username = findViewById(R.id.ll_username);
        ll_pw = findViewById(R.id.ll_pw);
        iv_eye = findViewById(R.id.iv_eye);
        et_pw = findViewById(R.id.et_pw);
        et_username = findViewById(R.id.et_username);
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
        //组件样式 开始------------------------------------------------------
        //eye控制密码显示
        iv_eye.setOnClickListener(this);

        //输入框聚焦处理
        et_username.setOnFocusChangeListener(this);
        et_pw.setOnFocusChangeListener(this);

        //输入优化，点击外侧LL即获取焦点
        ll_username.setOnClickListener(this);
        ll_pw.setOnClickListener(this);

        //ip获取
        et_ip = findViewById(R.id.et_ip);
        // 组件样式 结束-----------------------------------------------------

        String user = null, pw = null;
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        if (savedInstanceState != null) {
            user = savedInstanceState.getString("username");
            pw = savedInstanceState.getString("password");
        } else {
            //记住密码

            user = preferences.getString("username", null);
            pw = preferences.getString("password", null);
        }


        if (user != null) {
            et_username.setText(user);
        }
        if (pw != null) {
            et_pw.setText(pw);
        }

        //加载页
        ll_loading = findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.GONE);

        // 自动登录
        if (user != null && MyApplication.autoLogin) {
            btn_login.callOnClick();
        }else{
            et_username.setText("");
            et_pw.setText("");
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_eye:
                //在显示和隐藏输入框数据时，光标回归0，因此需要记录光标当前位置
                int pos = 0;
                if (is_pw_show == 0) {
                    //切换eye图片
                    if (is_pw_focus == 1) {
                        iv_eye.setImageResource(R.drawable.eye_open_focus);
                    } else {
                        iv_eye.setImageResource(R.drawable.eye_open);
                    }

                    //获取光标位置
                    pos = et_pw.getSelectionStart();
                    //显示输入内容
                    et_pw.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //恢复光标位置
                    et_pw.setSelection(pos);
                    is_pw_show = 1;
                } else {
                    //切换eye图片
                    if (is_pw_focus == 1) {
                        iv_eye.setImageResource(R.drawable.eye_close_focus);
                    } else {
                        iv_eye.setImageResource(R.drawable.eye_close);
                    }
                    //获取光标位置
                    pos = et_pw.getSelectionStart();
                    //隐藏输入内容
                    et_pw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //恢复光标位置
                    et_pw.setSelection(pos);
                    is_pw_show = 0;
                }
                break;
            case R.id.btn_login:
                username = et_username.getText().toString().trim();
                password = et_pw.getText().toString().trim();
                String ip = et_ip.getText().toString();
                if(ip.length()>0){
                    if(MyApplication.edution.equals("STUDENT")&&MyApplication.online_class){
                    }else Constant.API=ip;
                }
                login();
                break;
            case R.id.ll_username:
                ll_username.requestFocus();
                break;
            case R.id.ll_pw:
                ll_pw.requestFocus();
                break;
        }
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.et_username:
                if (b) {
                    //获取焦点：1.修改输入框的颜色，2.修改左侧icon的颜色
                    ll_username.setBackground(getDrawable(R.drawable.login_et_border_focus));
                    iv_username.setImageResource(R.drawable.username_icon_focus);
                } else {
                    ll_username.setBackground(getDrawable(R.drawable.login_et_border_unfocus));
                    iv_username.setImageResource(R.drawable.username_icon_unfocus);
                }
                break;
            case R.id.et_pw:
                if (b) {
                    if (is_pw_show == 1) {
                        iv_eye.setImageResource(R.drawable.eye_open_focus);
                    } else {
                        iv_eye.setImageResource(R.drawable.eye_close_focus);
                    }
                    ll_pw.setBackground(getDrawable(R.drawable.login_et_border_focus));
                    iv_pw.setImageResource(R.drawable.password_icon_focus);
                    is_pw_focus = 1;
                } else {
                    if (is_pw_show == 1) {
                        iv_eye.setImageResource(R.drawable.eye_open);
                    } else {
                        iv_eye.setImageResource(R.drawable.eye_close);
                    }
                    ll_pw.setBackground(getDrawable(R.drawable.login_et_border_unfocus));
                    iv_pw.setImageResource(R.drawable.password_icon_unfocus);
                    is_pw_focus = 0;
                }
                break;
        }
    }

    //请求数据-原始方法
    //private void login() {
    //    //开始创建转码任务
    //    new Thread(new Runnable() {
    //        @Override
    //        public void run() {
    //            try {
    //                URL url = new URL("http://www.cn901.net:8111/AppServer/ajax/userManage_login.do?" + "userName=" + userName + "&passWord=" + passWord);
    //                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
    //                httpURLConnection.setRequestMethod("GET");
    //                httpURLConnection.setConnectTimeout(8000);
    //                httpURLConnection.setReadTimeout(8000);
    //                httpURLConnection.connect();
    //                InputStream inputStream = httpURLConnection.getInputStream();
    //                InputStreamReader reader = new InputStreamReader(inputStream, "GBK");
    //                BufferedReader bufferedReader = new BufferedReader(reader);
    //                StringBuffer buffer = new StringBuffer();
    //                String temp = null;
    //                while ((temp = bufferedReader.readLine()) != null) {
    //                    buffer.append(temp);
    //                }
    //                // 关闭
    //                bufferedReader.close();
    //                reader.close();
    //                inputStream.close();
    //                httpURLConnection.disconnect();
    //                try {
    //                    String backLogJsonStr = buffer.toString();
    //                } catch (Exception e) {
    //                    e.printStackTrace();
    //                }
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }).start();
    //}

    //请求数据后台验证-Volley框架
    private void login() {
        ll_loading.setVisibility(View.VISIBLE);
        String url = Constant.API + Constant.LOGIN + "?userName=" + username + "&passWord=" + password+"&callback=ha";
        //Toast.makeText(LoginActivity.this, "登录URL:"+url, Toast.LENGTH_SHORT).show();
        // 初始化RequestQueue
        if (MyApplication.getHttpQueue()== null) {
            MyApplication.mQueue = Volley.newRequestQueue(this);
        }
        StringRequest request = new StringRequest(Request.Method.GET,url, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                // 及时解除loading效果
                ll_loading.setVisibility(View.GONE);

                boolean success = json.getBoolean("success");
                if(!success){
                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }
                if (success) {
                    //记住密码
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.commit();

                    /**
                     * 分角色登录
                     */
                    intent = null;
                    JSONObject obj = json.getJSONObject("data");

                    // 将未知key转为list
                    List<String> keysList = new ArrayList<>();
                    Iterator<String> keys = obj.keys();
                    for (Iterator<String> it = keys; keys.hasNext(); ) {
                        keysList.add(it.next());
                    }
                    String typeName = null;
                    Object intent_name;
                    if (keysList.contains("STUDENT")) {
                        typeName = "STUDENT";
                    } else if (keysList.contains("COMMON_TEACHER")) {
                        typeName = "COMMON_TEACHER";
                    } else if (keysList.contains("ADMIN_TEACHER")) {
                        typeName = "ADMIN_TEACHER";
                    }
                    JSONObject userInfo = obj.getJSONObject(typeName);
//                    String token = obj.getString("token");
//                    intent.putExtra("token", token);

                    // 全局变量
                    // 角色变量
                    MyApplication.username = username;
                    MyApplication.password = password;
                    MyApplication.typeName = typeName;
                    MyApplication.userId = userInfo.getString("userId");
                    MyApplication.cnName = userInfo.getString("name");
                    MyApplication.token = obj.getString("token");
                    MyApplication.picUrl = userInfo.getString("userPhoto");
                    if(!keysList.contains("STUDENT")){
                        Toast.makeText(LoginActivity.this, "请使用学生账号登录", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (keysList.contains("STUDENT")) {
                        typeName = "STUDENT";
                        if(MyApplication.online_class){
                            if (et_ip.getText().length() == 0) {
                                Toast.makeText(LoginActivity.this, "请输入课程ip", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                loadItems_Net();
                            }
                            //intent = new Intent(this, MainPagerActivity.class);
                        }else{
                            intent = new Intent(this, MainPagerActivity.class);
                        }

                    } else if (keysList.contains("COMMON_TEACHER")) {
                        typeName = "COMMON_TEACHER";
                        intent = new Intent(this, TMainPagerActivity.class);
                    } else if (keysList.contains("ADMIN_TEACHER")) {
                        typeName = "ADMIN_TEACHER";
                        intent = new Intent(this, TMainPagerActivity.class);
                    }
                    intent.putExtra("userId", MyApplication.userId);
                    intent.putExtra("realName", MyApplication.cnName);

                    intent.putExtra("username", username);
                    intent.putExtra("picUrl", userInfo.getString("userPhoto"));
                    intent.putExtra("password", password);
                    //两个一起用
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 开启自动登录
                    MyApplication.autoLogin = true;
                    //Toast.makeText(LoginActivity.this, "页面跳转", Toast.LENGTH_SHORT).show();

                    //登录成功跳转
                    startActivity(intent);

                }
            } catch (JSONException e) {
                Log.d("wen", "login: " + e);
            }
        }, error -> {
            // 及时解除loading效果
            Toast.makeText(LoginActivity.this, "网络连接失败:"+ error.toString(), Toast.LENGTH_SHORT).show();
            ll_loading.setVisibility(View.GONE);
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);

    }

    private void loadItems_Net() {
        String password = MyApplication.password;
        username = MyApplication.username;
        ip = et_ip.getText().toString();

        String mRequestUrl = "http://" + ip + ":8901" + Constant.KETANGPLAYBYSTU + "?userName=" + username + "&password=" + password;
        Log.e("mReq", "" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("learnPlan");
                //Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                Log.e("wen0228", "loadItems_Net: " + json);

                itemString = "[" + itemString + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<CourseScannerEntity> moreList = gson.fromJson(itemString, new TypeToken<List<CourseScannerEntity>>() {
                }.getType());
                Log.e("moreList", "" + moreList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = moreList;

                //标识线程
                message.what = 100;
                if (moreList.size() == 0) {
                  Toast.makeText(LoginActivity.this, "暂无课程开始", Toast.LENGTH_SHORT).show();
                } else {
                    // 判断学生username是否匹配
                    if (moreList.get(0).getIntroduction().equals("unExist")) {
                        Toast.makeText(LoginActivity.this, "您不是该课堂学生；或账户填写错误，请检查", Toast.LENGTH_SHORT).show();
                    } else if (moreList.get(0).getIntroduction().equals("noKetang")) {
                        Toast.makeText(LoginActivity.this, "暂无课程开始", Toast.LENGTH_SHORT).show();
                    } else {
                        handler.sendMessage(message);
                    }
                }
            } catch (JSONException e) {
                Log.e("wen20228", "loadItems_Net: " + e);
            }
        }, error -> {
            Toast.makeText(LoginActivity.this, "暂无课程开始", Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {

                SPUtils.getInstance().put("easyip", et_ip.getText().toString());
//                String ip = et_ip.getText().toString();
//
//                // 使用 Stream API 进行判断和删除
//                ipList = ipList.stream()
//                        .filter(str -> !str.equals(ip))
//                        .collect(Collectors.toList());
//                String newIpLog;
//                ArrayList<String> editIpList = new ArrayList<>(ipList.subList(0, ipList.size() - 1));
//
//                if (ipString.length() > 0) {
//                    newIpLog = ip + ", " + String.join(", ", editIpList);
//                } else {
//                    newIpLog = ip;
//                }
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("ips", newIpLog);
//                editor.commit();

                turnLookCourse((List<CourseScannerEntity>) message.obj);
            }
        }
    };
    private void turnLookCourse(List<CourseScannerEntity> moreList) {
        intent = new Intent(this, CourseLookActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("ip", ip);
        intent.putExtra("classname", moreList.get(0).getCourseName());
        intent.putExtra("teaname", moreList.get(0).getTeacherName());
        intent.putExtra("stuname", moreList.get(0).getIntroduction());
        startActivity(intent);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("username", username);
        outState.putString("password", password);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}