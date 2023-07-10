package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.HomeworkInterface;

import org.apache.commons.text.StringEscapeUtils;

public class HomeworkSeven2FiveFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeworkSeven2FiveFragm";

    private PagingInterface pageing;
    private HomeworkInterface transmit;

    int[] answer = {-1, -1, -1, -1, -1};
    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select};

    ClickableImageView[][] civ_answer_drawer = new ClickableImageView[5][7];

    //接口需要
    private HomeworkEntity homeworkEntity;
    private StuAnswerEntity stuAnswerEntity;
    private boolean inital = true;

    public static HomeworkSeven2FiveFragment newInstance(HomeworkEntity homeworkEntity, int position, int size, StuAnswerEntity stuAnswerEntity) {
        HomeworkSeven2FiveFragment fragment = new HomeworkSeven2FiveFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkEntity", homeworkEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
        transmit = (HomeworkInterface) context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        int position = 0, size = 0;
        if(getArguments() != null){
            homeworkEntity = (HomeworkEntity) getArguments().getSerializable("homeworkEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
            position = getArguments().getInt("position") + 1;
            size = getArguments().getInt("size");
        }


        //同步答案
        if (stuAnswerEntity.getStuAnswer().length() > 0) {
            Log.d(TAG, "onCreateView: " + stuAnswerEntity.getStuAnswer());
            String[] parts = stuAnswerEntity.getStuAnswer().split(",");
            for (int i = 0; i < parts.length; ++i) {
                String part = parts[i];
                if (part.length() == 1) {
                    answer[i] = part.charAt(0) - 'A';
                }
            }
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_homework_seven2five, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        /**
         * 多机适配：底栏高度
         */
        WindowManager windowManager = getActivity().getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        /** 转义数据中的字符实体 */
        homeworkEntity.setQuestionContent(StringEscapeUtils.unescapeHtml4(homeworkEntity.getQuestionContent()));

        WebView wv_content = view.findViewById(R.id.wv_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + homeworkEntity.getQuestionContent() + "</body>";
        wv_content.loadData(html_content, "text/html", "utf-8");

        //题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(homeworkEntity.getQuestionTypeName());

        //顶部题号染色
        int positionLen = String.valueOf(position).length();
        String questionNum = position + "/" + size + "题";
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);

        // 翻页按钮
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        civ_answer_drawer[0][0] = view.findViewById(R.id.civ_1a);
        civ_answer_drawer[0][1] = view.findViewById(R.id.civ_1b);
        civ_answer_drawer[0][2] = view.findViewById(R.id.civ_1c);
        civ_answer_drawer[0][3] = view.findViewById(R.id.civ_1d);
        civ_answer_drawer[0][4] = view.findViewById(R.id.civ_1e);
        civ_answer_drawer[0][5] = view.findViewById(R.id.civ_1f);
        civ_answer_drawer[0][6] = view.findViewById(R.id.civ_1g);

        civ_answer_drawer[1][0] = view.findViewById(R.id.civ_2a);
        civ_answer_drawer[1][1] = view.findViewById(R.id.civ_2b);
        civ_answer_drawer[1][2] = view.findViewById(R.id.civ_2c);
        civ_answer_drawer[1][3] = view.findViewById(R.id.civ_2d);
        civ_answer_drawer[1][4] = view.findViewById(R.id.civ_2e);
        civ_answer_drawer[1][5] = view.findViewById(R.id.civ_2f);
        civ_answer_drawer[1][6] = view.findViewById(R.id.civ_2g);

        civ_answer_drawer[2][0] = view.findViewById(R.id.civ_3a);
        civ_answer_drawer[2][1] = view.findViewById(R.id.civ_3b);
        civ_answer_drawer[2][2] = view.findViewById(R.id.civ_3c);
        civ_answer_drawer[2][3] = view.findViewById(R.id.civ_3d);
        civ_answer_drawer[2][4] = view.findViewById(R.id.civ_3e);
        civ_answer_drawer[2][5] = view.findViewById(R.id.civ_3f);
        civ_answer_drawer[2][6] = view.findViewById(R.id.civ_3g);

        civ_answer_drawer[3][0] = view.findViewById(R.id.civ_4a);
        civ_answer_drawer[3][1] = view.findViewById(R.id.civ_4b);
        civ_answer_drawer[3][2] = view.findViewById(R.id.civ_4c);
        civ_answer_drawer[3][3] = view.findViewById(R.id.civ_4d);
        civ_answer_drawer[3][4] = view.findViewById(R.id.civ_4e);
        civ_answer_drawer[3][5] = view.findViewById(R.id.civ_4f);
        civ_answer_drawer[3][6] = view.findViewById(R.id.civ_4g);

        civ_answer_drawer[4][0] = view.findViewById(R.id.civ_5a);
        civ_answer_drawer[4][1] = view.findViewById(R.id.civ_5b);
        civ_answer_drawer[4][2] = view.findViewById(R.id.civ_5c);
        civ_answer_drawer[4][3] = view.findViewById(R.id.civ_5d);
        civ_answer_drawer[4][4] = view.findViewById(R.id.civ_5e);
        civ_answer_drawer[4][5] = view.findViewById(R.id.civ_5f);
        civ_answer_drawer[4][6] = view.findViewById(R.id.civ_5g);

        // 设置点击事件
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 7; ++j) {
                civ_answer_drawer[i][j].setOnClickListener(this);
            }
        }

        // 底部按钮面板
        showRadioBtnDrawer();

        inital = false;
        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_page_last:
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                pageing.pageNext();
                break;
            default:
                for (int i = 0; i < 5; ++i) {
                    for (int j = 0; j < 7; ++j) {
                        if (civ_answer_drawer[i][j] == view) {
                            answer[i] = j;
                            break;
                        }
                    }
                }
                showRadioBtnDrawer();
                break;
        }

    }

    //展示按钮面板
    private void showRadioBtnDrawer() {

        //同步答案给Activity
        if(!inital){
            String myAnswer = "";
            boolean f = false;
            for (int i = 0; i < 5; ++i) {
                if (f) {
                    myAnswer += ',';
                } else {
                    f = !f;
                }
                if(answer[i] != -1){
                    myAnswer += (char) ('A' + answer[i]);
                }else{
                    myAnswer += "未答";
                }
            }
            transmit.setStuAnswer(stuAnswerEntity.getOrder(), myAnswer);
        }

        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (answer[i] != j) {
                    civ_answer_drawer[i][j].setImageResource(unselectIcons[j]);
                } else {
                    civ_answer_drawer[i][j].setImageResource(selectIcons[j]);
                }
            }
        }
    }

}