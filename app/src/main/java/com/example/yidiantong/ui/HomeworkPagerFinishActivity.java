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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeworkFinishPagerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.HomeworkMarkedEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HomeworkPagerFinishActivity extends AppCompatActivity implements View.OnClickListener, PagingInterface {
    private static final String TAG = "HomeworkPagerFinishActivity";
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
    private TextView tv_content;
    private View contentView = null;
    private List<String> question_types = new ArrayList<>();
    MyArrayAdapter myArrayAdapter = new MyArrayAdapter(this, question_types);
    private PopupWindow window;

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
        } catch (Exception e) {
        }

        /**
         * 其他
         */
        // 顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            this.finish();
        });
        // 顶栏目录
        tv_content = findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);

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
                for (int i = 0; i < list.size(); ++i) {
                    // 顶部目录菜单内容
                    question_types.add((i+1) + "." + list.get(i).getTypeName());
                    // 题面类型
                }
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
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadAnswer_Net() {

        String mRequestUrl = Constant.API + Constant.XUEBA_ANSWER + "?paperId=" + learnPlanId + "&questionId=" + username + "&learnPlanType=paper";

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
            Log.e("volley", "Volley_Error: " + error.toString());

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
        switch (view.getId()) {
            //目录切换作业题面
            case R.id.tv_content:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 180);

                    lv_homework.setAdapter(myArrayAdapter);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //切换页+消除选项口
                            currentItem = i;
                            vp_homework.setCurrentItem(currentItem);
                            window.dismiss();
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        int maxHeight = PxUtils.dip2px(HomeworkPagerFinishActivity.this, 245);
                        // 获取ListView的子项数目
                        int itemCount = lv_homework.getAdapter().getCount();

                        // 计算ListView的高度
                        int listViewHeight = 0;
                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lv_homework.getWidth(), View.MeasureSpec.AT_MOST);

                        for (int i = 0; i < itemCount; i++) {
                            View listItem = lv_homework.getAdapter().getView(i, null, lv_homework);
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                            listViewHeight += listItem.getMeasuredHeight();
                        }

                        // 如果计算出的高度超过最大高度，则设置为最大高度
                        ViewGroup.LayoutParams layoutParams = lv_homework.getLayoutParams();
                        if (listViewHeight > maxHeight) {
                            layoutParams.height = maxHeight;
                        }
                    });


                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(tv_content, -220, 5);
                break;
        }
    }
}