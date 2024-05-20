package com.example.yidiantong.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidiantong.R;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.MainPagerAdapter;


public class MainBookExerciseActivity extends AppCompatActivity {

    private NoScrollViewPager fsvp_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_book);
        fsvp_detail = findViewById(R.id.fsvp_detail);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        fsvp_detail.setAdapter(adapter);
        // 将 ViewPager 的当前显示页面设置为索引为 5 的页面,即错题本详情页面
        fsvp_detail.setCurrentItem(6);

    }

}