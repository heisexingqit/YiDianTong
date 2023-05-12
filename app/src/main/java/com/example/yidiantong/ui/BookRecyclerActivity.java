package com.example.yidiantong.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksRecyclerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;


public class BookRecyclerActivity extends AppCompatActivity implements RecyclerInterface {

    private String course_name;
    private ViewPager fvp_book_recycle;
    private BooksRecyclerAdapter adapter;
    private int currentItem = 0;
    private int pageCount = 0;


    // 接口参数
    String sourceId;
    String userName;
    String subjectId;
    String questionId;
    private String pos;
    private String num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recycle);

        fvp_book_recycle = findViewById(R.id.fvp_book_recycle);
        adapter = new BooksRecyclerAdapter(getSupportFragmentManager());
        fvp_book_recycle.setAdapter(adapter);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });


        //获取Intent参数
        course_name = getIntent().getStringExtra("name");
        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "错题详情");
        subjectId = getIntent().getStringExtra("subjectId");
        questionId = getIntent().getStringExtra("questionId");
        userName = getIntent().getStringExtra("username");
        pos = getIntent().getStringExtra("pos");

        currentItem = Integer.valueOf(pos);
        fvp_book_recycle.setCurrentItem(currentItem);


        loadItems_Net(currentItem);

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
    public void pageLast(String currentpage, String allpage) {
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
            builder.setNegativeButton("ok", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem -= 1;
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    @Override
    public void pageNext(String currentpage, String allpage) {
        int allpage1 = Integer.parseInt(allpage);
        if (currentItem == allpage1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经最后一题了");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("ok", null);
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

    // 更新页面
    @Override
    public void updatepage(String currentpage, String allpage) {
        int currentpage1 = Integer.parseInt(currentpage);
        int allpage1 = Integer.parseInt(allpage);
        if(allpage1 == 1){
           this.finish();
        }else if(currentpage1 == allpage1) {
            currentpage1 -= 1;
            loadItems_Net(currentpage1);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }else{
           loadItems_Net(currentpage1);
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
                List<BookRecyclerEntity> list = (List<BookRecyclerEntity>)message.obj;
                adapter.update(list);
            }
        }
    };

    // 获取错题详情信息
    private void loadItems_Net(int pos) {
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_ANSWER_QUESTION + "?sourceId=" + sourceId +"&userName=" +userName +"&subjectId=" + subjectId +"&currentPage=" + pos;
        Log.e("详细mRequestUrl",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                itemString = "["+itemString+"]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookRecyclerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<BookRecyclerEntity>>() {}.getType());

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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        queue.getCache().clear();
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

}