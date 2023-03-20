package com.example.yidiantong.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.fragment.HomeworkClozeFragment;
import com.example.yidiantong.fragment.HomeworkJudgeFragment;
import com.example.yidiantong.fragment.HomeworkMultipleFragment;
import com.example.yidiantong.fragment.HomeworkReadingFragment;
import com.example.yidiantong.fragment.HomeworkSingleFragment;
import com.example.yidiantong.fragment.HomeworkTranslationFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeworkPagerAdapter extends FragmentPagerAdapter {

    private List<HomeworkEntity> itemList = new ArrayList<>();

    public HomeworkPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void update(List<HomeworkEntity> itemList){
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (itemList.get(position).getQuestionTypeName()){
            case "单项选择题": fragment = HomeworkSingleFragment.newInstance(itemList.get(position), position, itemList.size()) ;
            break;

            default: ;
        }
        return fragment;



//        if(position == 0){
//            return HomeworkSingleFragment.newInstance();
//        }else if(position == 1){
//            return HomeworkMultipleFragment.newInstance();
//        }else if(position == 2){
//            return HomeworkJudgeFragment.newInstance();
//        }else if(position == 3){
//            return HomeworkTranslationFragment.newInstance();
//        }else if(position == 4){
//            return HomeworkReadingFragment.newInstance();
//        }else if(position == 5){
//            return HomeworkClozeFragment.newInstance();
//        }else{
//            return null;
//        }
    }

    @Override
    public int getCount() {
        return itemList.size();
    }
}
