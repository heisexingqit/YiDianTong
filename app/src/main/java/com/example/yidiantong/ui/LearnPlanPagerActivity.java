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
import com.example.yidiantong.bean.LearnPlanAnswerBatchEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.LearnPlanLinkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.entity.HomeworkStuAnswerInfo;
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
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    List<StuAnswerEntity> moreList2 = new ArrayList<>();

    private  ArrayList<LearnPlanAnswerBatchEntity> stuAnswerBatchList = new ArrayList<>();

    // 设置数据加载方式
    private static final int LOAD_FROM_NET = 1;
    private static final int LOAD_FROM_DB = 2;
    private int loadFrom = 0;

    // 计时器
    private long currentTimeMillis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_plan_pager);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
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
                tv_question_number.setText(String.valueOf(currentItem + 1));
                MyApplication.currentItem = currentItem;
                currentTimeMillis = System.currentTimeMillis();
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

        // 20240906 修改
        countReady = 0;
        // 双数据请求
        loadFrom = LOAD_FROM_NET;
        List<HomeworkStuAnswerInfo> homeworkStuAnswerInfos = MyApplication.database.homeworkStuAnswerDao().queryByUserAndHomework(username, learnPlanId);
        /*
         * public class HomeworkStuAnswerInfo {
         *     @NonNull
         *     public String userId; // 学生id
         *     @NonNull
         *     public String homeworkId; // 作业id
         *     @NonNull
         *     public String questionId; // 试题id
         *     public String stuAnswer; // 学生作答
         *     public String userName; // 学生名称
         *     public int order; // 题目顺序
         *     public String updateDate; // 更新日期
         *     // new params
         *     public String answerTime;
         *     public String useTime;
         * }
         */

        moreList2 = new ArrayList<>();
        for (HomeworkStuAnswerInfo info : homeworkStuAnswerInfos) {
            StuAnswerEntity entity = new StuAnswerEntity(info.order, info.questionId, "", info.stuAnswer, "", info.answerTime, info.useTime);
            entity.setId(info.questionId); // LearnPlan 中的配置名不同，不是questinId，而是id
            entity.setOptionTime(info.answerTime); // LearnPlan 中的配置名不同，answerTime，而是optionTime
            /*
             * public class StuAnswerEntity implements Serializable {
             *     private int order;
             *     private String questionId;
             *     private String status;
             *     private String stuAnswer;
             *     private String teaScore;
             *     // new params
             *     private String answerTime;
             *     private String useTime;
             * }
             */

            moreList2.add(entity);
        }

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
                        Intent toHome = new Intent(LearnPlanPagerActivity.this, MainPagerActivity.class);
                        toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(toHome);
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
        intent.putExtra("stuAnswerBatchList", stuAnswerBatchList);
        mResultLauncher.launch(intent);
    }

    private int countReady = 0;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            // 设计两请求锁，仅当两个请求都完成时才进行adapter更新
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
                countReady += 1;
            } else if (message.what == 101) {
                /**
                 * 学生作答信息
                 */
                moreList2 = (List<StuAnswerEntity>) message.obj;

                countReady += 1;
            }
            // 页面显示
            if (countReady >= 2) {
                adjustStuAnswerList();
                adapter.update(moreList, moreList2);
                rl_loading.setVisibility(View.GONE);
                if (MyApplication.isRotate) {
                    vp_homework.setCurrentItem(MyApplication.currentItem, false);
                    MyApplication.isRotate = false;
                }
            }
        }
    };

    private void adjustStuAnswerList() {
        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(day);
        // 学生作答列表moreList2不包含多媒体，需要补充
        for (int i = 0; i < moreList.size(); ++i) {
            LearnPlanItemEntity item = moreList.get(i);
            if (moreList2.size() <= i || moreList2.get(i).getOrder() > i + 1) {
                // 作答列表中缺少某个多媒体导致不对应，添加一个多媒体的空数据
                StuAnswerEntity newItem = new StuAnswerEntity(i + 1, item.getResourceId(), "", "", "", "", "0");
                moreList2.add(i, newItem);
            }
        }
        for (int i = 0; i < moreList2.size(); ++i) {
            String type = "res";
            if(questionIdx.contains(i)){
                type = "que";
            }

            if ("que".equals(type)) {
                // 学生作答内容
                stuAnswer[i] = moreList2.get(i).getStuAnswer();
                oldStuAnswer[i] = stuAnswer[i];
            } else {
                stuAnswer[i] = moreList.get(i).getResourceName();
            }

            // 初次写入数据库
            if(loadFrom == LOAD_FROM_NET) {
                HomeworkStuAnswerInfo info = new HomeworkStuAnswerInfo(username, learnPlanId, moreList2.get(i).getId(), moreList2.get(i).getStuAnswer(), MyApplication.cnName, i + 1, date);
                info.type = type;
                if (moreList2.get(i).getOptionTime() != null && moreList2.get(i).getOptionTime().length() > 0) {
                    info.answerTime = moreList2.get(i).getOptionTime();// TODO 可能接口中有值moreList2
                }
                if (moreList2.get(i).getUseTime() != null && moreList2.get(i).getUseTime().length() > 0) {
                    info.useTime = moreList2.get(i).getUseTime();// TODO 可能接口中有值moreList2
                }
                MyApplication.database.homeworkStuAnswerDao().insert(info);
            }
            // 20240906作答信息初始化同步
            LearnPlanAnswerBatchEntity batchInfo = new LearnPlanAnswerBatchEntity(moreList2.get(i).getId(), type, i + 1, moreList2.get(i).getStuAnswer());
            if(moreList2.get(i).getOptionTime() != null && moreList2.get(i).getOptionTime().length() > 0){
                batchInfo.setOptionTime(moreList2.get(i).getOptionTime());// TODO 可能接口中有值moreList2
            }
            if(moreList2.get(i).getUseTime() != null && moreList2.get(i).getUseTime().length() > 0){
                batchInfo.setUseTime(moreList2.get(i).getUseTime());// TODO 可能接口中有值moreList2
            }
            stuAnswerBatchList.add(batchInfo);
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


        // 数据库加载
        if (moreList2.size() > 0) {
            loadFrom = LOAD_FROM_DB;
            Log.e("0130", "loadItems_Net: 数据库读取");
            Message message = Message.obtain();
            message.obj = moreList2;
            message.what = 101;
            handler.sendMessage(message);
            return;
        }

        // 学生答题情况
        mRequestUrl = Constant.API + Constant.LEARNPLAN_ANSWER_ITEM_NEW + "?learnPlanId=" + learnPlanId + "&userName=" + username;
        Log.d("wen", "导学案答题URL: " + mRequestUrl);
        request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<StuAnswerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<StuAnswerEntity>>() {
                }.getType());

                // 遍历 itemList 并修改 stuAnswer 为 "未答" 的条目
                for (StuAnswerEntity item : itemList) {
                    if ("未答".equals(item.getStuAnswer())) {
                        item.setStuAnswer("");  // 将 "未答" 改为 ""
                    }
                }

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

    @Override
    protected void onResume() {
        super.onResume();
        currentTimeMillis = System.currentTimeMillis();
    }

    //pos是接口的order属性（1...n）因此要
    @Override
    public void setStuAnswer(int pos, String stuStr) {

        if(stuAnswer != null && stuAnswer.length >= pos){
            stuAnswer[pos - 1] = stuStr;
            moreList2.get(pos - 1).setStuAnswer(stuStr);
            // 20240906 同步答案
            stuAnswerBatchList.get(pos - 1).setStuAnswer(stuStr);
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
//        Log.e("wen0610", "uploadTime: " );
//
//        int pos = vp_homework.getCurrentItem();
//        String mRequestUrl = Constant.API + Constant.LEARNPLAN_SUBMIT_TIME + "?learnPlanId=" + learnPlanId + "&contentId=" + adapter.itemList.get(pos).getResourceId() +
//                "&userName=" + username + "&useTime=" + timeLong;
//
//        StringRequest request = new StringRequest(mRequestUrl, response -> {
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
////            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            Log.d("wen", "Volley_Error: " + error.toString());
//        });
//
//        MyApplication.addRequest(request, TAG);
    }

    // 上传试题
    private void uploadQuestion() {
        int pos = currentItem;
        // 做题时间数据必须要更新，所以取消判断
//        if (stuAnswer[pos] == null || stuAnswer[pos].equals(oldStuAnswer[pos]) || !questionIdx.contains(pos)) {
//            return;
//        }
        Log.e("wen0610", "uploadQuestion: " + pos);
        // 计算做题用时
        long useTime = Long.parseLong(stuAnswerBatchList.get(pos).getUseTime()) + System.currentTimeMillis() - currentTimeMillis;
        stuAnswerBatchList.get(pos).setUseTime(String.valueOf(useTime / 1000));

        // 计算切题时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date day = new Date();
        String date = sdf.format(day);
        stuAnswerBatchList.get(pos).setOptionTime(date);
        /*
        public class HomeworkStuAnswerInfo {
            @NonNull
            public String userId; // 学生id
            @NonNull
            public String homeworkId; // 作业id
            @NonNull
            public String questionId; // 试题id
            public String stuAnswer; // 学生作答
            public String userName; // 学生名称
            public int order; // 题目顺序
            public String updateDate; // 更新日期

            public String answerTime; // 作答时间
            public String useTime; // 页面用时
        }
        */


        HomeworkStuAnswerInfo info = new HomeworkStuAnswerInfo(username, learnPlanId, moreList2.get(pos).getId(), stuAnswerBatchList.get(pos).getStuAnswer(), MyApplication.cnName, pos + 1, date);
        info.answerTime = stuAnswerBatchList.get(pos).getOptionTime();
        info.useTime = stuAnswerBatchList.get(pos).getUseTime();
        info.type = stuAnswerBatchList.get(pos).getType(); // 注意类型
        MyApplication.database.homeworkStuAnswerDao().insert(info);

// 20240906：不在单题进行提交，改为最后统一提交
//        String mRequestUrl = null;
//        try {
//            mRequestUrl = Constant.API + Constant.LEARNPLAN_SUBMIT_QUS_ITEM + "?learnPlanId=" + learnPlanId + "&learnPlanName=" + URLEncoder.encode(title, "UTF-8") +
//                    "&userName=" + username + "&questionId=" + adapter.itemList.get(pos).getResourceId() + "&answer=" + URLEncoder.encode(stuAnswer[pos], "UTF-8") + "&status=" + (isNew ? 1 : 3);
//            Log.d(TAG, "submit: " + mRequestUrl);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        StringRequest request = new StringRequest(mRequestUrl, response -> {
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
////            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
//            Log.d("wen", "Volley_Error: " + error.toString());
//        });
//
//        MyApplication.addRequest(request, TAG);

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