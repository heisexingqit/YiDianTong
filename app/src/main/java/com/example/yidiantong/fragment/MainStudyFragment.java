package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtil;
import com.example.yidiantong.util.MyItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainStudyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainStudyFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window;
    private RecyclerView rv_study;
    private RelativeLayout rl_loading;

    //获得实例，并绑定参数
    public static MainStudyFragment newInstance(){
        MainStudyFragment fragment = new MainStudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String type = "9";

    //列表数据
    private List<HomeItemEntity> itemList = new ArrayList<>();
    HomeRecyclerAdapter adapter;

    //搜索
    private EditText et_search;
    private String searchStr = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_study, container, false);

        //获取组件
        rv_study = view.findViewById(R.id.rv_study);

        //RecyclerView两步必要配置
        rv_study.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_study.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        MyItemDecoration divider = new MyItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, false);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        rv_study.addItemDecoration(divider);

        //获取登录传递的参数
        username =  getActivity().getIntent().getStringExtra("username");

        //设置RecyclerViewAdapter
        adapter = new HomeRecyclerAdapter(getContext(), itemList);
        adapter.setmItemClickListener((v, pos) -> startActivity(new Intent(getActivity(), HomeworkPagerActivity.class)));
        rv_study.setAdapter(adapter);

        //弹出搜索栏菜单
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        //下拉刷新
        swipeRf = view.findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(()->{
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        //下拉加载
        rv_study.setOnScrollListener(new RecyclerView.OnScrollListener() {
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

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);

        //请求数据放后面
        //loadItems_Net();

        //搜索信息
        view.findViewById(R.id.tv_search).setOnClickListener(this);
        et_search = view.findViewById(R.id.et_search);

        return view;
    }

    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
        rv_study.scrollToPosition(0);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_search_select:
                if(contentView == null){
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_search_select2, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_all).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_7).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, -150, 0);
                break;
            case R.id.tv_all:
                if(!type.equals("9")){
                    type = "9";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if(!type.equals("1")){
                    type = "1";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_2:
                if(!type.equals("2")){
                    type = "2";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_7:
                if(!type.equals("7")){
                    type = "7";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_search:
                if(!searchStr.equals(et_search.getText().toString())){
                    searchStr = et_search.getText().toString();
                    refreshList();
                }
                break;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.loadData((List<HomeItemEntity>) message.obj);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if(adapter.isRefresh == 1){
            rl_loading.setVisibility(View.VISIBLE);
        }
        String mRequestUrl = Constant.API + Constant.NEW_ITEM + "?currentPage=" + currentPage + "&userId=" + username + "&resourceType=" + type + "&searchStr=" + searchStr;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtil.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
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

        }, error -> {
            Toast.makeText(MainStudyFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            adapter.fail();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    //慢加载
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()){
                refreshList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()){
            refreshList();
        }
    }
}
