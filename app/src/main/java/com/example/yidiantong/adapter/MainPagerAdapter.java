package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.fragment.BookDetailFragment;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {


    List<Fragment> mFragments = new ArrayList<>();

    @SuppressLint("WrongConstant")
    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments.add(MainHomeFragment.newInstance());
        mFragments.add(MainStudyFragment.newInstance());
        mFragments.add(MainCourseFragment.newInstance());
        mFragments.add(MainBookFragment.newInstance());
        mFragments.add(MainMyFragment.newInstance());
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
        }else if(position == 5){
            return BookDetailFragment.newInstance();
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
