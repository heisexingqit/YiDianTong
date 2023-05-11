package com.example.yidiantong.adapter;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;


import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.fragment.BookDetailMultipleFragment;
import com.example.yidiantong.fragment.BookDetailReadingFragment;
import com.example.yidiantong.fragment.BookDetailSingleFragment;
import com.example.yidiantong.fragment.HomeworkMultipleFragment;
import com.example.yidiantong.fragment.HomeworkSingleFragment;
import com.example.yidiantong.fragment.HomeworkTranslationFragment;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "BooksRecyclerAdapter";

    private List<BookRecyclerEntity> itemList = new ArrayList<>();

    public BooksRecyclerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void update(List<BookRecyclerEntity> itemList){
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        String questionTypeName = itemList.get(position).getTypeName();
        switch (questionTypeName){
            case "单项选择题":
                fragment = BookDetailSingleFragment.newInstance(itemList.get(position));
                break;

            case "多项选择题":
                fragment = BookDetailMultipleFragment.newInstance(itemList.get(position));
                break;
            case "七选五":
                fragment = BookDetailReadingFragment.newInstance(itemList.get(position));
                break;
            case "阅读理解题":
                fragment = BookDetailReadingFragment.newInstance(itemList.get(position));
                break;
            default:
                Log.e("其他选项",":"+questionTypeName);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
