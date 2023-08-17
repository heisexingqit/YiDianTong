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
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.HomeworkInterface;


public class ResourceFolderPaperFragment extends Fragment {
    private static final String TAG = "ResourceFolderPaperFragment";

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private WebView wv_content;

    public static ResourceFolderPaperFragment newInstance(LearnPlanItemEntity learnPlanEntity) {
        ResourceFolderPaperFragment fragment = new ResourceFolderPaperFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_resource_folder_paper, container, false);

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
}