package com.example.yidiantong.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.fragment.BookFragment;
import com.example.yidiantong.fragment.CourseFragment;
import com.example.yidiantong.fragment.HomeFragment;
import com.example.yidiantong.fragment.MineFragment;
import com.example.yidiantong.fragment.StudyFragment;

public class StudyPagerAdapter extends FragmentPagerAdapter {


    public StudyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return HomeFragment.newInstance();
        }else if(position == 1){
            return StudyFragment.newInstance();
        }else if(position == 2){
            return CourseFragment.newInstance();
        }else if(position == 3){
            return BookFragment.newInstance();
        }else if(position == 4){
            return MineFragment.newInstance();
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
