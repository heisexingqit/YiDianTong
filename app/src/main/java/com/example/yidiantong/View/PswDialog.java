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

import androidx.annotation.NonNull;

import com.example.yidiantong.R;


public class PswDialog extends Dialog implements View.OnClickListener {



    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private Button fbtn_cancel;
    private Button fbtn_confirm;

    private String cancel,confirm;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public void setCancel(String cancel,IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener=cancelListener;
    }
    public void setConfirm(String confirm,IOnConfirmListener confirmListener){
        this.confirm = confirm;
        this.confirmListener=confirmListener;
    }

    public PswDialog(@NonNull Context context) {
        super(context);
    }

    public PswDialog(@NonNull Context context,int themeResId) {
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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fbtn_cancel:
                if(cancelListener!=null){
                    cancelListener.onCancel(this);
                }
                dismiss();
                break;
            case R.id.fbtn_confirm:
                if(confirmListener!=null){
                    confirmListener.onConfirm(this);
                }
                dismiss();
                break;

        }
    }

    public interface IOnCancelListener{
        void onCancel(PswDialog dialog);
    }

    public interface IOnConfirmListener{
        void onConfirm(PswDialog dialog);
    }
}
