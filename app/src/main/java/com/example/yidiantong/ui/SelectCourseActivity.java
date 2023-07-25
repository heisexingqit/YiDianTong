package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksAdapter;
import com.example.yidiantong.adapter.SelectCouresAdapter;
import com.example.yidiantong.bean.BookInfoEntity;
import com.example.yidiantong.bean.SelectCourseEntity;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectCourseActivity extends AppCompatActivity {

    private RecyclerView frv_selsect;
    private String username;
    private SelectCouresAdapter adapter;
    //列表数据
    private List<SelectCourseEntity> itemList = new ArrayList<>();
    private LinearLayout fll_null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course);
        TextView ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("高考选科");

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        frv_selsect = findViewById(R.id.frv_selsect);

        //RecyclerView两步必要配置
        frv_selsect.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frv_selsect.setItemAnimator(new DefaultItemAnimator());

        //获取登录传递的参数
        username = this.getIntent().getStringExtra("username");
        //设置RecyclerViewAdapter
        adapter = new SelectCouresAdapter(this, itemList);

        // 展示选科信息
        loadItems_Net();

        // 设置空选项
        fll_null = findViewById(R.id.fll_null);

        frv_selsect.setAdapter(adapter);
        frv_selsect.setItemViewCacheSize(0);

        String userCn = this.getIntent().getStringExtra("userCn");
        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            if(itemList.get(pos).getStatus().equals("2")){
                Intent intent = new Intent(this, SelectDetailActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("userCn",userCn);
                intent.putExtra("mode",adapter.itemList.get(pos).getMode());
                intent.putExtra("taskId",adapter.itemList.get(pos).getTaskId());
                intent.putExtra("subjectComposeName",adapter.itemList.get(pos).getSubjectComposeName());

                startActivity(intent);
            }

        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.loadData((List<SelectCourseEntity>) message.obj);
            }
        }
    };

    //加载消息条目
    private void loadItems_Net() {
        String mRequestUrl = Constant.API + Constant.GET_SELECT_COURSE_TASK_LIST + "?userId=" + username + "&unitId=1101010010001";
        Log.e("GET_SELECT_COURSE_TASK_LIST",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Log.e("itemString",""+itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<SelectCourseEntity> itemList = gson.fromJson(itemString, new TypeToken<List<SelectCourseEntity>>() {}.getType());
                Log.e("itemList",""+itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

                // 发送消息给主线程
                if(itemList == null){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }else{
                    fll_null.setVisibility(View.GONE);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadItems_Net();
    }

}