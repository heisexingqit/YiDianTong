package com.example.yidiantong.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.ReadTaskEntity;
import com.example.yidiantong.fragment.ReadAloudLookFragment;

import java.util.ArrayList;
import java.util.List;

public class ReadAloudLookPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "ReadAloudLookPagerAdapter";

    private List<ReadTaskEntity> itemList = new ArrayList<>(); //题面列表
    private String type;
    //传递信息
    private String learnPlanId, username;

    public ReadAloudLookPagerAdapter(@NonNull FragmentManager fm, String type) {
        super(fm);
        this.type = type;
    }

    public void update(List<ReadTaskEntity> itemList){
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ReadAloudLookFragment.newInstance(itemList.get(position), position, itemList.size(), type);
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
