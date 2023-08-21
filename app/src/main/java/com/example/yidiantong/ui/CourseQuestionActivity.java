package com.example.yidiantong.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CourseQuestionActivity";
    private String stuname;
    private String stunum;
    private String ip;
    private String imagePath;
    private String questionTypeName;
    private String imgSource;
    // 单选
    int[] unselectDan = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect,R.drawable.e_unselect, R.drawable.f_unselect};
    int[] selectDan = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select,R.drawable.e_select, R.drawable.f_select};
    ClickableImageView[] iv_answer_dan = new ClickableImageView[6];
    int[] answer_dan = {-1, -1, -1, -1, -1,-1};
    int questionId = 0;
    //String[] option = {"A","B","C","D"};
    // 多选
    int[] unselectDuo = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2,R.drawable.e_unselect2, R.drawable.f_unselect2};
    int[] selectDuo = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2,R.drawable.e_select2, R.drawable.f_select2};
    ClickableImageView[] iv_answer_duo = new ClickableImageView[6];
    int[] answer_duo = {0, 0, 0, 0, 0, 0};
    String[] stuAnswerDuo = {"A","B","C","D","E","F"};
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
    private ActivityResultLauncher<Intent> mResultLauncher3;
    private Uri picUri, imageUri;

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
    //html内容数据
    private String html_answer = "";
    //点击大图
    private List<String> url_list = new ArrayList<>();
    private View contentView = null;
    private int picCount = 0;
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
    private View v;
    private int lockmode = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_question);
        // 网页信息
        stuname = getIntent().getStringExtra("stuname");
        stunum = getIntent().getStringExtra("stunum");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        String stu = stuname + "(" + stunum + ")";
        ftv_ls_user = findViewById(R.id.ftv_ls_user);
        ftv_ls_user.setText(stu);
        ip = getIntent().getStringExtra("ip");
        questionTypeName = getIntent().getStringExtra("questionTypeName");
        Log.e("tyoe1",""+questionTypeName);
        questionValueList = getIntent().getStringExtra("questionValueList");
        questionValueList = questionValueList.replace(",", "");
        Log.e("questionValueList", questionValueList);
        fll_cq = findViewById(R.id.fll_cq);

        Log.e("222","");
            // 注册Gallery回调组件
            mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == CourseQuestionActivity.this.RESULT_OK) {
                        Intent intent = result.getData();
                        //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
                        picUri = intent.getData();
                        Log.e("picUri", "" + picUri);
                        if (picUri != null) {
                            /*Gallery回调执行*/
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(CourseQuestionActivity.this.getContentResolver().openInputStream(picUri));
                                imageBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                                imageBase64 = imageBase64.replace("+", "%2b");
                                Log.e("imageBase64", "" + imageBase64);
                                uploadImage();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

            // 注册Camera回调组件
            mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == CourseQuestionActivity.this.RESULT_OK) {
                        Intent intent = new Intent(CourseQuestionActivity.this, DoodleActivity.class);
                        intent.putExtra("uri", imageUri.toString());
                        mResultLauncher3.launch(intent);
                    }
                }
            });

            // 注册 涂鸦板
            mResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == CourseQuestionActivity.this.RESULT_OK) {
                        Intent intent = result.getData();
                        imageBase64 = intent.getStringExtra("data");
                        imageBase64 = imageBase64.replace("+", "%2b");
                        uploadImage();
                    }
                }
            });

//        fll_cq.addView(v);

        // 中间图片
        fll_acq = findViewById(R.id.fll_acq);
        View v1 = LayoutInflater.from(this).inflate(R.layout.fragment_course_question, fll_acq, false);
        fwv_cq = v1.findViewById(R.id.fwv_cq);
        wv_answer = v1.findViewById(R.id.wv_answer);
        imagePath = getIntent().getStringExtra("imagePath");
        imagePath = imagePath.replaceAll("\\\\","/");
        fll_fcq = v1.findViewById(R.id.fll_fcq);
        fll_fcq.setVisibility(View.GONE);
        showWebImage(imagePath);
        fll_acq.addView(v1);

        // 提交按钮
        ftv_cq_commit = findViewById(R.id.ftv_cq_commit);

        changeCommit();

        // 提前创建Adapter
        imgadapter = new ImagePagerAdapter(CourseQuestionActivity.this, url_list);

    }

    private void JudgeType(String action) {
        v = LayoutInflater.from(this).inflate(R.layout.course_btn, fll_cq, false);
        fll_cq_danxuan = v.findViewById(R.id.fll_cq_danxuan);
        fll_cq_duouan = v.findViewById(R.id.fll_cq_duouan);
        fll_cq_panduan = v.findViewById(R.id.fll_cq_panduan);
        fll_cq_tiankong = v.findViewById(R.id.fll_cq_tiankong);
        if (questionTypeName.equals("单项选择题")) {
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

            iv_answer_dan[0] = iv_a;
            iv_answer_dan[1] = iv_b;
            iv_answer_dan[2] = iv_c;
            iv_answer_dan[3] = iv_d;
            iv_answer_dan[4] = iv_e;
            iv_answer_dan[5] = iv_f;

            content = "";
            for (int i = 0; i < 6; i++) {
                if (i < questionValueList.length()) {
                    iv_answer_dan[i].setVisibility(View.VISIBLE);
                    iv_answer_dan[i].setOnClickListener(this);
                } else {
                    iv_answer_dan[i].setVisibility(View.GONE);
                }
            }
        } else if (questionTypeName.equals("多项选择题")) {
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
            iv_answer_duo[0] = iv_a;
            iv_answer_duo[1] = iv_b;
            iv_answer_duo[2] = iv_c;
            iv_answer_duo[3] = iv_d;
            iv_answer_duo[4] = iv_e;
            iv_answer_duo[5] = iv_f;
            content = "";
            for (int i = 0; i < 6; i++) {
                if (i < questionValueList.length()) {
                    iv_answer_duo[i].setVisibility(View.VISIBLE);
                    iv_answer_duo[i].setOnClickListener(this);
                } else {
                    iv_answer_duo[i].setVisibility(View.GONE);
                }
            }
        } else if (questionTypeName.equals("判断题")) {
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
        } else if (questionTypeName.equals("填空题")) {
            fll_cq_danxuan.setVisibility(View.GONE);
            fll_cq_duouan.setVisibility(View.GONE);
            fll_cq_panduan.setVisibility(View.GONE);
            fll_cq_tiankong.setVisibility(View.VISIBLE);
            et_answer = v.findViewById(R.id.et_answer);
            v.findViewById(R.id.tv_save).setOnClickListener(this);
            v.findViewById(R.id.tv_erase).setOnClickListener(this);
            v.findViewById(R.id.civ_camera).setOnClickListener(this);
            v.findViewById(R.id.civ_gallery).setOnClickListener(this);
            content = "";
        }
        if(action.equals("lock")){
            lockmode = 1;
        }else{
            if(lockmode != 1) {
                fll_cq.addView(v);
            }
        }

    }

    private void showWebImage(String imagePath){
        fwv_cq.getSettings().setJavaScriptEnabled(true);
        fwv_cq.setWebChromeClient(new WebChromeClient());
        fwv_cq.loadUrl(imagePath);
    }

    private void uploadImage() {

        String mRequestUrl = ip + Constant.SAVE_BASE64_IMAGE + "?baseCode=" + imageBase64 +
                "&leanPlanId=" + learnPlanId + "&userId=" + stunum;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("url");
                String status = json.getString("status");
                if(status.equals("success")){
                    //封装消息，传递给主线程
                    Message message = Message.obtain();
                    message.obj = url;
                    // 发送消息给主线程
                    //标识线程
                    message.what = 101;
                    handler.sendMessage(message);
                }else{
                    Toast.makeText(this, "保存失败，重新拍照", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            //rl_submitting.setVisibility(View.GONE);
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
                Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();

                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                //permissionOpenCamera();

                break;
            case R.id.civ_gallery:
                Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();
                // 打开本地存储
                //permissionOpenGallery();
                break;
            case R.id.tv_save:
                //Toast.makeText(CourseQuestionActivity.this, "功能完善中，目前不可用", Toast.LENGTH_SHORT).show();
//                fll_fcq.setVisibility(View.VISIBLE);
                //fll_fcq.setOnClickListener(this);
//                html_answer += et_answer.getText().toString();
//                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
//                et_answer.setText("");
                break;
            case R.id.tv_erase:
//                html_answer = "";
//                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
//                url_list.clear();
//                imgadapter.updateData(url_list);
                break;
            case R.id.ftv_cq_commit:
                commit();
                break;
            case R.id.fll_fcq:
                getPicURl();
                if (contentView == null) {
                    if (picCount == 0) break;
                    contentView = LayoutInflater.from(this).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                    //  回显方法
                    contentView.findViewById(R.id.btn_save).setOnClickListener(view->{
                        Log.d(TAG, "onClick: ");
                        html_answer = html_answer.replace(originUrl, identifyUrl);
                        wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                        window.dismiss();
                    });
                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(view->{window.dismiss();});
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

                        @Override
                        public void onLongItemClick(int pos) {
                            Toast.makeText(CourseQuestionActivity.this, "长按", Toast.LENGTH_SHORT).show();
                            if (contentView2 == null) {
                                contentView2 = LayoutInflater.from(CourseQuestionActivity.this).inflate(R.layout.menu_pic_identify, null, false);
                                //绑定点击事件
                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
                                    identifyUrl = picIdentify(url_list.get(pos));
                                    originUrl = url_list.get(pos);
                                    url_list.set(pos, identifyUrl);
                                    imgadapter.notifyDataSetChanged();
                                    ll_selector.setVisibility(View.VISIBLE);

                                    window2.dismiss();
                                });

                                window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                window2.setTouchable(true);
                            }
                            window2.showAtLocation(contentView2, Gravity.CENTER, 0, 0);

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
                LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                ll_selector.setVisibility(View.INVISIBLE);
                imgadapter.notifyDataSetChanged();
                window.showAtLocation(CourseQuestionActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;

        }
    }

    private String picIdentify(String inputUrl) {
        return "https://www.baidu.com/img/PCgkdoodle_293edff43c2957071d2f6bfa606993ac.gif";
    }

    private void getPicURl() {
        url_list.clear();
        Html.fromHtml(html_answer, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                url_list.add(s);
                return null;
            }
        }, null);

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
                if((int) message.obj == 1){
                    Log.e("成功","");
                    Toast.makeText(CourseQuestionActivity.this, "答案提交成功", Toast.LENGTH_SHORT).show();
                }
            }else if(message.what == 101){
                picCount++;
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
                url_list.add(url);
                imgadapter.updateData(url_list);// 关键
                fll_fcq.setVisibility(View.VISIBLE); // 显示右侧框
                html_answer += "<img onclick=\"bigimage(this)\" src=\"" + url + "\" style=\"max-width:80px;\"/>";
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
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
        if(questionTypeName.equals("多项选择题")){
            for (int i = 0; i < questionValueList.length(); ++i) {
                if (answer_duo[i] != 0) {
                    content += stuAnswerDuo[i];
                }
                if(i!=questionValueList.length()-1){
                    content += ",";
                }
            }
            Log.e("content",""+content);
        }

        String stuname_en = Uri.encode(stuname);
        String content_en = Uri.encode(content);
        String learnPlanName_en = Uri.encode(learnPlanName);
        String questionAnswer_en = Uri.encode(questionAnswer);

        String mRequestUrl = ip + Constant.SAVE_STU_ANSWER_FROM_APP +"?userName=" + stunum + "&realName=" + stuname_en + "&learnPlanId=" + learnPlanId +"&learnPlanName=" + learnPlanName_en +
                "&resourceID=" + resourceID + "&interactionType=" + interactionType + "&content=" + content_en + "&questionAnswer=" + questionAnswer_en +
                "&questionScore=" + questionScore +"&answerTime=" +answerTime;
        Log.e("加密",""+mRequestUrl);
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


            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    //单选展示底部按钮
    private void showRadioBtnDan() {
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
                .permission(Permission.Group.CAMERA)
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
        if(action.equals("lock")){
            ftv_cq_commit.setEnabled(false);
            ftv_cq_commit.setTextColor(0xff666666);
        }else if(action.equals("read-lock")){
            ftv_cq_commit.setEnabled(true);
            ftv_cq_commit.setTextColor(0xff57c5e8);
            ftv_cq_commit.setOnClickListener(this);
        }
        JudgeType(action);

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
    public void onBackPressed(){
        Intent intent = new Intent(CourseQuestionActivity.this, CourseLookActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.e("type111",""+getIntent().getStringExtra("questionTypeName"));
        changeCommit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //JudgeType();
        getTypeAndImage();
        Log.e("type",""+getIntent().getStringExtra("questionTypeName"));
    }

    private void getTypeAndImage() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}