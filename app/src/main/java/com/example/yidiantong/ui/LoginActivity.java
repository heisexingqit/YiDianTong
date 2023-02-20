package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yidiantong.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private ImageView iv_eye;
    private EditText et_pw;
    private ImageView iv_account;
    private ImageView iv_pw;
    private LinearLayout ll_account;
    private LinearLayout ll_pw;
    private EditText et_account;

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

        //组件样式 开始------------------------------------------------------
        //eye控制密码显示
        iv_eye.setOnClickListener(this);

        //输入框聚焦处理
        et_account.setOnFocusChangeListener(this);
        et_pw.setOnFocusChangeListener(this);
        // 组件样式 结束-----------------------------------------------------

        //获取组件数据
        String mAccount = et_account.getText().toString();
        String mPassword = et_account.getText().toString();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_eye:
                //在显示和隐藏输入框数据时，光标回归0，因此需要记录光标当前位置
                int pos = 0;
                if (is_pw_show == 0) {
                    //切换eye图片
                    if(is_pw_focus == 1){
                        iv_eye.setImageResource(R.drawable.eye_open_focus);
                    }else{
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
                    if(is_pw_focus == 1){
                        iv_eye.setImageResource(R.drawable.eye_close_focus);
                    }else{
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
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.et_account:
                if (b) {
                    //获取焦点：1.修改输入框的颜色，2.修改左侧icon的颜色
                    ll_account.setBackground(getDrawable(R.drawable.edit_text_border_focus));
                    iv_account.setImageResource(R.drawable.account_icon_focus);
                } else {
                    ll_account.setBackground(getDrawable(R.drawable.edit_text_border_unfocus));
                    iv_account.setImageResource(R.drawable.account_icon_unfocus);
                }
                break;
            case R.id.et_pw:
                if (b) {
                    if(is_pw_show == 1){
                        iv_eye.setImageResource(R.drawable.eye_open_focus);
                    }else{
                        iv_eye.setImageResource(R.drawable.eye_close_focus);
                    }
                    ll_pw.setBackground(getDrawable(R.drawable.edit_text_border_focus));
                    iv_pw.setImageResource(R.drawable.password_icon_focus);
                    is_pw_focus = 1;
                } else {
                    if(is_pw_show == 1){
                        iv_eye.setImageResource(R.drawable.eye_open);
                    }else{
                        iv_eye.setImageResource(R.drawable.eye_close);
                    }
                    ll_pw.setBackground(getDrawable(R.drawable.edit_text_border_unfocus));
                    iv_pw.setImageResource(R.drawable.password_icon_unfocus);
                    is_pw_focus = 0;
                }
                break;
        }
    }
}