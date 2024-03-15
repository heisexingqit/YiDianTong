package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.THomeworkCameraItem;
import com.example.yidiantong.util.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Collections;
import java.util.List;

public class THomeworkCameraRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "THomeworkCameraRecyclerAdapter";

    //打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;


    private List<THomeworkCameraItem> itemList;

    private Context mContext;

    private int showPos;
    private int showTimianET = -1;
    private int showAnalysisET = -1;
    private int showAnswerET = -1;

    //答题区域HTML头
    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 16px;" +
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

    // 构造函数初始化
    public THomeworkCameraRecyclerAdapter(Context context, List<THomeworkCameraItem> itemList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        mContext = context;
    }

    // 点击事件
    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void update(List<THomeworkCameraItem> myList) {
        itemList = myList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 1:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list, parent, false);
                break;
            case 2:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list_checkbox, parent, false);
                break;
            case 3:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list_judge, parent, false);
                break;
            case 4:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list_translation, parent, false);
                break;
            case 99:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list_seven, parent, false);
                break;
            case 100:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list_reading, parent, false);
                break;
            default:
                v = layoutInflater.inflate(R.layout.item_t_homework_camera_list, parent, false);
                break;
        }

        return new ItemViewHolder(v);
    }

    public int getItemViewType(int position) {
        THomeworkCameraItem item = itemList.get(position);
        switch (item.getBaseTypeId()) {
            case "101":
                return 1;
            case "102":
                return 2;
            case "103":
                return 3;
            case "104":
                return 4;
            case "108":
                if (item.getTypeName().indexOf("七") != -1) {
                    return 99;
                } else {
                    return 100;
                }
            default:
                return 4;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).update(position, holder);
    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }

    public void setShowPos(int showPos) {
        this.showPos = showPos;
    }

    public void addEditText(String txt) {
        if (txt.length() == 0) {
            showAnswerET = -1;
            showAnalysisET = -1;
            showTimianET = -1;
        }
        if (showTimianET != -1) {
            THomeworkCameraItem item = itemList.get(showTimianET);
            item.setShitiShow(item.getShitiShow() + "<p>" + txt + "</p>");
            showTimianET = -1;
        } else if (showAnswerET != -1) {
            THomeworkCameraItem item = itemList.get(showAnalysisET);
            item.setShitiAnswer(item.getShitiAnswer() + "<p>" + txt + "</p>");
            showAnswerET = -1;
        } else if (showAnalysisET != -1) {
            THomeworkCameraItem item = itemList.get(showAnalysisET);
            item.setShitiAnalysis(item.getShitiAnalysis() + "<p>" + txt + "</p>");
            showAnalysisET = -1;
        }
        this.notifyDataSetChanged();
    }

    /**
     * 核心
     * ItemHolder类
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        // 组件变量

        // 0 题目信息
        private final TextView tv_count;            // 题号
        private final TextView tv_type;             // 类型
        private final TextView tv_scores;           // 分值
        private final ClickableImageView iv_up;     // 上调
        private final ClickableImageView iv_down;   // 下调
        private final ClickableImageView iv_delete; // 删除

        // ② 题面和分析部分，公共
        // 1.1 相册 相机 文字（题面+分析）
        private final ClickableImageView iv_timian_gallery;
        private final ClickableImageView iv_timian_camera;
        private final ClickableImageView iv_timian_input;
        private final ClickableImageView iv_analysis_gallery;
        private final ClickableImageView iv_analysis_camera;
        private final ClickableImageView iv_analysis_input;
        private final ClickableImageView iv_timian_gallery2;
        private final ClickableImageView iv_timian_camera2;
        private final ClickableImageView iv_timian_input2;
        private final ClickableImageView iv_analysis_gallery2;
        private final ClickableImageView iv_analysis_camera2;
        private final ClickableImageView iv_analysis_input2;

        // 1.2 相册 相机 文字 （题面+分析）大小按钮布局
        private final LinearLayout ll_hide_timian;
        private final LinearLayout ll_hide_analysis;
        private final LinearLayout ll_show_timian;
        private final LinearLayout ll_show_analysis;

        // 1.3 输入组件（题面+分析）
        private final EditText et_timian;
        private final WebView wv_timian;
        private final ScrollView sv_timian;
        private final EditText et_analysis;
        private final WebView wv_analysis;
        private final ScrollView sv_analysis;

        // 1.4 修改题目类型
        private final ClickableImageView iv_change_type;

        // ② 答案部分，多类型
        // 2.1 加减小题组件
        private final ClickableTextView tv_answer_add;
        private final ClickableTextView tv_answer_minus;
        private final RadioGroup rg_answer;
        private final TextView tv_ans_num;

        // 2.2 答案LinearLayout
        private final LinearLayout ll_answer;

        // 2.3 判断题单选按钮
        private final RadioButton rb_true;
        private final RadioButton rb_false;

        // 2.4 简答题
        private final ClickableImageView iv_answer_gallery2;
        private final ClickableImageView iv_answer_camera2;
        private final ClickableImageView iv_answer_input2;
        private final ClickableImageView iv_answer_gallery;
        private final ClickableImageView iv_answer_camera;
        private final ClickableImageView iv_answer_input;
        private final LinearLayout ll_hide_answer;
        private final LinearLayout ll_show_answer;
        private final EditText et_answer;
        private final WebView wv_answer;
        private final ScrollView sv_answer;

        // 2.4.1 简答题图片放大
        private final LinearLayout ll_analysis;
        private final LinearLayout ll_timian;

        // ③ 底部折叠
        // 3.1 折叠&查看
        private final LinearLayout ll_show;
        private final LinearLayout ll_hide;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // 获取组件 （根据不同的题目类型，部分变量可能是null）
            iv_timian_gallery = itemView.findViewById(R.id.iv_timian_gallery);
            iv_timian_camera = itemView.findViewById(R.id.iv_timian_camera);
            iv_timian_input = itemView.findViewById(R.id.iv_timian_input);
            iv_analysis_gallery = itemView.findViewById(R.id.iv_analysis_gallery);
            iv_analysis_camera = itemView.findViewById(R.id.iv_analysis_camera);
            iv_analysis_input = itemView.findViewById(R.id.iv_analysis_input);
            iv_timian_gallery2 = itemView.findViewById(R.id.iv_timian_gallery2);
            iv_timian_camera2 = itemView.findViewById(R.id.iv_timian_camera2);
            iv_timian_input2 = itemView.findViewById(R.id.iv_timian_input2);
            iv_analysis_gallery2 = itemView.findViewById(R.id.iv_analysis_gallery2);
            iv_analysis_camera2 = itemView.findViewById(R.id.iv_analysis_camera2);
            iv_analysis_input2 = itemView.findViewById(R.id.iv_analysis_input2);
            tv_answer_add = itemView.findViewById(R.id.tv_answer_add);
            tv_answer_minus = itemView.findViewById(R.id.tv_answer_minus);
            rg_answer = itemView.findViewById(R.id.rg_answer);
            tv_ans_num = itemView.findViewById(R.id.tv_ans_num);
            tv_count = itemView.findViewById(R.id.tv_count);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_scores = itemView.findViewById(R.id.tv_scores);
            iv_up = itemView.findViewById(R.id.iv_up);
            iv_down = itemView.findViewById(R.id.iv_down);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            ll_show = itemView.findViewById(R.id.ll_show);
            ll_hide = itemView.findViewById(R.id.ll_hide);
            ll_hide_timian = itemView.findViewById(R.id.ll_hide_timian);
            ll_hide_analysis = itemView.findViewById(R.id.ll_hide_analysis);
            ll_show_timian = itemView.findViewById(R.id.ll_show_timian);
            ll_show_analysis = itemView.findViewById(R.id.ll_show_analysis);
            et_timian = itemView.findViewById(R.id.et_timian);
            wv_timian = itemView.findViewById(R.id.wv_timian);
            sv_timian = itemView.findViewById(R.id.sv_timian);
            et_analysis = itemView.findViewById(R.id.et_analysis);
            wv_analysis = itemView.findViewById(R.id.wv_analysis);
            sv_analysis = itemView.findViewById(R.id.sv_analysis);
            ll_answer = itemView.findViewById(R.id.ll_answer);
            rb_true = itemView.findViewById(R.id.rb_true);
            rb_false = itemView.findViewById(R.id.rb_false);
            iv_answer_gallery = itemView.findViewById(R.id.iv_answer_gallery);
            iv_answer_gallery2 = itemView.findViewById(R.id.iv_answer_gallery2);
            iv_answer_camera = itemView.findViewById(R.id.iv_answer_camera);
            iv_answer_camera2 = itemView.findViewById(R.id.iv_answer_camera2);
            iv_answer_input = itemView.findViewById(R.id.iv_answer_input);
            iv_answer_input2 = itemView.findViewById(R.id.iv_answer_input2);
            ll_hide_answer = itemView.findViewById(R.id.ll_hide_answer);
            ll_show_answer = itemView.findViewById(R.id.ll_show_answer);
            et_answer = itemView.findViewById(R.id.et_answer);
            wv_answer = itemView.findViewById(R.id.wv_answer);
            sv_answer = itemView.findViewById(R.id.sv_answer);
            iv_change_type = itemView.findViewById(R.id.iv_change_type);
            ll_analysis = itemView.findViewById(R.id.ll_analysis);
            ll_timian = itemView.findViewById(R.id.ll_timian);
        }


        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void update(int pos, RecyclerView.ViewHolder holder) {

            // 第一块：公共部分
            // WebView点击查看大图组件
            if (wv_timian != null) {
                WebSettings webSettings = wv_timian.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv_timian.addJavascriptInterface(
                        new Object() {
                            @JavascriptInterface
                            @SuppressLint("JavascriptInterface")
                            public void bigPic() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里执行主线程的UI操作
                                        mItemClickListener.openImage(pos, "Show");
                                    }
                                });
                            }
                        }
                        , "myInterface");
            }
            if (wv_answer != null) {
                WebSettings webSettings = wv_answer.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv_answer.addJavascriptInterface(
                        new Object() {
                            @JavascriptInterface
                            @SuppressLint("JavascriptInterface")
                            public void bigPic() {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里执行主线程的UI操作
                                        mItemClickListener.openImage(pos, "Answer");

                                    }
                                });
                            }
                        }
                        , "myInterface");
            }
            if (wv_analysis != null) {
                WebSettings webSettings = wv_analysis.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv_analysis.addJavascriptInterface(
                        new Object() {
                            @JavascriptInterface
                            @SuppressLint("JavascriptInterface")
                            public void bigPic() {
                                // 使用Handler在主线程上执行UI操作
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 在这里执行主线程的UI操作
                                        mItemClickListener.openImage(pos, "Analysis");
                                    }
                                });
                            }
                        }
                        , "myInterface");
            }
            // 点击大图 复合触发
            ll_timian.setOnClickListener(v -> {
                mItemClickListener.openImage(pos, "Show");
            });
            ll_analysis.setOnClickListener(v -> {
                mItemClickListener.openImage(pos, "Analysis");
            });

            // 数据
            // 绑定组件
            THomeworkCameraItem item = itemList.get(pos);


            // 同步顶栏信息 【不变】
            int positionLen = String.valueOf(pos + 1).length();
            String questionNum = (pos + 1) + "/" + itemList.size();
            SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
            tv_count.setText(spannableString);
            tv_type.setText(item.getTypeName());
            tv_scores.setText("(" + item.getScore() + "分)");

            // 【答案部分】
            // 同步答案选项数量
            if (item.getBaseTypeId().equals("108") && (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1)) {
                tv_ans_num.setText(item.getSmallQueNum());
            } else if (item.getBaseTypeId().equals("101") || item.getBaseTypeId().equals("102")) {
                tv_ans_num.setText(item.getAnswerNum());
            }

            // 公共资源
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,  // 这里可以设置视图的宽度，也可以使用具体的像素值，如 ViewGroup.LayoutParams.MATCH_PARENT
                    ViewGroup.LayoutParams.WRAP_CONTENT   // 这里可以设置视图的高度，也可以使用具体的像素值，如 ViewGroup.LayoutParams.WRAP_CONTENT
            );

            // 滚动冲突
            NestedScrollView sv_main = mItemClickListener.getParentScrollView();
            View.OnTouchListener childTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    sv_main.requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            };

            // 复杂的点击监听器总和
            View.OnClickListener answerListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean noUpdate = false;
                    int ansNum = Integer.parseInt(item.getAnswerNum());
                    int queNum = Integer.parseInt(item.getSmallQueNum());
                    switch (view.getId()) {

                        case R.id.tv_answer_add:
                            if (item.getBaseTypeId().equals("108") && (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1)) {
                                // 添加小题
                                String answerStr = item.getShitiAnswer();

                                if (answerStr.length() > 0) {
                                    answerStr += ",";
                                    item.setShitiAnswer(answerStr);
                                }
                                queNum += 1;
                                item.setSmallQueNum(String.valueOf(queNum));
                            } else {

                                // 添加选项
                                ansNum += 1;
                                item.setAnswerNum(String.valueOf(ansNum));
                            }
                            break;

                        case R.id.tv_answer_minus:

                            if (item.getBaseTypeId().equals("108") && (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1)) {

                                // 限制最小值
                                if (queNum == 1) {
                                    Toast.makeText(mContext, "小题数不能小于1", Toast.LENGTH_SHORT).show();
                                    break;
                                }

                                // 答案合法性纠正
                                String answerStr = item.getShitiAnswer();

                                if (answerStr.length() > 0) {
                                    int lastIndex = answerStr.lastIndexOf(",");
                                    if (lastIndex != -1) {
                                        answerStr = answerStr.substring(0, lastIndex);
                                    }
                                    item.setShitiAnswer(answerStr);
                                }

                                queNum -= 1;
                                item.setSmallQueNum(String.valueOf(queNum));

                            } else {
                                if (ansNum == 3) {
                                    Toast.makeText(mContext, "选项不能小于3", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                ansNum -= 1;

                                String answerStr = item.getShitiAnswer();
                                if (answerStr.length() > 0) {
                                    char mxChar = (char) ('A' + ansNum);

                                    if (answerStr.charAt(0) == mxChar) {
                                        item.setShitiAnswer("");
                                    }
                                }
                                item.setAnswerNum(String.valueOf(ansNum));
                            }
                            break;
                        case R.id.iv_timian_input:
                        case R.id.iv_timian_input2:
                            et_timian.setVisibility(View.VISIBLE);
                            showTimianET = pos;
                            showAnswerET = -1;
                            showAnalysisET = -1;
                            break;
                        case R.id.iv_analysis_input:
                        case R.id.iv_analysis_input2:
                            et_analysis.setVisibility(View.VISIBLE);
                            showTimianET = -1;
                            showAnswerET = -1;
                            showAnalysisET = pos;
                            break;
                        case R.id.iv_timian_camera:
                        case R.id.iv_timian_camera2:
                            noUpdate = true;
                            mItemClickListener.openCamera(view, pos, "Show", wv_timian);
                            break;
                        case R.id.iv_timian_gallery:
                        case R.id.iv_timian_gallery2:
                            noUpdate = true;
                            mItemClickListener.openGallery(view, pos, "Show", wv_timian);
                            break;
                        case R.id.iv_analysis_camera:
                        case R.id.iv_analysis_camera2:
                            noUpdate = true;
                            mItemClickListener.openCamera(view, pos, "Analysis", wv_analysis);
                            break;
                        case R.id.iv_analysis_gallery:
                        case R.id.iv_analysis_gallery2:
                            noUpdate = true;
                            mItemClickListener.openGallery(view, pos, "Analysis", wv_analysis);
                            break;
                        case R.id.iv_answer_input:
                        case R.id.iv_answer_input2:
                            et_answer.setVisibility(View.VISIBLE);
                            showTimianET = -1;
                            showAnswerET = pos;
                            showAnalysisET = -1;
                            break;
                        case R.id.iv_answer_camera:
                        case R.id.iv_answer_camera2:
                            noUpdate = true;
                            mItemClickListener.openCamera(view, pos, "Answer", wv_answer);
                            break;
                        case R.id.iv_answer_gallery:
                        case R.id.iv_answer_gallery2:
                            noUpdate = true;
                            mItemClickListener.openGallery(view, pos, "Answer", wv_answer);
                            break;
                        // 需要update部分
                        case R.id.iv_delete:
                            itemList.remove(item);
                            if (itemList.size() == 0) {
                                mItemClickListener.showHideEmpty(false);
                            }
                            break;
                        case R.id.iv_up:
                            if (pos == 0) {
                                Toast.makeText(mContext, "已经是第一题", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            Collections.swap(itemList, pos, pos - 1);
                            break;
                        case R.id.iv_down:
                            if (pos == itemList.size() - 1) {
                                Toast.makeText(mContext, "已经是最后一题", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            Collections.swap(itemList, pos, pos + 1);
                            break;
                        case R.id.ll_show:
                            showPos = pos;
                            break;
                        case R.id.iv_change_type:
                            mItemClickListener.changeType(pos);
                            break;
                    }
                    if (!noUpdate) {
                        THomeworkCameraRecyclerAdapter.this.notifyDataSetChanged();
                    }

                }
            };

            switch (item.getBaseTypeId()) {
                case "101":
                    /**
                     * 单选题型 专属
                     */
                    // *************************
                    //  清除radioGroup之前的监听器
                    //  不然会导致多次触发，赋值混乱
                    // *************************
                    rg_answer.setOnCheckedChangeListener(null); // 先移除之前的监听器，防止触发多次
                    rg_answer.clearCheck(); // 清除之前的选择状态
                    rg_answer.removeAllViews();

                    char checkChar = ' ';

                    if (item.getShitiAnswer().length() > 0) {
                        checkChar = Jsoup.clean(item.getShitiAnswer(), Whitelist.none()).trim().charAt(0);
                    }

                    // 同步
                    for (int i = 0; i < Integer.parseInt(item.getAnswerNum()); ++i) {

                        char word = (char) ('A' + i);
                        ContextThemeWrapper wrapper = new ContextThemeWrapper(mContext, R.style.CustomRadioStyle);
                        RadioButton radioButton = new RadioButton(wrapper);

                        // 设置 margin 值，参数分别为左、上、右、下的间距（以像素为单位）
                        layoutParams.setMargins(5, 0, 5, 0);
                        radioButton.setLayoutParams(layoutParams);
                        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        radioButton.setTextColor(mContext.getResources().getColor(R.color.default_gray));
                        radioButton.setText(String.valueOf(word));
                        rg_answer.addView(radioButton);

                        if (word == checkChar) {
                            radioButton.setChecked(true);
                        } else {
                            radioButton.setChecked(false);
                        }
                    }

                    rg_answer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            RadioButton selectedRadioButton = holder.itemView.findViewById(i);
                            item.setShitiAnswer(selectedRadioButton.getText().toString());
                        }
                    });
                    break;
                case "102":
                    /**
                     * 多选题型 专属
                     */

                    ll_answer.removeAllViews();
                    ColorStateList colorStateList = mContext.getColorStateList(R.color.checkbox_color);

                    // 同步
                    String checkString = "";
                    if (item.getShitiAnswer().length() > 0) {
                        checkString = Jsoup.clean(item.getShitiAnswer(), Whitelist.none()).trim();
                    }

                    for (int i = 0; i < Integer.parseInt(item.getAnswerNum()); ++i) {
                        char word = (char) ('A' + i);
                        CheckBox checkBox = new CheckBox(mContext);
                        checkBox.setButtonTintList(colorStateList);

                        // 设置 margin 值，参数分别为左、上、右、下的间距（以像素为单位）
                        layoutParams.setMargins(5, 0, 5, 0);
                        checkBox.setLayoutParams(layoutParams);
                        checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        checkBox.setTextColor(mContext.getResources().getColor(R.color.default_gray));
                        checkBox.setText(String.valueOf(word));

                        // 监听状态
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                // 处理 checkBox1 的选中状态变化
                                String answerString = item.getShitiAnswer();

                                if (b) {
                                    // 复选框被选中
                                    answerString += StringUtils.sortString(compoundButton.getText().toString());


                                } else {
                                    // 复选框被取消选中
                                    answerString = answerString.replaceAll(compoundButton.getText().toString(), "");
                                }
                                item.setShitiAnswer(answerString);
                            }
                        });
                        ll_answer.addView(checkBox);
                        if (checkString.contains(String.valueOf(word))) {
                            checkBox.setChecked(true);
                        }
                    }
                    break;
                case "103":
                    /**
                     * 判断题型 专属
                     */
                    rg_answer.removeAllViews();

                    String checkStr = "";
                    if (item.getShitiAnswer().length() > 0) {
                        checkStr = Jsoup.clean(item.getShitiAnswer(), Whitelist.none()).trim();
                    }

                    // 同步
                    if (checkStr.equals("对")) {
                        rb_true.setChecked(true);
                    } else if (checkStr.equals("错")) {
                        rb_false.setChecked(true);
                    }
                    rg_answer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            RadioButton selectedRadioButton = holder.itemView.findViewById(i);
                            item.setShitiAnswer(selectedRadioButton.getText().toString());

                        }
                    });
                    break;
                case "108":
                    if (item.getTypeName().indexOf("七选五") != -1) {

                        Log.d("wen", "update: 七选五");

                        ll_answer.removeAllViews();
                        String[] strings = new String[0];
                        if (item.getShitiAnswer().indexOf("<p>") == -1) {
                            strings = item.getShitiAnswer().split(",");
                        }

                        for (int i = 0; i < 5; ++i) {
                            View view = layoutInflater.inflate(R.layout.item_homework_seven_radiobutton, ll_answer, false);
                            RadioGroup rg = view.findViewById(R.id.rg_answer);

                            // 同步
                            if (i < strings.length) {

                                RadioButton radioButton1 = view.findViewById(R.id.rb_a);
                                RadioButton radioButton2 = view.findViewById(R.id.rb_b);
                                RadioButton radioButton3 = view.findViewById(R.id.rb_c);
                                RadioButton radioButton4 = view.findViewById(R.id.rb_d);
                                RadioButton radioButton5 = view.findViewById(R.id.rb_e);
                                RadioButton radioButton6 = view.findViewById(R.id.rb_f);
                                RadioButton radioButton7 = view.findViewById(R.id.rb_g);

                                String data = strings[i];
                                if (data.equals("A")) {
                                    radioButton1.setChecked(true);
                                } else if (data.equals("B")) {
                                    radioButton2.setChecked(true);
                                } else if (data.equals("C")) {
                                    radioButton3.setChecked(true);
                                } else if (data.equals("D")) {
                                    radioButton4.setChecked(true); // 选中第四个选项或其他处理逻辑
                                } else if (data.equals("E")) {
                                    radioButton5.setChecked(true); // 选中第四个选项或其他处理逻辑
                                } else if (data.equals("F")) {
                                    radioButton6.setChecked(true); // 选中第四个选项或其他处理逻辑
                                } else if (data.equals("G")) {
                                    radioButton7.setChecked(true); // 选中第四个选项或其他处理逻辑
                                }
                            }

                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                                    String ansStr = "";
                                    for (int j = 0; j < 5; j++) {
                                        RadioGroup child = (RadioGroup) ll_answer.getChildAt(j);
                                        RadioButton selectedRadioButton = holder.itemView.findViewById(child.getCheckedRadioButtonId());
                                        String str = "";
                                        if (selectedRadioButton != null) {
                                            str = selectedRadioButton.getText().toString();
                                        }

                                        if (j != 0) {
                                            ansStr += ",";
                                        }
                                        ansStr += str;

                                    }

                                    item.setShitiAnswer(ansStr);

                                    Log.d("wen", "onCheckedChanged: " + ansStr);
                                }
                            });
                            ll_answer.addView(view);
                        }

                    } else if (item.getTypeName().indexOf("阅读理解") != -1 || item.getTypeName().indexOf("完形填空") != -1) {
                        Log.d("wen", "update: 阅读");

                        ll_answer.removeAllViews();
                        String[] strings = new String[0];
                        if (item.getShitiAnswer().indexOf("<p>") == -1) {
                            strings = item.getShitiAnswer().split(",");
                        }

                        for (int i = 0; i < Integer.parseInt(item.getSmallQueNum()); ++i) {
                            View view = layoutInflater.inflate(R.layout.item_homework_reading_radiobutton, ll_answer, false);
                            RadioGroup rg = view.findViewById(R.id.rg_answer);

                            // 同步
                            if (strings.length > i && strings[i].length() > 0) {
                                RadioButton radioButton1 = view.findViewById(R.id.rb_a);
                                RadioButton radioButton2 = view.findViewById(R.id.rb_b);
                                RadioButton radioButton3 = view.findViewById(R.id.rb_c);
                                RadioButton radioButton4 = view.findViewById(R.id.rb_d);
                                String data = strings[i];
                                if (data.equals("A")) {
                                    radioButton1.setChecked(true);
                                } else if (data.equals("B")) {
                                    radioButton2.setChecked(true);
                                } else if (data.equals("C")) {
                                    radioButton3.setChecked(true);
                                } else if (data.equals("D")) {
                                    radioButton4.setChecked(true); // 选中第四个选项或其他处理逻辑
                                }
                            }

                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                                    String ansStr = "";
                                    for (int j = 0; j < ll_answer.getChildCount(); j++) {
                                        RadioGroup child = (RadioGroup) ll_answer.getChildAt(j);
                                        RadioButton selectedRadioButton = holder.itemView.findViewById(child.getCheckedRadioButtonId());
                                        String str = "";
                                        if (selectedRadioButton != null) {
                                            str = selectedRadioButton.getText().toString();
                                        }

                                        if (j != 0) {
                                            ansStr += "," + str;
                                        } else {
                                            ansStr += str;
                                        }
                                    }

                                    item.setShitiAnswer(ansStr);

                                    Log.d("wen", "onCheckedChanged: " + ansStr);
                                }
                            });
                            ll_answer.addView(view);
                        }
                    }
                    break;
                default:
                    /**
                     * 简答题型 专属
                     */
                    // 输入按钮变换
                    if (item.getShitiAnswer().length() > 0) {
                        ll_hide_answer.setVisibility(View.VISIBLE);
                        ll_show_answer.setVisibility(View.GONE);
                        wv_answer.setVisibility(View.VISIBLE);
                        wv_answer.loadDataWithBaseURL(null, html_answer_head + item.getShitiAnswer() + html_answer_tail, "text/html", "utf-8", null);

                    } else {
                        //**********************
                        //  注意内容为空时同步视图
                        //**********************
                        wv_answer.setVisibility(View.GONE);
                        ll_hide_answer.setVisibility(View.GONE);
                        ll_show_answer.setVisibility(View.VISIBLE);
                        wv_timian.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
                    }

                    // 捕获回车键
                    et_answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                            // 判断动作是否为回车键按下
                            if (actionId == EditorInfo.IME_ACTION_DONE ||
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                                hideKeyboard(et_answer);

                                // 在这里执行你的回车键响应逻辑
                                // 例如，可以触发提交表单、搜索等操作
                                // 返回 true 表示已处理该事件
                                if (et_answer.getText().toString().length() != 0) {
                                    item.setShitiAnswer(item.getShitiAnswer() + "<p>" + et_answer.getText().toString() + "</p>");
                                    et_answer.setText("");
                                }
                                showAnswerET = -1;
                                THomeworkCameraRecyclerAdapter.this.notifyDataSetChanged();
                                Log.d("wen", "onEditorAction: " + item.getShitiShow());

                                return true;
                            }
                            return false;
                        }
                    });


                    // 输入按钮变换
                    if (item.getShitiAnswer().length() > 0 || showAnswerET == pos) {
                        ll_hide_answer.setVisibility(View.VISIBLE);
                        ll_show_answer.setVisibility(View.GONE);
                        if (item.getShitiAnswer().length() > 0) {
                            wv_answer.setVisibility(View.VISIBLE);
                        } else {
                            wv_answer.setVisibility(View.GONE);
                        }
                        wv_answer.loadDataWithBaseURL(null, html_answer_head + item.getShitiAnswer() + html_answer_tail, "text/html", "utf-8", null);

                    } else {
                        //**********************
                        //  注意内容为空时同步视图
                        //**********************
                        wv_answer.setVisibility(View.GONE);
                        ll_hide_answer.setVisibility(View.GONE);
                        ll_show_answer.setVisibility(View.VISIBLE);
                    }
                    iv_answer_input.setOnClickListener(answerListener);
                    iv_answer_input2.setOnClickListener(answerListener);
                    iv_answer_camera.setOnClickListener(answerListener);
                    iv_answer_camera2.setOnClickListener(answerListener);
                    iv_answer_gallery.setOnClickListener(answerListener);
                    iv_answer_gallery2.setOnClickListener(answerListener);

                    // input 框动画
                    if (showAnswerET == pos) {
                        et_answer.setVisibility(View.VISIBLE);
                        et_answer.requestFocus();
                    } else {
                        et_answer.setVisibility(View.GONE);
                        et_answer.setText("");
                    }

                    // 滚动冲突
                    sv_answer.setOnTouchListener(childTouchListener);
                    wv_analysis.setOnTouchListener(childTouchListener);
                    ll_answer.setOnClickListener(v -> {
                        mItemClickListener.openImage(pos, "Answer");
                    });
                    break;
            }

            tv_answer_add.setOnClickListener(answerListener);
            tv_answer_minus.setOnClickListener(answerListener);
            iv_up.setOnClickListener(answerListener);
            iv_down.setOnClickListener(answerListener);
            iv_delete.setOnClickListener(answerListener);
            ll_show.setOnClickListener(answerListener);
            iv_timian_input.setOnClickListener(answerListener);
            iv_timian_input2.setOnClickListener(answerListener);
            iv_analysis_input.setOnClickListener(answerListener);
            iv_analysis_input2.setOnClickListener(answerListener);
            iv_change_type.setOnClickListener(answerListener);

            // 隐藏&查看
            if (showPos != pos) {
                ll_hide.setVisibility(View.GONE);
                ll_show.setVisibility(View.VISIBLE);
            } else {
                ll_hide.setVisibility(View.VISIBLE);
                ll_show.setVisibility(View.GONE);
            }

            // 空列表判断
            if (itemList.size() > 0) {
                mItemClickListener.showHideEmpty(true);
            }

            // 输入按钮变换
            if (item.getShitiShow().length() > 0 || showTimianET == pos) {
                ll_hide_timian.setVisibility(View.VISIBLE);
                ll_show_timian.setVisibility(View.GONE);
                if (item.getShitiShow().length() > 0) {
                    wv_timian.setVisibility(View.VISIBLE);
                } else {
                    wv_timian.setVisibility(View.GONE);
                }
                wv_timian.loadDataWithBaseURL(null, html_answer_head + item.getShitiShow() + html_answer_tail, "text/html", "utf-8", null);

                if (showTimianET == pos) {
                    et_timian.requestFocus();
                }
            } else {
                //**********************
                //  注意内容为空时同步视图
                //**********************
                wv_timian.setVisibility(View.GONE);
                ll_hide_timian.setVisibility(View.GONE);
                ll_show_timian.setVisibility(View.VISIBLE);
            }

            // 输入按钮变换
            if (item.getShitiAnalysis().length() > 0 || showAnalysisET == pos) {
                ll_hide_analysis.setVisibility(View.VISIBLE);
                ll_show_analysis.setVisibility(View.GONE);
                if (item.getShitiAnalysis().length() > 0) {
                    wv_analysis.setVisibility(View.VISIBLE);
                } else {
                    wv_analysis.setVisibility(View.GONE);
                }
                wv_analysis.loadDataWithBaseURL(null, html_answer_head + item.getShitiAnalysis() + html_answer_tail, "text/html", "utf-8", null);

                if (showAnalysisET == pos) {
                    et_analysis.requestFocus();
                }
            } else {
                //**********************
                //  注意内容为空时同步视图
                //**********************
                wv_analysis.setVisibility(View.GONE);
                ll_hide_analysis.setVisibility(View.GONE);
                ll_show_analysis.setVisibility(View.VISIBLE);
            }

            // 捕获回车键
            et_timian.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    // 判断动作是否为回车键按下
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        hideKeyboard(et_timian);
                        // 在这里执行你的回车键响应逻辑
                        // 例如，可以触发提交表单、搜索等操作
                        // 返回 true 表示已处理该事件
                        if (et_timian.getText().toString().length() != 0) {
                            item.setShitiShow(item.getShitiShow() + "<p>" + et_timian.getText().toString() + "</p>");
                            et_timian.setText("");
                        }
                        showTimianET = -1;
                        THomeworkCameraRecyclerAdapter.this.notifyDataSetChanged();

                        Log.d("wen", "onEditorAction: " + item.getShitiShow());
                        return true;
                    }
                    return false;
                }
            });

            // 捕获回车键
            et_analysis.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    // 判断动作是否为回车键按下
                    if (actionId == EditorInfo.IME_ACTION_DONE ||
                            event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        hideKeyboard(et_analysis);
                        // 在这里执行你的回车键响应逻辑
                        // 例如，可以触发提交表单、搜索等操作
                        // 返回 true 表示已处理该事件
                        if (et_analysis.getText().toString().length() != 0) {
                            item.setShitiAnalysis(item.getShitiAnalysis() + "<p>" + et_analysis.getText().toString() + "</p>");
                            et_analysis.setText("");
                        }
                        showAnalysisET = -1;
                        THomeworkCameraRecyclerAdapter.this.notifyDataSetChanged();
                        Log.d("wen", "onEditorAction: " + item.getShitiShow());

                        return true;
                    }
                    return false;
                }
            });

            // 展示输入框
            if (showTimianET == pos) {
                et_timian.setVisibility(View.VISIBLE);
            } else {
                et_timian.setVisibility(View.GONE);
                et_timian.setText("");
            }
            if (showAnalysisET == pos) {
                et_analysis.setVisibility(View.VISIBLE);
            } else {
                et_analysis.setVisibility(View.GONE);
                et_analysis.setText("");
            }

            /**
             * 滚动冲突处理
             */

            sv_timian.setOnTouchListener(childTouchListener);
            sv_analysis.setOnTouchListener(childTouchListener);
            wv_timian.setOnTouchListener(childTouchListener);
            wv_analysis.setOnTouchListener(childTouchListener);

            // 图片上传
            iv_timian_camera.setOnClickListener(answerListener);
            iv_timian_camera2.setOnClickListener(answerListener);
            iv_timian_gallery.setOnClickListener(answerListener);
            iv_timian_gallery2.setOnClickListener(answerListener);
            iv_analysis_camera.setOnClickListener(answerListener);
            iv_analysis_camera2.setOnClickListener(answerListener);
            iv_analysis_gallery.setOnClickListener(answerListener);
            iv_analysis_gallery2.setOnClickListener(answerListener);

        }

        private void hideKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public interface MyItemClickListener {
        void openGallery(View view, int pos, String type, WebView wv);

        void openCamera(View view, int pos, String type, WebView wv);

        void showHideEmpty(Boolean isHide);

        NestedScrollView getParentScrollView();

        void changeType(int pos);

        void openImage(int pos, String type);
    }
}
