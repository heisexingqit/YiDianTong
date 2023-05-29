package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableWebView;
import com.example.yidiantong.util.StringUtils;

import java.util.List;

public class ShowStuAnsAdapter extends BaseAdapter {

    private Context context;
    private String[] ansArray;
    private String[] questionTypes;

    public ShowStuAnsAdapter(Context context, String[] ansArray, String[] questionTypes) {
        this.context = context;
        this.ansArray = ansArray;
        this.questionTypes = questionTypes;
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
            "    <script>\n" +
            "        function a(x) {\n" +
            "            test.mytoast(\"我尼玛\")\n" +
            "        }\n" +
            "    </script>\n" +
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
        wv.loadData("(" + (i + 1) + ") " + html_answer_head + showAns + html_answer_tail, "text/html", "utf-8");
        return v;
    }
}
