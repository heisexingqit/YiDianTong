package com.example.yidiantong.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.THomeworkPreviewAdapter;
import com.example.yidiantong.bean.HomeworkPreviewEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class THomeworkPreviewActivity extends AppCompatActivity {

    private static final String TAG = "THomeworkPreviewActivit";
    private String paperId;
    private String homeworkName;
    private TextView tv_title;
    private RecyclerView rv_main;
    private THomeworkPreviewAdapter adapter;
    private RelativeLayout rl_loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_preview);
        paperId = getIntent().getStringExtra("paperId");
        homeworkName = getIntent().getStringExtra("homeworkName");
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());
        tv_title = findViewById(R.id.tv_title);
        tv_title.setText(homeworkName);

        // RecyclerView初始化
        rv_main = findViewById(R.id.rv_main);
        //RecyclerView两步必要配置
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());

        adapter = new THomeworkPreviewAdapter(this, new ArrayList<>());
        rv_main.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        rv_main.setAdapter(adapter);
        rl_loading = findViewById(R.id.rl_loading);

        loadItems_Net();
    }

    private void loadItems_Net() {
        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_PREVIEW + "?paperId=" + paperId;

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                String msg = json.getString("message");
                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<HomeworkPreviewEntity> moreList = gson.fromJson(itemString, new TypeToken<List<HomeworkPreviewEntity>>() {
                }.getType());
                Log.e("wen0613", "loadItems_Net: " + moreList);
                for(HomeworkPreviewEntity item : moreList){
                    item.show = false;
                }
                if(moreList.size() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(msg.replace("。",""));
                    //设置title组件
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    //禁止返回和外部点击
                    builder.setCancelable(false);
                    //对话框弹出
                    builder.show();
                }

                adapter.update(moreList);
                rl_loading.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }
}