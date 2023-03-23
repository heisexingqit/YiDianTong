package com.example.yidiantong.fragment;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.target.ImageViewTargetFactory;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.HomeworkEntity;
import com.example.yidiantong.util.PageingInterface;

import com.example.yidiantong.util.PermissionUtil;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.PxUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Result;


public class HomeworkTranslationFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeworkTranslationFrag";

    private PageingInterface pageing;
    private int show = 0;
    private LinearLayout ll_context;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private Uri picUri, imageUri;

    private String html_answer = "<p style=\" color: rgb(117, 117, 117); word-wrap:break-word; \">";

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
    private EditText et_answer;
    private WebView wv_answer;



    public static HomeworkTranslationFragment newInstance(HomeworkEntity homeworkEntity, int position, int size) {
        HomeworkTranslationFragment fragment = new HomeworkTranslationFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkEntity", homeworkEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PageingInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        HomeworkEntity homeworkEntity = (HomeworkEntity) arg.getSerializable("homeworkEntity");

        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_translation, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //题面显示
        WebView wv_content = view.findViewById(R.id.wv_content);
        String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + homeworkEntity.getQuestionContent() + "</body>";
        wv_content.loadData(html_content, "text/html", "utf-8");

        //答案面显示
//        wv_answer = view.findViewById(R.id.wv_answer);
//        wv_answer.loadData(html_answer + "</p>", "text/html", "utf-8");


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
        ll_context = view.findViewById(R.id.ll_context);
        view.findViewById(R.id.iv_top).setOnClickListener(this);

        //图片展示组件
        ImageView iv_content = view.findViewById(R.id.iv_content);

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
                        iv_content.setImageURI(picUri);
                        Log.d("ning", "onActivityResult: " + picUri.toString());
                        /*Gallery回调执行*/

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
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        iv_content.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return view;
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
                    show = 1;
                } else {
                    params.height = PxUtils.dip2px(getActivity(), 235);
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
                wv_answer.loadData(html_answer + "</p>", "text/html", "utf-8");
                et_answer.setText("");
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
                    //选择图片文件类型
                    intent.setType("image/*");
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

    private void openCamera(){
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