package com.example.yidiantong.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.ReadTaskResultEntity;
import com.example.yidiantong.fragment.ReadAloudSubmitFragment;

import java.util.ArrayList;
import java.util.List;

public class ReadAloudSubmitPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "ReadAloudSubmitPagerAdapter";

    private List<ReadTaskResultEntity> itemList = new ArrayList<>(); // 结果列表

    //传递信息
    private String learnPlanId, username;

    public ReadAloudSubmitPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void update(List<ReadTaskResultEntity> readTaskResultList){
        this.itemList = readTaskResultList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ReadAloudSubmitFragment.newInstance(itemList.get(position), itemList.size());
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE; // 这样可以保证notifyDataSetChanged时，所有Fragment都会被重新创建
    }
}
