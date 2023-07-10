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
    private static final String TAG = "HomeworkPagerAdapter";

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
                fragment = HomeworkTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, username, itemList2.get(position));
                break;
            case "108":
                if(item.getQuestionTypeName().equals("七选五")){
                    fragment = HomeworkSeven2FiveFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                }else{
                    fragment = HomeworkReadingFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                }
                break;
            default:
                Log.d("wen", "未知题型: " + item.getQuestionTypeName());
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

}
