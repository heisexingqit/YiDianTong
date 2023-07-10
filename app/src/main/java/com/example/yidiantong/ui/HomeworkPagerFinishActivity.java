package com.example.yidiantong.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeworkFinishPagerAdapter;
import com.example.yidiantong.bean.HomeworkMarkedEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

public class HomeworkPagerFinishActivity extends AppCompatActivity implements View.OnClickListener, PagingInterface {
    private static final String TAG = "HomeworkPagerFinishActi";
    // Intent传入参数
    private String learnPlanId;
    private String title;
    private String username;

    // ViewPager相关
    private ViewPager vp_homework;
    private HomeworkFinishPagerAdapter adapter;
    private int currentItem = 0;
    private RelativeLayout rl_loading;

    // 页面信息
    private int pageCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_pager_finish);

        /**
         * 获取传入Intent参数
         */
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        title = getIntent().getStringExtra("title");
        username = getIntent().getStringExtra("username");

        /**
         * 获取页面组件
         */
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);

        /**
         * ViewPager相关
         */
        vp_homework = findViewById(R.id.vp_homework);
        adapter = new HomeworkFinishPagerAdapter(getSupportFragmentManager());
        vp_homework.setAdapter(adapter);

        // 滑动监听器
        vp_homework.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //翻页同步下标
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        vp_homework.setCurrentItem(currentItem);

        // ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(vp_homework.getContext(),
                    new AccelerateInterpolator());
            field.set(vp_homework, scroller);
            scroller.setmDuration(400);
        } catch (Exception e) {}

        /**
         * 其他
         */
        // 顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            this.finish();
        });

        // 数据请求
        loadItems_Net();

        // 加载遮蔽页面
        rl_loading = findViewById(R.id.rl_loading);
    }

    /**
     * 请求处理
     */
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                /**
                 * 题面信息
                 */
                List<HomeworkMarkedEntity> list = (List<HomeworkMarkedEntity>) message.obj;
                Log.d("wen", "批改信息: " + list.toString());
                rl_loading.setVisibility(View.GONE);
                adapter.update(list);

                pageCount = list.size();
            }
        }
    };

    /**
     * 请求批改信息
     */
    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.AFTER_MARKED + "?learnPlanId=" + learnPlanId + "&userName=" + username + "&learnPlanType=paper";
        Log.d("wen", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<HomeworkMarkedEntity> itemList = gson.fromJson(itemString, new TypeToken<List<HomeworkMarkedEntity>>() {
                }.getType());
                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = itemList;
                // 发送消息给主线程

                //标识线程
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(HomeworkPagerFinishActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void pageLast() {
        if (currentItem == 0) {
            Toast.makeText(this, "已经是第一题", Toast.LENGTH_SHORT).show();
        } else {
            currentItem -= 1;
            vp_homework.setCurrentItem(currentItem);
        }
    }

    @Override
    public void pageNext() {
        if (currentItem == pageCount - 1) {
            Toast.makeText(this, "已经是最后一题", Toast.LENGTH_SHORT).show();
        } else {
            currentItem += 1;
            vp_homework.setCurrentItem(currentItem);
        }
    }

    @Override
    public void onClick(View view) {

    }
}