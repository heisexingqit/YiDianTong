package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.TBellUpdateAdapter;

public class TBellNoticeUpdateActivity extends AppCompatActivity {

    private NoScrollViewPager fvp_bell;
    private TBellUpdateAdapter adapter;
    private int typeno;
    private TextView ftv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbell_notice_update);

        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });
        fvp_bell = findViewById(R.id.fvp_bell);
        ftv_title = findViewById(R.id.ftv_title);
        adapter = new TBellUpdateAdapter(getSupportFragmentManager());
        fvp_bell.setAdapter(adapter);
        String type = getIntent().getStringExtra("type");
        if(type.equals("3")){
            typeno = 0;
            ftv_title.setText("发布通知");
        }else if(type.equals("4")){
            typeno = 1;
            ftv_title.setText("发布公告");
        }else {
            typeno = -1;
        }
        fvp_bell.setCurrentItem(typeno);

    }
}