package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yidiantong.NoScrollViewPager;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.StudyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StudyListMainActivity";
    private LinearLayout ll_bottom_home;
    private int id_bottom_onclick = 0; //标定选择的
    private int[] iv_bottom_tab_focus = {R.drawable.bottom_tab_home_focus, R.drawable.bottom_tab_study_focus, R.drawable.bottom_tab_course_focus, R.drawable.bottom_tab_book_focus, R.drawable.bottom_tab_mine_focus};
    private int[] iv_bottom_tab_unfocus = {R.drawable.bottom_tab_home_unfocus, R.drawable.bottom_tab_study_unfocus, R.drawable.bottom_tab_course_unfocus, R.drawable.bottom_tab_book_unfocus, R.drawable.bottom_tab_mine_unfocus};
    private List<ImageView> iv_bottom_tab = new ArrayList<>();
    private List<TextView> tv_bottom_tab = new ArrayList<>();
    private NoScrollViewPager vp_study;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        //设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        // 样式代码 开始------------------------------------------------------------
        //获取组件
        findViewById(R.id.ll_bottom_home).setOnClickListener(this);
        findViewById(R.id.ll_bottom_study).setOnClickListener(this);
        findViewById(R.id.ll_bottom_course).setOnClickListener(this);
        findViewById(R.id.ll_bottom_book).setOnClickListener(this);
        findViewById(R.id.ll_bottom_mine).setOnClickListener(this);

        ImageView iv_bottom_home = findViewById(R.id.iv_bottom_home);
        ImageView iv_bottom_study = findViewById(R.id.iv_bottom_study);
        ImageView iv_bottom_course = findViewById(R.id.iv_bottom_course);
        ImageView iv_bottom_book = findViewById(R.id.iv_bottom_book);
        ImageView iv_bottom_mine = findViewById(R.id.iv_bottom_mine);
        iv_bottom_tab.add(iv_bottom_home);
        iv_bottom_tab.add(iv_bottom_study);
        iv_bottom_tab.add(iv_bottom_course);
        iv_bottom_tab.add(iv_bottom_book);
        iv_bottom_tab.add(iv_bottom_mine);

        TextView tv_bottom_home = findViewById(R.id.tv_bottom_home);
        TextView tv_bottom_study = findViewById(R.id.tv_bottom_study);
        TextView tv_bottom_course = findViewById(R.id.tv_bottom_course);
        TextView tv_bottom_book = findViewById(R.id.tv_bottom_book);
        TextView tv_bottom_mine = findViewById(R.id.tv_bottom_mine);
        tv_bottom_tab.add(tv_bottom_home);
        tv_bottom_tab.add(tv_bottom_study);
        tv_bottom_tab.add(tv_bottom_course);
        tv_bottom_tab.add(tv_bottom_book);
        tv_bottom_tab.add(tv_bottom_mine);
        //样式代码 结束------------------------------------------------------------

        vp_study = findViewById(R.id.vp_study);
        StudyPagerAdapter adapter = new StudyPagerAdapter(getSupportFragmentManager());
        vp_study.setAdapter(adapter);
        vp_study.setCurrentItem(0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_bottom_home:
                if(id_bottom_onclick != 0){
                    SwitchTabById(0);
                    vp_study.setCurrentItem(0, false);
                }
                break;
            case R.id.ll_bottom_study:
                if(id_bottom_onclick != 1){
                    SwitchTabById(1);
                    vp_study.setCurrentItem(1, false);
                }
                break;
            case R.id.ll_bottom_course:
                if(id_bottom_onclick != 2){
                    SwitchTabById(2);
                    vp_study.setCurrentItem(2, false);
                }
                break;
            case R.id.ll_bottom_book:
                if(id_bottom_onclick != 3){
                    SwitchTabById(3);
                    vp_study.setCurrentItem(3, false);
                }
                break;
            case R.id.ll_bottom_mine:
                if(id_bottom_onclick != 4){
                    SwitchTabById(4);
                    vp_study.setCurrentItem(4, false);
                }
                break;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void SwitchTabById(int id) {
        //撤销上个焦点
        iv_bottom_tab.get(id_bottom_onclick).setImageResource(iv_bottom_tab_unfocus[id_bottom_onclick]);
        tv_bottom_tab.get(id_bottom_onclick).setTextColor(getResources().getColor(R.color.tabbar_text));

        //获取这个焦点
        iv_bottom_tab.get(id).setImageResource(iv_bottom_tab_focus[id]);
        tv_bottom_tab.get(id).setTextColor(getResources().getColor(R.color.main_bg));
        id_bottom_onclick = id;
    }


}