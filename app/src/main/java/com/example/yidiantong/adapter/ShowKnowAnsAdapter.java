package com.example.yidiantong.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableWebView;

public class ShowKnowAnsAdapter extends BaseAdapter {

    private Context context;
    private String[] ansArray;
    private int[] type;

    public ShowKnowAnsAdapter(Context context, String[] ansArray, int[] type) {
        this.context = context;
        this.ansArray = ansArray;
        this.type = type;
    }

    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14.8px;" +
            "            max-width: 100%;\n" +
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
        String showAns = "";
        view = LayoutInflater.from(context).inflate(R.layout.item_know_web, null);
        ClickableWebView wv_content = view.findViewById(R.id.wv_content);
        // 获取屏幕宽度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        // 计算屏幕宽度的10%
        int maxWidth = (int)(screenWidth * 1f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(maxWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);
        fiv_bd_tf.setVisibility(View.VISIBLE);
        wv_content.setFocusable(false);
        wv_content.setClickable(false);
        if (ansArray[i] == null || ansArray[i].equals("")) {
            showAns = "<span style=\"color: red\">未答</span>";
        } else {
            showAns = ansArray[i];
        }
        if (type[i] == 1) {
            // html中的空格符
            showAns += "&nbsp;&nbsp;&nbsp;<img src=\"http://www.cn901.com/res/studentAnswerImg/AppImage/2024/07/12/m1001_225246239.png\" style=\"height:15px;width:auto;\" alt=\"Image Description\">";
        }else if (type[i] == 2) {
            showAns += "&nbsp;&nbsp;&nbsp;<img src=\"http://www.cn901.com/res/studentAnswerImg/AppImage/2024/07/12/m1001_225300413.png\" style=\"height:15px;width:auto;\" alt=\"Image Description\">";
        }else if (type[i] == 3) {
            showAns += "&nbsp;&nbsp;&nbsp;<img src=\"http://www.cn901.com/res/studentAnswerImg/AppImage/2024/07/12/m1001_225220664.png\" style=\"height:15px;width:auto;\" alt=\"Image Description\">";
        }
        wv_content.loadDataWithBaseURL(null, " (" + (i + 1) + ") " + html_answer_head + showAns + html_answer_tail, "text/html", "utf-8", null);

        wv_content.setLayoutParams(params);
        return view;
    }
}
