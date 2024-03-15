package com.example.yidiantong.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BookExerciseAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BookExerciseActivity extends AppCompatActivity {
    private static final String TAG = "BookExerciseActivity";
    private RecyclerView rv_main;
    private BookExerciseAdapter adapter;
    private String questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_exercise);
        questionId = getIntent().getStringExtra("questionId");
        Log.e("wen0314", "onCreate: " + questionId);
        //获取组件
        rv_main = findViewById(R.id.rv_main);

        //RecyclerView两步必要配置
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());
        //设置RecyclerViewAdapter
        if (adapter == null) {
            adapter = new BookExerciseAdapter(this, new ArrayList<>());
        }
        rv_main.setAdapter(adapter);
        loadItems_Net();
    }

    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.T_GET_TIFEN_TRAIN + "?userName=" + MyApplication.username + "&questionId=" + questionId;
        Log.e("wen0314", "loadItems_Net: " + mRequestUrl);
        StringRequest stringRequest = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");

                Gson gson = new Gson();
                // 使用Goson框架转换Json字符串为列表
                List<BookExerciseEntity> moreList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {
                }.getType());
                Log.e("wen0314", "loadItems_Net: " + moreList.size());
                adapter.update(moreList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            //加载失败
            Log.e("volley", "loadItems_Net: ");
        });

        MyApplication.addRequest(stringRequest, "TAG");

    }
}