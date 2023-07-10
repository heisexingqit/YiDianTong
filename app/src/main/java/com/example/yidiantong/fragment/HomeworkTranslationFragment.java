package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Html;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.ui.DoodleActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.HomeworkInterface;
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
import java.util.ArrayList;
import java.util.List;


public class HomeworkTranslationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeworkTranslationFrag";

    private PagingInterface pageing;
    private HomeworkInterface transmit;

    private int show = 0;
    private LinearLayout ll_context;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncher3;
    private Uri picUri, imageUri;

    //接口需要
    private String learnPlanId, username;

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
    //html内容数据
    private String html_answer;

    // 权限组（AndPermission自带）

    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    //作答组件
    private EditText et_answer;
    private WebView wv_answer;

    //图片编码
    private String imageBase64;

    //学生作答情况
    private StuAnswerEntity stuAnswerEntity;
    private ClickableImageView iv_top;
    private View contentView = null;

    //点击大图
    private List<String> url_list = new ArrayList<>();
    private PopupWindow window;
    private ImagePagerAdapter adapter;

    private LinearLayout ll_answer;

    HomeworkEntity homeworkEntity;

    private int picCount = 0;

    // 识别
    private View contentView2;
    private PopupWindow window2;
    private String identifyUrl;
    private String originUrl;

    public static HomeworkTranslationFragment newInstance(HomeworkEntity homeworkEntity, int position, int size, String learnPlanId, String username, StuAnswerEntity stuAnswerEntity) {
        HomeworkTranslationFragment fragment = new HomeworkTranslationFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkEntity", homeworkEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        args.putString("learnPlanId", learnPlanId);
        args.putString("username", username);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
        transmit = (HomeworkInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        int position = 0, size = 0;
        if (getArguments() != null) {
            homeworkEntity = (HomeworkEntity) getArguments().getSerializable("homeworkEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
            learnPlanId = getArguments().getString("learnPlanId");
            username = getArguments().getString("username");
            position = getArguments().getInt("position") + 1;
            size = getArguments().getInt("size");
        }

        if (html_answer == null) {
            html_answer = stuAnswerEntity.getStuAnswer();
        }

        getPicURl();

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_translation, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        // 答题面板
        ll_context = view.findViewById(R.id.ll_context);

        /**
         * 多机适配：底栏高度
         */
        WindowManager windowManager = getActivity().getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        // 长宽像素比
        float deviceAspectRatio = (float) screenHeight / screenWidth;
        // 获取底部布局
        LinearLayout block = view.findViewById(R.id.ll_bottom_block);
        if (deviceAspectRatio > 2.0) {
            ViewGroup.LayoutParams params = block.getLayoutParams();
            params.height = PxUtils.dip2px(getActivity(), 80);
            block.setLayoutParams(params);
            params = ll_context.getLayoutParams();
            params.height = PxUtils.dip2px(getActivity(), 255);
            ll_context.setLayoutParams(params);
        }

        //题面显示
        WebView wv_content = view.findViewById(R.id.wv_content);

        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + homeworkEntity.getQuestionContent() + "</body>";
        wv_content.loadData(html_content, "text/html", "utf-8");


        /**
         * 老的作答点击事件 源组件（为了代码复用，直接用）
         */
        ll_answer = view.findViewById(R.id.ll_answer);
        ll_answer.setOnClickListener(this);

        //作答面显示
        wv_answer = view.findViewById(R.id.wv_answer);
        wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
        WebSettings webSettings = wv_answer.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wv_answer.addJavascriptInterface(
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

        view.findViewById(R.id.tv_save).setOnClickListener(this);
        et_answer = view.findViewById(R.id.et_answer);

        //题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(homeworkEntity.getQuestionTypeName());

        //顶部题号染色
        int positionLen = String.valueOf(position).length();
        String questionNum = position + "/" + size + "题";
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        //编辑框组件
        iv_top = view.findViewById(R.id.iv_top);
        iv_top.setOnClickListener(this);
        view.findViewById(R.id.tv_erase).setOnClickListener(this);
        view.findViewById(R.id.civ_camera).setOnClickListener(this);
        view.findViewById(R.id.civ_gallery).setOnClickListener(this);

        // 注册Gallery回调组件
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
                    picUri = intent.getData();
                    if (picUri != null) {
                        /*Gallery回调执行*/
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(picUri));
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
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = new Intent(getActivity(), DoodleActivity.class);
                    intent.putExtra("uri", imageUri.toString());
                    mResultLauncher3.launch(intent);
                }
            }
        });

        // 注册 涂鸦板
        mResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = result.getData();
                    imageBase64 = intent.getStringExtra("data");
                    imageBase64 = imageBase64.replace("+", "%2b");
                    uploadImage();
                }
            }
        });

        // 提前创建Adapter
        adapter = new ImagePagerAdapter(getActivity(), url_list);

        return view;
    }

    private void getPicURl() {
        url_list.clear();
        Html.fromHtml(html_answer, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                url_list.add(s);
                return null;
            }
        }, null);

    }

    private String getHtmlAnswer() {
        return html_answer_head + html_answer + html_answer_tail;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                picCount++;
                String url = (String) message.obj;
                Log.d("wen", "handleMessage: " + url);
                url_list.add(url);
                adapter.updateData(url_list);// 关键
                html_answer += "<img onclick=\"bigimage(this)\" src=\"" + url + "\" style=\"max-width:80px;\"/>";
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                transmit.offLoading();
            } else if (message.what == 101) {
                // 复用老代码 触发点击
                ll_answer.performClick();
            }
        }
    };

    private void uploadImage() {
        transmit.onLoading();
        String mRequestUrl = Constant.API + Constant.UPLOAD_IMAGE + "?baseCode=" + imageBase64 + "&leanPlanId=" + learnPlanId + "&userId=" + username;
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();

                //封装消息，传递给主线程
                Message message = Message.obtain();

                message.obj = url;
                // 发送消息给主线程
                //标识线程
                message.what = 100;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_page_last:
                pageing.pageLast();
                break;
            case R.id.iv_page_next:
                pageing.pageNext();
                break;
            case R.id.iv_top:
                ViewGroup.LayoutParams params = ll_context.getLayoutParams();
                if (show == 0) {
                    params.height = PxUtils.dip2px(getActivity(), 729);
                    iv_top.setImageResource(R.drawable.down_icon);
                    show = 1;
                } else {
                    params.height = PxUtils.dip2px(getActivity(), 235);
                    iv_top.setImageResource(R.drawable.up_icon);
                    show = 0;
                }
                ll_context.setLayoutParams(params);
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
            case R.id.tv_save:
                html_answer += et_answer.getText().toString();
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                et_answer.setText("");
                break;
            case R.id.tv_erase:
                html_answer = "";
                picCount = 0;
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                url_list.clear();
                adapter.updateData(url_list);
                break;
            case R.id.ll_answer:
                getPicURl();
                if (contentView == null) {
                    if (picCount == 0) break;
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                    //  回显方法
                    //  回显方法
                    //  回显方法
                    contentView.findViewById(R.id.btn_save).setOnClickListener(v->{
                        Log.d(TAG, "onClick: ");
                        html_answer = html_answer.replace(originUrl, identifyUrl);
                        wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                        transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                        window.dismiss();
                    });
                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(v->{window.dismiss();});
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

                        @Override
                        public void onLongItemClick(int pos) {
                            Toast.makeText(getActivity(), "长按", Toast.LENGTH_SHORT).show();
                            if (contentView2 == null) {
                                contentView2 = LayoutInflater.from(getActivity()).inflate(R.layout.menu_pic_identify, null, false);
                                //绑定点击事件
                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
                                    identifyUrl = picIdentify(url_list.get(pos));
                                    originUrl = url_list.get(pos);
                                    url_list.set(pos, identifyUrl);
                                    adapter.notifyDataSetChanged();
                                    ll_selector.setVisibility(View.VISIBLE);

                                    window2.dismiss();
                                });

                                window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                                window2.setTouchable(true);
                            }
                            window2.showAtLocation(contentView2, Gravity.CENTER, 0, 0);

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
                LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                ll_selector.setVisibility(View.INVISIBLE);
                adapter.notifyDataSetChanged();
                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

                break;
        }
    }

    private String picIdentify(String inputUrl) {
        return "https://www.baidu.com/img/PCgkdoodle_293edff43c2957071d2f6bfa606993ac.gif";
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
                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("权限被禁用")
                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(HomeworkTranslationFragment.this)
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
        File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
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
            imageUri = FileProvider.getUriForFile(getActivity(),
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
                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("权限被禁用")
                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(HomeworkTranslationFragment.this)
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
            new AlertDialog.Builder(getActivity())
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
            new AlertDialog.Builder(getActivity())
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
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (AndPermission.hasPermissions(this, Permission.Group.STORAGE)) {
                    // 有对应的权限
                    Toast.makeText(getActivity(), "读写文件权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(getActivity(), "读写文件权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (AndPermission.hasPermissions(this, Permission.Group.CAMERA)) {
                    // 有对应的权限
                    Toast.makeText(getActivity(), "拍照权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(getActivity(), "拍照权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}