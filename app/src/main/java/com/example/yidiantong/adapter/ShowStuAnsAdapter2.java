package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableWebView;

import java.util.List;

public class ShowStuAnsAdapter2 extends BaseAdapter {

    private Context context;
    private String[] ansArray;
    private String[] questionTypes;
    private List<Integer> questionIdx;

    public ShowStuAnsAdapter2(Context context, String[] ansArray, String[] questionTypes, List<Integer> questionIdx) {
        this.context = context;
        this.ansArray = ansArray;
        this.questionTypes = questionTypes;
        this.questionIdx = questionIdx;
    }

    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        img{\n" +
            "        vertical-align: middle;" +
            "        }" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14.8px;" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n";

    private String html_answer_tail = "</body>";

    @Override
    public int getCount() {
        return ansArray.length;
    }

    @Override
    public Object getItem(int i) {
        return ansArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v;
        String showAns = "";
        v = LayoutInflater.from(context).inflate(R.layout.item_show_web, null);
        ClickableWebView wv = v.findViewById(R.id.wv_content);
        wv.setFocusable(false);
        wv.setClickable(false);
        if (ansArray[i].length() == 0) {
            showAns = "<span style=\"color: red\">未答</span>";
        } else {
            showAns = ansArray[i];
        }
        wv.loadData(" (" + (questionIdx.get(i) + 1) + ") " + html_answer_head + showAns + html_answer_tail, "text/html", "utf-8");
        return v;
    }
}
