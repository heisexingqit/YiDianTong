package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.StringUtil;

public class HomeworkClozeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeworkClozeFragment";
    int[] answer = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select};
    ClickableImageView[][] iv_answer_drawer = new ClickableImageView[10][4];
    ClickableImageView[] iv_answer = new ClickableImageView[10];
    int questionId = 0;
    private PageingInterface pageing;
    private TextView tv_question_id;
    private PopupWindow window;

    public static HomeworkClozeFragment newInstance() {
        HomeworkClozeFragment fragment = new HomeworkClozeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PageingInterface) context;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework_cloze, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //顶部题号染色
        SpannableString spannableString = StringUtil.getStringWithColor("6/6题", "#6CC1E0", 0, 1);
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
                if (questionId == 9) {
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.drawer_homework_cloze, null, false);
        //获取组件

        iv_answer_drawer[0][0] = view.findViewById(R.id.iv_1a);
        iv_answer_drawer[0][1] = view.findViewById(R.id.iv_1b);
        iv_answer_drawer[0][2] = view.findViewById(R.id.iv_1c);
        iv_answer_drawer[0][3] = view.findViewById(R.id.iv_1d);
        iv_answer_drawer[1][0] = view.findViewById(R.id.iv_2a);
        iv_answer_drawer[1][1] = view.findViewById(R.id.iv_2b);
        iv_answer_drawer[1][2] = view.findViewById(R.id.iv_2c);
        iv_answer_drawer[1][3] = view.findViewById(R.id.iv_2d);
        iv_answer_drawer[2][0] = view.findViewById(R.id.iv_3a);
        iv_answer_drawer[2][1] = view.findViewById(R.id.iv_3b);
        iv_answer_drawer[2][2] = view.findViewById(R.id.iv_3c);
        iv_answer_drawer[2][3] = view.findViewById(R.id.iv_3d);
        iv_answer_drawer[3][0] = view.findViewById(R.id.iv_4a);
        iv_answer_drawer[3][1] = view.findViewById(R.id.iv_4b);
        iv_answer_drawer[3][2] = view.findViewById(R.id.iv_4c);
        iv_answer_drawer[3][3] = view.findViewById(R.id.iv_4d);
        iv_answer_drawer[4][0] = view.findViewById(R.id.iv_5a);
        iv_answer_drawer[4][1] = view.findViewById(R.id.iv_5b);
        iv_answer_drawer[4][2] = view.findViewById(R.id.iv_5c);
        iv_answer_drawer[4][3] = view.findViewById(R.id.iv_5d);

        iv_answer_drawer[5][0] = view.findViewById(R.id.iv_6a);
        iv_answer_drawer[5][1] = view.findViewById(R.id.iv_6b);
        iv_answer_drawer[5][2] = view.findViewById(R.id.iv_6c);
        iv_answer_drawer[5][3] = view.findViewById(R.id.iv_6d);
        iv_answer_drawer[6][0] = view.findViewById(R.id.iv_7a);
        iv_answer_drawer[6][1] = view.findViewById(R.id.iv_7b);
        iv_answer_drawer[6][2] = view.findViewById(R.id.iv_7c);
        iv_answer_drawer[6][3] = view.findViewById(R.id.iv_7d);
        iv_answer_drawer[7][0] = view.findViewById(R.id.iv_8a);
        iv_answer_drawer[7][1] = view.findViewById(R.id.iv_8b);
        iv_answer_drawer[7][2] = view.findViewById(R.id.iv_8c);
        iv_answer_drawer[7][3] = view.findViewById(R.id.iv_8d);
        iv_answer_drawer[8][0] = view.findViewById(R.id.iv_9a);
        iv_answer_drawer[8][1] = view.findViewById(R.id.iv_9b);
        iv_answer_drawer[8][2] = view.findViewById(R.id.iv_9c);
        iv_answer_drawer[8][3] = view.findViewById(R.id.iv_9d);
        iv_answer_drawer[9][0] = view.findViewById(R.id.iv_10a);
        iv_answer_drawer[9][1] = view.findViewById(R.id.iv_10b);
        iv_answer_drawer[9][2] = view.findViewById(R.id.iv_10c);
        iv_answer_drawer[9][3] = view.findViewById(R.id.iv_10d);

        //收起抽屉事件
        ClickableImageView iv_drawer_close = view.findViewById(R.id.iv_drawer_close);
        iv_drawer_close.setOnClickListener(v -> {
            closeDrawer();
        });

        //抽屉面板按钮点击事件
        View.OnClickListener myListener = v -> {
            String id = (String) v.getTag();
            int qId = Integer.parseInt(id.substring(0, 1));
            int sId = Integer.parseInt(id.substring(1));
            answer[qId] = sId;
            showRadioBtnDrawer();
        };

        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 4; ++j) {
                iv_answer_drawer[i][j].setOnClickListener(myListener);
                iv_answer_drawer[i][j].setTag(i + String.valueOf(j));
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
        for (int i = 0; i < 10; ++i) {
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
        SpannableString spannableString = StringUtil.getStringWithColor((questionId + 1) + "/10题", "#6CC1E0", 0, 1);
        tv_question_id.setText(spannableString);
    }

    //展示底部按钮
    private void showRadioBtn() {
        for (int i = 0; i < 4; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
            }
        }
    }

}