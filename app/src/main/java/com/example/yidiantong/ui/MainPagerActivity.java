package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.yidiantong.R;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.MainPagerAdapter;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private int id_bottom_onclick = 0; //标定选择的
    private int[] iv_bottom_tab_focus = {R.drawable.bottom_tab_home_focus, R.drawable.bottom_tab_study_focus, R.drawable.bottom_tab_course_focus, R.drawable.bottom_tab_book_focus, R.drawable.bottom_tab_mine_focus};
    private int[] iv_bottom_tab_unfocus = {R.drawable.bottom_tab_home_unfocus, R.drawable.bottom_tab_study_unfocus, R.drawable.bottom_tab_course_unfocus, R.drawable.bottom_tab_book_unfocus, R.drawable.bottom_tab_mine_unfocus};
    private List<ImageView> iv_bottom_tab = new ArrayList<>();
    private List<TextView> tv_bottom_tab = new ArrayList<>();
    private NoScrollViewPager vp_main;

    //返回拦截
    private long firstTime = 0;
    private MainHomeFragment homeFragment;
    private MainStudyFragment studyFragment;
    private MainCourseFragment courseFragment;
    private MainBookFragment bookFragment;
    private MainMyFragment myFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pager);

        // 样式代码 ----------------------开始------------------------
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
        // 样式代码 ---------------------------结束-------------------------

        /**
         * ViewPager配置
         */

        homeFragment = new MainHomeFragment();
        studyFragment = new MainStudyFragment();
        courseFragment = new MainCourseFragment();
        bookFragment = new MainBookFragment();
        myFragment = new MainMyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.vp_main, homeFragment).commit();

//        vp_main = findViewById(R.id.vp_main);
//        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
//        vp_main.setAdapter(adapter);
//        vp_main.setCurrentItem(0);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.ll_bottom_home:
                if (id_bottom_onclick != 0) {
                    SwitchTabById(0);
//                    vp_main.setCurrentItem(0, false);
                    ft.replace(R.id.vp_main, homeFragment);
                }
                break;
            case R.id.ll_bottom_study:
                if (id_bottom_onclick != 1) {
                    SwitchTabById(1);
//                    vp_main.setCurrentItem(1, false);
                    ft.replace(R.id.vp_main, studyFragment);
                }
                break;
            case R.id.ll_bottom_course:
                if (id_bottom_onclick != 2) {
                    SwitchTabById(2);
//                    vp_main.setCurrentItem(2, false);
                    ft.replace(R.id.vp_main, courseFragment);
                }
                break;
            case R.id.ll_bottom_book:
                if (id_bottom_onclick != 3) {
                    SwitchTabById(3);
//                    vp_main.setCurrentItem(3, false);
                    ft.replace(R.id.vp_main, bookFragment);
                }
                break;
            case R.id.ll_bottom_mine:
                if (id_bottom_onclick != 4) {
                    SwitchTabById(4);
//                    vp_main.setCurrentItem(4, false);
                    ft.replace(R.id.vp_main, myFragment);
                }
                break;
            default:
                break;
        }
        ft.commit();
    }

    @SuppressLint("ResourceAsColor")
    private void SwitchTabById(int id) {
        //撤销上个焦点
        iv_bottom_tab.get(id_bottom_onclick).setImageResource(iv_bottom_tab_unfocus[id_bottom_onclick]);
        tv_bottom_tab.get(id_bottom_onclick).setTextColor(getResources().getColor(R.color.tabbar_text_unselect));

        //获取这个焦点
        iv_bottom_tab.get(id).setImageResource(iv_bottom_tab_focus[id]);
        tv_bottom_tab.get(id).setTextColor(getResources().getColor(R.color.main_bg));
        id_bottom_onclick = id;
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }
}