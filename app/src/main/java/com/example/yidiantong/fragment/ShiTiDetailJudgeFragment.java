package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.yidiantong.ui.KnowledgeShiTiActivity;
import com.example.yidiantong.ui.MainBookUpActivity;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class ShiTiDetailJudgeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BookDetailJudgeFragment";
    private RecyclerInterface pageing;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;
    private TextView ftv_bd_score; // 分数
    private Button fb_bd_sumbit;  // 提交按钮
    private TextView ftv_bd_answer;  // 参考答案
    private TextView ftv_bd_stuans;  // 你的答案
    private WebView fwv_bd_analysis1;  // 解析
    private LinearLayout fll_bd_answer;
    private int stuans = -1;
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title; // 单元标题
    private ImageView fiv_bd_mark; // 标记掌握
    private ImageView fiv_bd_exercise; // 举一反三

    private String userName;  // 用户名
    private String subjectId;  // 学科id
    private String courseName;  // 课程名
    private String flag;  // 模式标记

    int[] unselectIcons = {R.drawable.error_unselect, R.drawable.right_unselect};
    int[] selectIcons = {R.drawable.error_select, R.drawable.right_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1, -1, -1};
    int questionId = 0;
    String[] option = {"错", "对"};
    private BookExerciseEntity bookExerciseEntity;
    private ImageView fiv_bd_tf;  // 你的作答后的图标（√或×）
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private int type;
    private ImageView fiv_de_icon;  // 单元图标

    SharedPreferences preferences;

    public static ShiTiDetailJudgeFragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                       String courseName, String currentpage, String allpage) {
        ShiTiDetailJudgeFragment fragment = new ShiTiDetailJudgeFragment();
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

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_judge, container, false);
        // 知识点栏
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookExerciseEntity.getQuestionsSource());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);

        //题面显示
        WebView fwv_bd_content = view.findViewById(R.id.fwv_bd_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiShow() + "</body>";
        String html = html_content.replace("#", "%23");
        fwv_bd_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // 题号和平均分
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        if (bookExerciseEntity.getQuestionKeyword().equals("")) {
            ftv_bd_num.setText("第" + currentpage + "题");
        } else {
            ftv_bd_num.setText("第" + currentpage + "题  (" + bookExerciseEntity.getQuestionKeyword() + "");
        }
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
        ClickableImageView iv_r = view.findViewById(R.id.iv_r);
        ClickableImageView iv_e = view.findViewById(R.id.iv_e);
        iv_answer[0] = iv_e;
        iv_answer[1] = iv_r;
        iv_e.setOnClickListener(this);
        iv_r.setOnClickListener(this);

        // 提交答案按钮
        fb_bd_sumbit = view.findViewById(R.id.fb_bd_sumbit);
        fb_bd_sumbit.setOnClickListener(this);
        fll_bd_answer = view.findViewById(R.id.fll_bd_answer);

        // 题目数量，设置底部<题号/总题数>显示
        int positionLen = currentpage.length();
        System.out.println("currentpage ^-^:" + currentpage);
        System.out.println("allpage ^-^:" + allpage);
        String questionNum = currentpage + "/" + allpage;
        ftv_pbd_quenum = getActivity().findViewById(R.id.ftv_pbd_quenum);
        SpannableString spanString = new SpannableString(questionNum);
        StyleSpan span = new StyleSpan(Typeface.BOLD_ITALIC);//加粗
        spanString.setSpan(span, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int sizespan = 40;
        AbsoluteSizeSpan span1 = new AbsoluteSizeSpan(sizespan); // 字号
        spanString.setSpan(span1, 0, positionLen, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ftv_pbd_quenum.setText(spanString);

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);
        ftv_bd_answer.setText("【参考答案】" + bookExerciseEntity.getShiTiAnswer());
        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#", "%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);

        // 举一反三or巩固提升模式
        fll_bd_analysis.setVisibility(View.GONE);
        fll_bd_answer.setVisibility(View.VISIBLE);
        // 显示学生本地保存的作答
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
                arrayString = preferences.getString("OnlineTestLoadAnswer", null);
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
                if (loadAnswer.equals(bookExerciseEntity.getShiTiAnswer())) {
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                } else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                }
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:  // 上一题
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:  // 下一题
                pageing.pageNext(currentpage, allpage);
                return;
            case R.id.iv_e:  // 选择错
                answer[questionId] = 0;
                showRadioBtn();
                break;
            case R.id.iv_r:  // 选择对
                answer[questionId] = 1;
                showRadioBtn();
                break;
            case R.id.fb_bd_sumbit:  // 提交答案
                if (stuans == -1) {
                    //建立对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    //自定义title样式
                    TextView tv = new TextView(getActivity());
                    tv.setText("请先选择答案!");    //内容
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
                    ftv_bd_stuans.setText("【你的作答】" + option[stuans]);
                    // 判断答案是否相等
                    if (option[stuans].equals(bookExerciseEntity.getShiTiAnswer())) {
                        fiv_bd_tf.setImageResource(R.drawable.ansright);
                    } else {
                        fiv_bd_tf.setImageResource(R.drawable.answrong);
                    }
                    // 保存学生答案至服务器
                    saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), option[stuans], type);
                    // 保存学生答案至本地
                    String arrayString = null;
                    switch (type) {
                        case 1:
                            arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] exerciseStuLoadAnswer = arrayString.split(",");
                                exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = option[stuans]; // 数组题号对应页数-1
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
                                upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = option[stuans]; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", upStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("upStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            break;
                        case 3:
                            arrayString = preferences.getString("autoStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] autoStuLoadAnswer = arrayString.split(",");
                                autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = option[stuans]; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", autoStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("autoStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            break;
                        case 5:
                            arrayString = preferences.getString("OnlineTestLoadAnswer", null);
                            if (arrayString != null) {
                                String[] OnlineTestLoadAnswer = arrayString.split(",");
                                OnlineTestLoadAnswer[Integer.parseInt(currentpage) - 1] = option[stuans]; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", OnlineTestLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("OnlineTestLoadAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                Toast.makeText(getContext(), "测试完成！", Toast.LENGTH_SHORT).show();
                                if (flag.equals("自主学习")){
                                    Intent intent = new Intent(getActivity(), KnowledgeShiTiActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    getActivity().finish();
                                }else if (flag.equals("巩固提升")){
                                    Intent intent = new Intent(getActivity(), MainBookUpActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

    // 选择答案，设置样式
    private void showRadioBtn() {
        for (int i = 0; i < 2; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
                stuans = i;
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