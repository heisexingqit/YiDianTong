package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksRecoverAdapter;
import com.example.yidiantong.bean.BookDetailEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RemoveInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookRecoverActivity extends AppCompatActivity implements RemoveInterface {

    private String course_name;
    private RecyclerView frv_recover;
    private String username;
    private String coures_id;
    private BooksRecoverAdapter adapter;
    private String itemString;

    //列表数据
    private List<BookDetailEntity> itemList = new ArrayList<>();
    private List<BookDetailEntity.errorList> errorList = new ArrayList<>();
    private List<BookDetailEntity.errorList> quesList = new ArrayList<>();
    private String item;
    private LinearLayout fll_null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recover);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        //获取Intent参数
        course_name = getIntent().getStringExtra("name");
        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "错题回收站");
        tv_title.setTextColor(Color.BLACK);

        frv_recover = findViewById(R.id.frv_recover);
        //RecyclerView两步必要配置
        frv_recover.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frv_recover.setItemAnimator(new DefaultItemAnimator());
        // 获取Intent参数
        username = this.getIntent().getStringExtra("username");
        coures_id = this.getIntent().getStringExtra("subjectId");
        //设置RecyclerViewAdapter
        adapter = new BooksRecoverAdapter(this, errorList, itemList, quesList);
        frv_recover.setAdapter(adapter);

        fll_null = findViewById(R.id.fll_null);

        loadItems_Net();
    }



    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.loadData((List<BookDetailEntity.errorList>) message.obj);
            }else if(message.what == 101){
                adapter.loadData3((List<BookDetailEntity>) message.obj);
            }else {
                adapter.loadData2((List<BookDetailEntity.errorList>) message.obj);
            }
        }
    };


    private void loadItems_Net() {
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_GET_QUESTION_RECYCLE + "?subjectId=" + coures_id +"&userName=" +username +"&currentPage=1";
        Log.e("回收站mRequestUrl",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                String itemString = "";  // 所有题目表
                String itemStringnew = "";  // 所有题目表
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONArray error = json.getJSONArray("data");
                item = json.getString("data");
                System.out.println("item ^_^" + item);
                String error1 = json.getString("data");
                // 标题+所有题目表
                for(int j=0; j<error.length();j++){
                    String alitre0 = error.get(j).toString();
                    alitre0 = alitre0 + ",";
                    itemString += alitre0;
                    Log.e("jitemString","" + itemString);
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[","");
                    String itemre2 = itemre1.replace("]","");
                    if(j != error.length()-1){
                        itemre2 = itemre2 + ",";
                    }
                    itemString += itemre2;
                }

                // 所有题目表
                for(int j=0; j<error.length();j++){
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[","");
                    String itemre2 = itemre1.replace("]","");
                    if(j != error.length()-1){
                        itemre2 = itemre2 + ",";
                    }
                    itemStringnew += itemre2;
                }
                itemStringnew = "[" + itemStringnew + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity.errorList> quesList =gson.fromJson(itemStringnew, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());
                Log.e("quesList",""+quesList);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = quesList;

                //标识线程
                message.what = 102;
                handler.sendMessage(message);

                itemString = "[" + itemString + "]";

                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity.errorList> moreList =gson.fromJson(itemString, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());

                List moreList1 = new ArrayList();

                //封装消息，传递给主线程
                Message message1 = Message.obtain();
                message1.obj = moreList;

                // 发送消息给主线程
                if(moreList == null){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }else if(moreList.equals(moreList1)){
                    fll_null.setVisibility(View.VISIBLE);
                }else{
                    fll_null.setVisibility(View.GONE);
                }
                //标识线程
                message1.what = 100;
                handler.sendMessage(message1);

            }catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                item = json.getString("data");

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity> almostList = gson.fromJson(item, new TypeToken<List<BookDetailEntity>>() {}.getType());

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = almostList;
                String moreList1 = "[]";
                if(item.equals(moreList1)){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }
                //标识线程
                message.what = 101;
                handler.sendMessage(message);
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);

    }

    private final Handler handler2 = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                int f = (int) message.obj;
                if (f == 0) {
                    Toast.makeText(getApplicationContext(), "恢复失败", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "恢复成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void restore(String questionId) {
        String sourceId = getIntent().getStringExtra("sourceId");
        String subjectId = getIntent().getStringExtra("subjectId");
        questionId = questionId;
        String userName = getIntent().getStringExtra("username");
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_REMOVE + "?subjectId=" + subjectId  +"&userName=" +userName +"&questionId=" + questionId + "&sourceId=" + sourceId;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                Message msg = Message.obtain();
                if (isSuccess) {
                    msg.obj = 1;
                } else {
                    msg.obj = 0;
                }
                msg.what = 100;
                handler2.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void update() {
        loadItems_Net();
    }
}