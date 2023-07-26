package com.example.yidiantong.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class THomeworkCameraAddPickActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "THomeworkCameraAddPickA";

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

    // 标记上传图的显示位置
    private int picUploadPos;
    private String picUploadType;

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

        // 列表
        rv_main = findViewById(R.id.rv_main);

        //RecyclerView两步必要配置

        layoutManager = new CustomLinearLayoutManager(this);

        rv_main.setLayoutManager(layoutManager);

        rv_main.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        if (divider == null) {
            divider = new MyItemDecoration(this, DividerItemDecoration.VERTICAL, false);
            divider.setDrawable(this.getResources().getDrawable(R.drawable.divider_deep));
        }
        rv_main.addItemDecoration(divider);

        adapter = new THomeworkCameraRecyclerAdapter(this, itemList);
        adapter.setManager(layoutManager);
        rv_main.setAdapter(adapter);

        // 记住选择-本地数据读取
        // 获取本地数据

        Intent intent = getIntent();
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedHashMap<String, String>>() {
        }.getType();
        String json = intent.getStringExtra("xueduanMap");
        xueduanMap = gson.fromJson(json, type);
        json = intent.getStringExtra("xuekeMap");
        xuekeMap = gson.fromJson(json, type);
        json = intent.getStringExtra("banbenMap");
        banbenMap = gson.fromJson(json, type);
        json = intent.getStringExtra("jiaocaiMap");
        jiaocaiMap = gson.fromJson(json, type);
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

                THomeworkCameraItem item = itemList.get(picUploadPos);
                String addString = "<p><img src=\"" + url + "\"></p>";
                switch (picUploadType) {
                    case "Show":
                        if (item.getShitiShow() != null) {
                            item.setShitiShow(item.getShitiShow() + addString);
                        }else{
                            item.setShitiShow(addString);
                        }
                        break;
                    case "Answer":
                        if (item.getShitiAnswer() != null) {
                            item.setShitiAnswer(item.getShitiAnswer() + addString);
                        }else{
                            item.setShitiAnswer(addString);
                        }
                        break;
                    case "Analysis":
                        if (item.getShitiAnalysis() != null) {
                            item.setShitiAnalysis(item.getShitiAnalysis() + addString);
                        }else{
                            item.setShitiAnalysis(addString);
                        }
                        break;
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
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
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_question:
                showTypeMenu();
                break;
            case R.id.btn_save:
                submit();
                break;
            case R.id.btn_assign:
                break;

        }
    }

    private void submit() {
        for (int i = 0; i < itemList.size(); ++i) {
            THomeworkCameraItem item = itemList.get(i);
            item.setOrder(String.valueOf(i + 1));
        }
        StringBuilder jsonStringBuilder = new StringBuilder();
        String jsonStr = "[";
        for (int i = 0; i < itemList.size(); ++i) {
            THomeworkCameraItem item = itemList.get(i);
            item.setOrder(String.valueOf(i + 1));
            if (jsonStringBuilder.length() > 0) {
                jsonStringBuilder.append(", ");
            }
            jsonStringBuilder.append(item.toData());
        }
        jsonStr += jsonStringBuilder.toString();
        jsonStr += "]";
        try {
            mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_SAVE + "?userName=" + MyApplication.username + "&paperName=" + URLEncoder.encode(name, "UTF-8")
                    + "&paperId=" + paperId
                    + "&jsonStr=" + URLEncoder.encode(jsonStr, "UTF-8") + "&channelCode=" + xueduanMap.get(xueduan)
                    + "&subjectCode=" + xuekeMap.get(xueke) + "&textBookCode=" + banbenMap.get(banben) + "&gradeLevelCode=" + jiaocaiMap.get(jiaocai)
                    + "&pointCode=" + zhishidianId + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") + "&subjectName=" + URLEncoder.encode(xueduan, "UTF-8")
                    + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8")
                    + "&pointName=" + URLEncoder.encode(zhishidian, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d("wen", "submit: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                }
                Log.d("wen", "提交返回值 " + data);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void showTypeMenu() {
        if (typeView == null) {
            typeView = LayoutInflater.from(this).inflate(R.layout.menu_t_homework_camera_type, null, false);
            fl_type = typeView.findViewById(R.id.fl_type);

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

                            // 构建试题对象
                            THomeworkCameraItem newHomework = new THomeworkCameraItem();
                            newHomework.setTypeName(type);

                            for (THomeworkCameraType item : typeList) {
                                if (item.getTypeName().equals(type)) {
                                    newHomework.setBaseTypeId(item.getBaseTypeId());
                                    newHomework.setTypeId(item.getTypeId());
                                }
                            }
                            newHomework.setAnswerNum("4");
                            newHomework.setScore(et_score.getText().toString());
                            itemList.add(newHomework);
                            getQuestionId(itemList.size() - 1);
                            adapter.setShowPos(itemList.size() - 1);

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

                    if (type.equals(tv_name.getText().toString())) {
                        return;
                    }

                    type = tv_name.getText().toString();

                    if (lastType != null) {
                        lastType.setBackgroundResource(R.color.light_gray3);
                    }

                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);

                    lastType = tv_name;

                    ll_score.setVisibility(View.VISIBLE);
                    et_score.setText(item.getScore());
                });

                fl_type.addView(view);

            }

            typeWindow = new PopupWindow(typeView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            typeWindow.setTouchable(true);
        }

        ll_score.setVisibility(View.GONE);
        if (lastType != null) {
            lastType.setBackgroundResource(R.color.light_gray3);
            type = "";
            lastType = null;
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
        Log.d("wen", "type: " + mRequestUrl);
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