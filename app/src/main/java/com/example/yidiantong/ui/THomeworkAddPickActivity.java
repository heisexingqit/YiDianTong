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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.fragment.THomeworkPickAddFragment;
import com.example.yidiantong.fragment.THomeworkPickAssignFragment;
import com.example.yidiantong.fragment.THomeworkPickChangeFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.THomeworkAddInterface;
import com.google.android.flexbox.FlexboxLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class THomeworkAddPickActivity extends AppCompatActivity implements View.OnClickListener, THomeworkAddInterface {

    private static final String TAG = "THomeworkAddPickActivity";
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
    private String type = "全部";
    private String shareTag = "99";

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
    private TextView tv_type_null;

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

    // 选项容器
    private FlexboxLayout fl_xueduan;
    private FlexboxLayout fl_xueke;
    private FlexboxLayout fl_banben;
    private FlexboxLayout fl_jiaocai;
    private FlexboxLayout fl_type;

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

    /**
     * pop2内容，知识点树展示
     */
    // pop2返回按钮
    private ClickableImageView pop_iv_back;

    // Fragment组件
    private THomeworkPickAddFragment addFragment;
    private THomeworkPickChangeFragment changeFragment;
    private THomeworkPickAssignFragment assignFragment;

    private String name;
    private String introduce;
    private String paperId;

    private RelativeLayout rl_submitting;

    private boolean initalTime = true;
    private String xueduanId;
    private String xuekeId;
    private String banbenId;
    private String jiaocaiId;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_add_pick);

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
        // 记住选择-本地数据读取
        // 获取本地数据
        // ---------------------//
        //  新版，只给name，无map
        // ---------------------//
        Intent intent = getIntent();
        xueduan = intent.getStringExtra("xueduan");
        xueke = intent.getStringExtra("xueke");
        banben = intent.getStringExtra("banben");
        jiaocai = intent.getStringExtra("jiaocai");
        zhishidian = intent.getStringExtra("zhishidian");
        zhishidianId = intent.getStringExtra("zhishidianId");
        xueduanId = intent.getStringExtra("xueduanId");
        xuekeId = intent.getStringExtra("xuekeId");
        banbenId = intent.getStringExtra("banbenId");
        jiaocaiId = intent.getStringExtra("jiaocaiId");
        // ----------------//
        //  第1步，加载学段
        // ----------------//
        loadXueDuan();

        name = intent.getStringExtra("name");
        introduce = intent.getStringExtra("introduce");

        loadType();

        // Fragment写入
        addFragment = new THomeworkPickAddFragment(xueduanId, xuekeId, banbenId, jiaocaiId, zhishidianId, "all", "99");
        changeFragment = new THomeworkPickChangeFragment();
        assignFragment = new THomeworkPickAssignFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, addFragment).commit();

        if (paperId == null || paperId.length() == 0) {
            // 获取空试卷id
            getPaperId();
        }
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
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_t_add_homework_selector, null, false);
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
                    addFragment.updateItem(xueduanMap.get(xueduan), xuekeMap.get(xueke), banbenMap.get(banben), jiaocaiMap.get(jiaocai), zhishidianId, typeMap.get(type), shareTag);
                    window.dismiss();
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
        changeFragment.updateItem(addFragment.pickList);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, changeFragment).commit();
        iv_search_select.setVisibility(View.INVISIBLE);
    }

    // 挑选试题页面
    private void addUI() {
        changeBtn(btn_add);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, addFragment).commit();
        iv_search_select.setVisibility(View.VISIBLE);
    }

    //发布试题页面
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
        changeBtn(btn_assign);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, assignFragment).commit();
        iv_search_select.setVisibility(View.INVISIBLE);
    }

    /**
     * 下面是pop1相关方法
     */

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
        tv_type_null = contentView.findViewById(R.id.tv_type_null);

        fl_xueduan = contentView.findViewById(R.id.fl_xueduan);
        fl_xueke = contentView.findViewById(R.id.fl_xueke);
        fl_banben = contentView.findViewById(R.id.fl_banben);
        fl_jiaocai = contentView.findViewById(R.id.fl_jiaocai);
        fl_type = contentView.findViewById(R.id.fl_type);

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
        tv_type.setText(type);
        tv_type = contentView.findViewById(R.id.tv_type);
    }

    int count = 0; // 成功请求计数

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void submit(String startTime, String endTime, String ketang, String ketangId, String clas, String classId, String assignType, String stuIds, String stuNames, String learnType, String flag) {
        Log.e(TAG, "start: ==================================");
        Log.e(TAG, "ketang: " + ketang);
        Log.e(TAG, "ketangId: " + ketangId);
        Log.e(TAG, "clas: " + clas);
        Log.e(TAG, "classId: " + classId);
        Log.e(TAG, "stuIds: " + stuIds);
        Log.e(TAG, "stuNames: " + stuNames);
        Log.e(TAG, "end: ==================================");
        List<String> ketangNameList = new ArrayList<>(Arrays.asList(ketang.split(", ")));
        List<String> ketangIdList = new ArrayList<>(Arrays.asList(ketangId.split(", ")));
        List<String> clasList = new ArrayList<>(Arrays.asList(clas.split(", ")));
        List<String> classIdList = new ArrayList<>(Arrays.asList(classId.split(", ")));
        List<String> stuIdsList = new ArrayList<>(Arrays.asList(stuIds.split(", ")));
        List<String> stuNamesList = new ArrayList<>(Arrays.asList(stuNames.split(", ")));

        count = 0;
        String assignType_ = assignType;
        for (int i = 0; i < ketangNameList.size(); ++i) {
            // 第一次保存+布置，后面直接布置即可
            if (i > 0 && assignType.equals("1")) {
                assignType_ = "2";
            }
            ketang = ketangNameList.get(i);
            ketangId = ketangIdList.get(i);
            clas = clasList.get(i);
            classId = classIdList.get(i);
            stuIds = stuIdsList.get(i);
            stuNames = stuNamesList.get(i);

            // --------------------------------//
            //  这部分是从AddActivity获取的属性值，
            //  与PopUpWindow中的数值不同
            //  必须从intent中直接获取
            // --------------------------------//
            Intent intent = getIntent();

            String xueduan = intent.getStringExtra("xueduan");
            String xueduanId = intent.getStringExtra("xueduanId");
            String xueke = intent.getStringExtra("xueke");
            String xuekeId = intent.getStringExtra("xuekeId");
            String banben = intent.getStringExtra("banben");
            String banbenId = intent.getStringExtra("banbenId");
            String jiaocai = intent.getStringExtra("jiaocai");
            String jiaocaiId = intent.getStringExtra("jiaocaiId");
            String zhishidian = intent.getStringExtra("zhishidian");
            String zhishidianId = intent.getStringExtra("zhishidianId");

            StringBuilder jsonStringBuilder = new StringBuilder();
            String jsonString = "{\"data\":[";
            addFragment.pickList.forEach(item -> {
                if (jsonStringBuilder.length() > 0) {
                    jsonStringBuilder.append(", ");
                }
                jsonStringBuilder.append(item.toData());
            });
            jsonString += jsonStringBuilder.toString();
            jsonString += "], \"paperId\":\"" + paperId + "\"}";

            try {
                ketang = URLEncoder.encode(ketang, "UTF-8");
                clas = URLEncoder.encode(clas, "UTF-8");
                stuNames = URLEncoder.encode(stuNames, "UTF-8");
                jsonString = URLEncoder.encode(jsonString, "UTF-8");

                mRequestUrl = Constant.API + Constant.T_HOMEWORK_ASSIGN_SAVE + "?assignType=" + assignType_ +
                        "&channelCode=" + xueduanId + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") +
                        "&subjectCode=" + xuekeId + "&subjectName=" + URLEncoder.encode(xueke, "UTF-8") +
                        "&textBookCode=" + banbenId + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") +
                        "&gradeLevelCode=" + jiaocaiId + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8") +
                        "&pointCode=" + zhishidianId + "&introduction=" + URLEncoder.encode(introduce, "UTF-8") +
                        "&userName=" + MyApplication.username + "&paperName=" + URLEncoder.encode(name, "UTF-8") +
                        "&paperId=" + paperId + "&startTime=" + startTime + "&endTime=" + endTime +
                        "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classOrGroupId=" + classId +
                        "&classOrGroupName=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                        "&learnType=" + learnType + "&flag=" + flag + "&jsonStr=" + jsonString;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            StringRequest request = new StringRequest(mRequestUrl, response -> {
                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);

                    count++;
                    Log.d("wenss", "提交成功: " + json);
                    boolean success = json.getBoolean("success");
                    String message = json.getString("message");
                    if (count == ketangNameList.size()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(name);
                        if (success) {
                            if (assignType.equals("3")) {
                                builder.setMessage("作业保存成功");
                            } else {
                                builder.setMessage("作业布置成功");
                            }
                            builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    rl_submitting.setVisibility(View.GONE);
                                    Intent toHome = new Intent(THomeworkAddPickActivity.this, TMainPagerActivity.class);
                                    toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(toHome);
                                }
                            });
                        } else {
                            builder.setMessage(message);
                            builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    rl_submitting.setVisibility(View.GONE);
                                    Intent toHome = new Intent(THomeworkAddPickActivity.this, TMainPagerActivity.class);
                                    toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    startActivity(toHome);
                                }
                            });
                        }
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.d("wen", "Volley_Error: " + error.toString());
            });
            MyApplication.addRequest(request, TAG);
            rl_submitting.setVisibility(View.VISIBLE);
            try {
                // 休眠2秒钟，避免请求过快被丢弃
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void getPaperId() {
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_PAPER_ID + "?userName=" + MyApplication.username;

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.d(TAG, "getPaperId: " + paperId);
                paperId = json.getString("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
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
                fl_type.removeAllViews();
                iv_type.setImageResource(R.drawable.down_icon);
                tv_type_null.setVisibility(View.GONE);
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
                    showType();
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

                // ----------------//
                //  第2步，加载学科
                // ----------------//
                if (xueduan.length() > 0) {
                    loadXueKe();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                selectedTv(tv_name);
                lastXueduan = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                xueduan = (String) tv_name.getText();

                if (lastXueduan != null) {
                    unselectedTv(lastXueduan);
                }

                if (lastXueduan == tv_name) {
                    xueduan = "";
                    lastXueduan = null;
                    refresh(1);
                } else {
                    xueduan = tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastXueduan = tv_name;
                    refresh(1);
                    loadXueKe();
                }
                tv_xueduan.setText(xueduan);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueduan.getWidth() / 3 - PxUtils.dip2px(this, 15);
            tv_name.setLayoutParams(params);
            fl_xueduan.addView(view);
        });
    }

    private void loadXueKe() {

        if (StringUtils.hasEmptyString(xueduan)) {
            return;
        }
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_XUEKE + "?userName=" + MyApplication.username + "&channelCode=" + xueduanMap.get(xueduan);
        Log.d("wen", "loadXueKe: " + mRequestUrl);
        xuekeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    xuekeMap.put(object.getString("subjectName").substring(2), object.getString("subjectId"));
                }

                Log.d("wen", "学科: " + xuekeMap);
                // ----------------//
                //  第3步，加载版本
                // ----------------//
                if (xueke.length() > 0) {
                    loadBanBen();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_xueke, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(xueke)) {
                selectedTv(tv_name);
                lastXueke = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                xueke = (String) tv_name.getText();

                if (lastXueke != null) {
                    unselectedTv(lastXueke);
                }

                if (lastXueke == tv_name) {
                    xueke = "";
                    lastXueke = null;
                    refresh(2);
                } else {
                    selectedTv(tv_name);
                    lastXueke = tv_name;
                    refresh(2);
                    loadBanBen();
                }

                tv_xueke.setText(xueke);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueke.getWidth() / 3 - PxUtils.dip2px(this, 15);
            tv_name.setLayoutParams(params);
            fl_xueke.addView(view);
        });
    }

    private void loadBanBen() {
        if (StringUtils.hasEmptyString(xueduan, xueke)) {
            return;
        }
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_BANBEN + "?userName=" + MyApplication.username + "&channelCode=" + xueduanMap.get(xueduan) + "&subjectCode=" + xuekeMap.get(xueke);
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
                // ----------------//
                //  第4步，加载教材
                // ----------------//
                if (banben.length() > 0) {
                    loadJiaoCai();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                selectedTv(tv_name);
                lastBanben = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                banben = (String) tv_name.getText();

                if (lastBanben != null) {
                    unselectedTv(lastBanben);
                }

                if (lastBanben == tv_name) {
                    banben = "";
                    lastBanben = null;
                    refresh(3);
                } else {
                    banben = tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastBanben = tv_name;
                    refresh(3);
                    loadJiaoCai();
                }
                tv_banben.setText(banben);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_banben.getWidth() / 3 - PxUtils.dip2px(this, 15);
            tv_name.setLayoutParams(params);
            fl_banben.addView(view);
        });
    }

    private void loadJiaoCai() {

        if (StringUtils.hasEmptyString(xueduan, xueke, banben)) {
            return;
        }

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_JIAOCAI + "?userName=" + MyApplication.username + "&channelCode=" + xueduanMap.get(xueduan) + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben);
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
                // ----------------//
                //  第5步，加载知识点
                // ----------------//
                if (jiaocai.length() > 0) {
                    loadZhiShiDian();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
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
                selectedTv(tv_name);
                lastJiaocai = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                jiaocai = (String) tv_name.getText();

                if (lastJiaocai != null) {
                    unselectedTv(lastJiaocai);
                }

                if (lastJiaocai == tv_name) {
                    jiaocai = "";
                    lastJiaocai = null;
                    refresh(4);
                } else {
                    jiaocai = tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastJiaocai = tv_name;
                    refresh(4);
                    loadZhiShiDian();
                }
                tv_jiaocai.setText(jiaocai);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_jiaocai.getWidth() / 4 - PxUtils.dip2px(this, 15);
            tv_name.setLayoutParams(params);
            fl_jiaocai.addView(view);
        });
    }

    private void loadZhiShiDian() {

        if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai)) {
            return;
        }

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ADD_ZHISHIDIAN + "?userName=" + MyApplication.username + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" + jiaocaiMap.get(jiaocai);
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
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    /**
     * showZhiShiDian在点击事件中，
     * 回调在注入的JS接口中
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadType() {
        Log.e(TAG, "loadType1: " + xueduan);
        Log.e(TAG, "loadType2: " + xueke);
        Log.e(TAG, "loadType3: " + banben);
        Log.e(TAG, "loadType4: " + jiaocai);
        Log.e(TAG, "loadType5: " + zhishidian);
        if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai, zhishidian)) {
            return;
        }
        if (initalTime) {
            mRequestUrl = Constant.API + Constant.T_GET_QUESTION_TYPE + "?channelCode=" + xueduanId + "&subjectCode=" + xuekeId + "&textBookCode=" + banbenId + "&gradeLevelCode=" + jiaocaiId + "&pointCode=" + zhishidianId + "&shareTag=" + shareTag + "&teacherId=" + MyApplication.username;
            initalTime = false;
        } else {
            mRequestUrl = Constant.API + Constant.T_GET_QUESTION_TYPE + "?channelCode=" + xueduanMap.get(xueduan) + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" + jiaocaiMap.get(jiaocai) + "&pointCode=" + zhishidianId + "&shareTag=" + shareTag + "&teacherId=" + MyApplication.username;

        }
        typeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                Log.d("wenbb", "loadType: " + data);

                if (data != "null") {
                    typeMap.put("全部", "all");
                    for (String row : data.split("\\],\\[")) {
                        String[] values = row.replaceAll("\\[|\\]|\"", "").split(",");
                        typeMap.put(values[1], values[1]);
                        Log.e(TAG, "loadType: " + values[1]);
                    }
                }
                // 基类排序

                Log.d("wen", "类型: " + typeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showType() {
        if (typeMap.size() == 0) {
            tv_type_null.setVisibility(View.VISIBLE);
        }
        lastType = null;

        typeMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_type, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(type)) {
                selectedTv(tv_name);
                lastType = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                type = (String) tv_name.getText();

                if (lastType != null) {
                    unselectedTv(lastType);
                }

                if (lastType == tv_name) {
                    type = "";
                    lastType = null;
                } else {
                    type = tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastType = tv_name;
                }
                tv_type.setText(type);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_type.getWidth() / 3 - PxUtils.dip2px(this, 15);
            tv_name.setLayoutParams(params);
            fl_type.addView(view);
        });
    }

    void refresh(int lv) {
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
                type = "";
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
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }

    private void selectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_select);
        tv.setTextColor(getColor(R.color.red));
    }

    private void unselectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_unselect);
        tv.setTextColor(getColor(R.color.default_gray));
    }
}