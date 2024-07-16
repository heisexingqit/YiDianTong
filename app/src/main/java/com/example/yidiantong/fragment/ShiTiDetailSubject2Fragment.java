package com.example.yidiantong.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.bean.XueBaAnswerEntity;
import com.example.yidiantong.ui.BookExercise2ThreeActivity;
import com.example.yidiantong.ui.BookExerciseActivity;
import com.example.yidiantong.ui.KnowledgeShiTiActivity;
import com.example.yidiantong.ui.KnowledgeShiTiDetailActivity;
import com.example.yidiantong.ui.MainBookUpActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShiTiDetailSubject2Fragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShiTiDetailSubject2Fragment";
    private RecyclerInterface pageing;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;
    private TextView ftv_bd_score;
    private Button fb_bd_sumbit;
    private Button fb_bd_score;
    private TextView ftv_bd_answer;
    private TextView ftv_bd_stuans;
    private WebView fwv_bd_analysis1;
    private LinearLayout fll_bd_answer;
    private String stuans = "";
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title;
    private ImageView fiv_bd_mark;

    private ImageView fiv_bd_exercise;
    private ImageView iv_exercise_scores;
    private String userName;
    private String subjectId;
    private String courseName;
    private Boolean exerciseType;

    int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect};
    int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select};

    ClickableImageView[] iv_answer = new ClickableImageView[5];
    int[] answer = {-1, -1, -1, -1, -1};
    int questionId = 0;
    String[] option = {"A", "B", "C", "D"};
    private BookExerciseEntity bookExerciseEntity;
    private LinearLayout fll_br_model;
    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private ImageView fiv_de_icon;
    private EditText et_student_answer;

    private String cleanShitiAnswer;
    private String cleanStuAnswer;
    private WebView wv_stu_answer;
    private WebView wv_shiti_answer;

    private SharedPreferences preferences;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;
    private LinearLayout ll_input_image;
    private LinearLayout ll_stu_scores;
    private LinearLayout ll_stu_scores2;
    private LinearLayout ll_zero5;
    private WebView wv_stu_answer1;
    private ImageView iv_gallery;
    private ImageView iv_camera;
    private TextView tv_stu_answer;
    private TextWatcher myWatcher;
    private int type;
    private String flag; // 模式标记
    private int statusCurrent = 0; // 当前状态是否提交答案并打分

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
    private boolean show_xueba = false;
    private RelativeLayout ll_tiankong;

    // 评分
    private int scoreNum;
    private int score = -1; // -1表示未打分
    private int zero5 = 0;
    private TextView tv_zero5;
    private Button[] btnArray;
    private View[] viewArray;
    private CheckBox checkBox;
    private TextView tv_stu_scores;
    private TextView tv_all_scores;
    private ImageView fiv_bd_tf;
    private TextView tv_stu_scores2;
    private TextView tv_all_scores2;
    private ImageView fiv_bd_tf2;
    private View popView;
    private FlexboxLayout fl_score;


    public static ShiTiDetailSubject2Fragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                          String courseName, String currentpage, String allpage) {
        ShiTiDetailSubject2Fragment fragment = new ShiTiDetailSubject2Fragment();
        Bundle args = new Bundle();
        args.putSerializable("bookExerciseEntity", bookExerciseEntity);
        System.out.println("bookExerciseEntity313245:" + bookExerciseEntity.getShiTiAnswer());
        args.putInt("type", type);
        args.putString("userName", userName);
        args.putString("subjectId", subjectId);
        args.putString("courseName", courseName);
        args.putString("currentpage", currentpage);
        args.putString("allpage", allpage);
        fragment.setArguments(args);
        return fragment;
    }

    private Uri picUri, imageUri, cropUri;
    private String imageBase64;
    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private String exercise_stu_answer = "";
    private String exercise_stu_html = "";
    //答题区域HTML头
    private String html_head = "<head>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14px;" +
            "        }\n" +
            "    </style>\n" +
            "    <script>\n" +
            "        function bigimage(x) {\n" +
            "            myInterface.bigPic()\n" +
            "        }\n" +
            "    </script>\n" +
            "</head>\n" +
            "\n" +
            "<body onclick=\"bigimage(this)\">\n";
    private List<String> url_list = new ArrayList<>();

    private ImagePagerAdapter adapter;
    private PopupWindow window;
    private View contentView = null;

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
        View view = inflater.inflate(R.layout.fragment_book_shiti_subject2, container, false);
        ll_tiankong = view.findViewById(R.id.ll_tiankong);
        ll_zero5 = view.findViewById(R.id.ll_zero5);
        tv_stu_answer = view.findViewById(R.id.tv_stu_answer);
        tv_all_scores = view.findViewById(R.id.tv_all_scores);
        tv_all_scores.setText("[满分]  " + bookExerciseEntity.getScore());
        tv_all_scores2 = view.findViewById(R.id.tv_all_scores2);
        tv_all_scores2.setText("满分  " + bookExerciseEntity.getScore());

        ll_input_image = view.findViewById(R.id.ll_input_image);
        ll_input_image.setOnClickListener(this);
        wv_stu_answer = view.findViewById(R.id.wv_stu_answer);
        WebSettings webSettings = wv_stu_answer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_stu_answer.addJavascriptInterface(
                new Object() {
                    @JavascriptInterface
                    @SuppressLint("JavascriptInterface")
                    public void bigPic() {
                        /**
                         * Js注册的方法无法修改主UI，需要Handler
                         */
                        Message message = Message.obtain();
                        // 发送消息给主线程
                        //标识线程
                        message.what = 102;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");
        iv_camera = view.findViewById(R.id.iv_camera);
        iv_camera.setOnClickListener(this);
        iv_gallery = view.findViewById(R.id.iv_gallery);
        iv_gallery.setOnClickListener(this);
        // 学生输入
        et_student_answer = view.findViewById(R.id.et_stu_answer);
//        Log.e("wen0603", "onCreateView: 新建了2" + et_student_answer.getText().toString());


        // 手动清空 EditText 的内容
        myWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本内容发生改变之前调用
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 在文本内容发生改变时调用
                String inputText = s.toString();
                // 实时获取输入框内容，可以在这里进行相应处理
                // 例如，可以将输入内容显示在 Logcat 中
                exercise_stu_answer = inputText;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        et_student_answer.addTextChangedListener(myWatcher);

        // 标准答案
        wv_shiti_answer = view.findViewById(R.id.wv_shiti_answer);

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

        String html_answer = "<body style=\"color: rgb(100, 100, 100); font-size: 14px;line-height: 20px;\">" + bookExerciseEntity.getShiTiAnswer() + "</body>";
        System.out.println("shiTiAnswer ^-^:" + bookExerciseEntity.getShiTiAnswer());
        System.out.println("shiTiAnswer ^-^2:" + html_answer);

        ftv_bd_answer.setText("【参考答案】 ");
        wv_shiti_answer.loadDataWithBaseURL(null, html_answer, "text/html", "utf-8", null);


        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);
        fiv_bd_tf2 = view.findViewById(R.id.fiv_bd_tf2);
        TextView tv_shiti_analysis = view.findViewById(R.id.tv_shiti_analysis);

        if (bookExerciseEntity.getShiTiAnalysis() == null || bookExerciseEntity.getShiTiAnalysis().length() == 0) {
            tv_shiti_analysis.setVisibility(View.GONE);
        } else {
            String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
            String html1 = html_analysis.replace("#", "%23");
            fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);
        }

        // 评分部分
        ll_stu_scores = view.findViewById(R.id.ll_stu_scores);
        ll_stu_scores2 = view.findViewById(R.id.ll_stu_scores2);
        fl_score = view.findViewById(R.id.fl_score);
        tv_zero5 = view.findViewById(R.id.tv_zero5);
        // 动态加打分按钮
        tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
        tv_stu_scores2 = view.findViewById(R.id.tv_stu_scores2);
        checkBox = view.findViewById(R.id.cb_zero5);
        if (zero5 == 1) {
            checkBox.setChecked(true);
        }
        // 点击事件
        checkBox.setOnClickListener(v -> {
            if (zero5 == 1) {
                zero5 = 0;
            } else {
                zero5 = 1;
                if (score == -1) {
                    score = 0;
                }
                double nowScore = score + 0.5;
                if (nowScore > Double.parseDouble(bookExerciseEntity.getScore())) {
                    zero5 = 0;
                    checkBox.setChecked(false);
                    Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    zero5 = 1;
                }
            }
            showScoreBtn();
        });
        // 分数组件列表创建
        scoreNum = Integer.parseInt(bookExerciseEntity.getScore());
        btnArray = new Button[scoreNum + 1];
        viewArray = new View[scoreNum + 1];

        // 动态创建打分按钮
        for (int i = 0; i < scoreNum + 1; ++i) {
            viewArray[i] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_score_btn, fl_score, false);
            btnArray[i] = viewArray[i].findViewById(R.id.btn_score);
            btnArray[i].setText(String.valueOf(i));
            btnArray[i].setTag(i);

            // 点击事件
            btnArray[i].setOnClickListener(view1 -> {
                int idx = (int) view1.getTag();
                if (score != idx) {
                    double nowScore = idx + (zero5 == 1 ? 0.5 : 0);
                    if (nowScore > Double.parseDouble(bookExerciseEntity.getScore())) {
                        Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    score = idx;
                    showScoreBtn();

                    /**
                     * 测试：修改传入参数，是否能够实现实时更改
                     */

                    // 同步修改
                    if (window != null) {
                        window.dismiss();
                    }
                }
            });
        }
        // ------------------------#
        //  可伸缩打分按钮设计优化
        // ------------------------#
        if (scoreNum > 15) {
            // 设计伸缩优化
            for (int i = scoreNum; i > scoreNum - 15; --i) {
                fl_score.addView(viewArray[i]);
            }

            View plusView = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_score_btn, fl_score, false);
            Button plusBtn = plusView.findViewById(R.id.btn_score);
            plusBtn.setText("+");

            // 点击事件
            plusBtn.setOnClickListener(view1 -> {
                // popUpWindows弹窗
                showBtnPanel();
            });
            fl_score.addView(plusView);
        } else {
            // 不需要优化
            for (int i = scoreNum; i >= 0; --i) {
                fl_score.addView(viewArray[i]);
            }
        }


        // 获取ViewTreeObserver
        ViewTreeObserver viewTreeObserver = fl_score.getViewTreeObserver();
        // 添加OnGlobalLayoutListener监听器
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 在布局加载完成后调用此方法
                for (int i = 0; i < scoreNum + 1; ++i) {
                    ViewGroup.LayoutParams params = btnArray[i].getLayoutParams();
                    //params.width = fl_score.getWidth() / 8 - PxUtils.dip2px(view.getContext(), 20);
                    btnArray[i].setLayoutParams(params);
                }
                // 在需要的地方使用组件的宽度
                // 例如，可以将它用于进行其他操作或调整UI
                // ...

                // 可选：如果你只想监听一次，可以在获取宽度后移除监听器
                fl_score.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // 分数显示+按钮显示
        showScoreBtn();

        // 分数显示
        if (score == -1) {
            tv_stu_scores.setText("[得分]  ");
        } else {
            tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));
        }

        fb_bd_score = view.findViewById(R.id.fb_bd_score);
        fb_bd_score.setOnClickListener(v -> submitScore());

        //学霸答案显示
        /*tv_xueba = view.findViewById(R.id.tv_xueba);
        ftv_xuebaName1 = view.findViewById(R.id.ftv_xuebaName1);
        ftv_xuebaName2 = view.findViewById(R.id.ftv_xuebaName2);
        ftv_xuebaName3 = view.findViewById(R.id.ftv_xuebaName3);
        fwv_xuebaAnswer1 = view.findViewById(R.id.fwv_xuebaAnswer1);
        fwv_xuebaAnswer2 = view.findViewById(R.id.fwv_xuebaAnswer2);
        fwv_xuebaAnswer3 = view.findViewById(R.id.fwv_xuebaAnswer3);
        ll_xueba1 = view.findViewById(R.id.ll_xueba1);
        ll_xueba2 = view.findViewById(R.id.ll_xueba2);
        ll_xueba3 = view.findViewById(R.id.ll_xueba3);*/

        //加载学霸答案
//        loadAnswer_Net();

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#", "%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);


        stuans = bookExerciseEntity.getStuAnswer();
        cleanStuAnswer = bookExerciseEntity.getStuAnswer();

        exercise_stu_html = "";
        fll_bd_analysis.setVisibility(View.GONE);
        fll_bd_answer.setVisibility(View.VISIBLE);

        // 显示学生本地保存的作答
        // showLoadAnswer();
        // 拍照和相册copy代码=============================
        // 注册Gallery回调组件 NEW
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
//                    picUri = intent.getData();
                    Uri uri = intent.getData();
                    if (uri != null) {
                        /**
                         * 这里做了统一化操作：创建一个output.jpg文件，并将uri写入新文件，并将picUri赋给新文件（与拍照逻辑相似）
                         * 一是为了简化方法；
                         * 二是因为适配问题，有些手机应用不能返回数据，只能与拍照类似的调用方式才行；
                         * 三是因为获取本地图片只能返回uri，而不像拍照那样可以选择写入，因此需要手动。
                         */
                        File Image = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                        if (Image.exists()) {
                            Image.delete();
                        }
                        try {
                            Image.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 兼容方式获取文件Uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            picUri = FileProvider.getUriForFile(getActivity(),
                                    "com.example.yidiantong.fileprovider", Image);
                        } else {
                            picUri = Uri.fromFile(Image);
                        }

                        // uri写入文件Image
                        FileOutputStream outputStream = null;
                        FileInputStream inputStream = null;
                        try {
                            outputStream = new FileOutputStream(Image);
                            inputStream = (FileInputStream) getActivity().getContentResolver().openInputStream(uri);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            inputStream.close();
                            outputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /**
                         * 统一化操作结束++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                         */

                        Crop(picUri); // 裁剪图片
                    }

                }
            }
        });

        // 注册Camera回调组件 NEW
        mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
//                    Intent intent = new Intent(getActivity(), DoodleActivity.class);
//                    intent.putExtra("uri", imageUri.toString());
//                    mResultLauncher3.launch(intent);
                    Crop(imageUri); // 裁剪图片
                }
            }
        });

        /**
         * 注册通用裁切回调：与通用裁切方法对应。NEW
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    File Image = new File(getActivity().getExternalCacheDir(), "output_temp.jpg");
                    imageBase64 = ImageUtils.Bitmap2StrByBase64(getActivity(), Image);
                    uploadImage();
                }
            }
        });

//        getActivity().findViewById(R.id.fiv_back).setOnClickListener(v -> {
//            if (getActivity() != null) {
//                if (statusCurrent != 1) {
//                    getActivity().finish();
//                } else {
//                    showSubmitDialog();
//                }
//            }
//        });

        // 提前创建Adapter
        adapter = new ImagePagerAdapter(getActivity(), url_list);
        //showLoadAnswer();
        return view;
    }

    private void submitScore() {
        // 如果未作答, 提示请先进行评分
        if (score == -1) {
            Toast.makeText(getActivity(), "请先进行评分", Toast.LENGTH_SHORT).show();
            return;
        }

        // 取分数  tv_stu_scores.getText().toString()中"[得分]  0.5"中的0.5
        String scoreStr = tv_stu_scores.getText().toString();
        String[] split = scoreStr.split("  ");
        String tempscore = split[1];
        // 保存学生作答到本地
        String answer = exercise_stu_answer + "@&@" + exercise_stu_html + "@&@" + tempscore;
        System.out.println("exercise_stu_html" + exercise_stu_html);
        if (exercise_stu_html.equals("")) {
            ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(exercise_stu_answer);
        }else {
            ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuInput(exercise_stu_html);
        }
        String arrayString = null;
        switch (type) {
            case 1:
                arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                if (arrayString != null) {
                    String[] exerciseStuLoadAnswer = arrayString.split(",");
                    exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = answer; // 数组题号对应页数-1
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
                    upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = answer; // 数组题号对应页数-1
                    SharedPreferences.Editor editor1 = preferences.edit();
                    arrayString = TextUtils.join(",", upStuLoadAnswer);
                    System.out.println("arrayString: " + arrayString);
                    editor1.putString("upStuLoadAnswer", arrayString);
                    editor1.commit();
                }
                break;
            case 3:
                arrayString = preferences.getString("autoStuLoadAnswer", null);
                if (arrayString != null) {
                    String[] autoStuLoadAnswer = arrayString.split(",");
                    autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = answer; // 数组题号对应页数-1
                    SharedPreferences.Editor editor2 = preferences.edit();
                    arrayString = TextUtils.join(",", autoStuLoadAnswer);
                    System.out.println("arrayString: " + arrayString);
                    editor2.putString("autoStuLoadAnswer", arrayString);
                    editor2.commit();
                }
                break;
            case 5:
                arrayString = preferences.getString("OnlineTestAnswer", null);
                if (arrayString != null) {
                    String[] OnlineTestAnswer = arrayString.split(",");
                    OnlineTestAnswer[Integer.parseInt(currentpage) - 1] = answer; // 数组题号对应页数-1
                    SharedPreferences.Editor editor3 = preferences.edit();
                    arrayString = TextUtils.join(",", OnlineTestAnswer);
                    System.out.println("arrayString: " + arrayString);
                    editor3.putString("OnlineTestAnswer", arrayString);
                    editor3.commit();
                }
                if (!arrayString.contains("null")) {
                    Toast.makeText(getContext(), "测试完成！", Toast.LENGTH_SHORT).show();
                    if (flag.equals("自主学习")) {
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
                    } else if (flag.equals("巩固提升")) {
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

        statusCurrent = 2; // 提交打分
        // 判断是否满分
        if (tempscore.equals(bookExerciseEntity.getScore())) {
            fiv_bd_tf2.setImageResource(R.drawable.ansright);
            ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(1);
        } else if (tempscore.equals("0")) {
            fiv_bd_tf2.setImageResource(R.drawable.answrong);
            ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(2);
        } else {
            fiv_bd_tf2.setImageResource(R.drawable.anshalf);
            ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setAccType(3);
        }
        ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setIsZuoDaMeiPingFen(false);
        ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setStuScore(tempscore);
        ll_stu_scores.setVisibility(View.GONE);
        ll_stu_scores2.setVisibility(View.VISIBLE);
        tv_stu_scores2.setText("得分  " + tempscore);
        String mRequestUrl = "http://www.cn901.net:8111/AppServer/ajax/studentApp_updateRecommendQueScore.do?userName=" +
                userName + "&questionId=" + bookExerciseEntity.getQuestionId() + "&score=" + score + "&type=" + type;
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
                message.what = 101;
                handlerSave.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 如果提交答案但没有提交评分那么弹出提示框:请先进行评分
    private void showSubmitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("请先进行评分");
        builder.setPositiveButton("确定", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showBtnPanel() {
        // 创建一个 FlexboxLayout 实例
        if (popView == null) {
            popView = LayoutInflater.from(getActivity()).inflate(R.layout.t_homework_mark_btn_panel, null);
            FlexboxLayout popwindowView = popView.findViewById(R.id.fl_main);
            popView.findViewById(R.id.iv_close).setOnClickListener(v -> window.dismiss());

            for (int i = scoreNum - 15; i >= 0; --i) {
                popwindowView.addView(viewArray[i]);
            }

            window = new PopupWindow(
                    popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );
        }
        window.showAsDropDown(tv_stu_scores, 0, 0);

    }

    private void showScoreBtn() {
        for (int i = 0; i < scoreNum + 1; ++i) {
            if (score == i) {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report);
                btnArray[i].setTextColor(getResources().getColor(R.color.white));
            } else {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report_unselect);
                btnArray[i].setTextColor(getResources().getColor(R.color.main_bg));
            }
            tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));
        }
        if (zero5 == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
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
            String[] stuLoadAnswer = arrayString.split(",");
            String loadAnswer = stuLoadAnswer[Integer.parseInt(currentpage) - 1];
            System.out.println("loadAnswer:" + loadAnswer);
            if (!loadAnswer.equals("null")) {
                fll_bd_analysis.setVisibility(View.VISIBLE);
                fb_bd_sumbit.setVisibility(View.GONE);
                String[] split = loadAnswer.split("@&@");
                if (split[0].equals("")) {
                    ll_tiankong.setVisibility(View.GONE);
                    wv_stu_answer.loadDataWithBaseURL(null, html_head + split[1], "text/html", "utf-8", null);
                    ll_input_image.setVisibility(View.VISIBLE);
                } else {
                    tv_stu_answer.setText("【你的作答】");
                    iv_camera.setVisibility(View.GONE);
                    iv_gallery.setVisibility(View.GONE);
                    System.out.println("split[0]:" + split[0]);
                    et_student_answer.setFocusable(true);
                    et_student_answer.setText(split[0]);
                    et_student_answer.setFocusable(false);
                }
                // 设置学生分数
                ll_stu_scores2.setVisibility(View.VISIBLE);
                tv_stu_scores2.setText("得分  " + split[2]);
                // 判断是否满分
                if (split[2].equals(bookExerciseEntity.getScore())) {
                    fiv_bd_tf2.setImageResource(R.drawable.ansright);
                } else if (split[2].equals("0")) {
                    fiv_bd_tf2.setImageResource(R.drawable.answrong);
                } else {
                    fiv_bd_tf2.setImageResource(R.drawable.anshalf);
                }

                // 利用正则表达式统计"src"出现的次数
                Pattern srcPattern = Pattern.compile("src='(http[^']*)'");
                Matcher srcMatcher = srcPattern.matcher(loadAnswer);
                // 通过正则表达式匹配出所有图片的url
                while (srcMatcher.find()) {
                    String url = srcMatcher.group(1);
                    url_list.add(url); // 添加到图片url列表
                    adapter.updateData(url_list); // 更新图片列表
                }

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                if (statusCurrent != 1) {
                    if (!currentpage.equals("1")) et_student_answer.setText("");
                    pageing.pageLast(currentpage, allpage);
                } else {
                    Toast.makeText(getContext(), "请先进行评分!", Toast.LENGTH_SHORT).show();
                    //showSubmitDialog();
                }
                return;
            case R.id.iv_page_next:
                if (statusCurrent != 1) {
                    if (!currentpage.equals(allpage)) et_student_answer.setText("");
                    pageing.pageNext(currentpage, allpage);
                } else {
                    Toast.makeText(getContext(), "请先进行评分!", Toast.LENGTH_SHORT).show();
                    //showSubmitDialog();
                }
                return;
            case R.id.fb_bd_sumbit:
                if (exercise_stu_answer.length() == 0 && exercise_stu_html.length() == 0) {
                    // 答案为空
                    //建立对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    //自定义title样式
                    TextView tv = new TextView(getActivity());
                    tv.setText("请输入答案!");    //内容
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

                    statusCurrent = 1; // 提交状态
                    ll_stu_scores.setVisibility(View.VISIBLE);
                    tv_stu_answer.setText("【你的作答】");
                    iv_camera.setVisibility(View.GONE);
                    iv_gallery.setVisibility(View.GONE);

//                    show_xueba = true;
//                    loadAnswer_Net();

//                    fll_bd_answer.setVisibility(View.GONE);
                    fb_bd_sumbit.setVisibility(View.GONE);
                    // 判断编辑框是否为空
                    if (exercise_stu_answer.length() == 0) {
                        ll_tiankong.setVisibility(View.GONE);
                    }
                    fll_bd_analysis.setVisibility(View.VISIBLE);
                    et_student_answer.setFocusable(false);

                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setIsZuoDaMeiPingFen(true);
                    Date day = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sdf.format(day);
                    ((KnowledgeShiTiDetailActivity)getActivity()).bookExerciseEntityList.get(Integer.parseInt(currentpage) - 1).setZuodaDate(date);
                    Toast.makeText(getContext(), "答案保存成功！", Toast.LENGTH_SHORT).show();
                    // 保存学生答案至服务器
                    //saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), exercise_stu_answer.equals("") ? exercise_stu_html : exercise_stu_answer, type);

                }
                break;
            case R.id.iv_camera:
                et_student_answer.clearFocus();
                permissionOpenCamera();
                break;
            case R.id.iv_gallery:
                et_student_answer.clearFocus();
                permissionOpenGallery();
                break;
            case R.id.ll_input_image:

                url_list.clear();
                Document document = Jsoup.parse(exercise_stu_html);
                Elements imgElements = document.getElementsByTag("img");

                for (Element imgElement : imgElements) {
                    String src = imgElement.attr("src");
                    url_list.add(src);
                }
                if (contentView == null) {
                    if (url_list.size() == 0) return;
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.picture_menu_new, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
//                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                    //  回显方法
                    //  回显方法
                    //  回显方法
//                    contentView.findViewById(R.id.btn_save).setOnClickListener(v -> {
//                        Log.d(TAG, "onClick: ");
//                        html_answer = html_answer.replace(originUrl, identifyUrl);
//                        wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
//
//                        transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
//                        window.dismiss();
//                    });
//                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
//                        window.dismiss();
//                    });
                    vp_pic.setAdapter(adapter);

                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);

                    adapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick() {
                            vp_pic.setCurrentItem(0);
                            window.dismiss();
                        }
                    });

                    vp_pic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            tv.setText(position + 1 + "/" + url_list.size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                } else {
                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }

                adapter.notifyDataSetChanged();

                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
        }
    }

    private final Handler handlerSave = new Handler(Looper.getMainLooper()) {
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
            } else if (message.what == 101) {
                boolean f = (boolean) message.obj;
                if (f) {
                    Toast.makeText(getContext(), "分数提交成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "分数提交失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void saveAnswer2Server(String queAnswer, String stuAnswer, int type) {
        System.out.println("saveAnswer2Server: " + stuAnswer + " " + type);
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
                handlerSave.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
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
            } else if (message.what == 101) {
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
//                adapter.updateData(url_list);// 关键
                exercise_stu_html += "<img onclick='bigimage(this)' src='" + url + "' style=\"max-width:80px\">";
                wv_stu_answer.loadDataWithBaseURL(null, html_head + exercise_stu_html, "text/html", "utf-8", null);
                ll_input_image.setVisibility(View.VISIBLE);
//                transmit.offLoading();
            } else if (message.what == 102) {
                // 复用老代码 触发点击
                ll_input_image.performClick();

            }

        }
    };


    /**
     * 通用裁切方法。传输、读取文件、裁切、写入文件,最终以cropUri形式显示NEW
     *
     * @param uri 裁切前的图片Uri（pic：相册；image：照片）
     */
    private void Crop(Uri uri) {
        File Image = new File(getActivity().getExternalCacheDir(), "output_temp.jpg");
        if (Image.exists()) {
            Image.delete();
        }
        try {
            Image.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 兼容方式获取文件Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cropUri = FileProvider.getUriForFile(getActivity(),
                    "com.example.yidiantong.fileprovider", Image);
        } else {
            cropUri = Uri.fromFile(Image);
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        // 读写权限：要裁切需要先读取（读），后写入（写）
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // <关键>两步：目标URI转换为剪贴板数据 并设置给Intent
        ClipData clipData = ClipData.newUri(getActivity().getContentResolver(), "A photo", cropUri);
        intent.setClipData(clipData);

//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//
//        // outputX,outputY 是剪裁图片的宽高
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);

        // 设置输出文件位置和格式
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        intent.putExtra("noFaceDetection", true);

        mResultLauncherCrop.launch(intent);
    }

    // 图片上传
    private void uploadImage() {
//        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE;

        Map<String, String> params = new HashMap<>();
        Log.e("wen0221", "uploadImage: " + imageBase64);
        params.put("baseCode", imageBase64);
        params.put("leanPlanId", "");
        params.put("userId", MyApplication.username);

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();

                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = url;
                // 发送消息给主线程
                //标识线程
                message.what = 101;
                handler.sendMessage(message);
            } catch (JSONException e) {
//                transmit.offLoading();
                e.printStackTrace();
            }
        }, error -> {
//            transmit.offLoading();
            Log.d("wen", "Volley_Error: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        MyApplication.addRequest(request, TAG);
    }


    /**
     * 第三方权限申请包AndPermission: 自带权限组名，可直接在Fragment中回调
     * 申请拍照权限
     */
    private void permissionOpenCamera() {
        // 权限请求
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    // 获得权限后
                    @Override
                    public void onAction(List<String> data) {
                        openCamera();
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //判断是否点了永远拒绝，不再提示
                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
                            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(getActivity())
                                                .runtime()
                                                .setting()
                                                .start(REQUEST_CODE_CAMERA);
                                    })
                                    .setNegativeButton("取消", (dialog, which) -> {

                                    })
                                    .show();
                        }
                    }
                })
                .rationale(rCamera)
                .start();
    }

    private void openCamera() {
        /*
        第一步
        创建File对象，用于存储拍照后的照片，并将它存放在手机SD卡的应用关联缓存目录下。
        应用关联缓存目录：指SD卡中专门用于存放当前应用缓存数据的位置，调用getExternalCacheDir()方法可以得到该目录。
        具体的路径是/sdcard/Android/data/<package name>/cache .
        */
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
        if (outputImage.exists()) {
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        第二步
        对当前运行设备的系统版本进行判断，低于Android7.0，就调用Uri.fromFile(outputImage);
        否则，就调用FileProvider的getUriForFile()方法
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(getActivity(),
                    "com.example.yidiantong.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机程序
        Intent intent2 = new Intent("android.media.action.IMAGE_CAPTURE");
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        mResultLauncher2.launch(intent2);
    }

    /**
     * 第三方权限申请包AndPermission: 自带权限组名，可直接在Fragment中回调
     * 申请读写文件权限
     */
    private void permissionOpenGallery() {
        // 权限请求
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    // 获取权限后
                    @Override
                    public void onAction(List<String> data) {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        mResultLauncher.launch(intent);
//                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.setType("image/*");
//                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"}); // 添加特定的MIME类型以限制为图片
//                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // 设置初始目录为外部存储的图片目录
//                        mResultLauncher.launch(intent);
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        // 选择图片文件类型
//                        intent.setType("image/*");
//                        // 跳转到本地存储
//                        mResultLauncher.launch(intent);
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
//                        // 判断是否点了永远拒绝，不再提示
//                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("权限被禁用")
//                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
//                                    .setPositiveButton("跳转", (dialog, which) -> {
//                                        AndPermission.with(HomeworkTranslationFragment.this)
//                                                .runtime()
//                                                .setting()
//                                                .start(REQUEST_CODE_STORAGE);
//                                    })
//                                    .setNegativeButton("取消", (dialog, which) -> {
//
//                                    })
//                                    .show();
//                        }
                    }
                })
                .rationale(rGallery)
                .start();
    }

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。拍照申请
     */
    private Rationale rCamera = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("开启拍照权限才能拍照上传！")
                    .setPositiveButton("知道了", (dialog, which) -> {
                        executor.execute();
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        executor.cancel();
                    })
                    .show();

        }
    };

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("开启读写文件权限才能上传图片！")
                    .setPositiveButton("知道了", (dialog, which) -> {
                        executor.execute();
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        executor.cancel();
                    })
                    .show();
        }
    };

    private void setHtmlOnWebView(WebView wb, String str) {
        str = StringEscapeUtils.unescapeHtml4(str);
        // 定义图片点击放大的JavaScript函数

        String html_content = "<head><style>" +
                " p {\n" +
                "   margin: 0px;" +
                "   line-height: 30px;" +
                "   }" +
                "</style>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 14px; margin: 0px; padding: 0px\">" + str + "</body>";
        wb.getSettings().setJavaScriptEnabled(true); // 确保JavaScript可用
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }

    private Handler handler_xueba = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 102) {
                String html_answer_head = "<head>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            color: rgb(117, 117, 117);\n" +
                        "            word-wrap: break-word;\n" +
                        "            font-size: 14px;" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <script>\n" +
                        "        function lookImage(x) {\n" +
                        "        }\n" +
                        "        function bigimage(x) {\n" +
                        "            myInterface.bigPic()\n" +
                        "        }\n" +
                        "    </script>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body onclick=\"bigimage(this)\">\n";
                //学霸答案展示
                List<XueBaAnswerEntity> list = (List<XueBaAnswerEntity>) message.obj;

                if (list.size() > 0) {
                    tv_xueba.setVisibility(View.VISIBLE);
                    ftv_xuebaName1.setVisibility(View.VISIBLE);
                    ll_xueba1.setVisibility(View.VISIBLE);
                    String xuebaName1 = list.get(0).getStuName();
                    String xuebaAnswer1 = list.get(0).getStuAnswer();
                    ftv_xuebaName1.setText(xuebaName1 + "的作答");
                    setHtmlOnWebView(fwv_xuebaAnswer1, html_answer_head + xuebaAnswer1);

                }
                if (list.size() > 1) {
                    ftv_xuebaName2.setVisibility(View.VISIBLE);
                    ll_xueba2.setVisibility(View.VISIBLE);
                    String xuebaName2 = list.get(1).getStuName();
                    String xuebaAnswer2 = list.get(1).getStuAnswer();
                    ftv_xuebaName2.setText(xuebaName2 + "的作答");
                    setHtmlOnWebView(fwv_xuebaAnswer2, html_answer_head + xuebaAnswer2);
                }
                if (list.size() > 2) {
                    ftv_xuebaName3.setVisibility(View.VISIBLE);
                    ll_xueba3.setVisibility(View.VISIBLE);
                    String xuebaName3 = list.get(2).getStuName();
                    String xuebaAnswer3 = list.get(2).getStuAnswer();
                    ftv_xuebaName3.setText(xuebaName3 + "的作答");
                    setHtmlOnWebView(fwv_xuebaAnswer3, html_answer_head + xuebaAnswer3);
                }

            }
        }
    };

    private void loadAnswer_Net() {
        String sourceId = getActivity().getIntent().getStringExtra("sourceId");  // 单元id
        String mRequestUrl = Constant.API + Constant.XUEBA_ANSWER + "?paperId=" + sourceId + "&questionId=" + bookExerciseEntity.getQuestionId();

        Log.d("wen", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<XueBaAnswerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<XueBaAnswerEntity>>() {
                }.getType());
                Log.d("hsk0527", "学霸答案：" + itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = itemList;
                // 发送消息给主线程

                //标识线程
                message.what = 102;
                handler_xueba.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }


    @Override
    public void onResume() {
        super.onResume();
        showLoadAnswer();

        Log.e("wen0603", "onResume: " + exercise_stu_answer);
    }

}