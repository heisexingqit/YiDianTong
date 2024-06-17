package com.example.yidiantong.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.adapter.TLearnPlanPreviewAdapter;
import com.example.yidiantong.bean.LearnPlanActivityEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.LearnPlanLinkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TLearnPlanPreviewActivity extends AppCompatActivity implements View.OnClickListener, PagingInterface, LearnPlanInterface {
    private static final String TAG = "TLeanPlanPreviewActivit";
    private String paperId;
    private String homeworkName;
    private TextView tv_title;
    private ViewPager vp_main;
    // Activity页面核心组件
    private ViewPager vp_homework;
    private TLearnPlanPreviewAdapter adapter;
    private MyArrayAdapter myArrayAdapter;

    // ViewPager页码
    private int currentItem = 0;
    private int pageCount = 0;

    // 相关参数
    private String learnPlanId;
    private boolean isNew;
    private String username;
    private String title;
    String[] stuAnswer;
    String[] oldStuAnswer;
    private List<String> questionIds = new ArrayList<>();
    private List<Integer> questionIdx = new ArrayList<>();


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
    private TextView tv_question_number;

    // Adapter 参数
    List<LearnPlanItemEntity> moreList;
    List<StuAnswerEntity> moreList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlean_plan_preview);
        paperId = getIntent().getStringExtra("paperId");
        homeworkName = getIntent().getStringExtra("homeworkName");
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());
        rl_loading = findViewById(R.id.rl_loading);
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(homeworkName);
        tv_question_number = findViewById(R.id.tv_question_number);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);
        vp_main = findViewById(R.id.vp_main);
        adapter = new TLearnPlanPreviewAdapter(getSupportFragmentManager(), learnPlanId);

        vp_main.setAdapter(adapter);

        myArrayAdapter = new MyArrayAdapter(this, topArrayItem);

        loadItems_Net();
    }

    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.LEARNPLAN_ITEM + "?learnPlanId=" + paperId + "&deviceType=PHONE";
        Log.d("wen", "导学案资源URL: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getJSONObject("json").getString("data");
                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<LearnPlanLinkEntity> itemList = gson.fromJson(itemString, new TypeToken<List<LearnPlanLinkEntity>>() {
                }.getType());
                moreList = new ArrayList<>();
                int sumCount = 0;
                int allCount = 0;
                int idxCount = 0;
                // 遍历处理数据
                for (LearnPlanLinkEntity item : itemList) {
                    // 顶部根名称
                    topArrayItem.add(item.getLink());
                    List<LearnPlanActivityEntity> list2 = item.getActivityList();
                    topPagerIdx.put(allCount, -1);
                    allCount++;
                    for (LearnPlanActivityEntity item2 : list2) {
                        // 顶部儿子名称
                        topArrayItem.add("  " + item2.getActivityName());
                        topPagerIdx.put(allCount, -1);
                        allCount++;
                        List<LearnPlanItemEntity> list3 = item2.getResourceList();
                        sumCount += list3.size();
                        moreList.addAll(list3);
                        for (int i = 0; i < list3.size(); ++i) {
                            LearnPlanItemEntity item3 = list3.get(i);
                            // 顶部孙子名称
                            topArrayItem.add("    " + (i + 1) + "." + item3.getResourceName());
                            topPagerIdx.put(allCount, idxCount);
                            if (item3.getResourceType().equals("01")) {
                                questionIds.add(item3.getResourceId());
                                questionIdx.add(idxCount);
                            }
                            allCount++;
                            idxCount++;
                        }
                    }
                }
                pageCount = sumCount;
                adapter.update(moreList);
                setTopNum();
                rl_loading.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

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
                            if (topPagerIdx.get(i) != -1) {
                                Log.d(TAG, "onItemClick: " + topPagerIdx.get(i));
                                currentItem = topPagerIdx.get(i);
                                vp_main.setCurrentItem(currentItem);
                                setTopNum();
                                window.dismiss();
                            }
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        int maxHeight = PxUtils.dip2px(TLearnPlanPreviewActivity.this, 300);
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
        }
    }

    @Override
    public void setStuAnswer(int pos, String StuAnswer) {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void offLoading() {

    }

    @Override
    public void uploadTime(long timeLong) {

    }

    @Override
    public void pageLast() {
        if (currentItem == 0) {
            Toast.makeText(this, "已经是第一页", Toast.LENGTH_SHORT).show();
        } else {
            currentItem -= 1;
            vp_main.setCurrentItem(currentItem);
            setTopNum();
        }
    }

    @Override
    public void pageNext() {
        if (currentItem == pageCount - 1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("已经是最后一页");
            builder.setPositiveButton("确定", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem += 1;
            vp_main.setCurrentItem(currentItem);
            setTopNum();
        }
    }

    private void setTopNum() {
        int positionLen = String.valueOf(currentItem + 1).length();
        String questionNum = (currentItem + 1) + "/" + pageCount;
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);
    }
}