package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.StringUtil;

public class HomeworkSingleFragment extends Fragment implements View.OnClickListener {
    private PageingInterface pageing;

    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1, -1, -1};
    int questionId = 0;

    public static HomeworkSingleFragment newInstance() {
        HomeworkSingleFragment fragment = new HomeworkSingleFragment();
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
        View view = inflater.inflate(R.layout.fragment_homework_single, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //顶部题号染色
        SpannableString spannableString = StringUtil.getStringWithColor("1/6题", "#6CC1E0", 0, 1);
        tv_question_number.setText(spannableString);

        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        //显示答案选项
        ClickableImageView iv_a = view.findViewById(R.id.iv_a);
        ClickableImageView iv_b = view.findViewById(R.id.iv_b);
        ClickableImageView iv_c = view.findViewById(R.id.iv_c);
        ClickableImageView iv_d = view.findViewById(R.id.iv_d);
        iv_answer[0] = iv_a;
        iv_answer[1] = iv_b;
        iv_answer[2] = iv_c;
        iv_answer[3] = iv_d;
        iv_a.setOnClickListener(this);
        iv_b.setOnClickListener(this);
        iv_c.setOnClickListener(this);
        iv_d.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_page_last:
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                pageing.pageNext();
                break;
            case R.id.iv_a:
                answer[questionId] = 0;
                showRadioBtn();
                break;
            case R.id.iv_b:
                answer[questionId] = 1;
                showRadioBtn();
                break;
            case R.id.iv_c:
                answer[questionId] = 2;
                showRadioBtn();
                break;
            case R.id.iv_d:
                answer[questionId] = 3;
                showRadioBtn();
                break;
        }
    }

    //展示底部按钮
    private void showRadioBtn(){
        for(int i = 0; i < 4; ++i){
            if(answer[questionId] != i){
                iv_answer[i].setImageResource(unselectIcons[i]);
            }else{
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }
}