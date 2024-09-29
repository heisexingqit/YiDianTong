package com.example.yidiantong.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.fragment.ReadAloudSubmitFragment;

import java.util.ArrayList;
import java.util.List;

public class ReadAloudSubmitPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "ReadAloudSubmitPagerAdapter";

    private List<String> itemList = new ArrayList<>(); // 图片列表
    private String recordId;

    //传递信息
    private String learnPlanId, username;

    public ReadAloudSubmitPagerAdapter(@NonNull FragmentManager fm, String recordId) {
        super(fm);
        this.recordId = recordId;
    }

    public void update(List<String> itemList){
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ReadAloudSubmitFragment.newInstance(recordId, itemList.get(position), position, itemList.size());
    }

    @Override
    public int getCount() {
        return itemList.size();
    }
}
