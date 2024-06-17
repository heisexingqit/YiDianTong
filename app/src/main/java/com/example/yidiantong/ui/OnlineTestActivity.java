package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BookAutoAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OnlineTestActivity extends AppCompatActivity {
    private static final String TAG = "OnlineTestActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private LinearLayout ll_knowledge_name;

    //请求数据参数
    private String userName; //用户名
    private String subjectId;  //学科id
    private String course_name;  //学科名
    private String flag;  //模式标记

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    private String questionIds;
    BookAutoAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String sourceId = "";

    private SharedPreferences preferences;
    private String[] OnlineTestAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_exercise);

        preferences = getSharedPreferences("shiti", MODE_PRIVATE);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
//            Intent intent = new Intent(this, OnlineTestNullActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
            finish();
        });

        frv_detail = findViewById(R.id.frv_detail);
        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");
        subjectId = getIntent().getStringExtra("subjectId"); //学科名
        course_name = getIntent().getStringExtra("courseName"); //学科名
        flag = getIntent().getStringExtra("flag"); //模式标记


        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("在线测试");
        ll_knowledge_name = findViewById(R.id.ll_knowledge_name);
        ll_knowledge_name.setVisibility(View.GONE);

        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        fll_null = findViewById(R.id.fll_null);

        //设置RecyclerViewAdapter
        adapter = new BookAutoAdapter(this, itemList);
        frv_detail.setAdapter(adapter);

        //refreshList();

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(this, OnlineTestDetailActivity.class);
            BookExerciseEntity item = itemList.get(pos);
            intent.putExtra("questionId", item.questionId); //题目id
            intent.putExtra("subjectId", subjectId);  // 学科id
            intent.putExtra("username", userName);  // 用户名
            intent.putExtra("name", course_name);  // 学科名
            intent.putExtra("allpage", String.valueOf(itemList.size()));  // 总页数
            intent.putExtra("questionIds", questionIds);  // 题目id
            pos = pos + 1;
            intent.putExtra("pos", String.valueOf(pos));
            intent.putExtra("flag", flag);  // 模式标记
            startActivity(intent);
        });

    }

    // what:100 加载错题信息
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                Bundle receivedBundle = (Bundle) message.obj;
                // 获得三个列表信息
                itemList = (List<BookExerciseEntity>)receivedBundle.getSerializable("itemList");
                adapter.loadData(itemList);
                rl_loading.setVisibility(View.GONE);

                //创建本地数组保存学生作答信息
                SharedPreferences.Editor editor = preferences.edit();
                OnlineTestAnswer = new String[itemList.size()];
                String autoArrayString = TextUtils.join(",", OnlineTestAnswer);
                System.out.println("OnlineTestString:" + autoArrayString);
                editor.putString("OnlineTestAnswer", autoArrayString);
                editor.apply();
            }
        }
    };

    //发送请求获得试题数据,对请求到的数据进行处理
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        String mRequestUrl = "http://www.cn901.net:8111/AppServer/ajax/studentApp_getQuestionsZDJC.do?subjectId=" + subjectId;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
                questionIds = "";
                for (int i = 0; i < itemList.size(); i++) {
                    if (i == itemList.size() - 1) {
                        questionIds += itemList.get(i).questionId;
                    } else {
                        questionIds += itemList.get(i).questionId + ",";
                    }
                }
                System.out.println("questionIds:" + questionIds);
                System.out.println("itemList:" + itemList);

                Bundle bundle = new Bundle();
                bundle.putSerializable("itemList", (Serializable) itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = bundle;

                // 发送消息给主线程
                if (itemList == null || itemList.size() == 0 || itemString.equals("[]")) {
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    fll_null.setVisibility(View.GONE);
                }
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

    @Override
    public void onResume() {
        super.onResume();
        if (loadFirst) {
            loadItems_Net();
            loadFirst = false;
        }

    }

}