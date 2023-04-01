package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

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
    private RequestQueue queue;

    private SharedPreferences preferences;
    private LinearLayout ll_loading;

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
        // 组件样式 结束-----------------------------------------------------

        //发送请求验证
        queue = Volley.newRequestQueue(LoginActivity.this);

        //记住密码
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String user = preferences.getString("username", null);
        String pw = preferences.getString("password", null);
        if (user != null) {
            et_username.setText(user);
        }
        if (pw != null) {
            et_pw.setText(pw);
        }

        //加载页
        ll_loading = findViewById(R.id.ll_loading);
        ll_loading.setVisibility(View.GONE);

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
                username = et_username.getText().toString();
                password = et_pw.getText().toString();
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
        queue = Volley.newRequestQueue(LoginActivity.this);
        String url = Constant.API + Constant.LOGIN + "?userName=" + username + "&passWord=" + password;
        StringRequest request = new StringRequest(url, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                // 及时解除loading效果
                ll_loading.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                boolean success = json.getBoolean("success");
                JSONObject obj = json.getJSONObject("data");

                //拿到第一个key值
                String userType = obj.keys().next();
                switch (userType) {
                    case "STUDENT":
                        //学生
                        Log.d("wen", "userId: " + obj.getJSONObject(userType).getString("userId"));
                        break;
                    case "COMMON_TEACHER":
                        //普通教师

                        break;
                    case "ADMIN_TEACHER":
                        //管理员教师

                        break;
                }
                if (success) {
                    //记住密码
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.commit();

                    Intent intent = new Intent(this, MainPagerActivity.class);
                    intent.putExtra("username", username);
                    //两个一起用
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //登录成功跳转
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            // 及时解除loading效果
            ll_loading.setVisibility(View.GONE);
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }
}