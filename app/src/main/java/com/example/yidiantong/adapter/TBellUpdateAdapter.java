package com.example.yidiantong.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.fragment.TBellAnnounceSubmitFragment;
import com.example.yidiantong.fragment.TBellAnnounceUpdateFragment;
import com.example.yidiantong.fragment.TBellNoticeSubmitFragment;
import com.example.yidiantong.fragment.TBellNoticeUpdateFragment;

import java.util.ArrayList;
import java.util.List;

public class TBellUpdateAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TBellUpdateAdapter";

    List<Fragment> mFragments = new ArrayList<>();

    public TBellUpdateAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments.add(TBellNoticeUpdateFragment.newInstance());
        mFragments.add(TBellAnnounceUpdateFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e("position",""+position);
        if(position == 0){
            Log.e("去到修改通知页面","");
            return TBellNoticeUpdateFragment.newInstance();
        }else if(position == 1){
            Log.e("去到修改公告页面","");
            return TBellAnnounceUpdateFragment.newInstance();
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


}
