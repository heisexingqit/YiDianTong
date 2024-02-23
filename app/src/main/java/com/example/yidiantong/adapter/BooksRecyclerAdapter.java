package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.fragment.BookDetailJudgeFragment;
import com.example.yidiantong.fragment.BookDetailMultipleFragment;
import com.example.yidiantong.fragment.BookDetailReadingFragment;
import com.example.yidiantong.fragment.BookDetailSingleFragment;
import com.example.yidiantong.fragment.BookDetailClozeFragment;
import com.example.yidiantong.fragment.BookDetailSubjectFragment;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "BooksRecyclerAdapter";

    private List<BookRecyclerEntity> itemList = new ArrayList<>();

    @SuppressLint("WrongConstant")
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
        String baseTypeId = itemList.get(position).getBaseTypeId();
        switch (baseTypeId){
            case "101":
                fragment = BookDetailSingleFragment.newInstance(itemList.get(position));
                break;
            case "102":
                fragment = BookDetailMultipleFragment.newInstance(itemList.get(position));
                break;
            case "103":
                fragment = BookDetailJudgeFragment.newInstance(itemList.get(position));
                break;
            case "108":
            case "109":
                fragment = BookDetailReadingFragment.newInstance(itemList.get(position));
                break;
            case "104":
                fragment = BookDetailClozeFragment.newInstance(itemList.get(position));
                break;
            default:
                fragment = BookDetailSubjectFragment.newInstance(itemList.get(position));
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
