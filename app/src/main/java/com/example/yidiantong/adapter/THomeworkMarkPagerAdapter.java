package com.example.yidiantong.adapter;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.yidiantong.bean.THomeworkMarkedEntity;
import com.example.yidiantong.fragment.THomeworkMarkFragment;

import java.util.ArrayList;
import java.util.List;

public class THomeworkMarkPagerAdapter extends FragmentStateAdapter {


    private List<THomeworkMarkedEntity> itemList = new ArrayList<>();// 批改内容
    private Boolean canMark;

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return THomeworkMarkFragment.newInstance(itemList.get(position), position, itemList.size(), canMark);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public THomeworkMarkPagerAdapter(FragmentActivity fragmentActivity, Boolean canMark) {
        super(fragmentActivity);
        this.canMark = canMark;
    }

    public void update(List<THomeworkMarkedEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        // 返回数据对象的哈希值作为条目的唯一标识符
        return itemList.get(position).hashCode();
    }
}
