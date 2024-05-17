package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkMarkedEntity;
import com.example.yidiantong.ui.THomeworkImageMark;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.THomeworkMarkInterface;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.xinlan.imageeditlibrary.editimage.EditImageActivity;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class THomeworkMarkFragment extends Fragment {
    private static final String TAG = "THomeworkMarkFragment";

    // 接口需要
    private THomeworkMarkedEntity homeworkMarked;

    // 打分同步
    private THomeworkMarkInterface transmitInterface;

    // 分数上限
    private int scoreNum;
    private int score = -1;
    private int zero5 = 0;
    private Button[] btnArray;
    private View[] viewArray;
    private CheckBox checkBox;
    private TextView tv_stu_scores;

    private boolean isFirst = true;
    private int position; // 从1开始
    private WebView wv_content2;
    private String stuStr;

    private String oldUrl;
    private TextView tv_zero5;

    private PopupWindow window;
    private View popView;
    private static final int REQUEST_CODE_EDIT_IMAGE = 1001;


    public static THomeworkMarkFragment newInstance(THomeworkMarkedEntity homeworkMarked, int position, int size, boolean canMark) {
        THomeworkMarkFragment fragment = new THomeworkMarkFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkMarked", homeworkMarked);
        args.putInt("position", position);
        args.putInt("size", size);
        args.putBoolean("canMark", canMark);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        transmitInterface = (THomeworkMarkInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arg = getArguments();
//        position = arg.getInt("position") + 1;
//        int size = arg.getInt("size");
        homeworkMarked = (THomeworkMarkedEntity) arg.getSerializable("homeworkMarked");
        position = Integer.parseInt(homeworkMarked.getOrder());
        int size = Integer.parseInt(homeworkMarked.getOrderCount());
        boolean canMark = arg.getBoolean("canMark");

        //获取view
        View view = inflater.inflate(R.layout.fragment_t_homework_mark, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        // 获取组件
        FlexboxLayout fl_score = view.findViewById(R.id.fl_score);
        tv_zero5 = view.findViewById(R.id.tv_zero5);
        // 动态加打分按钮
        tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
        checkBox = view.findViewById(R.id.cb_zero5);

        //题号染色
        int positionLen = String.valueOf(position).length();
        String questionNum = position + " / " + size;
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);

        /**
         *  数据显示，四个vw_content
         */
        WebView wv_content = view.findViewById(R.id.wv_content);
        wv_content2 = view.findViewById(R.id.wv_content2);
        WebView wv_content3 = view.findViewById(R.id.wv_content3);
        WebView wv_content4 = view.findViewById(R.id.wv_content4);
        setHtmlOnWebView(wv_content, homeworkMarked.getShitiShow());
        stuStr = homeworkMarked.getStuAnswer().trim();

        // 设置WebView缓存策略
        WebSettings settings = wv_content2.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 禁止使用缓存加载内容

        // 图片批改
        WebSettings webSettings = wv_content2.getSettings();
        webSettings.setJavaScriptEnabled(true);

        wv_content2.addJavascriptInterface(
                new Object() {
                    @JavascriptInterface
                    @SuppressLint("JavascriptInterface")
                    public void bigPic(String url) {
                        //Log.d("HSK"," permissionOpenGallery的URL:"+url);
                        permissionOpenGallery(url);
                    }
                }
                , "myInterface");
        if (stuStr.length() == 0) {
            stuStr = "未答";
        }
        setHtmlOnWebView(wv_content2, stuStr);
        setHtmlOnWebView(wv_content3, homeworkMarked.getShitiAnswer());

        stuStr = homeworkMarked.getShitiAnalysis();
        if (stuStr.length() == 0) {
            stuStr = "略";
        }
        setHtmlOnWebView(wv_content4, stuStr);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText("[" + homeworkMarked.getTypeName() + "]");

        if (!canMark) {
            checkBox.setVisibility(View.GONE);
            fl_score.setVisibility(View.GONE);
            tv_zero5.setVisibility(View.GONE);
        }

        // 同步学生分数
        if (!homeworkMarked.getStatus().equals("4")) {
            String stuScore = homeworkMarked.getStuscore();
            score = (int) (Float.parseFloat(stuScore));
            zero5 = stuScore.contains(".5") ? 1 : 0;
        }

        // 如果可以批改分数
        if (canMark) {
            if (zero5 == 1) {
                checkBox.setChecked(true);
            }

            // 点击事件
            checkBox.setOnClickListener(v -> {
                if (zero5 == 1) {
                    zero5 = 0;
                } else {
                    zero5 = 1;
                    if (score == -1) {
                        score = 0;
                    }
                    double nowScore = score + 0.5;
                    if (nowScore > Double.parseDouble(homeworkMarked.getQuestionScore())) {
                        zero5 = 0;
                        Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        zero5 = 1;
                    }
                }
                showScoreBtn();
                // 同步修改
                transmitInterface.setStuAnswer(position - 1, score + (zero5 == 1 ? ".5" : ".0"));
            });


            // 首次创建：初始化
            if (isFirst) {
                // 分数组件列表创建
                scoreNum = Integer.parseInt(homeworkMarked.getQuestionScore());
                btnArray = new Button[scoreNum + 1];
                viewArray = new View[scoreNum + 1];
            }

            // 动态创建打分按钮
            for (int i = 0; i < scoreNum + 1; ++i) {
                viewArray[i] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_score_btn, fl_score, false);
                btnArray[i] = viewArray[i].findViewById(R.id.btn_score);
                btnArray[i].setText(String.valueOf(i));
                btnArray[i].setTag(i);

                // 点击事件
                btnArray[i].setOnClickListener(view1 -> {
                    int idx = (int) view1.getTag();
                    if (score != idx) {
                        double nowScore = idx + (zero5 == 1 ? 0.5 : 0);
                        if (nowScore > Double.parseDouble(homeworkMarked.getQuestionScore())) {
                            Toast.makeText(getActivity(), "分数超过上限", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        score = idx;
                        showScoreBtn();

                        /**
                         * 测试：修改传入参数，是否能够实现实时更改
                         */

                        // 同步修改
                        transmitInterface.setStuAnswer(position - 1, score + (zero5 == 1 ? ".5" : ".0"));
                        if (window != null) {
                            window.dismiss();
                        }
                    }
                });
            }
            // ------------------------#
            //  可伸缩打分按钮设计优化
            // ------------------------#
            if (scoreNum > 15) {
                // 设计伸缩优化
                for (int i = scoreNum; i > scoreNum - 15; --i) {
                    fl_score.addView(viewArray[i]);
                }

                View plusView = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_score_btn, fl_score, false);
                Button plusBtn = plusView.findViewById(R.id.btn_score);
                plusBtn.setText("+");

                // 点击事件
                plusBtn.setOnClickListener(view1 -> {
                    // popUpWindows弹窗
                    showBtnPanel();
                });
                fl_score.addView(plusView);
            } else {
                // 不需要优化
                for (int i = scoreNum; i >= 0; --i) {
                    fl_score.addView(viewArray[i]);
                }
            }


            // 获取ViewTreeObserver
            ViewTreeObserver viewTreeObserver = fl_score.getViewTreeObserver();
            // 添加OnGlobalLayoutListener监听器
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 在布局加载完成后调用此方法
                    for (int i = 0; i < scoreNum + 1; ++i) {
                        ViewGroup.LayoutParams params = btnArray[i].getLayoutParams();
                        //params.width = fl_score.getWidth() / 8 - PxUtils.dip2px(view.getContext(), 20);
                        btnArray[i].setLayoutParams(params);
                    }
                    // 在需要的地方使用组件的宽度
                    // 例如，可以将它用于进行其他操作或调整UI
                    // ...

                    // 可选：如果你只想监听一次，可以在获取宽度后移除监听器
                    fl_score.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

            // 分数显示+按钮显示
            showScoreBtn();

        }

        // 分数显示
        if (homeworkMarked.getStatus().equals("4")) {
            tv_stu_scores.setText("[得分]  ");
        } else {
            tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));
        }

        isFirst = false;
        return view;
    }

    private void showBtnPanel() {
        // 创建一个 FlexboxLayout 实例
        if (popView == null) {
            popView = LayoutInflater.from(getActivity()).inflate(R.layout.t_homework_mark_btn_panel, null);
            FlexboxLayout popwindowView = popView.findViewById(R.id.fl_main);
            popView.findViewById(R.id.iv_close).setOnClickListener(v -> window.dismiss());

            for (int i = scoreNum - 15; i >= 0; --i) {
                popwindowView.addView(viewArray[i]);
            }

            window = new PopupWindow(
                    popView,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    true
            );
        }
        window.showAsDropDown(tv_stu_scores, 0, 0);

    }


    // 调整分数按钮显示效果
    private void showScoreBtn() {
        if (homeworkMarked.getQuestionType().equals("101") || homeworkMarked.getQuestionType().equals("103")) {
            // 单选和判断，取消中间分数
            for (int i = 1; i < scoreNum; ++i) {
                btnArray[i].setVisibility(View.GONE);
            }
            checkBox.setVisibility(View.GONE);
            tv_zero5.setVisibility(View.GONE);
        }


        for (int i = 0; i < scoreNum + 1; ++i) {
            if (score == i) {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report);
                btnArray[i].setTextColor(getResources().getColor(R.color.white));
            } else {
                btnArray[i].setBackgroundResource(R.drawable.t_homework_report_unselect);
                btnArray[i].setTextColor(getResources().getColor(R.color.main_bg));
            }
            tv_stu_scores.setText("[得分]  " + score + (zero5 == 1 ? ".5" : ""));
        }
        if (zero5 == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     * @param wb  WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str) {
        str = StringEscapeUtils.unescapeHtml4(str);
        Log.e(TAG, "setHtmlOnWebView: " + str);
        String html_content = "<head><style>" +
                "    p {\n" +
                "    margin: 0px;\n" +
                "    line-height: 30px;\n" +
                "    }\n" +
                "    body {\n" +
                "       line-height: 30px;\n" +
                "    }\n" +
                "</style>" +
                "<script>\n" +
                "function bigimage(x) {\n" +
                "    myInterface.bigPic(x.src)\n" +
                "}\n" +
                "</script>\n" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 14px; margin: 0px; padding: 0px\">" + str + "</body>";
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }


    /**
     * 第三方权限申请包AndPermission: 自带权限组名，可直接在Fragment中回调
     * 申请读写文件权限
     */
    private void permissionOpenGallery(String url) {
        // 权限请求
        oldUrl = url;
        Intent intent = new Intent(getActivity(), THomeworkImageMark.class);
        intent.putExtra("imageUrl", url); // 如果有需要传递的数据，可以使用 Intent 的 putExtra 方法
        startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE);
//        AndPermission.with(this)
//                .runtime()
//                .permission(Permission.Group.STORAGE)
//                .onGranted(new Action<List<String>>() {
//                    // 获取权限后
//                    @Override
//                    public void onAction(List<String> data) {
//                        oldUrl = url;
//                        EditImageActivity.start(getActivity(), THomeworkMarkFragment.this, url, null, 0);
//                    }
//                }).onDenied(new Action<List<String>>() {
//                    @Override
//                    public void onAction(List<String> data) {
//                        // 判断是否点了永远拒绝，不再提示
////
//                    }
//                })
//                .rationale(rGallery)
//                .start();
    }

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。读写文件申请
     */
    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。拍照申请
     */
    private Rationale rGallery = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("提示")
                    .setMessage("开启读写文件权限才能批改图片！")
                    .setPositiveButton("知道了", (dialog, which) -> {
                        executor.execute();
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        executor.cancel();
                    })
                    .show();
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {

            String newUrl = data.getStringExtra("newUrl");
            //int img_length = data.getIntExtra("img_length",0);
            stuStr = homeworkMarked.getStuAnswer().trim().replace(oldUrl, newUrl);
            setHtmlOnWebView(wv_content2, stuStr);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    setHtmlOnWebView(wv_content2, stuStr);
//                }
//            }, delay_time*1000); // 设置延迟时间为1.5秒

//            boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);
//            // 图片已修改
//            if (isImageEdit) {
//                Log.e(TAG, "onActivityResult: 图片已替换");
//                stuStr = homeworkMarked.getStuAnswer().trim().replace(oldUrl, newUrl);
//                Log.d("HSK","stuStr："+stuStr);
//                setHtmlOnWebView(wv_content2, stuStr);
//            }
        }
    }
}