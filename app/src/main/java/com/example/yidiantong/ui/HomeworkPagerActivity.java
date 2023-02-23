package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.HomeworkPagerAdapter;
import com.example.yidiantong.util.PageingInterface;

import java.util.ArrayList;
import java.util.List;

public class HomeworkPagerActivity extends AppCompatActivity implements PageingInterface{
    private static final String TAG = "HomeworkPagerActivity";

    private List<ImageView> iv_bottom_tab = new ArrayList<>();
    private List<TextView> tv_bottom_tab = new ArrayList<>();
    private ViewPager vp_study;
    private int currentItem = 0;
    private int pagerCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_pager);

        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.old_status_bar_bg));
        }

        vp_study = findViewById(R.id.vp_homework);
        HomeworkPagerAdapter adapter = new HomeworkPagerAdapter(getSupportFragmentManager());
        pagerCount = adapter.getCount();
        vp_study.setAdapter(adapter);
        vp_study.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                //翻页同步下标
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        vp_study.setCurrentItem(currentItem);

        //顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v->{
            this.finish();
        });
    }


    @Override
    public void pageLast() {
        if(currentItem == 0){
            Toast.makeText(this, "已经是第一题", Toast.LENGTH_SHORT).show();
        }else{
            currentItem -= 1;
            vp_study.setCurrentItem(currentItem);
        }
    }

    //Fragment调用Activity方法， 实现Fragment的接口
    @SuppressLint("ResourceAsColor")
    @Override
    public void pageNext() {
        if (currentItem == pagerCount - 1){
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经最后一题，确定要提交作业？");	//内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(HomeworkPagerActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("取消", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        }else{
            currentItem += 1;
            vp_study.setCurrentItem(currentItem);
        }
    }
}