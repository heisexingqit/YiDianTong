package com.example.yidiantong.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.ui.HomeworkPagerActivity;

public class MainHomeFragment extends Fragment implements View.OnClickListener {

    private ImageView iv_search_select;

    //获得实例，并绑定参数
    public static MainHomeFragment newInstance(){
        MainHomeFragment fragment = new MainHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        rv_home.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //设置RecyclerViewAdapter
        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getContext(), 0, new HomeRecyclerAdapter.MyListener() {
            @Override
            public void onClick(int pos) {
                startActivity(new Intent(getActivity(), HomeworkPagerActivity.class));
            }
        });
        rv_home.setAdapter(adapter);

        //弹出搜索栏菜单
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        return view;
    }

    @Override
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
}