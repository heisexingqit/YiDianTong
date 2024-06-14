package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.fragment.HomeworkSeven2FiveFragment;
import com.example.yidiantong.fragment.LearnPlanJudgeFragment;
import com.example.yidiantong.fragment.LearnPlanMultipleFragment;
import com.example.yidiantong.fragment.LearnPlanPPTFragment;
import com.example.yidiantong.fragment.LearnPlanPaperFragment;
import com.example.yidiantong.fragment.LearnPlanReadingFragment;
import com.example.yidiantong.fragment.LearnPlanSingleFragment;
import com.example.yidiantong.fragment.LearnPlanTranslationFragment;
import com.example.yidiantong.fragment.LearnPlanVideoFragment;
import com.example.yidiantong.fragment.LearnPlanSeven2FiveFragment;

import java.util.ArrayList;
import java.util.List;

public class LearnPlanPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "LearnPlanPagerAdapter";

    public List<LearnPlanItemEntity> itemList = new ArrayList<>();//题面列表
    private List<StuAnswerEntity> itemList2 = new ArrayList<>();//答题情况列表

    //传递信息
    private String learnPlanId;

    @SuppressLint("WrongConstant")
    public LearnPlanPagerAdapter(@NonNull FragmentManager fm, String learnPlanId) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.learnPlanId = learnPlanId;
    }

    public void update(List<LearnPlanItemEntity> itemList, List<StuAnswerEntity> itemList2) {
        this.itemList = itemList;
        this.itemList2 = itemList2;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        LearnPlanItemEntity item = itemList.get(position);
        switch (item.getResourceType()){
            case "01":
                switch (item.getBaseTypeId()) {
                    case "101":
                        fragment = LearnPlanSingleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                        break;
                    case "102":
                        fragment = LearnPlanMultipleFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                        break;
                    case "103":
                        fragment = LearnPlanJudgeFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                        break;
                    case "104":
                    case "105":
                    case "106":
                    case "107":
                    case "109":
                    case "110":
                    case "111":
                    case "112":
                        fragment = LearnPlanTranslationFragment.newInstance(itemList.get(position), position, itemList.size(), learnPlanId, itemList2.get(position));
                        break;
                    case "108":
                        if(item.getResourceName().equals("七选五")){
                            fragment = LearnPlanSeven2FiveFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                        }else{
                            fragment = LearnPlanReadingFragment.newInstance(itemList.get(position), position, itemList.size(), itemList2.get(position));
                        }
                        break;
                    default:
                        Log.d("wen", "未知题型: " + itemList.get(position));
                        break;
                }
                break;
            case "02":
                fragment = LearnPlanPaperFragment.newInstance(itemList.get(position), itemList2.get(position));
                break;
            case "03":

                switch (item.getFormat()){
                    case "music":
                    case "video":
                        fragment = LearnPlanVideoFragment.newInstance(itemList.get(position), itemList2.get(position));
                        break;
                    case "ppt":
                        fragment = LearnPlanPPTFragment.newInstance(itemList.get(position), itemList2.get(position));
                        break;
                    default:
                        Log.d("wen", "导学案未知类型: " + item.getFormat());
                }

                break;
            default:
                Log.d(TAG, "（01,02,03）未知type: " + item.getResourceType());
        }
        return fragment;
    }

    @Override
    public int getCount() {
        Log.d("hsk0607", "itemList.size(): "+itemList.size());
        Log.d("hsk0607", "itemList2.size(): "+itemList2.size());
        return itemList2.size();

    }
}
