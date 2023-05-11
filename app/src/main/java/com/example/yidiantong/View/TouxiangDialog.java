package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;

public class TouxiangDialog extends Dialog {

    private TextView ftv_take_pic, ftv_open_album, ftv_cancel;
    private ImageView fiv_my;
    private Context context;
    private ClickListenerInterface clickListenerInterface;

    public interface ClickListenerInterface {
        public void doGetCamera();

        public void doGetPic();

        public void doCancel();
    }

    public TouxiangDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.context = context;
    }

    public TouxiangDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_update_touxiang, null);
        setContentView(view);

        fiv_my = view.findViewById(R.id.fiv_my);
        ftv_take_pic = view.findViewById(R.id.ftv_take_pic);
        ftv_open_album = view.findViewById(R.id.ftv_open_album);
        ftv_cancel = view.findViewById(R.id.ftv_cancel);

        ftv_take_pic.setOnClickListener(new clickListener());
        ftv_open_album.setOnClickListener(new clickListener());
        ftv_cancel.setOnClickListener(new clickListener());

    }

    public void setClickListener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }

    public class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ftv_take_pic:
                    clickListenerInterface.doGetCamera();
                    break;
                case R.id.ftv_open_album:
                    clickListenerInterface.doGetPic();
                    break;
                case R.id.ftv_cancel:
                    clickListenerInterface.doCancel();
                    break;
            }
        }
    }

}
