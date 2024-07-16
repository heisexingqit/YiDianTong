package com.example.yidiantong.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.BuildConfig;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.adapter.ResourceFolderRecyclerAdapter;
import com.example.yidiantong.bean.FolderResourceItem;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResourceFolderActivity extends AppCompatActivity {

    private static final String TAG = "ResourceFolderActivity";
    private RecyclerView rv_home;
    private MyItemDecoration divider;
    private ResourceFolderRecyclerAdapter adapter;

    private String mRequestUrl;
    private int currentPage = 1;

    private SwipeRefreshLayout swipeRf;
    private RelativeLayout rl_submitting;
    private TextView tv_submitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_folder);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());

        //获取组件
        rv_home = findViewById(R.id.rv_home);

        //RecyclerView两步必要配置
        rv_home.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        if (divider == null) {
            divider = new MyItemDecoration(this, DividerItemDecoration.VERTICAL, false);
            divider.setDrawable(getResources().getDrawable(R.drawable.divider_deep));
        }
        rv_home.addItemDecoration(divider);
        adapter = new ResourceFolderRecyclerAdapter(this, new ArrayList<>());
        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            getAndJumpToShow(pos);
        });

        rv_home.setAdapter(adapter);

        //下拉刷新
        swipeRf = findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(() -> {
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        //上拉加载
        rv_home.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //记录当前可见的底部item序号
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 >= adapter.getItemCount() && adapter.isDown == 0) {
                    loadItems_Net();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert lm != null;
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }
        });

        //慢加载，请求数据放后面
        if (adapter.itemList.size() == 0) {
            loadItems_Net();
        }

        // 请求资源遮罩
        rl_submitting = findViewById(R.id.rl_submitting);
        tv_submitting = findViewById(R.id.tv_submitting);
        tv_submitting.setText("请求资源中");
    }

    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
        rv_home.scrollToPosition(0);
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {


                /**
                 * 假0判断移至adapter中，根据refresh一起判断
                 */
            }
        }
    };

    private void loadItems_Net() {
        Log.e("wen0402", "onScrollStateChanged: SSSSSSSSSSSSSSSSS");

        // 获取本地app的版本名称

        mRequestUrl = Constant.API + Constant.GET_RESOURCE_FOLDER_ITEM + "?currentPage=" + currentPage + "&userId=" + MyApplication.username;

        Log.d("wen", "home: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<FolderResourceItem> moreList = gson.fromJson(itemString, new TypeToken<List<FolderResourceItem>>() {
                }.getType());

                if (moreList.size() > 0) {
                    // 只有非0才翻页，0不算
                    currentPage += 1;
                }
                adapter.loadData(moreList);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
            adapter.fail();
        });
        MyApplication.addRequest(request, TAG);
    }

    private void getAndJumpToShow(int pos) {
        rl_submitting.setVisibility(View.VISIBLE);
        FolderResourceItem item = adapter.itemList.get(pos);

        String type = "resource";
        if(item.getImgUrl().contains("doc")){
            type = "paper";
        }

        mRequestUrl = Constant.API + Constant.GET_RESOURCE_FOLDER_RESOURCE + "?id=" + item.getId() + "&type=" + type + "&deviceType=PHONE";

        Log.d("wen", "home: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                rl_submitting.setVisibility(View.GONE);
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                // 使用JSONObject解析这个字符串
                JSONObject jsonObject = new JSONObject(itemString);

                // 获取"url"键对应的值
                String url = jsonObject.getString("url");
                if(itemString.equals("null")||url.equals("预览文件不存在")){
                    // 取消遮罩
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("提示：该资源无法预览");
                    builder.setNegativeButton("关闭", null);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                }else{
                    Gson gson = new Gson();
                    LearnPlanItemEntity itemShow = gson.fromJson(itemString, LearnPlanItemEntity.class);
                    Intent intent = new Intent(ResourceFolderActivity.this, ResourceFolderShowActivity.class);
                    intent.putExtra("itemShow", itemShow);
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            rl_submitting.setVisibility(View.GONE);
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }
}