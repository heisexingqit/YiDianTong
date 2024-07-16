package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;

public class MyIntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_introduction);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        findViewById(R.id.iv_back).setOnClickListener(v->finish());
    }
}