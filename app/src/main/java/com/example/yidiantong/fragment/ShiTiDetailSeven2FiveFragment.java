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
import android.widget.RelativeLayout;
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
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShiTiDetailSeven2FiveFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShiTiDetailSeven2FiveFragment";
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
    private BookExerciseEntity bookExerciseEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private int type;
    private RelativeLayout frl_vedio;
    String[] option = {"A", "B", "C", "D", "E", "F", "G"};

    private ImageView fiv_bd_exercise; // 举一反三
    private String userName;
    private String subjectId;
    private String courseName;
    private String flag; // 模式标记

    private SharedPreferences preferences;

    ClickableImageView[][] iv_answer_drawer = new ClickableImageView[5][7];
    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select};
    int[] answer = {-1, -1, -1, -1, -1, -1, -1, -1};
    private WebView ftv_wv_answer;
    private WebView fwv_wb_answer;
    private String stuopt;//没有","的学生答案
    private String stuanswer;
    private String shitianswer2;
    private String keywordStr; //带有","的学生
    private String biaozhunanswer = "";//标准答案："ACD"


    public static ShiTiDetailSeven2FiveFragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                            String courseName, String currentpage, String allpage) {
        ShiTiDetailSeven2FiveFragment fragment = new ShiTiDetailSeven2FiveFragment();
        Bundle args = new Bundle();
        args.putSerializable("bookExerciseEntity", bookExerciseEntity);
        args.putInt("type", type);
        args.putString("userName", userName);
        args.putString("subjectId", subjectId);
        args.putString("courseName", courseName);
        args.putString("currentpage", currentpage);
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
        currentpage = arg.getString("currentpage");
        allpage = arg.getString("allpage");
        flag = getActivity().getIntent().getStringExtra("flag");

        //将试题答案格式化，利用正则表达式去掉<>标签及里面的内容
        String answer1 = bookExerciseEntity.getShiTiAnswer();
        String regEx = "<[^>]+>";
        Pattern p = Pattern.compile(regEx);
        Matcher m1 = p.matcher(answer1);
        String answerStr = m1.replaceAll("").trim();
        bookExerciseEntity.setShiTiAnswer(answerStr);

        //使用正则表达式获得题目答案
        Pattern pattern = Pattern.compile("故选([A-Z])");
        Matcher m = pattern.matcher(bookExerciseEntity.getShiTiAnswer());
        while (m.find()) {
            biaozhunanswer += m.group(1);
        }
        System.out.println("标准答案：" + biaozhunanswer);

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_seven2five, container, false);

        // 知识点栏
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookExerciseEntity.getQuestionKeyword());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);


        //题面显示
        WebView fwv_bd_content = view.findViewById(R.id.fwv_bd_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiShow() + "</body>";
        String html = html_content.replace("#", "%23");
        fwv_bd_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);


        // 题号和平均分
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        ftv_bd_num.setText("第" + currentpage + "题");
//        if (bookExerciseEntity.getQuestionKeyword().equals("")) {
//            ftv_bd_num.setText("第" + currentpage + "题");
//        } else {
//            ftv_bd_num.setText("第" + currentpage + "题  (" + bookExerciseEntity.getQuestionKeyword() + "");
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

        // 提交答案按钮
        fb_bd_sumbit = view.findViewById(R.id.fb_bd_sumbit);
        fb_bd_sumbit.setOnClickListener(this);
        fll_bd_answer = view.findViewById(R.id.fll_bd_answer);

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
        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnswer() + "</body>";
        fwv_wb_answer.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);


        // 举一反三or巩固提升
        fll_bd_analysis.setVisibility(View.GONE);
        fll_bd_answer.setVisibility(View.VISIBLE);

        /*getActivity().findViewById(R.id.fiv_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });*/
        // 获得学生本地保存的作答
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
                // 判断答案是否相等
                if (loadAnswer.equals(biaozhunanswer)) {
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                } else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                }
                // 给学生答案添加逗号
                StringBuilder sb = new StringBuilder(loadAnswer);
                int len = sb.length();
                for (int i = 0; i < len - 1; i++) {
                    sb.insert(i * 2 + 1, ",");
                }
                ftv_bd_stuans.setText(sb.toString());
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
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:
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
                    builder.setNegativeButton("ok", null);
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
                    Date day = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sdf.format(day);
                    if (MyApplication.typeActivity == 1) {
                        ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(stuopt);
                        ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    }else if (MyApplication.typeActivity == 2) {
                        ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(stuopt);
                        ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    }else if (MyApplication.typeActivity == 3) {
                        ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(stuopt);
                        ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    }else if (MyApplication.typeActivity == 4) {
                        ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(stuopt);
                        ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    }
                    Toast.makeText(getContext(), "答案保存成功！", Toast.LENGTH_SHORT).show();
                    // 保存学生答案至服务器
                    //saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), stuopt, type);
                    // 保存学生答案至本地
                    String arrayString = null;
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    switch (type) {
                        case 1:
                            arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] exerciseStuLoadAnswer = arrayString.split(",");
                                exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuopt; // 数组题号对应页数-1
                                System.out.println(stuanswer);
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
                                upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuopt; // 数组题号对应页数-1
                                System.out.println(stuanswer);
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
                                autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuopt; // 数组题号对应页数-1
                                System.out.println(stuanswer);
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
                                OnlineTestAnswer[Integer.parseInt(currentpage) - 1] = stuopt; // 数组题号对应页数-1
                                System.out.println(stuanswer);
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", OnlineTestAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("OnlineTestAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                Toast.makeText(getContext(), "测试完成！", Toast.LENGTH_SHORT).show();
                                if (flag.equals("自主学习")){
                                    Intent intent = new Intent(getActivity(), KnowledgeShiTiDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra("userName", getActivity().getIntent().getStringExtra("username"));
                                    intent.putExtra("subjectId", getActivity().getIntent().getStringExtra("subjectId"));
                                    intent.putExtra("unitId", getActivity().getIntent().getStringExtra("unitId"));
                                    intent.putExtra("xueduanId", getActivity().getIntent().getStringExtra("xueduan"));
                                    intent.putExtra("banbenId", getActivity().getIntent().getStringExtra("banben"));
                                    intent.putExtra("jiaocaiId", getActivity().getIntent().getStringExtra("jiaocai"));
                                    intent.putExtra("courseName", getActivity().getIntent().getStringExtra("name"));
                                    intent.putExtra("zhishidian", getActivity().getIntent().getStringExtra("zhishidian"));
                                    intent.putExtra("zhishidianId", getActivity().getIntent().getStringExtra("zhishidianId"));
                                    intent.putExtra("flag", "自主学习");
                                    startActivity(intent);
                                    getActivity().finish();
                                }else if (flag.equals("巩固提升")){
                                    Intent intent = new Intent(getActivity(), DetectionShiTiDetailActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    intent.putExtra("userName", getActivity().getIntent().getStringExtra("username"));
                                    intent.putExtra("subjectId", getActivity().getIntent().getStringExtra("subjectId"));
                                    intent.putExtra("unitId", getActivity().getIntent().getStringExtra("unitId"));
                                    intent.putExtra("courseName", getActivity().getIntent().getStringExtra("name"));
                                    intent.putExtra("flag", "自主学习");
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                            break;
                    }

                }
                break;
        }
    }


    // 判断学生答案对错
    private void judge(int state) {
        if (state == 1) {
            String shitianswer = bookExerciseEntity.getShiTiAnswer();
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
                if (MyApplication.typeActivity == 1) {
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 2) {
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 3) {
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 4) {
                    ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }
            } else {
                fiv_bd_tf.setImageResource(R.drawable.answrong);
                if (MyApplication.typeActivity == 1) {
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 2) {
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 3) {
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 4) {
                    ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }
            }
        } else if (state == 2) {
            Log.e("判断状态2", "");
            fiv_bd_tf.setVisibility(View.VISIBLE);
            // 判断答案是否相等
            if (stuopt.equals(biaozhunanswer)) {
                fiv_bd_tf.setImageResource(R.drawable.ansright);
                if (MyApplication.typeActivity == 1) {
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 2) {
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 3) {
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }else if (MyApplication.typeActivity == 4) {
                    ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
                    ((DetectionShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(bookExerciseEntity.getScore());
                }
            } else {
                fiv_bd_tf.setImageResource(R.drawable.answrong);
                if (MyApplication.typeActivity == 1) {
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 2) {
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((KnowledgeShiTiHistoryActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 3) {
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((DetectionShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }else if (MyApplication.typeActivity == 4) {
                    ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
                    ((DetectionShiTiHistoryActivity) getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore("0");
                }
            }
        }
    }

    private void saveAnswer2Server(String queAnswer, String stuAnswer,int type) {
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