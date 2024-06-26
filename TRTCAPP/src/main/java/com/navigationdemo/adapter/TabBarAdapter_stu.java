package com.navigationdemo.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TabBarAdapter_stu extends FragmentPagerAdapter {

    private String[] mTitles = new String[]{"视频列表", "聊天讨论"};

    public TabBarAdapter_stu(@NonNull FragmentManager fm) {
        super(fm);
    }


    private List<Fragment> mFragment;

    public List<Fragment> getmFragment() {
        return mFragment;
    }

    public void setmFragment(List<Fragment> mFragment) {
        this.mFragment = mFragment;
    }

    public void setTitle(String[] title) {
        mTitles = title;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragment.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
