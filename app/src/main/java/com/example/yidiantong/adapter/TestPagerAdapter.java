package com.example.yidiantong.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class TestPagerAdapter extends FragmentStateAdapter {

    private List<Fragment> fragments;

    public TestPagerAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    public void update(List<Fragment> fragments){
        this.fragments = fragments;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @Override
    public long getItemId(int position) {
        // 返回唯一的标识符，可以使用 Fragment 的 hashCode() 或其他方式生成
        return fragments.get(position).hashCode();
    }
}
