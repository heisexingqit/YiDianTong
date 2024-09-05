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
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnowledgePointActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgePointActivity";
    private String mRequestUrl;
    // 选择参数
    private String xueduan = "";//学段id
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
    private RelativeLayout rl_loading;
    private ClickableImageView iv_back;
    private HashMap<String, List<String>> map = new HashMap<>();
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    String questionIds = "";
    String message1 = "";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_t_homework_add_show_zsd);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        iv_back = findViewById(R.id.iv_back);

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");  //用户名
        xueduan = getIntent().getStringExtra("xueduanId"); //学段id
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
        contentView.findViewById(R.id.iv_back).setOnClickListener(v -> {
            finish();
            window.dismiss();
        });
        contentView.findViewById(R.id.tv_ok).setOnClickListener(v -> {
            jumpTo();
        });
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
        if (!window.isShowing()) finish();
    }

    // 页面跳转
    public void jumpTo() {
        // 如果没有选知识点则不跳转,弹出提示:请先选择学习章节点
        if (map.size() == 0) {
            Toast.makeText(KnowledgePointActivity.this, "请先选择考点", Toast.LENGTH_SHORT).show();
            return;
        }

        // 增加一个loading
        rl_loading.setVisibility(View.VISIBLE);
        wv_content.setVisibility(View.GONE);

        // 将map中数据转为 key:value1,value2;key:value1,value2;key:value1,value2; 的字符串格式
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            sb.append(entry.getKey()).append(":");
            for (String s : entry.getValue()) {
                sb.append(s).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(";");
        }
        Log.d("map", sb.toString());
        zhishidianId = sb.toString();

        String url = Constant.API + "/AppServer/ajax/studentApp_judgeCheck.do"
                + "?stuId=" + userName + "&channelCode=" + xueduan + "&subjectCode=" + course_Id
                + "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&catalogId=" + zhishidianId
                + "&unitId=1101010010001" + "&type=zzxx";
        Log.d("wen", "judgeCheck: " + url);
        StringRequest request = new StringRequest(url, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String result = json.getString("data");
                Log.d("wen", "result: " + result);
                Intent intent;
                if (result.equals("0")) {
                    //需要自我检测
                    intent = new Intent(KnowledgePointActivity.this,
                            OnlineTestNullActivity.class);
                } else if (result.equals("1")) {
                    //不需要检测直接进入自主学习
//                    intent = new Intent(KnowledgePointActivity.this, KnowledgeShiTiActivity.class); // 试题页面
                    intent = new Intent(KnowledgePointActivity.this,
                            KnowledgeShiTiDetailActivity.class); // 直接到做题页面
                } else {
                    //功能暂未开放
                    Toast.makeText(KnowledgePointActivity.this, "该学科自主学习功能暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("zhishidianId", zhishidianId);  // 知识点id
                intent.putExtra("userName", userName);  // 用户名
                intent.putExtra("unitId", "1101010010001");    // 学科id
                intent.putExtra("xueduanId", xueduan);    // 学科id
                intent.putExtra("subjectId", course_Id);    // 学科id
                intent.putExtra("banbenId", banben);  // 版本id
                intent.putExtra("jiaocaiId", jiaocai);  // 教材id
                intent.putExtra("courseName", course_name);  // 学科名
                intent.putExtra("zhishidian", zhishidian);  // 知识点
                intent.putExtra("flag", "自主学习");
                startActivity(intent);
                finish();
                window.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    // JS延迟关闭PopUpWindow
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                // 如果没有选知识点则不跳转,弹出提示:请先选择学习章节点
                if (zhishidianId.equals("")) {
                    Toast.makeText(KnowledgePointActivity.this, "请先选择学习章节点", Toast.LENGTH_SHORT).show();
                    return;
                }

                //跳转判断
//                String stu = getIntent().getStringExtra("stu");
                String url;
                url = Constant.API + "/AppServer/ajax/studentApp_judgeCheck.do"
                        + "?stuId=" + userName + "&channelCode=" + xueduan + "&subjectCode=" + course_Id
                        + "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&catalogId=" + zhishidianId
                        + "&unitId=1101010010001" + "&type=zzxx";
                Log.d("wen", "judgeCheck: " + url);
                StringRequest request = new StringRequest(url, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        String result = json.getString("data");
                        Log.d("wen", "result: " + result);
                        Intent intent;
                        if (result.equals("0")) {
                            //需要自我检测
                            intent = new Intent(KnowledgePointActivity.this,
                                    OnlineTestNullActivity.class);

                        } else if (result.equals("1")) {
                            //不需要检测直接进入自主学习
                            intent = new Intent(KnowledgePointActivity.this,
                                    KnowledgeShiTiActivity.class);
                        } else {
                            //功能暂未开放
                            Toast.makeText(KnowledgePointActivity.this, "该学科自主学习功能暂未开放，敬请期待", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        intent.putExtra("zhishidianId", zhishidianId);  // 知识点id
                        intent.putExtra("userName", userName);  // 用户名
                        intent.putExtra("unitId", "1101010010001");    // 学科id
                        intent.putExtra("xueduanId", xueduan);    // 学科id
                        intent.putExtra("subjectId", course_Id);    // 学科id
                        intent.putExtra("banbenId", banben);  // 版本id
                        intent.putExtra("jiaocaiId", jiaocai);  // 教材id
                        intent.putExtra("courseName", course_name);  // 学科名
                        intent.putExtra("zhishidian", zhishidian);  // 知识点
                        intent.putExtra("flag", "自主学习");
                        startActivity(intent);
                        finish();
                        window.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);

                // TODO 临时变更
//                loadZSDID();
            }
        }
    };

    // 加载知识点
    private void loadZhiShiDian() {
        rl_loading.setVisibility(View.VISIBLE);
        iv_back.setVisibility(View.VISIBLE);
        mRequestUrl = Constant.API + Constant.HOMEWORK_ADD_ZHISHIDIAN
                + "?stuId=" + userName + "&channelCode=" + xueduan + "&subjectCode=" + course_Id +
                "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&unitId=1101010010001";
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
            Log.d("syq", "displayHTMLContent: " + 1);
//            Message message = Message.obtain();
//            zhishidian = str;
//            zhishidianId = id;
//            message.what = 100;
//            handler.sendMessage(message);
        }

        @JavascriptInterface
        public void add2Map(String key, String value) {
            // 在这里处理返回的结果，例如显示在Log中
            Log.d("add2Map", "add2Map");
            Log.d("key", key);
            Log.d("value", value);
            if (map.containsKey(key)) {
                map.get(key).add(value);
            } else {
                List<String> list = new ArrayList<>();
                list.add(value);
                map.put(key, list);
            }
        }

        @JavascriptInterface
        public void remove2Map(String key, String value) {
            // 在这里处理返回的结果，例如显示在Log中
            Log.d("remove2Map", "remove2Map");
            Log.d("key", key);
            Log.d("value", value);
            if (map.containsKey(key)) {
                map.get(key).remove(value);
                if (map.get(key).size() == 0) {
                    map.remove(key);
                }
            }
        }
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

        // 正则表达式用于匹配 <label> 标签中的 onclick 属性
        String regex = "(<li><span><label[^>]*)onclick=\"[^\"]*\"";
        // 替换字符串，移除 onclick 属性
        String replacement = "$1";
        // 使用正则表达式替换，移除 onclick 属性
        String modifiedStr = str.replaceAll(regex, replacement);

        Document doc = Jsoup.parse(modifiedStr);
        // 选择 body 内的所有元素
        Element body = doc.select("body").first();
        // 创建一个新的文档，并将 body 的内容复制到新文档中
        Document newDoc = new Document("");
        newDoc.appendChild(body);
        modifiedStr = newDoc.html();
//        System.out.println("modifiedStr: " + modifiedStr);

        /*String html_content = "<head><style>" +
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
                "        AndroidInterface.displayHTMLContent(obj.textContent, obj.getAttribute(\"id\"));\n" +
                "    }\n" +
                "    function getCheckedIdsAndNames() {\n" +
                "        var checkboxes = document.querySelectorAll('input[type=\"checkbox\"]:checked');\n" +
                "        var ids = [];\n" +
                "        var names = [];\n" +
                "        for (var i = 0; i < checkboxes.length; i++) {\n" +
                "            var checkbox = checkboxes[i];\n" +
                "            var label = document.querySelector('label[id=\"' + checkbox.id + '\"]');\n" +
                "            if (label) {\n" +
                "                ids.push(checkbox.id);\n" +
                "                names.push(label.textContent.trim());\n" +
                "            }\n" +
                "        }\n" +
                "        AndroidInterface.sendCheckedIdsAndNames(ids.join(','), names.join(','));\n" +
                "    }\n" +
                "</script>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 16px; margin: 0px; padding: 0px\">" + modifiedStr +
                "</body>";*/

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
                "function _fk(obj) {\n" +
                "    AndroidInterface.displayHTMLContent(obj.textContent, obj.getAttribute(\"id\"));\n" +
                "}\n" +
                "function cataLogClick(obj) {\n" +
                "    var siblingNode = obj.nextElementSibling;\n" +
                "    if (window.getComputedStyle(siblingNode).display == 'none') {\n" +
                "        siblingNode.style.display = '';\n" +
                "    } else {\n" +
                "        siblingNode.style.display = 'none';\n" +
                "    }\n" +
                "}\n" +
                "function cataLogCheckBoxClick(obj) {\n" +
                "    obj.nextElementSibling.nextElementSibling.style.display = '';\n" +
                "    var siblingNode = obj.nextElementSibling.nextElementSibling;\n" +
                "    var lis = siblingNode.childNodes;\n" +
                "    for (var i = 0; i < lis.length; i++) {\n" +
                "        var inputNode = lis.item(i).childNodes[0];\n" +
                "        if (!inputNode || inputNode.nodeName !== 'INPUT') {\n" +
                "            continue;\n" +
                "        }\n" +
                "        var result = obj.id + ':' + inputNode.id;\n" +
                "        if (obj.checked) {\n" +
                "            inputNode.checked = true;\n" +
                "            AndroidInterface.add2Map(obj.getAttribute(\"id\"), inputNode.id);\n" +
                "        } else {\n" +
                "            inputNode.checked = false;\n" +
                "            AndroidInterface.remove2Map(obj.getAttribute(\"id\"), inputNode.id);\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "function pointCheckBoxClick(obj) {\n" +
                "    var ulNode = obj.parentElement.parentElement;\n" +
                "    var lis = ulNode.childNodes;\n" +
                "    var selectLi = 0;\n" +
                "    for (var i = 0; i < lis.length; i++) {\n" +
                "        var inputNode = lis.item(i).childNodes[0];\n" +
                "        if (!inputNode || inputNode.nodeName !== 'INPUT') {\n" +
                "            continue;\n" +
                "        }\n" +
                "        if (inputNode.checked) {\n" +
                "            selectLi++;\n" +
                "        }\n" +
                "    }\n" +
                "    var catalogNode = obj.parentElement.parentElement.previousElementSibling.previousElementSibling;\n" +
                "    if (obj.checked) {\n" +
                "        if (selectLi == lis.length) {\n" +
                "            catalogNode.checked = true;\n" +
                "        }\n" +
                "        AndroidInterface.add2Map(catalogNode.id, obj.id);\n" +
                "    } else {\n" +
                "        catalogNode.checked = false;\n" +
                "        AndroidInterface.remove2Map(catalogNode.id, obj.getAttribute(\"id\"));\n" +
                "    }\n" +
                "}\n" +
                "</script>" +
                "</head>" + modifiedStr;
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