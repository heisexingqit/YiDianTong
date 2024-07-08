package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.TBellUpdateAdapter;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;
import com.example.yidiantong.fragment.TBellAnnounceUpdateFragment;
import com.example.yidiantong.fragment.TBellNoticeUpdateFragment;

public class TBellNoticeUpdateActivity extends AppCompatActivity {

    private FrameLayout fvp_bell;
    private int typeno;
    private TextView ftv_title;
    private TBellNoticeUpdateFragment tBellNoticeUpdateFragment;
    private TBellAnnounceUpdateFragment tBellAnnounceUpdateFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbell_notice_update);

        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });
        fvp_bell = findViewById(R.id.fvp_bell);
        ftv_title = findViewById(R.id.ftv_title);
        tBellNoticeUpdateFragment = new TBellNoticeUpdateFragment();
        tBellAnnounceUpdateFragment = new TBellAnnounceUpdateFragment();
        String type = getIntent().getStringExtra("type");
        if(type.equals("3")){
            ftv_title.setText("发布通知");
            getSupportFragmentManager().beginTransaction().replace(R.id.fvp_bell,  tBellNoticeUpdateFragment).commit();
        }else if(type.equals("4")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fvp_bell, tBellAnnounceUpdateFragment).commit();
            ftv_title.setText("发布公告");
        }else {
            typeno = -1;
        }


    }
}