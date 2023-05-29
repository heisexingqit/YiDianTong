package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkMarkedEntity;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.TransmitInterface;
import com.example.yidiantong.util.TransmitInterface3;
import com.google.android.flexbox.FlexboxLayout;
import com.xuexiang.xui.XUI;
import com.xuexiang.xui.widget.button.SmoothCheckBox;

import org.apache.commons.text.StringEscapeUtils;

public class THomeworkMarkFragment extends Fragment implements View.OnClickListener {

    // 接口需要
    private THomeworkMarkedEntity homeworkMarked;

    // 打分同步
    private TransmitInterface3 transmitInterface;

    // 分数上限
    private int scoreNum;
    private int score = 0;
    private int zero5 = 0;
    private Button[] btnArray;
    private View[] viewArray;
    private SmoothCheckBox checkBox;
    private TextView tv_stu_scores;
    private int position; // 从1开始


    public static THomeworkMarkFragment newInstance(THomeworkMarkedEntity homeworkMarked, int position, int size, boolean canMark) {
        THomeworkMarkFragment fragment = new THomeworkMarkFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkMarked", homeworkMarked);
        args.putInt("position", position);
        args.putInt("size", size);
        args.putBoolean("canMark", canMark);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        transmitInterface = (TransmitInterface3) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        XUI.initTheme(getActivity());

        //取出携带的参数
        Bundle arg = getArguments();
        homeworkMarked = (THomeworkMarkedEntity) arg.getSerializable("homeworkMarked");
        position = Integer.parseInt(homeworkMarked.getOrder());
        int size = Integer.parseInt(homeworkMarked.getOrderCount());
        boolean canMark = arg.getBoolean("canMark");

        //获取view
        View view = inflater.inflate(R.layout.fragment_t_homework_mark, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        // 获取组件
        FlexboxLayout fl_score = view.findViewById(R.id.fl_score);
        TextView tv_zero5 = view.findViewById(R.id.tv_zero5);
        // 动态加打分按钮
        tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
        checkBox = view.findViewById(R.id.cb_zero5);


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
        setHtmlOnWebView(wv_content, homeworkMarked.getShitiShow());
        String stuStr = homeworkMarked.getStuAnswer().trim();
        if (stuStr.length() == 0) {
            stuStr = "未答";
        }
        setHtmlOnWebView(wv_content2, stuStr);
        setHtmlOnWebView(wv_content3, homeworkMarked.getShitiAnswer());

        stuStr = homeworkMarked.getShitiAnalysis();
        if (stuStr.length() == 0) {
            stuStr = "略";
        }
        setHtmlOnWebView(wv_content4, stuStr);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText("[" + homeworkMarked.getTypeName() + "]");

        if (!canMark) {
            checkBox.setVisibility(View.GONE);
            fl_score.setVisibility(View.GONE);
            tv_zero5.setVisibility(View.GONE);
        }

        // 同步学生分数
        String stuScore = transmitInterface.getStuScore(position - 1);
        score = (int) (Float.parseFloat(stuScore));
        zero5 = stuScore.contains(".5") ? 1 : 0;

        // 如果可以批改分数
        if (canMark) {
            // 点击事件
            checkBox.setOnClickListener(v -> {

                boolean isChecked = checkBox.isChecked();
                if (!isChecked) {
                    double nowScore = score + 0.5;
                    if (nowScore > Double.parseDouble(homeworkMarked.getQuestionScore())) {
                        zero5 = 0;
                        Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        zero5 = 1;
                    }
                } else {
                    zero5 = 0;
                }
                showScoreBtn();
                // 同步修改
                transmitInterface.setStuAnswer(position - 1, score + (zero5 == 1 ? ".5" : ".0"));

            });

            // 分数组件列表创建
            scoreNum = Integer.parseInt(homeworkMarked.getQuestionScore());
            btnArray = new Button[scoreNum + 1];
            viewArray = new View[scoreNum + 1];


            // 动态创建打分按钮
            for (int i = 0; i < scoreNum + 1; ++i) {
                viewArray[i] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_score_btn, fl_score, false);
                btnArray[i] = viewArray[i].findViewById(R.id.btn_score);
                btnArray[i].setText(String.valueOf(i));
                btnArray[i].setTag(i);

                // 点击事件
                btnArray[i].setOnClickListener(view1 -> {
                    int idx = (int) view1.getTag();
                    if (score != idx) {
                        double nowScore = idx + (zero5 == 1 ? 0.5 : 0);
                        if (nowScore > Double.parseDouble(homeworkMarked.getQuestionScore())) {
                            Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        score = idx;
                        showScoreBtn();
                        // 同步修改
                        transmitInterface.setStuAnswer(position - 1, score + (zero5 == 1 ? ".5" : ".0"));
                    }
                });
                fl_score.addView(viewArray[i]);
            }

            // 分数显示+按钮显示
            showScoreBtn();

        }

        // 分数显示
        tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));

        return view;
    }

    // 调整分数按钮显示效果
    private void showScoreBtn() {
        for (int i = 0; i < scoreNum + 1; ++i) {
            if (score == i) {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report);
                btnArray[i].setTextColor(getResources().getColor(R.color.white));
            } else {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report_unselect);
                btnArray[i].setTextColor(getResources().getColor(R.color.main_bg));
            }
            tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));
        }
        if (zero5 == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     *
     * @param wb  WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str) {
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