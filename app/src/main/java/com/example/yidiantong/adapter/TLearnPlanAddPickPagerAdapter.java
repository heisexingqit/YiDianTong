package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.LearnPlanAddItemEntity;
import com.example.yidiantong.fragment.TLearnPlanAddPaperFragment;
import com.example.yidiantong.fragment.TLearnPlanAddQuestionFragment;
import com.example.yidiantong.fragment.TLearnPlanAddPPTFragment;
import com.example.yidiantong.fragment.TLearnPlanAddVideoFragment;

import java.util.List;

public class TLearnPlanAddPickPagerAdapter extends FragmentStatePagerAdapter {

    public List<LearnPlanAddItemEntity> itemList; // 试题内容

    @SuppressLint("WrongConstant")
    public TLearnPlanAddPickPagerAdapter(@NonNull FragmentManager fm, List<LearnPlanAddItemEntity> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        itemList = list;
    }

    public void update(List<LearnPlanAddItemEntity> itemList) {
        this.itemList = itemList;
        Log.d("wen", "update: " + itemList.size());
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LearnPlanAddItemEntity item = itemList.get(position);
        switch (item.getType()) {
            case "question":
                return TLearnPlanAddQuestionFragment.newInstance(itemList.get(position));
            case "paper":
                return TLearnPlanAddPaperFragment.newInstance(itemList.get(position));
            case "resource":
                switch (item.getFormat()) {
                    case "ppt":
                        return TLearnPlanAddPPTFragment.newInstance(itemList.get(position));
                    case "video":
                    case "music":
                        return TLearnPlanAddVideoFragment.newInstance(itemList.get(position));
                    case "word":
                        return TLearnPlanAddPaperFragment.newInstance(itemList.get(position));
                    default:
                        Log.d("wen", "意外类型" + itemList.get(position));
                        return null;
                }
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }

}
