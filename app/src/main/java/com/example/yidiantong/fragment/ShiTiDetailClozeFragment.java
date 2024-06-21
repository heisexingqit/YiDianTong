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
import android.widget.EditText;
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
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.KnowledgeShiTiActivity;
import com.example.yidiantong.ui.MainBookUpActivity;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class ShiTiDetailClozeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShiTiDetailClozeFragment";
    private RecyclerInterface pageing;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;
    private TextView ftv_bd_score;
    private Button fb_bd_sumbit;
    private TextView ftv_bd_answer;
    private TextView ftv_bd_stuans;
    private WebView fwv_bd_analysis1;
    private LinearLayout fll_bd_answer;
    private String stuans = "";
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title;
    private ImageView fiv_bd_mark;
    private View view;

    private ImageView fiv_bd_exercise;// 举一反三
    private String userName;// 用户名
    private String subjectId;// 学科id
    private String courseName;// 课程名
    private String flag;// 模式标记

    private BookExerciseEntity bookExerciseEntity;
    private ImageView fiv_bd_tf;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private int type;
    private ImageView fiv_de_icon;
    private EditText et_student_answer;// 学生输入编辑框

    private String cleanShitiAnswer;
    private String cleanStuAnswer;
    private TextView tv_stu_answer;
    private WebView wv_shiti_answer;

    private SharedPreferences preferences;

    public static ShiTiDetailClozeFragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                       String courseName, String currentpage, String allpage) {
        ShiTiDetailClozeFragment fragment = new ShiTiDetailClozeFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        view = inflater.inflate(R.layout.fragment_book_detail_cloze, container, false);

        // 学生输入
        et_student_answer = view.findViewById(R.id.et_stu_answer);

        // 学生答案
        tv_stu_answer = view.findViewById(R.id.tv_stu_answer);
        // 标准答案
        wv_shiti_answer = view.findViewById(R.id.wv_shiti_answer);

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

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);

        // html清洗，去掉空格
        cleanShitiAnswer = Jsoup.clean(bookExerciseEntity.getShiTiAnswer(), Whitelist.none()).trim().replace("&nbsp;", "");

        ftv_bd_answer.setText("【参考答案】 ");
        wv_shiti_answer.loadDataWithBaseURL(null, bookExerciseEntity.getShiTiAnswer(), "text/html", "utf-8", null);

        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#", "%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);

        stuans = et_student_answer.getText().toString();
        System.out.println("et_student_answer: " + et_student_answer.getText().toString());
        System.out.println("stuans: " + stuans);
        cleanStuAnswer = stuans.replace(" ", "").replace(",", "");

        fll_bd_analysis.setVisibility(View.GONE);
        fll_bd_answer.setVisibility(View.VISIBLE);
        // 获得学生答案
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
                ftv_bd_stuans.setText("【你的作答】");
                tv_stu_answer.setText(loadAnswer);
                fiv_bd_tf.setVisibility(View.VISIBLE);
                // 判断答案是否相等
                String cleanLoadAnswer = loadAnswer.replace(" ", "").replace(",", "");
                if (cleanLoadAnswer.equals(cleanShitiAnswer)) {
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
            case R.id.iv_page_last:
                et_student_answer.setText("");
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:
                et_student_answer.setText("");
                pageing.pageNext(currentpage, allpage);
                return;
            case R.id.fb_bd_sumbit:
                stuans = et_student_answer.getText().toString();
                cleanStuAnswer = stuans.replace(" ", "").replace(",", "");
                if (stuans.length() == 0) {
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
                    ftv_bd_stuans.setText("【你的作答】");
                    tv_stu_answer.setText(stuans);
                    // 判断答案是否相等
                    if (cleanStuAnswer.equals(cleanShitiAnswer)) {
                        fiv_bd_tf.setImageResource(R.drawable.ansright);
                    } else {
                        fiv_bd_tf.setImageResource(R.drawable.answrong);
                    }
                    // 保存学生答案至服务器
                    saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), stuans, type);
                    // 保存学生答案至本地
                    String arrayString = null;
                    switch (type) {
                        case 1:
                            arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                            if (arrayString != null) {
                                String[] exerciseStuLoadAnswer = arrayString.split(",");
                                exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuans; // 数组题号对应页数-1
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
                                upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuans; // 数组题号对应页数-1
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
                                autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = stuans; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", autoStuLoadAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("autoStuLoadAnswer", arrayString);
                                editor.commit();
                            }
                            break;
                        case 5:
                            arrayString = preferences.getString("OnlineTestAnswer", null);
                            if (arrayString != null) {
                                String[] OnlineTestAnswer = arrayString.split(",");
                                OnlineTestAnswer[Integer.parseInt(currentpage) - 1] = stuans; // 数组题号对应页数-1
                                SharedPreferences.Editor editor = preferences.edit();
                                arrayString = TextUtils.join(",", OnlineTestAnswer);
                                System.out.println("arrayString: " + arrayString);
                                editor.putString("OnlineTestAnswer", arrayString);
                                editor.commit();
                            }
                            if (!arrayString.contains("null")) {
                                Toast.makeText(getContext(), "测试完成！", Toast.LENGTH_SHORT).show();
                                if (flag.equals("自主学习")){
                                    Intent intent = new Intent(getActivity(), KnowledgeShiTiActivity.class);
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
                                    Intent intent = new Intent(getActivity(), MainBookUpActivity.class);
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