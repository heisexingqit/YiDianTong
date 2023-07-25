package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.THuDongInterface;

public class HuDongDialog extends Dialog implements View.OnClickListener {

    private ImageView fiv_hd_cancle;
    private ImageView fiv_hd_start;
    private TextView ftv_hd_duo;
    private TextView ftv_hd_dan;
    private ImageView fiv_hd_duo_jian;
    private ImageView fiv_hd_duo_jia;
    private ImageView fiv_hd_dan_jian;
    private ImageView fiv_hd_dan_jia;
    private ImageView fiv_hd_luru;
    private ImageView fiv_hd_panduan;
    private ImageView fiv_hd_duo;
    private ImageView fiv_hd_dan;
    private int dansize;
    private int duosize;
    private THuDongInterface hudong;

    int[] unselectIcons = {R.drawable.hudong_01, R.drawable.hudong_02, R.drawable.hudong_03, R.drawable.hudong_04};
    int[] selectIcons = {R.drawable.hudong_010, R.drawable.hudong_020, R.drawable.hudong_030, R.drawable.hudong_040,};
    int answer = -1;

    ImageView[] iv_answer = new ImageView[5];
    private int click;

    public HuDongDialog(@NonNull Context context) {
        super(context);
        hudong = (THuDongInterface) context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.t_dialog_class_hudong);

        fiv_hd_dan = findViewById(R.id.fiv_hd_dan);
        fiv_hd_duo = findViewById(R.id.fiv_hd_duo);
        fiv_hd_panduan = findViewById(R.id.fiv_hd_panduan);
        fiv_hd_luru = findViewById(R.id.fiv_hd_luru);
        fiv_hd_dan_jia = findViewById(R.id.fiv_hd_dan_jia);
        fiv_hd_dan_jian = findViewById(R.id.fiv_hd_dan_jian);
        fiv_hd_duo_jia = findViewById(R.id.fiv_hd_duo_jia);
        fiv_hd_duo_jian = findViewById(R.id.fiv_hd_duo_jian);
        ftv_hd_dan = findViewById(R.id.ftv_hd_dan);
        ftv_hd_duo = findViewById(R.id.ftv_hd_duo);
        fiv_hd_start = findViewById(R.id.fiv_hd_start);
        fiv_hd_cancle = findViewById(R.id.fiv_hd_cancle);

        iv_answer[0] = fiv_hd_dan;
        iv_answer[1] = fiv_hd_duo;
        iv_answer[2] = fiv_hd_panduan;
        iv_answer[3] = fiv_hd_luru;

        dansize = Integer.valueOf(ftv_hd_dan.getText().toString());
        duosize = Integer.valueOf(ftv_hd_duo.getText().toString());
        Log.e("dansize",""+dansize);
        Log.e("duosize",""+duosize);

        fiv_hd_dan.setOnClickListener(this);
        fiv_hd_duo.setOnClickListener(this);
        fiv_hd_panduan.setOnClickListener(this);
        fiv_hd_luru.setOnClickListener(this);
        fiv_hd_dan_jia.setOnClickListener(this);
        fiv_hd_dan_jian.setOnClickListener(this);
        fiv_hd_duo_jia.setOnClickListener(this);
        fiv_hd_duo_jian.setOnClickListener(this);
        fiv_hd_start.setOnClickListener(this);
        fiv_hd_cancle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fiv_hd_dan:
                answer = 0;
                click = 0;
                showRadioBtn();
                break;
            case R.id.fiv_hd_duo:
                answer = 1;
                click = 1;
                showRadioBtn();
                break;
            case R.id.fiv_hd_panduan:
                answer = 2;
                click = 2;
                showRadioBtn();
                break;
            case R.id.fiv_hd_luru:
                answer = 3;
                click = 3;
                showRadioBtn();
                break;
            case R.id.fiv_hd_dan_jia:
                if(dansize < 6){
                    dansize = dansize + 1;
                    String dan = String.valueOf(dansize);
                    ftv_hd_dan.setText(dan);
                }
                break;
            case R.id.fiv_hd_dan_jian:
                if(dansize > 2){
                    dansize = dansize - 1;
                    String dan1 = String.valueOf(dansize);
                    ftv_hd_dan.setText(dan1);
                }
                break;
            case R.id.fiv_hd_duo_jia:
                if(duosize < 6){
                    duosize = duosize + 1;
                    String duo = String.valueOf(duosize);
                    ftv_hd_duo.setText(duo);
                }
                break;
            case R.id.fiv_hd_duo_jian:
                if(duosize > 3){
                    duosize = duosize - 1;
                    String duo1 = String.valueOf(duosize);
                    ftv_hd_duo.setText(duo1);
                }
                break;

            case R.id.fiv_hd_start:
                int size = 0;
                if(click == 0){
                    size = dansize;
                }else if(click == 1){
                    size = duosize;
                }
                Log.e("click",""+click);
                Log.e("size",""+size);
                hudong.doActionHuDong(click, size);
                dismiss();
                break;
            case R.id.fiv_hd_cancle:
                dismiss();
                break;

        }
    }

    //展示底部按钮
    private void showRadioBtn() {
        for (int i = 0; i < 4; ++i) {
            if (answer != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
                answer = -1;
            }
        }
    }
}
