package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KnowledgePointActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgePointActivity";
    private String mRequestUrl;
    // 选择参数
    private String banben = "";//版本id
    private String jiaocai = "";//教材id
    private String zhishidian = "";//知识点
    private String zhishidianData = "知识点列表未获取到或者为空";
    private String zhishidianId = "";

    private View contentView;
    private PopupWindow window;
    private WebView wv_content;
    private RelativeLayout ll_content;

    private String userName; //用户名
    private String course_Id;  //学科id
    private String course_name;  //学科名

    // TODO 临时变量
//    private List<BookExerciseEntity> itemList = new ArrayList<>();
//    private String questionIds;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_t_homework_add_show_zsd);

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");  //用户名
        course_Id = getIntent().getStringExtra("xuekeId"); //学科id
        course_name = getIntent().getStringExtra("xueke"); //学科名
        banben = getIntent().getStringExtra("banbenId"); //版本id
        jiaocai = getIntent().getStringExtra("jiaocaiId"); //教材id

        loadZhiShiDian();
        // 获取组件
        ll_content = findViewById(R.id.ll_content);
        // 知识点选择页面
        contentView = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_show_zsd
                , null, false);  // 获取布局,并设置为弹出窗口的布局
        contentView.findViewById(R.id.iv_back).setOnClickListener(v -> {finish(); window.dismiss(); });
        wv_content = contentView.findViewById(R.id.wv_content);
        // 设置滚动条样式, 去掉滚动条, 但是滚动是可以的
        wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // 设置WebView属性,允许运行执行js脚本
        wv_content.getSettings().setJavaScriptEnabled(true);
        MyJavaScriptInterface jsInterface = new MyJavaScriptInterface(this);
        wv_content.addJavascriptInterface(jsInterface, "AndroidInterface");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && window == null) {
            window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, true);
            window.showAsDropDown(ll_content, 0, 0);
        }
    }


    // JS延迟关闭PopUpWindow
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {

                Intent intent = new Intent(KnowledgePointActivity.this,
                        KnowledgeShiTiActivity.class);
                intent.putExtra("userName", userName);  // 用户名
                intent.putExtra("subjectId", course_Id);    // 学科id
                intent.putExtra("courseName", course_name);  // 学科名
                intent.putExtra("zhishidian", zhishidian);  // 知识点
                intent.putExtra("zhishidianId", zhishidianId);  // 知识点id
                startActivity(intent);

                // TODO 临时变更
//                loadZSDID();

            }
        }
    };

    // TODO 加载知识点id "dlzx230240"
//    private void loadZSDID() {
//        String mRequestUrl = "http://www.cn901.net:8111/AppServer/ajax/studentApp_getQuestionsZZXX.do?" +
//                "userId=" + userName + "&subjectId=" + course_Id + "&catalogId=" + zhishidianId;
//        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
//        StringRequest request = new StringRequest(mRequestUrl, response -> {
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//                String itemString = json.getString("data");
//                //TODO 汇报结束要进行修改
////                itemString = "[]";
//                //当试题列表为空时,需要跳转中间页进行处理
//                if (itemString.equals("[]") || itemString.equals("") || itemString.equals("null")) {
//                    Intent intent = new Intent(this, OnlineTestNullActivity.class);
//                    intent.putExtra("userName", userName);
//                    intent.putExtra("subjectId", course_Id);
//                    intent.putExtra("courseName", course_name);
//                    intent.putExtra("flag", "自主学习");
//                    startActivity(intent);
//                    finish();
//                }
//
//                Log.d("wen0501", "itemString: " + itemString);
//                Gson gson = new Gson();
//                //使用Goson框架转换Json字符串为列表
//                itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
//                questionIds = "";
//                for (int i = 0; i < itemList.size(); i++) {
//                    if (i == itemList.size() - 1) {
//                        questionIds += itemList.get(i).questionId;
//                    } else {
//                        questionIds += itemList.get(i).questionId + ",";
//                    }
//                }
//                System.out.println("questionIds:" + questionIds);
//                System.out.println("itemList:" + itemList);
//
//                String message1 = json.getString("message");
//                Log.d("song0321", "message: " + message1);
//                Alert(message1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
//        });
//        MyApplication.addRequest(request, TAG);
//    }

    // TODO 弹出提示框
//    private void Alert(String alert) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
//
//        // 自定义title样式
//        TextView tv = new TextView(this);
//        tv.setText(alert);    // 内容
//        tv.setTextSize(17); // 字体大小
//        tv.setPadding(30, 40, 30, 40); // 位置
//        tv.setTextColor(Color.parseColor("#000000")); // 颜色
//
//        // 设置title组件
//        builder.setCustomTitle(tv);
//
//        // 设置确定按钮及点击事件
//        builder.setNegativeButton("ok", (dialog, which) -> {
//            // 点击ok时执行的逻辑
//            Intent intent = new Intent(KnowledgePointActivity.this,
//                    KnowledgeShiTiDetailActivity.class);
//            intent.putExtra("userName", userName);  // 用户名
//            intent.putExtra("subjectId", course_Id);    // 学科id
//            intent.putExtra("name", course_name);  // 学科名
//            intent.putExtra("allpage", String.valueOf(itemList.size()));  // 总页数
//            intent.putExtra("questionIds", questionIds);  // 试题id
//            System.out.println("questionIds: " + questionIds);
//            intent.putExtra("pos", "1");  // 试题id数组
//            startActivity(intent);
//
//            // 关闭对话框
//            dialog.dismiss();
//        });
//
//        // 禁止返回和外部点击
//        builder.setCancelable(false);
//
//        // 对话框弹出
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


    // Java接口注入到Js中
    public class MyJavaScriptInterface {
        private Context context;

        public MyJavaScriptInterface(Context context) {
            this.context = context;
        }
        @JavascriptInterface
        public void displayHTMLContent(String str, String id) {
            // 在这里使用htmlContent进行处理，例如显示在TextView中
            // 封装消息，传递给主线程
            Message message = Message.obtain();
            zhishidian = str;
            zhishidianId = id;
            message.what = 100;
            handler.sendMessage(message);
        }
    }


    // 加载知识点
    private void loadZhiShiDian() {
        mRequestUrl = Constant.API + Constant.HOMEWORK_ADD_ZHISHIDIAN
                + "?userName=" + userName + "&subjectCode=" + course_Id +
                "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai;
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
        str = StringEscapeUtils.unescapeHtml4(str);
        System.out.println("str: " + str);

        // 正则表达式用于匹配 <label> 标签中的 onclick 属性
        String regex = "(<li><span><label[^>]*)onclick=\"[^\"]*\"";
        // 替换字符串，移除 onclick 属性
        String replacement = "$1";
        // 使用正则表达式替换，移除 onclick 属性
        String modifiedStr = str.replaceAll(regex, replacement);
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
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 16px; margin: 0px; padding: 0px\">" + modifiedStr +
                "</body>";
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保Dialog存在且显示中才去dismiss，避免空指针异常
        if (window != null && window.isShowing()) {
            window.dismiss();
        }
    }
}