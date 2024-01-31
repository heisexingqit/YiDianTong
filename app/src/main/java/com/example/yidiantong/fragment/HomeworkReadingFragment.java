package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.HomeworkInterface;


public class HomeworkReadingFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeworkReadingFragment";

    private PagingInterface pageing;
    private HomeworkInterface transmit;

    int[] answer;
    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select};

    ClickableImageView[][] iv_answer_drawer;

    ClickableImageView[] iv_answer = new ClickableImageView[4];
    int questionId = 0;
    private TextView tv_question_id;
    private PopupWindow window;

    //接口需要
    private HomeworkEntity homeworkEntity;
    private StuAnswerEntity stuAnswerEntity;
    private int choiceLen;

    public static HomeworkReadingFragment newInstance(HomeworkEntity homeworkEntity, int position, int size, StuAnswerEntity stuAnswerEntity) {
        HomeworkReadingFragment fragment = new HomeworkReadingFragment();
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
        pageing = (PagingInterface) context;
        transmit = (HomeworkInterface) context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        homeworkEntity = (HomeworkEntity) arg.getSerializable("homeworkEntity");
        stuAnswerEntity = (StuAnswerEntity) arg.getSerializable("stuAnswerEntity");
        choiceLen = Integer.parseInt(homeworkEntity.getQuestionChoiceList());
        answer = new int[choiceLen];
        iv_answer_drawer = new ClickableImageView[choiceLen][4];

        //同步答案
        if (stuAnswerEntity.getStuAnswer().length() > 0) {
            String[] parts = stuAnswerEntity.getStuAnswer().split(",");
            for (int i = 0; i < parts.length; ++i) {
                String part = parts[i];
                if (part.length() > 0 && !part.equals("未答")) {
                    answer[i] = part.charAt(0) - 'A';
                } else {
                    answer[i] = -1;
                }
            }
        } else {
            for (int i = 0; i < choiceLen; ++i) {
                answer[i] = -1;
            }
        }

        //获取View
        View view = inflater.inflate(R.layout.fragment_homework_reading, container, false);
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
        if (deviceAspectRatio > 2.0) {
            ViewGroup.LayoutParams params = block.getLayoutParams();
            params.height = PxUtils.dip2px(getActivity(), 80);
            block.setLayoutParams(params);
        }

        WebView wv_content = view.findViewById(R.id.wv_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + homeworkEntity.getQuestionContent() + "</body>";
        wv_content.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);

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
        showRadioBtn();

        //切换小题显示答案选项
        tv_question_id = view.findViewById(R.id.tv_question_id);
        view.findViewById(R.id.iv_quesiton_last).setOnClickListener(this);
        view.findViewById(R.id.iv_quesiton_next).setOnClickListener(this);

        //底部题号染色
        drawQustionId();

        //抽屉弹出
        view.findViewById(R.id.iv_drawer).setOnClickListener(this);

        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_page_last:
                if (window != null) {
                    closeDrawer();
                }
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                if (window != null) {
                    closeDrawer();
                }
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
            case R.id.iv_quesiton_last:
                if (questionId == 0) {
                    Toast.makeText(getActivity(), "已经是第一道小题", Toast.LENGTH_SHORT).show();
                } else {
                    questionId -= 1;
                    drawQustionId();
                    showRadioBtn();
                }
                break;
            case R.id.iv_quesiton_next:
                if (questionId == choiceLen - 1) {
                    Toast.makeText(getActivity(), "已经是最后一道小题", Toast.LENGTH_SHORT).show();
                } else {
                    questionId += 1;
                    drawQustionId();
                    showRadioBtn();
                }
                break;
            case R.id.iv_drawer:
                openDrawer();
                break;

            //抽屉按钮
        }
    }

    private void openDrawer() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.drawer_homework_reading, null, false);
        //获取组件
        /**
         * 重复选项组件
         */
        LinearLayout ll_parent = view.findViewById(R.id.ll_parent);
        for (int i = 0; i < choiceLen; ++i) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.my_component, ll_parent, false);
            TextView tv_num = v.findViewById(R.id.tv_num);
            tv_num.setText(String.valueOf(i + 1));
            iv_answer_drawer[i][0] = v.findViewById(R.id.iv_a);
            iv_answer_drawer[i][1] = v.findViewById(R.id.iv_b);
            iv_answer_drawer[i][2] = v.findViewById(R.id.iv_c);
            iv_answer_drawer[i][3] = v.findViewById(R.id.iv_d);
            ll_parent.addView(v);
        }

        //收起抽屉事件
        ClickableImageView iv_drawer_close = view.findViewById(R.id.iv_drawer_close);
        iv_drawer_close.setOnClickListener(v -> {
            closeDrawer();
        });

        //抽屉面板按钮点击事件
        View.OnClickListener myListener = v -> {
            String id = (String) v.getTag();
            String[] strArr = id.split("-");
            int qId = Integer.parseInt(strArr[0]);
            int sId = Integer.parseInt(strArr[1]);
            answer[qId] = sId;
            showRadioBtnDrawer();
        };

        for (int i = 0; i < choiceLen; ++i) {
            for (int j = 0; j < 4; ++j) {
                iv_answer_drawer[i][j].setOnClickListener(myListener);
                iv_answer_drawer[i][j].setTag(i + "-" + j);
            }
        }

        showRadioBtnDrawer();
        window = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);

        window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    //关闭抽屉
    private void closeDrawer() {
        window.dismiss();
        showRadioBtn();
    }

    //展示抽屉按钮
    private void showRadioBtnDrawer() {

        //同步答案给Activity
        String myAnswer = "";
        boolean f = false;
        boolean isEmpty = true; // 空答案判断
        for (int i = 0; i < choiceLen; ++i) {
            if (f) {
                myAnswer += ',';
            } else {
                f = !f;
            }
            if (answer[i] != -1) {
                isEmpty = false;
                myAnswer += (char) ('A' + answer[i]);
            } else {
                myAnswer += "未答";
            }
        }
        if (isEmpty) {
            myAnswer = "";
        }
        Log.e(TAG, "showRadioBtnDrawer: 是我吗");
        transmit.setStuAnswer(stuAnswerEntity.getOrder(), myAnswer);

        for (int i = 0; i < choiceLen; ++i) {
            for (int j = 0; j < 4; ++j) {
                if (answer[i] != j) {
                    iv_answer_drawer[i][j].setImageResource(unselectIcons[j]);
                } else {
                    iv_answer_drawer[i][j].setImageResource(selectIcons[j]);
                }
            }
        }
    }

    //底部题号染色
    private void drawQustionId() {

        SpannableString spannableString = StringUtils.getStringWithColor((questionId + 1) + "/" + choiceLen + "题", "#6CC1E0", 0, 1);
        tv_question_id.setText(spannableString);
    }

    //展示底部按钮
    private void showRadioBtn() {

        //同步答案给Activity
        String myAnswer = "";
        boolean f = false;
        boolean isEmpty = true; // 空答案判断
        for (int i = 0; i < choiceLen; ++i) {
            if (f) {
                myAnswer += ',';
            } else {
                f = !f;
            }
            if (answer[i] != -1) {
                isEmpty = false;
                myAnswer += (char) ('A' + answer[i]);
            } else {
                myAnswer += "未答";
            }
        }
        if (isEmpty) {
            myAnswer = "";
        }
        transmit.setStuAnswer(stuAnswerEntity.getOrder(), myAnswer);

        for (int i = 0; i < 4; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }
}