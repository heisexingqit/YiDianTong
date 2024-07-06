package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.BookExerciseActivity;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.Arrays;
import java.util.List;

public class BookExerciseAdapterW extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LinearLayout ll_timu;

    private List<BookExerciseEntity> itemList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    //答题区域HTML头
    private String html_head = "<head>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14px;" +
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

    public BookExerciseAdapterW(Context context, List<BookExerciseEntity> itemList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
        mContext = context;
        // 获取屏幕尺寸

    }
    private ExerciseInterface myInterface;
    public void setOnclickListener(ExerciseInterface e){
        myInterface = e;
    }

    public void update(List<BookExerciseEntity> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        int type = Integer.parseInt(itemList.get(position).baseTypeId);
        if (type == 108) {
            if (itemList.get(position).typeName.contains("七")) {
                type = 199; // 七选五特殊题型
            }
        }
        return type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        switch (viewType) {
            case 101:
            case 102:
            case 103:
                // 已完成
                v = layoutInflater.inflate(R.layout.item_book_exercise, parent, false);
                break;
            case 108:
                // 已完成
                v = layoutInflater.inflate(R.layout.item_book_exercise_reading, parent, false);
                break;
            case 199:
                // 已完成
                v = layoutInflater.inflate(R.layout.item_book_exercise_seven, parent, false);
                break;
            case 104:
            default:
                v = layoutInflater.inflate(R.layout.item_book_exercise_cloze, parent, false);
                break;
        }


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).update(position, holder);

    }
    private Handler handler;
    public void setHandler(Handler handler) {
        this.handler=handler;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_type_name, tv_shiti_answer, tv_stu_answer;
        private LinearLayout ll_danxuan, ll_duoxuan, ll_panduan, ll_answer_analysis;
        private WebView wv_timian, wv_analysis;
        private Button btn_submit;
        private ImageView iv_result;
        private ClickableImageView iv_a, iv_b, iv_c, iv_d;  // 单选按钮
        private ClickableImageView iv_a2, iv_b2, iv_c2, iv_d2;  // 多选按钮
        private ClickableImageView iv_r, iv_e;  // 判断按钮
        //        private TextView tv_id;  // 判断按钮
        private LinearLayout ll_reading_parent; // 阅读题父组件
        private ClickableImageView iv_answer_drawer[][]; // 阅读题数组
        int[] unselectIcons = {R.drawable.a_unselect, R.drawable.b_unselect, R.drawable.c_unselect, R.drawable.d_unselect,R.drawable.e_unselect,R.drawable.f_unselect,R.drawable.g_unselect};
        int[] selectIcons = {R.drawable.a_select, R.drawable.b_select, R.drawable.c_select, R.drawable.d_select,R.drawable.e_select,R.drawable.f_select,R.drawable.g_select};
        private WebView wv_answer;
        private ClickableImageView iv_answer_drawer2[][] = new ClickableImageView[5][7];
        private LinearLayout ll_input_image; // 主观题 图片展示
        private WebView wv_stu_answer; // 主观题 图片展示
        private ClickableImageView iv_camera, iv_gallery; // 主观题图片按钮
        private EditText et_stu_answer;
        private RelativeLayout rl_submitting;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_type_name = itemView.findViewById(R.id.tv_type_name);
            ll_danxuan = itemView.findViewById(R.id.ll_danxuan);
            ll_duoxuan = itemView.findViewById(R.id.ll_duoxuan);
            ll_panduan = itemView.findViewById(R.id.ll_panduan);
            wv_timian = itemView.findViewById(R.id.wv_timian);
            wv_analysis = itemView.findViewById(R.id.wv_analysis);
            ll_answer_analysis = itemView.findViewById(R.id.ll_answer_analysis);
            iv_a = itemView.findViewById(R.id.iv_a);
            iv_b = itemView.findViewById(R.id.iv_b);
            iv_c = itemView.findViewById(R.id.iv_c);
            iv_d = itemView.findViewById(R.id.iv_d);
            btn_submit = itemView.findViewById(R.id.btn_submit);
            iv_result = itemView.findViewById(R.id.iv_result);
            tv_shiti_answer = itemView.findViewById(R.id.tv_shiti_answer);
            tv_stu_answer = itemView.findViewById(R.id.tv_stu_answer);
            iv_a2 = itemView.findViewById(R.id.iv_a2);
            iv_b2 = itemView.findViewById(R.id.iv_b2);
            iv_c2 = itemView.findViewById(R.id.iv_c2);
            iv_d2 = itemView.findViewById(R.id.iv_d2);
            iv_r = itemView.findViewById(R.id.iv_r);
            iv_e = itemView.findViewById(R.id.iv_e);
//            tv_id = itemView.findViewById(R.id.tv_id);
            ll_reading_parent = itemView.findViewById(R.id.ll_reading_parent);
            wv_answer = itemView.findViewById(R.id.wv_answer);
            // 7选5
            // 1
            iv_answer_drawer2[0][0] = itemView.findViewById(R.id.iv_1a);
            iv_answer_drawer2[0][1] = itemView.findViewById(R.id.iv_1b);
            iv_answer_drawer2[0][2] = itemView.findViewById(R.id.iv_1c);
            iv_answer_drawer2[0][3] = itemView.findViewById(R.id.iv_1d);
            iv_answer_drawer2[0][4] = itemView.findViewById(R.id.iv_1e);
            iv_answer_drawer2[0][5] = itemView.findViewById(R.id.iv_1f);
            iv_answer_drawer2[0][6] = itemView.findViewById(R.id.iv_1g);
            // 2
            iv_answer_drawer2[1][0] = itemView.findViewById(R.id.iv_2a);
            iv_answer_drawer2[1][1] = itemView.findViewById(R.id.iv_2b);
            iv_answer_drawer2[1][2] = itemView.findViewById(R.id.iv_2c);
            iv_answer_drawer2[1][3] = itemView.findViewById(R.id.iv_2d);
            iv_answer_drawer2[1][4] = itemView.findViewById(R.id.iv_2e);
            iv_answer_drawer2[1][5] = itemView.findViewById(R.id.iv_2f);
            iv_answer_drawer2[1][6] = itemView.findViewById(R.id.iv_2g);
            // 3
            iv_answer_drawer2[2][0] = itemView.findViewById(R.id.iv_3a);
            iv_answer_drawer2[2][1] = itemView.findViewById(R.id.iv_3b);
            iv_answer_drawer2[2][2] = itemView.findViewById(R.id.iv_3c);
            iv_answer_drawer2[2][3] = itemView.findViewById(R.id.iv_3d);
            iv_answer_drawer2[2][4] = itemView.findViewById(R.id.iv_3e);
            iv_answer_drawer2[2][5] = itemView.findViewById(R.id.iv_3f);
            iv_answer_drawer2[2][6] = itemView.findViewById(R.id.iv_3g);
            // 4
            iv_answer_drawer2[3][0] = itemView.findViewById(R.id.iv_4a);
            iv_answer_drawer2[3][1] = itemView.findViewById(R.id.iv_4b);
            iv_answer_drawer2[3][2] = itemView.findViewById(R.id.iv_4c);
            iv_answer_drawer2[3][3] = itemView.findViewById(R.id.iv_4d);
            iv_answer_drawer2[3][4] = itemView.findViewById(R.id.iv_4e);
            iv_answer_drawer2[3][5] = itemView.findViewById(R.id.iv_4f);
            iv_answer_drawer2[3][6] = itemView.findViewById(R.id.iv_4g);
            // 5
            iv_answer_drawer2[4][0] = itemView.findViewById(R.id.iv_5a);
            iv_answer_drawer2[4][1] = itemView.findViewById(R.id.iv_5b);
            iv_answer_drawer2[4][2] = itemView.findViewById(R.id.iv_5c);
            iv_answer_drawer2[4][3] = itemView.findViewById(R.id.iv_5d);
            iv_answer_drawer2[4][4] = itemView.findViewById(R.id.iv_5e);
            iv_answer_drawer2[4][5] = itemView.findViewById(R.id.iv_5f);
            iv_answer_drawer2[4][6] = itemView.findViewById(R.id.iv_5g);
            ll_input_image = itemView.findViewById(R.id.ll_input_image);
            wv_stu_answer = itemView.findViewById(R.id.wv_stu_answer);
            iv_camera = itemView.findViewById(R.id.iv_camera);
            iv_gallery = itemView.findViewById(R.id.iv_gallery);
            et_stu_answer = itemView.findViewById(R.id.et_stu_answer);
            //加载页面
            rl_submitting = itemView.findViewById(R.id.rl_submitting);
            ll_timu = itemView.findViewById(R.id.ll_timu);

        }
        private class TimianWebViewClient extends WebViewClient {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 当WebView加载完成时，调整rl_submitting的大小
                adjustLoadingLayoutSize();
            }
        }

        public void update(int pos, RecyclerView.ViewHolder holder) {
            // 初始化UI
            btn_submit.setVisibility(View.VISIBLE);
            BookExerciseEntity item = itemList.get(pos); // 获取当前item
            Log.e("wen0524", "update: " + item.getStuAnswer());
            tv_type_name.setText(item.typeName); // 设置题目类型名称

            // 设置WebViewClient以便监听加载完成事件
            wv_timian.setWebViewClient(new TimianWebViewClient());

            // 题面设置
            String html_content = "<body style=\"color: rgb(100, 100, 100); font-size: 14px;line-height: 20px;\">" + item.shiTiShow + "</body>";
            String html = html_content.replace("#", "%23");
            wv_timian.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);



            // 解析设置
            String html_content_analysis = "<body style=\"color: rgb(100, 100, 100); font-size: 14px;line-height: 20px;\">" + item.shiTiAnalysis + "</body>";
            String html_analysis = html_content_analysis.replace("#", "%23");
            wv_analysis.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);

//            switch (pos){
//                case 0:
//                    tv_id.setText("举例一:");
//                    break;
//                case 1:
//                    tv_id.setText("举例二:");
//                    break;
//                case 2:
//                    tv_id.setText("举例三:");
//                    break;
//            }
            // 答案设置
            switch (item.baseTypeId) {
                case "101":
                    // UI显示
                    ll_danxuan.setVisibility(View.VISIBLE);
                    ll_duoxuan.setVisibility(View.GONE);
                    ll_panduan.setVisibility(View.GONE);
                    // 单选按钮点击
                    View.OnClickListener danxuanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.iv_a:
                                    item.stuAnswer = "A";
                                    break;
                                case R.id.iv_b:
                                    item.stuAnswer = "B";
                                    break;
                                case R.id.iv_c:
                                    item.stuAnswer = "C";
                                    break;
                                case R.id.iv_d:
                                    item.stuAnswer = "D";
                                    break;
                            }
                            showDanxuanBtn(item.stuAnswer);
                        }
                    };
                    showDanxuanBtn(item.stuAnswer); // 默认触发，初始化按钮
                    iv_a.setOnClickListener(danxuanListener);
                    iv_b.setOnClickListener(danxuanListener);
                    iv_c.setOnClickListener(danxuanListener);
                    iv_d.setOnClickListener(danxuanListener);

                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");

                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "102": // 多选题
                    // UI显示
                    ll_danxuan.setVisibility(View.GONE);
                    ll_duoxuan.setVisibility(View.VISIBLE);
                    ll_panduan.setVisibility(View.GONE);
                    // 单选按钮点击
                    View.OnClickListener duoxuanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            switch (view.getId()) {
                                case R.id.iv_a2:
                                    if (item.stuAnswer.contains("A")) {
                                        item.stuAnswer = item.stuAnswer.replace("A", "");
                                    } else {
                                        item.stuAnswer += "A";
                                    }
                                    break;
                                case R.id.iv_b2:
                                    if (item.stuAnswer.contains("B")) {
                                        item.stuAnswer = item.stuAnswer.replace("B", "");
                                    } else {
                                        item.stuAnswer += "B";
                                    }
                                    break;
                                case R.id.iv_c2:
                                    if (item.stuAnswer.contains("C")) {
                                        item.stuAnswer = item.stuAnswer.replace("C", "");
                                    } else {
                                        item.stuAnswer += "C";
                                    }
                                    break;
                                case R.id.iv_d2:
                                    if (item.stuAnswer.contains("D")) {
                                        item.stuAnswer = item.stuAnswer.replace("D", "");
                                    } else {
                                        item.stuAnswer += "D";
                                    }
                                    break;
                            }
                            // 将答案字符串转换为字符数组，并排序
                            char[] answerArray = item.stuAnswer.toCharArray();
                            Arrays.sort(answerArray);
                            // 反转字符数组，使其从大到小排序
                            item.stuAnswer = new StringBuilder(new String(answerArray)).toString();
                            showDuoxuanBtn(item.stuAnswer);
                        }
                    };
                    showDuoxuanBtn(item.stuAnswer); // 默认触发，初始化按钮

                    iv_a2.setOnClickListener(duoxuanListener);
                    iv_b2.setOnClickListener(duoxuanListener);
                    iv_c2.setOnClickListener(duoxuanListener);
                    iv_d2.setOnClickListener(duoxuanListener);


                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");


                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "103": // 判断题
                    // UI显示
                    ll_danxuan.setVisibility(View.GONE);
                    ll_duoxuan.setVisibility(View.GONE);
                    ll_panduan.setVisibility(View.VISIBLE);
                    // 单选按钮点击
                    View.OnClickListener panduanListener = new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            switch (view.getId()) {
                                case R.id.iv_r:
                                    item.stuAnswer = "对";
                                    break;
                                case R.id.iv_e:
                                    item.stuAnswer = "错";
                            }
                            showPanduanBtn(item.stuAnswer);
                        }
                    };
                    showPanduanBtn(item.stuAnswer); // 默认触发，初始化按钮
                    iv_r.setOnClickListener(panduanListener);
                    iv_e.setOnClickListener(panduanListener);

                    // html清洗
                    item.shiTiAnswer = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");


                    // 答案设置
                    tv_shiti_answer.setText("【参考答案】" + item.shiTiAnswer);
                    break;
                case "108":
                    if (item.typeName.contains("七选")) {
                        View.OnClickListener read75Listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = (String) view.getTag();
                                String[] strArr = id.split("-");
                                int qId = Integer.parseInt(strArr[0]);
                                int sId = Integer.parseInt(strArr[1]);
                                String[] parts = item.stuAnswer.split(",");
                                parts[qId] = Character.toString((char) (sId + 'A'));
                                item.stuAnswer = String.join(",", parts);
                                showReading75Btn(item.stuAnswer);
                            }
                        };
                        showReading75Btn(item.stuAnswer); // 默认触发，初始化按钮

                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < 5; ++i) {
                            for (int j = 0; j < 7; ++j) {
                                iv_answer_drawer2[i][j].setTag(i + "-" + j);
                                iv_answer_drawer2[i][j].setOnClickListener(read75Listener);
                            }
                            sb.append("未答");
                            if (i != item.orderNum - 1) {
                                sb.append(",");
                            }
                        }

                        item.stuAnswer = sb.toString();
                        // html清洗
                        String cleanString = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");
                        tv_shiti_answer.setText("【参考答案】" + cleanString);
                        item.shiTiAnswer = cleanString;

                    } else {
                        // 阅读题
                        // UI显示

                        // 单选按钮点击
                        iv_answer_drawer = new ClickableImageView[item.orderNum][4];
                        View.OnClickListener readingListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id = (String) view.getTag();
                                String[] strArr = id.split("-");
                                int qId = Integer.parseInt(strArr[0]);
                                int sId = Integer.parseInt(strArr[1]);
                                Log.e("wen0322", "onClick: " + qId + " " + sId);
                                String[] parts = item.stuAnswer.split(",");
                                parts[qId] = Character.toString((char) (sId + 'A'));
                                item.stuAnswer = String.join(",", parts);
                                showReadingBtn(item.stuAnswer);
                            }
                        };
                        showReadingBtn(item.stuAnswer); // 默认触发，初始化按钮


                        StringBuilder sb = new StringBuilder();
                        Log.e("wen0322", "update: " + item.orderNum);
                        // 阅读题
                        for (int i = 0; i < item.orderNum; ++i) {
                            View v = LayoutInflater.from(mContext).inflate(R.layout.my_component, ll_reading_parent, false);
                            TextView tv_num = v.findViewById(R.id.tv_num);
                            tv_num.setText(String.valueOf(i + 1));

                            iv_answer_drawer[i][0] = v.findViewById(R.id.iv_a);
                            iv_answer_drawer[i][0].setTag(i + "-" + 0);
                            iv_answer_drawer[i][1] = v.findViewById(R.id.iv_b);
                            iv_answer_drawer[i][1].setTag(i + "-" + 1);
                            iv_answer_drawer[i][2] = v.findViewById(R.id.iv_c);
                            iv_answer_drawer[i][2].setTag(i + "-" + 2);
                            iv_answer_drawer[i][3] = v.findViewById(R.id.iv_d);
                            iv_answer_drawer[i][3].setTag(i + "-" + 3);
                            for (int j = 0; j < 4; ++j) {
                                iv_answer_drawer[i][j].setOnClickListener(readingListener);
                            }
                            sb.append("未答");
                            if (i != item.orderNum - 1) {
                                sb.append(",");
                            }
                            ll_reading_parent.addView(v);
                        }
                        item.stuAnswer = sb.toString();
                        // html清洗
                        String cleanString = Jsoup.clean(item.shiTiAnswer, Whitelist.none()).trim().replace("&nbsp;", "");

                        // 答案设置
                        if (cleanString.length() > 30) {
                            wv_answer.setVisibility(View.VISIBLE);
                            String html_content_answer = "<body style=\"color: rgb(100, 100, 100); font-size: 14px;line-height: 20px;\">" + item.shiTiAnswer + "</body>";
                            String html_answer = html_content_answer.replace("#", "%23");
                            wv_answer.loadDataWithBaseURL(null, html_answer, "text/html", "utf-8", null);
                        } else {
                            wv_answer.setVisibility(View.GONE);
                            tv_shiti_answer.setText("【参考答案】" + cleanString);
                            item.shiTiAnswer = cleanString;
                        }
                    }
                    break;
                case "104": // 填空题
                default: // 主观题
                    // 设置光标的位置和长度
                    iv_camera.setOnClickListener(view -> {
                        myInterface.openDrawCamera(pos, wv_stu_answer, ll_input_image);
                    });
                    iv_gallery.setOnClickListener(view -> {
                        myInterface.openDrawGallery(pos, wv_stu_answer, ll_input_image);
                    });
                    et_stu_answer.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            // 在文本内容发生改变之前调用
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            // 在文本内容发生改变时调用
                            String inputText = s.toString();
                            // 实时获取输入框内容，可以在这里进行相应处理
                            // 例如，可以将输入内容显示在 Logcat 中
                            item.stuAnswer = inputText;
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }

                    });

                    et_stu_answer.setText(item.stuAnswer);
                    if(item.stuHtml == null || item.stuHtml.length() == 0){
                        ll_input_image.setVisibility(View.GONE);
                    }
                    WebSettings webSettings = wv_stu_answer.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    wv_stu_answer.addJavascriptInterface(
                            new Object() {
                                @JavascriptInterface
                                @SuppressLint("JavascriptInterface")
                                public void bigPic() {
                                    /**
                                     * Js注册的方法无法修改主UI，需要Handler
                                     */
                                    Message message = Message.obtain();
                                    // 发送消息给主线程
                                    //标识线程
                                    message.what = 102;
                                    message.obj = item.stuHtml;
                                    handler.sendMessage(message);
                                }
                            }
                            , "myInterface");
                    wv_stu_answer.loadDataWithBaseURL(null, html_head+item.stuHtml, "text/html", "utf-8", null);


                    // 解析设置
                    html_content_analysis = "<body style=\"color: rgb(100, 100, 100); font-size: 14px;line-height: 20px;\">" + item.shiTiAnswer + "</body>";
                    html_analysis = html_content_analysis.replace("#", "%23");
                    wv_answer.loadDataWithBaseURL(null, html_analysis, "text/html", "utf-8", null);

                    break;
            }
            ll_answer_analysis.setVisibility(View.GONE);


            // 提交按钮设置
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("hsk0603","按钮点击");
                    switch (item.baseTypeId) {
                        case "101": // 单选题
                        case "102": // 多选题
                        case "103": // 判断题
                        case "108":

                            if (item.stuAnswer == null || item.stuAnswer.length() == 0) {
                                Toast.makeText(mContext, "请选择答案", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                boolean allUnanswered = true; // 假设所有答案都是未答
                                String[] parts = item.stuAnswer.split(",");
                                for (int i = 0; i < parts.length; ++i) {
                                    if (!parts[i].equals("未答")) {
                                        allUnanswered = false; // 只要有一个答案不是未答，就更新标记
                                        break; // 只要找到一个非未答答案就可以提前结束循环
                                    }
                                }
                                if (allUnanswered) {
                                    Toast.makeText(mContext, "请选择答案", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            int resultType = checkStringAnswer(item.stuAnswer, item.shiTiAnswer);
                            // 结果图标
                            if (resultType == 0) {
                                iv_result.setImageResource(R.drawable.answrong);
                            } else if(resultType == 1) {
                                iv_result.setImageResource(R.drawable.ansright);
                            }else{
                                iv_result.setImageResource(R.drawable.anshalf);
                            }
                            // 学生作答显示
                            tv_stu_answer.setText("【你的答案】" + item.stuAnswer);

                            break;


                        default: // 主观题
                            Log.e("wen0601", "onClick: 点击触发");
//                            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                            if (imm != null) {
//                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                            }
//                            // 清除 EditText 的焦点，使光标不再闪烁
                            et_stu_answer.clearFocus();
                            if ((item.stuAnswer == null || item.stuAnswer.length() == 0) && (item.stuHtml == null || item.stuHtml.length() == 0)) {
                                Toast.makeText(mContext, "请填写答案", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            tv_stu_answer.setVisibility(View.GONE);
                            iv_result.setVisibility(View.GONE);
                            // 取消输入框焦点
                            break;

                    }
                    ((BookExerciseActivity)mContext).show_rl_submitting();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 隐藏加载页面
                            ((BookExerciseActivity)mContext).hade_rl_submitting();

                            btn_submit.setVisibility(View.GONE);
                            ll_answer_analysis.setVisibility(View.VISIBLE); // 显示答案解析
                        }
                    }, 1000); // 设置延迟时间

                }
            });

        }



        private void adjustLoadingLayoutSize() {
//            int llTimuWidth = ll_timu.getMeasuredWidth();
//            int llTimuHeight = ll_timu.getMeasuredHeight();
//            // 设置rl_submitting的大小，确保在WebView内容加载完成后执行
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int llTimuWidth = displayMetrics.widthPixels;
            int llTimuHeight = displayMetrics.heightPixels;

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    llTimuWidth, // 设置宽度与ll_timu相同
                    llTimuHeight // 设置高度与ll_timu相同
            );
            rl_submitting.setLayoutParams(layoutParams);
            // 如果需要立即显示rl_submitting，可以在这里设置其可见性
        }
        private void showDanxuanBtn(String showAnswer) {
            iv_a.setImageResource(R.drawable.a_unselect);
            iv_b.setImageResource(R.drawable.b_unselect);
            iv_c.setImageResource(R.drawable.c_unselect);
            iv_d.setImageResource(R.drawable.d_unselect);
            switch (showAnswer) {
                case "A":
                    iv_a.setImageResource(R.drawable.a_select);
                    break;
                case "B":
                    iv_b.setImageResource(R.drawable.b_select);
                    break;
                case "C":
                    iv_c.setImageResource(R.drawable.c_select);
                    break;
                case "D":
                    iv_d.setImageResource(R.drawable.d_select);
                    break;
            }

        }

        private void showPanduanBtn(String showAnswer) {
            iv_r.setImageResource(R.drawable.right_unselect);
            iv_e.setImageResource(R.drawable.error_unselect);
            switch (showAnswer) {
                case "对":
                    iv_r.setImageResource(R.drawable.right_select);
                    break;
                case "错":
                    iv_e.setImageResource(R.drawable.error_select);
                    break;
            }
        }

        private void showDuoxuanBtn(String showAnswer) {
            // 检查showAnswer中包含的字符
            if (showAnswer.contains("A")) {
                iv_a2.setImageResource(R.drawable.a_select2);
            } else {
                iv_a2.setImageResource(R.drawable.a_unselect2);
            }
            if (showAnswer.contains("B")) {
                iv_b2.setImageResource(R.drawable.b_select2);
            } else {
                iv_b2.setImageResource(R.drawable.b_unselect2);
            }
            if (showAnswer.contains("C")) {
                iv_c2.setImageResource(R.drawable.c_select2);
            } else {
                iv_c2.setImageResource(R.drawable.c_unselect2);
            }
            if (showAnswer.contains("D")) {
                iv_d2.setImageResource(R.drawable.d_select2);
            } else {
                iv_d2.setImageResource(R.drawable.d_unselect2);
            }

        }

        private void showReadingBtn(String showAnswer) {
            String[] parts = showAnswer.split(",");
            for (int i = 0; i < parts.length; ++i) {
                // 根据答案更新按钮状态
                if (!parts[i].equals("未答")) {
                    int selectedOptionIndex = parts[i].charAt(0) - 'A';
                    // 循环遍历当前题目的所有选项按钮
                    for (int j = 0; j < 4; ++j) {
                        if (j == selectedOptionIndex) {
                            // 将选中的按钮高亮显示
                            iv_answer_drawer[i][j].setImageResource(selectIcons[j]);
                        } else {
                            // 将未选中的按钮取消高亮显示
                            iv_answer_drawer[i][j].setImageResource(unselectIcons[j]);
                        }
                    }
                } else {
                    // 如果问题未被回答，设置所有选项为未选中状态
                    for (int j = 0; j < 4; ++j) {
                        iv_answer_drawer[i][j].setImageResource(unselectIcons[j]);
                    }
                }
            }
        }

        private void showReading75Btn(String showAnswer) {
            String[] parts = showAnswer.split(",");
            for (int i = 0; i < 5; ++i) {
                // 根据答案更新按钮状态
                if (!parts[i].equals("未答")) {
                    int selectedOptionIndex = parts[i].charAt(0) - 'A';
                    // 循环遍历当前题目的所有选项按钮
                    for (int j = 0; j < 7; ++j) {
                        if (j == selectedOptionIndex) {
                            // 将选中的按钮高亮显示
                            iv_answer_drawer2[i][j].setImageResource(selectIcons[j]);
                        } else {
                            // 将未选中的按钮取消高亮显示
                            iv_answer_drawer2[i][j].setImageResource(unselectIcons[j]);
                        }
                    }
                } else {
                    // 如果问题未被回答，设置所有选项为未选中状态
                    for (int j = 0; j < 7; ++j) {
                        iv_answer_drawer2[i][j].setImageResource(unselectIcons[j]);
                    }
                }
            }
        }

        int checkStringAnswer(String stuAnswer, String stdAnswer){

            // HTML清洗
            stuAnswer = Jsoup.clean(stuAnswer, Whitelist.none()).trim().replace("&nbsp;", " ");
            stdAnswer = Jsoup.clean(stdAnswer, Whitelist.none()).trim().replace("&nbsp;", " ");

            // 将答案字符串分割成单词
            String[] stuWords = stuAnswer.split("[,，. 。 ]");
            String[] stdWords = stdAnswer.split("[,，. 。 ]");

            // 检查分割后数组的长度是否相同
            if (stuWords.length != stdWords.length) {
                return 0; // 分割后数组长度不同，直接判断错误
            }

            // 逐个比较单词
            int correctCount = 0;
            for (int i = 0; i < stuWords.length; i++) {
                if (stuWords[i].equals(stdWords[i])) {
                    correctCount++;
                }
            }

            // 判断结果并返回相应值
            if (correctCount == stuWords.length) {
                return 1; // 全相同
            } else if (correctCount > 0) {
                return 2; // 部分相同
            } else {
                return 0; // 全错误
            }
        }


    }

    public interface ExerciseInterface {
        void openDrawCamera(int pos, WebView wb, LinearLayout ll);
        void openDrawGallery(int pos, WebView wb, LinearLayout ll);
    }

}
