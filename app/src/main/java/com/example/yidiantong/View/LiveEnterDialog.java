package com.example.yidiantong.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.button.SmoothCheckBox;

public class LiveEnterDialog extends Dialog implements View.OnClickListener {

    private final CheckBox cb_camera;
    private final CheckBox cb_mico;
    private final TextView tv_title;
    private Context mContext;
    private LiveEnterDialog.MyInterface myInterface;

    public void setMyInterface(LiveEnterDialog.MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    public LiveEnterDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.live_enter_dialog);
        cb_camera = findViewById(R.id.cb_camera);
        cb_camera.setChecked(true);
        cb_mico = findViewById(R.id.cb_mico);
        tv_title = findViewById(R.id.tv_title);
        findViewById(R.id.tv_enter).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_enter:
                myInterface.submit(cb_camera.isChecked(), cb_mico.isChecked());
                break;
            case R.id.tv_cancel:
                this.dismiss();
        }
    }

    public interface MyInterface {
        void submit(boolean isCamera, boolean isMico);
    }
}
