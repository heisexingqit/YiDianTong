package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.ui.LoginActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtil;
import com.example.yidiantong.util.MyItemDecoration;
import com.example.yidiantong.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainHomeFragment";
    private ImageView iv_search_select;

    //获得实例，并绑定参数
    public static MainHomeFragment newInstance(){
        MainHomeFragment fragment = new MainHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String type = "all";

    //列表数据
    private List<HomeItemEntity> itemList = new ArrayList<>();
    HomeRecyclerAdapter adapter;
    private int isRefresh = 0;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        //获取组件
        RecyclerView rv_home = view.findViewById(R.id.rv_home);

        //RecyclerView两步必要配置
        rv_home.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        MyItemDecoration divider = new MyItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, false);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        rv_home.addItemDecoration(divider);

        //获取登录传递的参数
        username =  getActivity().getIntent().getStringExtra("username");


        //设置RecyclerViewAdapter
        adapter = new HomeRecyclerAdapter(getContext(), itemList);
        adapter.setmItemClickListener((v, pos) -> startActivity(new Intent(getActivity(), HomeworkPagerActivity.class)));
        rv_home.setAdapter(adapter);

        //弹出搜索栏菜单
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        //下拉刷新
        SwipeRefreshLayout swipeRf = view.findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(()->{
            swipeRf.setRefreshing(true);
            currentPage = 1;
            isRefresh = 1;
            loadItems_Net();
            swipeRf.setRefreshing(false);
        });

        //下拉加载
        rv_home.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //记录当前可见的底部item序号
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == recyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 >= adapter.getItemCount() && adapter.isDown == 0){
                    loadItems_Net();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }
        });

        //请求数据放后面
        loadItems_Net();
        return view;
    }


    @Override/**/
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_search_select:
                View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_search_select, null, false);

                PopupWindow window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                window.setTouchable(true);
                window.showAsDropDown(iv_search_select, -150, 0);
                break;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                if(isRefresh == 1){
                    itemList = (List<HomeItemEntity>) message.obj;
                    isRefresh = 0;
                }else{
                    itemList.addAll((List<HomeItemEntity>) message.obj);
                }
                adapter.notifyDataSetChanged();
                currentPage += 1;
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        String mRequestUrl = Constant.API + Constant.NEW_ITEM + "?currentPage=" + currentPage + "&userId=" + username + "&resourceType=" + type;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtil.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                //使用Gson框架转换电影列表
                List<HomeItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<HomeItemEntity>>() {}.getType());

                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = moreList;
                // 发送消息给主线程
                if(moreList.size() < 12){
                    adapter.isDown = 1;
                }
                //标识线程
                message.what = 100;
                handler.sendMessage(message);
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainHomeFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                adapter.fail();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }
}