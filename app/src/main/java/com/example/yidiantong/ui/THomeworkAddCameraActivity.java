package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class THomeworkAddCameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "THomeworkAddCameraActivity";

    private Map<String, String> xueduanMap = new LinkedHashMap<>();
    private Map<String, String> xuekeMap = new LinkedHashMap<>();
    private Map<String, String> banbenMap = new LinkedHashMap<>();
    private Map<String, String> jiaocaiMap = new LinkedHashMap<>();

    private int showPos = -1;
    private FlexboxLayout fl_xueduan;
    private FlexboxLayout fl_xueke;
    private FlexboxLayout fl_banben;
    private FlexboxLayout fl_jiaocai;
    private ClickableImageView iv_xueduan;
    private ClickableImageView iv_xueke;
    private ClickableImageView iv_banben;
    private ClickableImageView iv_jiaocai;

    private String mRequestUrl;

    // 选择参数
    private String xueduan = "";
    private String xueke = "";
    private String banben = "";
    private String jiaocai = "";
    private String zhishidian = "";
    private String zhishidianData = "知识点列表未获取到或者为空";
    private String zhishidianId = "";

    // 选择的标记
    private TextView lastXueduan;
    private TextView lastXueke;
    private TextView lastBanben;
    private TextView lastJiaocai;

    private View view;
    private TextView tv_xueduan;
    private TextView tv_xueke;
    private TextView tv_banben;
    private TextView tv_jiaocai;
    private TextView tv_point;
    private TextView tv_xueduan_null;
    private TextView tv_xueke_null;
    private TextView tv_banben_null;
    private TextView tv_jiaocai_null;
    private TextView tv_toPoint;

    private View contentView;
    private PopupWindow window;
    private LinearLayout ll_top;
    private ClickableImageView pop_iv_back;
    private EditText et_name;
    private EditText et_introduce;

    private SharedPreferences preferences;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_add_camera);

        // 获取组件
        ll_top = findViewById(R.id.ll_top);
        et_name = findViewById(R.id.et_name);
        et_introduce = findViewById(R.id.et_introduce);

        Button btn_confirm = findViewById(R.id.btn_confirm);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        tv_xueduan = findViewById(R.id.tv_xueduan);
        tv_xueduan.setOnClickListener(v -> iv_xueduan.callOnClick());
        tv_xueke = findViewById(R.id.tv_xueke);
        tv_xueke.setOnClickListener(v -> iv_xueke.callOnClick());
        tv_banben = findViewById(R.id.tv_banben);
        tv_banben.setOnClickListener(v -> iv_banben.callOnClick());
        tv_jiaocai = findViewById(R.id.tv_jiaocai);
        tv_jiaocai.setOnClickListener(v -> iv_jiaocai.callOnClick());
        tv_point = findViewById(R.id.tv_point);
        tv_point.setOnClickListener(this);

        tv_toPoint = findViewById(R.id.tv_toPoint);
        tv_toPoint.setOnClickListener(this);

        TextView tv_mc = findViewById(R.id.tv_mc);
        TextView tv_xd = findViewById(R.id.tv_xd);
        TextView tv_xk = findViewById(R.id.tv_xk);
        TextView tv_bb = findViewById(R.id.tv_bb);
        TextView tv_jc = findViewById(R.id.tv_jc);
        TextView tv_zsd = findViewById(R.id.tv_zsd);

        tv_xueduan_null = findViewById(R.id.tv_xueduan_null);
        tv_xueke_null = findViewById(R.id.tv_xueke_null);
        tv_banben_null = findViewById(R.id.tv_banben_null);
        tv_jiaocai_null = findViewById(R.id.tv_jiaocai_null);

        fl_xueduan = findViewById(R.id.fl_xueduan);
        fl_xueke = findViewById(R.id.fl_xueke);
        fl_banben = findViewById(R.id.fl_banben);
        fl_jiaocai = findViewById(R.id.fl_jiaocai);

        iv_xueduan = findViewById(R.id.iv_xueduan);
        iv_xueke = findViewById(R.id.iv_xueke);
        iv_banben = findViewById(R.id.iv_banben);
        iv_jiaocai = findViewById(R.id.iv_jiaocai);

        // 红星染色
        tv_mc.setText(StringUtils.getStringWithColor(tv_mc.getText().toString(), "#fb0202", 0, 1));
        tv_xd.setText(StringUtils.getStringWithColor(tv_xd.getText().toString(), "#fb0202", 0, 1));
        tv_xk.setText(StringUtils.getStringWithColor(tv_xk.getText().toString(), "#fb0202", 0, 1));
        tv_bb.setText(StringUtils.getStringWithColor(tv_bb.getText().toString(), "#fb0202", 0, 1));
        tv_jc.setText(StringUtils.getStringWithColor(tv_jc.getText().toString(), "#fb0202", 0, 1));
        tv_zsd.setText(StringUtils.getStringWithColor(tv_zsd.getText().toString(), "#fb0202", 0, 1));

        // 核心点击事件
        findViewById(R.id.iv_back).setOnClickListener(v -> {
            finish();
        });

        iv_xueduan.setOnClickListener(this);
        iv_xueke.setOnClickListener(this);
        iv_banben.setOnClickListener(this);
        iv_jiaocai.setOnClickListener(this);

        // 记住选择-本地数据读取
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        String json = preferences.getString("xueduanMap", "");
        if ("".equals(json)) {
            // 首先load学段
            loadXueDuan();
        } else {
            Gson gson = new Gson();
            Type type = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();
            xueduanMap = gson.fromJson(json, type);
            json = preferences.getString("xuekeMap", "");
            xuekeMap = gson.fromJson(json, type);
            json = preferences.getString("banbenMap", "");
            banbenMap = gson.fromJson(json, type);
            json = preferences.getString("jiaocaiMap", "");
            jiaocaiMap = gson.fromJson(json, type);
            zhishidianData = preferences.getString("zhishidianData", "");
            zhishidianId = preferences.getString("zhishidianId", "");

            xueduan = preferences.getString("xueduan", "");
            xueke = preferences.getString("xueke", "");
            banben = preferences.getString("banben", "");
            jiaocai = preferences.getString("jiaocai", "");
            zhishidian = preferences.getString("zhishidian", "");
            tv_xueduan.setText(xueduan);
            tv_xueke.setText(xueke);
            tv_banben.setText(banben);
            tv_jiaocai.setText(jiaocai);
            tv_point.setText(zhishidian);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_show_zsd, null, false);
                    // 获取组件
                    contentView.findViewById(R.id.iv_back).setOnClickListener(v -> window.dismiss());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                }
                WebView wv_content = contentView.findViewById(R.id.wv_content);
                pop_iv_back = contentView.findViewById(R.id.iv_back);
                pop_iv_back.setOnClickListener(v -> window.dismiss());
                wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv_content.getSettings().setJavaScriptEnabled(true);
                MyJavaScriptInterface jsInterface = new MyJavaScriptInterface(this);
                wv_content.addJavascriptInterface(jsInterface, "AndroidInterface");
                setHtmlOnWebView(wv_content, zhishidianData);
                window.showAsDropDown(ll_top, 0, 0);
                break;
            case R.id.btn_confirm:
                if (et_name.getText().toString().length() == 0 || xueduan.length() == 0 || xueke.length() == 0 || banben.length() == 0 || jiaocai.length() == 0 || zhishidian.length() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("必填项不完整");
                    builder.setNegativeButton("关闭", null);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                } else {
                    // 记住选择-本地写入 + Intent传参
                    SharedPreferences.Editor editor = preferences.edit();
                    Intent intent = new Intent(this, THomeworkCameraAddPickActivity.class);
                    Gson gson = new Gson();
                    // 学段
                    String json = gson.toJson(xueduanMap);
                    editor.putString("xueduanMap", json);
                    editor.putString("xueduan", xueduan);
                    intent.putExtra("xueduanMap", json);
                    intent.putExtra("xueduan", xueduan);
                    // 学科
                    json = gson.toJson(xuekeMap);
                    editor.putString("xuekeMap", json);
                    editor.putString("xueke", xueke);
                    intent.putExtra("xuekeMap", json);
                    intent.putExtra("xueke", xueke);

                    // 学科
                    json = gson.toJson(banbenMap);
                    editor.putString("banbenMap", json);
                    editor.putString("banben", banben);
                    intent.putExtra("banbenMap", json);
                    intent.putExtra("banben", banben);
                    // 教材
                    json = gson.toJson(jiaocaiMap);
                    editor.putString("jiaocaiMap", json);
                    editor.putString("jiaocai", jiaocai);
                    intent.putExtra("jiaocaiMap", json);
                    intent.putExtra("jiaocai", jiaocai);
                    // 知识点
                    editor.putString("zhishidianData", zhishidianData);
                    editor.putString("zhishidian", zhishidian);
                    editor.putString("zhishidianId", zhishidianId);
                    intent.putExtra("zhishidianData", zhishidianData);
                    intent.putExtra("zhishidian", zhishidian);
                    intent.putExtra("zhishidianId", zhishidianId);
                    editor.commit();

                    intent.putExtra("name", et_name.getText().toString());
                    intent.putExtra("introduce", et_introduce.getText().toString());
                    startActivity(intent);
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    // JS延迟关闭PopUpWindow
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                window.dismiss();
                tv_point.setText(zhishidian);
            }
        }
    };

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

    // 展开第newPos类
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
                default:
                    break;
            }
            showPos = newPos;
        } else {
            // 同时showPos为-1
            showPos = -1;
        }
    }

    /**
     * 请求批改信息 将Handler内容写入，速度更快（如遇到报错，再用Handler）
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
                    lastXueduan = null;
                    refresh(1);
                } else {
                    xueduan = tv_name.getText().toString();
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastXueduan = tv_name;
                    refresh(1);
                    loadXueKe();
                }
                tv_xueduan.setText(xueduan);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueduan.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
            tv_name.setLayoutParams(params);
            fl_xueduan.addView(view);
        });
    }

    private void loadXueKe() {
        /**
         * 加载学科方法：
         * 构建学科map<高中数学,id>
         */
        if (xueduanMap.get(xueduan) == null) {
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

    // 展现学科
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
                    lastXueke = null;
                    refresh(2);
                } else {
                    // 点击事件，获取xueke（包含学段）
                    xueke = xueduan + tv_name.getText().toString();
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
        if (xueduanMap.get(xueduan) == null || xuekeMap.get(xueke) == null) {
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
                    lastBanben = null;
                    refresh(3);
                } else {
                    banben = tv_name.getText().toString();
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastBanben = tv_name;
                    refresh(3);
                    loadJiaoCai();
                }
                tv_banben.setText(banben);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_banben.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
            tv_name.setLayoutParams(params);
            fl_banben.addView(view);
        });
    }

    private void loadJiaoCai() {

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
                    lastJiaocai = null;
                    refresh(4);
                } else {
                    jiaocai = tv_name.getText().toString();
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastJiaocai = tv_name;
                    refresh(4);
                    loadZhiShiDian();
                }
                tv_jiaocai.setText(jiaocai);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_jiaocai.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
            tv_name.setLayoutParams(params);
            fl_jiaocai.addView(view);
        });
    }

    private void loadZhiShiDian() {

        if (xuekeMap.get(xueke) == null || banbenMap.get(banben) == null || jiaocaiMap.get(jiaocai) == null) {
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
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    void refresh(int type) {
        switch (type) {
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