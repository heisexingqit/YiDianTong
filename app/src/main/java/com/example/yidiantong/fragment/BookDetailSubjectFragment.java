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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
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
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.bean.XueBaAnswerEntity;
import com.example.yidiantong.ui.BookExercise2ThreeActivity;
import com.example.yidiantong.ui.BookExerciseActivity;
import com.example.yidiantong.ui.MainBookExerciseActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookDetailSubjectFragment extends Fragment implements View.OnClickListener {

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
    private BookRecyclerEntity bookrecyclerEntity;
    private ImageView fiv_bd_tf;
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
    private WebView wv_stu_answer1;
    private ImageView iv_gallery;
    private ImageView iv_camera;
    private TextView tv_stu_answer;
    private TextWatcher myWatcher;

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

    public static BookDetailSubjectFragment newInstance(BookRecyclerEntity bookrecyclerEntity, String userName, String subjectId, String courseName, Boolean exerciseType) {
        BookDetailSubjectFragment fragment = new BookDetailSubjectFragment();
        Bundle args = new Bundle();
        args.putSerializable("bookrecyclerEntity", bookrecyclerEntity);
        args.putString("userName", userName);
        args.putString("subjectId", subjectId);
        args.putString("courseName", courseName);
        args.putBoolean("exerciseType", exerciseType);
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

//        preferences = getActivity().getSharedPreferences("book", Context.MODE_PRIVATE);
        //取出携带的参数
        Bundle arg = getArguments();
//        Log.e("wen0603", "onCreateView: 新建了1" + exercise_stu_answer);
        bookrecyclerEntity = (BookRecyclerEntity) arg.getSerializable("bookrecyclerEntity");
        userName = arg.getString("userName");
        subjectId = arg.getString("subjectId");
        courseName = arg.getString("courseName");
        exerciseType = arg.getBoolean("exerciseType");

        //获取view
        View view = inflater.inflate(R.layout.fragment_book_detail_subject, container, false);
        ll_tiankong = view.findViewById(R.id.ll_tiankong);
        tv_stu_answer = view.findViewById(R.id.tv_stu_answer);

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

        // 解析部分
        fll_bd_analysis = view.findViewById(R.id.fll_bd_analysis);
        ftv_bd_answer = view.findViewById(R.id.ftv_bd_answer);

        // html清洗
//        cleanShitiAnswer = Jsoup.clean(bookrecyclerEntity.getShitiAnswer(), Whitelist.none()).trim().replace("&nbsp;", "");
        String html_answer = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnswer() + "</body>";
        String html0 = html_answer.replace("#", "%23");

        ftv_bd_answer.setText("【参考答案】 ");
        wv_shiti_answer.loadDataWithBaseURL(null, html0, "text/html", "utf-8", null);


        ftv_bd_stuans = view.findViewById(R.id.ftv_bd_stuans);
        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);
        fiv_bd_tf = view.findViewById(R.id.fiv_bd_tf);
        TextView tv_shiti_analysis = view.findViewById(R.id.tv_shiti_analysis);
        LinearLayout ll_shiti_analysis = view.findViewById(R.id.ll_shiti_analysis);
        if (bookrecyclerEntity.getShitiAnalysis() == null || bookrecyclerEntity.getShitiAnalysis().length() == 0) {
            tv_shiti_analysis.setVisibility(View.GONE);
            ll_shiti_analysis.setVisibility(View.GONE);
        } else {
            String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnalysis() + "</body>";
            String html1 = html_analysis.replace("#", "%23");
            fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);
        }


        //学霸答案显示
        tv_xueba = view.findViewById(R.id.tv_xueba);
        ftv_xuebaName1 = view.findViewById(R.id.ftv_xuebaName1);
        ftv_xuebaName2 = view.findViewById(R.id.ftv_xuebaName2);
        ftv_xuebaName3 = view.findViewById(R.id.ftv_xuebaName3);
        fwv_xuebaAnswer1 = view.findViewById(R.id.fwv_xuebaAnswer1);
        fwv_xuebaAnswer2 = view.findViewById(R.id.fwv_xuebaAnswer2);
        fwv_xuebaAnswer3 = view.findViewById(R.id.fwv_xuebaAnswer3);
        ll_xueba1 = view.findViewById(R.id.ll_xueba1);
        ll_xueba2 = view.findViewById(R.id.ll_xueba2);
        ll_xueba3 = view.findViewById(R.id.ll_xueba3);

        //加载学霸答案
        loadAnswer_Net();

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getShitiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#", "%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);


//        stuans = bookrecyclerEntity.getStuAnswer();
        cleanStuAnswer = bookrecyclerEntity.getStuScore();

        if (!exerciseType) {
            exercise_stu_html = "";
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

            // 模式初始化 默认学生作答
            if (modename.equals("练习模式")) {
                fll_bd_analysis.setVisibility(View.GONE);
                fll_bd_answer.setVisibility(View.VISIBLE);
                mode = 0;
            } else {
                fll_bd_answer.setVisibility(View.GONE);
                fll_bd_analysis.setVisibility(View.VISIBLE);
                mode = 1;
                if (stuans.length() == 0) {
                    ftv_bd_stuans.setText("【你的作答】");
                    fiv_bd_tf.setVisibility(View.GONE); // 隐藏对错图标
                } else {
                    ftv_bd_stuans.setText("【你的作答】");
                    // 答案显示
                    html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getStuAnswer() + "</body>";
                    html = html_content.replace("#", "%23");
                    wv_stu_answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    fiv_bd_tf.setVisibility(View.VISIBLE); // 显示对错图标
                }
                mode_stuans();
            }
        } else {
            fll_bd_analysis.setVisibility(View.GONE);
            fll_bd_answer.setVisibility(View.VISIBLE);
            // 隐藏知识点图标,得分
            fiv_de_icon.setVisibility(View.GONE);
            ftv_bd_score.setVisibility(View.GONE);
        }

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
        // 提前创建Adapter
        adapter = new ImagePagerAdapter(getActivity(), url_list);
        return view;
    }

//    private void showLoadAnswer() {
//        // 显示学生本地保存的作答
//        String arrayString = preferences.getString("stuLoadAnswer", null);
//        if (arrayString != null) {
//            String[] stuLoadAnswer = arrayString.split(",");
//            String loadAnswer = stuLoadAnswer[Integer.parseInt(currentpage) - 1];
//            System.out.println("loadAnswer:" + loadAnswer);
//            if (!loadAnswer.equals("null")) {
//                fll_bd_answer.setVisibility(View.GONE);
//                fll_bd_analysis.setVisibility(View.VISIBLE);
//                ftv_bd_stuans.setText("【你的作答】" + loadAnswer);
//                // 判断答案是否相等
//                if (loadAnswer.equals(bookrecyclerEntity.getShitiAnswer())) {
//                    fiv_bd_tf.setImageResource(R.drawable.ansright);
//                } else {
//                    fiv_bd_tf.setImageResource(R.drawable.answrong);
//                }
//            }
//        }
//    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_page_last:
                pageing.pageLast(currentpage, allpage);
                et_student_answer.setText("");

                return;
            case R.id.iv_page_next:
                pageing.pageNext(currentpage, allpage);
                et_student_answer.setText("");

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
                    tv_stu_answer.setText("【你的作答】");
                    iv_camera.setVisibility(View.GONE);
                    iv_gallery.setVisibility(View.GONE);

                    show_xueba = true;
                    loadAnswer_Net();

//                    fll_bd_answer.setVisibility(View.GONE);
                    et_student_answer.clearFocus();
                    fb_bd_sumbit.setVisibility(View.GONE);

                    fll_bd_analysis.setVisibility(View.VISIBLE);
//                    ftv_bd_stuans.setText("【你的作答】");
//                    String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getStuAnswer() + "</body>";
//                    String html = html_content.replace("#", "%23");
//                    wv_stu_answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

                    // 判断答案是半对还是全对
//                    if (Float.parseFloat(cleanStuAnswer) < 0.3) {
//                        fiv_bd_tf.setImageResource(R.drawable.answrong);
//                    } else {
//                        fiv_bd_tf.setImageResource(R.drawable.anshalf);
//                    }
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
                            tv_stu_answer.setText("  请输入答案:");
                            ll_tiankong.setVisibility(View.VISIBLE);
                            ll_input_image.setVisibility(View.GONE);
                            fb_bd_sumbit.setVisibility(View.VISIBLE);
                            mode = 0;
                            dialog_model.dismiss();
                            et_student_answer.setText("");
                            exercise_stu_html = "";
//                            showLoadAnswer();
                        } else {
                            Log.e("wen0601", "onClick: 复习模式选择触发");
                            ll_tiankong.setVisibility(View.GONE);
                            wv_stu_answer.setVisibility(View.VISIBLE);
                            fll_bd_analysis.setVisibility(View.VISIBLE);
                            tv_stu_answer.setText("【你的作答】");
                            fb_bd_sumbit.setVisibility(View.GONE);

//                            if (stuans.length() == 0) {
//                                ftv_bd_stuans.setText("【你的作答】");
//                                fiv_bd_tf.setVisibility(View.GONE);
//                            } else {
//                                ftv_bd_stuans.setText("【你的作答】");
//                                String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getStuAnswer() + "</body>";
//                                String html = html_content.replace("#", "%23");
//                                wv_stu_answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
//
//                                fiv_bd_tf.setVisibility(View.VISIBLE);
//                            }
                            ftv_br_mode.setText("复习模式");
                            mode = 1;
                            dialog_model.dismiss();
                            mode_stuans();
                        }
                    }
                });
                dialog_model = builder_model.create();
                dialog_model.show();
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
                    tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);

                    adapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick() {
                            vp_pic.setCurrentItem(0);
                            window.dismiss();
                        }
// // 手写公式识别-弃
//                        @Override
//                        public void onLongItemClick(int pos) {
//                            Toast.makeText(getActivity(), "长按", Toast.LENGTH_SHORT).show();
//                            if (contentView2 == null) {
//                                contentView2 = LayoutInflater.from(getActivity()).inflate(R.layout.menu_pic_identify, null, false);
//                                //绑定点击事件
//                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
//                                    identifyUrl = picIdentify(url_list.get(pos));
//                                    originUrl = url_list.get(pos);
//                                    url_list.set(pos, identifyUrl);
//                                    adapter.notifyDataSetChanged();
//                                    ll_selector.setVisibility(View.VISIBLE);
//
//                                    window2.dismiss();
//                                });
//
//                                window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//                                window2.setTouchable(true);
//                            }
//                            window2.showAtLocation(contentView2, Gravity.CENTER, 0, 0);
//
//                        }
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
                    tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }

                adapter.notifyDataSetChanged();

                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
        }
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

    // 错题回收站
    private void recover() {
        String sourceId = getActivity().getIntent().getStringExtra("sourceId");
        String subjectId = getActivity().getIntent().getStringExtra("subjectId");
        String questionId = bookrecyclerEntity.getQuestionId();
        String userName = getActivity().getIntent().getStringExtra("username");
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_BIAOJI + "?sourceId=" + sourceId + "&userName=" + userName + "&subjectId=" + subjectId + "&questionId=" + questionId;
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

    //复习模式你的作答
    private void mode_stuans() {
        ll_input_image.setVisibility(View.VISIBLE);
        String html_content = "", html = "";
        if (bookrecyclerEntity.getStuAnswer().length() == 0) {
            html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\"><p style=\"color: red;\">未答</p></body>";
            html = html_content.replace("#", "%23");
            wv_stu_answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } else {
            html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookrecyclerEntity.getStuAnswer() + "</body>";
            html = html_content.replace("#", "%23");
            wv_stu_answer.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        }
    }

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
        String mRequestUrl = Constant.API + Constant.XUEBA_ANSWER + "?paperId=" + sourceId + "&questionId=" + bookrecyclerEntity.getQuestionId();

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
        et_student_answer.setText(exercise_stu_answer);

        Log.e("wen0603", "onResume: " + exercise_stu_answer);
    }

}