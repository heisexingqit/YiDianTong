package com.example.yidiantong.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.fragment.BookDetailSingleFragment;
import com.example.yidiantong.fragment.CourseQuestionFragment;

public class CourseQuestionAdapter extends FragmentStatePagerAdapter {


    public CourseQuestionAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        fragment = CourseQuestionFragment.newInstance();
        return fragment;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
