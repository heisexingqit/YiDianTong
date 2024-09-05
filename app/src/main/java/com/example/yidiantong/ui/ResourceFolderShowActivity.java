package com.example.yidiantong.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.fragment.ResourceFolderImgFragment;
import com.example.yidiantong.fragment.ResourceFolderPPTFragment;
import com.example.yidiantong.fragment.ResourceFolderPaperFragment;
import com.example.yidiantong.fragment.ResourceFolderVideoFragment;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

public class ResourceFolderShowActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_folder_show);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        LearnPlanItemEntity itemShow = (LearnPlanItemEntity) getIntent().getSerializableExtra("itemShow");
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(itemShow.getResourceName());
        FrameLayout fl_main = findViewById(R.id.fl_main);
        ImageButton btn_download  = findViewById(R.id.btn_download);
        btn_download.setOnClickListener(v -> {
            // 权限请求
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.Group.STORAGE)
                    .onGranted(new Action<List<String>>() {
                        // 获取权限后
                        @Override
                        public void onAction(List<String> data) {
                            // 获取资源的下载链接
                            String downloadUrl = itemShow.getPath();
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                // 创建DownloadManager请求
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                                String fileName;
                                // 设置下载文件的名称
                                if(itemShow.getFormat().equals("word")){
                                    fileName= itemShow.getResourceName() + "." + "docx";
                                }else if(itemShow.getFormat().equals("image")){
                                    fileName = itemShow.getResourceName() + "." + "png";
                                }else if(itemShow.getFormat().equals("ppt")){
                                    fileName = itemShow.getResourceName() + "." + "pptx";
                                }else {
                                    fileName = itemShow.getResourceName() + "." + itemShow.getFormat();
                                }

                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                                // 使用系统默认通知，当下载开始和结束时会自动显示通知
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                // 获取DownloadManager实例
                                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

                                // 开始下载
                                long downloadId = downloadManager.enqueue(request);

                                // 显示下载提示
                                Toast.makeText(ResourceFolderShowActivity.this, "开始下载 " + fileName, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ResourceFolderShowActivity.this, "缺少下载链接", Toast.LENGTH_SHORT).show();
                            }

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

        });

        Fragment fragment = null;
        Log.d("wen", "onCreate: " + itemShow);
        switch (itemShow.getFormat()) {
            case "word":
            case "pdf":
                fragment = ResourceFolderPaperFragment.newInstance(itemShow);
                break;
            case "image":
                fragment = ResourceFolderImgFragment.newInstance(itemShow);
                break;
            case "video":
            case "music":
                btn_download.setVisibility(View.INVISIBLE);
                fragment = ResourceFolderVideoFragment.newInstance(itemShow);
                break;
            case "ppt":
                fragment = ResourceFolderPPTFragment.newInstance(itemShow);
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(ResourceFolderShowActivity.this)
                    .setTitle("提示")
                    .setMessage("开启读写文件权限才能下载资源！")
                    .setPositiveButton("知道了", (dialog, which) -> {
                        executor.execute();
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        executor.cancel();
                    })
                    .show();
        }
    };

}