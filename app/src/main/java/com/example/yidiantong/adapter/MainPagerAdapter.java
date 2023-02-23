package com.example.yidiantong.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {


    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return MainHomeFragment.newInstance();
        }else if(position == 1){
            return MainStudyFragment.newInstance();
        }else if(position == 2){
            return MainCourseFragment.newInstance();
        }else if(position == 3){
            return MainBookFragment.newInstance();
        }else if(position == 4){
            return MainMyFragment.newInstance();
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
