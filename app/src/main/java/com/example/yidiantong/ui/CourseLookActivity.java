package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import com.example.yidiantong.R;

public class CourseLookActivity extends AppCompatActivity {

    private TextView ftv_cl_classname;
    private TextView ftv_cl_teaname;
    private TextView ftv_cl_stuname;
    private TextView ftv_cl_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_look);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        ftv_cl_classname = findViewById(R.id.ftv_cl_classname);
        ftv_cl_teaname = findViewById(R.id.ftv_cl_teaname);
        ftv_cl_stuname = findViewById(R.id.ftv_cl_stuname);
        ftv_cl_exit = findViewById(R.id.ftv_cl_exit);

        ftv_cl_classname.setText("课堂："+ this.getIntent().getStringExtra("classname"));
        ftv_cl_teaname.setText("教师："+ this.getIntent().getStringExtra("teaname"));
        ftv_cl_stuname.setText("学生："+ this.getIntent().getStringExtra("stuname"));

        ftv_cl_exit.setOnClickListener(v -> {
            this.finish();
        });

    }
}