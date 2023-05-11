package com.example.yidiantong.adapter;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.fragment.HomeworkJudgeFragment;
import com.example.yidiantong.fragment.HomeworkMultipleFragment;
import com.example.yidiantong.fragment.HomeworkReadingFragment;
import com.example.yidiantong.fragment.HomeworkSeven2FiveFragment;
import com.example.yidiantong.fragment.HomeworkSingleFragment;
import com.example.yidiantong.fragment.HomeworkTranslationFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeworkPagerAdapter extends FragmentPagerAdapter {

    private List<HomeworkEntity> itemList = new ArrayList<>();//题面列表
    private List<StuAnswerEntity> itemList2 = new ArrayList<>();//答题情况列表

    public int countReady = 0;

    //传递信息
    private String learnPlanId, username;

    public HomeworkPagerAdapter(@NonNull FragmentManager fm, String learnPlanId, String username) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.learnPlanId = learnPlanId;
        this.username = username;
    }

    public void updateQ(List<HomeworkEntity> itemList) {
        this.itemList = itemList;
        countReady += 1;
        if (countReady >= 2) {
            this.notifyDataSetChanged();
        }
    }

    public void updateA(List<StuAnswerEntity> itemList) {
        this.itemList2 = itemList;
        countReady += 1;
        if (countReady >= 2) {
            this.notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        String questionTypeName = itemList.get(position).getQuestionTypeName();
        switch (questionTypeName) {
            case "单项选择题":
            case "单选题":
                fragment = HomeworkSingleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "多项选择题":
                fragment = HomeworkMultipleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "判断题":
                fragment = HomeworkJudgeFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "简答题":
            case "填空题":
            case "书面表达题":
            case "单词拼写":
                fragment = HomeworkTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, username, itemList2.get(position));
                break;
            case "七选五":
                fragment = HomeworkSeven2FiveFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "阅读理解题":
            case "完形填空题":
                fragment = HomeworkReadingFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            default:
                Log.d("wen", "未知题型: " + questionTypeName);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

}
