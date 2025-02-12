package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
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
import com.example.yidiantong.adapter.HomeworkPagerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.HomeworkAnswerBatchEntity;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.entity.HomeworkStuAnswerInfo;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyReadWriteLock;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeworkPagerActivity extends AppCompatActivity implements PagingInterface, View.OnClickListener, HomeworkInterface {
    private static final String TAG = "HomeworkPagerActivity";

    // Activity页面核心组件
    private ViewPager vp_homework;
    private HomeworkPagerAdapter adapter;

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
    private RelativeLayout rl_loading;

    // 老答案
    private String[] oldStuAnswer;

    private int count = 0;

    private ArrayList<HomeworkAnswerBatchEntity> stuAnswerBatchList = new ArrayList<>();

    // 设置数据加载方式
    private static final int LOAD_FROM_NET = 1;
    private static final int LOAD_FROM_DB = 2;
    private int loadFrom = 0;

    // 计时器
    private long currentTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_pager);

        //获取Intent参数
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        Log.e(TAG, "onCreate: " + learnPlanId);
        TextView tv_title = findViewById(R.id.tv_title);
        title = getIntent().getStringExtra("title");
        tv_title.setText(title);
        username = getIntent().getStringExtra("username");
        isNew = getIntent().getBooleanExtra("isNew", true);

        //ViewPager适配器设置
        vp_homework = findViewById(R.id.vp_homework);
        adapter = new HomeworkPagerAdapter(getSupportFragmentManager(), learnPlanId, username);
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
                MyApplication.currentItem = currentItem;
                currentTimeMillis = System.currentTimeMillis();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Log.e(TAG, "currentItem: " + MyApplication.currentItem);
        vp_homework.setCurrentItem(currentItem);

        // 顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            uploadQuestion();
            this.finish();
        });

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

        stuAnswerList.clear();
        for (HomeworkStuAnswerInfo info : homeworkStuAnswerInfos) {
            StuAnswerEntity entity = new StuAnswerEntity(info.order, info.questionId, "", info.stuAnswer, "", info.answerTime, info.useTime);
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

            stuAnswerList.add(entity);
        }
        loadItems_Net();

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

        // 顶栏目录
        tv_content = findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);

        // 顶栏眼睛
        findViewById(R.id.iv_eye).setOnClickListener(this);

        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    int index = intent.getIntExtra("currentItem", 0);
                    if (index == -1) {
                        Intent toHome = new Intent(HomeworkPagerActivity.this, MainPagerActivity.class);
                        toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(toHome);
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
        count += 1;
        Log.e(TAG, "onCreate: " + count);
    }

    @Override
    public void pageLast() {
        if (currentItem == 0) {
            Toast.makeText(this, "已经是第一题", Toast.LENGTH_SHORT).show();
        } else {
            // 上传答案
            uploadQuestion();
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
            tv.setText("已经最后一题，确定要提交作业？");    //内容
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
            // 上传答案
            uploadQuestion();
            currentItem += 1;
            vp_homework.setCurrentItem(currentItem);
        }
    }

    //跳转至提交作业页面
    private void jumpToSubmitPage() {
        uploadQuestion();
        Intent intent = new Intent(HomeworkPagerActivity.this, HomeworkSubmitActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("stuAnswer", stuAnswer);
        intent.putExtra("learnPlanId", learnPlanId);
        intent.putExtra("username", username);
        intent.putExtra("questionIds", questionIds);
        intent.putExtra("questionTypes", question_types_array);
        intent.putExtra("isNew", isNew);
        intent.putExtra("stuAnswerBatchList", stuAnswerBatchList);
        mResultLauncher.launch(intent);
    }

    List<HomeworkEntity> timianList = new ArrayList<>();
    List<StuAnswerEntity> stuAnswerList = new ArrayList<>();
    private int countReady = 0;
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
                List<HomeworkEntity> list = (List<HomeworkEntity>) message.obj;

                // 确保list不为空再进行后续操作
                if (!list.isEmpty()) {
                    pageCount = list.size();
                    questionIds = new String[list.size()];
                    question_types_array = new String[list.size()];

                    for (int i = 0; i < list.size(); ++i) {
                        // 顶部目录菜单内容
                        question_types.add(list.get(i).getQuestionName());
                        // 题面类型
                        question_types_array[i] = list.get(i).getQuestionTypeName();
                        // 题目ID
                        questionIds[i] = list.get(i).getQuestionId();
                    }

                    timianList = list;
                    countReady += 1;
                    Log.e("wen0221", "handleMessage: " + countReady);
                } else {
                    // 处理list为空的情况，比如给出提示或者采取其他措施
                    Log.e("wen0221", "handleMessage: List is empty.");
                }
            } else if (message.what == 101) {
                /**
                 * 学生作答信息
                 */
                List<StuAnswerEntity> list2 = (List<StuAnswerEntity>) message.obj;
                stuAnswer = new String[list2.size()];
                oldStuAnswer = new String[list2.size()];
                Log.e(TAG, "handleMessage: 获取信息");

                for (int i = 0; i < list2.size(); ++i) {
                    // 学生作答内容
                    stuAnswer[i] = list2.get(i).getStuAnswer();
                    oldStuAnswer[i] = list2.get(i).getStuAnswer();

                    if (loadFrom == LOAD_FROM_NET) {
                        // 数据库同步
                        Log.e("0130", "handleMessage: 数据库同步");
                        java.util.Date day = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date = sdf.format(day);
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
                        HomeworkStuAnswerInfo info = new HomeworkStuAnswerInfo(username, learnPlanId, list2.get(i).getQuestionId(), stuAnswer[i], MyApplication.cnName, i + 1, date);
                        if (list2.get(i).getAnswerTime() != null && list2.get(i).getAnswerTime().length() > 0) {
                            info.answerTime = list2.get(i).getAnswerTime();// TODO 可能接口中有值list2
                        }
                        if (list2.get(i).getUseTime() != null && list2.get(i).getUseTime().length() > 0) {
                            info.useTime = list2.get(i).getUseTime();// TODO 可能接口中有值list2
                        }

                        MyApplication.database.homeworkStuAnswerDao().insert(info);
                    }

                    // 20240906作答信息初始化同步
                    // 此处同步userTime，与list2一致
                    HomeworkAnswerBatchEntity batchInfo = new HomeworkAnswerBatchEntity(list2.get(i).getQuestionId(), i + 1, list2.get(i).getStuAnswer());
                    if (list2.get(i).getAnswerTime() != null && list2.get(i).getAnswerTime().length() > 0) {
                        batchInfo.setAnswerTime(list2.get(i).getAnswerTime());// TODO 可能接口中有值list2
                    }
                    if (list2.get(i).getUseTime() != null && list2.get(i).getUseTime().length() > 0) {
                        batchInfo.setUseTime(list2.get(i).getUseTime());// TODO 可能接口中有值list2
                    }
                    stuAnswerBatchList.add(batchInfo);
                }
                stuAnswerList = list2;
                countReady += 1;
                Log.e("wen0221", "stuAnswerBatchList: " + stuAnswerBatchList.get(0).getUseTime());

            }
            // 页面显示
            if (countReady >= 2) {
                Log.e("wen0226", "handleMessage: ++++++++++++++");
                adapter.update(timianList, stuAnswerList);
                rl_loading.setVisibility(View.GONE);
                if (MyApplication.isRotate) {
                    vp_homework.setCurrentItem(MyApplication.currentItem, false);
                    MyApplication.isRotate = false;
                }
            }
        }

    };

    // 加载作业条目，进行ViewPager渲染；同时加载学生答题情况
    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.HOMEWORK_ITEM + "?learnPlanId=" + learnPlanId + "&userName=" + username;
        Log.d(TAG, "题目信息: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.e("wen0523", "loadItems_Net: " + json);

                String itemString = json.getString("data");
                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<HomeworkEntity> itemList = gson.fromJson(itemString, new TypeToken<List<HomeworkEntity>>() {
                }.getType());
                //封装消息，传递给主线程
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


        if (stuAnswerList.size() > 0) {
            loadFrom = LOAD_FROM_DB;
            Log.e("0130", "loadItems_Net: 数据库读取");
            Message message = Message.obtain();
            message.obj = stuAnswerList;
            message.what = 101;
            handler.sendMessage(message);
            return;
        }

        Log.e("0130", "loadItems_Net: 请求读取");

        //学生答题情况
        mRequestUrl = Constant.API + Constant.ANSWER_ITEM_NEW + "?paperId=" + learnPlanId + "&userName=" + username;
        Log.d(TAG, "答题信息: " + mRequestUrl);
        request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<StuAnswerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<StuAnswerEntity>>() {
                }.getType());
                // 遍历 itemList 并修改 stuAnswer 为 "未答" 的条目
                for (StuAnswerEntity item : itemList) {
                    if ("未答".equals(item.getStuAnswer())) {
                        item.setStuAnswer("");  // 将 "未答" 改为 ""
                    }
                }

                Log.e(TAG, "loadItems_Net: 学生作答" + itemList.toString());
                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = itemList;
                // 发送消息给主线程
                //标识线程
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
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 120);

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
                        // 测量并设置ListView的宽度为最宽的列表项的宽度
                        int maxHeight = PxUtils.dip2px(HomeworkPagerActivity.this, 245);
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
                window.showAsDropDown(tv_content, -20, 20);
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
        Log.e("wen0907", "setStuAnswer: " + pos + " " + stuStr);

        if (stuAnswer != null && stuAnswer.length >= pos) {
            stuAnswer[pos - 1] = stuStr;

            stuAnswerList.get(pos - 1).setStuAnswer(stuStr);
            // 20240906 同步答案
            stuAnswerBatchList.get(pos - 1).setStuAnswer(stuStr);

//            Log.e(TAG, "setStuAnswer: pos" + (pos - 1));
//            Log.e(TAG, "setStuAnswer: 新答案" + stuStr);
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

    // 上传试题
    private void uploadQuestion() {

        /*
        上传方法在切换题目时触发，以为要计算做题时间，所以不能直接上传，
        注意，上传的数据是翻页的上一页，或者当前页，所以是currentItem
        知道要上传的数据位置了，拿到数据即可，但是需要修改的一个点就是【花费时间】
        花费时间需要在这里进行计算，同时写入数据库
        计算花费时间的逻辑，通过翻页组件+onResume方法来启动时间，然后通过触发这个方法时截止
         */
        int pos = currentItem;
        // 做题时间数据必须要更新，所以取消判断
//        if (stuAnswer[pos] == null || stuAnswer[pos].equals(oldStuAnswer[pos])) {
//            return;
//        }
        Log.e("wen0907", "uploadQuestion: " + pos);

        // 计算做题用时
        long useTime = Long.parseLong(stuAnswerBatchList.get(pos).getUseTime()) + (System.currentTimeMillis() - currentTimeMillis) / 1000;
        stuAnswerBatchList.get(pos).setUseTime(String.valueOf(useTime));

        // 计算切题时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date day = new Date();
        String date = sdf.format(day);
        stuAnswerBatchList.get(pos).setAnswerTime(date);
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


        HomeworkStuAnswerInfo info = new HomeworkStuAnswerInfo(username, learnPlanId, stuAnswerList.get(pos).getQuestionId(), stuAnswerBatchList.get(pos).getStuAnswer(), MyApplication.cnName, pos + 1, date);
        info.answerTime = stuAnswerBatchList.get(pos).getAnswerTime();
        info.useTime = stuAnswerBatchList.get(pos).getUseTime();
        MyApplication.database.homeworkStuAnswerDao().insert(info);

        // 20240906：不在单题进行提交，改为最后统一提交
//        String mRequestUrl = null;
//        try {
//            mRequestUrl = Constant.API + Constant.SUBMIT_ANSWER + "?learnPlanId=" + learnPlanId +
//                    "&stuId=" + username + "&questionId=" + questionIds[pos] + "&answer=" + URLEncoder.encode(stuAnswer[pos], "UTF-8") + "&answerTime=" + date;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        StringRequest request = new StringRequest(mRequestUrl, response -> {
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//            Log.e("volley", "Volley_Error: " + error.toString());
//
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 在屏幕方向变化时，确保ViewPager的项位置不变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 处理横向屏幕方向
            MyApplication.isRotate = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 处理纵向屏幕方向
            MyApplication.isRotate = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 读写解锁
        MyReadWriteLock.checkout(username, this);
    }
}