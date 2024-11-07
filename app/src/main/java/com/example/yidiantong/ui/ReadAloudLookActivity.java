package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.adapter.ReadAloudLookPagerAdapter;
import com.example.yidiantong.bean.ReadTaskEntity;
import com.example.yidiantong.fragment.ReadAloudLookFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.HomeworkInterface2;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReadAloudLookActivity extends AppCompatActivity implements View.OnClickListener, PagingInterface, HomeworkInterface2 {
    private static final String TAG = "ReadAloudLookActivity";
    // Activity页面核心组件
    private ViewPager vp_homework;
    private ReadAloudLookPagerAdapter adapter;

    // ViewPager页码
    private int currentItem = 0;
    private int pageCount = 0;

    // 相关参数
    private String learnPlanId;
    private String[] questionIds;
    private boolean isNew;
    private String username;
    private String[] stuAnswer;
    private String title;

    // 顶部组件
    private String[] question_types_array;
    private View contentView = null;
    private PopupWindow window;
    private TextView tv_content;
    private List<String> question_types = new ArrayList<>();
    MyArrayAdapter myArrayAdapter = new MyArrayAdapter(this, question_types);
    private ActivityResultLauncher<Intent> mResultLauncher;

    // 加载+遮蔽
    private RelativeLayout rl_submitting;
    private TextView tv_submitting;
    private RelativeLayout rl_loading;

    // 老答案
    private String[] oldStuAnswer;

    private int count = 0;
    private TextView tv_current;
    private TextView tv_all;
    private TextView tv_title;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_aloud_look);

        //获取Intent参数
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        username = getIntent().getStringExtra("username");
        isNew = getIntent().getBooleanExtra("isNew", true);
        title = getIntent().getStringExtra("title");

        // findViewById
        tv_current = findViewById(R.id.tv_current);
        tv_all = findViewById(R.id.tv_all);
        rl_loading = findViewById(R.id.rl_loading);
        rl_submitting = findViewById(R.id.rl_submitting);
        tv_submitting = findViewById(R.id.tv_submitting);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(title);
        // ViewPager适配器设置
        vp_homework = findViewById(R.id.vp_homework);
        vp_homework.setOffscreenPageLimit(0); // 禁止预加载

        adapter = new ReadAloudLookPagerAdapter(getSupportFragmentManager());
        vp_homework.setAdapter(adapter);
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
        //滑动监听器
        vp_homework.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //翻页同步下标
                currentItem = position;
                tv_current.setText(String.valueOf(currentItem + 1));
                MyApplication.currentItem = currentItem;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Log.e(TAG, "currentItem: " + MyApplication.currentItem);
        vp_homework.setCurrentItem(currentItem);


        // 顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            this.finish();
        });

        loadItems_Net(); // 数据加载

        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    int index = intent.getIntExtra("currentItem", 0);
                    if (index == -1) {
                        Intent toHome = new Intent(ReadAloudLookActivity.this, MainPagerActivity.class);
                        toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(toHome);
                    } else {
                        currentItem = index;
                        vp_homework.setCurrentItem(currentItem, false);
                    }
                }
            }
        });

    }

    private List<ReadTaskEntity> readTaskList;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
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

    //Fragment调用Activity方法， 实现Fragment的接口
    @Override
    public void pageNext() {
        if (currentItem == pageCount - 1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经最后一页");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            // 上传答案
            currentItem += 1;
            vp_homework.setCurrentItem(currentItem);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what){
                case 100:
                    readTaskList = (List<ReadTaskEntity>) message.obj;
                    for (ReadTaskEntity task : readTaskList) {
                        task.recordId = learnPlanId;
                        task.isNew = isNew;
                    }
                    adapter.update(readTaskList);
                    pageCount = readTaskList.size();
                    tv_all.setText(String.valueOf(pageCount));
                    rl_loading.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void loadItems_Net() {
        // 跟读作业列表
        String mRequestUrl = Constant.API + Constant.GET_READ_TASK_INFO + "?recordId=" + learnPlanId;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<ReadTaskEntity> itemList = gson.fromJson(itemString, new TypeToken<List<ReadTaskEntity>>() {}.getType());
                if(itemList == null || itemList.size() == 0) {
                    rl_loading.setVisibility(View.GONE);
                    Toast.makeText(this, "图片数据出错", Toast.LENGTH_SHORT).show();
                    return;
                }
                Message msg = new Message();
                msg.what = 100;
                msg.obj = itemList;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
                rl_loading.setVisibility(View.GONE);
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
            rl_loading.setVisibility(View.GONE);
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void jumpToSubmit() {
        Intent intent = new Intent(this, ReadAloudSubmitActivity.class);
        intent.putExtra("recordId", learnPlanId);
        intent.putExtra("pos", currentItem);
        intent.putExtra("isNew", isNew);
        List<String> imageList = new ArrayList<>();
        for(ReadTaskEntity task : readTaskList){
            imageList.add(task.imageId);
        }
        mResultLauncher.launch(intent);
    }

    @Override
    public void onLoading(String tip) {
        tv_submitting.setText(tip);
        rl_submitting.setVisibility(View.VISIBLE);
    }

    @Override
    public void offLoading() {
        rl_submitting.setVisibility(View.GONE);
    }

    @Override
    public void refreshData() {

    }

}