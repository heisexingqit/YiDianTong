package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.LearnPlanAddItemEntity;
import com.example.yidiantong.fragment.TPackagePickAddFragment;
import com.example.yidiantong.fragment.TPackagePickChangeFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.TLearnPlanAddInterface;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTeachEditPackageActivity extends AppCompatActivity implements View.OnClickListener, TLearnPlanAddInterface {

    private static final String TAG = "TTeachEditPackageActivity";
    private String mRequestUrl = "";

    // 顶部按钮
    private Button btn_add;
    private Button btn_change;
    private Button btn_assign;
    private Button lastBtn = null;

    private ClickableImageView iv_search_select;

    // 双window+双contentView
    private View contentView, contentView2; // 一级，二级
    private PopupWindow window, window2; // 一级，二级

    /**
     * Pop1内容，一个选项列表
     */

    // 选项展示标记
    private int showPos = -1;

    // 选择数据
    private String xueduan = "";
    private String xueke = "";
    private String banben = "";
    private String jiaocai = "";
    private String zhishidian = "";
    private String zhishidianData = "知识点列表未获取到或者为空";
    private String zhishidianId = "";
    private String type = "0";
    private String typeSub = "";
    private String shareTag = "";

    // 备选数据
    private Map<String, String> xueduanMap = new LinkedHashMap<>();
    private Map<String, String> xuekeMap = new LinkedHashMap<>();
    private Map<String, String> banbenMap = new LinkedHashMap<>();
    private Map<String, String> jiaocaiMap = new LinkedHashMap<>();
    private Map<String, String> typeMap = new LinkedHashMap<>();

    // 空数据组件
    private TextView tv_xueduan_null;
    private TextView tv_xueke_null;
    private TextView tv_banben_null;
    private TextView tv_jiaocai_null;

    // 展示被选内容组件
    private TextView tv_xueduan;
    private TextView tv_xueke;
    private TextView tv_banben;
    private TextView tv_jiaocai;
    private TextView tv_point;
    private TextView tv_type;

    // 上一个选择组件
    private TextView lastXueduan;
    private TextView lastXueke;
    private TextView lastBanben;
    private TextView lastJiaocai;
    private TextView lastType;
    private TextView lastTypeSub;

    // 选项容器
    private FlexboxLayout fl_xueduan;
    private FlexboxLayout fl_xueke;
    private FlexboxLayout fl_banben;
    private FlexboxLayout fl_jiaocai;
    private FlexboxLayout fl_resource_type;

    // 选项箭头按钮
    private ClickableImageView iv_xueduan;
    private ClickableImageView iv_xueke;
    private ClickableImageView iv_banben;
    private ClickableImageView iv_jiaocai;
    private ClickableImageView iv_type;

    // pop1顶部按钮
    private Button lastPopBtn;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;

    // 选项块视图
    private View view;

    // 知识点跳转
    private TextView tv_toPoint;

    // 默认类型选项
    private LinearLayout ll_type;

    // type固定按钮
    private TextView tv_type_all;
    private TextView tv_type_question;
    private TextView tv_type_paper;
    private TextView tv_type_resource;

    /**
     * pop2内容，知识点树展示
     */
    // pop2返回按钮
    private ClickableImageView pop_iv_back;

    // Fragment组件
    private TPackagePickAddFragment addFragment;
    private TPackagePickChangeFragment changeFragment;

    private RelativeLayout rl_submitting;

    private String learnPlanId;

    private String learnPlanName;
    private String xueduanCode;
    private String xuekeCode;
    private String banbenCode;
    private String jiaocaiCode;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        learnPlanId = getIntent().getStringExtra("learnPlanId");
        learnPlanName = getIntent().getStringExtra("learnPlanName");

        setContentView(R.layout.activity_tteach_edit_package);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        iv_search_select = findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        // 顶部按钮动画
        btn_add = findViewById(R.id.btn_add);
        btn_change = findViewById(R.id.btn_change);
        btn_assign = findViewById(R.id.btn_assign);
        btn_add.setOnClickListener(this);
        btn_change.setOnClickListener(this);
        btn_assign.setOnClickListener(this);
        lastBtn = btn_add;

        // 遮蔽
        rl_submitting = findViewById(R.id.rl_submitting);
        TextView tv_submitting = findViewById(R.id.tv_submitting);
        tv_submitting.setText("保存中...");

        /**
         * pop1预备代码
         */
        // 获取上级添加页面数据
        Intent intent = getIntent();
        xueduan = intent.getStringExtra("xueduan");
        xueduanCode = intent.getStringExtra("xueduanCode");
        xueke = intent.getStringExtra("xueke");
        xuekeCode = intent.getStringExtra("xuekeCode");
        banben = intent.getStringExtra("banben");
        banbenCode = intent.getStringExtra("banbenCode");
        jiaocai = intent.getStringExtra("jiaocai");
        jiaocaiCode = intent.getStringExtra("jiaocaiCode");
        zhishidian = intent.getStringExtra("zhishidian");
        zhishidianId = intent.getStringExtra("zhishidianId");

        // 添加子页面Fragment
        // 默认参数
        this.type = "0";
        shareTag = "99";
        addFragment = new TPackagePickAddFragment(xueduan, xueduanCode, xueke, xuekeCode, banben, banbenCode, jiaocai, jiaocaiCode, zhishidian, zhishidianId, this.type, typeSub, shareTag);
        changeFragment = new TPackagePickChangeFragment();
        loadPickList();
    }

    /**
     * 顶部按钮动画
     *
     * @param nowBtn 新选择的btn
     */
    private void changeBtn(Button nowBtn) {
        if (nowBtn.getId() != lastBtn.getId()) {
            lastBtn.setBackgroundResource(0);
            lastBtn.setTextColor(getResources().getColor(R.color.t_blue));
            nowBtn.setBackgroundResource(R.drawable.t_homework_add_pick);
            nowBtn.setTextColor(getResources().getColor(R.color.white));
            lastBtn = nowBtn;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addUI();
                break;
            case R.id.btn_change:
                changeUI();
                break;
            case R.id.btn_assign:
                assignUI();
                break;
            case R.id.iv_search_select:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_t_add_learn_plan_selector, null, false);
                    loadPopupView();
                    //绑定点击事件
                    window = new PopupWindow(contentView, PxUtils.dip2px(this, 400), PxUtils.dip2px(this, 640), true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, 0, 10);
                break;
            /**
             * 下面是pop1相关组件点击事件
             */
            case R.id.btn_1:
                changePopBtn(btn_1);
                shareTag = "99";
                break;
            case R.id.btn_2:
                changePopBtn(btn_2);
                shareTag = "10";
                break;
            case R.id.btn_3:
                changePopBtn(btn_3);
                shareTag = "50";
                break;
            case R.id.iv_xueduan:
                showList(0);
                break;
            case R.id.iv_xueke:
                showList(1);
                break;
            case R.id.iv_banben:
                showList(2);
                break;
            case R.id.iv_jiaocai:
                showList(3);
                break;
            case R.id.tv_point:
                showList(4);
                break;
            case R.id.tv_toPoint:
                // 知识点选择页面
                if (contentView2 == null) {
                    contentView2 = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_show_zsd, null, false);
                    // 获取组件
                    contentView2.findViewById(R.id.iv_back).setOnClickListener(v -> window.dismiss());
                    window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                }
                WebView wv_content = contentView2.findViewById(R.id.wv_content);
                pop_iv_back = contentView2.findViewById(R.id.iv_back);
                pop_iv_back.setOnClickListener(v -> window2.dismiss());
                wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv_content.getSettings().setJavaScriptEnabled(true);
                MyJavaScriptInterface jsInterface = new MyJavaScriptInterface(this);
                wv_content.addJavascriptInterface(jsInterface, "AndroidInterface");
                setHtmlOnWebView(wv_content, zhishidianData);
                window2.showAsDropDown(iv_search_select, 0, 0);
                break;
            case R.id.iv_type:
                showList(5);
                break;
            case R.id.tv_reset:
                refresh(0);
                break;
            case R.id.tv_confirm:
                if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai, zhishidian, shareTag, type)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("必填项不完整");
                    builder.setNegativeButton("关闭", null);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                } else {
                    addFragment.updateItem(xueduan, xueduanMap.get(xueduan), xueke, xuekeMap.get(xueke), banben, banbenMap.get(banben), jiaocai, jiaocaiMap.get(jiaocai), zhishidian, zhishidianId, this.type, typeMap.get(typeSub) == null ? "" : typeMap.get(typeSub), shareTag);
                    window.dismiss();
                }
                break;
            // 固定type
            case R.id.tv_type_all:
                if (lastType != view) {
                    Log.d(TAG, "onClick: 不同");
                    lastType.setBackgroundResource(R.color.light_gray3);
                    tv_type_all.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastType = (TextView) view;
                    type = "0";
                    fl_resource_type.removeAllViews();
                    tv_type.setText("全部");
                }
                break;
            case R.id.tv_type_question:
                if (lastType != view) {
                    lastType.setBackgroundResource(R.color.light_gray3);
                    tv_type_question.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastType = (TextView) view;
                    type = "question";
                    loadType();
                    tv_type.setText("试题");
                }
                break;
            case R.id.tv_type_paper:
                if (lastType != view) {
                    lastType.setBackgroundResource(R.color.light_gray3);
                    tv_type_paper.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastType = (TextView) view;
                    type = "paper";
                    typeSub = "";
                    loadType();
                    tv_type.setText("试卷");
                }
                break;
            case R.id.tv_type_resource:
                if (lastType != view) {
                    lastType.setBackgroundResource(R.color.light_gray3);
                    tv_type_resource.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastType = (TextView) view;
                    type = "resource";
                    typeSub = "";
                    loadType();
                    tv_type.setText("资源");
                }
                break;

        }
    }

    // 修改试题顺序页面
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void changeUI() {
        if (addFragment.pickList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("暂无选中试题");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
            return;
        }
        changeBtn(btn_change);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, changeFragment).commit();
        changeFragment.updateItem(addFragment.pickList);
        iv_search_select.setVisibility(View.INVISIBLE);
    }

    // 挑选试题页面
    private void addUI() {
        changeBtn(btn_add);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, addFragment).commit();
        iv_search_select.setVisibility(View.VISIBLE);
    }

    //发布试题页面
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void assignUI() {

        if (addFragment.pickList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("暂无选中试题");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
            return;
        }
        try {
            submit("", "", "", "", "", "", "3", "", "", "", "edit");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下面是pop1相关方法
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadPopupView() {
        btn_1 = contentView.findViewById(R.id.btn_1);
        lastPopBtn = btn_1;
        btn_2 = contentView.findViewById(R.id.btn_2);
        btn_3 = contentView.findViewById(R.id.btn_3);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);

        tv_xueduan = contentView.findViewById(R.id.tv_xueduan);
        tv_xueduan.setOnClickListener(v -> iv_xueduan.callOnClick());
        tv_xueke = contentView.findViewById(R.id.tv_xueke);
        tv_xueke.setOnClickListener(v -> iv_xueke.callOnClick());
        tv_banben = contentView.findViewById(R.id.tv_banben);
        tv_banben.setOnClickListener(v -> iv_banben.callOnClick());
        tv_jiaocai = contentView.findViewById(R.id.tv_jiaocai);
        tv_jiaocai.setOnClickListener(v -> iv_jiaocai.callOnClick());
        tv_type = contentView.findViewById(R.id.tv_type);
        tv_type.setOnClickListener(v -> iv_type.callOnClick());
        tv_point = contentView.findViewById(R.id.tv_point);
        tv_point.setOnClickListener(this);
        tv_toPoint = contentView.findViewById(R.id.tv_toPoint);
        tv_toPoint.setOnClickListener(this);

        tv_xueduan_null = contentView.findViewById(R.id.tv_xueduan_null);
        tv_xueke_null = contentView.findViewById(R.id.tv_xueke_null);
        tv_banben_null = contentView.findViewById(R.id.tv_banben_null);
        tv_jiaocai_null = contentView.findViewById(R.id.tv_jiaocai_null);
        ll_type = contentView.findViewById(R.id.ll_type);

        fl_xueduan = contentView.findViewById(R.id.fl_xueduan);
        fl_xueke = contentView.findViewById(R.id.fl_xueke);
        fl_banben = contentView.findViewById(R.id.fl_banben);
        fl_jiaocai = contentView.findViewById(R.id.fl_jiaocai);
        fl_resource_type = contentView.findViewById(R.id.fl_resource_type);

        // type专属
        tv_type_all = contentView.findViewById(R.id.tv_type_all);
        tv_type_question = contentView.findViewById(R.id.tv_type_question);
        tv_type_paper = contentView.findViewById(R.id.tv_type_paper);
        tv_type_resource = contentView.findViewById(R.id.tv_type_resource);
        tv_type_all.setOnClickListener(this);
        tv_type_question.setOnClickListener(this);
        tv_type_paper.setOnClickListener(this);
        tv_type_resource.setOnClickListener(this);
        lastType = tv_type_all;

        iv_xueduan = contentView.findViewById(R.id.iv_xueduan);
        iv_xueke = contentView.findViewById(R.id.iv_xueke);
        iv_banben = contentView.findViewById(R.id.iv_banben);
        iv_jiaocai = contentView.findViewById(R.id.iv_jiaocai);
        iv_type = contentView.findViewById(R.id.iv_type);
        iv_xueduan.setOnClickListener(this);
        iv_xueke.setOnClickListener(this);
        iv_banben.setOnClickListener(this);
        iv_jiaocai.setOnClickListener(this);
        iv_type.setOnClickListener(this);

        contentView.findViewById(R.id.tv_confirm).setOnClickListener(this);
        contentView.findViewById(R.id.tv_reset).setOnClickListener(this);
        tv_xueduan.setText(xueduan);
        tv_xueke.setText(xueke);
        tv_banben.setText(banben);
        tv_jiaocai.setText(jiaocai);
        tv_point.setText(zhishidian);
        String typeName = "全部";
        switch (type) {
            case "paper":
                typeName = "试卷";
                break;
            case "question":
                typeName = "试题";
                break;
            case "resource":
                typeName = "资源";
                break;
        }
        tv_type.setText(typeName);
        loadXueDuan();
        loadXueKe();
        loadBanBen();
        loadJiaoCai();
        loadZhiShiDian();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void submit(String startTime, String endTime, String ketang, String ketangId, String clas, String classId, String assignType, String stuIds, String stuNames, String learnType, String flag) throws UnsupportedEncodingException {
        Intent intent = getIntent();

        // 导学案专属参数

        String classHours = "";
        String studyHours = "";

        String Introduce = "";
        String Goal = "";
        String Emphasis = "";
        String Difficulty = "";
        String Extension = "";
        String Summary = "";

        // 真正数据
        String xueduan = intent.getStringExtra("xueduan");
        String xueduanCode = intent.getStringExtra("xueduanCode");
        String xueke = intent.getStringExtra("xueke");
        String xuekeCode = intent.getStringExtra("xuekeCode");
        String banben = intent.getStringExtra("banben");
        String banbenCode = intent.getStringExtra("banbenCode");
        String jiaocai = intent.getStringExtra("jiaocai");
        String jiaocaiCode = intent.getStringExtra("jiaocaiCode");
        String zhishidian = intent.getStringExtra("zhishidian");
        String zhishidianId = intent.getStringExtra("zhishidianId");


        StringBuilder jsonStringBuilder = new StringBuilder();
        String jsonString = "[";
        List<LearnPlanAddItemEntity> pickList = addFragment.pickList;
        for (int i = 0; i < pickList.size(); ++i) {
            LearnPlanAddItemEntity item = pickList.get(i);
            item.setOrder(i + 1);
            if (jsonStringBuilder.length() > 0) {
                jsonStringBuilder.append(", ");
            }
            jsonStringBuilder.append(item.toData());
        }
        jsonString += jsonStringBuilder.toString();
        jsonString += "]";

        Log.d("wen", "内容串: " + jsonString);

        ketang = URLEncoder.encode(ketang, "UTF-8");
        clas = URLEncoder.encode(clas, "UTF-8");
        stuNames = URLEncoder.encode(stuNames, "UTF-8");
        jsonString = URLEncoder.encode(jsonString, "UTF-8");

        mRequestUrl = Constant.API + Constant.T_LEARN_PLAN_ASSIGN_SAVE + "?assignType=" + assignType +
                "&channelCode=" + xueduanCode + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") +
                "&subjectCode=" + xuekeCode + "&subjectName=" + URLEncoder.encode(xueke, "UTF-8") +
                "&textBookCode=" + banbenCode + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") +
                "&gradeLevelCode=" + jiaocaiCode + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8") +
                "&pointCode=" + zhishidianId + "&pointName=" + URLEncoder.encode(zhishidian, "UTF-8") +

                "&type=3" + "&learnPlanType=" + "&classHours=" + classHours +
                "&studyHours=" + studyHours + "&Introduce=" + URLEncoder.encode(Introduce, "UTF-8") + "&Goal=" + URLEncoder.encode(Goal, "UTF-8") +
                "&Emphasis=" + URLEncoder.encode(Emphasis, "UTF-8") + "&Difficulty=" + URLEncoder.encode(Difficulty, "UTF-8") + "&Summary=" + URLEncoder.encode(Summary, "UTF-8") + "&Extension=" + URLEncoder.encode(Extension, "UTF-8") +

                "&startTime=" + startTime + "&endTime=" + endTime +
                "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classIds=" + classId +
                "&className=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                "&roomType=" + learnType +

                "&userName=" + MyApplication.username + "&learnPlanId=" + learnPlanId +
                "&learnPlanName=" + URLEncoder.encode(learnPlanName, "UTF-8") + "&flag=edit" + "&jsonStr=" + jsonString;

        Log.d("wen", "URL: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.d(TAG, "submit: " + json);
                boolean success = json.getBoolean("success");
                String msg = json.getString("message");


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(learnPlanName);

                if (success) {
                    if (assignType.equals("3")) {
                        builder.setMessage("授课包保存成功");
                    } else {
                        builder.setMessage("授课包布置成功");
                    }
                    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rl_submitting.setVisibility(View.GONE);
                            Intent toHome = new Intent(TTeachEditPackageActivity.this, TMainPagerActivity.class);
                            toHome.putExtra("pos", 2);
                            //两个一起用
                            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            toHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(toHome);
                        }
                    });
                } else {
                    builder.setMessage(msg);
                    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rl_submitting.setVisibility(View.GONE);
                            Intent toHome = new Intent(TTeachEditPackageActivity.this, TMainPagerActivity.class);
                            toHome.putExtra("pos", 2);
                            //两个一起用
                            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            toHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(toHome);
                        }
                    });
                }

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
        rl_submitting.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLearnPlanId(String learnPlanId) {

    }

    // Java接口注入到Js中
    public class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @JavascriptInterface
        public void displayHTMLContent(String str, String id) {
            // 在这里使用htmlContent进行处理，例如显示在TextView中

            zhishidian = str;
            zhishidianId = id;

            // 封装消息，传递给主线程
            Message message = Message.obtain();
            message.what = 100;
            handler.sendMessage(message);
        }
    }

    /**
     * Pop顶部按钮动画
     *
     * @param nowBtn 新选择的btn
     */
    private void changePopBtn(Button nowBtn) {
        if (nowBtn != lastPopBtn) {
            nowBtn.setBackgroundResource(R.drawable.t_homework_add_pick);
            nowBtn.setTextColor(getColor(R.color.white));
            lastPopBtn.setBackgroundResource(R.drawable.t_homework_add_border);
            lastPopBtn.setTextColor(getColor(R.color.t_blue));
            lastPopBtn = nowBtn;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showList(int newPos) {
        // 关闭showPos部分视图
        switch (showPos) {
            case 0:
                fl_xueduan.removeAllViews();
                iv_xueduan.setImageResource(R.drawable.down_icon);
                tv_xueduan_null.setVisibility(View.GONE);
                break;
            case 1:
                fl_xueke.removeAllViews();
                iv_xueke.setImageResource(R.drawable.down_icon);
                tv_xueke_null.setVisibility(View.GONE);
                break;
            case 2:
                fl_banben.removeAllViews();
                iv_banben.setImageResource(R.drawable.down_icon);
                tv_banben_null.setVisibility(View.GONE);
                break;
            case 3:
                fl_jiaocai.removeAllViews();
                iv_jiaocai.setImageResource(R.drawable.down_icon);
                tv_jiaocai_null.setVisibility(View.GONE);
                break;
            case 4:
                tv_toPoint.setVisibility(View.GONE);
                break;
            case 5:
                fl_resource_type.removeAllViews();
                ll_type.setVisibility(View.GONE);
                iv_type.setImageResource(R.drawable.down_icon);
                break;
            default:
                break;
        }

        if (newPos != showPos) {
            // 展示newPos部分视图
            switch (newPos) {
                case 0:
                    showXueDuan();
                    iv_xueduan.setImageResource(R.drawable.up_icon);
                    break;
                case 1:
                    showXueKe();
                    iv_xueke.setImageResource(R.drawable.up_icon);
                    break;
                case 2:
                    showBanBen();
                    iv_banben.setImageResource(R.drawable.up_icon);
                    break;
                case 3:
                    showJiaoCai();
                    iv_jiaocai.setImageResource(R.drawable.up_icon);
                    break;
                case 4:
                    tv_toPoint.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    ll_type.setVisibility(View.VISIBLE);
                    iv_type.setImageResource(R.drawable.up_icon);
                    break;
                default:
                    break;
            }
            showPos = newPos;
        } else {
            // 同时showPos为-1
            showPos = -1;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                refresh(5);
                loadType();
                window2.dismiss();
                tv_point.setText(zhishidian);
            }
        }
    };

    // 获取选题信息
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadPickList() {
        mRequestUrl = Constant.API + Constant.T_GET_LEARN_PLAN_PICKLIST + "?learnPlanId=" + learnPlanId + "&deviceType=PAD";

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();
                List<LearnPlanAddItemEntity> moreList = gson.fromJson(itemString, new com.google.gson.reflect.TypeToken<List<LearnPlanAddItemEntity>>() {
                }.getType());
                addFragment.pickList = moreList;
                changeFragment.pickList = moreList;
                changeUI();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    /**
     * 请求和展示各种信息 将Handler内容写入，速度更快（如遇到报错，再用Handler）
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadXueDuan() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_XUEDUAN + "?userName=" + MyApplication.username;
        Log.d("wen", "loadXueDuan: " + mRequestUrl);
        xueduanMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    xueduanMap.put(object.getString("channelName"), object.getString("channelId"));
                }
                Log.d("wen", "学段: " + xueduanMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showXueDuan() {
        if (xueduanMap.size() == 0) {
            tv_xueduan_null.setVisibility(View.VISIBLE);
        }
        lastXueduan = null;

        xueduanMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_xueduan, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(xueduan)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastXueduan = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                xueduan = (String) tv_name.getText();

                if (lastXueduan != null) {
                    lastXueduan.setBackgroundResource(R.color.light_gray3);
                }

                if (lastXueduan == tv_name) {
                    xueduan = "";
                    xueduanCode = "";
                    lastXueduan = null;
                    refresh(1);
                } else {
                    xueduan = tv_name.getText().toString();
                    xueduanCode = xueduanMap.get(xueduan);
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastXueduan = tv_name;
                    refresh(1);
                    loadXueKe();
                }
                tv_xueduan.setText(xueduan);

            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueduan.getWidth() / 3 - PxUtils.dip2px(this, 14);
            tv_name.setLayoutParams(params);
            fl_xueduan.addView(view);
        });
    }

    private void loadXueKe() {

        if (StringUtils.hasEmptyString(xueduan)) {
            return;
        }
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_XUEKE + "?userName=" + MyApplication.username + "&channelCode=" + xueduanCode;
        Log.d("wen", "loadXueKe: " + mRequestUrl);
        xuekeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    xuekeMap.put(object.getString("subjectName"), object.getString("subjectId"));
                }

                Log.d("wen", "学科: " + xuekeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showXueKe() {
        if (xuekeMap.size() == 0) {
            tv_xueke_null.setVisibility(View.VISIBLE);
        }
        lastXueke = null;
        xuekeMap.forEach((name, id) -> {
            // name（包含学段）
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_xueke, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            // 按钮显示（不含学段）
            tv_name.setText(name.substring(2));
            // xueke（包含学段）
            if (name.equals(xueke)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastXueke = tv_name;
            }
            tv_name.setOnClickListener(v -> {

                if (lastXueke != null) {
                    lastXueke.setBackgroundResource(R.color.light_gray3);
                }

                if (lastXueke == tv_name) {
                    xueke = "";
                    xuekeCode = "";
                    lastXueke = null;
                    refresh(2);
                } else {
                    // 点击事件，获取xueke（包含学段）
                    xueke = xueduan + tv_name.getText().toString();
                    xuekeCode = xuekeMap.get(xueke);

                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastXueke = tv_name;
                    refresh(2);
                    loadBanBen();
                }

                tv_xueke.setText(xueke);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueke.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
            tv_name.setLayoutParams(params);
            fl_xueke.addView(view);
        });
    }

    private void loadBanBen() {
        if (StringUtils.hasEmptyString(xueduan, xueke)) {
            return;
        }
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_BANBEN + "?userName=" + MyApplication.username + "&channelCode=" + xueduanCode + "&subjectCode=" + xuekeCode;
        Log.d("wen", "loadBanBen: " + mRequestUrl);
        banbenMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    banbenMap.put(object.getString("textbookName"), object.getString("textBookCode"));
                }

                Log.d("wen", "版本: " + banbenMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showBanBen() {
        if (banbenMap.size() == 0) {
            tv_banben_null.setVisibility(View.VISIBLE);
        }
        lastBanben = null;
        banbenMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_banben, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(banben)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastBanben = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                banben = (String) tv_name.getText();

                if (lastBanben != null) {
                    lastBanben.setBackgroundResource(R.color.light_gray3);
                }

                if (lastBanben == tv_name) {
                    banben = "";
                    banbenCode = "";
                    lastBanben = null;
                    refresh(3);
                } else {
                    banben = tv_name.getText().toString();
                    banbenCode = banbenMap.get(banben);
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastBanben = tv_name;
                    refresh(3);
                    loadJiaoCai();
                }
                tv_banben.setText(banben);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_banben.getWidth() / 3 - PxUtils.dip2px(this, 14);
            tv_name.setLayoutParams(params);
            fl_banben.addView(view);
        });
    }

    private void loadJiaoCai() {

        if (StringUtils.hasEmptyString(xueduan, xueke, banben)) {
            return;
        }

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_JIAOCAI + "?userName=" + MyApplication.username + "&channelCode=" + xueduanCode + "&subjectCode=" + xuekeCode + "&textBookCode=" + banbenCode;
        Log.d("wen", "loadJiaoCai: " + mRequestUrl);

        jiaocaiMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    jiaocaiMap.put(object.getString("gradeLevelName"), object.getString("gradeLevelCode"));
                }

                Log.d("wen", "教材: " + jiaocaiMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showJiaoCai() {
        if (jiaocaiMap.size() == 0) {
            tv_jiaocai_null.setVisibility(View.VISIBLE);
        }

        lastJiaocai = null;
        jiaocaiMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_jiaocai, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(jiaocai)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastJiaocai = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                jiaocai = (String) tv_name.getText();
                if (lastJiaocai != null) {
                    lastJiaocai.setBackgroundResource(R.color.light_gray3);
                }

                if (lastJiaocai == tv_name) {
                    jiaocai = "";
                    jiaocaiCode = "";
                    lastJiaocai = null;
                    refresh(4);
                } else {
                    jiaocai = tv_name.getText().toString();
                    jiaocaiCode = jiaocaiMap.get(jiaocai);
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastJiaocai = tv_name;
                    refresh(4);
                    loadZhiShiDian();
                }
                tv_jiaocai.setText(jiaocai);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_jiaocai.getWidth() / 3 - PxUtils.dip2px(this, 14);
            tv_name.setLayoutParams(params);
            fl_jiaocai.addView(view);
        });
    }

    private void loadZhiShiDian() {

        if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai)) {
            return;
        }

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_ZHISHIDIAN + "?userName=" + MyApplication.username + "&subjectCode=" + xuekeCode + "&textBookCode=" + banbenCode + "&gradeLevelCode=" + jiaocaiCode;
        Log.d("wen", "loadZhiShiDian: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                zhishidianData = json.getString("data");
                Log.d("wen", "loadZhiShiDian: " + zhishidian);
                if (zhishidianData.length() == 0) {
                    zhishidianData = "知识点列表未获取到或者为空";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    /**
     * showZhiShiDian在[tv_toPoint的点击事件]中，
     * 回调在注入的JS接口中
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadType() {
        if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai, zhishidian)) {
            return;
        }
        switch (type) {
            case "0":
                return;
            case "question":
                mRequestUrl = Constant.API + Constant.T_GET_QUESTION_TYPE;
                break;
            case "paper":
                mRequestUrl = Constant.API + Constant.T_GET_PAPER_TYPE;
                break;
            case "resource":
                mRequestUrl = Constant.API + Constant.T_GET_RESOURCE_TYPE;
                break;
            default:
                Log.d(TAG, "未知类型: " + type);
                break;
        }

        mRequestUrl += "channelCode=" + xueduanMap.get(xueduan) + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" +
                jiaocaiMap.get(jiaocai) + "&pointCode=" + zhishidianId + "&shareTag=" + shareTag + "&teacherId=" + MyApplication.username;
        Log.d("wen", "type: " + mRequestUrl);
        typeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");

                if (!data.equals("null") && data.length() > 0 && !data.equals("[]")) {
                    for (String row : data.split("\\],\\[")) {
                        String[] values = row.replaceAll("\\[|\\]|\"", "").split(",");
                        typeMap.put(values[1], values[0]);
                    }
                }
                Log.d("wen", "类型: " + typeMap);
                // 加载完赶紧显示
                showType();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showType() {
        fl_resource_type.removeAllViews();
        typeMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_resource_type, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setTextColor(getResources().getColor(R.color.black));
            tv_name.setText(name);
            if (name.equals(typeSub)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastTypeSub = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                typeSub = (String) tv_name.getText();

                if (lastTypeSub != null) {
                    lastTypeSub.setBackgroundResource(R.color.light_gray3);
                }

                if (lastTypeSub == tv_name) {
                    typeSub = "";
                    lastTypeSub = null;
                } else {
                    typeSub = tv_name.getText().toString();
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastTypeSub = tv_name;
                }
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_resource_type.getWidth() / 3 - PxUtils.dip2px(this, 14);
            tv_name.setLayoutParams(params);
            fl_resource_type.addView(view);
        });
    }

    private void refresh(int lv) {
        switch (lv) {
            case 0:
                xueduan = "";
                tv_xueduan.setText("");
            case 1:
                xueke = "";
                xuekeMap.clear();
                tv_xueke.setText("");
            case 2:
                banben = "";
                banbenMap.clear();
                tv_banben.setText("");
            case 3:
                jiaocai = "";
                jiaocaiMap.clear();
                tv_jiaocai.setText("");
            case 4:
                zhishidian = "";
                zhishidianData = "知识点列表未获取到或者为空";
                tv_point.setText("");
            case 5:
                type = "0";
                typeMap.clear();
                tv_type.setText("");
                break;
        }
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     *
     * @param wb  WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str) {
        str = StringEscapeUtils.unescapeHtml4(str);
        String html_content = "<head><style>" +
                " p {\n" +
                "   margin: 0px;" +
                "   line-height: 30px;" +
                "   }" +
                " li {\n" +
                "   margin-bottom: 15px;\n" +
                "   }" +
                "</style>" +
                "<script>\n" +
                "    function _fk(obj) {\n" +
                "        AndroidInterface.displayHTMLContent(obj.textContent,obj.getAttribute(\"id\"))" +
                "     }\n" +
                "</script>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 16px; margin: 0px; padding: 0px\">" + str +
                "</body>";
        wb.loadData(html_content, "text/html", "utf-8");
    }
}