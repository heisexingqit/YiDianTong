package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.fragment.BookDetailClozeFragment;
import com.example.yidiantong.fragment.BookDetailJudgeFragment;
import com.example.yidiantong.fragment.BookDetailMultipleFragment;
import com.example.yidiantong.fragment.BookDetailReadingFragment;
import com.example.yidiantong.fragment.BookDetailSingleFragment;
import com.example.yidiantong.fragment.BookDetailSubjectFragment;
import com.example.yidiantong.fragment.ShiTiDetailClozeFragment;
import com.example.yidiantong.fragment.ShiTiDetailJudgeFragment;
import com.example.yidiantong.fragment.ShiTiDetailMultipleFragment;
import com.example.yidiantong.fragment.ShiTiDetailReadingFragment;
import com.example.yidiantong.fragment.ShiTiDetailSeven2FiveFragment;
import com.example.yidiantong.fragment.ShiTiDetailSingleFragment;
import com.example.yidiantong.fragment.ShiTiDetailSubjectFragment;

import java.util.ArrayList;
import java.util.List;

public class BooksRecyclerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "BooksRecyclerAdapter";

    private List<BookRecyclerEntity> itemList1 = new ArrayList<>();
    private List<BookExerciseEntity> itemList2 = new ArrayList<>();
    private String userName;
    private String subjectId;
    private String courseName;
    private Boolean exerciseType = false;
    private String allpage;
    private String currentpage;
    private int type;

    @SuppressLint("WrongConstant")
    public BooksRecyclerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void update1(List<BookRecyclerEntity> itemList,String userName, String subjectId, String courseName, Boolean exerciseType){
        this.itemList1 = itemList;
        this.userName = userName;
        this.subjectId = subjectId;
        this.courseName = courseName;
        this.exerciseType = exerciseType;
        this.notifyDataSetChanged();
    }

    public void update2(List<BookExerciseEntity> itemList, int type, String userName, String subjectId,
                        String courseName, Boolean exerciseType,String currentpage, String allpage){
        this.itemList2 = itemList;
        this.type = type;
        this.userName = userName;
        this.subjectId = subjectId;
        this.courseName = courseName;
        this.exerciseType = exerciseType;
        this.currentpage = currentpage;
        this.allpage = allpage;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        Log.e("wen0603", "getItem: " + exerciseType);
        if (!exerciseType){
            String baseTypeId = itemList1.get(position).getBaseTypeId();
            switch (baseTypeId){
                case "101":
                    fragment = BookDetailSingleFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
                    break;
                case "102":
                    fragment = BookDetailMultipleFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
                    break;
                case "103":
                    fragment = BookDetailJudgeFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
                    break;
                case "108":
                case "109":
                    fragment = BookDetailReadingFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
                    break;
//                case "104":
//                    fragment = BookDetailClozeFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
//                    break;
                default:
                    fragment = BookDetailSubjectFragment.newInstance(itemList1.get(position),userName,subjectId,courseName,exerciseType);
                    break;
            }
            return fragment;
        }else {
            String baseTypeId = itemList2.get(position).getBaseTypeId();
            switch (baseTypeId){
                case "101":
                    fragment = ShiTiDetailSingleFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    break;
                case "102":
                    fragment = ShiTiDetailMultipleFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    break;
                case "103":
                    fragment = ShiTiDetailJudgeFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    break;
                case "108":
                case "109":
                    if (itemList2.get(position).getTypeName().contains("七选五")) {
                        fragment = ShiTiDetailSeven2FiveFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    }else {
                        fragment = ShiTiDetailReadingFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    }
                    break;
                case "104":
                    //填空题
                    fragment = ShiTiDetailClozeFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    break;
                default:
                    //主观题
                    fragment = ShiTiDetailSubjectFragment.newInstance(itemList2.get(position),type,userName,subjectId,courseName,currentpage,allpage);
                    break;
            }
            return fragment;
        }
    }

    @Override
    public int getCount() {
        if (!exerciseType) {
            return itemList1.size();
        }else {
            return itemList2.size();
        }
    }



    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
