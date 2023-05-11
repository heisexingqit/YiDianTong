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
import com.example.yidiantong.fragment.TMainBellFragment;
import com.example.yidiantong.fragment.TMainLatestFragment;
import com.example.yidiantong.fragment.TMainMyFragment;
import com.example.yidiantong.fragment.TMainReportFragment;
import com.example.yidiantong.fragment.TMainTeachFragment;

import java.util.ArrayList;
import java.util.List;

public class TMainPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> mFragments = new ArrayList<>();

    public TMainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments.add(TMainLatestFragment.newInstance());
        mFragments.add(TMainReportFragment.newInstance());
        mFragments.add(TMainTeachFragment.newInstance());
        mFragments.add(TMainBellFragment.newInstance());
        mFragments.add(TMainMyFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
