package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
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
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksRecyclerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.ZZXXStuAnswerBean;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 此页面与KnowledgeBookDetailActivity功能全部一样 只是数据获取方式不同
 */
public class DetectionShiTiHistoryActivity extends AppCompatActivity implements RecyclerInterface, View.OnClickListener {
    private static final String TAG = "DetectionShiTiHistoryActivity";
    private String course_name;  //课程名称
    private ViewPager fvp_book_recycle;  //可滑动ViewPager
    private BooksRecyclerAdapter adapter;  //ViewPager适配器
    private int currentItem = 1;  //当前ViewPager位置
    private int pageCount = 0;
    private boolean exerciseType = true;  //是否是举一反三

    // 接口参数
    String userName;  //用户名
    String taskId;  //用户名
    String subjectId;  //学科ID
    String questionId; //题目ID
    String allpage;
    String zhishidian;  //知识点
    String zhishidianId;  //知识点ID
    String pos;  //题目位置
    String questionIdAll = "";  //题目类型
    String[] questionIds;  //题目ID数组
    public List<BookExerciseEntity> bookExerciseEntityList;  //题目列表
    String xueduan;  //学段
    String banben;  //版本
    String jiaocai;
    String unitId;
    String message;
    private LinearLayout fll_null;
    private LinearLayout fll_bd;
    private TextView tv_massage;
    private RelativeLayout rl_loading;
    private SharedPreferences preferences;
    private String[] upStuLoadAnswer;
    private TextView tv_content;
    private View contentView = null;
    private List<String> question_types = new ArrayList<>();
    MyArrayAdapter myArrayAdapter = new MyArrayAdapter(this, question_types);
    private PopupWindow window;
    private ActivityResultLauncher<Intent> mResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_exercise_detail);
        preferences = getSharedPreferences("shiti", MODE_PRIVATE);
        ((MyApplication) getApplication()).checkAndHandleGlobalVariables(this);

        MyApplication.typeActivity = 4;
        MyApplication.typeHistory = 2;
        fvp_book_recycle = findViewById(R.id.fvp_book_recycle);
        adapter = new BooksRecyclerAdapter(getSupportFragmentManager());
        fvp_book_recycle.setAdapter(adapter);
        tv_massage = findViewById(R.id.tv_massage);
        fll_null = findViewById(R.id.fll_null);
        rl_loading = findViewById(R.id.rl_loading);
        fll_bd = findViewById(R.id.fll_bd);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            if (bookExerciseEntityList.size() > 0 && bookExerciseEntityList.get(currentItem - 1).isZuoDaMeiPingFen) {
                Toast.makeText(this, "请先进行评分!", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    saveAnswer2Server();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        bookExerciseEntityList = new ArrayList<>();
        //获取Intent参数
        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("智能测试试题详情");
        userName = getIntent().getStringExtra("userName");  //用户名
        taskId = getIntent().getStringExtra("taskId");  //任务ID
        subjectId = getIntent().getStringExtra("subjectId"); //学科名
        course_name = getIntent().getStringExtra("courseName"); //学科id
        zhishidian = getIntent().getStringExtra("zhishidian"); //知识点
        zhishidianId = getIntent().getStringExtra("zhishidianId"); //知识点id
        xueduan = getIntent().getStringExtra("xueduanId"); //学段
        banben = getIntent().getStringExtra("banbenId"); //版本
        jiaocai = getIntent().getStringExtra("jiaocaiId"); //教材
        unitId = getIntent().getStringExtra("unitId"); //考点

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
                        Toast.makeText(DetectionShiTiHistoryActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        currentItem = index;
                        loadItems_Net(currentItem);
                    }
                }
            }
        });

        // 获取自主学习历史中的试题信息
        String mRequestUrl = Constant.API + "/AppServer/ajax/studentApp_getQuestionsByRecordId.do" +
                "?stuId=" + userName + "&taskId=" + taskId;
        Log.d("song", "mRequestUrl: " + mRequestUrl);
        StringRequest request1 = new StringRequest(mRequestUrl, response1 -> {
            try {
                JSONObject json1 = JsonUtils.getJsonObjectFromString(response1);
                message = json1.getString("message");
                Log.d("song", "message: " + message);
                String itemString = json1.getString("data");
                Log.d("song", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                bookExerciseEntityList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {
                }.getType());
                for (int i = 0; i < bookExerciseEntityList.size(); i++) {
                    // 顶部目录菜单内容
                    question_types.add(bookExerciseEntityList.get(i).getTypeName());
                    if (i == bookExerciseEntityList.size() - 1) {
                        questionIdAll += bookExerciseEntityList.get(i).questionId;
                    } else {
                        questionIdAll += bookExerciseEntityList.get(i).questionId + ",";
                    }
                }
                pos = "1";
                allpage = bookExerciseEntityList.size() + "";
                questionIds = questionIdAll.split(",");
                System.out.println("questionIds ^-^:" + Arrays.toString(questionIds));
                System.out.println("bookExerciseEntityList ^-^:" + bookExerciseEntityList.size());

                // 判断message信息
                // 根据返回的message展示tv_massage的内容
                if (message.equals("选择章节已掌握")) {
                    tv_massage.setText("重新选择章节进行学习");
                    // 进行页面跳转 跳转到KnowledgePointActivity
                    tv_massage.setOnClickListener(v -> {
                        Intent intent = new Intent(this, AutoDetectionActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("xueduanId", xueduan);
                        intent.putExtra("xuekeId", subjectId);
                        intent.putExtra("xueke", course_name);
                        intent.putExtra("banbenId", banben);
                        intent.putExtra("jiaocaiId", jiaocai);
                        startActivity(intent);
                        finish();
                    });
                } else if (message.equals("选择考点已掌握")) {
                    tv_massage.setText("重新选择章节进行学习");
                    // 进行页面跳转 跳转到KnowledgePointActivity
                    tv_massage.setOnClickListener(v -> {
                        Intent intent = new Intent(this, AutoDetectionActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("xueduanId", xueduan);
                        intent.putExtra("xuekeId", subjectId);
                        intent.putExtra("xueke", course_name);
                        intent.putExtra("banbenId", banben);
                        intent.putExtra("jiaocaiId", jiaocai);
                        startActivity(intent);
                        finish();
                    });
                } else if (message.equals("暂无试题")) {
                    tv_massage.setText("选择考点下暂无试题");
                    // 红色字体
                    tv_massage.setTextColor(Color.parseColor("#FF0000"));
                }
                if (bookExerciseEntityList == null || bookExerciseEntityList.size() == 0 || bookExerciseEntityList.equals("[]")) {
                    fll_null.setVisibility(View.VISIBLE);
                    rl_loading.setVisibility(View.GONE);
                    return;
                }
                fll_null.setVisibility(View.GONE);
                rl_loading.setVisibility(View.VISIBLE);
                //创建本地数组保存学生作答信息
                SharedPreferences.Editor editor = preferences.edit();
                upStuLoadAnswer = new String[bookExerciseEntityList.size()];
                String upArrayString = TextUtils.join(",", upStuLoadAnswer);
                System.out.println("upArrayString：" + upArrayString);
                editor.putString("upStuLoadAnswer", upArrayString);
                editor.apply();
                currentItem = Integer.valueOf(pos);
                fvp_book_recycle.setCurrentItem(currentItem);


                // 获取历史中的作答信息
                String mRequestUrl2 = Constant.API + "/AppServer/ajax/studentApp_getZZXXAnswerList.do" +
                        "?stuId=" + userName + "&taskId=" + taskId;
                Log.d("song", "mRequestUrl2: " + mRequestUrl2);
                StringRequest request2 = new StringRequest(mRequestUrl2, response2 -> {
                    try {
                        JSONObject json2 = JsonUtils.getJsonObjectFromString(response2);
                        String bianhao = json2.getString("message");
                        Log.d("song", "bianhao: " + bianhao);
                        String itemString2 = json2.getString("data");
                        Log.d("song", "itemString2: " + itemString2);
                        //使用Gson框架转换Json字符串为列表
                        List<ZuoDaXinXi> tempList = gson.fromJson(itemString2, new TypeToken<List<ZuoDaXinXi>>() {
                        }.getType());

                        // 将作答信息输入到bookExerciseEntityList中
                        for (int i = 0; i < bookExerciseEntityList.size(); i++) {
                            BookExerciseEntity bookExerciseEntity = bookExerciseEntityList.get(i);
                            for (ZuoDaXinXi zuoDaXinXi : tempList) {
                                if (bookExerciseEntity.getQuestionId().equals(zuoDaXinXi.questionId)) {
                                    bookExerciseEntity.setStuInput(zuoDaXinXi.stuAnswer);  // 学生作答信息
                                    bookExerciseEntity.setStuScore(zuoDaXinXi.teaScore);   // 学生作答分数
                                    Date day = new Date();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String date = sdf.format(day);
                                    bookExerciseEntity.setZuodaDate(date); // 学生作答时间
                                    String answer = ""; // 本地需保存的答案
                                    if (bookExerciseEntity.getBaseTypeId().equals("101") || bookExerciseEntity.getBaseTypeId().equals("103")
                                            || bookExerciseEntity.getBaseTypeId().equals("108") || bookExerciseEntity.getBaseTypeId().equals("109")) {
                                        if (zuoDaXinXi.status.equals("right"))
                                            bookExerciseEntity.setAccType(1);
                                        else bookExerciseEntity.setAccType(2);
                                        answer = zuoDaXinXi.stuAnswer;
                                    } else {
                                        if (zuoDaXinXi.teaScore.equals(zuoDaXinXi.queSscore))
                                            bookExerciseEntity.setAccType(1);
                                        else if (zuoDaXinXi.teaScore.equals("0"))
                                            bookExerciseEntity.setAccType(2);
                                        else bookExerciseEntity.setAccType(3);
                                        // 判断是多选还是主观题
                                        if (bookExerciseEntity.getBaseTypeId().equals("102")) {
                                            answer = zuoDaXinXi.stuAnswer;
                                        }else {
                                            if (zuoDaXinXi.stuAnswer.contains("img")) {
                                                answer = "@&@" + zuoDaXinXi.stuAnswer + "@&@" + zuoDaXinXi.teaScore;
                                            }else {
                                                answer = zuoDaXinXi.stuAnswer +  "@&@" + "@&@" + zuoDaXinXi.teaScore;
                                            }
                                        }

                                    }
                                    // 将作答信息存至本地
                                    String arrayString = preferences.getString("upStuLoadAnswer", null);
                                    if (arrayString != null) {
                                        String[] upStuLoadAnswer = arrayString.split(",");
                                        upStuLoadAnswer[i] = answer; // 数组题号对应页数-1
                                        SharedPreferences.Editor editor2 = preferences.edit();
                                        arrayString = TextUtils.join(",", upStuLoadAnswer);
                                        System.out.println("upArrayString: " + arrayString);
                                        editor2.putString("upStuLoadAnswer", arrayString);
                                        editor2.commit();
                                    }
                                }
                            }
                        }
                        loadItems_Net(currentItem);// 加载试题信息
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
                });
                MyApplication.addRequest(request2, TAG);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request1, TAG);

        //滑动监听器
        fvp_book_recycle.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //翻页同步下标
                currentItem = position;
                MyApplication.currentItem = currentItem;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(fvp_book_recycle.getContext(),
                    new AccelerateInterpolator());
            field.set(fvp_book_recycle, scroller);
            scroller.setmDuration(400);
        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //目录切换作业题面
            case R.id.tv_content:
                if (bookExerciseEntityList.size() == 0) {
                    Toast.makeText(this, "没有试题!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 145);

                    lv_homework.setAdapter(myArrayAdapter);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Boolean isZuoDaMeiPingFen = bookExerciseEntityList.get(currentItem - 1).getIsZuoDaMeiPingFen();
                            if (isZuoDaMeiPingFen) {
                                Toast.makeText(DetectionShiTiHistoryActivity.this, "请先进行评分", Toast.LENGTH_SHORT).show();
                                window.dismiss();
                            } else {
                                //切换页+消除选项口
                                currentItem = i + 1;
                                System.out.println("currentItem:" + currentItem);
//                            fvp_book_recycle.setCurrentItem(3);
                                loadItems_Net(currentItem);
                                window.dismiss();
                            }
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        // 测量并设置ListView的宽度为最宽的列表项的宽度
                        int maxHeight = PxUtils.dip2px(DetectionShiTiHistoryActivity.this, 245);
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
                if (bookExerciseEntityList.size() == 0) {
                    Toast.makeText(this, "没有试题!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean isZuoDaMeiPingFen = bookExerciseEntityList.get(currentItem - 1).getIsZuoDaMeiPingFen();
                if (isZuoDaMeiPingFen) {
                    Toast.makeText(this, "请先进行评分!", Toast.LENGTH_SHORT).show();
                } else {
                    // 发送请求,保存学生的答案
                    try {
                        saveAnswer2Server();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    jumpToSubmitPage();
                }
        }
    }

    public void saveAnswer2Server() throws ParseException {
        // 将bookExerciseEntityList中的accType不为0的数据封装成answerjson
        String answerjson = "";
        List<ZZXXStuAnswerBean> temp = new ArrayList<>();
        Gson gson = new Gson();
        for (BookExerciseEntity entity : bookExerciseEntityList) {
            if (entity.accType != 0) {
                ZZXXStuAnswerBean zzxxStuAnswerBean =
                        new ZZXXStuAnswerBean(message, entity.questionId, entity.stuInput, entity.stuScore, entity.zuodaDate);
                temp.add(zzxxStuAnswerBean);
            }
        }
        if (temp.size() == 0) {
            return;
        }
        answerjson = gson.toJson(temp);
        try {
            answerjson = URLEncoder.encode(answerjson, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.e("wen0223", "saveAnswer2Server: " + answerjson);
        String mRequestUrl = Constant.API + "/AppServer/ajax/studentApp_saveZZXXAnswer.do?userId=" +
                userName + "&taskId=" + message + "&answerjson=" + answerjson;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String success = json.getString("success");
                Log.d("wen0321", "success: saveZZXXAnswer" + success);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    //跳转至提交作业页面
    private void jumpToSubmitPage() {
        Intent intent = new Intent(DetectionShiTiHistoryActivity.this, DetectionSubmitActivity.class);
        intent.putExtra("itemList", (Serializable) bookExerciseEntityList);
        intent.putExtra("userName", userName);
        intent.putExtra("subjectId", subjectId);
        intent.putExtra("courseName", course_name);
        intent.putExtra("zhishidian", zhishidian);
        intent.putExtra("zhishidianId", zhishidianId);
        intent.putExtra("xueduanId", xueduan);
        intent.putExtra("banbenId", banben);
        intent.putExtra("jiaocaiId", jiaocai);
        intent.putExtra("unitId", unitId);
        intent.putExtra("message", message);
        mResultLauncher.launch(intent);
    }


    // 回到上一题
    @Override
    public void pageLast(String currentpage, String allpage) {
        System.out.println("减一前currentItem ^-^:" + currentItem);
        if (currentItem == 1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经是第一题了");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("关闭", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem -= 1;
            System.out.println("减一后currentItem ^-^:" + currentItem);
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    // 前往下一题
    @Override
    public void pageNext(String currentpage, String allpage) {
        int allpage1 = Integer.parseInt(allpage);
        if (currentItem == allpage1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经是最后一题了");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("关闭", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem += 1;
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    // 将错题标记掌握移至错题本时更新页面
    @Override
    public void updatepage(String currentpage, String allpage) {
        currentItem = Integer.parseInt(currentpage);
        int allpage1 = Integer.parseInt(allpage);
        if (allpage1 == 1) {  //只有一题时
            this.finish();
        } else if (currentItem == allpage1) {
            currentItem -= 1;
            // 延迟1秒
            try {
                Thread.sleep(1000); // 延迟1秒，单位为毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        } else {
            // 延迟0.1秒
            try {
                Thread.sleep(1000); // 延迟1秒，单位为毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                List<BookExerciseEntity> list = (List<BookExerciseEntity>) message.obj;
                String currentpage = String.valueOf(currentItem);
                adapter.update2(list, 2, userName, subjectId, course_name, exerciseType, currentpage, allpage);
            }
        }
    };

    // 获取错题详情信息
    private void loadItems_Net(int pos) {
        String mRequestUrl = "http://www.cn901.com//ShopGoods/ajax/learnPlan_getQuestion.do" + "?questionId=" + questionIds[pos - 1];
        Log.e("wen0223", "详细信息单题请求" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("questionInfo");
                //使用正则表达式对返回结果新增选项设置个数
                Pattern pattern = Pattern.compile("answernumber=(\\\\*)\"(\\d+)");
                Matcher m1 = pattern.matcher(itemString);
                int answerNumber = 1;//选项个数
                while (m1.find()) {
                    answerNumber = Integer.parseInt(m1.group(2));
                    break;
                }
                itemString = "[" + itemString + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookExerciseEntity> itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {
                }.getType());
                fll_bd.setVisibility(View.VISIBLE);
                for (BookExerciseEntity item : itemList) {
                    //设置选项个数
                    item.setAnswerNumber(answerNumber);
                    item.setQuestionKeyword(bookExerciseEntityList.get(pos - 1).getQuestionKeyword());
                }
                Log.e("wen0223", "loadItems_Net: " + itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;
                //标识线程
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

    //Activity中将权限结果回传给Fragment
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public class ZuoDaXinXi {
        public String questionId;  // 问题id
        public String teaScore;    // 作答分数
        public String queSscore;   // 试题分数
        public String stuAnswer;   // 学生答案
        public String status;      // 是否正确
    }
}