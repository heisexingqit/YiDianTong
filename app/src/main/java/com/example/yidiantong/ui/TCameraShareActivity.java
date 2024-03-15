package com.example.yidiantong.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.TKeTangListEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TCameraShareActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TCameraShareActivity";
    private PhotoView pv_content;
    private ClickableImageView iv_camera;
    private ClickableTextView tv_share_on;
    private ClickableTextView tv_share_off;

    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;
    private String imageBase64;
    private Uri imageUri;
    private Uri cropUri;
    private String learnPlanId;
    private String imageUrl;

    boolean pushFlag = false;
    boolean pullFlag = false;
    private String ip;
    private String userId;

    // 创建一个handler runable组成的监听器，间隔500ms执行一次
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getYDTMessage();
            handler.postDelayed(this, 500);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcamera_share);

        ip = getIntent().getStringExtra("ip");
        userId = getIntent().getStringExtra("userId");
        learnPlanId = getIntent().getStringExtra("learnPlanId");
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        pv_content = findViewById(R.id.pv_content);

        iv_camera = findViewById(R.id.iv_camera);
        tv_share_on = findViewById(R.id.tv_share_on);
        tv_share_off = findViewById(R.id.tv_share_off);
        iv_camera.setOnClickListener(this);
        tv_share_on.setOnClickListener(this);
        tv_share_off.setOnClickListener(this);

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
                    imageBase64 = ImageUtils.Bitmap2StrByBase64(TCameraShareActivity.this, Image);
                    uploadImage();
                }
            }
        });

        // 启动runnable监听器
        handler.postDelayed(runnable, 500);

    }


    private void changeUI() {
        if (pushFlag) {
            clickableButton(tv_share_on);
        } else {
            unclickableButton(tv_share_on);
        }

        if (pullFlag) {
            clickableButton(tv_share_off);
        } else {
            unclickableButton(tv_share_off);
        }
    }


    private void clickableButton(ClickableTextView tv) {
        tv.setBackgroundResource(R.drawable.bell_class_focus);
        tv.setTextColor(Color.WHITE);
    }

    private void unclickableButton(ClickableTextView tv) {
        tv.setBackgroundResource(R.drawable.bell_class_unfocus);
        tv.setTextColor(getResources().getColor(R.color.gray_new));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_camera:
                permissionOpenCamera();
                break;
            case R.id.tv_share_on:
                if (pushFlag) {
                    showImage(true);
                }
                break;
            case R.id.tv_share_off:
                if (pullFlag) {
                    showImage(false);
                }
                break;
        }
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
                        // 判断是否点了永远拒绝，不再提示
//                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("权限被禁用")
//                                    .setMessage("拍照权限被禁用，请到APP设置页面手动开启！")
//                                    .setPositiveButton("跳转", (dialog, which) -> {
//                                        AndPermission.with(HomeworkTranslationFragment.this)
//                                                .runtime()
//                                                .setting()
//                                                .start(REQUEST_CODE_CAMERA);
//                                    })
//                                    .setNegativeButton("取消", (dialog, which) -> {
//
//                                    })
//                                    .show();
//                        }
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
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。拍照申请
     */
    private Rationale rCamera = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new androidx.appcompat.app.AlertDialog.Builder(TCameraShareActivity.this)
                    .setTitle("提示")
                    .setMessage("开启拍照权限才能拍照！")
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
            cropUri = FileProvider.getUriForFile(this,
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

    // 图片上传
    private void uploadImage() {

        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_YDT_UPLOAD_IMAGE;


        Map<String, String> params = new HashMap<>();

        params.put("baseCode", imageBase64);
        params.put("learnPlanId", learnPlanId);
        params.put("userId", userId);
//        Log.e("0129", "baseCode: " + imageBase64);
//        Log.e("0129", "learnPlanId: " + learnPlanId);
//        Log.e("0129", "userId: " + userId);

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String isSuccess = json.getString("status");
                if (isSuccess.equals("success")) {
                    imageUrl = json.getString("url");
//                    TKeTangListEntity entity = new TKeTangListEntity();
//                    entity.setAction("do:ShareStuAnswer");
//                    entity.setActionType("shareImage");
//                    entity.setResId("<img src=\\\"" + json.getString("url") + "\\\"/>");
//
//                    entity.setUserNum("one");
//                    List<TKeTangListEntity> messageList = new ArrayList<>(Arrays.asList(entity));
//                    doAction(messageList, "shareImage");
                    Glide.with(TCameraShareActivity.this)
                            .load(imageUrl)
                            .into(pv_content);
                    pushFlag = true;
                    changeUI();
                } else {
                    Toast.makeText(this, "保存失败，重新拍照", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        MyApplication.addRequest(request, TAG);
    }

    private void showImage(Boolean isShow) {

        String content = null;
        try {
            content = URLEncoder.encode(URLEncoder.encode("<img src='" + imageUrl + "'/>", "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_SEND_MESSAGE
                + "?messageBean.type=0"
                + "&messageBean.userType=teacher"
                + "&messageBean.userNum=one"
                + "&messageBean.source=" + userId
                + "&messageBean.target=0"
                + "&messageBean.messageType=0"
                + "&messageBean.resId=" + content
                + "&messageBean.resPath="
                + "&messageBean.learnPlanId=" + learnPlanId
                + "&messageBean.resRootPath="
                + "&messageBean.desc=";
        if (isShow) {
            mRequestUrl += "&messageBean.action=do:ShareStuAnswer";
        } else {
            mRequestUrl += "&messageBean.action=do:CloseStuAnswer";
        }

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            if (isShow) {
                pullFlag = true;
                pushFlag = false;

            } else {
                pullFlag = false;
                pushFlag = true;
            }
            changeUI();
            Log.e("0130", "response: " + response);
        }, error -> {
            Log.e("0130", "error: " + error);
        });
        MyApplication.addRequest(request, TAG);
    }


    private void getYDTMessage() {
        // 使用Volley框架进行网络请求
        String mRequestUrl = "http://" + ip + ":8901" + Constant.T_GET_MESSAGE_LIST_BY_TEA + "?userId=shitiShow";

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String jsonArray = json.getString("messageList");
                Gson gson = new Gson();
                List<TKeTangListEntity> messageList = gson.fromJson(jsonArray, new TypeToken<List<TKeTangListEntity>>() {
                }.getType());
                if (messageList.size() > 0) {

                    // 获取到messageList最新的一条消息
                    TKeTangListEntity entity = messageList.get(messageList.size() - 1);
                    if (entity.getAction().equals("do:closeShareImage")) {
                        pullFlag = false;
                        pushFlag = true;
                        changeUI();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 终止runable监听器
        handler.removeCallbacks(runnable);
    }
}