package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.HuDongDialog;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.TKeTangListEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.THuDongInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TRemoveControlActivity extends AppCompatActivity implements THuDongInterface, View.OnClickListener {
    private static final String TAG = "TRemoveControlActivity";
    int[] unselectDan = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect, R.drawable.e_unselect, R.drawable.f_unselect, R.drawable.g_unselect, R.drawable.h_unselect};
    int[] selectDan = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select, R.drawable.e_select, R.drawable.f_select, R.drawable.g_select, R.drawable.h_select};
    int[] unselectDuo = {R.drawable.a_unselect2, R.drawable.b_unselect2, R.drawable.c_unselect2, R.drawable.d_unselect2, R.drawable.e_unselect2, R.drawable.f_unselect2, R.drawable.g_unselect2, R.drawable.h_unselect2};
    int[] selectDuo = {R.drawable.a_select2, R.drawable.b_select2, R.drawable.c_select2, R.drawable.d_select2, R.drawable.e_select2, R.drawable.f_select2, R.drawable.g_select2, R.drawable.h_select2};
    private boolean isSelected[] = new boolean[8];
    int[] unselectJudge = {R.drawable.right_unselect, R.drawable.error_unselect};
    int[] selectJudge = {R.drawable.right_select, R.drawable.error_select};

    private ImageView fiv_dianming;
    private ImageView fiv_rc_xueqing;
    private ImageView fiv_rc_baizhao;
    private ImageView fib_rc_tiwen;
    private ImageView fib_rc_suiji;
    private ImageView fib_rc_qiangda;
    private TextView ftv_rc_break;
    private ImageView fiv_rc_skbcd;
    private Button fbtn_bc_cancle;
    private Button fbtn_bc_confirm;
    private ImageView fiv_action;

    private TextView msg;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            this.update();
            handler.postDelayed(this, 200);
        }

        void update() {
            loadItems_Net();
            loadResouceList();
        }
    };
    private ImageView fiv_rc_six;
    private ImageView fiv_ztfx;
    private ImageView fiv_qsfx;
    private ImageView fiv_tcxq;
    private ImageView fiv_rc_one;
    private ImageView fiv_rc_two;
    private ImageView fiv_rc_three;
    private ImageView fiv_rc_four;
    private ImageView fiv_rc_five;
    private ImageView fiv_rc_seven;
    private ImageView fiv_rc_eight;
    private ImageView fiv_rc_nine;
    private ImageView fiv_rc_ten;
    private LinearLayout fll_allanalysis;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow1;
    private PopupWindow popupWindowDetailAnalysis;
    private PopupWindow popupWindowSetAnswer;
    private TextView ftv_rc_text;
    private ImageView fiv_rc_left1;
    private ImageView fiv_rc_left2;
    private ImageView fiv_rc_right1;
    private ImageView fiv_rc_right2;
    private TextView ftv_rc_bottom;
    private String stuname;
    private String mRequestUrl;
    private String mRequestUrl0;
    private int mode = -1;
    private View.OnClickListener listener_ppt;
    private View.OnClickListener listener_vas;
    private String hdmode;
    private String typehd = "";
    private View.OnClickListener listener_hudong;
    private PopupWindow window1;
    private String desc_num;

    private View contentView;
    private MyArrayAdapter myArrayAdapter;
    private List<String> menuList;
    private boolean showMenuFlag = false;
    private ImageView iv_detail_analysis;
    private ImageView iv_answer;
    private ImageView iv_answer2;
    private ImageView iv_close;
    private boolean openRes = false;
    private LinearLayout ll_line1, ll_line2;
    private ClickableImageView iv_line1[] = new ClickableImageView[4];
    private ClickableImageView iv_line2[] = new ClickableImageView[4];
    private ClickableImageView iv_save_answer;
    private ClickableImageView iv_cancel;
    private String answer_content = "";
    private boolean subjectFlag = false;
    private boolean publishFlag = true;
    private boolean showStuFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tremove_control);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        // 下课和列表
        ftv_rc_break = findViewById(R.id.ftv_rc_break);
        fiv_rc_skbcd = findViewById(R.id.fiv_rc_skbcd);
        // 左侧按钮
        fib_rc_tiwen = findViewById(R.id.fib_rc_tiwen);
        fib_rc_suiji = findViewById(R.id.fib_rc_suiji);
        fib_rc_qiangda = findViewById(R.id.fib_rc_qiangda);

        // 右侧文本+按钮
        ftv_rc_text = findViewById(R.id.ftv_rc_text);
        fiv_rc_left1 = findViewById(R.id.fiv_rc_left1);
        fiv_rc_left2 = findViewById(R.id.fiv_rc_left2);
        fiv_rc_right1 = findViewById(R.id.fiv_rc_right1);
        fiv_rc_right2 = findViewById(R.id.fiv_rc_right2);
        ftv_rc_bottom = findViewById(R.id.ftv_rc_bottom);

        // 底部按钮
        fiv_dianming = findViewById(R.id.fiv_rc_jrdm);
        fiv_rc_xueqing = findViewById(R.id.fiv_rc_ckxq);
        fiv_rc_baizhao = findViewById(R.id.fiv_rc_stpz);

        // 遥控器按钮
        fiv_rc_one = findViewById(R.id.fiv_rc_one);
        fiv_rc_two = findViewById(R.id.fiv_rc_two);
        fiv_rc_three = findViewById(R.id.fiv_rc_three);
        fiv_rc_four = findViewById(R.id.fiv_rc_four);
        fiv_rc_five = findViewById(R.id.fiv_rc_five);
        fiv_rc_six = findViewById(R.id.fiv_rc_six);
        fiv_rc_seven = findViewById(R.id.fiv_rc_seven);
        fiv_rc_eight = findViewById(R.id.fiv_rc_eight);
        fiv_rc_nine = findViewById(R.id.fiv_rc_nine);
        fiv_rc_ten = findViewById(R.id.fiv_rc_ten);


        fib_rc_tiwen.setOnClickListener(this);
        fib_rc_suiji.setOnClickListener(this);
        fib_rc_qiangda.setOnClickListener(this);

        fiv_dianming.setOnClickListener(this);
        fiv_rc_xueqing.setOnClickListener(this);
        fiv_rc_baizhao.setOnClickListener(this);

        ftv_rc_break.setOnClickListener(this);
        fiv_rc_skbcd.setOnClickListener(this);

        myArrayAdapter = new MyArrayAdapter(this, new ArrayList<>(Arrays.asList("暂无内容"))); //适配器


        loadItems_Net();
        handler.postDelayed(runnable, 200);

        // -----------------------#
        //  签到功能封装
        //  接口调用+APP自触发
        // -----------------------#
        View inflater = LayoutInflater.from(this).inflate(R.layout.t_dialog_sign, null);
        popupWindow = new PopupWindow(inflater,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        fiv_action = inflater.findViewById(R.id.fiv_action);

        // 二级面板
        inflater = LayoutInflater.from(this).inflate(R.layout.t_dialog_analysis, null);
        popupWindowDetailAnalysis = new PopupWindow(inflater,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);
        iv_detail_analysis = inflater.findViewById(R.id.iv_detail_or_analysis);
        iv_answer = inflater.findViewById(R.id.iv_answer);
        iv_answer2 = inflater.findViewById(R.id.iv_answer2);
        iv_close = inflater.findViewById(R.id.iv_close);


        // 设置答案面板
        inflater = LayoutInflater.from(this).inflate(R.layout.t_dialog_set_answer_pannel, null);
        popupWindowSetAnswer = new PopupWindow(inflater,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, false);

        iv_line1[0] = inflater.findViewById(R.id.iv_line1_1);
        iv_line1[1] = inflater.findViewById(R.id.iv_line1_2);
        iv_line1[2] = inflater.findViewById(R.id.iv_line1_3);
        iv_line1[3] = inflater.findViewById(R.id.iv_line1_4);
        iv_line2[0] = inflater.findViewById(R.id.iv_line2_1);
        iv_line2[1] = inflater.findViewById(R.id.iv_line2_2);
        iv_line2[2] = inflater.findViewById(R.id.iv_line2_3);
        iv_line2[3] = inflater.findViewById(R.id.iv_line2_4);
        iv_save_answer = inflater.findViewById(R.id.iv_save);
        iv_cancel = inflater.findViewById(R.id.iv_cancel);
        ll_line1 = inflater.findViewById(R.id.ll_line1);
        ll_line2 = inflater.findViewById(R.id.ll_line2);
        iv_cancel.setOnClickListener(v -> {
            popupWindowSetAnswer.dismiss();
        });
        iv_save_answer.setOnClickListener(v -> {
            if (answer_content.length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请选择答案")
                        .setPositiveButton("确定", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                setAnswer(answer_content);
                popupWindowSetAnswer.dismiss();
            }
        });


        // 高度自动设置为屏幕宽度
        ConstraintLayout cl_remove_main = findViewById(R.id.cl_remove_main);
        // 获取屏幕尺寸
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // 计算所需的高度（屏幕高度的 0.8 倍）
        int newWidth = (int) (screenWidth * 0.8);

        // 设置ConstraintLayout的新高度
        ViewGroup.LayoutParams layoutParams = cl_remove_main.getLayoutParams();
        layoutParams.height = newWidth;
        cl_remove_main.setLayoutParams(layoutParams);
    }

    private Handler handler1 = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                changePage((List<TKeTangListEntity>) message.obj);
            } else if (message.what == 101) {
                menuList = (List<String>) message.obj;
            }
        }
    };

    private void changePage(List<TKeTangListEntity> messageList) {
        switch (messageList.get(0).getActionType()) {
            case "sign":
                // 二级页面模版
                if (messageList.get(0).getAction().equals("openSign")) {
                    Log.e("0126", "changePage: 可以的");
                    //对话框弹出
                    popupWindow.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);

                    fiv_action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();

                            // 从手机端向电脑端传关闭点名消息
                            doAction(messageList, "closeSign");
                        }
                    });
                } else if (messageList.get(0).getAction().equals("closeSign")) {
                    popupWindow.dismiss();

                }
                break;
            case "allAnalysis":
                View inflater1 = LayoutInflater.from(this).inflate(R.layout.t_dialog_allanalysis, null);
                popupWindow1 = new PopupWindow(inflater1,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, false);
                fll_allanalysis = inflater1.findViewById(R.id.fll_allanalysis);
                if (messageList.get(0).getAction().equals("openAllAnalysis")) {
                    //对话框弹出
                    popupWindow1.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);
//                    WindowManager.LayoutParams lp = getWindow().getAttributes();
//                    lp.alpha = 0.5f; //0.0-1.0
//                    getWindow().setAttributes(lp);
                    fiv_ztfx = inflater1.findViewById(R.id.fiv_ztfx);
                    fiv_qsfx = inflater1.findViewById(R.id.fiv_qsfx);
                    fiv_tcxq = inflater1.findViewById(R.id.fiv_tcxq);
                    fiv_ztfx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 整体学情
                            doAction(messageList, "allQuestionAnalysis");
                        }
                    });
                    fiv_qsfx.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 趋势学情
                            doAction(messageList, "allQuestionAnalysisLine");
                        }
                    });
                    fiv_tcxq.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow1.dismiss();
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 1.0f; //0.0-1.0
                            getWindow().setAttributes(lp);
                            // 从手机端向电脑端传关闭学情消息
                            doAction(messageList, "closeAllAnalysis");
                        }
                    });
                } else if (messageList.get(0).getAction().equals("closeAllAnalysis")) {
                    fiv_ztfx.setVisibility(View.GONE);
                    fiv_qsfx.setVisibility(View.GONE);
                    fiv_tcxq.setVisibility(View.GONE);
                    ;
                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                    lp.alpha = 1.0f; //0.0-1.0
                    getWindow().setAttributes(lp);
                }
                break;

            case "word":
                if (messageList.get(0).getAction().equals("openWord")) {
                    button_black();
                    fiv_rc_four.setImageResource(R.drawable.four_b);
                    fiv_rc_five.setImageResource(R.drawable.five_b);
                    fiv_rc_six.setImageResource(R.drawable.six_b);
                    fiv_rc_seven.setImageResource(R.drawable.seven_b);
                    fiv_rc_nine.setImageResource(R.drawable.nine_c);
                    fiv_rc_ten.setImageResource(R.drawable.ten_b);
                } else {
                    button_white();
                    // 按钮不可点击
                }
                //点击事件
                View.OnClickListener listener_word = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_one:
                                doAction(messageList, "wordresUp");
                                break;
                            case R.id.fiv_rc_two:
                                doAction(messageList, "wordclose");
                                break;
                            case R.id.fiv_rc_three:
                                doAction(messageList, "wordresDown");
                                break;
                            case R.id.fiv_rc_four:
                                doAction(messageList, "wordchangeSizeUp");
                                break;
                            case R.id.fiv_rc_five:
                                doAction(messageList, "wordchangeSizeDown");
                                break;
                            case R.id.fiv_rc_six:
                                doAction(messageList, "wordprePage");
                                break;
                            case R.id.fiv_rc_seven:
                                doAction(messageList, "wordpre");
                                break;
                            case R.id.fiv_rc_nine:
                                doAction(messageList, "wordnext");
                                break;
                            case R.id.fiv_rc_ten:
                                doAction(messageList, "wordnextPage");
                                break;
                        }
                    }
                };
                fiv_rc_one.setOnClickListener(listener_word);
                fiv_rc_two.setOnClickListener(listener_word);
                fiv_rc_three.setOnClickListener(listener_word);
                fiv_rc_four.setOnClickListener(listener_word);
                fiv_rc_five.setOnClickListener(listener_word);
                fiv_rc_six.setOnClickListener(listener_word);
                fiv_rc_seven.setOnClickListener(listener_word);
                fiv_rc_nine.setOnClickListener(listener_word);
                fiv_rc_ten.setOnClickListener(listener_word);
                break;

            case "ppt":
                if (messageList.get(0).getAction().equals("openPPT")) {
                    button_black();
                    fiv_rc_six.setImageResource(R.drawable.six_b);
                    fiv_rc_ten.setImageResource(R.drawable.ten_b);
                    fiv_rc_eight.setImageResource(R.drawable.eight_c);
                    fiv_rc_seven.setImageResource(R.drawable.seven_e);
                    fiv_rc_nine.setImageResource(R.drawable.nine_e);
                    mode = 1;
                } else if (messageList.get(0).getAction().equals("playPPT")) {
                    button_black();
                    fiv_rc_six.setImageResource(R.drawable.six_b);
                    fiv_rc_ten.setImageResource(R.drawable.ten_b);
                    fiv_rc_eight.setImageResource(R.drawable.eight_b);
                    fiv_rc_seven.setImageResource(R.drawable.seven_c);
                    fiv_rc_nine.setImageResource(R.drawable.nine_b);
                    fiv_rc_seven.setOnClickListener(listener_ppt);
                    fiv_rc_nine.setOnClickListener(listener_ppt);
                    mode = 2;
                } else {
                    mode = -1;
                    button_white();
                }

                listener_ppt = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_one:
                                doAction(messageList, "pptresUp");
                                break;
                            case R.id.fiv_rc_two:
                                doAction(messageList, "pptclose");
                                break;
                            case R.id.fiv_rc_three:
                                doAction(messageList, "pptresDown");
                                break;
                            case R.id.fiv_rc_six:
                                doAction(messageList, "pptprePage");
                                break;
                            case R.id.fiv_rc_seven:
                                doAction(messageList, "pptpre");
                                break;
                            case R.id.fiv_rc_eight:
                                Log.e("wen0306", "onClick: " + mode);
                                if (mode == 1) {
                                    doAction(messageList, "pptplay");
                                    // pptplay后自动发出消息 pptplay状态
                                    Message message = new Message();
                                    message.what = 100;
                                    // 创建一个虚拟的TKeTangListEntity对象
                                    TKeTangListEntity tKeTangListEntity = new TKeTangListEntity();
                                    tKeTangListEntity.setActionType("ppt");
                                    tKeTangListEntity.setAction("playPPT");
                                    tKeTangListEntity.setType("0");
                                    tKeTangListEntity.setUserNum("one");
                                    tKeTangListEntity.setSource(getIntent().getStringExtra("teacherId"));
                                    tKeTangListEntity.setTarget("0");
                                    tKeTangListEntity.setMessageType("0");
                                    tKeTangListEntity.setLearnPlanId(getIntent().getStringExtra("learnPlanId"));

                                    // 创建一个包含tKeTangListEntity的List
                                    List<TKeTangListEntity> list = new ArrayList<>();
                                    list.add(tKeTangListEntity);
                                    message.obj = list;
                                    handler1.sendMessage(message);
                                    mode = 2;
                                } else if (mode == 2) {
                                    doAction(messageList, "pptexit");
                                    // pptplay后自动发出消息 pptOpen状态
                                    Message message = new Message();
                                    message.what = 100;
                                    // 创建一个虚拟的TKeTangListEntity对象
                                    TKeTangListEntity tKeTangListEntity = new TKeTangListEntity();
                                    tKeTangListEntity.setActionType("ppt");
                                    tKeTangListEntity.setAction("openPPT");
                                    tKeTangListEntity.setType("0");
                                    tKeTangListEntity.setUserNum("one");
                                    tKeTangListEntity.setSource(getIntent().getStringExtra("teacherId"));
                                    tKeTangListEntity.setTarget("0");
                                    tKeTangListEntity.setMessageType("0");
                                    tKeTangListEntity.setLearnPlanId(getIntent().getStringExtra("learnPlanId"));

                                    // 创建一个包含tKeTangListEntity的List
                                    List<TKeTangListEntity> list = new ArrayList<>();
                                    list.add(tKeTangListEntity);
                                    message.obj = list;
                                    handler1.sendMessage(message);

                                    mode = 1;
                                }
                                break;
                            case R.id.fiv_rc_nine:
                                doAction(messageList, "pptnext");
                                break;
                            case R.id.fiv_rc_ten:
                                doAction(messageList, "pptnextPage");
                                break;

                        }
                    }
                };
                fiv_rc_one.setOnClickListener(listener_ppt);
                fiv_rc_two.setOnClickListener(listener_ppt);
                fiv_rc_three.setOnClickListener(listener_ppt);
                fiv_rc_six.setOnClickListener(listener_ppt);
                fiv_rc_eight.setOnClickListener(listener_ppt);
                fiv_rc_ten.setOnClickListener(listener_ppt);
                break;
            case "resourceWeb":
                if (messageList.get(0).getAction().equals("openResourceWeb")) {
                    button_black();
                } else {
                    button_white();
                }

                View.OnClickListener listener_web = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_one:
                                doAction(messageList, "webresUp");
                                break;
                            case R.id.fiv_rc_two:
                                doAction(messageList, "webclose");
                                break;
                            case R.id.fiv_rc_three:
                                doAction(messageList, "webresDown");
                                break;
                        }
                    }
                };
                fiv_rc_one.setOnClickListener(listener_web);
                fiv_rc_two.setOnClickListener(listener_web);
                fiv_rc_three.setOnClickListener(listener_web);
                break;
            case "videoAndSound":
                if (messageList.get(0).getAction().equals("play")) {
                    button_black();
                    fiv_rc_eight.setImageResource(R.drawable.eight_c);
                    fiv_rc_seven.setImageResource(R.drawable.seven_d);
                    fiv_rc_nine.setImageResource(R.drawable.nine_d);
                    mode = 1;
                } else if (messageList.get(0).getAction().equals("pause")) {
                    button_black();
                    fiv_rc_eight.setImageResource(R.drawable.eight_d);
                    fiv_rc_seven.setImageResource(R.drawable.seven_d);
                    fiv_rc_nine.setImageResource(R.drawable.nine_d);
                    mode = 2;
                } else {
                    mode = -1;
                    button_white();
                }
                View.OnClickListener listener_vas = new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_one:
                                doAction(messageList, "vasresUp");
                                break;
                            case R.id.fiv_rc_two:
                                doAction(messageList, "vasclose");
                                break;
                            case R.id.fiv_rc_three:
                                doAction(messageList, "vasresDown");
                                break;
                            case R.id.fiv_rc_seven:
                                doAction(messageList, "webgo");
                                break;
                            case R.id.fiv_rc_nine:
                                doAction(messageList, "webback");
                                break;
                            case R.id.fiv_rc_eight:
                                if (mode == 1) {
                                    doAction(messageList, "webstart");
                                } else if (mode == 2) {
                                    doAction(messageList, "webstop");
                                }
                        }
                    }
                };
                fiv_rc_one.setOnClickListener(listener_vas);
                fiv_rc_two.setOnClickListener(listener_vas);
                fiv_rc_three.setOnClickListener(listener_vas);
                fiv_rc_seven.setOnClickListener(listener_vas);
                fiv_rc_nine.setOnClickListener(listener_vas);
                fiv_rc_eight.setOnClickListener(listener_vas);
                break;
            case "question":
                if (messageList.get(0).getAction().equals("openQuestion")) {
                    button_black();
                    fiv_rc_four.setImageResource(R.drawable.four_b);
                    fiv_rc_five.setImageResource(R.drawable.five_b);
                    fiv_rc_eight.setImageResource(R.drawable.eight_e);
                    // 关闭提问面板【消息冲突，打开试题和关闭提问相同】
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);
                    ftv_rc_text.setVisibility(View.VISIBLE);
                    openRes = true;
                } else if (messageList.get(0).getAction().equals("showQuestionAnswer")) {
                    button_black();
                    fiv_rc_four.setImageResource(R.drawable.four_b);
                    fiv_rc_five.setImageResource(R.drawable.five_b);
                    fiv_rc_eight.setImageResource(R.drawable.eight_f);
                } else if (messageList.get(0).getAction().equals("closeRes")) {
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);
                    ftv_rc_text.setVisibility(View.VISIBLE);
                    openRes = false;
                } else {
                    button_white();
                }

                View.OnClickListener listener_que = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_one:
                                doAction(messageList, "queresUp");
                                break;
                            case R.id.fiv_rc_two:
                                doAction(messageList, "queclose");
                                break;
                            case R.id.fiv_rc_three:
                                doAction(messageList, "queresDown");
                                break;
                            case R.id.fiv_rc_four:
                                doAction(messageList, "quechangeSizeBig");
                                break;
                            case R.id.fiv_rc_five:
                                doAction(messageList, "quechangeSizeSmall");
                                break;
                            case R.id.fiv_rc_eight:
                                doAction(messageList, "quelookAnswer");
                                break;
                        }
                    }
                };
                fiv_rc_one.setOnClickListener(listener_que);
                fiv_rc_two.setOnClickListener(listener_que);
                fiv_rc_three.setOnClickListener(listener_que);
                fiv_rc_four.setOnClickListener(listener_que);
                fiv_rc_five.setOnClickListener(listener_que);
                fiv_rc_eight.setOnClickListener(listener_que);
                break;
            case "questionAnswer":
                int light[] = {-1, -1, -1, -1};
                listener_hudong = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.fiv_rc_left1:
                                if (typehd.equals("正在作答中")) {
                                    doActionFour("stopAnswer");
                                } else if (typehd.equals("作答结束")) {
                                    doActionFour("answerAgain");
                                } else if (typehd.equals("随机作答") | typehd.equals("随机答案")) {
                                    doActionFour("randomAgain");
                                } else if (typehd.equals("抢答学生") | typehd.equals("抢答答案")) {
                                    doActionFour("responderAgain");
                                }
                                break;
                            case R.id.fiv_rc_left2:
                                if (typehd.equals("正在作答中") | typehd.equals("作答结束")) {
                                    if (subjectFlag) {
                                        doActionFour("danr");
                                    } else {
                                        doActionFour("dtfx");
                                    }
                                } else {
                                    doActionFour("dz");
                                }
                                break;
                            case R.id.fiv_rc_right1:
                                if (typehd.equals("正在作答中") | typehd.equals("作答结束")) {
                                    doActionFour("dtxq");
                                } else {
                                    if(typehd.contains("客观")){
                                        // 设置答案
                                        publishFlag = false;
                                        getAnswerParams();
                                    }else{
                                        if(showStuFlag){
                                            // 状态更新在监听部分
                                            doActionFour("closeStuAnswer");
                                        }else {
                                            // 查看学生作答
                                            doActionFour("openStuAnswer");
                                        }
                                    }
                                }
                                break;
                            case R.id.fiv_rc_right2:
                                doActionFour("closeAnswerWindow");
                                //todo
                                break;
                        }
                    }
                };
                // 一起作答 一点通→教师端
                if (messageList.get(0).getAction().equals("AnswerTogetherStartObjective")) {
                    // 客观题
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen2);
                    // 三按钮互斥
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);

                    fiv_rc_left1.setImageResource(R.drawable.jszd);
                    fiv_rc_left2.setImageResource(R.drawable.dtfx);
                    fiv_rc_right1.setImageResource(R.drawable.dtxq);
                    fiv_rc_right2.setImageResource(R.drawable.closetw);
                    if (messageList.get(0).getDesc() == null || messageList.get(0).getDesc().length() == 0) {
                        messageList.get(0).setDesc("正在作答中");
                    }
                    ftv_rc_bottom.setText(messageList.get(0).getDesc());
                    typehd = "正在作答中";
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                    subjectFlag = false;
                } else if (messageList.get(0).getAction().equals("AnswerTogetherStartSubjective")) {
                    // 主观题 ===================
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen2);
                    // 三按钮互斥
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);

                    fiv_rc_left1.setImageResource(R.drawable.jszd);
                    fiv_rc_left2.setImageResource(R.drawable.danr);
                    fiv_rc_right1.setImageResource(R.drawable.dtxq);
                    fiv_rc_right2.setImageResource(R.drawable.closetw);
                    if (messageList.get(0).getDesc() == null || messageList.get(0).getDesc().length() == 0) {
                        messageList.get(0).setDesc("正在作答中");
                    }
                    ftv_rc_bottom.setText(messageList.get(0).getDesc());
                    typehd = "正在作答中";
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                    subjectFlag = true;

                } else if (messageList.get(0).getAction().equals("AnswerTogetherStopObjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fiv_rc_left1.setImageResource(R.drawable.cxzd);
                    fiv_rc_left2.setImageResource(R.drawable.dtfx);
                    fiv_rc_right1.setImageResource(R.drawable.dtxq);
                    fiv_rc_right2.setImageResource(R.drawable.closetw);
                    ftv_rc_bottom.setText("作答结束");
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    // 三按钮互斥
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);
                    Log.e("wen0321", "changePage: 作答结束");
                    typehd = "作答结束";
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                } else if (messageList.get(0).getAction().equals("AnswerTogetherStopSubjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fiv_rc_left1.setImageResource(R.drawable.cxzd);
                    fiv_rc_left2.setImageResource(R.drawable.danr);
                    fiv_rc_right1.setImageResource(R.drawable.dtxq);
                    fiv_rc_right2.setImageResource(R.drawable.closetw);
                    ftv_rc_bottom.setText("作答结束");
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen2);
                    // 三按钮互斥
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);
                    Log.e("wen0321", "changePage: 作答结束");
                    typehd = "作答结束";
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                } else if (messageList.get(0).getAction().equals("RandomObjective") || messageList.get(0).getAction().equals("RandomSubjective")) {
                    // 随机  一点通→教师端

                    ftv_rc_text.setVisibility(View.GONE);
                    fiv_rc_left1.setImageResource(R.drawable.cxsj_a);
                    fiv_rc_left2.setImageResource(R.drawable.dz_a);
                    if (messageList.get(0).getAction().equals("RandomObjective")) {
                        fiv_rc_right1.setImageResource(R.drawable.szda_a);
                    } else {
                        fiv_rc_right1.setImageResource(R.drawable.xsda_0);
                    }
                    fiv_rc_right2.setImageResource(R.drawable.closesj_0);
                    fib_rc_suiji.setImageResource(R.drawable.suiji2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);

                    ftv_rc_bottom.setText("正在随机...");
                    for (int i = 0; i < 4; i++) {
                        light[i] = 0;
                    }
                    button_light(light);
                } else if (messageList.get(0).getAction().equals("RandomPeopleObjective") || messageList.get(0).getAction().equals("RandomPeopleSubjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_suiji.setImageResource(R.drawable.suiji2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);

                    fiv_rc_left1.setImageResource(R.drawable.cxsj_b);
                    fiv_rc_left2.setImageResource(R.drawable.dz_a);
                    if (messageList.get(0).getAction().equals("RandomPeopleObjective")) {
                        fiv_rc_right1.setImageResource(R.drawable.szda_a);
                    } else {
                        fiv_rc_right1.setImageResource(R.drawable.xsda_0);
                    }
                    fiv_rc_right2.setImageResource(R.drawable.closesj);
                    stuname = messageList.get(0).getDesc();
                    ftv_rc_bottom.setText(stuname);
                    light[0] = 1;
                    light[1] = 0;
                    light[2] = 0;
                    light[3] = 1;
                    button_light(light);
                    typehd = "随机作答";
                } else if (messageList.get(0).getAction().equals("RandomAnswerObjective") || messageList.get(0).getAction().equals("RandomAnswerSubjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_suiji.setImageResource(R.drawable.suiji2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda1);

                    fiv_rc_left1.setImageResource(R.drawable.cxsj_b);
                    fiv_rc_left2.setImageResource(R.drawable.dz_b);
                    if (messageList.get(0).getAction().equals("RandomAnswerObjective")) {
                        fiv_rc_right1.setImageResource(R.drawable.szda_b);
                        typehd = "随机答案客观";
                    } else {
                        fiv_rc_right1.setImageResource(R.drawable.xsda);
                        // 状态设置，学生答案未显示状态
                        showStuFlag = false;
                        typehd = "随机答案主观";
                    }
                    fiv_rc_right2.setImageResource(R.drawable.closesj);
                    ftv_rc_bottom.setText(messageList.get(0).getDesc());
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                }else if(messageList.get(0).getAction().equals("RandomAnswerSubjectiveShowStuAnswer")){
                    // 状态设置，学生答案显示状态
                    showStuFlag = true;
                    fiv_rc_right1.setImageResource(R.drawable.gbda2);
                }

                // 抢答  一点通→教师端
                else if (messageList.get(0).getAction().equals("ResponderReadyObjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);

                    fiv_rc_left1.setImageResource(R.drawable.cxqd_a);
                    fiv_rc_left2.setImageResource(R.drawable.dz_a);
                    fiv_rc_right1.setImageResource(R.drawable.szda_a);
                    fiv_rc_right2.setImageResource(R.drawable.closeqd_0);
                    ftv_rc_bottom.setText("正在抢答");
                    for (int i = 0; i < 4; i++) {
                        light[i] = 0;
                    }
                    button_light(light);
                } else if (messageList.get(0).getAction().equals("ResponderObjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);

                    fiv_rc_left1.setImageResource(R.drawable.cxqd_a);
                    fiv_rc_left2.setImageResource(R.drawable.dz_a);
                    fiv_rc_right1.setImageResource(R.drawable.szda_a);
                    fiv_rc_right2.setImageResource(R.drawable.closeqd);
                    ftv_rc_bottom.setText("正在抢答");
                    light[0] = 0;
                    light[1] = 0;
                    light[2] = 0;
                    light[3] = 1;
                    button_light(light);
                    typehd = "正在抢答";
                } else if (messageList.get(0).getAction().equals("ResponderPeopleObjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);

                    fiv_rc_left1.setImageResource(R.drawable.cxqd_b);
                    fiv_rc_left2.setImageResource(R.drawable.dz_a);
                    fiv_rc_right1.setImageResource(R.drawable.szda_a);
                    fiv_rc_right2.setImageResource(R.drawable.closeqd);
                    stuname = messageList.get(0).getDesc();
                    ftv_rc_bottom.setText(stuname);
                    light[0] = 1;
                    light[1] = 0;
                    light[2] = 0;
                    light[3] = 1;
                    button_light(light);
                    typehd = "抢答学生";
                } else if (messageList.get(0).getAction().equals("ResponderAnswerObjective")) {
                    ftv_rc_text.setVisibility(View.GONE);
                    fib_rc_qiangda.setImageResource(R.drawable.qiangda2);
                    // 三按钮互斥
                    fib_rc_tiwen.setImageResource(R.drawable.tiwen1);
                    fib_rc_suiji.setImageResource(R.drawable.suiji1);

                    fiv_rc_left1.setImageResource(R.drawable.cxqd_b);
                    fiv_rc_left2.setImageResource(R.drawable.dz_b);
                    fiv_rc_right1.setImageResource(R.drawable.szda_b);
                    fiv_rc_right2.setImageResource(R.drawable.closeqd);
                    ftv_rc_bottom.setText("回显学生答案" + messageList.get(0).getDesc());
                    for (int i = 0; i < 4; i++) {
                        light[i] = 1;
                    }
                    button_light(light);
                    typehd = "抢答答案";
                }
                break;
            //下课
            case "toScan":
                if (messageList.get(0).getAction().equals("toScan")) {
                    this.finish();
                }
                break;
            case "questionAnalysis":
                if (messageList.get(0).getAction().equals("dtxqObjective")) {
                    // 【答题详情】切换二级页面
                    popupWindowDetailAnalysis.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);
                    iv_detail_analysis.setImageResource(R.drawable.dtfx);
                    // 点击事件
                    iv_detail_analysis.setOnClickListener(ve -> {
                        doActionFour("dtfx");
                        iv_detail_analysis.setImageResource(R.drawable.dtxq);
                    });
                    iv_answer.setOnClickListener(ve -> {
//                        questionAnalysis("publicAnswer");
                        publishFlag = true;
                        getAnswerParams();

                    });
                    iv_answer2.setVisibility(View.GONE);
                    iv_close.setOnClickListener(ve -> {
                        popupWindowDetailAnalysis.dismiss();
                        questionAnalysis("closeQuestionAnalysis");
                    });
                } else if (messageList.get(0).getAction().equals("dtxqSubjective") || messageList.get(0).getAction().equals("danrSubjective")) {
                    // 【答题详情】切换二级页面
                    popupWindowDetailAnalysis.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);
                    iv_detail_analysis.setImageResource(R.drawable.danr);
                    // 点击事件
                    iv_detail_analysis.setOnClickListener(ve -> {
                        doActionFour("danr");
                    });
                    iv_answer.setImageResource(R.drawable.dtxq);
                    iv_answer.setOnClickListener(ve -> {
                        doActionFour("dtxq");
                    });
                    if (messageList.get(0).getResId().contains("PRQUI")) {
                        iv_answer2.setVisibility(View.VISIBLE);
                        iv_answer2.setImageResource(R.drawable.gbda);
                        iv_answer2.setOnClickListener(v -> {
                            Log.e("wen0402", "changePage: 授课包直接公布答案" );
                            questionAnalysis("publicAnswer");
                        });
                    } else {
                        iv_answer2.setVisibility(View.GONE);
                    }
                    iv_close.setOnClickListener(ve -> {
                        popupWindowDetailAnalysis.dismiss();
                        questionAnalysis("closeQuestionAnalysis");
                    });

                } else if (messageList.get(0).getAction().equals("dtfxObjective") || messageList.get(0).getAction().equals("dtfx")) {
                    // 【单题分析】切换二级页面
                    popupWindowDetailAnalysis.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);
                    iv_detail_analysis.setImageResource(R.drawable.dtxq);
                    // 点击事件
                    iv_detail_analysis.setOnClickListener(ve -> {
                        doActionFour("dtxq");
                        iv_detail_analysis.setImageResource(R.drawable.dtfx);

                    });
                    iv_answer.setImageResource(R.drawable.gbda);
                    iv_answer.setOnClickListener(ve -> {
//                        questionAnalysis("publicAnswer");
                        publishFlag = true;
                        getAnswerParams();

                    });
                    iv_answer2.setVisibility(View.GONE);
                    iv_close.setOnClickListener(ve -> {
                        popupWindowDetailAnalysis.dismiss();
                        questionAnalysis("closeQuestionAnalysis");

                    });
                } else if (messageList.get(0).getAction().equals("closeQuestionAnalysis")) {
                    // 关闭二级页面
                    popupWindowDetailAnalysis.dismiss();
                } else if (messageList.get(0).getAction().equals("dtxqObjectiveShowAnswer") || messageList.get(0).getAction().equals("dtxqSubjectiveShowAnswer")) {
                    // 【答题详情】【公布答案后】切换二级页面
                    popupWindowDetailAnalysis.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);
                    if(messageList.get(0).getAction().equals("dtxqSubjectiveShowAnswer")){
                        iv_detail_analysis.setImageResource(R.drawable.danr);
                        // 点击事件
                        iv_detail_analysis.setOnClickListener(ve -> {
                            doActionFour("danr");
                        });
                    }else{
                        iv_detail_analysis.setImageResource(R.drawable.dtfx);
                        // 点击事件
                        iv_detail_analysis.setOnClickListener(ve -> {
                            doActionFour("dtfx");
                        });
                    }

                    iv_answer.setImageResource(R.drawable.right_students);
                    iv_answer2.setVisibility(View.VISIBLE);
                    iv_answer2.setImageResource(R.drawable.all_students);


                    iv_answer.setOnClickListener(ve -> {
                        questionAnalysis("ddxs");
                    });
                    iv_answer2.setOnClickListener(ve -> {
                        questionAnalysis("qbxs");
                    });
                    iv_close.setOnClickListener(ve -> {
                        popupWindowDetailAnalysis.dismiss();
                        questionAnalysis("closeQuestionAnalysis");
                    });
                }
                Log.e("wen0337", "changePage: " + messageList.get(0));
                break;
        }

    }

    // 右上四个按钮是否可被点击
    private void button_light(int light[]) {
        ImageView iv[] = {fiv_rc_left1, fiv_rc_left2, fiv_rc_right1, fiv_rc_right2};
        for (int i = 0; i < 4; i++) {
            if (light[i] == 1) {
                iv[i].setEnabled(true);
                iv[i].setOnClickListener(listener_hudong);
            } else {
                iv[i].setEnabled(false);
            }
        }
    }

    // 遥控器 教师端→一点通
    private void doAction(List<TKeTangListEntity> messageList, String action) {

        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");

        String userNum = messageList.get(0).getUserNum();
        String resId = messageList.get(0).getResId();
        String resPath = messageList.get(0).getResPath();
        String learnPlanId = messageList.get(0).getLearnPlanId();
        if (learnPlanId == null || learnPlanId.length() == 0) {
            learnPlanId = getIntent().getStringExtra("learnPlanId");
        }
        String resRootPath = messageList.get(0).getResRootPath();
        String desc = messageList.get(0).getDesc();

        String mRequestUrl_origin = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" + "&userNum=" + userNum +
                "&source=" + teacherId + "&target=0" + "&messageType=0" + "action" + "&resId=" + resId + "&resPath=" + resPath + "&learnPlanId=" +
                learnPlanId + "&resRootPath=" + resRootPath + "&desc=" + desc;
        // 关闭点名
        if (action.equals("closeSign")) {
            mRequestUrl0 = "&action=do:goBackFromSign" + "&actionType=sign";

        } else if (action.equals("closeAllAnalysis")) {
            // 关闭学情
            mRequestUrl0 = "&action=do:goBackFromAllAnalysis" + "&actionType=allAnalysis";

        } else if (action.equals("allQuestionAnalysis")) {
            // 整体分析
            mRequestUrl0 = "&action=allQuestionAnalysis" + "&actionType=allAnalysis";

        } else if (action.equals("allQuestionAnalysisLine")) {
            // 趋势分析
            mRequestUrl0 = "&action=allQuestionAnalysisLine" + "&actionType=allAnalysis";
        } else if (action.equals("webresUp")) {
            // web上一资源
            mRequestUrl0 = "&action=resUp" + "&actionType=resourceWeb";
        } else if (action.equals("webclose")) {
            // web关闭资源
            mRequestUrl0 = "&action=close" + "&actionType=resourceWeb";
        } else if (action.equals("webresDown")) {
            // web下一资源
            mRequestUrl0 = "&action=resDown" + "&actionType=resourceWeb";
        } else if (action.equals("wordresUp")) {
            // word上一资源
            mRequestUrl0 = "&action=resUp" + "&actionType=word";
        } else if (action.equals("wordclose")) {
            // word关闭资源
            mRequestUrl0 = "&action=close" + "&actionType=word";
        } else if (action.equals("wordresDown")) {
            // word下一资源
            mRequestUrl0 = "&action=resDown" + "&actionType=word";
        } else if (action.equals("wordpre")) {
            // word光标上移
            mRequestUrl0 = "&action=pre" + "&actionType=word";
        } else if (action.equals("wordnext")) {
            // word光标下移
            mRequestUrl0 = "&action=next" + "&actionType=word";
        } else if (action.equals("wordprePage")) {
            // word上一页
            mRequestUrl0 = "&action=prePage" + "&actionType=word";
        } else if (action.equals("wordnextPage")) {
            // word下一页
            mRequestUrl0 = "&action=nextPage" + "&actionType=word";
        } else if (action.equals("wordchangeSizeUp")) {
            // word放大
            mRequestUrl0 = "&action=changeSizeUp" + "&actionType=word";
        } else if (action.equals("wordchangeSizeDown")) {
            // word缩小
            mRequestUrl0 = "&action=changeSizeDown" + "&actionType=word";
        } else if (action.equals("pptresUp")) {
            // ppt上一资源
            mRequestUrl0 = "&action=resUp" + "&actionType=ppt";
        } else if (action.equals("pptclose")) {
            // ppt关闭资源
            mRequestUrl0 = "&action=close" + "&actionType=ppt";
        } else if (action.equals("pptresDown")) {
            // ppt下一资源
            mRequestUrl0 = "&action=resDown" + "&actionType=ppt";
        } else if (action.equals("pptprePage")) {
            // ppt上一页
            mRequestUrl0 = "&action=prePage" + "&actionType=ppt";
        } else if (action.equals("pptnextPage")) {
            // ppt下一页
            mRequestUrl0 = "&action=nextPage" + "&actionType=ppt";
        } else if (action.equals("pptpre")) {
            // ppt上一动画
            mRequestUrl0 = "&action=pre" + "&actionType=ppt";
        } else if (action.equals("pptnext")) {
            // ppt上一动画
            mRequestUrl0 = "&action=next" + "&actionType=ppt";
        } else if (action.equals("pptplay")) {
            // ppt预览
            mRequestUrl0 = "&action=play" + "&actionType=ppt";
        } else if (action.equals("pptexit")) {
            // ppt关闭预览
            mRequestUrl0 = "&action=exit" + "&actionType=ppt";
        } else if (action.equals("vasresUp")) {
            // vas上一资源
            mRequestUrl0 = "&action=resUp" + "&actionType=videoAndSound";
        } else if (action.equals("vasclose")) {
            // vas关闭资源
            mRequestUrl0 = "&action=close" + "&actionType=videoAndSound";
        } else if (action.equals("vasresDown")) {
            // vas下一资源
            mRequestUrl0 = "&action=resDown" + "&actionType=videoAndSound";
        } else if (action.equals("vasstart")) {
            // vas开始
            mRequestUrl0 = "&action=start" + "&actionType=videoAndSound";
        } else if (action.equals("vasstop")) {
            // vas暂停
            mRequestUrl0 = "&action=stop" + "&actionType=videoAndSound";
        } else if (action.equals("vasgo")) {
            // vas快进
            mRequestUrl0 = "&action=go" + "&actionType=videoAndSound";
        } else if (action.equals("vasback")) {
            // vas后退
            mRequestUrl0 = "&action=back" + "&actionType=videoAndSound";
        } else if (action.equals("queresUp")) {
            // question上一资源
            mRequestUrl0 = "&action=resUp" + "&actionType=question";
        } else if (action.equals("queclose")) {
            // question关闭资源
            mRequestUrl0 = "&action=close" + "&actionType=question";
        } else if (action.equals("queresDown")) {
            // question下一资源
            mRequestUrl0 = "&action=resDown" + "&actionType=question";
        } else if (action.equals("quechangeSizeBig")) {
            // question放大
            mRequestUrl0 = "&action=changeSizeBig" + "&actionType=question";
        } else if (action.equals("quechangeSizeSmall")) {
            // question缩小
            mRequestUrl0 = "&action=changeSizeSmall" + "&actionType=question";
        } else if (action.equals("quelookAnswer")) {
            // question显示答案
            mRequestUrl0 = "&action=lookAnswer" + "&actionType=question";
        }
        mRequestUrl = mRequestUrl_origin.replace("action", mRequestUrl0);
        mRequestUrl = mRequestUrl.replace("&", "&messageBean.");
        mRequestUrl = mRequestUrl.replace("?", "?messageBean.");
        Log.e("0129", "关键的请求" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            Log.e("0129", "doAction: " + action);

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 互动开始答题按钮  教师端→一点通
    @Override
    public void doActionHuDong(int answer, int size) {
        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        String hou;
        if (answer == 0 | answer == 1) {
            hou = String.valueOf(size);
        } else {
            hou = "0";
        }
        answer = answer + 1;
        desc_num = answer + "_" + hou;
        Log.e("wen0306", "doActionHuDong: " + desc_num);
        String mRequestUrl_origin = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                "&source=" + teacherId + "&target=0" + "&messageType=0" + "actioncontent" + "&actionType=questionAnswer" + "&desc=" + desc_num;

        if (hdmode.equals("answerTogether")) {
            // 一起作答
            mRequestUrl0 = "&action=answerTogether";
        } else if (hdmode.equals("answerRandom")) {
            // 随机
            mRequestUrl0 = "&action=answerRandom";
        } else if (hdmode.equals("answerResponder")) {
            // 抢答
            mRequestUrl0 = "&action=answerResponder";
        }

        mRequestUrl = mRequestUrl_origin.replace("actioncontent", mRequestUrl0);
        mRequestUrl = mRequestUrl.replace("&", "&messageBean.");
        mRequestUrl = mRequestUrl.replace("?", "?messageBean.");
        Log.e("mRequestUrl111", "" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);

    }

    // 右上四个按钮  教师端→一点通
    private void doActionFour(String action) {
        Log.e("wen0402", "doActionFour请求: " + action);

        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        String mRequestUrl_origin = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                "&source=" + teacherId + "&target=0" + "&messageType=0" + "actioncontext" + "&actionType=questionAnswer" + "&desc=" + desc_num;
        if (action.equals("stopAnswer")) {
            // 结束作答
            mRequestUrl0 = "&action=stopAnswer";
        } else if (action.equals("answerAgain")) {
            // 重新作答
            mRequestUrl0 = "&action=answerAgain";
        } else if (action.equals("dtfx")) {
            // 单题分析
            mRequestUrl0 = "&action=dtfx";
        } else if (action.equals("dtxq")) {
            // 答题详情
            Log.e("wen0401", "doActionFour: 答题详情");
            mRequestUrl0 = "&action=dtxq";
        } else if (action.equals("danr")) {
            mRequestUrl0 = "&action=danr";
        } else if (action.equals("closeAnswerWindow")) {
            // 关闭互动窗口
            mRequestUrl0 = "&action=closeAnswerWindow";
        } else if (action.equals("randomAgain")) {
            // 重新选人
            mRequestUrl0 = "&action=randomAgain";
        } else if (action.equals("dz")) {
            // 点赞
            mRequestUrl0 = "&action=dz";
        } else if (action.equals("responderAgain")) {
            // 重新抢答
            mRequestUrl0 = "&action=responderAgain";
        } else if(action.equals("openStuAnswer")){
            // 学生作答
            mRequestUrl0 = "&action=openStuAnswer";
        }else if(action.equals("closeStuAnswer")){
            // 关闭作答
            mRequestUrl0 = "&action=closeStuAnswer";
        }

        mRequestUrl = mRequestUrl_origin.replace("actioncontext", mRequestUrl0);
        mRequestUrl = mRequestUrl.replace("&", "&messageBean.");
        mRequestUrl = mRequestUrl.replace("?", "?messageBean.");
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 试题分析二级页面请求
    private void questionAnalysis(String action) {
        Log.e("wen0402", "questionAnalysis: 请求" + action);
        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        mRequestUrl = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                "&source=" + teacherId + "&target=0" + "&messageType=0&actionType=questionAnalysis" +
                "&action=" + action +
                "&desc=";

        mRequestUrl = mRequestUrl.replace("&", "&messageBean.");
        mRequestUrl = mRequestUrl.replace("?", "?messageBean.");
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 获取设置答案的参数
    private void getAnswerParams() {
        String ip = getIntent().getStringExtra("ip");
        mRequestUrl = "http://" + ip + ":8901" + Constant.T_REMOVE_GET_ANSWER_PARAMS;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                showAnswerPannel(json.getInt("qType"), json.getInt("qNum"), json.getString("qAnswer"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 展示设置答案的面板
    private void showAnswerPannel(int type, int num, String answer) {
        if (answer.equals("haveAnswer")) {
            Log.e("wen0402", "showAnswerPannel: 已有答案，直接公布");
            questionAnalysis("publicAnswer");
            return;
        }
        answer_content = "";
        int line1_num = (num + 1) / 2;
        int line2_num = num - line1_num;
        switch (type) {
            case 1:
                // 判断
                View.OnClickListener panduan = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answer_content = (String) view.getTag();
                        if (answer_content.equals("T")) {
                            iv_line1[0].setImageResource(selectJudge[0]);
                            iv_line1[1].setImageResource(unselectJudge[0]);
                        } else {
                            iv_line1[0].setImageResource(unselectJudge[0]);
                            iv_line1[1].setImageResource(selectJudge[1]);
                        }
                    }
                };

                ll_line2.setVisibility(View.GONE);
                iv_line1[0].setVisibility(View.VISIBLE);
                iv_line1[0].setImageResource(unselectJudge[0]);
                iv_line1[0].setTag("T");
                iv_line1[0].setOnClickListener(panduan);
                iv_line1[1].setVisibility(View.VISIBLE);
                iv_line1[1].setImageResource(unselectJudge[1]);
                iv_line1[1].setTag("R");
                iv_line1[1].setOnClickListener(panduan);


                for (int i = 2; i < 4; ++i) {
                    iv_line1[i].setVisibility(View.GONE);
                }

                break;
            case 2:
                // 单选
                View.OnClickListener danxuan = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 获取点击的信息
                        answer_content = String.valueOf(view.getTag());
                        // 绘制点击后的面板
                        for (int i = 0; i < 4; ++i) {
                            if (iv_line1[i].getTag() != null) {

                                if (iv_line1[i].getTag().equals(view.getTag())) {
                                    iv_line1[i].setImageResource(selectDan[(char) iv_line1[i].getTag() - 'A']);
                                } else {
                                    iv_line1[i].setImageResource(unselectDan[(char) iv_line1[i].getTag() - 'A']);
                                }
                            }

                            if (iv_line2[i].getTag() != null) {
                                if (iv_line2[i].getTag().equals(view.getTag())) {
                                    iv_line2[i].setImageResource(selectDan[(char) iv_line2[i].getTag() - 'A']);
                                } else {
                                    iv_line2[i].setImageResource(unselectDan[(char) iv_line2[i].getTag() - 'A']);
                                }
                            }
                        }
                    }

                };
                if (num <= 3) {
                    ll_line2.setVisibility(View.GONE);
                    for (int i = 0; i < 4; ++i) {
                        if (i < num) {
                            iv_line1[i].setVisibility(View.VISIBLE);
                            iv_line1[i].setVisibility(unselectDan[i]);
                            iv_line1[i].setTag((char) (i + 'A'));
                            iv_line1[i].setOnClickListener(danxuan);
                        } else {
                            iv_line1[i].setVisibility(View.GONE);
                        }
                    }
                } else {
                    ll_line2.setVisibility(View.VISIBLE);

                    Log.e(TAG, "showAnswerPannel: " + line1_num + "  " + line2_num);
                    for (int i = 0; i < 4; ++i) {
                        if (i < line1_num) {
                            iv_line1[i].setVisibility(View.VISIBLE);
                            iv_line1[i].setImageResource(unselectDan[i]);
                            iv_line1[i].setTag((char) (i + 'A'));
                            iv_line1[i].setOnClickListener(danxuan);
                        } else {
                            iv_line1[i].setVisibility(View.GONE);
                        }
                    }
                    for (int i = 0; i < 4; ++i) {
                        if (i < line2_num) {
                            iv_line2[i].setVisibility(View.VISIBLE);
                            iv_line2[i].setImageResource(unselectDan[i + line1_num]);
                            iv_line2[i].setTag((char) (i + line1_num + 'A'));
                            iv_line2[i].setOnClickListener(danxuan);
                        } else {
                            iv_line2[i].setVisibility(View.GONE);
                        }
                    }
                }
                break;
            case 3:
                // 多选
                View.OnClickListener duoxuan = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int idx = (char) view.getTag() - 'A';
                        if (isSelected[idx]) {
                            // 切换选中状态
                            ((ClickableImageView) view).setImageResource(unselectDuo[idx]);
                            isSelected[idx] = false;
                        } else {
                            ((ClickableImageView) view).setImageResource(selectDuo[idx]);
                            isSelected[idx] = true;
                        }
                        List<String> sb = new ArrayList<>();
                        for (int i = 0; i < 8; ++i) {
                            if (isSelected[i]) {
                                sb.add(String.valueOf((char) (i + 'A')));
                            }
                        }
                        answer_content = String.join("", sb);
                    }
                };

                if (num <= 3) {
                    ll_line2.setVisibility(View.GONE);
                    for (int i = 0; i < 4; ++i) {
                        if (i < num) {
                            iv_line1[i].setVisibility(View.VISIBLE);
                            iv_line1[i].setVisibility(unselectDuo[i]);
                            iv_line1[i].setTag((char) (i + 'A'));
                            iv_line1[i].setOnClickListener(duoxuan);
                        } else {
                            iv_line1[i].setVisibility(View.GONE);
                        }
                    }
                } else {
                    ll_line2.setVisibility(View.VISIBLE);
                    line1_num = (num + 1) / 2;
                    line2_num = num - line1_num;
                    for (int i = 0; i < 4; ++i) {
                        if (i < line1_num) {
                            iv_line1[i].setVisibility(View.VISIBLE);
                            iv_line1[i].setImageResource(unselectDuo[i]);
                            iv_line1[i].setTag((char) (i + 'A'));
                            iv_line1[i].setOnClickListener(duoxuan);
                        } else {
                            iv_line1[i].setVisibility(View.GONE);
                        }
                    }
                    for (int i = 0; i < 4; ++i) {
                        if (i < line2_num) {
                            iv_line2[i].setVisibility(View.VISIBLE);
                            iv_line2[i].setImageResource(unselectDuo[i + line1_num]);
                            iv_line2[i].setTag((char) (i + line1_num + 'A'));
                            iv_line2[i].setOnClickListener(duoxuan);
                        } else {
                            iv_line2[i].setVisibility(View.GONE);
                        }
                    }
                }
                break;

        }
        popupWindowSetAnswer.showAtLocation(fiv_rc_six, Gravity.CENTER, 0, 0);

        Log.e(TAG, "showAnswerPannel获取到的参数: type=" + type + ", num=" + num + ",answer=" + answer);

    }

    // 设置答案
    private void setAnswer(String answer) {
        String ip = getIntent().getStringExtra("ip");
        mRequestUrl = "http://" + ip + ":8901" + Constant.T_REMOVE_SET_ANSWER + "?answer=" + answer;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                if (publishFlag) {
                    questionAnalysis("publicAnswer");
                    Log.e("wen0402", "setAnswer: 设置后直接公布");
                }else{
                    Log.e("wen0402", "setAnswer: 仅设置，不公布");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 恢复非点击状态
    private void button_white() {
        fiv_rc_one.setImageResource(R.drawable.one_a);
        fiv_rc_two.setImageResource(R.drawable.two_a);
        fiv_rc_three.setImageResource(R.drawable.three_a);
        fiv_rc_four.setImageResource(R.drawable.four_a);
        fiv_rc_five.setImageResource(R.drawable.five_a);
        fiv_rc_six.setImageResource(R.drawable.six_a);
        fiv_rc_seven.setImageResource(R.drawable.seven_a);
        fiv_rc_eight.setImageResource(R.drawable.eight_a);
        fiv_rc_nine.setImageResource(R.drawable.nine_a);
        fiv_rc_ten.setImageResource(R.drawable.ten_a);
        fiv_rc_one.setEnabled(false);
        fiv_rc_two.setEnabled(false);
        fiv_rc_three.setEnabled(false);
        fiv_rc_four.setEnabled(false);
        fiv_rc_five.setEnabled(false);
        fiv_rc_six.setEnabled(false);
        fiv_rc_seven.setEnabled(false);
        fiv_rc_eight.setEnabled(false);
        fiv_rc_nine.setEnabled(false);
        fiv_rc_ten.setEnabled(false);
    }

    // 可点击状态
    private void button_black() {
        fiv_rc_one.setImageResource(R.drawable.one_b);
        fiv_rc_two.setImageResource(R.drawable.two_b);
        fiv_rc_three.setImageResource(R.drawable.three_b);
        fiv_rc_one.setEnabled(true);
        fiv_rc_two.setEnabled(true);
        fiv_rc_three.setEnabled(true);
        fiv_rc_four.setEnabled(true);
        fiv_rc_five.setEnabled(true);
        fiv_rc_six.setEnabled(true);
        fiv_rc_seven.setEnabled(true);
        fiv_rc_eight.setEnabled(true);
        fiv_rc_nine.setEnabled(true);
        fiv_rc_ten.setEnabled(true);
    }

    // 获得授课一点通消息列表信息
    private void loadItems_Net() {

        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_GET_MESSAGE_LIST_BY_TEA + "?userId=" + teacherId;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONArray item = json.getJSONArray("messageList");
                String itemList = item.get(item.length() - 1).toString();
                Log.e("wen0402", "itemList: " + itemList);
                itemList = "[" + itemList + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<TKeTangListEntity> messageList = gson.fromJson(itemList, new TypeToken<List<TKeTangListEntity>>() {
                }.getType());

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = messageList;

                //标识线程
                message.what = 100;
                if (messageList.size() != 0) {
                    Log.e("wen0402", "loadItems_Net: " + messageList.get(0));
                    handler1.sendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });

        MyApplication.addRequest(request, TAG);
    }

    private void loadResouceList() {
        String ip = getIntent().getStringExtra("ip");
        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_REMOVE_GET_RESOUCE_LIST;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String resourceString = json.getString("list");

                Gson gson = new Gson();

                //使用Goson框架转换Json字符串为列表
                List<String> messageList = gson.fromJson(resourceString, new TypeToken<List<String>>() {
                }.getType());

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = messageList;

                //标识线程
                message.what = 101;
                handler1.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        MyApplication.addRequest(request, TAG);

    }


    private void showMenu() {
        if (contentView == null) {
            contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

            ListView lv_homework = contentView.findViewById(R.id.lv_homework);
            lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 180);
            window1 = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
            window1.setOutsideTouchable(true);
            lv_homework.setAdapter(myArrayAdapter);

            lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    showResouce(i);
                    showMenuFlag = false;
                    window1.dismiss();
                }
            });
        }
        if (menuList.size() == 0) {
            myArrayAdapter.update(new ArrayList<>(Arrays.asList("暂无内容")));
        } else {
            myArrayAdapter.update(menuList);
        }
        window1.showAsDropDown(fiv_rc_skbcd, 0, 10);

    }

    private void showResouce(int id) {
        String ip = getIntent().getStringExtra("ip");

        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE
                + "?messageBean.type=0"
                + "&messageBean.userType=teacher"
                + "&messageBean.userNum=one"
                + "&messageBean.source=" + getIntent().getStringExtra("teacherId")
                + "&messageBean.target=0"
                + "&messageBean.messageType=0"
                + "&messageBean.action=openRes"
                + "&messageBean.actionType=openRes"
                + "&messageBean.resId="
                + "&messageBean.resPath="
                + "&messageBean.learnPlanId="
                + "&messageBean.resRootPath="
                + "&messageBean.desc=" + id;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            Log.e("0130", "response: " + response);
        }, error -> {
            Log.e("0130", "error: " + error);
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fib_rc_tiwen:
                hdmode = "answerTogether";
                hudongdialog();
                break;
            case R.id.fib_rc_suiji:
                hdmode = "answerRandom";
                hudongdialog();
                break;
            case R.id.fib_rc_qiangda:
                hdmode = "answerResponder";
                hudongdialog();
                break;
            // 点名
            case R.id.fiv_rc_jrdm:
                Log.e("0126", "onClick: 点名");
                doActionClick("openSign");
                break;
            // 学情
            case R.id.fiv_rc_ckxq:
                doActionClick("openAllAnalysis");
                break;
            // 拍照
            case R.id.fiv_rc_stpz:
                permissionOpenCamera();
                break;
            // 下课
            case R.id.ftv_rc_break:
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View inflater = LayoutInflater.from(this).inflate(R.layout.t_dialog_breakclass, null);
                builder.setView(inflater);
                AlertDialog dialog = builder.create();
                //禁止返回和外部点击
                dialog.setCancelable(false);
                //对话框弹出
                dialog.show();
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.gravity = Gravity.CENTER;
                    lp.dimAmount = 0.0f;
                    lp.alpha = 1.0f;
                    window.setAttributes(lp);
                }

                fbtn_bc_cancle = inflater.findViewById(R.id.fbtn_bc_cancle);
                fbtn_bc_confirm = inflater.findViewById(R.id.fbtn_bc_confirm);
                fbtn_bc_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                fbtn_bc_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        doActionClick("toScan");
                    }
                });

                break;
            case R.id.fiv_rc_skbcd:
                if (showMenuFlag) {
                    window1.dismiss();
                    showMenuFlag = false;
                } else {
                    showMenu();
                    showMenuFlag = true;
                }
                break;
        }

    }

    // 教师端到一点通点击按钮点名、学情、下课
    private void doActionClick(String action) {
        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        if (action.equals("openSign")) {
            mRequestUrl0 = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                    "&source=" + teacherId + "&target=0" + "&messageType=0" + "&action=openSign" + "&actionType=sign";
        } else if (action.equals("openAllAnalysis")) {
            mRequestUrl0 = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                    "&source=" + teacherId + "&target=0" + "&messageType=0" + "&action=openAllAnalysis" + "&actionType=allAnalysis";
        } else if (action.equals("toScan")) {
            mRequestUrl0 = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE + "?type=0" + "&userType=teacher" +
                    "&source=" + teacherId + "&target=0" + "&messageType=0" + "&action=toScan" + "&actionType=toScan";
        }
        mRequestUrl = mRequestUrl0.replace("&", "&messageBean.");
        mRequestUrl = mRequestUrl.replace("?", "?messageBean.");
        StringRequest request = new StringRequest(mRequestUrl, response -> {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
            // 返回值是null

            if (action.equals("openSign")) {
                // 模拟监听"授课一点通"点名消息
                TKeTangListEntity entity = new TKeTangListEntity();
                entity.setAction("openSign");
                entity.setActionType("sign");
                entity.setUserNum("one");
                List<TKeTangListEntity> messageList = new ArrayList<>(Arrays.asList(entity));
                changePage(messageList);
            }

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        Log.e("0126", "请求URL：" + mRequestUrl);

        MyApplication.addRequest(request, TAG);
    }

    // 左栏
    private void hudongdialog() {
        if (openRes) {
            doActionHuDong(0, 0);
            return;
        }

        HuDongDialog builder = new HuDongDialog(this);
        Window window = builder.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.dimAmount = 0.0f;
            lp.alpha = 1.0f;
            window.setAttributes(lp);
        }
        builder.show();
    }

    // 拍照分享
    private void permissionOpenCamera() {
        Intent intent = new Intent(this, TCameraShareActivity.class);
        intent.putExtra("learnPlanId", getIntent().getStringExtra("learnPlanId"));
        intent.putExtra("userId", getIntent().getStringExtra("teacherId"));
        intent.putExtra("ip", getIntent().getStringExtra("ip"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新

        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
        if (popupWindow1 != null && popupWindow1.isShowing()) {
            popupWindow1.dismiss();
        }
        super.onDestroy();
    }
}