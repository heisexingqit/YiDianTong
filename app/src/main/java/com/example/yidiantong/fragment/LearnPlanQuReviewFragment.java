package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.StringUtils;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.easing.Linear;


public class LearnPlanQuReviewFragment extends Fragment implements View.OnClickListener {
    private LearnPlanItemEntity learnPlanEntity;

    private PagingInterface paging;
    private LearnPlanInterface transmit;
    private TextView tv_question_type;
    private WebView wv_content;
    private WebView wv_content2;
    private WebView wv_content3;
    private LinearLayout ll_analysis;
    private LinearLayout ll_show;

    public static LearnPlanQuReviewFragment newInstance(LearnPlanItemEntity learnPlanEntity) {
        LearnPlanQuReviewFragment fragment = new LearnPlanQuReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }
    public static LearnPlanQuReviewFragment newInstance(LearnPlanItemEntity learnPlanEntity,int position, int size) {
        LearnPlanQuReviewFragment fragment = new LearnPlanQuReviewFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        paging = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    private boolean isShow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_learn_plan_qu_review, container, false);
        int position=0,size=0;
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity) getArguments().getSerializable("learnPlanEntity");
            position = getArguments().getInt("position") + 1;
            size = getArguments().getInt("size");
        }
        tv_question_type = view.findViewById(R.id.tv_question_type);
        wv_content = view.findViewById(R.id.wv_content);
        wv_content2 = view.findViewById(R.id.wv_content2);
        wv_content3 = view.findViewById(R.id.wv_content3);
        ll_analysis = view.findViewById(R.id.ll_analysis);
        ll_show = view.findViewById(R.id.ll_show);

        tv_question_type.setText(learnPlanEntity.getResourceName());
        wv_content.loadDataWithBaseURL(null, learnPlanEntity.getQuestion(), "text/html", "utf-8", null);
        wv_content2.loadDataWithBaseURL(null, learnPlanEntity.getAnswer(), "text/html", "utf-8", null);
        if(learnPlanEntity.getAnalysis() == null || learnPlanEntity.getAnalysis().length() == 0){
            ll_analysis.setVisibility(View.GONE);
        }else{
            wv_content3.loadDataWithBaseURL(null, learnPlanEntity.getAnalysis(), "text/html", "utf-8", null);
        }

        wv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 这里处理你的点击事件
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (isShow) {
                        isShow = false;
                        ll_show.setVisibility(View.GONE);
                    } else {
                        isShow = true;
                        ll_show.setVisibility(View.VISIBLE);
                    }
                }
                return false; // 返回 false 以便让 WebView 继续处理触摸事件
            }
        });
        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);
        //顶部题号染色
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);
        int positionLen = String.valueOf(position).length();
        String questionNum = position + "/" + size + "题";
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                paging.pageLast();
                break;
            case R.id.iv_page_next:
                paging.pageNext();
                break;
        }
    }
}