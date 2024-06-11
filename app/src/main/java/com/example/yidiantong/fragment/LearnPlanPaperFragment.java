package com.example.yidiantong.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.HomeworkInterface;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;


public class LearnPlanPaperFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearnPlanQuestionFragme";
    private PagingInterface paging;
    private LearnPlanInterface transmit;

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private StuAnswerEntity stuAnswerEntity;
    private WebView wv_content;

    // 观看时间
    private long timeStart;


    public static LearnPlanPaperFragment newInstance(LearnPlanItemEntity learnPlanEntity, StuAnswerEntity stuAnswerEntity) {
        LearnPlanPaperFragment fragment = new LearnPlanPaperFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        paging = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_learn_plan_paper, container, false);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getResourceName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

        ImageButton btn_download = view.findViewById(R.id.btn_download);
        //下载资源
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
                            String downloadUrl = learnPlanEntity.getUrl();
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                // 创建DownloadManager请求
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                                // 设置下载文件的名称
                                String fileName = learnPlanEntity.getResourceName() + "." + learnPlanEntity.getFormat();
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                                // 使用系统默认通知，当下载开始和结束时会自动显示通知
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                                // 获取DownloadManager实例
                                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                                // 开始下载
                                long downloadId = downloadManager.enqueue(request);

                                // 显示下载提示
                                Toast.makeText(getContext(), "开始下载 " + fileName, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "缺少下载链接", Toast.LENGTH_SHORT).show();
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

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        wv_content = view.findViewById(R.id.wv_content);
        WebSettings webSettings = wv_content.getSettings();
        // 设置可以运行JS代码
        webSettings.setJavaScriptEnabled(true);

        Log.d("ww", "onCreateView: " + learnPlanEntity.getUrl());

        // 运行JS代码修改样式
        wv_content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 使用 JavaScript 控制类的宽度（直接运行）
                String javascript = "javascript:(function() { " +
                        "var elements = document.getElementsByClassName('word-paper'); " +
                        "for (var i = 0; i < elements.length; i++) { " +
                        "   elements[i].style.width = '95%'; " +
                        "} " +
                        "})()";

                view.loadUrl(javascript);
            }
        });

        wv_content.loadUrl(learnPlanEntity.getUrl());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                paging.pageLast();
                break;
            case R.id.iv_page_next:
                paging.pageNext();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        transmit.uploadTime(System.currentTimeMillis() - timeStart);
    }
    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(getContext())
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