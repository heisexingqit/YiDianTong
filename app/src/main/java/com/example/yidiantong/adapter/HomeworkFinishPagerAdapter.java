package com.example.yidiantong.adapter;


import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.HomeworkMarkedEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.fragment.HomeworkFinishFragment;
import com.example.yidiantong.fragment.HomeworkJudgeFragment;
import com.example.yidiantong.fragment.HomeworkMultipleFragment;
import com.example.yidiantong.fragment.HomeworkReadingFragment;
import com.example.yidiantong.fragment.HomeworkSeven2FiveFragment;
import com.example.yidiantong.fragment.HomeworkSingleFragment;
import com.example.yidiantong.fragment.HomeworkTranslationFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeworkFinishPagerAdapter extends FragmentPagerAdapter {

    private List<HomeworkMarkedEntity> itemList = new ArrayList<>();// 批改内容
    private String paperId;

    @SuppressLint("WrongConstant")
    public HomeworkFinishPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void update(List<HomeworkMarkedEntity> itemList,String paperId) {
        this.itemList = itemList;
        this.paperId=paperId;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return HomeworkFinishFragment.newInstance(itemList.get(position), position, itemList.size(),paperId);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

}
