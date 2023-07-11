package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.HomeworkMarkedEntity;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.StringUtils;

import org.apache.commons.text.StringEscapeUtils;

public class HomeworkFinishFragment extends Fragment implements View.OnClickListener {

    private PagingInterface pageing;

    //接口需要
    private HomeworkMarkedEntity homeworkMarked;

    public static HomeworkFinishFragment newInstance(HomeworkMarkedEntity homeworkMarked, int position, int size) {
        HomeworkFinishFragment fragment = new HomeworkFinishFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkMarked", homeworkMarked);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        homeworkMarked = (HomeworkMarkedEntity) arg.getSerializable("homeworkMarked");

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_finish, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //题号染色
        int positionLen = String.valueOf(position).length();
        String questionNum = position + " / " + size;
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);

        /**
         *  数据显示，四个vw_content
         */
        WebView wv_content = view.findViewById(R.id.wv_content);
        WebView wv_content2 = view.findViewById(R.id.wv_content2);
        WebView wv_content3 = view.findViewById(R.id.wv_content3);
        WebView wv_content4 = view.findViewById(R.id.wv_content4);
        setHtmlOnWebView(wv_content, homeworkMarked.getTiMian());
        setHtmlOnWebView(wv_content2, homeworkMarked.getStandardAnswer());
        setHtmlOnWebView(wv_content3, homeworkMarked.getAnalysis());
        setHtmlOnWebView(wv_content4, homeworkMarked.getStuAnswer());

        /**
         * 简单判断答案是否为不可见
         */
        boolean isWatch = !"******".equals(homeworkMarked.getStandardAnswer());
        if(isWatch){
            LinearLayout ll_watch = view.findViewById(R.id.ll_watch);
            ll_watch.setVisibility(View.VISIBLE);
            TextView tv_total_scores = view.findViewById(R.id.tv_total_scores);
            tv_total_scores.setText("满分:" + homeworkMarked.getFullScore());
            TextView tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
            tv_stu_scores.setText("得分:" + homeworkMarked.getScore());
            ImageView iv_stu_scores = view.findViewById(R.id.iv_stu_scores);
            if(Math.abs(homeworkMarked.getScore() - homeworkMarked.getFullScore()) < 0.00001){
                iv_stu_scores.setImageResource(R.drawable.right);
            }else if(homeworkMarked.getScore() > 0){
                iv_stu_scores.setImageResource(R.drawable.half_right);
            }else{
                iv_stu_scores.setImageResource(R.drawable.error);
            }
        }



        //题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText("[" + homeworkMarked.getTypeName() + "]");

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                pageing.pageNext();
                break;
        }
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     * @param wb WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str){
        str = StringEscapeUtils.unescapeHtml4(str);
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