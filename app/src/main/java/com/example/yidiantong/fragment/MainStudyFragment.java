package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
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

import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.ui.HomeworkPagerActivity;

public class MainStudyFragment extends Fragment {

    //int[] type = {1, 2, 3, 4};

    //获得实例，并绑定参数
    public static MainStudyFragment newInstance(){
        MainStudyFragment fragment = new MainStudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        rv_home.addItemDecoration(divider);

        //设置RecyclerViewAdapter
        //HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(getContext(), type);
        //adapter.setMyListener(pos -> startActivity(new Intent(getActivity(), HomeworkPagerActivity.class)));
        //rv_home.setAdapter(adapter);
        return view;
    }
}