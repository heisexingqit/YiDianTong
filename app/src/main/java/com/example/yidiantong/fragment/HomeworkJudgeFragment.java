package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.StringUtils;

public class HomeworkJudgeFragment extends Fragment implements View.OnClickListener {

    private PageingInterface pageing;
    int[] unselectIcons = {R.drawable.right_unselect, R.drawable.error_unselect};
    int[] selectIcons = {R.drawable.right_select, R.drawable.error_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1};
    int questionId = 0;

    public static HomeworkJudgeFragment newInstance() {
        HomeworkJudgeFragment fragment = new HomeworkJudgeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PageingInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_homework_judge, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //顶部题号染色
        SpannableString spannableString = StringUtils.getStringWithColor("3/6题", "#6CC1E0", 0, 1);
        tv_question_number.setText(spannableString);

        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        //显示答案选项
        ClickableImageView iv_r = view.findViewById(R.id.iv_r);
        ClickableImageView iv_e = view.findViewById(R.id.iv_e);
        iv_answer[0] = iv_r;
        iv_answer[1] = iv_e;
        iv_r.setOnClickListener(this);
        iv_e.setOnClickListener(this);

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
            case R.id.iv_r:
                answer[questionId] = 0;
                showRadioBtn();
                break;
            case R.id.iv_e:
                answer[questionId] = 1;
                showRadioBtn();
                break;
        }
    }

    //展示底部按钮
    private void showRadioBtn() {
        for (int i = 0; i < 2; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }
}