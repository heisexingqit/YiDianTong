package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.StringUtils;

public class HomeworkMultipleFragment extends Fragment implements View.OnClickListener {

    private PageingInterface pageing;

    int[] unselectIcons = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2};
    int[] selectIcons = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[][] answer = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1},{-1, -1}};

    public static HomeworkMultipleFragment newInstance(HomeworkEntity homeworkEntity, int position, int size) {
        HomeworkMultipleFragment fragment = new HomeworkMultipleFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkEntity", homeworkEntity);
        args.putInt("position", position);
        args.putInt("size", size);
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

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        HomeworkEntity homeworkEntity = (HomeworkEntity)arg.getSerializable("homeworkEntity");

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_multiple, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //题面显示
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

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
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
                answer[0][0] = 0;
                showRadioBtn();
                break;
            case R.id.iv_b:
                answer[1][0] = 1;
                showRadioBtn();
                break;
            case R.id.iv_c:
                answer[2][0] = 2;
                showRadioBtn();
                break;
            case R.id.iv_d:
                answer[3][0] = 3;
                showRadioBtn();
                break;
        }
    }

    //展示底部按钮
    private void showRadioBtn(){
        for(int i = 0; i < 4; ++i) {
            if (answer[i][0] != i && answer[i][1] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);

            } else if (answer[i][0] == i && answer[i][1] != i) {
                iv_answer[i].setImageResource(selectIcons[i]);
                answer[i][1] = i;
                answer[i][0] = -1;

            } else if (answer[i][0] != i && answer[i][1] == i) {
                iv_answer[i].setImageResource(selectIcons[i]);

            }else{
                iv_answer[i].setImageResource(unselectIcons[i]);
                answer[i][1] = -1;
                answer[i][0] = -1;

            }
        }
    }
}