package com.example.yidiantong.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.THomeworkCameraItem;
import com.example.yidiantong.ui.THomeworkCameraAddPickActivity;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;

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


    private THomeworkCameraAddPickActivity.CustomLinearLayoutManager manager;

    public void setManager(THomeworkCameraAddPickActivity.CustomLinearLayoutManager manager) {
        this.manager = manager;
    }

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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_t_homework_camera_list, parent, false);
        return new ItemViewHolder(v);
    }

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

    /**
     * 核心
     * ItemHolder类
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
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
        private final ClickableTextView tv_answer_add;
        private final ClickableTextView tv_answer_minus;
        private final RadioGroup rg_answer;
        private final TextView tv_ans_num;
        private final TextView tv_count;
        private final TextView tv_type;
        private final TextView tv_scores;
        private final ClickableImageView iv_up;
        private final ClickableImageView iv_down;
        private final ClickableImageView iv_delete;
        // 折叠&查看
        private final LinearLayout ll_show;
        private final LinearLayout ll_hide;

        // 输入按钮变换
        private final LinearLayout ll_hide_timian;
        private final LinearLayout ll_hide_analysis;
        private final LinearLayout ll_show_timian;
        private final LinearLayout ll_show_analysis;

        private final EditText et_timian;
        private final WebView wv_timian;
        private final ScrollView sv_timian;
        private final LinearLayout ll_root;

        private final EditText et_analysis;
        private final WebView wv_analysis;

        private final ScrollView sv_analysis;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
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
            ll_root = itemView.findViewById(R.id.ll_root);
            et_analysis = itemView.findViewById(R.id.et_analysis);
            wv_analysis = itemView.findViewById(R.id.wv_analysis);
            sv_analysis = itemView.findViewById(R.id.sv_analysis);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos, RecyclerView.ViewHolder holder) {
            //数据
            //绑定组件
            THomeworkCameraItem item = itemList.get(pos);

            // 同步答案数
            tv_ans_num.setText(item.getAnswerNum());
            // 同步顶栏信息
            int positionLen = String.valueOf(pos + 1).length();
            String questionNum = (pos + 1) + "/" + itemList.size();
            SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
            tv_count.setText(spannableString);
            tv_type.setText(item.getTypeName());
            tv_scores.setText("(" + item.getScore() + "分)");

            /**
             * 单选题型 专属
             */
            rg_answer.removeAllViews();

            ContextThemeWrapper wrapper = new ContextThemeWrapper(mContext, R.style.CustomRadioStyle);
            for (int i = 0; i < Integer.parseInt(item.getAnswerNum()); ++i) {
                char word = (char) ('A' + i);
                RadioButton radioButton = new RadioButton(wrapper);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,  // 这里可以设置视图的宽度，也可以使用具体的像素值，如 ViewGroup.LayoutParams.MATCH_PARENT
                        ViewGroup.LayoutParams.WRAP_CONTENT   // 这里可以设置视图的高度，也可以使用具体的像素值，如 ViewGroup.LayoutParams.WRAP_CONTENT
                );
                // 设置 margin 值，参数分别为左、上、右、下的间距（以像素为单位）
                layoutParams.setMargins(5, 0, 20, 0);
                radioButton.setLayoutParams(layoutParams);
                radioButton.setTextSize(PxUtils.dip2px(mContext, 8));
                radioButton.setTextColor(mContext.getResources().getColor(R.color.default_gray));
                radioButton.setText(String.valueOf(word));
                rg_answer.addView(radioButton);
            }
            rg_answer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    RadioButton selectedRadioButton = holder.itemView.findViewById(i);
                    item.setShitiAnswer(selectedRadioButton.getText().toString());
                }
            });

            // 修改单选按钮
            View.OnClickListener answerListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int ansNum = Integer.parseInt(itemList.get(pos).getAnswerNum());
                    switch (view.getId()) {
                        case R.id.tv_answer_add:
                            ansNum += 1;
                            itemList.get(pos).setAnswerNum(String.valueOf(ansNum));
                            break;
                        case R.id.tv_answer_minus:
                            if (ansNum == 3) {
                                Toast.makeText(mContext, "选项不能小于3", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            ansNum -= 1;
                            itemList.get(pos).setAnswerNum(String.valueOf(ansNum));
                            break;
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
                        case R.id.iv_timian_input:
                        case R.id.iv_timian_input2:
                            if (item.getShitiShow() == null) {
                                item.setShitiShow("");
                            }
                            showTimianET = pos;
                            break;
                            case R.id.iv_analysis_input:
                        case R.id.iv_analysis_input2:
                            if (item.getShitiAnalysis() == null) {
                                item.setShitiAnalysis("");
                            }
                            showAnalysisET = pos;
                            break;
                        case R.id.iv_timian_camera:
                        case R.id.iv_timian_camera2:
                            mItemClickListener.openCamera(view, pos, "Show");
                            break;
                        case R.id.iv_timian_gallery:
                        case R.id.iv_timian_gallery2:
                            mItemClickListener.openGallery(view, pos, "Show");
                            break;
                        case R.id.iv_analysis_camera:
                        case R.id.iv_analysis_camera2:
                            mItemClickListener.openCamera(view, pos, "Analysis");
                            break;
                        case R.id.iv_analysis_gallery:
                        case R.id.iv_analysis_gallery2:
                            mItemClickListener.openGallery(view, pos, "Analysis");
                            break;



                    }
                    THomeworkCameraRecyclerAdapter.this.notifyDataSetChanged();
                }
            };
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
            if (item.getShitiShow() != null) {
                ll_hide_timian.setVisibility(View.VISIBLE);
                ll_show_timian.setVisibility(View.GONE);
                if (item.getShitiShow().length() > 0) {
                    wv_timian.loadData(html_answer_head + item.getShitiShow() + html_answer_tail, "text/html", "utf-8");
                }
            } else {
                ll_hide_timian.setVisibility(View.GONE);
                ll_show_timian.setVisibility(View.VISIBLE);
            }


            // 输入按钮变换
            if (item.getShitiAnalysis() != null) {
                ll_hide_analysis.setVisibility(View.VISIBLE);
                ll_show_analysis.setVisibility(View.GONE);
                if (item.getShitiAnalysis().length() > 0) {
                    wv_timian.loadData(html_answer_head + item.getShitiAnalysis() + html_answer_tail, "text/html", "utf-8");
                }
            } else {
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
                        // 在这里执行你的回车键响应逻辑
                        // 例如，可以触发提交表单、搜索等操作
                        // 返回 true 表示已处理该事件
                        item.setShitiShow(item.getShitiShow() + "<p>" + et_timian.getText().toString() + "</p>");
                        wv_timian.loadData(html_answer_head + item.getShitiShow() + html_answer_tail, "text/html", "utf-8");
                        et_timian.setVisibility(View.GONE);
                        showTimianET = -1;
                        et_timian.setText("");
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
                        // 在这里执行你的回车键响应逻辑
                        // 例如，可以触发提交表单、搜索等操作
                        // 返回 true 表示已处理该事件
                        item.setShitiAnalysis(item.getShitiAnalysis() + "<p>" + et_analysis.getText().toString() + "</p>");
                        wv_analysis.loadData(html_answer_head + item.getShitiAnalysis() + html_answer_tail, "text/html", "utf-8");
                        et_analysis.setVisibility(View.GONE);
                        showAnalysisET = -1;
                        et_analysis.setText("");
                        return true;
                    }
                    return false;
                }
            });


            // 展示输入框
            if (showTimianET == pos) {
                et_timian.setVisibility(View.VISIBLE);
            } else {
                if (et_timian.getText().toString().length() > 0) {
                    item.setShitiShow(item.getShitiShow() + "<p>" + et_timian.getText().toString() + "</p>");
                    et_timian.setText("");
                }
                et_timian.setVisibility(View.GONE);
            }
            if (showAnalysisET == pos) {
                et_analysis.setVisibility(View.VISIBLE);
            } else {
                if (et_analysis.getText().toString().length() > 0) {
                    item.setShitiAnalysis(item.getShitiAnalysis() + "<p>" + et_analysis.getText().toString() + "</p>");
                    et_analysis.setText("");
                }
                et_analysis.setVisibility(View.GONE);
            }


            View.OnTouchListener canScrollTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d("wen", "onTouch: 开启");
                    manager.setScrollEnabled(true);
                    return false;
                }
            };

            View.OnTouchListener noScrollTouchListener = new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Log.d("wen", "onTouch: 关闭");
                    manager.setScrollEnabled(false);
                    return false;
                }
            };
            wv_timian.setOnTouchListener(noScrollTouchListener);
            wv_analysis.setOnTouchListener(noScrollTouchListener);
            sv_timian.setOnTouchListener(noScrollTouchListener);
            sv_analysis.setOnTouchListener(noScrollTouchListener);
            ll_root.setOnTouchListener(canScrollTouchListener);

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
    }

    public interface MyItemClickListener {
        void openGallery(View view, int pos, String type);

        void openCamera(View view, int pos, String type);

        void showHideEmpty(Boolean isHide);
    }
}
