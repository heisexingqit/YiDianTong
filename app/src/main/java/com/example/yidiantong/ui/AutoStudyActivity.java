package com.example.yidiantong.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoStudyActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AutoStudyActivity";

    private Map<String, String> xueduanMap = new LinkedHashMap<>();  // 学段
    private Map<String, String> xuekeMap = new LinkedHashMap<>();  // 学科
    private Map<String, String> banbenMap = new LinkedHashMap<>();  // 版本-哪一版
    private Map<String, String> jiaocaiMap = new LinkedHashMap<>();  // 教材

    private int showPos = -1;
    private FlexboxLayout fl_xueduan;
    private FlexboxLayout fl_xueke;
    private FlexboxLayout fl_banben;
    private FlexboxLayout fl_jiaocai;
    private ClickableImageView iv_xueduan;  // 下拉箭头
    private ClickableImageView iv_xueke;
    private ClickableImageView iv_banben;
    private ClickableImageView iv_jiaocai;

    private String mRequestUrl;

    // 选择参数
    private String xueduan = "";
    private String xueke = "";
    private String banben = "";
    private String jiaocai = "";

    // 选择的标记
    private TextView lastXueduan;
    private TextView lastXueke;
    private TextView lastBanben;
    private TextView lastJiaocai;

    private View view;
    private TextView ftv_title;
    private TextView tv_xueduan;
    private TextView tv_xueke;
    private TextView tv_banben;
    private TextView tv_jiaocai;
    private TextView tv_xueduan_null;
    private TextView tv_xueke_null;
    private TextView tv_banben_null;
    private TextView tv_jiaocai_null;

    private SharedPreferences preferences;
    private boolean loadPreference = true; // 自动填写判断，仅第一次执行


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_study);

        // 获取组件
        Button btn_select_knowledge = findViewById(R.id.btn_select_knowledge);
        btn_select_knowledge.setOnClickListener(this);
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("自主学习");
        tv_xueduan = findViewById(R.id.tv_xueduan);
        tv_xueduan.setOnClickListener(v -> iv_xueduan.callOnClick());
        tv_xueke = findViewById(R.id.tv_xueke);
        tv_xueke.setOnClickListener(v -> iv_xueke.callOnClick());
        tv_banben = findViewById(R.id.tv_banben);
        tv_banben.setOnClickListener(v -> iv_banben.callOnClick());
        tv_jiaocai = findViewById(R.id.tv_jiaocai);
        tv_jiaocai.setOnClickListener(v -> iv_jiaocai.callOnClick());

        TextView tv_xd = findViewById(R.id.tv_xd);
        TextView tv_xk = findViewById(R.id.tv_xk);
        TextView tv_bb = findViewById(R.id.tv_bb);
        TextView tv_jc = findViewById(R.id.tv_jc);

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
        tv_xd.setText(StringUtils.getStringWithColor(tv_xd.getText().toString(), "#fb0202", 0, 1));
        tv_xk.setText(StringUtils.getStringWithColor(tv_xk.getText().toString(), "#fb0202", 0, 1));
        tv_bb.setText(StringUtils.getStringWithColor(tv_bb.getText().toString(), "#fb0202", 0, 1));
        tv_jc.setText(StringUtils.getStringWithColor(tv_jc.getText().toString(), "#fb0202", 0, 1));

        // 返回点击事件
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            finish();
        });

        iv_xueduan.setOnClickListener(this);
        iv_xueke.setOnClickListener(this);
        iv_banben.setOnClickListener(this);
        iv_jiaocai.setOnClickListener(this);

        // --------------------------------//
        //  记住选择-本地数据读取
        //  改进，仅存储id和name
        //  通过id和name进行逐级判断
        //  逐级比对： 学段 学科 版本 教材 知识点
        // --------------------------------//
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        // 这里仅初始化preferences，后面进行判断时再用
        // ------------------//
        // 首先加载学段  第1步
        // ------------------//
        loadXueDuan();
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
            case R.id.btn_select_knowledge:
                if (StringUtils.hasEmptyString(xueduan, xueke, banben, jiaocai)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("必填项不完整");
                    builder.setNegativeButton("关闭", null);
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                } else {
                    // 记住选择-本地写入 + Intent传参
                    SharedPreferences.Editor editor = preferences.edit();
                    Intent intent = new Intent(this, KnowledgePointActivity.class);
                    intent.putExtra("userName", MyApplication.username);
                    // 学段
                    editor.putString("xueduan", xueduan);
                    intent.putExtra("xueduan", xueduan);
                    intent.putExtra("xueduanId", xueduanMap.get(xueduan));

                    // 学科
                    editor.putString("xueke", xueke);
                    intent.putExtra("xueke", xueke);
                    intent.putExtra("xuekeId", xuekeMap.get(xueke));

                    // 版本
                    editor.putString("banben", banben);
                    intent.putExtra("banben", banben);
                    intent.putExtra("banbenId", banbenMap.get(banben));

                    // 教材
                    editor.putString("jiaocai", jiaocai);
                    intent.putExtra("jiaocai", jiaocai);
                    intent.putExtra("jiaocaiId", jiaocaiMap.get(jiaocai));

                    editor.commit();

                    startActivity(intent);
                }
                break;
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
     * 发送请求,获取学段信息
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
                // --------------------//
                // 接着比对同步学段  第2步
                // --------------------//
                if (loadPreference) {
                    String xueduanName = preferences.getString("xueduan", "");
                    if (xueduanMap.getOrDefault(xueduanName, "").length() > 0) {
                        xueduan = xueduanName;
                        tv_xueduan.setText(xueduan);
                        // -----------------------//
                        // 同步学段完成，加载学科 第3步
                        // -----------------------//
                        loadXueKe();
                    } else {
                        loadPreference = false;
                    }
                }
//                showXueDuan();
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
//                    selectedTv(tv_name);
                    xueduan = tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastXueduan = tv_name;
                    loadXueKe();
                    refresh(1);
                }
                tv_xueduan.setText(xueduan);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueduan.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_xueduan.addView(view);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadXueKe() {
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
                    xuekeMap.put(object.getString("subjectName").substring(2), object.getString("subjectId"));
                }
                Log.d("wen", "学科: " + xuekeMap);

                // -----------------//
                // 判断同步学科 第4步
                // -----------------//
                if (loadPreference) {
                    String xuekeName = preferences.getString("xueke", "");
                    if (xuekeMap.getOrDefault(xuekeName, "").length() > 0) {
                        xueke = xuekeName;
                        tv_xueke.setText(xueke);
                        // -----------------------//
                        // 同步学段完成，加载学科 第5步
                        // -----------------------//
                        loadBanBen();
                    } else {
                        loadPreference = false;
                    }
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
            tv_name.setText(name);
            // xueke（包含学段）
            if (name.equals(xueke)) {
                selectedTv(tv_name);
                lastXueke = tv_name;
            }
            tv_name.setOnClickListener(v -> {

                if (lastXueke != null) {
                    unselectedTv(lastXueke);
                }

                if (lastXueke == tv_name) {
                    xueke = "";
                    lastXueke = null;
                    refresh(2);
                } else {
                    // 点击事件，获取xueke
                    xueke =  tv_name.getText().toString();
                    selectedTv(tv_name);
                    lastXueke = tv_name;
                    loadBanBen();
                    refresh(2);
                }

                tv_xueke.setText(xueke);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_xueke.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_xueke.addView(view);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                // -----------------//
                // 判断同步版本 第6步
                // -----------------//
                if (loadPreference) {
                    String banbenName = preferences.getString("banben", "");
                    if (banbenMap.getOrDefault(banbenName, "").length() > 0) {
                        banben = banbenName;
                        tv_banben.setText(banben);
                        // -----------------------//
                        // 同步版本完成，加载教材 第7步
                        // -----------------------//
                        loadJiaoCai();
                    } else {
                        loadPreference = false;
                    }
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
                    loadJiaoCai();
                    refresh(3);
                }
                tv_banben.setText(banben);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_banben.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_banben.addView(view);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                // -----------------//
                // 判断同步教材 第8步
                // -----------------//
                if (loadPreference) {
                    String jiaocaiName = preferences.getString("jiaocai", "");
                    if (jiaocaiMap.getOrDefault(jiaocaiName, "").length() > 0) {
                        jiaocai = jiaocaiName;
                        tv_jiaocai.setText(jiaocai);
                        // -----------------------//
                        // 同步教材完成，全部同步完成
                        // -----------------------//
                        loadPreference = false;
                    }
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
                }
                tv_jiaocai.setText(jiaocai);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_jiaocai.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_jiaocai.addView(view);
        });
    }

    // 刷新文本的内容
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
        }
    }

    // 选中
    private void selectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_select);
        tv.setTextColor(getColor(R.color.red));
    }

    // 取消选中
    private void unselectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_unselect);
        tv.setTextColor(getColor(R.color.default_gray));
    }
}