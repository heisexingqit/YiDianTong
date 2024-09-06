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

// 单选题详情界面, 用于展示单选题详情,举一反三,标记掌握,模式切换
public class BookDetailSingleFragment extends Fragment implements View.OnClickListener {

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
    private ImageView iv_exercise_scores;
    private ImageView fiv_bd_exercise;// 举一反三
    private TextView tv_all_scores;
    private TextView tv_stu_scores;

    private String userName; // 用户名
    private String subjectId; // 学科ID
    private String courseName; // 课程名
    private Boolean exerciseType; // 是否是举一反三or巩固提升
    private RelativeLayout rl_loading;

    //学霸答案
    private TextView tv_xueba;
    private TextView ftv_xuebaName1;
    private TextView ftv_xuebaName2;
    private TextView ftv_xuebaName3;
    private WebView fwv_xuebaAnswer1;
    private WebView fwv_xuebaAnswer2;
    private WebView fwv_xuebaAnswer3;
    private LinearLayout ll_xueba3;
    private LinearLayout ll_xueba1;
    private LinearLayout ll_xueba2;

    SharedPreferences preferences;

    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1, -1, -1};
    int questionId = 0;
    String[] option = {"A", "B", "C", "D"};
    private BookRecyclerEntity bookrecyclerEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private ImageView fiv_de_icon;

    public static BookDetailSingleFragment newInstance(BookRecyclerEntity bookrecyclerEntity, String userName, String subjectId, String courseName, Boolean exerciseType) {
        BookDetailSingleFragment fragment = new BookDetailSingleFragment();
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
        View view = inflater.inflate(R.layout.fragment_book_detail_single, container, false);

        // 知识点栏
        rl_loading = view.findViewById(R.id.rl_loading);
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookrecyclerEntity.getSourceName());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);
        tv_all_scores = view.findViewById(R.id.tv_all_scores);
        tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
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

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);
        ftv_bd_answer.setText("【参考答案】" + bookrecyclerEntity.getShitiAnswer());
        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);
        TextView tv_shiti_analysis = view.findViewById(R.id.tv_shiti_analysis);
        LinearLayout ll_shiti_analysis = view.findViewById(R.id.ll_shiti_analysis);
        if(bookrecyclerEntity.getShitiAnalysis() == null || bookrecyclerEntity.getShitiAnalysis().length() == 0){
            tv_shiti_analysis.setVisibility(View.GONE);
            ll_shiti_analysis.setVisibility(View.GONE);
        }else {
            String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnalysis() + "</body>";
            String html1 = html_analysis.replace("#", "%23");
            fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);
        }
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
            // 修改练习or复习模式
            fll_br_model = getActivity().findViewById(R.id.fll_br_model);
            fll_br_model.setOnClickListener(this);
            ftv_br_mode = getActivity().findViewById(R.id.ftv_br_mode);
            String modename = ftv_br_mode.getText().toString();

            if (modename.equals("练习模式")) {
                fll_bd_analysis.setVisibility(View.GONE);
                fll_bd_answer.setVisibility(View.VISIBLE);
                mode = 0;
//                // 显示学生本地保存的作答
//                showLoadAnswer();
            } else {
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                mode = 1;
                reviewShowAnswer();
            }
        } else { // 举一反三or巩固提升模式
            fll_bd_analysis.setVisibility(View.GONE);
            fll_bd_answer.setVisibility(View.VISIBLE);
            // 隐藏知识点图标,得分
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
                ftv_bd_stuans.setText("【你的作答】" + loadAnswer);
                // 判断答案是否相等
                if (loadAnswer.equals(bookrecyclerEntity.getShitiAnswer())) {
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                    tv_stu_scores.setText("得分：" + bookrecyclerEntity.getScore());
                } else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                    tv_stu_scores.setText("得分：0");
                }
                tv_all_scores.setText("满分：" + bookrecyclerEntity.getScore());
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                if (!currentpage.equals("1")) rl_loading.setVisibility(View.VISIBLE);
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:
                if (!currentpage.equals(allpage)) rl_loading.setVisibility(View.VISIBLE);
                pageing.pageNext(currentpage, allpage);
                return;
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
                    ftv_bd_stuans.setText("【你的作答】" + option[stuans]);
                    // 判断答案是否相等
                    if (option[stuans].equals(bookrecyclerEntity.getShitiAnswer())) {
                        fiv_bd_tf.setImageResource(R.drawable.ansright);
                    } else {
                        fiv_bd_tf.setImageResource(R.drawable.answrong);
                    }

                    // 保存学生答案至本地
                    String arrayString = preferences.getString("stuLoadAnswer", null);
                    if (arrayString != null) {
                        String[] stuLoadAnswer = arrayString.split(",");
                        stuLoadAnswer[Integer.parseInt(currentpage) - 1] = option[stuans]; // 数组题号对应页数-1
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
                        recover();
                        pageing.updatepage(currentpage, allpage);
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
            case R.id.fiv_bd_exercise:  // 举一反三
                Intent intent = new Intent(getActivity(), BookExercise2ThreeActivity.class);
                intent.putExtra("userName", userName);  // 用户名
                intent.putExtra("subjectId", subjectId);    // 学科id
                intent.putExtra("courseName", courseName);  // 学科名
                intent.putExtra("questionId", bookrecyclerEntity.getQuestionId());   // 题目id
                startActivity(intent);
                break;
            case R.id.fll_br_model: // 模式选择
                AlertDialog.Builder builder_model = new AlertDialog.Builder(getActivity());
                final String[] items = new String[]{"练习模式", "复习模式"};
                builder_model.setSingleChoiceItems(items, mode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (items[which] == "练习模式") {
                            stuans = -1; // 初始化stuans
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
                        Log.e("0123", "onClick: " + bookrecyclerEntity.getStuAnswer());
                        Log.e("0123", "onClick: " + stuans);
                    }
                });
                dialog_model = builder_model.create();
                dialog_model.show();
                break;
        }
    }

    private void reviewShowAnswer() {
        // 解析原本学生作答的答案
        if (bookrecyclerEntity.getStuAnswer().length() != 0) {
            stuans = (int) (bookrecyclerEntity.getStuAnswer().charAt(0) - 'A');
        }
        if (stuans == -1) {
            // 创建一个 SpannableString 对象
            String text = "【你的作答】 未答";
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
            ftv_bd_stuans.setText("【你的作答】" + option[stuans]);
            fiv_bd_tf.setVisibility(View.VISIBLE);
            // 判断答案是否相等
            if (option[stuans].equals(bookrecyclerEntity.getShitiAnswer())) {
                fiv_bd_tf.setImageResource(R.drawable.ansright);
            } else {
                fiv_bd_tf.setImageResource(R.drawable.answrong);
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
                } else {
                    Toast.makeText(getContext(), "提交成功！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // 错题回收站
    private void recover() {
        String sourceId = getActivity().getIntent().getStringExtra("sourceId");
        String subjectId = getActivity().getIntent().getStringExtra("subjectId");
        String questionId = bookrecyclerEntity.getQuestionId();
        String userName = getActivity().getIntent().getStringExtra("username");
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_BIAOJI + "?sourceId=" + sourceId + "&userName=" + userName + "&subjectId=" + subjectId + "&questionId=" +  bookrecyclerEntity.getQuestionId();
        Log.e("具体错mRequestUrl", "" + mRequestUrl);
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

    //展示底部按钮
    private void showRadioBtn() {
        for (int i = 0; i < 4; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
                stuans = i;
            }
        }
    }

    //复习模式你的作答
    private void mode_stuans() {

    }


}