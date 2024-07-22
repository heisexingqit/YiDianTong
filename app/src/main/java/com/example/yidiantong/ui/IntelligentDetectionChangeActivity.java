package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class IntelligentDetectionChangeActivity extends AppCompatActivity {
    private static final String TAG = "IntelligentDetectionChangeActivity";
    private String mRequestUrl;
    // 选择参数
    private String xueduan = "";//学段id
    private String banben = "";//版本id
    private String jiaocai = "";//教材id
    private String zhishidian = "";//知识点
    private String zhishidianData = "知识点列表未获取到或者为空";
    private String zhishidianId = "";
    private String message;

    private View contentView;
    private PopupWindow window;
    private WebView wv_content;
    private RelativeLayout ll_content;

    private String userName; //用户名
    private String subjectId;  //学科id
    private String unitId;  //考点
    private String course_name;  //学科名
    private RelativeLayout rl_loading;
    private ClickableImageView fiv_back;
    private ClickableImageView fiv_refresh;
    private TextView ftv_title;

    @SuppressLint("JavascriptInterface")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_t_knowledge_study_change);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("智能检测结果反馈");
        fiv_back = findViewById(R.id.fiv_back);
        fiv_back.setOnClickListener(v -> {
            if (MyApplication.typeHistory == 1) {
                Intent intent = new Intent(IntelligentDetectionChangeActivity.this, AutoDetectionActivity.class);
                // 通过添加以下标志来清除堆栈中的所有活动，并使 `ActivityA` 成为堆栈顶部的唯一活动
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            if (MyApplication.typeHistory == 2) {
                Intent intent = new Intent(IntelligentDetectionChangeActivity.this, DetectionHistoryActivity.class);
                // 通过添加以下标志来清除堆栈中的所有活动，并使 `ActivityA` 成为堆栈顶部的唯一活动
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }

            finish(); if (window != null && window.isShowing()) { window.dismiss(); }
        });

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");  //用户名
        System.out.println("userName: KnowledgeStudyChangeActivity" + userName);
        subjectId = getIntent().getStringExtra("subjectId"); //学科名
        course_name = getIntent().getStringExtra("courseName"); //学科id
        zhishidian = getIntent().getStringExtra("zhishidian"); //知识点
        zhishidianId = getIntent().getStringExtra("zhishidianId"); //知识点id
        xueduan = getIntent().getStringExtra("xueduanId"); //学段
        banben = getIntent().getStringExtra("banbenId"); //版本
        jiaocai = getIntent().getStringExtra("jiaocaiId"); //教材
        unitId = getIntent().getStringExtra("unitId"); //考点
        message = getIntent().getStringExtra("message"); //任务id

        loadZhiShiDian();
        // 获取组件
        ll_content = findViewById(R.id.ll_content);
        // 知识点选择页面
        wv_content = findViewById(R.id.wv_content);
        // 设置滚动条样式, 去掉滚动条, 但是滚动是可以的
        wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // 设置WebView属性,允许运行执行js脚本
        wv_content.getSettings().setJavaScriptEnabled(true);
        MyJavaScriptInterface jsInterface = new MyJavaScriptInterface(this);
        wv_content.addJavascriptInterface(jsInterface, "AndroidInterface");
    }


    // JS延迟关闭PopUpWindow
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                // TODO 临时变更
//                loadZSDID();
            }
        }
    };

    // Java接口注入到Js中
    public class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }
    }


    // 加载知识点
    private void loadZhiShiDian() {
        rl_loading.setVisibility(View.VISIBLE);
        fiv_back.setVisibility(View.VISIBLE);
        mRequestUrl = Constant.API + "/AppServer/ajax/studentApp_getStuPointPossessNew.do"
                + "?stuId=" + userName + "&taskId=" + message + "&unitId=1101010010001";
        Log.d("wen", "loadZhiShiDian: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                zhishidianData = json.getString("data");
                Log.d("wen", "知识点: " + zhishidianData);
                if (zhishidianData.length() == 0) {
                    zhishidianData = "知识点列表未获取到或者为空";
                }
                // 将知识点数据加载到WebView中
                setHtmlOnWebView(wv_content, zhishidianData);
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
     * 将HTML内容显示在WebView中，包含转义和样式
     *
     * @param wb  WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str) {
        rl_loading.setVisibility(View.GONE);
        str = StringEscapeUtils.unescapeHtml4(str);
        System.out.println("str: " + str);

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
                "</script>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 16px; margin: 0px; padding: 0px\">" + str +
                "</body>";
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保Dialog存在且显示中才去dismiss，避免空指针异常
        /*if (window != null && window.isShowing()) {
            window.dismiss();
        }*/
    }
}