package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BookExerciseAdapterW;
import com.example.yidiantong.adapter.BookExerciseAdapterW2Three;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookExercise2ThreeActivity extends AppCompatActivity {
    private static final String TAG = "BookExercise2ThreeActivity";
    private RecyclerView rv_main;
    private BookExerciseAdapterW2Three adapter;
    private String questionId;
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;
    private String imageBase64;
    private Uri picUri, imageUri, cropUri;
    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private List<BookExerciseEntity> moreList;
    private int pos_iamge; // 图片interface数据
    private WebView wv_image; // 图片interface数据
    private LinearLayout ll_image; // 图片interface数据

    // 新的接口数据
    private int currentPage = 0;
    private int recommendNum = -1;
    private int searchNum = -1;
    private String qIds = "";
    private String qDbids = "";
    private RelativeLayout rl_submitting;
    private String userName; //用户名
    private String course_Id;  //学科id
    private String course_name;  //学科名
    private String questionIdd; //举一反三题目id
    private TextView tv_knowledge_name;//原试题考点

    //答题区域HTML头
    private String html_head = "<head>\n" +
            "    <style>\n" +
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_exercise2three);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        questionId = getIntent().getStringExtra("questionId");
        Log.e("wen0314", "onCreate: " + questionId);
        Log.e("wen0314", "BookExerciseActivity:提分练习 " );

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.ll_refresh).setOnClickListener(v -> loadItems_Net());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName"); //用户名
        course_Id = getIntent().getStringExtra("subjectId");  //学科id
        course_name = getIntent().getStringExtra("courseName");  //学科名
        questionIdd = getIntent().getStringExtra("questionId"); //举一反三题目id

        //获取组件
        tv_knowledge_name = findViewById(R.id.tv_knowledge_name);
        rv_main = findViewById(R.id.rv_main);
        rl_submitting = findViewById(R.id.rl_submitting);
        //RecyclerView两步必要配置
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());
        //设置RecyclerViewAdapter
        if (adapter == null) {
            adapter = new BookExerciseAdapterW2Three(this, new ArrayList<>());
            adapter.setHandler(handler);
        }
        rv_main.setAdapter(adapter);
        loadItems_Net();

        BookExerciseAdapterW2Three.ExerciseInterface myInterface = new BookExerciseAdapterW2Three.ExerciseInterface() {
            @Override
            public void openDrawCamera(int pos, WebView wb, LinearLayout ll) {
                pos_iamge = pos;
                wv_image = wb;
                ll_image = ll;
                permissionOpenCamera();
            }

            @Override
            public void openDrawGallery(int pos, WebView wb, LinearLayout ll) {
                pos_iamge = pos;
                wv_image = wb;
                ll_image = ll;
                permissionOpenGallery();
            }
        };
        adapter.setOnclickListener(myInterface);


        // 拍照和相册copy代码=============================
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
                            picUri = FileProvider.getUriForFile(BookExercise2ThreeActivity.this,
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

        /**
         * 注册通用裁切回调：与通用裁切方法对应。NEW
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    File Image = new File(getExternalCacheDir(), "output_temp.jpg");
                    imageBase64 = ImageUtils.Bitmap2StrByBase64(BookExercise2ThreeActivity.this, Image);
                    uploadImage();
                }
            }
        });
        img_adapter = new ImagePagerAdapter(this, url_list);

    }


    private void loadItems_Net() {

        String mRequestUrl = Constant.API + Constant.GET_JUYIFANSAN + "?userId=" + userName + "&questionId="
                + questionIdd + "&currentPage=" + currentPage;
        Log.e("wen0524", "loadItems_Net: " + mRequestUrl);

        StringRequest stringRequest = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.e("wen0524", "json: " + json);

                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);
                String[] split = message1.split("@_@");
//                Alert(split[0]);
                //去掉末尾的逗号
                if (split[1].endsWith(",")) {
                    split[1] = split[1].substring(0, split[1].length() - 1);
                }
                tv_knowledge_name.setText("原试题考点: " + split[1]);

                String itemString = json.getString("data");
                // 新数据同步
//                if (currentPage == 0) {
//                    recommendNum = data.getInt("recommendNum");
//                    searchNum = data.getInt("searchNum");
//                    qIds = data.getString("qIds");
//                    qDbids = data.getString("qDbids");
//                }
                currentPage += 1;
                Log.e("wen0524", "itemString: " + itemString);

                Gson gson = new Gson();
                // 使用Goson框架转换Json字符串为列表
                moreList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {
                }.getType());
                Log.e("wen0524", "moreList: " + moreList.size());

                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = moreList;
                // 发送消息给主线程
                //标识线程
                message.what = 101;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            //加载失败
            Log.e("volley", "loadItems_Net: ");
        });

        MyApplication.addRequest(stringRequest, "TAG");

    }
    private void Alert(String alert) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_LIGHT);
        //自定义title样式
        TextView tv = new TextView(this);
        tv.setText(alert);    //内容
        tv.setTextSize(17);//字体大小
        tv.setPadding(30, 40, 30, 40);//位置
        tv.setTextColor(Color.parseColor("#000000"));//颜色
        //设置title组件
        builder.setCustomTitle(tv);
        android.app.AlertDialog dialog = builder.create();
        builder.setNegativeButton("ok", null);
        //禁止返回和外部点击
        builder.setCancelable(false);
        //对话框弹出
        builder.show();
    }

    private String exercise_stu_html;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
//                adapter.updateData(url_list);// 关键
                moreList.get(pos_iamge).stuHtml += "<img onclick='bigimage(this)' src='" + url + "' style=\"max-width:80px\">";
                wv_image.loadDataWithBaseURL(null, html_head+moreList.get(pos_iamge).stuHtml, "text/html", "utf-8", null);
                ll_image.setVisibility(View.VISIBLE);
//                transmit.offLoading();
            }else if(message.what==101){
                List<BookExerciseEntity> moreList = (List<BookExerciseEntity>) message.obj;
                adapter.update(moreList);
                adapter.notifyDataSetChanged();

            }else if (message.what == 102) {
                // 复用老代码 触发点击
                exercise_stu_html = (String) message.obj;
                show_photo();
            }

        }
    };

    // 图片上传
    private void uploadImage() {
//        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE;

        Map<String, String> params = new HashMap<>();
        Log.e("wen0221", "uploadImage: " + imageBase64);
        params.put("baseCode", imageBase64);
        params.put("leanPlanId", "");
        params.put("userId", MyApplication.username);

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                Toast.makeText(this, json.getString("message"), Toast.LENGTH_SHORT).show();

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
        }) {
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
                        if (AndPermission.hasAlwaysDeniedPermission(BookExercise2ThreeActivity.this, data)) {
                            new AlertDialog.Builder(BookExercise2ThreeActivity.this)
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(BookExercise2ThreeActivity.this)
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
            new AlertDialog.Builder(BookExercise2ThreeActivity.this)
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
            new AlertDialog.Builder(BookExercise2ThreeActivity.this)
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
            cropUri = FileProvider.getUriForFile(BookExercise2ThreeActivity.this,
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void show_rl_submitting(){
        rl_submitting.setVisibility(View.VISIBLE);
    }
    public void hade_rl_submitting(){
        rl_submitting.setVisibility(View.GONE);
    }

    private List<String> url_list = new ArrayList<>();
    private View contentView;
    private PopupWindow window;
    TextView tv;
    private ImagePagerAdapter img_adapter;

    //照片放大方法
    public void show_photo(){
        url_list.clear();
        Document document = Jsoup.parse(exercise_stu_html);
        Elements imgElements = document.getElementsByTag("img");

        for (Element imgElement : imgElements) {
            String src = imgElement.attr("src");
            url_list.add(src);
        }
        if (contentView == null) {
            if (url_list.size() == 0) return;
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
            vp_pic.setAdapter(img_adapter);


            //顶部标签
            tv = contentView.findViewById(R.id.tv_picNum);
            tv.setText("1/" + url_list.size());
            window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            window.setTouchable(true);

            img_adapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                @Override
                public void onItemClick() {
                    vp_pic.setCurrentItem(0);
                    window.dismiss();
                }
// // 手写公式识别-弃
//                        @Override
//                        public void onLongItemClick(int pos) {
//                            Toast.makeText(getActivity(), "长按", Toast.LENGTH_SHORT).show();
//                            if (contentView2 == null) {
//                                contentView2 = LayoutInflater.from(getActivity()).inflate(R.layout.menu_pic_identify, null, false);
//                                //绑定点击事件
//                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
//                                    identifyUrl = picIdentify(url_list.get(pos));
//                                    originUrl = url_list.get(pos);
//                                    url_list.set(pos, identifyUrl);
//                                    adapter.notifyDataSetChanged();
//                                    ll_selector.setVisibility(View.VISIBLE);
//
//                                    window2.dismiss();
//                                });
//
//                                window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//                                window2.setTouchable(true);
//                            }
//                            window2.showAtLocation(contentView2, Gravity.CENTER, 0, 0);
//
//                        }
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
        } else {
            //顶部标签
            tv = contentView.findViewById(R.id.tv_picNum);
            tv.setText("1/" + url_list.size());
        }

        img_adapter.notifyDataSetChanged();

        window.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

}