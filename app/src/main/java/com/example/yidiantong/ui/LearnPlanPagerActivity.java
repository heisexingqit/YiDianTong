package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.LearnPlanPagerAdapter;
import com.example.yidiantong.adapter.MyArrayLearnPlanAdapter;
import com.example.yidiantong.bean.LearnPlanActivityEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.LearnPlanLinkEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnPlanPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LearnPlanPagerActivity";

    // Activity页面核心组件
    private ViewPager vp_homework;
    private LearnPlanPagerAdapter adapter;
    private MyArrayLearnPlanAdapter myArrayAdapter;
    private List<LearnPlanItemEntity> learnPlanList = new ArrayList<>();

    // ViewPager页码
    private int currentItem = 0;
    private int pageCount = 0;

    // 相关参数
    private String learnPlanId;
    private boolean isNew;
    private String title;

    // 顶部组件
    private TextView tv_content;
    private View contentView;
    private PopupWindow window;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private List<String> topArrayItem = new ArrayList<>();
    private Map<Integer, Integer> topPagerIdx = new HashMap<>();

    // 加载+遮蔽
    private RelativeLayout rl_submitting;
    private RelativeLayout rl_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_plan_pager);

        //获取Intent参数
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        TextView tv_title = findViewById(R.id.tv_title);
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        isNew = getIntent().getBooleanExtra("isNew", true);

        //ViewPager适配器设置
        vp_homework = findViewById(R.id.vp_homework);
        adapter = new LearnPlanPagerAdapter(getSupportFragmentManager(), learnPlanId, learnPlanList);
        vp_homework.setAdapter(adapter);

        //滑动监听器
        vp_homework.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //翻页同步下标
                currentItem = position;
                Log.e("currentItem",":"+currentItem);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(vp_homework.getContext(),
                    new AccelerateInterpolator());
            field.set(vp_homework, scroller);
            scroller.setmDuration(400);
        } catch (Exception e) {}

        vp_homework.setCurrentItem(currentItem);

        // 顶栏相关
        myArrayAdapter = new MyArrayLearnPlanAdapter(this, topArrayItem);
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            this.finish();
        });

        // 顶栏目录
        tv_content = findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);

        // 顶栏眼睛
        findViewById(R.id.iv_eye).setOnClickListener(this);

        // 数据请求
        loadItems_Net();

        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    int index = intent.getIntExtra("currentItem", 0);
                    if (index == -1) {
                        Toast.makeText(LearnPlanPagerActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        currentItem = index;
                        vp_homework.setCurrentItem(currentItem);
                    }
                }
            }
        });

        // 遮蔽
        rl_submitting = findViewById(R.id.rl_submitting);
        TextView tv_submitting = findViewById(R.id.tv_submitting);
        tv_submitting.setText("图片提交中...");

        // 加载
        rl_loading = findViewById(R.id.rl_loading);
    }

    //跳转至提交作业页面
    private void jumpToSubmitPage() {
//        Intent intent = new Intent(HomeworkPagerActivity.this, HomeworkSubmitActivity.class);
//        intent.putExtra("title", title);
//        intent.putExtra("stuAnswer", stuAnswer);
//        intent.putExtra("learnPlanId", learnPlanId);
//        intent.putExtra("username", username);
//        intent.putExtra("questionIds", questionIds);
//        intent.putExtra("questionTypes", question_types_array);
//        intent.putExtra("isNew", isNew);
//        mResultLauncher.launch(intent);
    }

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
                List<LearnPlanLinkEntity> list = (List<LearnPlanLinkEntity>) message.obj;
                int sumCount = 0;
                int allCount = 0;
                int idxCount = 0;
                // 遍历处理数据
                for (LearnPlanLinkEntity item: list) {
                    // 顶部根名称
                    topArrayItem.add(item.getLink());
                    List<LearnPlanActivityEntity> list2 = item.getActivityList();
                    topPagerIdx.put(allCount, -1);
                    allCount++;
                    for(LearnPlanActivityEntity item2: list2){
                        // 顶部儿子名称
                        topArrayItem.add("  " + item2.getActivityName());
                        topPagerIdx.put(allCount, -1);
                        allCount++;
                        List<LearnPlanItemEntity> list3 = item2.getResourceList();

                        sumCount += list3.size();
                        for(int i = 0; i < list3.size(); ++i){
                            // 顶部孙子名称
                            topArrayItem.add("    " + (i+1) + "." + list3.get(i).getResourceName());
                            topPagerIdx.put(allCount, idxCount);
                            allCount ++;
                            idxCount ++;
                        }
                    }
                }
                pageCount = sumCount;
                rl_loading.setVisibility(View.GONE);
            }
        }
    };

    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.LEARNPLAN_ITEM + "?learnPlanId=" + learnPlanId + "&deviceType=PHONE";
        Log.d(TAG, "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getJSONObject("json").getString("data");
                Log.d(TAG, "loadItems_Net-Data: " + itemString);
                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<LearnPlanLinkEntity> itemList = gson.fromJson(itemString, new TypeToken<List<LearnPlanLinkEntity>>() {
                }.getType());
                Log.d(TAG, "loadItems_Net-List: " + itemList);

                // 封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //目录切换作业题面
            case R.id.tv_content:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_learn_plan, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);

                    lv_homework.setAdapter(myArrayAdapter);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //切换页+消除选项口
                            if(topPagerIdx.get(i) != -1){
                                Log.d(TAG, "onItemClick: " + topPagerIdx.get(i));
                                currentItem = topPagerIdx.get(i);
                                vp_homework.setCurrentItem(currentItem);
                                window.dismiss();
                            }
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        int maxHeight = PxUtils.dip2px(LearnPlanPagerActivity.this, 245);
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
                        Log.d(TAG, "listViewHeight: " + listViewHeight);
                        Log.d(TAG, "maxHeight: " + maxHeight);

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
            case R.id.iv_eye:
                jumpToSubmitPage();
        }
    }
}