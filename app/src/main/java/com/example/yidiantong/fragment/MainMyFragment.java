package com.example.yidiantong.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
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


import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.PswDialog;
import com.example.yidiantong.View.TouxiangDialog;
import com.example.yidiantong.ui.LoginActivity;
import com.example.yidiantong.util.PermissionUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainMyFragment extends Fragment implements View.OnClickListener {

    private LinearLayout f_ll_info;
    private LinearLayout f_ll_us;
    private LinearLayout f_ll_update;
    private LinearLayout f_ll_psw;
    private LinearLayout f_ll_center;
    private Button fbtn_exit;
    private ImageView fiv_my;

    private int is_pw_show = 0;//0表示隐藏，1表示显示
    private int is_pw_focus = 0;//0表示没有聚焦，1表示聚焦
    private Button fbtn_cancel;
    private Button fbtn_confirm;

    // 更换头像
    private ActivityResultLauncher<Intent> mResultLauncher;
    private ActivityResultLauncher<Intent> mResultLauncher2;
    private ActivityResultLauncher<Intent> mResultLauncherCrop;
    private ActivityResultLauncher<Intent> mResultLauncherCrop2;
    private Uri picUri, imageUri, cropUri;

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

    public static MainMyFragment newInstance() {
        MainMyFragment fragment = new MainMyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获取视图View
        View view = inflater.inflate(R.layout.fragment_main_my, container, false);

        f_ll_info = view.findViewById(R.id.f_ll_info);
        f_ll_us = view.findViewById(R.id.f_ll_us);
        f_ll_update = view.findViewById(R.id.f_ll_update);
        f_ll_psw = view.findViewById(R.id.f_ll_psw);
        f_ll_center = view.findViewById(R.id.f_ll_center);
        fbtn_exit = view.findViewById(R.id.fbtn_exit);
        fbtn_cancel = view.findViewById(R.id.fbtn_cancel);
        fbtn_confirm = view.findViewById(R.id.fbtn_confirm);

        // 点击头像
        fiv_my = view.findViewById(R.id.fiv_my);


        // 设置点击事件
        f_ll_info.setOnClickListener(this);
        f_ll_us.setOnClickListener(this);
        f_ll_update.setOnClickListener(this);
        f_ll_psw.setOnClickListener(this);
        f_ll_center.setOnClickListener(this);
        fbtn_exit.setOnClickListener(this);
        fiv_my.setOnClickListener(this);



        //注册Gallery回调组件
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    Intent intent = result.getData();
                    //Uri和path相似，都是定位路径，属于一步到位方式 =》 如果是path 则 Uri.parse(path)
                    picUri = intent.getData();
                    if (picUri != null) {
                        startPhotoZoom(picUri);
                    }
                }
            }
        });

        //注册相册Crop裁剪组件
        mResultLauncherCrop = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == getActivity().RESULT_OK) {
                            Intent intent = result.getData();
                            Bitmap bitmap1 = intent.getExtras().getParcelable("data");
                            if (bitmap1 != null) {
                                fiv_my.setImageBitmap(bitmap1);
                                Log.d("ning", "onActivityResult: " + picUri.toString());
                                Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                //注册Camera回调组件
                mResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == getActivity().RESULT_OK) {
                            Crop(imageUri); // 裁剪图片
                        }
                    }
                });

        //注册相机Crop裁剪组件
        mResultLauncherCrop2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    try {
                        Log.e("imageUri111", String.valueOf(cropUri));
                        //  decodeStream()可以将output_image.jpg解析成Bitmap对象。
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(cropUri));
                        fiv_my.setImageBitmap(bitmap);
                        Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
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
            case R.id.f_ll_info:

                break;
            case R.id.f_ll_us:

                break;
            case R.id.f_ll_update:

                break;
            case R.id.f_ll_psw:
                // 修改密码弹窗
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
                        Toast.makeText(getActivity(), "修改成功:原密码：" + old_pw + " 新密码：" + new_pw, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                break;
            case R.id.f_ll_center:
                break;
            case R.id.fbtn_exit:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                //两个一起用
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //登录成功跳转
                startActivity(intent);
                break;
            case R.id.fiv_my:
                openDialog();
                break;
        }
    }

    private void openDialog() {
        final TouxiangDialog touxiangDialog = new TouxiangDialog(getActivity());
        Window dialogWindow = touxiangDialog.getWindow();
        WindowManager.LayoutParams p_lp = dialogWindow.getAttributes();
        int notificationBar  = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        int[] location = new int[2] ;
        //fiv_my.getLocationInWindow(location); //获取在当前窗体内的绝对坐标
        fiv_my.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        Log.e("location", String.valueOf(location[1]));
        dialogWindow.setGravity(Gravity.TOP);
        p_lp.x=0; //对 dialog 设置 x 轴坐标
        p_lp.y=location [1] + fiv_my.getHeight() - notificationBar;
        p_lp.alpha=1f;//dialog透明度
        dialogWindow.setAttributes(p_lp);
        touxiangDialog.setCanceledOnTouchOutside(true);
        touxiangDialog.show();
        touxiangDialog.setClickListener(new TouxiangDialog.ClickListenerInterface() {
            // 相机
            @Override
            public void doGetCamera() {
                //启动相机程序
                /*
                 * 第零步，先申请权限
                 * */
                if (PermissionUtil.checkPermissionForFragment(getActivity(), PERMISSIONS_CAMERA, REQUEST_CODE_CAMERA)) {
                    openCamera();
                }
                touxiangDialog.dismiss();
            }

            // 图库
            @Override
            public void doGetPic() {
                if (PermissionUtil.checkPermissionForFragment(getActivity(), PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE)) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //选择图片文件类型
                    intent.setType("image/*");
                    //跳转
                    mResultLauncher.launch(intent);
                }
                touxiangDialog.dismiss();
            }

            // 取消
            @Override
            public void doCancel() {
                touxiangDialog.dismiss();
            }
        });
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

    // 图片剪切
    private void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        mResultLauncherCrop.launch(intent);

    }

    // 图片剪切
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

        cropUri = Uri.fromFile(Image);

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        mResultLauncherCrop2.launch(intent);
    }

}



