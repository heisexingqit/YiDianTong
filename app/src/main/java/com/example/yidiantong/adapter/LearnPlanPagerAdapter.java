package com.example.yidiantong.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.fragment.HomeworkJudgeFragment;
import com.example.yidiantong.fragment.HomeworkMultipleFragment;
import com.example.yidiantong.fragment.HomeworkReadingFragment;
import com.example.yidiantong.fragment.HomeworkSeven2FiveFragment;
import com.example.yidiantong.fragment.HomeworkSingleFragment;
import com.example.yidiantong.fragment.HomeworkTranslationFragment;
import com.example.yidiantong.fragment.LearnPlanSingleFragment;

import java.util.ArrayList;
import java.util.List;

public class LearnPlanPagerAdapter extends FragmentPagerAdapter {

    private List<LearnPlanItemEntity> itemList;//题面列表

    //传递信息
    private String learnPlanId;

    public LearnPlanPagerAdapter(@NonNull FragmentManager fm, String learnPlanId, List<LearnPlanItemEntity> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.learnPlanId = learnPlanId;
        itemList = list;
    }

    public void update(List<LearnPlanItemEntity> list) {
        itemList = list;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        String questionTypeName = itemList.get(position).getResourceName();
        switch (questionTypeName) {
//            case "单项选择题":
//            case "单选题":
            default:
                fragment = LearnPlanSingleFragment.newInstance(itemList.get(position), position, itemList.size());
                break;
//            case "多项选择题":
//                fragment = HomeworkMultipleFragment.newInstance(itemList.get(position), position, itemList.size());
//                break;
//            case "判断题":
//                fragment = HomeworkJudgeFragment.newInstance(itemList.get(position), position, itemList.size());
//                break;
//            case "简答题":
//            case "填空题":
//            case "书面表达题":
//            case "单词拼写":
//            case "解答题":
//                fragment = HomeworkTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, username, itemList2.get(position));
//                break;
//            case "七选五":
//                fragment = HomeworkSeven2FiveFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
//                break;
//            case "阅读理解题":
//            case "完形填空题":
//                fragment = HomeworkReadingFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
//                break;
//            default:
//                Log.d("wen", "未知题型: " + questionTypeName);
//                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

}
