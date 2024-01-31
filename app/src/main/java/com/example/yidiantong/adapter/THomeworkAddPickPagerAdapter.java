package com.example.yidiantong.adapter;


import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.THomeworkAddEntity;
import com.example.yidiantong.fragment.THomeworkAddItemFragment;

import java.util.List;

public class THomeworkAddPickPagerAdapter extends FragmentStatePagerAdapter {

    public List<THomeworkAddEntity> itemList; // 试题内容

    @SuppressLint("WrongConstant")
    public THomeworkAddPickPagerAdapter(@NonNull FragmentManager fm, List<THomeworkAddEntity> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        itemList = list;
    }

    public void update(List<THomeworkAddEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return THomeworkAddItemFragment.newInstance(itemList.get(position));
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

}
