package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yidiantong.R;
import com.example.yidiantong.View.PswDialog;

public class MainMyFragment extends Fragment implements View.OnClickListener {

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

    public static MainMyFragment newInstance(){
        MainMyFragment fragment = new MainMyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //获取视图View
        View view = inflater.inflate(R.layout.fragment_main_my, container, false);

        f_ll_info = view.findViewById(R.id.f_ll_info);
        f_ll_us = view.findViewById(R.id.f_ll_us);
        f_ll_update = view.findViewById(R.id.f_ll_update);
        f_ll_psw = view.findViewById(R.id.f_ll_psw);
        f_ll_center = view.findViewById(R.id.f_ll_center);
        fbtn_exit = view.findViewById(R.id.fbtn_exit);
        fbtn_cancel = view.findViewById(R.id.fbtn_cancel);
        fbtn_confirm = view.findViewById(R.id.fbtn_confirm);

        // 设置点击事件
        f_ll_info.setOnClickListener(this);
        f_ll_us.setOnClickListener(this);
        f_ll_update.setOnClickListener(this);
        f_ll_psw.setOnClickListener(this);
        f_ll_center.setOnClickListener(this);
        fbtn_exit.setOnClickListener(this);

        return view;
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
                PswDialog builder = new PswDialog(getActivity());

                builder.setCancel("cancel",new PswDialog.IOnCancelListener(){

                    @Override
                    public void onCancel(PswDialog dialog) {
                        Toast.makeText(getActivity(),"已取消", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setConfirm("confirm",new PswDialog.IOnConfirmListener(){

                    @Override
                    public void onConfirm(PswDialog dialog) {
                        String old_pw = dialog.old_pw;
                        String new_pw = dialog.new_pw;
                        Toast.makeText(getActivity(),"修改成功:原密码："+old_pw+" 新密码："+new_pw, Toast.LENGTH_SHORT).show();
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