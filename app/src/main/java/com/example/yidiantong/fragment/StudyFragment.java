package com.example.yidiantong.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.ui.HomeworkPagerActivity;

public class StudyFragment extends Fragment {

    //获得实例，并绑定参数
    public static StudyFragment newInstance(){
        StudyFragment fragment = new StudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //获取组件
        RecyclerView rv_home = view.findViewById(R.id.rv_home);

        //设置RecyclerViewAdapter
        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getContext(), 1, new HomeRecyclerAdapter.MyListener() {
            @Override
            public void onClick(int pos) {
                startActivity(new Intent(getActivity(), HomeworkPagerActivity.class));
            }
        });

        //RecyclerView两步必要配置
        rv_home.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());
        //添加间隔线
        rv_home.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        rv_home.setAdapter(adapter);
        return view;
    }
}