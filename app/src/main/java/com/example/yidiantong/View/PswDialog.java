package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;


public class PswDialog extends Dialog implements View.OnClickListener {

    private int is_old_show = 0;//0表示隐藏，1表示显示
    private int is_new1_show = 0;//0表示隐藏，1表示显示
    private int is_new2_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private Button fbtn_cancel;
    private Button fbtn_confirm;

    private String cancel, confirm;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    //第一个输入框
    private ImageView fiv_old_eye;
    private EditText fet_old_psw;

    //第二个输入框
    private ImageView fiv_new1_eye;
    private EditText fet_new1_psw;

    //第二个输入框
    private ImageView fiv_new2_eye;
    private EditText fet_new2_psw;

    public String old_pw;
    public String new_pw;

    private Context mContext;

    public void setCancel(String cancel, IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener = cancelListener;
    }

    public void setConfirm(String confirm, IOnConfirmListener confirmListener) {
        this.confirm = confirm;
        this.confirmListener = confirmListener;
    }

    public PswDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public PswDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_update_psw);

        fbtn_cancel = findViewById(R.id.fbtn_cancel);
        fbtn_confirm = findViewById(R.id.fbtn_confirm);

        fbtn_cancel.setOnClickListener(this);
        fbtn_confirm.setOnClickListener(this);

        //获取组件
        fiv_old_eye = findViewById(R.id.fiv_old_eye);
        fiv_new1_eye = findViewById(R.id.fiv_new1_eye);
        fiv_new2_eye = findViewById(R.id.fiv_new2_eye);
        fet_old_psw = findViewById(R.id.fet_old_psw);
        fet_new1_psw = findViewById(R.id.fet_new1_psw);
        fet_new2_psw = findViewById(R.id.fet_new2_psw);
        //给eye设置监听事件
        fiv_old_eye.setOnClickListener(this);
        fiv_new1_eye.setOnClickListener(this);
        fiv_new2_eye.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fbtn_cancel:
                if (cancelListener != null) {
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.fbtn_confirm:
                if (confirmListener != null) {
                    old_pw = fet_old_psw.getText().toString();
                    new_pw = fet_new1_psw.getText().toString();
                    String new_pw2 = fet_new2_psw.getText().toString();
                    if(!new_pw.equals(new_pw2)){
                        Toast.makeText(mContext, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();
                        break;
                    }else if (!MyApplication.password.equals(old_pw)) { // 假设MyApplication.password存储的是正确的原密码
                        Toast.makeText(mContext, "原密码错误！", Toast.LENGTH_SHORT).show();
                        break;
                    }else confirmListener.onConfirm(this);
                }
                dismiss();
                break;
            case R.id.fiv_old_eye:
                if (is_old_show == 0) {
                    showPw(fiv_old_eye, fet_old_psw);
                    is_old_show = 1;
                } else {
                    hidePw(fiv_old_eye, fet_old_psw);
                    is_old_show = 0;
                }
                break;
            case R.id.fiv_new1_eye:
                if (is_new1_show == 0) {
                    showPw(fiv_new1_eye, fet_new1_psw);
                    is_new1_show = 1;
                } else {
                    hidePw(fiv_new1_eye, fet_new1_psw);
                    is_new1_show = 0;
                }
                break;
            case R.id.fiv_new2_eye:
                if (is_new2_show == 0) {
                    showPw(fiv_new2_eye, fet_new2_psw);
                    is_new2_show = 1;
                } else {
                    hidePw(fiv_new2_eye, fet_new2_psw);
                    is_new2_show = 0;
                }
                break;
        }
    }

    //et = 输入框，iv = eye图标 隐藏密码方法
    private void hidePw(ImageView iv, EditText et) {
        //在显示和隐藏输入框数据时，光标回归0，因此需要记录光标当前位置
        int pos = 0;
        iv.setImageResource(R.drawable.eye_close);
        //获取光标位置
        pos = et.getSelectionStart();
        et.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //恢复光标位置
        et.setSelection(pos);
    }

    //et = 输入框，iv = eye图标 显示密码方法
    private void showPw(ImageView iv, EditText et) {
        //在显示和隐藏输入框数据时，光标回归0，因此需要记录光标当前位置
        int pos = 0;
        //切换eye图标为open
        iv.setImageResource(R.drawable.eye_open);
        //获取光标位置
        pos = et.getSelectionStart();
        et.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        //恢复光标位置
        et.setSelection(pos);
    }


    public interface IOnCancelListener {
        void onCancel(PswDialog dialog);
    }

    public interface IOnConfirmListener {
        void onConfirm(PswDialog dialog);
    }
}
