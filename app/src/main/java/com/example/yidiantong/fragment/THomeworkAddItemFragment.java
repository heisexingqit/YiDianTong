package com.example.yidiantong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkAddEntity;

import org.apache.commons.text.StringEscapeUtils;

public class THomeworkAddItemFragment extends Fragment {

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
        super.onCreate(savedInstanceState);

        // 取数据
        Bundle arg = getArguments();
        THomeworkAddEntity homeworkAddEntity = (THomeworkAddEntity) arg.getSerializable("homeworkAddEntity");

        View view = inflater.inflate(R.layout.fragment_t_homework_add_item, container, false);
        WebView wv_content = view.findViewById(R.id.wv_content);
        WebView wv_answer = view.findViewById(R.id.wv_answer);
        WebView wv_analysis = view.findViewById(R.id.wv_analysis);
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