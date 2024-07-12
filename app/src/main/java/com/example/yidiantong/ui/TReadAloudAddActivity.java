package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 朗读设置题目页
public class TReadAloudAddActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TReadAloudAddActivity";

    private EditText fet_bell_name;
    private TextView ftv_bell_class_name;
    private TextView ftv_bellclass_null;
    private FlexboxLayout ffl_bellclass;
    private String mRequestUrl;

    private Map<String, String> classMap = new HashMap<>();
    private View view;
    private List<String> bellclass = new ArrayList<>();

    // 判断内容在那里为空，-1均不为空，0标题，1班级，2内容
    private int nullmode = -1;
    // 判断对象，0全部，1全部老师，2全部学生
    private int all_tea_stu = -1;
    // 1为选中，2为未选中
    private int[] allmode = {2, 2};
    private String mRequestUrl1;


    // 朗诵内容区域和底部按钮
    private LinearLayout ll_content;  //朗诵内容区域
    private RelativeLayout ll_bottom_block; //底部按钮
    private WebView wv_content;  //朗诵内容WebView
    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncher3;
    private ActivityResultLauncher<Intent> mResultLauncherCrop; //NEW
    private Uri picUri, imageUri, cropUri;//NEW
    //html内容数据
    private String html_answer = "";
    //答题区域HTML头
    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        img{\n" +
            "        vertical-align: middle;" +
            "        max-width:40px !important;" +
            "        height:40px !important;" +
            "        }" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14px;" +
            "        }\n" +
            "    </style>\n" +
            "    <script>\n" +
            "        function bigimage(x) {\n" +
            "            myInterface.bigPic()\n" +
            "        }\n" +
            "    </script>\n" +
            "</head>\n" +
            "\n" +
            "<body onclick=\"bigimage(this)\">\n";
    //html尾
    private String html_answer_tail = "</body>";
    //点击大图
    private List<String> url_list = new ArrayList<>();
    private PopupWindow window;
    private ImagePagerAdapter adapter;
    private LinearLayout ll_answer;
    //图片编码
    private String imageBase64;
    private View contentView = null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tread_aloud);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("发布朗读任务");

        fet_bell_name = findViewById(R.id.fet_bell_name);
        ftv_bell_class_name = findViewById(R.id.ftv_bell_class_name);
        ftv_bellclass_null = findViewById(R.id.ftv_bellclass_null);
        ffl_bellclass = findViewById(R.id.ffl_bellclass);

        loadClass();

        //作答面显示
        wv_content = findViewById(R.id.wv_content);
        wv_content.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
        WebSettings webSettings = wv_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_content.addJavascriptInterface(
                new Object() {
                    @JavascriptInterface
                    @SuppressLint("JavascriptInterface")
                    public void bigPic() {
                        /**
                         * Js注册的方法无法修改主UI，需要Handler
                         */
                        Message message = Message.obtain();
                        // 发送消息给主线程
                        //标识线程
                        message.what = 101;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");

        ll_content = findViewById(R.id.ll_content);
        ll_content.setOnClickListener(this);
        ll_bottom_block = findViewById(R.id.ll_bottom_block);

        findViewById(R.id.tv_save).setOnClickListener(this);  //保存答案按钮
        findViewById(R.id.civ_camera).setOnClickListener(this);//拍照
        findViewById(R.id.civ_gallery).setOnClickListener(this);//相册
        // 注册Gallery回调组件 NEW
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
//                    picUri = intent.getData();
                    Uri uri = intent.getData();
                    if (uri != null) {
                        /**
                         * 这里做了统一化操作：创建一个output.jpg文件，并将uri写入新文件，并将picUri赋给新文件（与拍照逻辑相似）
                         * 一是为了简化方法；
                         * 二是因为适配问题，有些手机应用不能返回数据，只能与拍照类似的调用方式才行；
                         * 三是因为获取本地图片只能返回uri，而不像拍照那样可以选择写入，因此需要手动。
                         */
                        File Image = new File(getExternalCacheDir(), "output_image.jpg");
                        if (Image.exists()) {
                            Image.delete();
                        }
                        try {
                            Image.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 兼容方式获取文件Uri
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            picUri = FileProvider.getUriForFile(TReadAloudAddActivity.this,
                                    "com.example.yidiantong.fileprovider", Image);
                        } else {
                            picUri = Uri.fromFile(Image);
                        }

                        // uri写入文件Image
                        FileOutputStream outputStream = null;
                        FileInputStream inputStream = null;
                        try {
                            outputStream = new FileOutputStream(Image);
                            inputStream = (FileInputStream) getContentResolver().openInputStream(uri);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }
                            inputStream.close();
                            outputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        /**
                         * 统一化操作结束++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                         */

                        Crop(picUri); // 裁剪图片
                    }

                }
            }
        });

        // 注册Camera回调组件 NEW
        mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
//                    Intent intent = new Intent(getActivity(), DoodleActivity.class);
//                    intent.putExtra("uri", imageUri.toString());
//                    mResultLauncher3.launch(intent);
                    Crop(imageUri); // 裁剪图片
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
                    try {
                        imageBase64 = URLEncoder.encode(imageBase64, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    uploadImage();
                }
            }
        });

        /**
         * 注册通用裁切回调：与通用裁切方法对应。NEW
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    File Image = new File(getExternalCacheDir(), "output_temp.jpg");
                    imageBase64 = ImageUtils.Bitmap2StrByBase64(TReadAloudAddActivity.this, Image);
                    uploadImage();
                }
            }
        });

        // 提前创建Adapter
        adapter = new ImagePagerAdapter(this, url_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                //判断信息是否为空
                if (fet_bell_name.length() == 0) {
                    nullmode = 0;
                } else if (ftv_bell_class_name.length() == 0) {
                    nullmode = 1;
                } else if (html_answer == null || html_answer.trim().isEmpty()) {
                    nullmode = 2;
                } else {
                    nullmode = -1;
                }
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                //自定义title样式
                TextView tv1 = new TextView(this);
                if (nullmode == 0) {
                    tv1.setText("请输入标题！");    //内容
                } else if (nullmode == 1) {
                    tv1.setText("请选择班级！");    //内容
                } else if (nullmode == 2) {
                    tv1.setText("请先上传朗读内容！");    //内容
                }
                tv1.setTextSize(17);//字体大小
                tv1.setPadding(30, 40, 30, 40);//位置
                tv1.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv1);
                //禁止返回和外部点击
                builder.setCancelable(false);
                if (nullmode != -1) {
                    builder.setNegativeButton("确定", null);
                    //对话框弹出
                    builder.show();
                } else {
                    submit();
                }
                break;
            case R.id.civ_camera:
                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                permissionOpenCamera();
                break;
            case R.id.civ_gallery:
                // 打开本地存储
                permissionOpenGallery();
                break;
            case R.id.ll_content:
                if (contentView == null) {
                    if (url_list.size() == 0) break;
                    contentView = LayoutInflater.from(this).inflate(R.layout.picture_menu_new, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
//                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                    //  回显方法
                    //  回显方法
                    //  回显方法
//                    contentView.findViewById(R.id.btn_save).setOnClickListener(v -> {
//                        Log.d(TAG, "onClick: ");
//                        html_answer = html_answer.replace(originUrl, identifyUrl);
//                        wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
//
//                        transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
//                        window.dismiss();
//                    });
//                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
//                        window.dismiss();
//                    });
                    vp_pic.setAdapter(adapter);

                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);

                    adapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick() {
                            vp_pic.setCurrentItem(0);
                            window.dismiss();
                        }
                    });

                    vp_pic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            tv.setText(position + 1 + "/" + url_list.size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
                else{
                    System.out.println("我点击了一下");
                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }
                adapter.notifyDataSetChanged();
                window.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submit() {
        ll_bottom_block.setVisibility(View.GONE);
        System.out.println("html_answer:" + html_answer);

        // 保存学生答案至本地
//                String arrayString = null;
//                switch (type) {
//                    case 1:
//                        arrayString = preferences.getString("exerciseStuLoadAnswer", null);
//                        if (arrayString != null) {
//                            String[] exerciseStuLoadAnswer = arrayString.split(",");
//                            exerciseStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
//                            SharedPreferences.Editor editor = preferences.edit();
//                            arrayString = TextUtils.join(",", exerciseStuLoadAnswer);
//                            System.out.println("arrayString: " + arrayString);
//                            editor.putString("exerciseStuLoadAnswer", arrayString);
//                            editor.commit();
//                        }
//                        break;
//                    case 2:
//                        arrayString = preferences.getString("upStuLoadAnswer", null);
//                        if (arrayString != null) {
//                            String[] upStuLoadAnswer = arrayString.split(",");
//                            upStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
//                            SharedPreferences.Editor editor1 = preferences.edit();
//                            arrayString = TextUtils.join(",", upStuLoadAnswer);
//                            System.out.println("arrayString: " + arrayString);
//                            editor1.putString("upStuLoadAnswer", arrayString);
//                            editor1.commit();
//                        }
//                        break;
//                    case 3:
//                        arrayString = preferences.getString("autoStuLoadAnswer", null);
//                        if (arrayString != null) {
//                            String[] autoStuLoadAnswer = arrayString.split(",");
//                            autoStuLoadAnswer[Integer.parseInt(currentpage) - 1] = html_answer; // 数组题号对应页数-1
//                            SharedPreferences.Editor editor2 = preferences.edit();
//                            arrayString = TextUtils.join(",", autoStuLoadAnswer);
//                            System.out.println("arrayString: " + arrayString);
//                            editor2.putString("autoStuLoadAnswer", arrayString);
//                            editor2.commit();
//                        }
//                        break;
//                }

        wv_content.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);

        String className = null;
        String content = null;
        String title = null;
        String userCn = null;
        try {
            className = URLEncoder.encode(ftv_bell_class_name.getText().toString(), "UTF-8");
            content = URLEncoder.encode(getHtmlAnswer(), "UTF-8");
            title = URLEncoder.encode(fet_bell_name.getText().toString(), "UTF-8");
            userCn = URLEncoder.encode(MyApplication.cnName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder result = new StringBuilder();
        bellclass.forEach(item -> {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(classMap.get(item));
        });


        mRequestUrl1 = Constant.API + Constant.T_BELL_SAVE_MANAGE_NOTICE + "?classId=" + result + "&className=" + className + "&userName=" + MyApplication.username + "&userCN=" + userCn + "&content=" + content + "&title=" + title + "&setDateFlag=1" + "&saveOrUpdate=" + "save";
        Log.e("0110", "submit: " + mRequestUrl1);
        StringRequest request = new StringRequest(mRequestUrl1, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                if (isSuccess) {
                    builder.setTitle("布置成功");
                } else {
                    builder.setTitle("布置失败，请稍后重试");
                }
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotolast();
                    }
                });
                //禁止返回和外部点击
                builder.setCancelable(false);
                builder.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    // 返回到首页
    private void gotolast() {
        Intent toHome = new Intent(this, TMainPagerActivity.class);
        //两个一起用
        toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(toHome);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadClass() {
        mRequestUrl = Constant.API + Constant.T_BELL_ADD_CLASS + "?userName=" + MyApplication.username;
        Log.d("", "loadclass: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = data.length() - 1; i >= 0; i--) {
                    JSONObject object = data.getJSONObject(i);
                    classMap.put(object.getString("name"), object.getString("id"));
                }
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = classMap;
                //标识线程
                message.what = 100;
                handlerClass.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showClass(Map<String, String> obj) {
        classMap = obj;
        if (classMap.size() == 0) {
            ftv_bellclass_null.setVisibility(View.VISIBLE);
        }
        classMap.forEach((name, id) -> {
            view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, ffl_bellclass, false);
            TextView ftv_bell_class = view.findViewById(R.id.tv_name);
            ftv_bell_class.setText(name);
            if (bellclass.contains(name)) {
                selectedTv(ftv_bell_class);
            }
            ftv_bell_class.setOnClickListener(v -> {
                String className = (String) ftv_bell_class.getText().toString();
                if (bellclass.contains(className)) {
                    bellclass.remove(className);
                    unselectedTv(ftv_bell_class);
                } else {
                    bellclass.add(className);
                    selectedTv(ftv_bell_class);
                }
                ftv_bell_class_name.setText(String.join(",", bellclass));

            });
            ViewGroup.LayoutParams params = ftv_bell_class.getLayoutParams();
            params.width = ffl_bellclass.getWidth() / 2 - PxUtils.dip2px(view.getContext(), 15);
            ftv_bell_class.setLayoutParams(params);
            ffl_bellclass.addView(view);
        });
    }

    // 选中班级显示
    private final Handler handlerClass = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            showClass((Map<String, String>) message.obj);
        }
    };


    // 图片html格式
    private String getHtmlAnswer() {
        return html_answer_head + html_answer + html_answer_tail;
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
                url_list.add(url);
                adapter.updateData(url_list);// 关键
                html_answer += "<img onclick='bigimage(this)' src='" + url + "'>";
                wv_content.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);

//                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
//                transmit.offLoading();
            } else if (message.what == 101) {
                // 复用老代码 触发点击
                ll_answer.performClick();
            }
        }
    };

    // 图片上传
    private void uploadImage() {
//        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE;

        Map<String, String> params = new HashMap<>();
        Log.e("wen0221", "uploadImage: " + imageBase64);
//        Log.e("wen0221", "uploadImage: " + learnPlanId);
//        Log.e("wen0221", "uploadImage: " + username);
        params.put("baseCode", imageBase64);
//        params.put("leanPlanId", learnPlanId);
//        params.put("userId", username);

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                Toast.makeText(TReadAloudAddActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = url;
                // 发送消息给主线程
                //标识线程
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
//                transmit.offLoading();
                e.printStackTrace();
            }
        }, error -> {
//            transmit.offLoading();
            Log.d("wen", "Volley_Error: " + error.toString());
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
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
                .permission(Permission.Group.CAMERA, Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    // 获得权限后
                    @Override
                    public void onAction(List<String> data) {
                        openCamera();
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //判断是否点了永远拒绝，不再提示
                        if (AndPermission.hasAlwaysDeniedPermission(TReadAloudAddActivity.this, data)) {
                            new androidx.appcompat.app.AlertDialog.Builder(TReadAloudAddActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(TReadAloudAddActivity.this)
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
            imageUri = FileProvider.getUriForFile(TReadAloudAddActivity.this,
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
                        //打开相册
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        mResultLauncher.launch(intent);

//                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        intent.addCategory(Intent.CATEGORY_OPENABLE);
//                        intent.setType("image/*");
//                        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"}); // 添加特定的MIME类型以限制为图片
//                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // 设置初始目录为外部存储的图片目录
//                        mResultLauncher.launch(intent);

//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        // 选择图片文件类型
//                        intent.setType("image/*");
//                        // 跳转到本地存储
//                        mResultLauncher.launch(intent);
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
//                        // 判断是否点了永远拒绝，不再提示
//                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("权限被禁用")
//                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
//                                    .setPositiveButton("跳转", (dialog, which) -> {
//                                        AndPermission.with(HomeworkTranslationFragment.this)
//                                                .runtime()
//                                                .setting()
//                                                .start(REQUEST_CODE_STORAGE);
//                                    })
//                                    .setNegativeButton("取消", (dialog, which) -> {
//
//                                    })
//                                    .show();
//                        }
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
            new androidx.appcompat.app.AlertDialog.Builder(TReadAloudAddActivity.this)
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
            new androidx.appcompat.app.AlertDialog.Builder(TReadAloudAddActivity.this)
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
                    Toast.makeText(TReadAloudAddActivity.this, "读写文件权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(TReadAloudAddActivity.this, "读写文件权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
                    // 有对应的权限
                    Toast.makeText(TReadAloudAddActivity.this, "拍照权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(TReadAloudAddActivity.this, "拍照权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 通用裁切方法。传输、读取文件、裁切、写入文件,最终以cropUri形式显示NEW
     *
     * @param uri 裁切前的图片Uri（pic：相册；image：照片）
     */
    private void Crop(Uri uri) {

        File Image = new File(getExternalCacheDir(), "output_temp.jpg");
        if (Image.exists()) {
            Image.delete();
        }
        try {
            Image.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 兼容方式获取文件Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cropUri = FileProvider.getUriForFile(TReadAloudAddActivity.this,
                    "com.example.yidiantong.fileprovider", Image);
        } else {
            cropUri = Uri.fromFile(Image);
        }

        Intent intent = new Intent("com.android.camera.action.CROP");
        // 读写权限：要裁切需要先读取（读），后写入（写）
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // <关键>两步：目标URI转换为剪贴板数据 并设置给Intent
        ClipData clipData = ClipData.newUri(getContentResolver(), "A photo", cropUri);
        intent.setClipData(clipData);

//        // aspectX aspectY 是宽高的比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//
//        // outputX,outputY 是剪裁图片的宽高
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);

        // 设置输出文件位置和格式
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        intent.putExtra("noFaceDetection", true);

        mResultLauncherCrop.launch(intent);
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