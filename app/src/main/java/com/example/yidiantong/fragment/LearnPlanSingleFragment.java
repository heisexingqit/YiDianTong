package com.example.yidiantong.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.HomeworkInterface;

import org.apache.commons.text.StringEscapeUtils;

public class LearnPlanSingleFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearnPlanSingleFragment";

    private PagingInterface pageing;
    private LearnPlanInterface transmit;

    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect, R.drawable.h_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select, R.drawable.h_select};

    ClickableImageView[] iv_answer;
    int answer = -1;

    /**
     * 选择项个数
     */
    private int choiceLen;

    //接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private StuAnswerEntity stuAnswerEntity;

    public static LearnPlanSingleFragment newInstance(LearnPlanItemEntity learnPlanEntity, int position, int size, StuAnswerEntity stuAnswerEntity) {
        LearnPlanSingleFragment fragment = new LearnPlanSingleFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
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
        pageing = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        int position = 0, size = 0;
        if(getArguments() != null){
            learnPlanEntity = (LearnPlanItemEntity) getArguments().getSerializable("learnPlanEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
            position = getArguments().getInt("position") + 1;
            size = getArguments().getInt("size");
        }
        choiceLen = (learnPlanEntity.getQuestionChoiceList().length()+ 1) / 2;
        Log.d("wen", "onCreateView: " + choiceLen);

        //同步答案
        if (stuAnswerEntity.getStuAnswer() != null && stuAnswerEntity.getStuAnswer().length() > 0) {
            answer = stuAnswerEntity.getStuAnswer().charAt(0) - 'A';
        }

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_single, container, false);

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

        /** 转义数据中的字符实体 */
        learnPlanEntity.setQuestion(StringEscapeUtils.unescapeHtml4(learnPlanEntity.getQuestion()));

        WebView wv_content = view.findViewById(R.id.wv_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + learnPlanEntity.getQuestion() + "</body>";
        wv_content.loadData(html_content, "text/html", "utf-8");

        //题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getResourceName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

        //顶部题号染色
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);
        tv_question_number.setVisibility(View.GONE);
//        int positionLen = String.valueOf(position).length();
//        String questionNum = position + "/" + size + "题";
//        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
//        tv_question_number.setText(spannableString);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        /**
         * 重复选项组件
         */
        iv_answer = new ClickableImageView[choiceLen];
        LinearLayout ll_parent = view.findViewById(R.id.ll_parent);
        for (int i = 0; i < choiceLen; ++i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.my_component2, ll_parent, false);
            iv_answer[i] = v.findViewById(R.id.iv_a);
            iv_answer[i].setImageResource(unselectIcons[i]);
            iv_answer[i].setOnClickListener(this);
            // 根部设置一个权重，如果给ImageView设置会影响xml的属性
            v.findViewById(R.id.ll_root).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            ll_parent.addView(v);
        }

        //初始化按钮
        showRadioBtn();
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
            default:
                for(int i = 0; i < choiceLen; ++i){
                    if(view == iv_answer[i]){
                        answer = i;
                        showRadioBtn();
                    }
                }
        }
    }

    //展示底部按钮
    private void showRadioBtn() {
        //同步答案给Activity
        if(answer != -1){
            transmit.setStuAnswer(stuAnswerEntity.getOrder(), String.valueOf((char) (answer + 'A')));
        }
        for (int i = 0; i < choiceLen; ++i) {
            if (answer != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }
}