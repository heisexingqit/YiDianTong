package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.fragment.BookDetailFragment;
import com.example.yidiantong.fragment.BookDetailMultipleFragment;
import com.example.yidiantong.fragment.BookDetailReadingFragment;
import com.example.yidiantong.fragment.BookDetailSingleFragment;
import com.example.yidiantong.fragment.MainBookFragment;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.fragment.MainHomeFragment;
import com.example.yidiantong.fragment.MainMyFragment;
import com.example.yidiantong.fragment.MainStudyFragment;
import com.example.yidiantong.fragment.TBellAnnounceSubmitFragment;
import com.example.yidiantong.fragment.TBellAnnounceUpdateFragment;
import com.example.yidiantong.fragment.TBellNoticeSubmitFragment;
import com.example.yidiantong.fragment.TBellNoticeUpdateFragment;
import com.example.yidiantong.ui.TBellNoticeUpdateActivity;

import java.util.ArrayList;
import java.util.List;

public class TBellSubmitAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "TBellSubmitAdapter";

    List<Fragment> mFragments = new ArrayList<>();

    @SuppressLint("WrongConstant")
    public TBellSubmitAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments.add(TBellNoticeSubmitFragment.newInstance());
        mFragments.add(TBellAnnounceSubmitFragment.newInstance());
        mFragments.add(TBellNoticeUpdateFragment.newInstance());
        mFragments.add(TBellAnnounceUpdateFragment.newInstance());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e("position",""+position);
        if(position == 0){
            Log.e("去到发布通知页面","");
            return TBellNoticeSubmitFragment.newInstance();
        }else if(position == 1){
            Log.e("去到发布公告页面","");
            return TBellAnnounceSubmitFragment.newInstance();
        }else if(position == 2){
            Log.e("去到修改通知页面","");
            return TBellNoticeUpdateFragment.newInstance();
        }else if(position == 3){
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
