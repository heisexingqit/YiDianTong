package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanAddItemEntity;

import org.apache.commons.text.StringEscapeUtils;

public class TLearnPlanAddQuestionFragment extends Fragment {

    public TLearnPlanAddQuestionFragment() {
        // Required empty public constructor
    }

    public static TLearnPlanAddQuestionFragment newInstance(LearnPlanAddItemEntity learnPlanAddItemEntity) {
        TLearnPlanAddQuestionFragment fragment = new TLearnPlanAddQuestionFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanAddItemEntity", learnPlanAddItemEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 取数据
        Bundle arg = getArguments();
        LearnPlanAddItemEntity learnPlanAddItemEntity = (LearnPlanAddItemEntity) arg.getSerializable("learnPlanAddItemEntity");

        View view = inflater.inflate(R.layout.fragment_t_learn_plan_add_question, container, false);
        WebView wv_content = view.findViewById(R.id.wv_content);
        WebView wv_answer = view.findViewById(R.id.wv_answer);
        WebView wv_analysis = view.findViewById(R.id.wv_analysis);
        setHtmlOnWebView(wv_content, learnPlanAddItemEntity.getShitiShow());
        setHtmlOnWebView(wv_answer, learnPlanAddItemEntity.getShitiAnswer());
        setHtmlOnWebView(wv_analysis, learnPlanAddItemEntity.getShitiAnalysis());

        return view;
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     * @param wb WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str){
        str = StringEscapeUtils.unescapeHtml4(str);
        str = str.replace("<img", "<img style=\"max-width:100%;height:auto\"");
        String html_content = "<head><style>" +
                " p {\n" +
                "   margin: 0px;" +
                "   line-height: 30px;" +
                "   }" +
                "</style>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 14px; margin: 0px; padding: 0px\">" + str + "</body>";
        wb.loadData(html_content, "text/html", "utf-8");
    }
}