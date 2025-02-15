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
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.DetectionShiTiDetailActivity;
import com.example.yidiantong.ui.DetectionShiTiHistoryActivity;
import com.example.yidiantong.ui.KnowledgeShiTiActivity;
import com.example.yidiantong.ui.KnowledgeShiTiDetailActivity;
import com.example.yidiantong.ui.KnowledgeShiTiHistoryActivity;
import com.example.yidiantong.ui.MainBookUpActivity;
import com.example.yidiantong.ui.OnlineTestDetailActivity;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ShiTiDetailMultipleFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BookDetailSingleFragment";
    private RecyclerInterface pageing;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;
    private TextView ftv_bd_score;
    private Button fb_bd_sumbit;
    private TextView ftv_bd_answer;
    private TextView ftv_bd_stuans;
    private WebView fwv_bd_analysis1;
    private LinearLayout fll_bd_answer;
    private int stuans = -1;
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title;
    private ImageView fiv_bd_mark;
    private ImageView fiv_de_icon;
    private AlertDialog dialog_model;
    private ImageView fiv_bd_exercise;// 举一反三

    private String userName;  //用户名
    private String subjectId;  //学科ID
    private String courseName;  //课程名
    private String flag;  //模式标记
    private TextView tv_all_scores;
    private TextView tv_stu_scores;

    private String currentpage;
    private String allpage;
    private int type;

    int[] unselectIcons = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2};
    int[] selectIcons = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[][] answer = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};

    int questionId = 0;
    String[] option = {"A", "B", "C", "D"};
    String result = "";
    private BookExerciseEntity bookExerciseEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private TextView ftv_br_mode;
    private int mode = 0;

    private SharedPreferences preferences;

    public static ShiTiDetailMultipleFragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                          String courseName, String currentPage, String allpage) {
        ShiTiDetailMultipleFragment fragment = new ShiTiDetailMultipleFragment();
        Bundle args = new Bundle();
        args.putSerializable("bookExerciseEntity", bookExerciseEntity);
        args.putInt("type", type);
        args.putString("userName", userName);
        args.putString("subjectId", subjectId);
        args.putString("courseName", courseName);
        args.putString("currentPage", currentPage);
        args.putString("allpage", allpage);
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
        preferences = getActivity().getSharedPreferences("shiti", Context.MODE_PRIVATE);
        //取出携带的参数
        Bundle arg = getArguments();
        bookExerciseEntity = (BookExerciseEntity) arg.getSerializable("bookExerciseEntity");
        type = arg.getInt("type");
        userName = arg.getString("userName");
        subjectId = arg.getString("subjectId");
        courseName = arg.getString("courseName");
        currentpage = arg.getString("currentPage");
        allpage = arg.getString("allpage");
        flag = getActivity().getIntent().getStringExtra("flag");

        //将试题答案格式化，利用正则表达式去掉<>标签及里面的内容
        String answer = bookExerciseEntity.getShiTiAnswer();
        String regEx = "<[^>]+>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(answer);
        String answerStr = m.replaceAll("").trim();
        bookExerciseEntity.setShiTiAnswer(answerStr);

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_multiple, container, false);

        // 知识点栏
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookExerciseEntity.getQuestionKeyword());
        tv_all_scores = view.findViewById(R.id.tv_all_scores);
        tv_stu_scores = view.findViewById(R.id.tv_stu_scores);

        //题面显示
        WebView fwv_bd_content = view.findViewById(R.id.fwv_bd_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiShow() + "</body>";
        fwv_bd_content.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);

        // 题号和平均分
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        ftv_bd_num.setText("第" + currentpage + "题" + bookExerciseEntity.getShiTiAnswer());
//        if (bookExerciseEntity.getQuestionKeyword().equals("")) {
//            ftv_bd_num.setText("第" + currentpage + "题" + bookExerciseEntity.getShiTiAnswer());
//        } else {
//            ftv_bd_num.setText("第" + currentpage + "题" + bookExerciseEntity.getShiTiAnswer() +  "(" + bookExerciseEntity.getQuestionKeyword() + "");
//        }
        ftv_bd_score = view.findViewById(R.id.ftv_bd_score);
        ftv_bd_score.setVisibility(View.GONE);

        //翻页组件
        ImageView iv_pager_last = getActivity().findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = getActivity().findViewById(R.id.iv_page_next);
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

        // 提交答案按钮
        fb_bd_sumbit = view.findViewById(R.id.fb_bd_sumbit);
        fb_bd_sumbit.setOnClickListener(this);
        fll_bd_answer = view.findViewById(R.id.fll_bd_answer);

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        //fll_bd_analysis.setVisibility(View.GONE);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);
        ftv_bd_answer.setText("【参考答案】" + bookExerciseEntity.getShiTiAnswer());
        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
        fwv_bd_analysis1.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);

        // 题目数量
        int positionLen = currentpage.length();
        String questionNum = currentpage + "/" + allpage;
        ftv_pbd_quenum = getActivity().findViewById(R.id.ftv_pbd_quenum);
        SpannableString spanString = new SpannableString(questionNum);
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);//加粗
        spanString.setSpan(span, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int sizespan = 40;
        AbsoluteSizeSpan span1 = new AbsoluteSizeSpan(sizespan); // 字号
        spanString.setSpan(span1, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ftv_pbd_quenum.setText(spanString);

        // 举一反三or巩固提升模式
        fll_bd_analysis.setVisibility(View.GONE);
        fll_bd_answer.setVisibility(View.VISIBLE);

        /*getActivity().findViewById(R.id.fiv_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });*/
        // 获得本地学生作答
        showLoadAnswer();

        return view;
    }

    private void showLoadAnswer() {
        // 显示学生本地保存的作答
        String arrayString = null;
        switch (type) {
            case 1:
                arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                break;
            case 2:
                arrayString = preferences.getString("upStuLoadAnswer", null);
                System.out.println("看看我执行了吗？");
                break;
            case 3:
                arrayString = preferences.getString("autoStuLoadAnswer", null);
                break;
            case 5:
                arrayString = preferences.getString("OnlineTestAnswer", null);
                break;
        }
        if (arrayString != null) {
            String[] exerciseStuLoadAnswer = arrayString.split(",");
            String loadAnswer = exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1];
            System.out.println("loadAnswer:" + loadAnswer);
            if (!loadAnswer.equals("null")) {
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                ftv_bd_stuans.setText("【你的作答】" + loadAnswer);
                // 判断答案是否相等
                // 将字符串转换为 double 类型
                double score = Double.parseDouble(bookExerciseEntity.getScore());
                // 计算分数的一半
                double halfScore = score / 2.0;
                String halfScoreStr;
                // 判断是否为整数或半整数并格式化
                if (halfScore % 1 == 0) {
                    // 如果是整数
                    halfScoreStr = String.valueOf((int) halfScore);
                } else if (halfScore % 1 == 0.5) {
                    // 如果是半整数
                    halfScoreStr = String.format("%.1f", halfScore);
                } else {
                    // 对于其他值（虽然在此上下文中不太可能出现）
                    halfScoreStr = String.valueOf(halfScore);
                }
                if (loadAnswer.equals(bookExerciseEntity.getShiTiAnswer())) {
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                    tv_stu_scores.setText("得分  " + bookExerciseEntity.getScore());
                } else if (bookExerciseEntity.getShiTiAnswer().contains(loadAnswer)) {
                    fiv_bd_tf.setImageResource(R.drawable.anshalf);
                    tv_stu_scores.setText("得分  " + halfScoreStr);
                } else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                    tv_stu_scores.setText("得分  0");
                }
                tv_all_scores.setText("满分  " + bookExerciseEntity.getScore());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                pageing.pageLast(currentpage, allpage);
                break;
            case R.id.iv_page_next:
                pageing.pageNext(currentpage, allpage);
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
            case R.id.fb_bd_sumbit:
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 2; j++) {
                        if (answer[i][j] != -1) {
                            stuans = 1;
                        }
                    }
                }
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
                    builder.setNegativeButton("ok", null);
                    //禁止返回和外部点击
                    builder.setCancelable(false);
                    //对话框弹出
                    builder.show();
                } else {
                    fll_bd_answer.setVisibility(View.GONE);
                    fll_bd_analysis.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 4; ++i) {
                        for (int j = 0; j < 2; j++) {
                            if (answer[i][j] != -1) {
                                result += option[answer[i][j]];
                            }
                        }
                    }
                    ftv_bd_stuans.setText("【你的作答】" + result);
                    // 判断答案是否相等
                    // 将字符串转换为 double 类型
                    double score = Double.parseDouble(bookExerciseEntity.getScore());
                    // 计算分数的一半
                    double halfScore = score / 2.0;
                    String halfScoreStr;
                    // 判断是否为整数或半整数并格式化
                    if (halfScore % 1 == 0) {
                        // 如果是整数
                        halfScoreStr = String.valueOf((int) halfScore);
                    } else if (halfScore % 1 == 0.5) {
                        // 如果是半整数
                        halfScoreStr = String.format("%.1f", halfScore);
                    } else {
                        // 对于其他值（虽然在此上下文中不太可能出现）
                        halfScoreStr = String.valueOf(halfScore);
                    }
                    if (result.equals(bookExerciseEntity.getShiTiAnswer())) {
                        fiv_bd_tf.setImageResource(R.drawable.ansright);
                        tv_stu_scores.setText("得分  " + bookExerciseEntity.getScore());
                        if (MyApplication.typeActivity == 1) {
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                        } else if (MyApplication.typeActivity == 2) {
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                        } else if (MyApplication.typeActivity == 3) {
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                        } else if (MyApplication.typeActivity == 4) {
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                        } else if (MyApplication.typeActivity == 5) {
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                        }
                    } else if (bookExerciseEntity.getShiTiAnswer().contains(result)) {
                        fiv_bd_tf.setImageResource(R.drawable.anshalf);
                        tv_stu_scores.setText("得分  " + halfScoreStr);
                        if (MyApplication.typeActivity == 1) {
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(halfScoreStr);
                        } else if (MyApplication.typeActivity == 2) {
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(halfScoreStr);
                        } else if (MyApplication.typeActivity == 3) {
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(halfScoreStr);
                        } else if (MyApplication.typeActivity == 4) {
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(halfScoreStr);
                        } else if (MyApplication.typeActivity == 5) {
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(halfScoreStr);
                        }
                    } else {
                        fiv_bd_tf.setImageResource(R.drawable.answrong);
                        tv_stu_scores.setText("得分  0");
                        if (MyApplication.typeActivity == 1) {
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                            ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                        } else if (MyApplication.typeActivity == 2) {
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                            ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                        } else if (MyApplication.typeActivity == 3) {
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                            ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                        } else if (MyApplication.typeActivity == 4) {
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                            ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                        } else if (MyApplication.typeActivity == 5) {
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                            ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                        }
                    }
                    tv_all_scores.setText("满分  " + bookExerciseEntity.getScore());
                    Date day = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sdf.format(day);
                    if (MyApplication.typeActivity == 1) {
                        ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(result);
                        ((KnowledgeShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    } else if (MyApplication.typeActivity == 2) {
                        ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(result);
                        ((KnowledgeShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    } else if (MyApplication.typeActivity == 3) {
                        ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(result);
                        ((DetectionShiTiDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    } else if (MyApplication.typeActivity == 4) {
                        ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(result);
                        ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    } else if (MyApplication.typeActivity == 5) {
                        ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(result);
                        ((OnlineTestDetailActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    }


                    Toast.makeText(getContext(), "答案保存成功！", Toast.LENGTH_SHORT).show();
                    // 保存学生答案至服务器
                    //saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), result, type);

                    // 保存学生答案至本地
                    String arrayString = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    switch (type) {
                        case 1:
                            arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] exerciseStuLoadAnswer = arrayString.split(",");
                                exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = result; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", exerciseStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("exerciseStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            break;
                        case 2:
                            arrayString = preferences.getString("upStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] upStuLoadAnswer = arrayString.split(",");
                                upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = result; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", upStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("upStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                builder.setTitle("提示")
                                        .setMessage("所有试题已经作答完毕，前往查看学习结果？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Feedback information to the Activity
                                                if (MyApplication.typeActivity == 3) {
                                                    ((DetectionShiTiDetailActivity) getActivity()).viewModel.setMyVariable(3);
                                                } else if (MyApplication.typeActivity == 4) {
                                                    ((DetectionShiTiHistoryActivity) getActivity()).viewModel.setMyVariable(4);
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }

                            break;
                        case 3:
                            arrayString = preferences.getString("autoStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] autoStuLoadAnswer = arrayString.split(",");
                                autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = result; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", autoStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("autoStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                builder.setTitle("提示")
                                        .setMessage("所有试题已经作答完毕，前往查看学习结果？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Feedback information to the Activity
                                                if (MyApplication.typeActivity == 1) {
                                                    ((KnowledgeShiTiDetailActivity) getActivity()).viewModel.setMyVariable(1);
                                                } else if (MyApplication.typeActivity == 2) {
                                                    ((KnowledgeShiTiHistoryActivity) getActivity()).viewModel.setMyVariable(2);
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                            break;
                        case 5:
                            arrayString = preferences.getString("OnlineTestAnswer", null);
                            if (arrayString != null) {
                                String[] OnlineTestAnswer = arrayString.split(",");
                                OnlineTestAnswer[Integer.parseInt(currentpage) - 1] = result; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", OnlineTestAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("OnlineTestAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                builder.setTitle("提示")
                                        .setMessage("所有试题已经作答完毕，前往查看学习结果？")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Feedback information to the Activity
                                                if (MyApplication.typeActivity == 5) {
                                                    ((OnlineTestDetailActivity) getActivity()).viewModel.setMyVariable(1);
                                                }
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                            }
                            break;
                    }
                }
                break;

        }
    }

    //展示底部按钮
    private void showRadioBtn() {
        //{-1,-1}两个中只要有一个为i，即为选中状态
        for (int i = 0; i < 4; ++i) {
            if (answer[i][0] != i && answer[i][1] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);

            } else if (answer[i][0] == i && answer[i][1] != i) {
                iv_answer[i].setImageResource(selectIcons[i]);
                answer[i][1] = i;
                answer[i][0] = -1;

            } else if (answer[i][0] != i && answer[i][1] == i) {
                iv_answer[i].setImageResource(selectIcons[i]);

            } else {
                iv_answer[i].setImageResource(unselectIcons[i]);
                answer[i][1] = -1;
                answer[i][0] = -1;
            }
        }
    }

    private void saveAnswer2Server(String queAnswer, String stuAnswer, int type) {
        String mRequestUrl = "http://www.cn901.net:8111/AppServer/ajax/studentApp_savePythonRecommendQueAnswer.do?userId=" +
                userName + "&questionId=" + bookExerciseEntity.getQuestionId() + "&queAnswer=" + queAnswer + "&stuAnswer=" +
                stuAnswer + "&baseTypeId=" + bookExerciseEntity.getBaseTypeId() + "&type=" + type;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String success = json.getString("success");
                Log.d("wen0321", "success: " + success);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = success.equals("true");
                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                boolean f = (boolean) message.obj;
                if (f) {
                    Toast.makeText(getContext(), "答案保存成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "答案保存失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}