package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.CustomeMovebutton;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.TMainPagerAdapter;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;
import com.example.yidiantong.fragment.TMainBellFragment;
import com.example.yidiantong.fragment.TMainLatestFragment;
import com.example.yidiantong.fragment.TMainMyFragment;
import com.example.yidiantong.fragment.TMainReportFragment;
import com.example.yidiantong.fragment.TMainTeachFragment;

import java.util.ArrayList;
import java.util.List;

public class TMainPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private int id_bottom_onclick = 0; //标定选择的
    private int[] iv_bottom_tab_focus = {R.drawable.bottom_tab_latest_focus, R.drawable.bottom_tab_report_focus, R.drawable.bottom_tab_teach_focus, R.drawable.bottom_tab_bell_focus, R.drawable.bottom_tab_mine_focus};
    private int[] iv_bottom_tab_unfocus = {R.drawable.bottom_tab_latest_unfocus, R.drawable.bottom_tab_report_unfocus, R.drawable.bottom_tab_teach_unfocus, R.drawable.bottom_tab_bell_unfocus, R.drawable.bottom_tab_mine_unfocus};

    private List<ImageView> iv_bottom_tab = new ArrayList<>();
    private List<TextView> tv_bottom_tab = new ArrayList<>();

    private NoScrollViewPager vp_main;

    //返回拦截
    private long firstTime = 0;
    private TMainLatestFragment latestFragment;
    private TMainTeachFragment teachFragment;
    private TMainReportFragment reportFragment;
    private TMainBellFragment bellFragment;
    private TMainMyFragment myFragment;

    // 悬浮按钮
    private WindowManager wm;
    private WindowManager.LayoutParams wmParams;
    private com.example.yidiantong.View.CustomeMovebutton CustomeMovebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmain_pager);

        findViewById(R.id.ll_bottom_latest).setOnClickListener(this);
        findViewById(R.id.ll_bottom_report).setOnClickListener(this);
        findViewById(R.id.ll_bottom_teach).setOnClickListener(this);
        findViewById(R.id.ll_bottom_bell).setOnClickListener(this);
        findViewById(R.id.ll_bottom_mine).setOnClickListener(this);

        ImageView iv_bottom_latest = findViewById(R.id.iv_bottom_latest);
        ImageView iv_bottom_report = findViewById(R.id.iv_bottom_report);
        ImageView iv_bottom_teach = findViewById(R.id.iv_bottom_teach);
        ImageView iv_bottom_bell = findViewById(R.id.iv_bottom_bell);
        ImageView iv_bottom_mine = findViewById(R.id.iv_bottom_mine);
        iv_bottom_tab.add(iv_bottom_latest);
        iv_bottom_tab.add(iv_bottom_report);
        iv_bottom_tab.add(iv_bottom_teach);
        iv_bottom_tab.add(iv_bottom_bell);
        iv_bottom_tab.add(iv_bottom_mine);

        TextView tv_bottom_latest = findViewById(R.id.tv_bottom_latest);
        TextView tv_bottom_report = findViewById(R.id.tv_bottom_report);
        TextView tv_bottom_teach = findViewById(R.id.tv_bottom_teach);
        TextView tv_bottom_bell = findViewById(R.id.tv_bottom_bell);
        TextView tv_bottom_mine = findViewById(R.id.tv_bottom_mine);
        tv_bottom_tab.add(tv_bottom_latest);
        tv_bottom_tab.add(tv_bottom_report);
        tv_bottom_tab.add(tv_bottom_teach);
        tv_bottom_tab.add(tv_bottom_bell);
        tv_bottom_tab.add(tv_bottom_mine);


        /**
         * ViewPager配置
         */

        latestFragment = new TMainLatestFragment();
        teachFragment = new TMainTeachFragment();
        reportFragment = new TMainReportFragment();
        bellFragment = new TMainBellFragment();
        myFragment = new TMainMyFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.vp_main, latestFragment).commit();

//        vp_main = findViewById(R.id.vp_main);
//        TMainPagerAdapter adapter = new TMainPagerAdapter(getSupportFragmentManager());
//        vp_main.setAdapter(adapter);
//        vp_main.setCurrentItem(0);

        // 显示悬浮按钮
        handler.sendEmptyMessageDelayed(0, 500);

    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.ll_bottom_latest:
                if (id_bottom_onclick != 0) {
                    SwitchTabById(0);
//                    vp_main.setCurrentItem(0, false);
                    ft.replace(R.id.vp_main, latestFragment);
                }
                break;
            case R.id.ll_bottom_report:
                if (id_bottom_onclick != 1) {
                    SwitchTabById(1);
//                    vp_main.setCurrentItem(1, false);
                    ft.replace(R.id.vp_main, reportFragment);
                }
                break;
            case R.id.ll_bottom_teach:
                if (id_bottom_onclick != 2) {
                    SwitchTabById(2);
//                    vp_main.setCurrentItem(2, false);
                    ft.replace(R.id.vp_main, teachFragment);
                }
                break;
            case R.id.ll_bottom_bell:
                if (id_bottom_onclick != 3) {
                    SwitchTabById(3);
//                    vp_main.setCurrentItem(3, false);
                    ft.replace(R.id.vp_main, bellFragment);
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(TMainPagerActivity.this)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent, 1);
                        } else {
                            showMoveButtonView();
                        }
                    }
                    break;
            }
        }
    };

    private void showMoveButtonView() {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        wmParams = ((MyApplication) getApplication()).getMywmParams();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//API Level 26
            wmParams.type=WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
        } else {
            wmParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        wmParams.format= PixelFormat.TRANSLUCENT;//设置背景图片
        wmParams.flags= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;//
        wmParams.gravity = Gravity.LEFT|Gravity.TOP;//
        wmParams.x = widthPixels;  //设置位置像素
        wmParams.y = heightPixels-500;
        wmParams.width=200; //设置图片大小
        wmParams.height=200;
        CustomeMovebutton = new CustomeMovebutton(getApplicationContext());
        CustomeMovebutton.setImageResource(R.drawable.sj_bubble);
        CustomeMovebutton.setBackgroundResource(R.drawable.move_button_bg_un);

        wm.addView(CustomeMovebutton, wmParams);

        CustomeMovebutton.setOnSpeakListener(new CustomeMovebutton.OnSpeakListener() {
            @Override
            public void onSpeakListener() {
                goScanner();
            }
        });
    }

    private void goScanner() {
        Intent intent= new Intent(this, TCourseScannerActivity.class);
        //intent.putExtra("stuname",moreList.get(0).getIntroduction());
        startActivity(intent);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(CustomeMovebutton != null){
//            wm.removeView(CustomeMovebutton);
//        }
//    }
}