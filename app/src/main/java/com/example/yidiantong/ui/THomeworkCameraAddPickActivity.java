package com.example.yidiantong.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.THomeworkCameraRecyclerAdapter;
import com.example.yidiantong.bean.THomeworkCameraItem;
import com.example.yidiantong.bean.THomeworkCameraType;
import com.example.yidiantong.fragment.THomeworkCameraSingleFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;
import com.google.android.flexbox.FlexboxLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class THomeworkCameraAddPickActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "THomeworkCameraAddPickActivity";

    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncher3;
    private Uri picUri, imageUri;

    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    //图片编码
    private String imageBase64;

    private View typeView;
    private PopupWindow typeWindow;

    private String type = "";
    private TextView lastType;
    private LinearLayout ll_score;
    private EditText et_score;

    // 后端请求URL
    private String mRequestUrl;

    // Intent获取数据
    private String xueduan = "";
    private String xueke = "";
    private String banben = "";
    private String jiaocai = "";
    private String zhishidian = "";
    private String zhishidianData = "知识点列表未获取到或者为空";
    private String zhishidianId = "";
    private String name;
    private String introduce;
    private String paperId = "-1";
    private Map<String, String> xueduanMap = new LinkedHashMap<>();
    private Map<String, String> xuekeMap = new LinkedHashMap<>();
    private Map<String, String> banbenMap = new LinkedHashMap<>();
    private Map<String, String> jiaocaiMap = new LinkedHashMap<>();
    private Map<String, String> typeMap = new LinkedHashMap<>();
    private FlexboxLayout fl_type;
    private List<THomeworkCameraType> typeList;

    // 核心数据List
    private List<THomeworkCameraItem> itemList = new ArrayList<>();

    // RecyclerView 相关
    private RecyclerView rv_main;
    private MyItemDecoration divider;
    private THomeworkCameraRecyclerAdapter adapter;

    private ImageView iv_empty;
    public CustomLinearLayoutManager layoutManager;

    // 修改标记
    private boolean changeFlag = false;
    // 标记上传图的显示位置
    private int picUploadPos;
    private String picUploadType;
    private LinearLayout ll_hide;
    private LinearLayout ll_divide_hide;
    private EditText et_num;

    private ScrollView sv_main;

    // 类型修改
    private boolean isTypeChange;
    private int typeChangePos;

    // 滚动控制器
    public class CustomLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CustomLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean scrollEnabled) {
            isScrollEnabled = scrollEnabled;
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thomework_camera_add_pick);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_assign).setOnClickListener(this);
        findViewById(R.id.iv_add_question).setOnClickListener(this);

        sv_main = findViewById(R.id.sv_main);

        // 列表
        rv_main = findViewById(R.id.rv_main);

        //RecyclerView两步必要配置

        layoutManager = new CustomLinearLayoutManager(this);

        rv_main.setLayoutManager(layoutManager);

        rv_main.setItemAnimator(new DefaultItemAnimator());

        layoutManager.setScrollEnabled(false);

        //添加间隔线
        if (divider == null) {
            divider = new MyItemDecoration(this, DividerItemDecoration.VERTICAL, false);
            divider.setDrawable(this.getResources().getDrawable(R.drawable.divider_deep));
        }
        rv_main.addItemDecoration(divider);

        adapter = new THomeworkCameraRecyclerAdapter(this, itemList);
        rv_main.setAdapter(adapter);

        // 记住选择-本地数据读取
        // 获取本地数据

        Intent intent = getIntent();
        Gson gson = new Gson();
        Type mtype = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        String json = intent.getStringExtra("xueduanMap");
        xueduanMap = gson.fromJson(json, mtype);
        json = intent.getStringExtra("xuekeMap");
        xuekeMap = gson.fromJson(json, mtype);
        json = intent.getStringExtra("banbenMap");
        banbenMap = gson.fromJson(json, mtype);
        json = intent.getStringExtra("jiaocaiMap");
        jiaocaiMap = gson.fromJson(json, mtype);
        zhishidianData = intent.getStringExtra("zhishidianData");
        zhishidianId = intent.getStringExtra("zhishidianId");

        xueduan = intent.getStringExtra("xueduan");
        xueke = intent.getStringExtra("xueke");
        banben = intent.getStringExtra("banben");
        jiaocai = intent.getStringExtra("jiaocai");
        zhishidian = intent.getStringExtra("zhishidian");

        name = intent.getStringExtra("name");
        introduce = intent.getStringExtra("introduce");

        loadType();

        iv_empty = findViewById(R.id.iv_empty);

        adapter.setmItemClickListener(new THomeworkCameraRecyclerAdapter.MyItemClickListener() {
            @Override
            public void openGallery(View view, int pos, String type) {
                picUploadPos = pos;
                picUploadType = type;
                // 打开本地存储
                permissionOpenGallery();
            }

            @Override
            public void openCamera(View view, int pos, String type) {
                picUploadPos = pos;
                picUploadType = type;
                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                permissionOpenCamera();

            }

            @Override
            public void showHideEmpty(Boolean isHide) {
                if (isHide) {
                    iv_empty.setVisibility(View.GONE);
                } else {
                    iv_empty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setChangeFlag(boolean isChange) {
                changeFlag = isChange;
            }

            @Override
            public ScrollView getParentScrollView() {
                return sv_main;
            }

            @Override
            public void changeType(int pos) {
                type = itemList.get(pos).getTypeName();
                isTypeChange = true;
                typeChangePos = pos;
                showTypeMenu();
            }
        });

        // 注册Gallery回调组件
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
                    picUri = intent.getData();
                    if (picUri != null) {
                        /*Gallery回调执行*/
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(picUri));
                            imageBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                            imageBase64 = imageBase64.replace("+", "%2b");
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
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = new Intent(THomeworkCameraAddPickActivity.this, DoodleActivity.class);
                    intent.putExtra("uri", imageUri.toString());
                    mResultLauncher3.launch(intent);
                }
            }
        });

        // 注册 涂鸦板
        mResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    imageBase64 = intent.getStringExtra("data");
                    imageBase64 = imageBase64.replace("+", "%2b");
                    uploadImage();
                }
            }
        });

    }

    private void uploadImage() {
//        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE + "?baseCode=" + imageBase64 + "&leanPlanId=" + itemList.get(picUploadPos).getQuestionId() + "&userId=" + MyApplication.username
                + "&type=" + picUploadType;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();

                if (isSuccess) {
                    changeFlag = true;
                    THomeworkCameraItem item = itemList.get(picUploadPos);
                    String addString = "<p><img src=\"" + url + "\"></p>";
                    switch (picUploadType) {
                        case "Show":
                            if (item.getShitiShow() != null) {
                                item.setShitiShow(item.getShitiShow() + addString);
                            } else {
                                item.setShitiShow(addString);
                            }
                            break;
                        case "Answer":
                            if (item.getShitiAnswer() != null) {
                                item.setShitiAnswer(item.getShitiAnswer() + addString);
                            } else {
                                item.setShitiAnswer(addString);
                            }
                            break;
                        case "Analysis":
                            if (item.getShitiAnalysis() != null) {
                                item.setShitiAnalysis(item.getShitiAnalysis() + addString);
                            } else {
                                item.setShitiAnalysis(addString);
                            }
                            break;
                    }
                    adapter.notifyDataSetChanged();
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
    private void loadType() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_GET_TYPE + "?subjectId=" + xuekeMap.get(xueke);
        Log.d("wen", "type: " + mRequestUrl);
        typeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                Gson gson = new Gson();
                typeList = gson.fromJson(data, new com.google.gson.reflect.TypeToken<List<THomeworkCameraType>>() {
                }.getType());

                Log.d("wen", "类型: " + typeList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_question:
                isTypeChange = false;
                showTypeMenu();
                break;
            case R.id.btn_save:
                submit(false);
                break;
            case R.id.btn_assign:
                assign();
                break;

            // 类型Menu相关

        }
    }

    private void assign() {
        if (changeFlag == false && !paperId.equals("-1")) {
            Intent intent = new Intent(this, TTeachAssginActivity.class);
            intent.putExtra("learnPlanId", paperId);
            intent.putExtra("learnPlanName", name);
            intent.putExtra("type", "paper");
            intent.putExtra("typeCamera", "true");
            startActivity(intent);
        } else {
            submit(true);
        }
    }

    private void submit(boolean isAssign) {
        if (itemList.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("请添加试题");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
        }

        StringBuilder jsonStringBuilder = new StringBuilder();
        String jsonStr = "[";
        for (int i = 0; i < itemList.size(); ++i) {
            THomeworkCameraItem item = itemList.get(i);

            // 非法判断
            if (item.getShitiShow() == null || item.getShitiShow().length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请填写第" + (i + 1) + "题面");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            } else if (item.getShitiAnswer() == null || item.getShitiAnswer().length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请填写第" + (i + 1) + "答案");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }

            // 题型格式判断
            boolean isLegal = true;
            switch (item.getBaseTypeId()) {
                case "101":
                    if (item.getShitiAnswer().length() != 1) {
                        isLegal = false;
                    }
                    char ans = item.getShitiAnswer().charAt(0);
                    if (ans < 'A' || ans >= 'A' + Integer.parseInt(item.getAnswerNum())) {
                        isLegal = false;
                    }
                    break;

                case "102":
                    Set<Character> charSet = new HashSet<>();
                    for (char c : item.getShitiAnswer().toCharArray()) {
                        if (!charSet.add(c)) {
                            isLegal = false;
                        }
                    }
                    for (int j = 0; j < item.getShitiAnswer().length() - 1; i++) {
                        char currentChar = item.getShitiAnswer().charAt(i);
                        char nextChar = item.getShitiAnswer().charAt(i + 1);

                        // 检查当前字符和下一个字符是否按顺序排布
                        if (currentChar > nextChar) {
                            isLegal = false;
                        }

                        if (currentChar < 'A' || currentChar >= 'A' + Integer.parseInt(item.getAnswerNum())) {
                            isLegal = false;
                        }

                        if (nextChar < 'A' || nextChar >= 'A' + Integer.parseInt(item.getAnswerNum())) {
                            isLegal = false;
                        }
                    }
                case "108":
                    if (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1) {
                        String[] strings = item.getShitiAnswer().split(",");
                        if (strings.length != Integer.parseInt(item.getSmallQueNum())) {
                            isLegal = false;
                        }
                        for (int j = 0; j < strings.length; ++j) {
                            String str = strings[j];
                            if (str.length() != 1) {
                                isLegal = false;
                            }
                            if (strings[j].charAt(0) < 'A' || strings[j].charAt(0) > 'D') {
                                isLegal = false;
                            }
                        }
                    } else if (item.getTypeName().indexOf("七选五") != -1) {
                        String[] strings = item.getShitiAnswer().split(",");
                        if (strings.length != 5) {
                            isLegal = false;
                        }
                        for (int j = 0; j < strings.length; ++j) {
                            String str = strings[j];
                            if (str.length() != 1) {
                                isLegal = false;
                            }
                            if (strings[j].charAt(0) < 'A' || strings[j].charAt(0) > 'G') {
                                isLegal = false;
                            }
                        }
                    }
                    break;
            }
            if (!isLegal) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("第" + (i + 1) + "题答案不完整或不合法");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }


            item.setOrder(String.valueOf(i + 1));
            if (jsonStringBuilder.length() > 0) {
                jsonStringBuilder.append(", ");
            }
            jsonStringBuilder.append(item.toData());
        }
        jsonStr += jsonStringBuilder.toString();
        jsonStr += "]";

        // 知识点参数实际是属性拼接
        String zsdLongString = banben + "/" + jiaocai + "/" + zhishidian + "/";
        try {
            mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_SAVE + "?userName=" + MyApplication.username + "&paperName=" + URLEncoder.encode(name, "UTF-8")
                    + "&paperId=" + paperId
                    + "&jsonStr=" + URLEncoder.encode(jsonStr, "UTF-8") + "&channelCode=" + xueduanMap.get(xueduan)
                    + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" + jiaocaiMap.get(jiaocai)
                    + "&pointCode=" + zhishidianId + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") + "&subjectName=" + URLEncoder.encode(xueduan, "UTF-8")
                    + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8")
                    + "&pointName=" + URLEncoder.encode(zsdLongString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("wen", "submit: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    paperId = json.getString("data");
                    changeFlag = false;
                    if (isAssign) {
                        assign();
                    } else {
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d("wen", "提交返回值:试卷Id： " + paperId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void showTypeMenu() {
        if (typeView == null) {
            typeView = LayoutInflater.from(this).inflate(R.layout.menu_t_homework_camera_type, null, false);
            fl_type = typeView.findViewById(R.id.fl_type);
            ll_hide = typeView.findViewById(R.id.ll_hide);
            ll_divide_hide = typeView.findViewById(R.id.ll_divide_hide);
            TextView tv_score = typeView.findViewById(R.id.tv_score);
            et_num = typeView.findViewById(R.id.et_num);

            //绑定点击事件
            View.OnClickListener typeOnclickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.tv_cancel:
                            typeWindow.dismiss();
                            break;
                        case R.id.tv_confirm:
                            if (type.length() == 0) {
                                Toast.makeText(THomeworkCameraAddPickActivity.this, "请选择题型", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if (et_score.getText().toString().trim().length() == 0) {
                                Toast.makeText(THomeworkCameraAddPickActivity.this, "请输入试题分数", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            Log.d("wen", "测试1: " + isTypeChange);

                            THomeworkCameraItem newHomework;
                            if (isTypeChange) {
                                newHomework = itemList.get(typeChangePos);
                                newHomework.setAnswerNum("-1");
                                newHomework.setSmallQueNum("-1");

                            } else {
                                newHomework = new THomeworkCameraItem();
                            }

                            // 构建试题对象
                            newHomework.setTypeName(type);

                            for (THomeworkCameraType item : typeList) {
                                if (item.getTypeName().equals(type)) {
                                    newHomework.setBaseTypeId(item.getBaseTypeId());
                                    newHomework.setTypeId(item.getTypeId());
                                }
                            }
                            // 构建参数
                            String strAnswerNum = et_num.getText().toString();
                            String strAnswerScore = et_score.getText().toString();
                            if (newHomework.getBaseTypeId().equals("101") || newHomework.getBaseTypeId().equals("102")) {
                                newHomework.setAnswerNum("4");
                                newHomework.setScore(et_score.getText().toString());
                            } else if (newHomework.getBaseTypeId().equals("108") && (newHomework.getTypeName().indexOf("阅读理解") != -1 || newHomework.getTypeName().indexOf("完形填空") != -1)) {
                                newHomework.setSmallQueNum(strAnswerNum);
                                newHomework.setScore(String.valueOf(Integer.parseInt(strAnswerNum) * Integer.parseInt(strAnswerScore)));
                            } else {
                                newHomework.setScore(et_score.getText().toString());
                            }

                            if (!isTypeChange) {
                                itemList.add(newHomework);
                                getQuestionId(itemList.size() - 1);
                                adapter.setShowPos(itemList.size() - 1);
                            } else {
                                adapter.setShowPos(typeChangePos);
                            }

                            Log.d("wen", "测试2: " + isTypeChange);
                            adapter.notifyDataSetChanged();
                            typeWindow.dismiss();
                            break;
                    }
                }
            };
            typeView.findViewById(R.id.tv_cancel).setOnClickListener(typeOnclickListener);
            typeView.findViewById(R.id.tv_confirm).setOnClickListener(typeOnclickListener);

            et_score = typeView.findViewById(R.id.et_score);
            ll_score = typeView.findViewById(R.id.ll_score);

            for (int i = 0; i < typeList.size(); ++i) {

                THomeworkCameraType item = typeList.get(i);

                View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_camera_block, fl_type, false);
                TextView tv_name = view.findViewById(R.id.tv_name);
                tv_name.setText(item.getTypeName());

                tv_name.setOnClickListener(v -> {
                    type = tv_name.getText().toString();

                    if (lastType != null) {
                        lastType.setBackgroundResource(R.color.light_gray3);
                    }

                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);

                    lastType = tv_name;

                    ll_score.setVisibility(View.VISIBLE);
                    tv_score.setText("分值");
                    if (item.getBaseTypeId().equals("108") && item.getTypeName().indexOf("阅读理解") != -1) {
                        ll_hide.setVisibility(View.VISIBLE);
                        ll_divide_hide.setVisibility(View.VISIBLE);
                        tv_score.setText("每道子题面预设分值");
                        et_num.setText("5");
                        et_score.setText(String.valueOf(Integer.parseInt(item.getScore()) / 5));
                    } else if (item.getBaseTypeId().equals("108") && item.getTypeName().indexOf("完形填空") != -1) {
                        ll_hide.setVisibility(View.VISIBLE);
                        ll_divide_hide.setVisibility(View.VISIBLE);
                        tv_score.setText("每道子题面预设分值");
                        et_num.setText("10");
                        et_score.setText(String.valueOf(Integer.parseInt(item.getScore()) / 10));
                    } else {
                        ll_hide.setVisibility(View.GONE);
                        ll_divide_hide.setVisibility(View.GONE);
                        et_score.setText(item.getScore());
                    }
                });
                fl_type.addView(view);
            }
            typeWindow = new PopupWindow(typeView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            typeWindow.setTouchable(true);
        }

        ll_score.setVisibility(View.GONE);
        ll_hide.setVisibility(View.GONE);
        ll_divide_hide.setVisibility(View.GONE);

        if (lastType != null) {
            lastType.setBackgroundResource(R.color.light_gray3);
            if (!isTypeChange) {
                type = "";
            }
            lastType = null;
        }

        Log.d("wen", "showTypeMenu: " + isTypeChange);
        if (isTypeChange) {
            // 同步type
            for (int j = 0; j < fl_type.getChildCount(); j++) {
                View child = (View) fl_type.getChildAt(j);
                TextView tv_name = child.findViewById(R.id.tv_name);
                if (tv_name.getText().toString().equals(type)) {
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastType = tv_name;
                }
            }
            THomeworkCameraItem item = itemList.get(typeChangePos);
            if (item.getBaseTypeId().equals("108") && (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1)) {
                et_score.setText(String.valueOf(Integer.parseInt(item.getScore()) / Integer.parseInt(item.getSmallQueNum())));
                et_num.setText(item.getSmallQueNum());
            }
        }
        typeWindow.showAtLocation(typeView, Gravity.CENTER, 0, 0);
    }

    private void getQuestionId(int pos) {

        try {
            mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_GET_ID + "?userName=" + MyApplication.username + "&channelCode=" + xueduanMap.get(xueduan)
                    + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" + jiaocaiMap.get(jiaocai)
                    + "&pointCode=" + zhishidianId + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") + "&subjectName=" + URLEncoder.encode(xueduan, "UTF-8")
                    + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8")
                    + "&pointName=" + URLEncoder.encode(zhishidian, "UTF-8") + "&typeId=" + itemList.get(pos).getTypeId() + "&typeName="
                    + URLEncoder.encode(itemList.get(pos).getTypeName(), "UTF-8") + "&baseTypeId=" + URLEncoder.encode(itemList.get(pos).getBaseTypeId(), "UTF-8")
                    + "&score=" + et_score.getText();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        typeMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                Log.d(TAG, "getQuestionId: " + data);
                itemList.get(pos).setQuestionId(data);

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
                        if (AndPermission.hasAlwaysDeniedPermission(THomeworkCameraAddPickActivity.this, data)) {
                            new AlertDialog.Builder(THomeworkCameraAddPickActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(THomeworkCameraAddPickActivity.this)
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
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
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
                        if (AndPermission.hasAlwaysDeniedPermission(THomeworkCameraAddPickActivity.this, data)) {
                            new AlertDialog.Builder(THomeworkCameraAddPickActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(THomeworkCameraAddPickActivity.this)
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

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。拍照申请
     */
    private Rationale rCamera = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(THomeworkCameraAddPickActivity.this)
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
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(THomeworkCameraAddPickActivity.this)
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
                    Toast.makeText(this, "读写文件权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(this, "读写文件权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
                    // 有对应的权限
                    Toast.makeText(this, "拍照权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(this, "拍照权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}