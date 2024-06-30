package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.fragment.LearnPlanQuReviewFragment;
import com.example.yidiantong.fragment.LearnPlanPPTFragment;
import com.example.yidiantong.fragment.LearnPlanPaperFragment;
import com.example.yidiantong.fragment.LearnPlanVideoFragment;

import java.util.ArrayList;
import java.util.List;

public class TLearnPlanPreviewAdapter extends FragmentPagerAdapter {
    private static final String TAG = "LearnPlanPagerAdapter";

    public List<LearnPlanItemEntity> itemList = new ArrayList<>();//题面列表

    //传递信息
    private String learnPlanId;

    @SuppressLint("WrongConstant")
    public TLearnPlanPreviewAdapter(@NonNull FragmentManager fm, String learnPlanId) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.learnPlanId = learnPlanId;
    }

    public void update(List<LearnPlanItemEntity> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        LearnPlanItemEntity item = itemList.get(position);
        switch (item.getResourceType()) {
            case "01":
                fragment = LearnPlanQuReviewFragment.newInstance(itemList.get(position),position, itemList.size());
                break;
            case "02":
                fragment = LearnPlanPaperFragment.newInstance(itemList.get(position),position, itemList.size());
                break;
            case "03":
                switch (item.getFormat()) {
                    case "music":
                    case "video":
                        fragment = LearnPlanVideoFragment.newInstance(itemList.get(position),position, itemList.size());
                        break;
                    case "ppt":
                        fragment = LearnPlanPPTFragment.newInstance(itemList.get(position),position, itemList.size());
                        break;
                    default:
                        Log.d("wen", "导学案未知类型: " + item.getFormat());
                }

                break;
            default:
                Log.d(TAG, "（01,02,03）未知type: " + item.getResourceType());
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }
}
