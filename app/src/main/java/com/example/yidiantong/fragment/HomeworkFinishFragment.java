package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.bean.HomeworkMarkedEntity;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.StringUtils;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HomeworkFinishFragment extends Fragment implements View.OnClickListener {
    private List<String> url_list = new ArrayList<>();
    private PagingInterface pageing;
    private View contentView = null;
    private ImagePagerAdapter adapter;
    private PopupWindow window;

    private String html_answer_head = "<head>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            color: rgb(117, 117, 117);\n" +
            "            word-wrap: break-word;\n" +
            "            font-size: 14px;" +
            "        }\n" +
            "    </style>\n" +
            "    <script>\n" +
            "        function lookImage(x) {\n" +
            "        }\n" +
            "        function bigimage(x) {\n" +
            "            myInterface.bigPic()\n" +
            "        }\n" +
            "    </script>\n" +
            "</head>\n" +
            "\n" +
            "<body onclick=\"bigimage(this)\">\n";
    //接口需要
    private HomeworkMarkedEntity homeworkMarked;

    public static HomeworkFinishFragment newInstance(HomeworkMarkedEntity homeworkMarked, int position, int size) {
        HomeworkFinishFragment fragment = new HomeworkFinishFragment();
        Bundle args = new Bundle();
        args.putSerializable("homeworkMarked", homeworkMarked);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //取出携带的参数
        Bundle arg = getArguments();
        int position = arg.getInt("position") + 1;
        int size = arg.getInt("size");
        homeworkMarked = (HomeworkMarkedEntity) arg.getSerializable("homeworkMarked");
        adapter = new ImagePagerAdapter(getActivity(), url_list);
        //获取view
        View view = inflater.inflate(R.layout.fragment_homework_finish, container, false);
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);

        //题号染色
        int positionLen = String.valueOf(position).length();
        String questionNum = position + " / " + size;
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);

        /**
         *  数据显示，四个vw_content
         */
        WebView wv_content = view.findViewById(R.id.wv_content);
        WebView wv_content2 = view.findViewById(R.id.wv_content2);
        WebView wv_content3 = view.findViewById(R.id.wv_content3);
        WebView wv_content4 = view.findViewById(R.id.wv_content4);

        setHtmlOnWebView(wv_content, homeworkMarked.getTiMian());
        setHtmlOnWebView(wv_content4, html_answer_head+homeworkMarked.getStuAnswer());
        String htmlContent = homeworkMarked.getStuAnswer(); // 或者是从网络请求得到的HTML
        Document doc = Jsoup.parse(htmlContent); // 或 Jsoup.connect(url).get();

// 选择所有的img标签
        Elements imgElements = doc.select("img");
        for (Element imgElement : imgElements) {
            String imgSrc = imgElement.absUrl("src"); // 获取绝对URL
            url_list.add(imgSrc);
            adapter.updateData(url_list);// 关键
        }
        wv_content4.addJavascriptInterface(
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
                        Log.d("hsk0522","图片放大");
                        message.what = 101;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");
        String answerString = homeworkMarked.getStandardAnswer();
        String analysisString = homeworkMarked.getAnalysis();
        Log.e("wen0222", "onCreateView: " + homeworkMarked);

        /**
         * 简单判断答案是否为不可见
         */
        boolean isWatch = !"******".equals(homeworkMarked.getStandardAnswer());
        if(homeworkMarked.getShowAnswerFlag().equals("0")){
            isWatch = false;
            answerString = "******";
            analysisString = "******";
        }

        setHtmlOnWebView(wv_content2, answerString);
        setHtmlOnWebView(wv_content3, analysisString);

        if(isWatch){
            // 显示底部一栏
            LinearLayout ll_watch = view.findViewById(R.id.ll_watch);
            ll_watch.setVisibility(View.VISIBLE);
            TextView tv_total_scores = view.findViewById(R.id.tv_total_scores);
            tv_total_scores.setText("满分:" + homeworkMarked.getFullScore());
            TextView tv_stu_scores = view.findViewById(R.id.tv_stu_scores);
            tv_stu_scores.setText("得分:" + homeworkMarked.getScore());
            ImageView iv_stu_scores = view.findViewById(R.id.iv_stu_scores);
            if(Math.abs(homeworkMarked.getScore() - homeworkMarked.getFullScore()) < 0.00001){
                iv_stu_scores.setImageResource(R.drawable.right);
            }else if(homeworkMarked.getScore() > 0){
                iv_stu_scores.setImageResource(R.drawable.half_right);
            }else{
                iv_stu_scores.setImageResource(R.drawable.error);
            }
        }

        //题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText("[" + homeworkMarked.getTypeName() + "]");

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

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
        }
    }

    /**
     * 将HTML内容显示在WebView中，包含转义和样式
     * @param wb WebView组件对象
     * @param str 原始HTML数据
     */
    private void setHtmlOnWebView(WebView wb, String str){
        str = StringEscapeUtils.unescapeHtml4(str);
        // 定义图片点击放大的JavaScript函数

        String html_content = "<head><style>" +
                " p {\n" +
                "   margin: 0px;" +
                "   line-height: 30px;" +
                "   }" +
                "</style>" +
                "</head><body style=\"color: rgb(117, 117, 117); font-size: 14px; margin: 0px; padding: 0px\">" + str + "</body>";
        wb.getSettings().setJavaScriptEnabled(true); // 确保JavaScript可用
        wb.loadDataWithBaseURL(null, html_content, "text/html", "utf-8", null);
    }
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 101) {
                if (contentView == null) {

                    if (url_list.size() == 0) return;
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
//                    LinearLayout ll_selector = contentView.findViewById(R.id.ll_selector);
                    //  回显方法
                    //  回显方法
                    //  回显方法
//                    contentView.findViewById(R.id.btn_save).setOnClickListener(v -> {
//                        Log.d(TAG, "onClick: ");
//                        html_answer = html_answer.replace(originUrl, identifyUrl);
//                        wv_answer.loadDataWithBaseURL(null, getHtmlAnswer(), "text/html", "utf-8", null);
//
//                        transmit.setStuAnswer(stuAnswerEntity.getOrder(), html_answer);
//                        window.dismiss();
//                    });
//                    contentView.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
//                        window.dismiss();
//                    });
                    vp_pic.setAdapter(adapter);

                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);

                    adapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClick() {
                            vp_pic.setCurrentItem(0);
                            window.dismiss();
                        }
// // 手写公式识别-弃
//                        @Override
//                        public void onLongItemClick(int pos) {
//                            Toast.makeText(getActivity(), "长按", Toast.LENGTH_SHORT).show();
//                            if (contentView2 == null) {
//                                contentView2 = LayoutInflater.from(getActivity()).inflate(R.layout.menu_pic_identify, null, false);
//                                //绑定点击事件
//                                contentView2.findViewById(R.id.tv_all).setOnClickListener(v -> {
//                                    identifyUrl = picIdentify(url_list.get(pos));
//                                    originUrl = url_list.get(pos);
//                                    url_list.set(pos, identifyUrl);
//                                    adapter.notifyDataSetChanged();
//                                    ll_selector.setVisibility(View.VISIBLE);
//
//                                    window2.dismiss();
//                                });
//
//                                window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//                                window2.setTouchable(true);
//                            }
//                            window2.showAtLocation(contentView2, Gravity.CENTER, 0, 0);
//
//                        }
                    });

                    vp_pic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            tv.setText(position + 1 + "/" + url_list.size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                } else {
                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }

                adapter.notifyDataSetChanged();

                window.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);

            }

        }
    };

}