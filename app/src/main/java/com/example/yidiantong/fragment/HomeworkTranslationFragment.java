package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.util.PageingInterface;

import com.example.yidiantong.util.StringUtil;
import com.example.yidiantong.util.Utils;


public class HomeworkTranslationFragment extends Fragment implements View.OnClickListener {

    private PageingInterface pageing;
    private int show = 0;
    private LinearLayout ll_context;

    public static HomeworkTranslationFragment newInstance() {
        HomeworkTranslationFragment fragment = new HomeworkTranslationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PageingInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_translation, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        SpannableString spannableString = StringUtil.getStringWithColor("4/6题", "#6CC1E0", 0, 1);
        tv_question_number.setText(spannableString);

        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);
        ll_context = view.findViewById(R.id.ll_context);
        view.findViewById(R.id.iv_top).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.iv_page_last:
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                pageing.pageNext();
                break;
            case R.id.iv_top:
                ViewGroup.LayoutParams params = ll_context.getLayoutParams();
                if(show == 0){
                    params.height = Utils.dip2px(getActivity(), 300);
                    show = 1;
                }else{
                    params.height = Utils.dip2px(getActivity(), 180);
                    show = 0;
                }
                ll_context.setLayoutParams(params);
                break;
        }
    }

}