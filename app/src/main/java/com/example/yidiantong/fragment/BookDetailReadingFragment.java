package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.bean.XueBaAnswerEntity;
import com.example.yidiantong.ui.BookExercise2ThreeActivity;
import com.example.yidiantong.ui.BookExerciseActivity;
import com.example.yidiantong.ui.MainBookExerciseActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BookDetailReadingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BookDetailReadingFragment";
    private RecyclerInterface pageing;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;
    private TextView ftv_bd_score;
    private Button fb_bd_sumbit;
    private TextView ftv_bd_stuans;
    private LinearLayout fll_bd_answer;
    private int stuans = -1;
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title;
    private ImageView fiv_bd_mark;
    private ImageView fiv_de_icon;
    private BookRecyclerEntity bookrecyclerEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private RelativeLayout frl_vedio;
    String[] option = {"A", "B", "C", "D", "E", "F", "G"};
    private RelativeLayout rl_loading;

    private ImageView fiv_bd_exercise; // 举一反三
    private ImageView iv_exercise_scores; //
    private String userName;
    private String subjectId;
    private String courseName;
    private Boolean exerciseType;// 是否是举一反三or巩固提升

    private SharedPreferences preferences;

    ClickableImageView[][] iv_answer_drawer = new ClickableImageView[5][7];
    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select};
    int[] answer = {-1, -1, -1, -1, -1, -1, -1, -1};
    private WebView ftv_wv_answer;
    private WebView fwv_wb_answer;
    private String stuopt;
    private String stuanswer;
    private String shitianswer2;
    private String keywordStr;
    private LinearLayout fll_bd_vedio;



    public static BookDetailReadingFragment newInstance(BookRecyclerEntity bookrecyclerEntity, String userName, String subjectId, String courseName, Boolean exerciseType) {
        BookDetailReadingFragment fragment = new BookDetailReadingFragment();
        Bundle args = new Bundle();
        args.putSerializable("bookrecyclerEntity", bookrecyclerEntity);
        args.putString("userName", userName);
        args.putString("subjectId", subjectId);
        args.putString("courseName", courseName);
        args.putBoolean("exerciseType", exerciseType);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (RecyclerInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("book", Context.MODE_PRIVATE);
        //取出携带的参数
        Bundle arg = getArguments();
        bookrecyclerEntity = (BookRecyclerEntity) arg.getSerializable("bookrecyclerEntity");
        userName = arg.getString("userName");
        subjectId = arg.getString("subjectId");
        courseName = arg.getString("courseName");
        exerciseType = arg.getBoolean("exerciseType");

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_seven2five, container, false);

        // 知识点栏
        rl_loading = view.findViewById(R.id.rl_loading);
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookrecyclerEntity.getSourceName());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);
        //设置图标和类型
        int icon_id = -1;
        String SourceType = bookrecyclerEntity.getSourceType();
        switch (SourceType) {
            case "1":
                icon_id = R.drawable.guide_plan_icon;
                break;
            case "2":
                icon_id = R.drawable.homework_icon;
                break;
            case "3":
                icon_id = R.drawable.class_icon;
            default:
                break;
        }
        fiv_de_icon.setImageResource(icon_id);

        //题面显示
        WebView fwv_bd_content = view.findViewById(R.id.fwv_bd_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiShow() + "</body>";
        String html = html_content.replace("#", "%23");
        fwv_bd_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // 题号和平均分
        currentpage = bookrecyclerEntity.getCurrentPage();  // 当前页数，题号
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        ftv_bd_num.setText("第" + currentpage + "题");
        ftv_bd_score = view.findViewById(R.id.ftv_bd_score);
        ftv_bd_score.setText("得分: " + bookrecyclerEntity.getStuScore() + " 全班均分:" + bookrecyclerEntity.getAvgScore());



        //翻页组件
        ImageView iv_pager_last = getActivity().findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = getActivity().findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        // 提交答案按钮
        fb_bd_sumbit = view.findViewById(R.id.fb_bd_sumbit);
        fb_bd_sumbit.setOnClickListener(this);
        fll_bd_answer = view.findViewById(R.id.fll_bd_answer);

        // 题目数量
        int positionLen = String.valueOf(bookrecyclerEntity.getCurrentPage()).length();
        currentpage = bookrecyclerEntity.getCurrentPage();
        allpage = bookrecyclerEntity.getAllPage();
        String questionNum = currentpage + "/" + allpage;
        ftv_pbd_quenum = getActivity().findViewById(R.id.ftv_pbd_quenum);

        SpannableString spanString = new SpannableString(questionNum);
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);//加粗
        spanString.setSpan(span, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int sizespan = 40;
        AbsoluteSizeSpan span1 = new AbsoluteSizeSpan(sizespan); // 字号
        spanString.setSpan(span1, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ftv_pbd_quenum.setText(spanString);


        // 显示答案选项
        iv_answer_drawer[0][0] = view.findViewById(R.id.iv_1a);
        iv_answer_drawer[0][1] = view.findViewById(R.id.iv_1b);
        iv_answer_drawer[0][2] = view.findViewById(R.id.iv_1c);
        iv_answer_drawer[0][3] = view.findViewById(R.id.iv_1d);
        iv_answer_drawer[0][4] = view.findViewById(R.id.iv_1e);
        iv_answer_drawer[0][5] = view.findViewById(R.id.iv_1f);
        iv_answer_drawer[0][6] = view.findViewById(R.id.iv_1g);
        // 2
        iv_answer_drawer[1][0] = view.findViewById(R.id.iv_2a);
        iv_answer_drawer[1][1] = view.findViewById(R.id.iv_2b);
        iv_answer_drawer[1][2] = view.findViewById(R.id.iv_2c);
        iv_answer_drawer[1][3] = view.findViewById(R.id.iv_2d);
        iv_answer_drawer[1][4] = view.findViewById(R.id.iv_2e);
        iv_answer_drawer[1][5] = view.findViewById(R.id.iv_2f);
        iv_answer_drawer[1][6] = view.findViewById(R.id.iv_2g);
        // 3
        iv_answer_drawer[2][0] = view.findViewById(R.id.iv_3a);
        iv_answer_drawer[2][1] = view.findViewById(R.id.iv_3b);
        iv_answer_drawer[2][2] = view.findViewById(R.id.iv_3c);
        iv_answer_drawer[2][3] = view.findViewById(R.id.iv_3d);
        iv_answer_drawer[2][4] = view.findViewById(R.id.iv_3e);
        iv_answer_drawer[2][5] = view.findViewById(R.id.iv_3f);
        iv_answer_drawer[2][6] = view.findViewById(R.id.iv_3g);
        // 4
        iv_answer_drawer[3][0] = view.findViewById(R.id.iv_4a);
        iv_answer_drawer[3][1] = view.findViewById(R.id.iv_4b);
        iv_answer_drawer[3][2] = view.findViewById(R.id.iv_4c);
        iv_answer_drawer[3][3] = view.findViewById(R.id.iv_4d);
        iv_answer_drawer[3][4] = view.findViewById(R.id.iv_4e);
        iv_answer_drawer[3][5] = view.findViewById(R.id.iv_4f);
        iv_answer_drawer[3][6] = view.findViewById(R.id.iv_4g);
        // 5
        iv_answer_drawer[4][0] = view.findViewById(R.id.iv_5a);
        iv_answer_drawer[4][1] = view.findViewById(R.id.iv_5b);
        iv_answer_drawer[4][2] = view.findViewById(R.id.iv_5c);
        iv_answer_drawer[4][3] = view.findViewById(R.id.iv_5d);
        iv_answer_drawer[4][4] = view.findViewById(R.id.iv_5e);
        iv_answer_drawer[4][5] = view.findViewById(R.id.iv_5f);
        iv_answer_drawer[4][6] = view.findViewById(R.id.iv_5g);

        //按钮点击事件
        View.OnClickListener myListener = v -> {
            String id = (String) v.getTag();
            int qId = Integer.parseInt(id.substring(0, 1));
            int sId = Integer.parseInt(id.substring(1));
            answer[qId] = sId;
            showRadioBtnDrawer();
        };
        // 显示按钮
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 7; ++j) {
                iv_answer_drawer[i][j].setOnClickListener(myListener);
                iv_answer_drawer[i][j].setTag(i + String.valueOf(j));
            }
        }

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        fwv_wb_answer = view.findViewById(R.id.fwv_wb_answer);
        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);

        WebView fwv_bd_analysis = view.findViewById(R.id.fwv_bd_analysis);
        TextView tv_shiti_analysis = view.findViewById(R.id.tv_shiti_analysis);
        LinearLayout ll_shiti_analysis = view.findViewById(R.id.ll_shiti_analysis);
        if(bookrecyclerEntity.getShitiAnalysis() == null || bookrecyclerEntity.getShitiAnalysis().length() == 0){
            tv_shiti_analysis.setVisibility(View.GONE);
            ll_shiti_analysis.setVisibility(View.GONE);
        }else {
            String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnalysis() + "</body>";
            fwv_bd_analysis.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);
        }

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnswer() + "</body>";
        fwv_wb_answer.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);

        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);


        if (!exerciseType) {
            // 提分练习
            iv_exercise_scores = getActivity().findViewById(R.id.iv_exercise_scores);
            iv_exercise_scores.setOnClickListener(this);
            setHasOptionsMenu(true);

            // 标记掌握
            fiv_bd_mark = getActivity().findViewById(R.id.fiv_bd_mark);
            fiv_bd_mark.setOnClickListener(this);
            setHasOptionsMenu(true);
            // 举一反三
            fiv_bd_exercise = getActivity().findViewById(R.id.fiv_bd_exercise);
            fiv_bd_exercise.setOnClickListener(this);
            // 修改模式
            fll_br_model = getActivity().findViewById(R.id.fll_br_model);
            fll_br_model.setOnClickListener(this);
            ftv_br_mode = getActivity().findViewById(R.id.ftv_br_mode);
            String modename = ftv_br_mode.getText().toString();

            if (modename.equals("练习模式")) {
                fll_bd_analysis.setVisibility(View.GONE);
                fll_bd_answer.setVisibility(View.VISIBLE);
                mode = 0;
//                // 显示学生本地保存的答案
//                showLoadAnswer();
            } else {
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                mode = 1;
                reviewShowAnswer();
//                stuanswer = bookrecyclerEntity.getStuAnswer();
//                if (stuanswer.length() != 0 && stuans != 2) {
//                    stuans = 1;
//                }
//                // 状态-1：json也没数据，也没有练习数据
//                if (stuans == -1) {
//                    // 创建一个 SpannableString 对象
//                    String text = "未答";
//                    SpannableString spannableString = new SpannableString(text);
//                    // 获取文本的长度
//                    int textLength = text.length();
//                    // 获取文本的后两个字符的起始位置
//                    int start = textLength - 2;
//                    // 设置后两个字符的颜色为红色
//                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    ftv_bd_stuans.setText(spannableString);
//                    fiv_bd_tf.setVisibility(View.GONE);
//                }
//                // 状态1：json有数据
//                else if (stuans == 1) {
//                    ftv_bd_stuans.setText(stuanswer);
//                    fiv_bd_tf.setVisibility(View.VISIBLE);
//                    judge(stuans);
//                }
//                // 状态2：练习有数据
//                else {
//                    ftv_bd_stuans.setText(keywordStr);
//                    fiv_bd_tf.setVisibility(View.VISIBLE);
//                    judge(stuans);
//                }
            }
        }else { // 举一反三or巩固提升
            fll_bd_analysis.setVisibility(View.GONE);
            fll_bd_answer.setVisibility(View.VISIBLE);
            // 隐藏知识点图标，得分
            fiv_de_icon.setVisibility(View.GONE);
            ftv_bd_score.setVisibility(View.GONE);
        }

        return view;
    }

    private void showLoadAnswer() {
        // 显示学生本地保存的作答
        String arrayString = preferences.getString("stuLoadAnswer", null);
        if (arrayString != null) {
            String[] stuLoadAnswer = arrayString.split(",");
            String loadAnswer = stuLoadAnswer[Integer.parseInt(currentpage) - 1];
            System.out.println("loadAnswer:" + loadAnswer);
            if (!loadAnswer.equals("null")) {
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                ftv_bd_stuans.setText(loadAnswer);
                // 判断答案是否相等
                System.out.println("bookrecyclerEntity.getShitiAnswer():" + bookrecyclerEntity.getShitiAnswer());
                if (loadAnswer.equals(bookrecyclerEntity.getShitiAnswer())) {
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                } else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                }
            }
        }
    }



    //展示按钮
    private void showRadioBtnDrawer() {
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 7; ++j) {
                if (answer[i] != j) {
                    iv_answer_drawer[i][j].setImageResource(unselectIcons[j]);
                } else {
                    iv_answer_drawer[i][j].setImageResource(selectIcons[j]);
                }
            }
        }
        // 判断选项状态，全部选择，状态stuans=2，否则stuans=-1
        int state = 0;
        stuopt = "";
        for (int i = 0; i < 5; ++i) {
            if (answer[i] == -1) {
                state++;
            } else {
                stuopt += option[answer[i]];
                Log.e("stuopt", "" + stuopt);
            }
        }
        if (state != 0) {
            stuans = -1;
        } else {
            Log.e("标记状态2", "");
            stuans = 2;
        }

        // 给学生答案添加逗号
        StringBuilder sb = new StringBuilder(stuopt);
        int len = sb.length();
        for (int i = 0; i < len; i++) {
            sb.insert(i + i + 1, ",");
        }
        keywordStr = sb.deleteCharAt(sb.length() - 1).toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
//                if (!currentpage.equals("1")) rl_loading.setVisibility(View.VISIBLE);
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:
//                if (!currentpage.equals(allpage)) rl_loading.setVisibility(View.VISIBLE);
                pageing.pageNext(currentpage, allpage);
                return;
            case R.id.fb_bd_sumbit:
                if (stuans == -1) {
                    //建立对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    //自定义title样式
                    TextView tv = new TextView(getActivity());
                    tv.setText("请输入答案");    //内容
                    tv.setTextSize(17);//字体大小
                    tv.setPadding(30, 40, 30, 40);//位置
                    tv.setTextColor(Color.parseColor("#000000"));//颜色
                    //设置title组件
                    builder.setCustomTitle(tv);
                    AlertDialog dialog = builder.create();
                    builder.setNegativeButton("关闭", null);
                    //禁止返回和外部点击
                    builder.setCancelable(false);
                    //对话框弹出
                    builder.show();
                } else {
                    fll_bd_answer.setVisibility(View.GONE);
                    fll_bd_analysis.setVisibility(View.VISIBLE);
                    fb_bd_sumbit.setVisibility(View.GONE);
                    if (stuans == 1) {
                        ftv_bd_stuans.setText(stuanswer);
                        judge(stuans);
                    } else {
                        ftv_bd_stuans.setText(keywordStr);
                        judge(stuans);
                    }

                    // 保存学生答案至本地
                    String arrayString = preferences.getString("stuLoadAnswer", null);
                    if (arrayString != null) {
                        String[] stuLoadAnswer = arrayString.split(",");
                        stuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuopt; // 数组题号对应页数-1
                        System.out.println(stuanswer);
                        SharedPreferences.Editor editor = preferences.edit();
                        arrayString = TextUtils.join(",", stuLoadAnswer);
                        System.out.println("arrayString: " + arrayString);
                        editor.putString("stuLoadAnswer", arrayString);
                        editor.commit();
                    }
                }
                break;
            case R.id.fiv_bd_mark:
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                //自定义title样式
                TextView tv = new TextView(getActivity());
                tv.setText("是否要标记本题？");    //内容
                tv.setTextSize(17);//字体大小
                tv.setPadding(30, 40, 30, 40);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv);
                builder.setMessage("标记掌握的试题将被移出错题本，可以在“错题回收站”中查看和恢复");
                AlertDialog dialog = builder.create();
                // 按钮
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //recover();
                        //pageing.updatepage();
                    }
                });
                //禁止返回和外部点击
                builder.setCancelable(false);
                //对话框弹出
                builder.show();
                break;
            case R.id.iv_exercise_scores:
                // 弹出一个简单的Dialog提示 "功能完善中"
//                AlertDialog.Builder builder_exercise = new AlertDialog.Builder(getActivity());
//                builder_exercise.setMessage("功能完善中");
//                builder_exercise.setPositiveButton("确定", null);
//                builder_exercise.show();
                Intent toExercise = new Intent(getActivity(), BookExerciseActivity.class);
                toExercise.putExtra("questionId", bookrecyclerEntity.getQuestionId());
                startActivity(toExercise);
                break;

            case R.id.fiv_bd_exercise:
                Intent intent = new Intent(getActivity(), BookExercise2ThreeActivity.class);
                intent.putExtra("userName", userName); // 用户名
                intent.putExtra("subjectId", subjectId); // 学科id
                intent.putExtra("courseName", courseName);  // 学科名
                intent.putExtra("questionId", bookrecyclerEntity.getQuestionId());   // 题目id
                startActivity(intent);
            case R.id.fll_br_model:
                AlertDialog.Builder builder_model = new AlertDialog.Builder(getActivity());
                final String[] items = new String[]{"练习模式", "复习模式"};
                builder_model.setSingleChoiceItems(items, mode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which] == "练习模式") {
                            fll_bd_answer.setVisibility(View.VISIBLE);
                            fll_bd_analysis.setVisibility(View.GONE);
                            ftv_br_mode.setText("练习模式");
                            mode = 0;
                            dialog_model.dismiss();
                            showLoadAnswer();
                        } else {
                            fll_bd_answer.setVisibility(View.GONE);
                            fll_bd_analysis.setVisibility(View.VISIBLE);
                            reviewShowAnswer();
                            ftv_br_mode.setText("复习模式");
                            mode = 1;
                            dialog_model.dismiss();
                        }
                    }
                });
                dialog_model = builder_model.create();
                dialog_model.show();
                break;
        }
    }

    private void reviewShowAnswer() {
        stuanswer = bookrecyclerEntity.getStuAnswer();
//        if (stuanswer.length() != 0 && stuans != 2) {
//            stuans = 1;
//        }
        if (stuanswer.length() != 0) {
            stuans = 1;
        }
        if (stuans == -1) {
            // 创建一个 SpannableString 对象
            String text = "未答";
            SpannableString spannableString = new SpannableString(text);
            // 获取文本的长度
            int textLength = text.length();
            // 获取文本的后两个字符的起始位置
            int start = textLength - 2;
            // 设置后两个字符的颜色为红色
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, textLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ftv_bd_stuans.setText(spannableString);
            fiv_bd_tf.setVisibility(View.GONE);
        } else {
            if (stuans == 1) {
                ftv_bd_stuans.setText(stuanswer);
                System.out.println("stuanswer: " + stuanswer);
                judge(stuans);
            } else {
                ftv_bd_stuans.setText(keywordStr);
                judge(stuans);
            }
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                int f = (int) message.obj;
                if (f == 0) {
                    Toast.makeText(getContext(), "提交失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // 错题回收站
    private void recover() {
        String sourceId = getActivity().getIntent().getStringExtra("sourceId");
        String subjectId = getActivity().getIntent().getStringExtra("subjectId");
        String questionId = getActivity().getIntent().getStringExtra("questionId");
        String userName = getActivity().getIntent().getStringExtra("username");
        Log.e("加入错题本回收站", "");
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_BIAOJI + "?sourceId=" + sourceId + "&userName=" + userName + "&subjectId=" + subjectId + "&questionId=" + questionId;
        Log.e("mRequestUrl", "" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                Message msg = Message.obtain();
                if (isSuccess) {
                    msg.obj = 1;
                } else {
                    msg.obj = 0;
                }
                msg.what = 100;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    // 判断学生答案对错
    private void judge(int state) {
        if (state == 1) {
            String shitianswer = bookrecyclerEntity.getShitiAnswer();
            String shitianswer1 = shitianswer.replace("<p>", "");
            shitianswer2 = shitianswer1.replace("</p>", "");
            Log.e("shitianswer", "" + shitianswer);
            Log.e("shitianswer2", "" + shitianswer2);
            String stuanswer1 = stuanswer.replace(",", "");
            Log.e("stuanswer", "" + stuanswer);
            Log.e("stuanswer1", "" + stuanswer1);
            fiv_bd_tf.setVisibility(View.VISIBLE);
            // 判断答案是否相等
            if (stuanswer1.equals(shitianswer2)) {
                fiv_bd_tf.setImageResource(R.drawable.ansright);
            } else {
                fiv_bd_tf.setImageResource(R.drawable.answrong);
            }
        } else if (state == 2) {
            Log.e("判断状态2", "");
            fiv_bd_tf.setVisibility(View.VISIBLE);
            // 判断答案是否相等
            if (stuopt.equals(shitianswer2)) {
                fiv_bd_tf.setImageResource(R.drawable.ansright);
            } else {
                fiv_bd_tf.setImageResource(R.drawable.answrong);
            }
        }
    }

}