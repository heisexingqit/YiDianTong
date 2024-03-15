package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.ui.BookExerciseActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class BookDetailJudgeFragment extends Fragment implements View.OnClickListener {

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
    private ImageView fiv_bd_exercise;

    int[] unselectIcons = {R.drawable.error_unselect, R.drawable.right_unselect};
    int[] selectIcons = {R.drawable.error_select, R.drawable.right_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1, -1, -1};
    int questionId = 0;
    String[] option = {"错", "对"};
    private BookRecyclerEntity bookrecyclerEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int  mode = 0;
    private String currentpage;
    private String allpage;
    private ImageView fiv_de_icon;

    public static BookDetailJudgeFragment newInstance(BookRecyclerEntity bookrecyclerEntity) {
        BookDetailJudgeFragment fragment = new BookDetailJudgeFragment();
        Bundle args = new Bundle();
        args.putSerializable("bookrecyclerEntity", bookrecyclerEntity);
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
        //取出携带的参数
        Bundle arg = getArguments();
        bookrecyclerEntity = (BookRecyclerEntity)arg.getSerializable("bookrecyclerEntity");

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_judge, container, false);

        // 知识点栏
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookrecyclerEntity.getSourceName());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);
        //设置图标和类型
        int icon_id = -1;
        String SourceType = bookrecyclerEntity.getSourceType();
        switch (SourceType ){
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
        String html = html_content.replace("#","%23");
        fwv_bd_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);


        // 题号和平均分
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        ftv_bd_num.setText(bookrecyclerEntity.getNum());
        ftv_bd_score = view.findViewById(R.id.ftv_bd_score);
        ftv_bd_score.setText("得分: "+ bookrecyclerEntity.getStuScore()+" 全班均分:"+ bookrecyclerEntity.getAvgScore());

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

        // 标记掌握
        fiv_bd_mark = getActivity().findViewById(R.id.fiv_bd_mark);
        fiv_bd_mark.setOnClickListener(this);

        // 提分练习
        fiv_bd_exercise = getActivity().findViewById(R.id.fiv_bd_exercise);
        fiv_bd_exercise.setOnClickListener(this);

        setHasOptionsMenu(true);


        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);
        ftv_bd_answer.setText("【参考答案】"+ bookrecyclerEntity.getShitiAnswer());
        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#","%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);

        // 修改模式
        fll_br_model = getActivity().findViewById(R.id.fll_br_model);
        fll_br_model.setOnClickListener(this);
        ftv_br_mode = getActivity().findViewById(R.id.ftv_br_mode);
        String modename = ftv_br_mode.getText().toString();

        // 解析原本学生作答
        if(bookrecyclerEntity.getStuAnswer().length() != 0){
            stuans = bookrecyclerEntity.getStuAnswer().equals("对") ? 1 : 0;
        }
        if(modename.equals("练习模式")){
            fll_bd_analysis.setVisibility(View.GONE);
            fll_bd_answer.setVisibility(View.VISIBLE);
            mode = 0;
        }else {
            fll_bd_answer.setVisibility(View.GONE);
            fll_bd_analysis.setVisibility(View.VISIBLE);
            mode = 1;
            if(stuans == -1){
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
            }else{
                ftv_bd_stuans.setText("【你的作答】"+option[stuans]);
                fiv_bd_tf.setVisibility(View.VISIBLE);
                // 判断答案是否相等
                if(option[stuans].equals(bookrecyclerEntity.getShitiAnswer())){
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                }else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                }
            }
        }
        return view;
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
            case R.id.iv_e:
                answer[questionId] = 0;
                showRadioBtn();
                break;
            case R.id.iv_r:
                answer[questionId] = 1;
                showRadioBtn();
                break;

            case R.id.fb_bd_sumbit:
                if(stuans == -1){
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
                }else{
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                ftv_bd_stuans.setText("【你的作答】"+ option[stuans]);
                // 判断答案是否相等
                if(option[stuans].equals(bookrecyclerEntity.getShitiAnswer())){
                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                }else {
                    fiv_bd_tf.setImageResource(R.drawable.answrong);
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
            case R.id.fiv_bd_exercise:
                // 弹出一个简单的Dialog提示 "功能完善中"
                AlertDialog.Builder builder_exercise = new AlertDialog.Builder(getActivity());
                builder_exercise.setMessage("功能完善中");
                builder_exercise.setPositiveButton("确定", null);
                builder_exercise.show();


                Intent toExercise = new Intent(getActivity(), BookExerciseActivity.class);
                toExercise.putExtra("questionId", bookrecyclerEntity.getQuestionId());
//                startActivity(toExercise);
                break;
            case R.id.fll_br_model:
                AlertDialog.Builder builder_model = new AlertDialog.Builder(getActivity());
                final String[] items = new String[] { "练习模式", "复习模式" };
                builder_model.setSingleChoiceItems(items, mode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which] == "练习模式"){
                            fll_bd_answer.setVisibility(View.VISIBLE);
                            fll_bd_analysis.setVisibility(View.GONE);
                            ftv_br_mode.setText("练习模式");
                            mode = 0;
                            dialog_model.dismiss();
                        }else{
                            fll_bd_answer.setVisibility(View.GONE);
                            fll_bd_analysis.setVisibility(View.VISIBLE);
                            if(stuans == -1){
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
                            }else{
                                ftv_bd_stuans.setText("【你的作答】"+option[stuans]);
                                fiv_bd_tf.setVisibility(View.VISIBLE);
                                // 判断答案是否相等
                                if(option[stuans].equals(bookrecyclerEntity.getShitiAnswer())){
                                    fiv_bd_tf.setImageResource(R.drawable.ansright);
                                }else {
                                    fiv_bd_tf.setImageResource(R.drawable.answrong);
                                }
                            }
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
                }else{
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
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_BIAOJI + "?sourceId=" + sourceId +"&userName=" +userName +"&subjectId=" + subjectId +"&questionId=" + questionId;
        Log.e("具体错mRequestUrl",""+mRequestUrl);
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
        for (int i = 0; i < 2; ++i) {
            if (answer[questionId] != i) {
                iv_answer[i].setImageResource(unselectIcons[i]);
            } else {
                iv_answer[i].setImageResource(selectIcons[i]);
                stuans = i;
            }
        }
    }

    //复习模式你的作答
    private void mode_stuans(){

    }
}