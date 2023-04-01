package com.example.yidiantong.fragment;

import android.Manifest;
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
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableWebView;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PageingInterface;
import com.example.yidiantong.util.PermissionUtil;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.TransmitInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeworkTranslationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeworkTranslationFrag";

    private PageingInterface pageing;
    private TransmitInterface transmit;

    private int show = 0;
    private LinearLayout ll_context;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private Uri picUri, imageUri;

    //接口需要
    private String learnPlanId, username;
    //html头
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
            "        function a(x) {\n" +
            "            test.mytoast(\"我尼玛\")\n" +
            "        }\n" +
            "    </script>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n";
    //html尾
    private String html_answer_tail = "</body>";
    //html内容数据
    private String html_answer = "";

    //权限组
    private static final String[] PERMISSIONS_STORAGE = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final String[] PERMISSIONS_CAMERA = new String[]{
            Manifest.permission.CAMERA
    };

    //标识码
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    //作答组件
    private EditText et_answer;
    private ClickableWebView wv_answer;

    //图片编码
    private String imageBase64;

    //学生作答情况
    private StuAnswerEntity stuAnswerEntity;
    private ClickableImageView iv_top;
    private View contentView = null;
    //点击大图
    private List<String> url_list = new ArrayList<>();
    private PopupWindow window;

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
        pageing = (PageingInterface) context;
        transmit = (TransmitInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        learnPlanId = arg.getString("learnPlanId");
        username = arg.getString("username");
        HomeworkEntity homeworkEntity = (HomeworkEntity) arg.getSerializable("homeworkEntity");
        stuAnswerEntity = (StuAnswerEntity) arg.getSerializable("stuAnswerEntity");
        html_answer = stuAnswerEntity.getStuAnswer();
        if (url_list.size() == 0) {
            Html.fromHtml(html_answer, new Html.ImageGetter() {
                @Override
                public Drawable getDrawable(String s) {
                    url_list.add(s);
                    return null;
                }
            }, null);
        }

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_translation, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //题面显示
        WebView wv_content = view.findViewById(R.id.wv_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + homeworkEntity.getQuestionContent() + "</body>";
        wv_content.loadData(html_content, "text/html", "utf-8");

        //作答面显示
        wv_answer = view.findViewById(R.id.wv_answer);
        wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
        wv_answer.setClickable(false);
        wv_answer.setFocusable(false);
        view.findViewById(R.id.ll_answer).setOnClickListener(this);

        //传答案给Activity
        transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);

        //保存按钮
        view.findViewById(R.id.tv_save).setOnClickListener(this);

        //答案输入框
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
        ll_context = view.findViewById(R.id.ll_context);
        iv_top = view.findViewById(R.id.iv_top);
        iv_top.setOnClickListener(this);
        view.findViewById(R.id.tv_erase).setOnClickListener(this);

        //图片展示组件
//        ImageView iv_content = view.findViewById(R.id.iv_content);

        //拍照按钮
        view.findViewById(R.id.civ_camera).setOnClickListener(this);

        //相册按钮
        view.findViewById(R.id.civ_gallery).setOnClickListener(this);

        //注册Gallery回调组件
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
//                            iv_content.setImageURI(picUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(picUri));
//                            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), picUri);

//                            iv_content.setImageBitmap(bitmap);
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

        //注册Camera回调组件
        mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    try {
                        //  decodeStream()可以将output_image.jpg解析成Bitmap对象。
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri), null, options);
//
                        imageBase64 = ImageUtils.Bitmap2StrByBase64(bitmap);
                        imageBase64 = imageBase64.replace("+", "%2b");
                        uploadImage();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
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
                String url = (String) message.obj;
                url_list.add(url);
                html_answer += "<img onclick=\"bigimage(this)\" src=\"" + url + "\" style=\"max-width:80px;\"/>";
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
            }
        }
    };

    private void uploadImage() {

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
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
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

                //启动相机程序
                /*
                 * 第零步，先申请权限
                 * */
                if (PermissionUtil.checkPermissionForFragment(getActivity(), PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA)) {
                    openCamera();
                }
                break;
            case R.id.civ_gallery:

                if (PermissionUtil.checkPermissionForFragment(getActivity(), PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //选择图片文件类型
                    intent.setType("image/*");
                    //跳转
                    mResultLauncher.launch(intent);
                }
                break;
            case R.id.tv_save:
                html_answer += et_answer.getText().toString();
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                et_answer.setText("");
                break;
            case R.id.tv_erase:
                html_answer = "";
                wv_answer.loadData(getHtmlAnswer(), "text/html", "utf-8");
                transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
                break;
            case R.id.ll_answer:
//                Toast.makeText(getActivity(), "TTTTTTTTTTTT", Toast.LENGTH_SHORT).show();
                if (contentView == null) {
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
                    ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(), url_list);
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
                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

                break;
        }
    }

    //在用户进行授权后，处理请求结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case REQUEST_CODE_STORAGE:
                if (PermissionUtil.checkGrant(grantResults)) {
                    //跳转到相册,这里的目的地址写得很泛。
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");

                    //选择图片文件类型
                    //跳转
                    mResultLauncher.launch(intent);
                } else {
                    jumpToSettings();
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (PermissionUtil.checkGrant(grantResults)) {
                    openCamera();
                } else {
                    jumpToSettings();
                }
                break;
            default:
                break;
        }
    }


    //请求失败跳转设置
    private void jumpToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        //必须要setData不然会闪退
        intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

}