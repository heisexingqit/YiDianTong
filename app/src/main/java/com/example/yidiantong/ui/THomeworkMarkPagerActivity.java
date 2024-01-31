package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.THomeworkMarkPagerAdapter;
import com.example.yidiantong.bean.THomeworkMarkedEntity;
import com.example.yidiantong.bean.THomeworkStudentItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyReadWriteLock;
import com.example.yidiantong.util.NumberUtils;
import com.example.yidiantong.util.THomeworkMarkInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class THomeworkMarkPagerActivity extends AppCompatActivity implements View.OnClickListener, THomeworkMarkInterface {
    private static final String TAG = "THomeworkMarkActivity";

    // 参数相关
    private THomeworkStudentItemEntity itemEntity;
    private String name;
    private int pageCount = 0;
    private int pageCountAll = 0;
    private String scoreCount;
    private String mode;
    private String type;

    // 页面组件
    private ViewPager2 vp_homework;
    private THomeworkMarkPagerAdapter adapter;
    private int currentItem = 0;
    private LinearLayout ll_main;
    private boolean finishData = false;
    private Button btn_last;
    private Button btn_next;

    // 请求参数
    private String taskId;
    private String stuName;

    // 跳转
    ActivityResultLauncher mResultLauncher;

    // 批改情况列表
    private List<Double> qusScoresList = new ArrayList<>();
    private List<Double> stuScoresList = new ArrayList<>();
    private List<String> statusList = new ArrayList<>();
    private List<THomeworkMarkedEntity> moreListAll;
    private Boolean canMark;
    private List<String> questionIdList = new ArrayList<>();

    // ViewPagerAdapter中列表
    List<THomeworkMarkedEntity> moreList;

    private boolean isAll = false; // 标记ViewPager是否已经是全部题目

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_mark);

        // 获取参数
        Intent intent = getIntent();
        itemEntity = (THomeworkStudentItemEntity) intent.getSerializableExtra("item");
        stuName = intent.getStringExtra("stuName");
        taskId = intent.getStringExtra("taskId");
        name = intent.getStringExtra("name");
        scoreCount = intent.getStringExtra("scoreCount");
        canMark = intent.getBooleanExtra("canMark", true);
        mode = intent.getStringExtra("mode");
        type = intent.getStringExtra("type");

        findViewById(R.id.iv_eye).setOnClickListener(this);

        // 获取组件
        ll_main = findViewById(R.id.ll_main);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            finish();
        });
        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(name);
        vp_homework = findViewById(R.id.vp_homework);
        // 设置用户输入模式为不可触屏
        vp_homework.setUserInputEnabled(false);

        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    int index = intent.getIntExtra("currentItem", 0);
                    if (index == -1) {
                        Toast.makeText(THomeworkMarkPagerActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                        Intent back = new Intent();
                        setResult(Activity.RESULT_OK, back);
                        finish();
                    } else if (index == -2) {
                        finish();
                    } else {
                        if (mode.equals("1")) {
                            isAll = true;
                        }
                        // 将pager内容改为全部
                        if (!isAll) {
                            Log.e(TAG, "onActivityResult: ViewPager刷新了！");
                            moreList = moreListAll;
                            adapter.update(moreListAll);
                        }

                        isAll = true;
                        pageCount = moreListAll.size();
                        currentItem = index;
                        vp_homework.setCurrentItem(currentItem, false);
                        btnShow();
                    }

                }
            }
        });

        // 弹窗
        if (!itemEntity.getStatus().equals("2")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(THomeworkMarkPagerActivity.this);
            builder.setMessage("该学生已批改! 是否直接查看批改结果?");

            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    jumpToSubmit();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // 显示面板
                    ll_main.setVisibility(View.VISIBLE);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        return true;
                    }
                    return false;
                }
            });
            dialog.show();
        } else {
            // 显示面板
            ll_main.setVisibility(View.VISIBLE);
        }

        // 主要的ViewPager组件
        adapter = new THomeworkMarkPagerAdapter(this, canMark);
        vp_homework.setAdapter(adapter);
        vp_homework.setCurrentItem(currentItem);

        // ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(vp_homework.getContext(),
                    new AccelerateInterpolator());
            field.set(vp_homework, scroller);
            scroller.setmDuration(300);
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e);
        }

        // 数据请求
        loadItems_Net();

        //翻页组件
        btn_last = findViewById(R.id.btn_last);
        btn_next = findViewById(R.id.btn_next);
        btn_last.setOnClickListener(this);
        btn_next.setOnClickListener(this);

        btnShow();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ll_main.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_last:
                if (currentItem == 0) {
                    Toast.makeText(this, "已经是第一题", Toast.LENGTH_SHORT).show();
                } else {
                    currentItem -= 1;
                    vp_homework.setCurrentItem(currentItem);
                    Log.d("wen", "Activity: " + moreList.get(currentItem).getOrder());
                }
                btnShow();
                break;
            case R.id.btn_next:
                if (currentItem == pageCount - 1) {
                    jumpToSubmit();
                } else {

                    currentItem += 1;
                    vp_homework.setCurrentItem(currentItem);
                    Log.d("wen", "Activity: " + moreList.get(currentItem).getOrder());
                }
                btnShow();
                break;
            case R.id.iv_eye:
                jumpToSubmit();
                break;
        }
    }

    private void btnShow() {
        if (currentItem > 0) {
            btn_last.setBackgroundResource(R.drawable.t_homework_report);
        } else {
            btn_last.setBackgroundResource(R.drawable.t_homework_mark_unable);
        }
        Log.d("wen", "btnShow: " + currentItem + " " + pageCount);
        if (currentItem == pageCount - 1) {
            btn_next.setText("完成批改");
        } else {
            btn_next.setText("下一题");
        }
    }

    private void jumpToSubmit() {
        if (finishData) {
            Intent intent = new Intent(THomeworkMarkPagerActivity.this, THomeworkMarkSubmitActivity.class);
            double sum = 0;
            for (int i = 0; i < pageCountAll; ++i) {
                sum += stuScoresList.get(i);
            }
            // 分数格式化
            intent.putExtra("stuScore", NumberUtils.getFormatNumString(String.format("%.1f", sum)));
            intent.putExtra("scoreCount", NumberUtils.getFormatNumString(scoreCount));
            intent.putExtra("status", (Serializable) statusList);
            intent.putExtra("canMark", canMark);
            intent.putExtra("taskId", taskId);
            intent.putExtra("name", name);
            intent.putExtra("stuUserName", stuName);
            intent.putExtra("stuScoresList", (Serializable) stuScoresList);
            intent.putExtra("questionIdList", (Serializable) questionIdList);
            intent.putExtra("type", type);

            mResultLauncher.launch(intent);
        }
    }

    // 批改情况生成
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
                moreListAll = (List<THomeworkMarkedEntity>) message.obj;
                pageCountAll = moreListAll.size();

                moreList = new ArrayList<>();

                // 构造提交参数
                for (int i = 0; i < moreListAll.size(); ++i) {
                    THomeworkMarkedEntity item = moreListAll.get(i);

                    questionIdList.add(item.getQuestionID());
                    if (item.getStuAnswer().length() == 0) {
                        statusList.add("no_answer");
                    } else {
                        // 构建状态列表
                        String iconStr = "";
                        switch (item.getStatus()) { // 1正确；2错误；3部分答对；4手工未阅
                            case "1":
                                iconStr += "correct";
                                break;
                            case "2":
                                iconStr += "error";
                                break;
                            case "3":
                                iconStr += "half_correct";
                                break;
                            case "4":
                                iconStr += "no_mark";
                                break;
                        }
                        statusList.add(iconStr);
                    }

                    // 同步分数
                    stuScoresList.add(Double.parseDouble(item.getStuscore()));
                    qusScoresList.add(Double.parseDouble(item.getQuestionScore()));
                }

                // 深复制
                Iterator<THomeworkMarkedEntity> iterator = moreListAll.iterator();
                while (iterator.hasNext()) {
                    THomeworkMarkedEntity item = iterator.next();
                    // 模式适配
                    if (mode.equals("2") && itemEntity.getStatus().equals("2")) {
                        if (item.getStatus().equals("4")) {
                            moreList.add(item);
                        }
                    } else {
                        moreList.add(item);
                    }
                }

                // 两种模式，mode1是全部，mode2是只给未批改
                adapter.update(moreList);

                pageCount = moreList.size();
                Log.d("wen", "handleMessage: " + pageCount);
                btnShow();
                finishData = true;
            }
        }
    };

    /**
     * 请求批改信息 将Handler内容写入，速度更快（如遇到报错，再用Handler）
     */
    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_MARK + "?taskId=" + taskId + "&type=" + type + "&userName=" + stuName;
        Log.d("wen", "批改总信息: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject obj = json.getJSONObject("data");
                String itemString = obj.getString("handList");
                String autoMark = obj.getString("autoMark");
                Log.d("wen", "自动批改标记:" + autoMark);
                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<THomeworkMarkedEntity> itemList = gson.fromJson(itemString, new TypeToken<List<THomeworkMarkedEntity>>() {
                }.getType());
                for(int i = 0; i < itemList.size(); ++i){
                    Log.e(TAG, "loadItems_Net: " + itemList.get(i).getStuAnswer());
                }
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = itemList;

                // 标识线程
                message.what = 100;
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
    public void setStuAnswer(int pos, String stuScore) {
        if (stuScoresList.size() <= pos) {
            return;
        }
        double socres = Double.parseDouble(stuScore);
        // 同步分数
        stuScoresList.set(pos, socres);
        if (socres < 0.1) {
            if (statusList.get(pos).equals("no_answer")) {
                statusList.set(pos, "hand_no_answer");
            } else {
                statusList.set(pos, "hand_error");
            }
        } else if (Math.abs(socres - qusScoresList.get(pos)) < 0.1) {
            statusList.set(pos, "hand_correct");
        } else {
            statusList.set(pos, "hand_half_correct");
        }
    }

    @Override
    public String getStuScore(int pos) {
        return String.format("%.2f", stuScoresList.get(pos));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 读写解锁
        MyReadWriteLock.checkoutT(MyApplication.username, this);
    }
}