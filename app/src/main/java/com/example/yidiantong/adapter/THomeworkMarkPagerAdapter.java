package com.example.yidiantong.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.THomeworkMarkedEntity;
import com.example.yidiantong.fragment.THomeworkMarkFragment;

import java.util.ArrayList;
import java.util.List;

public class THomeworkMarkPagerAdapter extends FragmentPagerAdapter {


    private List<THomeworkMarkedEntity> itemList = new ArrayList<>();// 批改内容
    private Boolean canMark;


    public THomeworkMarkPagerAdapter(@NonNull FragmentManager fm, Boolean canMark) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.canMark = canMark;
    }

    public void update(List<THomeworkMarkedEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return THomeworkMarkFragment.newInstance(itemList.get(position), position, itemList.size(), canMark);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }
}
