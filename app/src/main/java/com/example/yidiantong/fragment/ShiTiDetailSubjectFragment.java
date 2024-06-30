package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.KnowledgeShiTiActivity;
import com.example.yidiantong.ui.MainBookUpActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShiTiDetailSubjectFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ShiTiDetailSubjectFragment";
    private RecyclerInterface pageing;
    private HomeworkInterface transmit;
    private TextView ftv_bd_num;
    private TextView ftv_pbd_quenum;

    private TextView ftv_bd_answer;
    private WebView fwv_bd_analysis1;
    private String stuans = "";
    private LinearLayout fll_bd_analysis;
    private TextView ftv_br_title;
    private ImageView fiv_bd_mark;


    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncher3;
    private ActivityResultLauncher<Intent> mResultLauncherCrop; //NEW
    private Uri picUri, imageUri, cropUri;//NEW
    // 软键盘弹出NEW
    private boolean isKeyboardVisible = false;
    private InputMethodManager imm;
    // 作答组件
    private EditText et_answer;
    private WebView wv_answer;
    //html内容数据
    private String html_answer = "";
    //答题区域HTML头
    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        img{\n" +
            "        vertical-align: middle;" +
            "        max-width:40px !important;" +
            "        height:40px !important;" +
            "        }" +
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
    //html尾
    private String html_answer_tail = "</body>";
    //点击大图
    private List<String> url_list = new ArrayList<>();
    private PopupWindow window;
    private ImagePagerAdapter adapter;
    private LinearLayout ll_answer;
    //图片编码
    private String imageBase64;
    private View contentView = null;
    //输入答案
    private LinearLayout ll_bottom_block;

    private ImageView fiv_bd_exercise;
    private String userName;
    private String subjectId;
    private String courseName;
    private String flag; // 模式标记

    private BookExerciseEntity bookExerciseEntity;

    private AlertDialog dialog_model;
    private TextView ftv_br_mode;
    private int mode = 0;
    private String currentpage;
    private String allpage;
    private int type;
    private ImageView fiv_de_icon;


    private String cleanShitiAnswer;
    private String cleanStuAnswer;
    private WebView wv_stu_answer;
    private WebView wv_shiti_answer;

    private SharedPreferences preferences;

    public static ShiTiDetailSubjectFragment newInstance(BookExerciseEntity bookExerciseEntity, int type, String userName, String subjectId,
                                                         String courseName, String currentpage, String allpage) {
        ShiTiDetailSubjectFragment fragment = new ShiTiDetailSubjectFragment();
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
        View view = inflater.inflate(R.layout.fragment_book_shiti_subject, container, false);

        et_answer = view.findViewById(R.id.et_answer);

        // 标准答案
        wv_shiti_answer = view.findViewById(R.id.wv_shiti_answer);
        //作答面显示
        wv_answer = view.findViewById(R.id.wv_answer);
        wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
        WebSettings webSettings = wv_answer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_answer.addJavascriptInterface(
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
                        message.what = 101;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");

        // 知识点栏
        ftv_br_title = view.findViewById(R.id.ftv_br_title);
        ftv_br_title.setText(bookExerciseEntity.getQuestionsSource());
        fiv_de_icon = view.findViewById(R.id.fiv_de_icon);

        //题面显示
        WebView fwv_bd_content = view.findViewById(R.id.fwv_bd_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiShow() + "</body>";
        String html = html_content.replace("#", "%23");
        fwv_bd_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);


        // 题号
        ftv_bd_num = view.findViewById(R.id.ftv_bd_num);
        if (bookExerciseEntity.getQuestionKeyword().equals("")) {
            ftv_bd_num.setText("第" + currentpage + "题");
        } else {
            ftv_bd_num.setText("第" + currentpage + "题  (" + bookExerciseEntity.getQuestionKeyword() + "");
        }

        //翻页组件
        ImageView iv_pager_last = getActivity().findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = getActivity().findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

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

        // html清洗
        cleanShitiAnswer = Jsoup.clean(bookExerciseEntity.getShiTiAnswer(), Whitelist.none()).trim().replace("&nbsp;", "");

        ftv_bd_answer.setText("【参考答案】 ");
        wv_shiti_answer.loadDataWithBaseURL(null, bookExerciseEntity.getShiTiAnswer(), "text/html", "utf-8", null);


        fwv_bd_analysis1 = view.findViewById(R.id.fwv_bd_analysis);

        String html_analysis = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + bookExerciseEntity.getShiTiAnalysis() + "</body>";
        String html1 = html_analysis.replace("#", "%23");
        fwv_bd_analysis1.loadDataWithBaseURL(null, html1, "text/html", "utf-8", null);

        stuans = bookExerciseEntity.getStuAnswer();
        cleanStuAnswer = bookExerciseEntity.getStuAnswer();

        fll_bd_analysis.setVisibility(View.GONE);

        ll_answer = view.findViewById(R.id.ll_answer);
        ll_answer.setOnClickListener(this);
        ll_bottom_block = view.findViewById(R.id.ll_bottom_block);

        view.findViewById(R.id.tv_save).setOnClickListener(this);  //保存答案按钮
        view.findViewById(R.id.civ_camera).setOnClickListener(this);//拍照
        view.findViewById(R.id.civ_gallery).setOnClickListener(this);//相册
        // 注册Gallery回调组件 NEW
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
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
                if (result.getResultCode() == getActivity().RESULT_OK) {
//                    Intent intent = new Intent(getActivity(), DoodleActivity.class);
//                    intent.putExtra("uri", imageUri.toString());
//                    mResultLauncher3.launch(intent);
                    Crop(imageUri); // 裁剪图片
                }
            }
        });

        // 注册 涂鸦板
        mResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = result.getData();
                    imageBase64 = intent.getStringExtra("data");
                    try {
                        imageBase64 = URLEncoder.encode(imageBase64, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    uploadImage();
                }
            }
        });

        /**
         * 注册通用裁切回调：与通用裁切方法对应。NEW
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    File Image = new File(getActivity().getExternalCacheDir(), "output_temp.jpg");
                    imageBase64 = ImageUtils.Bitmap2StrByBase64(getActivity(), Image);
                    uploadImage();
                }
            }
        });

        // 提前创建Adapter
        adapter = new ImagePagerAdapter(getActivity(), url_list);

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
                ll_bottom_block.setVisibility(View.GONE);
                String s = html_answer_head + loadAnswer + html_answer_tail;
                wv_answer.loadDataWithBaseURL(null, s, "text/html", "utf-8", null);

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
                pageing.pageLast(currentpage, allpage);
                return;
            case R.id.iv_page_next:
                pageing.pageNext(currentpage, allpage);
                return;
            case R.id.tv_save:
                fll_bd_analysis.setVisibility(View.VISIBLE);
                ll_bottom_block.setVisibility(View.GONE);
                html_answer += et_answer.getText().toString();
                System.out.println("html_answer:" + html_answer);

                // 保存学生答案至服务器
                saveAnswer2Server(bookExerciseEntity.getShiTiAnswer(), html_answer, type);

                // 保存学生答案至本地
                String arrayString = null;
                switch (type) {
                    case 1:
                        arrayString = preferences.getString("exerciseStuLoadAnswer", null);
                        if (arrayString != null) {
                            String[] exerciseStuLoadAnswer = arrayString.split(",");
                            exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
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
                            upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
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
                            autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
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
                            OnlineTestAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
                            SharedPreferences.Editor editor3 = preferences.edit();
                            arrayString = TextUtils.join(",", OnlineTestAnswer);
                            System.out.println("arrayString: " + arrayString);
                            editor3.putString("OnlineTestAnswer", arrayString);
                            editor3.commit();
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

                wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);

//                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                et_answer.setText("");
                break;
            case R.id.civ_camera:
                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                permissionOpenCamera();
                break;
            case R.id.civ_gallery:
                // 打开本地存储
                permissionOpenGallery();
                break;
            case R.id.ll_answer:

                if (contentView == null) {
                    if (url_list.size() == 0) break;
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
                }
                else{
                    System.out.println("我点击了一下");
                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }
                adapter.notifyDataSetChanged();

                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
        }
    }

    private String getHtmlAnswer() {
        return html_answer_head + html_answer + html_answer_tail;
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
                handlerSave.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
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
            }
        }
    };

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
                url_list.add(url);
                adapter.updateData(url_list);// 关键
                html_answer += "<img onclick='bigimage(this)' src='" + url + "'>";
                wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);

//                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
//                transmit.offLoading();
            } else if (message.what == 101) {
                // 复用老代码 触发点击
                ll_answer.performClick();
            }
        }
    };

    // 图片上传
    private void uploadImage() {
//        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE;

        Map<String, String> params = new HashMap<>();
        Log.e("wen0221", "uploadImage: " + imageBase64);
//        Log.e("wen0221", "uploadImage: " + learnPlanId);
//        Log.e("wen0221", "uploadImage: " + username);
        params.put("baseCode", imageBase64);
//        params.put("leanPlanId", learnPlanId);
//        params.put("userId", username);

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
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
                transmit.offLoading();
                e.printStackTrace();
            }
        }, error -> {
            transmit.offLoading();
            Log.d("wen", "Volley_Error: " + error.toString());
        }){
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
                                        AndPermission.with(ShiTiDetailSubjectFragment.this)
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

                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        // 选择图片文件类型
                        intent.setType("image/*");
                        // 跳转到本地存储
                        mResultLauncher.launch(intent);
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

    /**
     * 处理最后从Setting返回后的提示
     *
     * @param requestCode 权限码
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                    // 有对应的权限
                    Toast.makeText(getActivity(), "读写文件权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(getActivity(), "读写文件权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
                    // 有对应的权限
                    Toast.makeText(getActivity(), "拍照权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(getActivity(), "拍照权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void hideInputKB() {
        imm.hideSoftInputFromWindow(et_answer.getWindowToken(), 0);
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
}