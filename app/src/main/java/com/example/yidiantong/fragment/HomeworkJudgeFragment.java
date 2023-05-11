package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.TransmitInterface;

public class HomeworkJudgeFragment extends Fragment implements View.OnClickListener {

    private PageingInterface pageing;
    int[] unselectIcons = {R.drawable.right_unselect, R.drawable.error_unselect};
    int[] selectIcons = {R.drawable.right_select, R.drawable.error_select};

    ClickableImageView[] iv_answer = new ClickableImageView[2];
    int answer = -1;
    int questionId = 0;

    private TransmitInterface transmit;

    //接口需要
    private HomeworkEntity homeworkEntity;
    private StuAnswerEntity stuAnswerEntity;

    public static HomeworkJudgeFragment newInstance(HomeworkEntity homeworkEntity, int position, int size, StuAnswerEntity stuAnswerEntity) {
        HomeworkJudgeFragment fragment = new HomeworkJudgeFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkEntity", homeworkEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PageingInterface) context;
        transmit = (TransmitInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        homeworkEntity = (HomeworkEntity) arg.getSerializable("homeworkEntity");
        stuAnswerEntity = (StuAnswerEntity) arg.getSerializable("stuAnswerEntity");

        //同步答案
        if (stuAnswerEntity.getStuAnswer().length() > 0) {
            answer = "对".equals(stuAnswerEntity.getStuAnswer()) ? 1 : 0;
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_homework_judge, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        /**
         * 多机适配：底栏高度
         */
        WindowManager windowManager = getActivity().getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        // 长宽像素比
        float deviceAspectRatio = (float) screenHeight / screenWidth;
        // 获取底部布局
        RelativeLayout block = view.findViewById(R.id.rl_bottom_block);
        if(deviceAspectRatio > 2.0){
            ViewGroup.LayoutParams params = block.getLayoutParams();
            params.height = PxUtils.dip2px(getActivity(), 80);
            block.setLayoutParams(params);
        }

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
                answer = 1;
                showRadioBtn();
                break;
            case R.id.iv_e:
                answer = 0;
                showRadioBtn();
                break;
        }
    }

    //展示底部按钮
    private void showRadioBtn() {
        //同步答案给Activity
        transmit.setStuAnswer(stuAnswerEntity.getOrder(), answer == 1 ? "对" : "错");

        for (int i = 0; i < 2; ++i) {
            if (answer != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }
}