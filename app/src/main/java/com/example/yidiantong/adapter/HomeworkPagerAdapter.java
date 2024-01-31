package com.example.yidiantong.adapter;


import android.content.res.Configuration;
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
    private static final String TAG = "HomeworkPagerAdapter";

    private List<HomeworkEntity> itemList = new ArrayList<>();//题面列表
    private List<StuAnswerEntity> itemList2 = new ArrayList<>();//答题情况列表

    //传递信息
    private String learnPlanId, username;

    public HomeworkPagerAdapter(@NonNull FragmentManager fm, String learnPlanId, String username) {
        super(fm);
        this.learnPlanId = learnPlanId;
        this.username = username;
    }

    public void update(List<HomeworkEntity> itemList, List<StuAnswerEntity> itemList2) {
        this.itemList = itemList;
        this.itemList2 = itemList2;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        HomeworkEntity item = itemList.get(position);

        switch (item.getBaseTypeId()) {
            case "101":
                fragment = HomeworkSingleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "102":
                fragment = HomeworkMultipleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "103":
                fragment = HomeworkJudgeFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                break;
            case "104":
            case "105":
            case "106":
            case "107":
            case "109":
            case "110":
            case "111":
            case "112":
                fragment = HomeworkTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, username, itemList2.get(position));
                break;
            case "108":
                if (item.getQuestionTypeName().equals("七选五")) {
                    fragment = HomeworkSeven2FiveFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                } else {
                    fragment = HomeworkReadingFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                }
                break;
            default:
                fragment = HomeworkTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, username, itemList2.get(position));
                Log.d("wen", "未知题型: " + item.getBaseTypeId());
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList2.size();
    }
}
