package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.example.yidiantong.adapter.HistoryAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.HistoryEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class KnowledgeHistoryActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgeHistoryActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;

    //请求数据参数
    private int currentPage = -1;
    private String userName; //用户名
    private String xueduan;  //请求类型
    private String subjectId;  //学科id
    private String courseName;  //学科id
    private String banben;  //学科id
    private String jiaocai;  //学科id
    private String unitId;  //学校id

    //列表数据
    private List<HistoryEntity> itemList = new ArrayList<>();
    HistoryAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;

    private SharedPreferences preferences;
    private String[] autoStuLoadAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_history);
        ((MyApplication) getApplication()).checkAndHandleGlobalVariables(this);
        //preferences = getSharedPreferences("shiti", MODE_PRIVATE);


        frv_detail = findViewById(R.id.frv_detail);
        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");  //用户名
        subjectId = getIntent().getStringExtra("xuekeId"); //学科名
        courseName = getIntent().getStringExtra("xueke"); //学科名
        xueduan = getIntent().getStringExtra("xueduanId"); //学段
        banben = getIntent().getStringExtra("banbenId"); //版本
        jiaocai = getIntent().getStringExtra("jiaocaiId"); //教材
        unitId = getIntent().getStringExtra("unitId"); //考点
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            finish();
        });

        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("自主学习历史记录");

        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        fll_null = findViewById(R.id.fll_null);

        //设置RecyclerViewAdapter
        adapter = new HistoryAdapter(this, itemList);
        frv_detail.setAdapter(adapter);

        loadItems_Net();

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(this, KnowledgeShiTiHistoryActivity.class);
            HistoryEntity item = itemList.get(pos);
            intent.putExtra("userName", userName); //用户名
            intent.putExtra("taskId", item.getId()); //用户名
            intent.putExtra("subjectId", subjectId); //学科id
            intent.putExtra("courseName", courseName); //学科名
            intent.putExtra("xueduanId", xueduan); //学段
            intent.putExtra("banbenId", banben); //版本
            intent.putExtra("jiaocaiId", jiaocai); //教材
            intent.putExtra("unitId", unitId); //考点
            startActivity(intent);
            finish();
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
                itemList = (List<HistoryEntity>) receivedBundle.getSerializable("itemList");
                adapter.loadData(itemList);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;

                //创建本地数组保存学生作答信息
//                SharedPreferences.Editor editor = preferences.edit();
//                autoStuLoadAnswer = new String[itemList.size()];
//                String autoArrayString = TextUtils.join(",", autoStuLoadAnswer);
//                System.out.println("autoArrayString:" + autoArrayString);
//                editor.putString("autoStuLoadAnswer", autoArrayString);
//                editor.apply();
            }
        }
    };

    //发送请求获得试题数据,对请求到的数据进行处理
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        String mRequestUrl = Constant.API + "/AppServer/ajax/studentApp_getZZXXRecordList.do"
                + "?stuId=" + userName + "&channelCode=" + xueduan + "&subjectCode=" + subjectId
                + "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&unitId=1101010010001";
        Log.e("syq", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);

                String itemString = json.getString("data");
                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                itemList = gson.fromJson(itemString, new TypeToken<List<HistoryEntity>>() {
                }.getType());

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
}