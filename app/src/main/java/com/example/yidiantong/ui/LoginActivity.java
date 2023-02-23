package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;



public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private static final String TAG = "LoginActivity";

    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private ImageView iv_eye;
    private EditText et_pw;
    private ImageView iv_account;
    private ImageView iv_pw;
    private LinearLayout ll_account;
    private LinearLayout ll_pw;
    private EditText et_account;

    private String account;
    private String password;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //获取控件
        iv_account = findViewById(R.id.iv_account);
        iv_pw = findViewById(R.id.iv_pw);
        ll_account = findViewById(R.id.ll_account);
        ll_pw = findViewById(R.id.ll_pw);
        iv_eye = findViewById(R.id.iv_eye);
        et_pw = findViewById(R.id.et_pw);
        et_account = findViewById(R.id.et_account);
        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);

        //组件样式 开始------------------------------------------------------
        //eye控制密码显示
        iv_eye.setOnClickListener(this);

        //输入框聚焦处理
        et_account.setOnFocusChangeListener(this);
        et_pw.setOnFocusChangeListener(this);
        // 组件样式 结束-----------------------------------------------------

        //发送请求验证
        queue = Volley.newRequestQueue(LoginActivity.this);
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
                account = et_account.getText().toString();
                password = et_pw.getText().toString();
                login();
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.et_account:
                if (b) {
                    //获取焦点：1.修改输入框的颜色，2.修改左侧icon的颜色
                    ll_account.setBackground(getDrawable(R.drawable.login_et_border_focus));
                    iv_account.setImageResource(R.drawable.account_icon_focus);
                } else {
                    ll_account.setBackground(getDrawable(R.drawable.login_et_border_unfocus));
                    iv_account.setImageResource(R.drawable.account_icon_unfocus);
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
    //                    Log.d(TAG, "run: " + backLogJsonStr);
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
        queue = Volley.newRequestQueue(LoginActivity.this);
        String url = "http://www.cn901.net:8111/AppServer/ajax/userManage_login.do?" + "userName=" + account + "&passWord=" + password;
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = JsonUtil.getJsonObjectFromString(response);
                    Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    Boolean success = json.getBoolean("success");
                    if(success){
                        //登录成功跳转
                        startActivity(new Intent(LoginActivity.this, MainPagerActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: Volley请求失败：" + error);
            }
        });
        queue.add(request);
    }
}