package com.example.yidiantong.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class HudongActivity extends AppCompatActivity {
    private static final String TAG = "HudongActivity";
    private WebView myWebView;
    private long exitTime = 0;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hudong);
        LinearLayout ll_loading = findViewById(R.id.ll_loading);
        // 从上一个页面获取
        String keciID = getIntent().getStringExtra("keciID");
        String stuId = getIntent().getStringExtra("stuId");
        String BottomTitle = getIntent().getStringExtra("BottomTitle");
        url = "http://"+Constant.T_HOMEWORK_GET_HUDONG+"?keciID=" + keciID + "&stuId=" + stuId;

        findViewById(R.id.iv_back).setOnClickListener(v -> {
            finish();
        });
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(BottomTitle);

        myWebView = findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 使加载的网页可以运行JS代码
        myWebView.loadUrl(url);	//设置网址
        //loadTaskStatus();
        //myWebView.setWebViewClient(new WebViewClient());

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING); //支持内容重新布局
        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setTextZoom(2);//设置文本的缩放倍数，默认为 100
        //webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        webSettings.setStandardFontFamily("");//设置 WebView 的字体，默认字体为 "sans-serif"
        webSettings.setDefaultFontSize(20);//设置 WebView 字体的大小，默认大小为 16
        webSettings.setMinimumFontSize(12);//设置 WebView 支持的最小字体大小，默认为 8

        // 添加JavaScript接口
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // 设置WebViewClient
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 注入JavaScript代码，重写playMp4函数
                myWebView.loadUrl("javascript:(function() {" +
                        "   var originalPlayMp4 = window.playMp4;" +
                        "   window.playMp4 = function(url1) {" +
                        "       var url = url1;" +
                        "       Android.playMp4(url);" +
                        "   }" +
                        "})()");
                // 页面加载完成后隐藏加载布局
                runOnUiThread(() -> ll_loading.setVisibility(View.GONE));
            }
        });
    }
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void playMp4(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setDataAndType(Uri.parse(url), "video/mp4");
            Log.d(TAG, "playMp4: "+url);
            startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                Toast.makeText(getApplicationContext(), "再按一次返回退出",
//                        Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                //super.onBackPressed();
//                finish();
//            }
            finish(); // 这将关闭当前Activity
        }
    }
    private void loadTaskStatus() {

        StringRequest request = new StringRequest(url, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                int TaskStatus = json.getInt("data");
                Boolean isSuccess = json.getBoolean("success");
                if (TaskStatus>0&&isSuccess) {
                    // 封装消息，传递给主线程
                    Message message = Message.obtain();

                    // 携带数据
                    message.obj = TaskStatus;

                    //标识线程
                    message.what = 102;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        MyApplication.addRequest(request, TAG);
    }

}