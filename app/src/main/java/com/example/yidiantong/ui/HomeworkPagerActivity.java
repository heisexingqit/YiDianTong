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
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeworkPagerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.TransmitInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HomeworkPagerActivity extends AppCompatActivity implements PageingInterface, View.OnClickListener, TransmitInterface {
    private static final String TAG = "HomeworkPagerActivity";

    private ViewPager vp_homework;
    private int currentItem = 0;
    private int pageCount = 0;

    //接口参数
    private String learnPlanId;
    private HomeworkPagerAdapter adapter;

    //questionId
    private String[] questionIds;
    //题目类型
    private String[] question_types_array;

    //顶栏目录
    private View contentView = null;
    private PopupWindow window;
    private TextView tv_content;
    private final List<String> question_types = new ArrayList<>();
    MyArrayAdapter myArrayAdapter = new MyArrayAdapter(this, question_types);
    private String username;

    String[] stuAnswer;
    private String title;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private Boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_pager);

        //获取Intent参数
        learnPlanId = getIntent().getStringExtra("learnPlanId");
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        vp_homework.setCurrentItem(currentItem);

        //顶栏返回按钮
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            this.finish();
        });


        loadItems_Net();

        //ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(vp_homework.getContext(),
                    new AccelerateInterpolator());
            field.set(vp_homework, scroller);
            scroller.setmDuration(400);
        } catch (Exception e) {

        }

        //顶栏目录
        tv_content = findViewById(R.id.tv_content);
        tv_content.setOnClickListener(this);

        //顶栏眼睛
        findViewById(R.id.iv_eye).setOnClickListener(this);

        //提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    int index = intent.getIntExtra("currentItem", 0);
                    if (index == -1) {
                        Toast.makeText(HomeworkPagerActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        currentItem = index;
                        vp_homework.setCurrentItem(currentItem);
                    }

                }
            }
        });
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
            currentItem += 1;
            vp_homework.setCurrentItem(currentItem);
        }
    }

    //跳转至提交作业页面
    private void jumpToSubmitPage() {
        Intent intent = new Intent(HomeworkPagerActivity.this, HomeworkSubmitActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("stuAnswer", stuAnswer);
        intent.putExtra("learnPlanId", learnPlanId);
        intent.putExtra("username", username);
        intent.putExtra("questionIds", questionIds);
        intent.putExtra("isNew", isNew);
        intent.putExtra("questionTypes", question_types_array);
        mResultLauncher.launch(intent);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                List<HomeworkEntity> list = (List<HomeworkEntity>) message.obj;
                adapter.updateQ(list);
                pageCount = adapter.getCount();
                myArrayAdapter.notifyDataSetChanged();

                questionIds = new String[list.size()];
                question_types_array = new String[list.size()];
                for (int i = 0; i < list.size(); ++i) {
                    //顶部目录菜单
                    question_types.add((i + 1) + ". " + list.get(i).getQuestionTypeName());
                    //题面类型
                    question_types_array[i] = list.get(i).getQuestionTypeName();
                    //题目ID
                    questionIds[i] = list.get(i).getQuestionId();
                }


            } else if (message.what == 101) {
                //加载学生答题情况
                List<StuAnswerEntity> list2 = (List<StuAnswerEntity>) message.obj;
                stuAnswer = new String[list2.size()];
                adapter.updateA(list2);
                for (int i = 0; i < list2.size(); ++i) {
                    stuAnswer[i] = list2.get(i).getStuAnswer();
                }

            }
        }
    };

    //加载作业条目，进行ViewPager渲染；同时加载学生答题情况
    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.HOMEWORK_ITEM + "?learnPlanId=" + learnPlanId;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<HomeworkEntity> itemList = gson.fromJson(itemString, new TypeToken<List<HomeworkEntity>>() {
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
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        //学生答题情况
        mRequestUrl = Constant.API + Constant.ANSWER_ITEM + "?paperId=" + learnPlanId + "&userName=" + username;
        request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<StuAnswerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<StuAnswerEntity>>() {
                }.getType());

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
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //目录切换作业题面
            case R.id.tv_content:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);

//                    ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, question_types);
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
        stuAnswer[pos - 1] = stuStr;
    }
}