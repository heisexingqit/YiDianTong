package com.example.yidiantong;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yidiantong.View.PswDialog;

public class MyActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout f_ll_info;
    private LinearLayout f_ll_us;
    private LinearLayout f_ll_update;
    private LinearLayout f_ll_psw;
    private LinearLayout f_ll_center;
    private Button fbtn_exit;

    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private Button fbtn_cancel;
    private Button fbtn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        f_ll_info = findViewById(R.id.f_ll_info);
        f_ll_us = findViewById(R.id.f_ll_us);
        f_ll_update = findViewById(R.id.f_ll_update);
        f_ll_psw = findViewById(R.id.f_ll_psw);
        f_ll_center = findViewById(R.id.f_ll_center);

        fbtn_exit = findViewById(R.id.fbtn_exit);


        fbtn_cancel = findViewById(R.id.fbtn_cancel);
        fbtn_confirm = findViewById(R.id.fbtn_confirm);

        // 设置点击事件
        f_ll_info.setOnClickListener(this);
        f_ll_us.setOnClickListener(this);
        f_ll_update.setOnClickListener(this);
        f_ll_psw.setOnClickListener(this);
        f_ll_center.setOnClickListener(this);

        fbtn_exit.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.f_ll_info:

                break;
            case R.id.f_ll_us:

                break;
            case R.id.f_ll_update:

                break;
            case R.id.f_ll_psw:
                // 修改密码弹窗
                PswDialog builder = new PswDialog(MyActivity.this);

                builder.setCancel("cancel",new PswDialog.IOnCancelListener(){

                    @Override
                    public void onCancel(PswDialog dialog) {
                        Toast.makeText(MyActivity.this,"已取消", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setConfirm("confirm",new PswDialog.IOnConfirmListener(){

                    @Override
                    public void onConfirm(PswDialog dialog) {
                        Toast.makeText(MyActivity.this,"修改成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
            case R.id.f_ll_center:

                break;
            case R.id.fbtn_exit:

                break;

            case R.id.fiv_old_eye:

                break;
        }
    }
}