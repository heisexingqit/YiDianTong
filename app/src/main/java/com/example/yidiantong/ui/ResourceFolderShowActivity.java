package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.fragment.LearnPlanPPTFragment;
import com.example.yidiantong.fragment.LearnPlanPaperFragment;
import com.example.yidiantong.fragment.LearnPlanVideoFragment;
import com.example.yidiantong.fragment.ResourceFolderPPTFragment;
import com.example.yidiantong.fragment.ResourceFolderPaperFragment;
import com.example.yidiantong.fragment.ResourceFolderVideoFragment;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.PagingInterface;

public class ResourceFolderShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_folder_show);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        LearnPlanItemEntity itemShow = (LearnPlanItemEntity) getIntent().getSerializableExtra("itemShow");
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(itemShow.getResourceName());
        FrameLayout fl_main = findViewById(R.id.fl_main);
        Fragment fragment = null;
        Log.d("wen", "onCreate: " + itemShow);
        switch (itemShow.getFormat()) {
            case "word":
                fragment = ResourceFolderPaperFragment.newInstance(itemShow);

                break;
            case "video":
            case "music":
                fragment = ResourceFolderVideoFragment.newInstance(itemShow);

                break;
            case "ppt":
                fragment = ResourceFolderPPTFragment.newInstance(itemShow);
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, fragment).commit();
    }
}