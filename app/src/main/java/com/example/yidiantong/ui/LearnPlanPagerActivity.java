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
import android.text.SpannableString;
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
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.LearnPlanActivityEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.LearnPlanLinkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.MyReadWriteLock;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LearnPlanPagerActivity extends AppCompatActivity implements View.OnClickListener, PagingInterface, LearnPlanInterface {

    private static final String TAG = "LearnPlanPagerActivity";

    // Activity页面核心组件
    private ViewPager vp_homework;
    private LearnPlanPagerAdapter adapter;
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
        setContentView(R.layout.activity_learn_plan_pager);

        // 获取Intent参数
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        TextView tv_title = findViewById(R.id.tv_title);
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        username = getIntent().getStringExtra("username");
        isNew = getIntent().getBooleanExtra("isNew", true);

        //ViewPager适配器设置
        vp_homework = findViewById(R.id.vp_homework);
        adapter = new LearnPlanPagerAdapter(getSupportFragmentManager(), learnPlanId);
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
                Log.e("currentItem", ":" + currentItem);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        vp_homework.setCurrentItem(currentItem);

        // 顶栏相关
        myArrayAdapter = new MyArrayAdapter(this, topArrayItem);
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
                        setTopNum();
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

        tv_question_number = findViewById(R.id.tv_question_number);

    }

    @Override
    public void pageLast() {
        if (currentItem == 0) {
            Toast.makeText(this, "已经是第一页", Toast.LENGTH_SHORT).show();
        } else {
            uploadQuestion();
            currentItem -= 1;
            vp_homework.setCurrentItem(currentItem);
            setTopNum();
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
            tv.setText("已经最后一页，确定要提交导学案？");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    jumpToSubmitPage();
                }
            });
            builder.setNegativeButton("取消", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            uploadQuestion();
            currentItem += 1;
            vp_homework.setCurrentItem(currentItem);
            setTopNum();
        }
    }

    private void setTopNum() {
        int positionLen = String.valueOf(currentItem + 1).length();
        String questionNum = (currentItem + 1) + "/" + pageCount;
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);
    }

    //跳转至提交作业页面
    private void jumpToSubmitPage() {
        uploadQuestion();
        Intent intent = new Intent(LearnPlanPagerActivity.this, LearnPlanSubmitActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("stuAnswer", stuAnswer);
        intent.putExtra("type", getIntent().getStringExtra("type"));
        intent.putExtra("learnPlanId", learnPlanId);
        intent.putExtra("username", username);
        intent.putExtra("questionIds", (Serializable) questionIds);
        intent.putExtra("questionIdx", (Serializable) questionIdx);
        intent.putExtra("isNew", isNew);
        mResultLauncher.launch(intent);
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
                moreList = new ArrayList<>();
                List<LearnPlanLinkEntity> list = (List<LearnPlanLinkEntity>) message.obj;
                int sumCount = 0;
                int allCount = 0;
                int idxCount = 0;
                // 遍历处理数据
                for (LearnPlanLinkEntity item : list) {
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

                stuAnswer = new String[moreList.size()];
                oldStuAnswer = new String[moreList.size()];
                pageCount = sumCount;
                setTopNum();
                if (moreList2 != null) {
                    adjustStuAnswerList();
                    adapter.update(moreList, moreList2);
                    rl_loading.setVisibility(View.GONE);
                }
            } else if (message.what == 101) {
                /**
                 * 学生作答信息
                 */
                moreList2 = (List<StuAnswerEntity>) message.obj;
                if (moreList != null && moreList.size() > 0) {
                    Log.d("wen", "handleMessage: 作答内容请求完毕");
                    adjustStuAnswerList();
                    adapter.update(moreList, moreList2);
                    rl_loading.setVisibility(View.GONE);
                }
            }
        }
    };

    private void adjustStuAnswerList() {
        for (int i = 0; i < moreList.size(); ++i) {
            LearnPlanItemEntity item = moreList.get(i);
            if (moreList2.size() <= i || !moreList2.get(i).getQuestionId().equals(item.getResourceId())) {
                moreList2.add(i, new StuAnswerEntity(i + 1));
            }
        }
        for (int i = 0; i < moreList2.size(); ++i) {
            if (questionIdx.contains(i)) {
                // 学生作答内容
                stuAnswer[i] = moreList2.get(i).getStuAnswer();
                oldStuAnswer[i] = stuAnswer[i];
            } else {
                stuAnswer[i] = moreList.get(i).getResourceName();
            }
        }
    }

    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.LEARNPLAN_ITEM + "?learnPlanId=" + learnPlanId + "&deviceType=PHONE";
        Log.d("wen", "导学案资源URL: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getJSONObject("json").getString("data");
                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<LearnPlanLinkEntity> itemList = gson.fromJson(itemString, new TypeToken<List<LearnPlanLinkEntity>>() {
                }.getType());
                Log.d("wen", "Gson处理后: " + itemList);

                // 封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);

        // 学生答题情况
        mRequestUrl = Constant.API + Constant.LEARNPLAN_ANSWER_ITEM + "?learnPlanId=" + learnPlanId + "&userName=" + username;
        Log.d("wen", "导学案答题URL: " + mRequestUrl);
        request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<StuAnswerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<StuAnswerEntity>>() {
                }.getType());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = itemList;
                // 发送消息给主线程
                // 标识线程
                message.what = 101;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
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
                                vp_homework.setCurrentItem(currentItem);
                                setTopNum();
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

    //pos是接口的order属性（1...n）因此要
    @Override
    public void setStuAnswer(int pos, String stuStr) {

        if(stuAnswer != null && stuAnswer.length >= pos){
            stuAnswer[pos - 1] = stuStr;
        }

    }

    @Override
    public void onLoading() {
        rl_submitting.setVisibility(View.VISIBLE);
    }

    @Override
    public void offLoading() {
        rl_submitting.setVisibility(View.GONE);
    }

    @Override
    public void uploadTime(long timeLong) {
        Log.e("wen0610", "uploadTime: " );

        int pos = vp_homework.getCurrentItem();
        String mRequestUrl = Constant.API + Constant.LEARNPLAN_SUBMIT_TIME + "?learnPlanId=" + learnPlanId + "&contentId=" + adapter.itemList.get(pos).getResourceId() +
                "&userName=" + username + "&useTime=" + timeLong;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
//            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });

        MyApplication.addRequest(request, TAG);
    }

    // 上传试题
    private void uploadQuestion() {
        int pos = vp_homework.getCurrentItem();
        if (stuAnswer[pos] == null || stuAnswer[pos].equals(oldStuAnswer[pos]) || !questionIdx.contains(pos)) {
            return;
        }
        Log.e("wen0610", "uploadQuestion: " + pos);

        String mRequestUrl = null;
        try {
            mRequestUrl = Constant.API + Constant.LEARNPLAN_SUBMIT_QUS_ITEM + "?learnPlanId=" + learnPlanId + "&learnPlanName=" + URLEncoder.encode(title, "UTF-8") +
                    "&userName=" + username + "&questionId=" + adapter.itemList.get(pos).getResourceId() + "&answer=" + URLEncoder.encode(stuAnswer[pos], "UTF-8") + "&status=" + (isNew ? 1 : 3);
            Log.d(TAG, "submit: " + mRequestUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
//            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });

        MyApplication.addRequest(request, TAG);

        // 同步更新
        oldStuAnswer[pos] = stuAnswer[pos];
    }

    @Override
    public void onBackPressed() {
        uploadQuestion();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 读写解锁
        MyReadWriteLock.checkout(username, this);
    }
}