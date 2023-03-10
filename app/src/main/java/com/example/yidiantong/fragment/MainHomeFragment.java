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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ThemedSpinnerAdapter;
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
import java.util.Iterator;
import java.util.List;

public class MainHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainHomeFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private View loadingView = null;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window;
    private RecyclerView rv_home;
    private RelativeLayout rl_loading;

    //??????????????????????????????
    public static MainHomeFragment newInstance(){
        MainHomeFragment fragment = new MainHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //??????????????????
    private int currentPage = 1;
    private String username;
    private String type = "all";

    //????????????
    private List<HomeItemEntity> itemList = new ArrayList<>();
    HomeRecyclerAdapter adapter;

    //??????
    private EditText et_search;
    private String searchStr = "";



    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        //????????????
        rv_home = view.findViewById(R.id.rv_home);

        //RecyclerView??????????????????
        rv_home.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());

        //???????????????
        MyItemDecoration divider = new MyItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, false);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        rv_home.addItemDecoration(divider);

        //???????????????????????????
        username =  getActivity().getIntent().getStringExtra("username");

        //??????RecyclerViewAdapter
        adapter = new HomeRecyclerAdapter(getContext(), itemList);
        adapter.setmItemClickListener((v, pos) -> startActivity(new Intent(getActivity(), HomeworkPagerActivity.class)));
        rv_home.setAdapter(adapter);

        //?????????????????????
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        //????????????
        swipeRf = view.findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(()->{
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        //????????????
        rv_home.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //???????????????????????????item??????
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

        //?????????
        rl_loading = view.findViewById(R.id.rl_loading);

        //?????????????????????
        //loadItems_Net();

        //????????????
        view.findViewById(R.id.tv_search).setOnClickListener(this);
        et_search = view.findViewById(R.id.et_search);

        return view;
    }
    //????????????
    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
        rv_home.scrollToPosition(0);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_search_select:
                if(contentView == null){
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_search_select, null, false);
                    //??????????????????
                    contentView.findViewById(R.id.tv_all).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_3).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_4).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_7).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, -150, 0);
                break;
            case R.id.tv_all:
                if(!type.equals("all")){
                    type = "all";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if(!type.equals("1")) {
                    type = "1";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_2:
                if(!type.equals("2")) {
                    type = "2";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_3:
                if(!type.equals("3")) {
                    type = "3";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_4:
                if(!type.equals("4")) {
                    type = "4";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_7:
                if(!type.equals("7")) {
                    type = "7";
                    refreshList();
                }
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

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 111");
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

    //???????????????????????????????????????????????????upDown??????????????????
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
                //??????Goson????????????Json??????????????????
                List<HomeItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<HomeItemEntity>>() {}.getType());

                //?????????????????????????????????
                Message message = Message.obtain();

                message.obj = moreList;
                // ????????????????????????
                if(moreList.size() < 12){
                    adapter.isDown = 1;
                }
                //????????????
                message.what = 100;
                handler.sendMessage(message);
            }catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(MainHomeFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            adapter.fail();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    //?????????
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