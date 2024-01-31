package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkAddEntity;

import org.apache.commons.text.StringEscapeUtils;

public class THomeworkAddItemFragment extends Fragment {

    private THomeworkAddEntity homeworkAddEntity;
    private WebView wv_content;
    private WebView wv_answer;
    private WebView wv_analysis;

    public THomeworkAddItemFragment() {
        // Required empty public constructor
    }

    public static THomeworkAddItemFragment newInstance(THomeworkAddEntity homeworkAddEntity) {
        THomeworkAddItemFragment fragment = new THomeworkAddItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkAddEntity", homeworkAddEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 取数据
        Bundle arg = getArguments();
        homeworkAddEntity = (THomeworkAddEntity) arg.getSerializable("homeworkAddEntity");

        View view = inflater.inflate(R.layout.fragment_t_homework_add_item, container, false);
        wv_content = view.findViewById(R.id.wv_content);
        wv_answer = view.findViewById(R.id.wv_answer);
        wv_analysis = view.findViewById(R.id.wv_analysis);
        setHtmlOnWebView(wv_content, homeworkAddEntity.getTiMian());
        setHtmlOnWebView(wv_answer, homeworkAddEntity.getAnswer());
        setHtmlOnWebView(wv_analysis, homeworkAddEntity.getAnalysis());

        return view;
    }


    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     * @param wb WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str){
        str = StringEscapeUtils.unescapeHtml4(str);
        /** CSS BUG说明
         * img标签中自带一个style属性，重复设置style导致后面的【垂直居中】属性失效
         * 可以直接设置img属性，而不是style，或者在head中设置style标签，可以与内联style共同作用。
         */
        String html_content = "<head><style>" +
                " p {\n" +
                "   margin: 0px;" +
                "   line-height: 30px;" +
                "   }" +
                "</style>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 14px; margin: 0px; padding: 0px\">" + str + "</body>";
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }
}