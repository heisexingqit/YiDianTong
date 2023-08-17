package com.example.yidiantong.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.HomeworkInterface;


public class LearnPlanPaperFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearnPlanQuestionFragme";
    private PagingInterface paging;
    private LearnPlanInterface transmit;

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private StuAnswerEntity stuAnswerEntity;
    private WebView wv_content;

    // 观看时间
    private long timeStart;


    public static LearnPlanPaperFragment newInstance(LearnPlanItemEntity learnPlanEntity, StuAnswerEntity stuAnswerEntity) {
        LearnPlanPaperFragment fragment = new LearnPlanPaperFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        paging = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_learn_plan_paper, container, false);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getResourceName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        wv_content = view.findViewById(R.id.wv_content);
        WebSettings webSettings = wv_content.getSettings();
        // 设置可以运行JS代码
        webSettings.setJavaScriptEnabled(true);

        Log.d("ww", "onCreateView: " + learnPlanEntity.getUrl());

        // 运行JS代码修改样式
        wv_content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 使用 JavaScript 控制类的宽度（直接运行）
                String javascript = "javascript:(function() { " +
                        "var elements = document.getElementsByClassName('word-paper'); " +
                        "for (var i = 0; i < elements.length; i++) { " +
                        "   elements[i].style.width = '95%'; " +
                        "} " +
                        "})()";

                view.loadUrl(javascript);
            }
        });

        wv_content.loadUrl(learnPlanEntity.getUrl());

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

    @Override
    public void onResume() {
        super.onResume();
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        transmit.uploadTime(System.currentTimeMillis() - timeStart);
    }
}