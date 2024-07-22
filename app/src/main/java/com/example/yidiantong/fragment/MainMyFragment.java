package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.PswDialog;
import com.example.yidiantong.View.TouxiangDialog;
import com.example.yidiantong.ui.AutoDetectionActivity;
import com.example.yidiantong.ui.AutoStudyActivity;
import com.example.yidiantong.ui.LoginActivity;
import com.example.yidiantong.ui.MyIntroductionActivity;
import com.example.yidiantong.ui.SelectCourseActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.ImageUtils;
import com.example.yidiantong.util.JsonUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMyFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MainMyFragment";

    private LinearLayout f_ll_info;  // 账号信息
    private LinearLayout f_ll_us;    // 关于我们
    private LinearLayout f_ll_update;  // 检查更新
    private LinearLayout f_ll_psw;   // 修改密码
    private LinearLayout f_ll_auto_study;  // 自主学习
    private LinearLayout f_ll_intelligent_detection;  // 智能检测
    private LinearLayout f_ll_center;  // 选课中心
    private Button fbtn_exit;  // 退出登录
    private ImageView fiv_my;  // 头像

    private int is_pw_show = 0; //0表示隐藏，1表示显示
    private int is_pw_focus = 0; //0表示没有聚焦，1表示聚焦
    private Button fbtn_cancel;
    private Button fbtn_confirm;

    // 更换头像
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;
    private Uri picUri, imageUri, cropUri;

    // 权限组（AndPermission自带）

    // 标识码（与权限对应）
    private static final int REQUEST_CODE_STORAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;

    private String username;
    private String realName;
    private String imageBase64;
    private String newPW;
    private SharedPreferences preferences;


    // 创建实例（空参数）
    public static MainMyFragment newInstance() {
        MainMyFragment fragment = new MainMyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);  // 创建实例后可以通过gET
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取根视图 view
        View view = inflater.inflate(R.layout.fragment_main_my, container, false);

        // 获取view中的组件
        f_ll_info = view.findViewById(R.id.f_ll_info);
        f_ll_us = view.findViewById(R.id.f_ll_us);
        f_ll_update = view.findViewById(R.id.f_ll_update);
        f_ll_psw = view.findViewById(R.id.f_ll_psw);
        f_ll_auto_study = view.findViewById(R.id.f_ll_auto_study);
        f_ll_intelligent_detection = view.findViewById(R.id.f_ll_intelligent_detection);

        f_ll_center = view.findViewById(R.id.f_ll_center);
        fbtn_exit = view.findViewById(R.id.fbtn_exit);
        fbtn_cancel = view.findViewById(R.id.fbtn_cancel);
        fbtn_confirm = view.findViewById(R.id.fbtn_confirm);

        TextView tv_version = view.findViewById(R.id.tv_version);
        tv_version.setText(MyApplication.versionName);


        // 点击头像
        fiv_my = view.findViewById(R.id.fiv_my);

        // 设置点击事件
        f_ll_info.setOnClickListener(this);
        f_ll_us.setOnClickListener(this);
        f_ll_update.setOnClickListener(this);
        f_ll_psw.setOnClickListener(this);
        f_ll_auto_study.setOnClickListener(this);
        f_ll_intelligent_detection.setOnClickListener(this);
        f_ll_center.setOnClickListener(this);
        fbtn_exit.setOnClickListener(this);
        fiv_my.setOnClickListener(this);

        // 注册Gallery回调组件
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = result.getData();
                    // 获取到真实的Uri
                    Uri uri = intent.getData();
                    if (uri != null) {
                        /**
                         * 这里做了统一化操作：创建一个output.jpg文件，并将uri写入新文件，并将picUri赋给新文件（与拍照逻辑相似）
                         * 一是为了简化方法；
                         * 二是因为适配问题，有些手机应用不能返回数据，只能与拍照类似的调用方式才行；
                         * 三是因为获取本地图片只能返回uri，而不像拍照那样可以选择写入，因此需要手动。
                         */
                        File Image = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
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
                            picUri = FileProvider.getUriForFile(getActivity(),
                                    "com.example.yidiantong.fileprovider", Image);
                        } else {
                            picUri = Uri.fromFile(Image);
                        }

                        // uri写入文件Image
                        FileOutputStream outputStream = null;
                        FileInputStream inputStream = null;
                        try {
                            outputStream = new FileOutputStream(Image);
                            inputStream = (FileInputStream) getActivity().getContentResolver().openInputStream(uri);

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


        // 注册Camera回调组件
        mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Crop(imageUri); // 裁剪图片
                }
            }
        });

        /**
         * 注册通用裁切回调：与通用裁切方法对应。
         */
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    File Image = new File(getActivity().getExternalCacheDir(), "output_temp.jpg");

                    imageBase64 = ImageUtils.Bitmap2StrByBase64(getActivity(), Image);
                    uploadImage();
                }
            }
        });


        /**
         * 真实用户数据设置
         */
        TextView tv_username = view.findViewById(R.id.tv_username);
        username = MyApplication.username;
        realName = MyApplication.cnName;
        tv_username.setText(realName + "(" + username + ")");

        // 获取图片
        String picUrl = MyApplication.picUrl;
        ImageLoader.getInstance().displayImage(picUrl, fiv_my, MyApplication.getLoaderOptions());

        preferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);

        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                String picUrl = (String) message.obj;
                MyApplication.picUrl = picUrl;
                ImageLoader.getInstance().displayImage(picUrl, fiv_my, MyApplication.getLoaderOptions());
                Toast.makeText(getActivity(), "修改头像成功", Toast.LENGTH_SHORT).show();
            } else if (message.what == 101) {
                MyApplication.password = (String) message.obj;
            }
        }
    };

    // 修改密码
    private void changePW() {
        String mRequestUrl = Constant.API + Constant.CHANGE_PW + "?passWord=" + newPW + "&userName=" + username;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    MyApplication.password = newPW;
                    Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("password", "");
                    editor.commit();
                    // 修改密码成功后退出重新登录
                    fbtn_exit.callOnClick();
                } else {
                    Toast.makeText(getActivity(), "修改密码失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);

    }

    // 上传图片
    private void uploadImage() {
        String mRequestUrl = Constant.API + Constant.UPLOAD_HEAD_PHOTO;

        Map<String, String> params = new HashMap<>();
        params.put("baseCode", imageBase64);
        params.put("userId", username);

        Log.e("debug0116", "base64编码长度: " + imageBase64.length());

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    //封装消息，传递给主线程
                    Message message = Message.obtain();

                    message.obj = url;
                    // 发送消息给主线程
                    //标识线程
                    message.what = 100;
                    handler.sendMessage(message);
                } else {
                    Toast.makeText(getActivity(), "修改头像失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.f_ll_info:
                break;
            case R.id.f_ll_us:
                startActivity(new Intent(getActivity(), MyIntroductionActivity.class));
                break;
            case R.id.f_ll_update:
                checkUpdate();
                break;
            case R.id.f_ll_psw:
                // 修改密码弹窗创建和设置
                PswDialog builder = new PswDialog(getActivity());
                builder.setCancel("cancel", new PswDialog.IOnCancelListener() {
                    @Override
                    public void onCancel(PswDialog dialog) {
                        Toast.makeText(getActivity(), "已取消", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setConfirm("confirm", new PswDialog.IOnConfirmListener() {
                    @Override
                    public void onConfirm(PswDialog dialog) {
                        String old_pw = dialog.old_pw;
                        String new_pw = dialog.new_pw;
                        if (MyApplication.password.equals(old_pw)) {
                            newPW = new_pw;
                            changePW();
                        } else {
                            Toast.makeText(getActivity(), "原密码错误！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // 修改密码弹窗弹出
                builder.show();
                break;
            //自主学习
            case R.id.f_ll_auto_study:
                Intent intent_auto = new Intent(getActivity(), AutoStudyActivity.class);
                intent_auto.putExtra("username", username);
                startActivity(intent_auto);
                break;
            //智能检测
            case R.id.f_ll_intelligent_detection:
                Intent intent_check = new Intent(getActivity(), AutoDetectionActivity.class);
                intent_check.putExtra("username", username);
                startActivity(intent_check);
                break;
            case R.id.f_ll_center:
                Intent intent_center = new Intent(getActivity(), SelectCourseActivity.class);
                intent_center.putExtra("username", username);
                intent_center.putExtra("userCn", realName);
                startActivity(intent_center);
                break;
            case R.id.fbtn_exit:
                // 关闭自动登录
                MyApplication.autoLogin = false;
                // 退出登录
                Intent intent = new Intent(getActivity(), LoginActivity.class);

                //两个一起用
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //登录成功跳转
                startActivity(intent);
                break;
            case R.id.fiv_my:
                // 修改头像
                openDialog();
                break;
        }
    }

    // 创建修改头像Dialog
    private void openDialog() {
        final TouxiangDialog touxiangDialog = new TouxiangDialog(getActivity());
        Window dialogWindow = touxiangDialog.getWindow();
        WindowManager.LayoutParams p_lp = dialogWindow.getAttributes();
        int notificationBar = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));

        int[] location = new int[2];

        fiv_my.getLocationOnScreen(location); //获取在整个屏幕内的绝对坐标
        Log.e("location", String.valueOf(location[1]));

        dialogWindow.setGravity(Gravity.TOP);
        p_lp.x = 0; //对 dialog 设置 x 轴坐标
        p_lp.y = location[1] + fiv_my.getHeight() - notificationBar;
        p_lp.alpha = 1f; //dialog透明度
        dialogWindow.setAttributes(p_lp);
        touxiangDialog.setCanceledOnTouchOutside(true);
        touxiangDialog.show();
        touxiangDialog.setClickListener(new TouxiangDialog.ClickListenerInterface() {
            // 相机选项
            @Override
            public void doGetCamera() {
                // 启动相机程序
                /*
                 第零步，先申请权限
                 */
                permissionOpenCamera();
                touxiangDialog.dismiss();
            }

            // 图库选项
            @Override
            public void doGetPic() {
                // 打开本地存储
                permissionOpenGallery();
                touxiangDialog.dismiss();
            }

            // 取消选项
            @Override
            public void doCancel() {
                touxiangDialog.dismiss();
            }
        });
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
//                                        AndPermission.with(MainMyFragment.this)
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
                        // 判断是否点了永远拒绝，不再提示
//                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
//                            new AlertDialog.Builder(getActivity())
//                                    .setTitle("权限被禁用")
//                                    .setMessage("读写文件权限被禁用，请到APP设置页面手动开启！")
//                                    .setPositiveButton("跳转", (dialog, which) -> {
//                                        AndPermission.with(MainMyFragment.this)
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
     * 通用裁切方法。传输、读取文件、裁切、写入文件,最终以cropUri形式显示
     *
     * @param uri 裁切前的图片Uri（pic：相册；image：照片）
     */
    private void Crop(Uri uri) {

        File Image = new File(getActivity().getExternalCacheDir(), "output_temp.jpg");
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
            cropUri = FileProvider.getUriForFile(getActivity(),
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
        ClipData clipData = ClipData.newUri(getActivity().getContentResolver(), "A photo", cropUri);
        intent.setClipData(clipData);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);

        // 设置输出文件位置和格式
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        intent.putExtra("noFaceDetection", true);

        mResultLauncherCrop.launch(intent);
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


    private void checkUpdate() {
        // 创建AlertDialog.Builder实例
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String mRequestUrl = Constant.API + Constant.CHECK_VERSION + "?version=" + MyApplication.versionName + "&type=stu&autoType=noauto" + "&userId=" + username;
        Log.e("0124", "checkUpdate: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONObject data = json.getJSONObject("data");
                Log.e("0124", "检查版本: " + json);
                if (data.getString("status").equals("0")) {
                    // 【不需要更新】
                    // 设置对话框消息内容
                    builder.setMessage("已是最新版本! 版本号为V" + MyApplication.versionName);

                    // 设置PositiveButton（确定按钮）的点击事件
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 确定按钮点击事件处理
                            dialog.dismiss(); // 关闭对话框
                        }
                    });
                    builder.show();
                } else {

                    String downloadUrl = data.getString("oploadLink");
                    // 【需要更新】
                    builder.setMessage("检测到有新版本，是否更新？");

                    // 设置PositiveButton（确定按钮）的点击事件
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // 启动浏览器
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            // 设置数据（要打开的URL）
                            intent.setData(Uri.parse(downloadUrl));
                            startActivity(intent);

                            dialog.dismiss(); // 关闭对话框
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 确定按钮点击事件处理
                            dialog.dismiss(); // 关闭对话框
                        }
                    });
                    builder.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

}



