package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.CourseLookEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LogUtils;
import com.example.yidiantong.util.PxUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CourseQuestionActivity";
    private String stuname;
    private String stunum;
    private String ip;
    private String imagePath;
    private String questionTypeName;
    private String questionType;
    private String imgSource;
    // 单选
    int[] unselectDan = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect, R.drawable.h_unselect};
    int[] selectDan = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select, R.drawable.h_select};
    ClickableImageView[] iv_answer_dan = new ClickableImageView[8];
    int[] answer_dan = {-1, -1, -1, -1, -1, -1, -1, -1};
    int questionId = 0;
    //String[] option = {"A","B","C","D"};
    // 多选
    int[] unselectDuo = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2, R.drawable.e_unselect2, R.drawable.f_unselect2, R.drawable.g_unselect2, R.drawable.h_unselect2};
    int[] selectDuo = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2, R.drawable.e_select2, R.drawable.f_select2, R.drawable.g_select2, R.drawable.h_select2};
    ClickableImageView[] iv_answer_duo = new ClickableImageView[8];
    int[] answer_duo = {0, 0, 0, 0, 0, 0, 0, 0};
    String[] stuAnswerDuo = {"A", "B", "C", "D", "E", "F", "G", "H"};
    // 判断
    int[] unselectJudge = {R.drawable.right_unselect, R.drawable.error_unselect};
    int[] selectJudge = {R.drawable.right_select, R.drawable.error_select};
    ClickableImageView[] iv_answer_jud = new ClickableImageView[2];
    int answer_jud = -1;
    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;

    private Uri picUri, imageUri, cropUri;

    private ImagePagerAdapter imgadapter;
    private LinearLayout fll_cq;
    private LinearLayout fll_cq_danxuan;
    private LinearLayout fll_cq_duouan;
    private LinearLayout fll_cq_panduan;
    private LinearLayout fll_cq_tiankong;
    private String questionValueList;
    private EditText et_answer;
    private TextView ftv_ls_user;
    private TextView ftv_cq_commit;
    private List<CourseLookEntity.queList> listQue;
    private String content = "";
    private LinearLayout fll_fcq;
    private LinearLayout ll_answer;
    private WebView wv_answer;
    //答题区域HTML头
    private String html_answer_head = "<head>\n" +
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
    //html尾
    private String html_answer_tail = "</body>";
    //html内容数据
    private String html_answer = "";
    //点击大图
    private List<String> url_list = new ArrayList<>();
    private View contentView = null;
    // 识别
    private View contentView2;
    private PopupWindow window2;
    private String identifyUrl;
    private String originUrl;
    private PopupWindow window;
    private String learnPlanId;
    //图片编码
    private String imageBase64;
    private String action;
    private LinearLayout fvp_cq;
    private WebView fwv_cq;
    private LinearLayout fll_acq;
    private int viewJudge = -1;
    private int lockmode = -1;

    private BroadcastReceiver finishReceiver;

    private ImageView fiv_ls_ma; // 二维码控件
    private Bitmap imgBitmap = null; // 二维码url解析图片对象

    // 软键盘弹出NEW
    private boolean isKeyboardVisible = false;
    private InputMethodManager imm;
    private RelativeLayout lr_bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_question);
        Log.e("wen0228", "onNewIntent: 新建");
        // 提前创建Adapter
        imgadapter = new ImagePagerAdapter(CourseQuestionActivity.this, url_list);

        // 网页信息
        stuname = getIntent().getStringExtra("stuname");
        stunum = getIntent().getStringExtra("stunum");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        String stu = stuname + "(" + stunum + ")";
        ftv_ls_user = findViewById(R.id.ftv_ls_user);
        ftv_ls_user.setText(stu);
        ip = getIntent().getStringExtra("ip");

        questionTypeName = getIntent().getStringExtra("questionTypeName");
        questionType = getIntent().getStringExtra("questionType");
        Log.e("tyoe1", "" + questionTypeName);
        questionValueList = getIntent().getStringExtra("questionValueList");
        questionValueList = questionValueList.replace(",", "");
        fll_cq = findViewById(R.id.fll_cq);

        // 注册Gallery回调组件
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
                    Uri uri = intent.getData();
                    if (uri != null) {
                        /**
                         * 这里做了统一化操作：创建一个output.jpg文件，并将uri写入新文件，并将picUri赋给新文件（与拍照逻辑相似）
                         * 一是为了简化方法；
                         * 二是因为适配问题，有些手机应用不能返回数据，只能与拍照类似的调用方式才行；
                         * 三是因为获取本地图片只能返回uri，而不像拍照那样可以选择写入，因此需要手动。
                         */
                        File Image = new File(getExternalCacheDir(), "output_image.jpg");
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
                            picUri = FileProvider.getUriForFile(CourseQuestionActivity.this,
                                    "com.example.yidiantong.fileprovider", Image);
                        } else {
                            picUri = Uri.fromFile(Image);
                        }

                        // uri写入文件Image
                        FileOutputStream outputStream = null;
                        FileInputStream inputStream = null;
                        try {
                            outputStream = new FileOutputStream(Image);
                            inputStream = (FileInputStream) getContentResolver().openInputStream(uri);

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

        // 注册Camera回调组件
        mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == CourseQuestionActivity.this.RESULT_OK) {
                    //                    Intent intent = new Intent(getActivity(), DoodleActivity.class);
//                    intent.putExtra("uri", imageUri.toString());
//                    mResultLauncher3.launch(intent);
                    Crop(imageUri); // 裁剪图片
                }
            }
        });

        /**
         * 注册通用裁切回调：与通用裁切方法对应。
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == CourseQuestionActivity.RESULT_OK) {
                    File Image = new File(getExternalCacheDir(), "output_temp.jpg");

                    imageBase64 = ImageUtils.Bitmap2StrByBase64(CourseQuestionActivity.this, Image);
                    uploadImage();
                }
            }
        });

        // 中间图片
        fll_acq = findViewById(R.id.fll_acq);
        View v1 = LayoutInflater.from(this).inflate(R.layout.fragment_course_question, fll_acq, false);
        fwv_cq = v1.findViewById(R.id.fwv_cq);
        wv_answer = v1.findViewById(R.id.wv_answer);
        imagePath = getIntent().getStringExtra("imagePath");
        imagePath = imagePath.replaceAll("\\\\", "/");
        fll_fcq = v1.findViewById(R.id.fll_fcq);
        fll_fcq.setVisibility(View.GONE);
        showWebImage(imagePath);
        fll_acq.addView(v1);
        fll_fcq.setOnClickListener(this);

        lr_bottom = findViewById(R.id.lr_bottom);

        // 提交按钮
        ftv_cq_commit = findViewById(R.id.ftv_cq_commit);

        changeCommit();


        // 注册广播接收器
        finishReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish(); // 结束子Activity
            }
        };
        IntentFilter filter = new IntentFilter("finish_activities");
        registerReceiver(finishReceiver, filter);

        // ----------------//
        //  二维码弹窗
        // ----------------//

        fiv_ls_ma = findViewById(R.id.fiv_ls_ma);
        fiv_ls_ma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadItems_Net_QC();
            }
        });

        // -------------------//
        //  作答面展示，点击放大
        // -------------------//
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
                        message.what = 102;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");

    }

    private void JudgeType(String action) {
        fll_cq.removeAllViews();
        fll_fcq.setVisibility(View.GONE);
        wv_answer.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        View v = LayoutInflater.from(this).inflate(R.layout.course_btn, fll_cq, false);
        fll_cq_danxuan = v.findViewById(R.id.fll_cq_danxuan);
        fll_cq_duouan = v.findViewById(R.id.fll_cq_duouan);
        fll_cq_panduan = v.findViewById(R.id.fll_cq_panduan);
        fll_cq_tiankong = v.findViewById(R.id.fll_cq_tiankong);
        Log.e("0202ee", "JudgeTypeName: " + questionTypeName);
        Log.e("0202ee", "JudgeType: " + questionType);
        if (questionType.equals("1")) { // 单选题
            fll_cq_danxuan.setVisibility(View.VISIBLE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //单选显示答案选项
            ClickableImageView iv_a = v.findViewById(R.id.fiv_cb_a);
            ClickableImageView iv_b = v.findViewById(R.id.fiv_cb_b);
            ClickableImageView iv_c = v.findViewById(R.id.fiv_cb_c);
            ClickableImageView iv_d = v.findViewById(R.id.fiv_cb_d);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_e);
            ClickableImageView iv_f = v.findViewById(R.id.fiv_cb_f);
            ClickableImageView iv_g = v.findViewById(R.id.fiv_cb_g);
            ClickableImageView iv_h = v.findViewById(R.id.fiv_cb_h);

            iv_answer_dan[0] = iv_a;
            iv_answer_dan[1] = iv_b;
            iv_answer_dan[2] = iv_c;
            iv_answer_dan[3] = iv_d;
            iv_answer_dan[4] = iv_e;
            iv_answer_dan[5] = iv_f;
            iv_answer_dan[6] = iv_g;
            iv_answer_dan[7] = iv_h;

            content = "";
            for (int i = 0; i < 8; i++) {
                if (i < questionValueList.length()) {
                    iv_answer_dan[i].setVisibility(View.VISIBLE);
                    iv_answer_dan[i].setOnClickListener(this);
                } else {
                    iv_answer_dan[i].setVisibility(View.GONE);
                }
            }
        } else if (questionType.equals("2")) { // 多选题
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.VISIBLE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //多选显示答案选项
            ClickableImageView iv_a = v.findViewById(R.id.fiv_cb_a2);
            ClickableImageView iv_b = v.findViewById(R.id.fiv_cb_b2);
            ClickableImageView iv_c = v.findViewById(R.id.fiv_cb_c2);
            ClickableImageView iv_d = v.findViewById(R.id.fiv_cb_d2);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_e2);
            ClickableImageView iv_f = v.findViewById(R.id.fiv_cb_f2);
            ClickableImageView iv_g = v.findViewById(R.id.fiv_cb_g2);
            ClickableImageView iv_h = v.findViewById(R.id.fiv_cb_h2);
            iv_answer_duo[0] = iv_a;
            iv_answer_duo[1] = iv_b;
            iv_answer_duo[2] = iv_c;
            iv_answer_duo[3] = iv_d;
            iv_answer_duo[4] = iv_e;
            iv_answer_duo[5] = iv_f;
            iv_answer_duo[6] = iv_g;
            iv_answer_duo[7] = iv_h;
            content = "";
            Log.e("0115", "JudgeType: " + questionValueList);
            for (int i = 0; i < 8; i++) {
                if (i < questionValueList.length()) {
                    iv_answer_duo[i].setVisibility(View.VISIBLE);
                    iv_answer_duo[i].setOnClickListener(this);
                } else {
                    iv_answer_duo[i].setVisibility(View.GONE);
                }
                answer_duo[i] = 0; // 初始化多选答案
            }
        } else if (questionType.equals("4")) { // 判断题
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.VISIBLE);
            fll_cq_tiankong.setVisibility(View.GONE);
            //显示答案选项
            ClickableImageView iv_r = v.findViewById(R.id.fiv_cb_r);
            ClickableImageView iv_e = v.findViewById(R.id.fiv_cb_er);
            iv_answer_jud[0] = iv_r;
            iv_answer_jud[1] = iv_e;
            content = "";
            iv_r.setOnClickListener(this);
            iv_e.setOnClickListener(this);
        } else {
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.VISIBLE);
            et_answer = v.findViewById(R.id.et_answer);
            v.findViewById(R.id.tv_save).setOnClickListener(this);
            v.findViewById(R.id.tv_erase).setOnClickListener(this);
            v.findViewById(R.id.civ_camera).setOnClickListener(this);
            v.findViewById(R.id.civ_gallery).setOnClickListener(this);

            // 添加软键盘状态监听NEW
            et_answer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    Rect r = new Rect();
                    et_answer.getWindowVisibleDisplayFrame(r);

                    int screenHeight = et_answer.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) { // 15% of the screen
                        if (!isKeyboardVisible) {
                            // Move the EditText up
                            lr_bottom.setTranslationY(-keypadHeight);

                            isKeyboardVisible = true;
                        }
                    } else {
                        if (isKeyboardVisible) {

                            // Reset the EditText position
                            lr_bottom.setTranslationY(0);
                            isKeyboardVisible = false;
                        }
                    }
                }
            });

            // -------------------#
            //  视图初始化，清空内容
            // -------------------#
            fll_fcq.setVisibility(View.GONE);
            et_answer.setText("");
            html_answer = "";
            wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
            url_list.clear();
            imgadapter.updateData(url_list);

            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            // 隐藏输入框
            fwv_cq.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideInputKB();
                    return false;
                }
            });
            html_answer = "";
            content = "";
        }
//        else{
//            Log.e("0202ee", "未知题型名称: " + questionTypeName);
//            Log.e("0202ee", "未知题型编号: " + questionType);
//        }
        if (action.equals("lock")) {
            lockmode = 1;
        } else if (lockmode != 1) {
            fll_cq.addView(v);

        }
    }

    private void hideInputKB() {
        imm.hideSoftInputFromWindow(et_answer.getWindowToken(), 0);
    }


    private void showWebImage(String imagePath) {
//        fwv_cq.getSettings().setJavaScriptEnabled(true);
//        fwv_cq.setWebChromeClient(new WebChromeClient());
        fwv_cq.loadUrl(imagePath);

        // -------------------------------------------------#
        //  修复WebView大小变化BUG
        //  【思路】设置LinearLayout高度为定值，则WebView大小固定
        // -------------------------------------------------#
        fll_acq.post(new Runnable() {
            @Override
            public void run() {
                int actualHeight = fll_acq.getHeight();
                Log.e("0112", "showWebImage: " + actualHeight);
                // 创建新的 LayoutParams 对象，并设置高度
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, actualHeight);
                // 应用新的 LayoutParams
                fll_acq.setLayoutParams(layoutParams);
            }
        });

    }

    private void uploadImage() {
        String mRequestUrl = ip + Constant.SAVE_BASE64_IMAGE;

        Map<String, String> params = new HashMap<>();
        params.put("baseCode", imageBase64);
        params.put("learnPlanId", learnPlanId);
        params.put("userId", stunum);

        Log.e("debug0116", "base64编码长度: " + imageBase64.length());

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String url = json.getString("url");
                String status = json.getString("status");
                if (status.equals("success")) {
                    // 封装消息，传递给主线程
                    Message message = Message.obtain();
                    message.obj = url;

                    // 发送消息给主线程
                    // 标识线程
                    message.what = 101;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (JSONException e) {
                Toast.makeText(this, "图片上传失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }, error -> {
            Log.e("debug0116", "volley: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fiv_cb_a:
                answer_dan[questionId] = 0;
                content = "A";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_b:
                answer_dan[questionId] = 1;
                content = "B";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_c:
                answer_dan[questionId] = 2;
                content = "C";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_d:
                answer_dan[questionId] = 3;
                content = "D";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_e:
                answer_dan[questionId] = 4;
                content = "E";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_f:
                answer_dan[questionId] = 5;
                content = "F";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_g:
                answer_dan[questionId] = 6;
                content = "G";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_h:
                answer_dan[questionId] = 7;
                content = "H";
                showRadioBtnDan();
                break;
            case R.id.fiv_cb_a2:
                answer_duo[0] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_b2:
                answer_duo[1] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_c2:
                answer_duo[2] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_d2:
                answer_duo[3] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_e2:
                answer_duo[4] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_f2:
                answer_duo[5] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_g2:
                answer_duo[6] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_h2:
                answer_duo[7] ^= 1;
                showRadioBtnDuo();
                break;
            case R.id.fiv_cb_r:
                answer_jud = 0;
                content = "对";
                showRadioBtnJud();
                break;
            case R.id.fiv_cb_er:
                answer_jud = 1;
                showRadioBtnJud();
                content = "错";
                break;
            case R.id.civ_camera:
//                Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();

                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                permissionOpenCamera();

                break;
            case R.id.civ_gallery:
//                Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();
                // 打开本地存储
                permissionOpenGallery();
                break;
            case R.id.tv_save:
//                Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();
                fll_fcq.setVisibility(View.VISIBLE);
                html_answer += et_answer.getText().toString();
                wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
                et_answer.setText("");
                content = html_answer;
                break;
            case R.id.tv_erase:
                html_answer = "";
                wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
                url_list.clear();
                imgadapter.updateData(url_list);
                break;
            case R.id.ftv_cq_commit:
                commit();
                break;
            case R.id.fll_fcq:
                if (contentView == null) {
                    if (url_list.size() == 0) break;
                    contentView = LayoutInflater.from(this).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
//                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
//                    //  回显方法
//                    contentView.findViewById(R.id.btn_save).setOnClickListener(view -> {
//                        Log.d(TAG, "onClick: ");
//                        html_answer = html_answer.replace(originUrl, identifyUrl);
//                        wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
//
//                        window.dismiss();
//                    });
//                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
//                        window.dismiss();
//                    });
                    vp_pic.setAdapter(imgadapter);

                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                    imgadapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick() {
                            vp_pic.setCurrentItem(0);
                            window.dismiss();
                        }

//                        @Override
//                        public void onLongItemClick(int pos) {
//                            Toast.makeText(CourseQuestionActivity.this, "长按", Toast.LENGTH_SHORT).show();
//                            if (contentView2 == null) {
//                                contentView2 = LayoutInflater.from(CourseQuestionActivity.this).inflate(R.layout.menu_pic_identify, null, false);
//                                //绑定点击事件
//                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
//                                    identifyUrl = picIdentify(url_list.get(pos));
//                                    originUrl = url_list.get(pos);
//                                    url_list.set(pos, identifyUrl);
//                                    imgadapter.notifyDataSetChanged();
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
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }
                imgadapter.notifyDataSetChanged();

                window.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;

        }
    }

    private String getHtmlAnswer() {
        return html_answer_head + html_answer + html_answer_tail;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                if ((int) message.obj == 1) {
                    Toast.makeText(CourseQuestionActivity.this, "答案提交成功", Toast.LENGTH_SHORT).show();
                }
            } else if (message.what == 101) {
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
                url_list.add(url);
                imgadapter.updateData(url_list);// 关键
                fll_fcq.setVisibility(View.VISIBLE); // 显示右侧框
                html_answer += "<img onclick=\"bigimage(this)\" src=\"" + url + "\" style=\"max-width:80px\">";
                wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
                content = html_answer;
                Toast.makeText(CourseQuestionActivity.this, "图片上传成功", Toast.LENGTH_SHORT).show();
            } else if (message.what == 102) {
                // 复用老代码 触发点击
                fll_fcq.performClick();
            }
        }
    };

    // 提交答案到服务器
    private void commit() {
        String resourceID = getIntent().getStringExtra("resourceID");
        String questionScore = getIntent().getStringExtra("questionScore");
        String interactionType = getIntent().getStringExtra("interactionType");
        String questionAnswer = getIntent().getStringExtra("questionAnswer");
        String learnPlanName = getIntent().getStringExtra("learnPlanName");
        String answerTime = getIntent().getStringExtra("answerTime");
        if (questionTypeName.equals("多项选择题")) {
            content = "";
            for (int i = 0; i < questionValueList.length(); ++i) {
                if (answer_duo[i] != 0) {
                    content += stuAnswerDuo[i];
                }
                if (i != questionValueList.length() - 1) {
                    content += ",";
                }
            }
        }
        if(content.length() == 0){
            Toast.makeText(this, "作答内容为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("debug0115", "userName: " + stunum);
        Log.e("debug0115", "realName: " + stuname);
        Log.e("debug0115", "learnPlanId: " + learnPlanId);
        Log.e("debug0115", "resourceID: " + resourceID);
        Log.e("debug0115", "interactionType: " + interactionType);
        Log.e("debug0115", "content: " + content);
        content = content.replace("onclick=\"bigimage(this)\" ", ""); // 格式清洗
        Log.e("debug0115", "questionScore: " + questionScore);
        Log.e("debug0115", "answerTime: " + answerTime);
        try {
            Log.e("debug0115", "提交内容: " + content);
            String stuname_en = URLEncoder.encode(URLEncoder.encode(stuname, "UTF-8"), "UTF-8");
            String content_en = URLEncoder.encode(URLEncoder.encode(content, "UTF-8"), "UTF-8");
            String learnPlanName_en = URLEncoder.encode(URLEncoder.encode(learnPlanName, "UTF-8"), "UTF-8");
            String questionAnswer_en;
            if (questionAnswer == null) {
                questionAnswer_en = "";
            } else {
                questionAnswer_en = URLEncoder.encode(URLEncoder.encode(questionAnswer, "UTF-8"), "UTF-8");
            }
            String mRequestUrl = "";
            if (interactionType.equals("3") || interactionType.equals("5")) {
                mRequestUrl = ip + Constant.SAVE_STU_SUB_ANSWER_FROM_APP;
            } else {
                mRequestUrl = ip + Constant.SAVE_STU_ANSWER_FROM_APP;
            }

            mRequestUrl += "?userName=" + stunum + "&realName=" + stuname_en + "&learnPlanId=" + learnPlanId + "&learnPlanName=" + learnPlanName_en +
                    "&resourceID=" + resourceID + "&interactionType=" + interactionType + "&content=" + content_en + "&questionAnswer=" + questionAnswer_en +
                    "&questionScore=" + questionScore + "&answerTime=" + answerTime;

            StringRequest request = new StringRequest(mRequestUrl, response -> {
                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);

                    //结果信息
                    String status = json.getString("status");
                    Message msg = Message.obtain();
                    if (status.equals("success")) {
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
                Log.e("volley", "Volley_Error: " + error.toString());

            });
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //单选展示底部按钮
    private void showRadioBtnDan() {
        Log.e("wen0304", "全部: " + questionValueList.length());
        Log.e("wen0304", "选中: " + answer_dan[questionId]);
        for (int i = 0; i < questionValueList.length(); ++i) {
            if (answer_dan[questionId] != i) {
                iv_answer_dan[i].setImageResource(unselectDan[i]);
            } else {
                iv_answer_dan[i].setImageResource(selectDan[i]);
            }
        }
    }

    //多选展示底部按钮
    private void showRadioBtnDuo() {
        for (int i = 0; i < questionValueList.length(); ++i) {
            if (answer_duo[i] == 0) {
                iv_answer_duo[i].setImageResource(unselectDuo[i]);
            } else {
                iv_answer_duo[i].setImageResource(selectDuo[i]);
            }
        }
    }

    //判断展示底部按钮
    private void showRadioBtnJud() {
//        //同步答案给Activity
//        transmit.setStuAnswer(stuAnswerEntity.getOrder(), answer == 1 ? "对" : "错");
        for (int i = 0; i < 2; ++i) {
            if (answer_jud != i) {
                iv_answer_jud[i].setImageResource(unselectJudge[i]);
            } else {
                iv_answer_jud[i].setImageResource(selectJudge[i]);
            }
        }
    }

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。拍照申请
     */
    private Rationale rCamera = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(CourseQuestionActivity.this)
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
                        // 判断是否点了永远拒绝，不再提示
                        if (AndPermission.hasAlwaysDeniedPermission(CourseQuestionActivity.this, data)) {
                            new AlertDialog.Builder(CourseQuestionActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(CourseQuestionActivity.this)
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
        File outputImage = new File(this.getExternalCacheDir(), "output_image.jpg");
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
            imageUri = FileProvider.getUriForFile(this,
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
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(CourseQuestionActivity.this)
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
                        // 判断是否点了永远拒绝，不再提示
                        if (AndPermission.hasAlwaysDeniedPermission(CourseQuestionActivity.this, data)) {
                            new AlertDialog.Builder(CourseQuestionActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(CourseQuestionActivity.this)
                                                .runtime()
                                                .setting()
                                                .start(REQUEST_CODE_STORAGE);
                                    })
                                    .setNegativeButton("取消", (dialog, which) -> {

                                    })
                                    .show();
                        }
                    }
                })
                .rationale(rGallery)
                .start();
    }


    private void changeCommit() {
        action = getIntent().getStringExtra("action");
        if (action.equals("lock")) {
            ftv_cq_commit.setEnabled(false);
            ftv_cq_commit.setTextColor(0xff666666);
        } else if (action.equals("read-lock")) {
            ftv_cq_commit.setEnabled(true);
            ftv_cq_commit.setTextColor(0xff57c5e8);
            ftv_cq_commit.setOnClickListener(this);
            questionValueList = getIntent().getStringExtra("questionValueList");
            questionValueList = questionValueList.replace(",", "");
            JudgeType(action);
            showWebImage(imagePath);
        }

        Log.d("wenpeng", "onCreate: 创建");
    }

    /**
     * 处理最后从Setting返回后的提示
     *
     * @param requestCode 权限码
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                    // 有对应的权限
                    Toast.makeText(CourseQuestionActivity.this, "读写文件权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(CourseQuestionActivity.this, "读写文件权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
                    // 有对应的权限
                    Toast.makeText(CourseQuestionActivity.this, "拍照权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(CourseQuestionActivity.this, "拍照权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // 按返回键，不传回上一界面
    public void onBackPressed() {
//        Intent intent = new Intent(CourseQuestionActivity.this, CourseLookActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("wen0306", "onNewIntent: 复用");
        super.onNewIntent(intent);

        setIntent(intent);
        // 页面切换
        questionTypeName = intent.getStringExtra("questionTypeName");
        questionType = intent.getStringExtra("questionType");
        imagePath = intent.getStringExtra("imagePath");
        imagePath = imagePath.replaceAll("\\\\", "/");
        lockmode = -1;
        changeCommit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //JudgeType();
        getTypeAndImage();
    }

    private void getTypeAndImage() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消广播接收器的注册
        unregisterReceiver(finishReceiver);
        Log.e("wen0306", "onDestroy: 销毁");

    }

    /**
     * 通用裁切方法。传输、读取文件、裁切、写入文件,最终以cropUri形式显示NEW
     *
     * @param uri 裁切前的图片Uri（pic：相册；image：照片）
     */
    private void Crop(Uri uri) {

        File Image = new File(getExternalCacheDir(), "output_temp.jpg");
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
            cropUri = FileProvider.getUriForFile(CourseQuestionActivity.this,
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
        ClipData clipData = ClipData.newUri(getContentResolver(), "A photo", cropUri);
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

    // ---------------------//
    //  二维码弹窗 方法
    //  源于LockActivity页面中
    // ---------------------//

    private Handler handler_qc = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                requestWebPhotoBitmap((String) message.obj);
            } else if (message.what == 101) {
                if ((int) message.obj == 1) {
                    Log.e("成功", "");
                }
            }
        }
    };

    // 二维码图片
    private void requestWebPhotoBitmap(String imageurl) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL bitmapUrl = new URL(imageurl);
                connection = (HttpURLConnection) bitmapUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                // 判断是否请求成功
                if (connection.getResponseCode() == 200) {
                    Message hintMessage = new Message();
                    hintMessage.what = 100;
                    hintHandler.sendMessage(hintMessage);

                    InputStream inputStream = connection.getInputStream();
                    imgBitmap = BitmapFactory.decodeStream(inputStream);

                    Message message = showHandler.obtainMessage();
                    showHandler.sendMessage(message);
                } else {
                    Message hintMessage = new Message();
                    hintMessage.what = 101;
                    hintHandler.sendMessage(hintMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private final Handler hintHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 100)
                Log.e(TAG, "handleMessage: 获取图片中，请稍等");
//                Toast.makeText(CourseLockActivity.this, "获取图片中，请稍等", Toast.LENGTH_SHORT).show();
            else if (msg.what == 101)
                Toast.makeText(CourseQuestionActivity.this, "网络错误，请重试", Toast.LENGTH_SHORT).show();
        }
    };

    private ImageView fiv_erweima;
    @SuppressLint("HandlerLeak")
    private final Handler showHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 弹窗
            View inflater = LayoutInflater.from(CourseQuestionActivity.this).inflate(R.layout.course_stu_erweima, null);
            PopupWindow popupWindow = new PopupWindow(inflater,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, false);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f; //0.0-1.0
            getWindow().setAttributes(lp);
            fiv_erweima = inflater.findViewById(R.id.fiv_erweima);
            fiv_erweima.setImageBitmap(imgBitmap); //填充控件
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(fll_acq, Gravity.CENTER, 0, 0);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f; //0.0-1.0
                    getWindow().setAttributes(lp);
                }
            });
        }
    };

    private void loadItems_Net_QC() {
        String mRequestUrl = ip + Constant.GET_QRCODE_URL;
        Log.e("mReq_stu", "" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String imageurl = json.getString("url");
                Log.e("imageurl", "" + imageurl);

                Message message = Message.obtain();
                message.obj = imageurl;

                //标识线程
                message.what = 100;
                handler_qc.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }
}