
package com.example.yidiantong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yidiantong.adapter.ImagePagerAdapter;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.HomeItemEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private WebView lvvv;

    private View contentView = null;
    public PopupWindow window;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
//        lvvv = findViewById(R.id.lvvv);
////        lvvv.getSettings().setSupportZoom(true);
//        lvvv.getSettings().setBuiltInZoomControls(true);
//        lvvv.getSettings().setDisplayZoomControls(false);
//        WebSettings webSettings = lvvv.getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        lvvv.loadData("<!DOCTYPE html>\n" +
//                "<html>\n" +
//                "\n" +
//                "<head>\n" +
//                "</head>\n" +
//                "\n" +
//                "<body>\n" +
//                "    <img width=\"200px\" onclick=\"a(this)\" src=\"https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png\" />" +
//                "    <script>\n" +
//                "        function a(x) {\n" +
//                "            test.mytoast(x.src)\n" +
//                "        }\n" +
//                "    </script>\n" +
//                "</body>\n" +
//                "\n" +
//                "</html>", "text/html", "utf-8");
//
//        lvvv.addJavascriptInterface(new AndroidtoJs(), "test");
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                if (contentView == null) {
                    // 大图核心
                    contentView = LayoutInflater.from(TestActivity.this).inflate(R.layout.picture_menu, null, false);
                    ViewPager vp = contentView.findViewById(R.id.vp_picture);
                    List<Integer> picList = new ArrayList<>(Arrays.asList(R.drawable.camera,R.drawable.photo));
//                    ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(TestActivity.this, picList);
//                    vp.setAdapter(imagePagerAdapter);
                    // 顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + picList.size());
                    vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            tv.setText(position + 1 + "/" + picList.size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    contentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(TestActivity.this, "TETSTST", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    WebView webView = contentView.findViewById(R.id.wv);
//                    webView.getSettings().setBuiltInZoomControls(true);
//                    webView.getSettings().setDisplayZoomControls(false);
//                    Log.d("wen", "handleMessage: " + message.obj);
//                    webView.loadData("<img width=\"100%\" onclick=\"a(this)\" src=\"" + message.obj + "\" />", "text/html", "utf-8");

                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
//                    imagePagerAdapter.setClickListener(new ImagePagerAdapter.MyItemClickListener() {
//                        @Override
//                        public void onItemClick() {
//                            window.dismiss();
//                        }
//                    });
                }
                window.showAtLocation(TestActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//                WindowManager.LayoutParams lp = TestActivity.this.getWindow().getAttributes();
//                lp.alpha = 0.5f; //0.0-1.0
//                TestActivity.this.getWindow().setAttributes(lp);
//                window.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                    @Override
//                    public void onDismiss() {
////                        lp.alpha = 1.0f; //0.0-1.0
////                        TestActivity.this.getWindow().setAttributes(lp);
//                    }
//                });
            }
        }
    };

    public class AndroidtoJs  {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void mytoast(String msg) {
            Message message = Message.obtain();
            message.obj = msg;
            message.what = 100;
            handler.sendMessage(message);
        }
    }
}