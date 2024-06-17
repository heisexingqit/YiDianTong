package com.example.yidiantong.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.yidiantong.R;
import com.example.yidiantong.adapter.ImagePagerAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.namee.permissiongen.PermissionGen;
import pl.droidsonroids.gif.GifImageView;

public class ReadAloudLookActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NoticeLookActivity";
    private String type;
    private TextView ftv_title;
    private TextView ftv_nl_title;
    private TextView ftv_nl_user;
    private TextView ftv_nl_time;
    private String readTime;
    private String readAuthor;
    private String readTitle;
    private String readContent;
    private LinearLayout ll_content;
    private WebView wv_content;

    // 点击大图
    private List<String> url_list = new ArrayList<>();
    private PopupWindow window;
    private ImagePagerAdapter adapter;
    private View contentView = null;

    //录音组件
    private LinearLayout ll_video;
    private LinearLayout ll_play;
    private TextView tv_play;
    private GifImageView giv_play;
    private GifImageView giv_video;
    //语音操作对象
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    //语音文件保存路径
    private String FileName = null;
    //是否正在播放
    private boolean isPlaying = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_aloud_look);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        // 提前创建Adapter
        adapter = new ImagePagerAdapter(this, url_list);
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("朗读");
        //朗读任务标题
        ftv_nl_title = findViewById(R.id.ftv_nl_title);
        readTitle = getIntent().getStringExtra("readTitle");
        ftv_nl_title.setText(readTitle);
        //发布作者
        ftv_nl_user = findViewById(R.id.ftv_nl_user);
        readAuthor = getIntent().getStringExtra("readAuthor");
        ftv_nl_user.setText(readAuthor);
        //发布时间
        ftv_nl_time = findViewById(R.id.ftv_nl_time);
        readTime = getIntent().getStringExtra("readTime");
        ftv_nl_time.setText(readTime);
        //朗读内容
        wv_content = findViewById(R.id.wv_content);
        readContent = getIntent().getStringExtra("readContent");

        // 利用正则表达式统计"src"出现的次数
        Pattern srcPattern = Pattern.compile("src='(http[^']*)'");
        Matcher srcMatcher = srcPattern.matcher(readContent);
        // 通过正则表达式匹配出所有图片的url
        while (srcMatcher.find()) {
            String url = srcMatcher.group(1);
            url_list.add(url); // 添加到图片url列表
            adapter.updateData(url_list); // 更新图片列表
        }

        wv_content.loadDataWithBaseURL(null, readContent,
                "text/html", "utf-8", null);
        WebSettings webSettings = wv_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_content.addJavascriptInterface(
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
                        message.what = 101;
                        handler.sendMessage(message);
                    }
                }
                , "myInterface");

        //设置录音文件保存的路径
        FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        FileName += "/" + readTitle + "1.mp3";
        System.out.println(FileName);
        ll_content = findViewById(R.id.ll_content);
        ll_content.setOnClickListener(this);
    }

    //录音权限申请
    private void requestPermission() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WAKE_LOCK)
                .request();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 101) {
                // 复用老代码 触发点击
                ll_content.performClick();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_content:
                if (contentView == null) {
                    if (url_list.size() == 0) break;
                    contentView = LayoutInflater.from(this).inflate(R.layout.picture_menu, null, false);
                    requestPermission();//请求麦克风权限
                    // 录音组件
                    ll_video = contentView.findViewById(R.id.ll_video);
                    ll_video.setVisibility(View.VISIBLE);
                    giv_video = contentView.findViewById(R.id.giv_video);
                    // 播放组件
                    ll_play = contentView.findViewById(R.id.ll_play);
                    giv_play = contentView.findViewById(R.id.giv_play);
                    tv_play = contentView.findViewById(R.id.tv_play);
                    ll_play.setOnClickListener(this);
                    //判断是否有录音
                    if (new File(FileName).exists()) {
                        ll_video.setVisibility(View.GONE);
                        ll_play.setVisibility(View.VISIBLE);
                    } else {
                        ll_video.setVisibility(View.VISIBLE);
                        ll_play.setVisibility(View.GONE);
                    }
                    // 设置长摁点击事件
                    ll_video.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    // 长摁事件
                                    // 更换组件样式
                                    ll_video.setBackgroundColor(Color.parseColor("#808080"));
                                    giv_video.setVisibility(View.VISIBLE);
                                    // 开始录音
                                    mRecorder = new MediaRecorder();
                                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                    mRecorder.setOutputFile(FileName);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                    try {
                                        mRecorder.prepare();
                                        mRecorder.start();
                                    } catch (IOException e) {
                                        Log.e(TAG, "prepare() failed ---" + e.getMessage());
                                    }
                                    return true;

                                case MotionEvent.ACTION_UP:
                                    ll_video.setBackgroundColor(Color.parseColor("#ffffff"));
                                    giv_video.setVisibility(View.GONE);
                                    // 松开手指，停止录音
                                    if (mRecorder != null) {
                                        mRecorder.stop();
                                        mRecorder.release();
                                        mRecorder = null;
                                        // 展示播放和隐藏录音按钮
                                        ll_video.setVisibility(View.GONE);
                                        ll_play.setVisibility(View.VISIBLE);
                                        // 录音停止，可以在这里处理录音文件
                                    }
                                    return true;

                                default:
                                    return false;
                            }
                        }
                    });

                    ViewPager vp_pic = contentView.findViewById(R.id.vp_picture);
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
                    });
                    vp_pic.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
                        @Override
                        public void onPageSelected(int position) {
                            tv.setText(position + 1 + "/" + url_list.size());
                            // 更新文件路径
                            FileName = Environment.getExternalStorageDirectory().getAbsolutePath();
                            FileName += "/" + readTitle + (position + 1) + ".mp3";
                            //判断是否有本页面的录音
                            if (new File(FileName).exists()) {
                                ll_video.setVisibility(View.GONE);
                                ll_play.setVisibility(View.VISIBLE);
                            } else {
                                ll_video.setVisibility(View.VISIBLE);
                                ll_play.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onPageScrollStateChanged(int state) {}
                    });
                } else{
                    //顶部标签
                    TextView tv = contentView.findViewById(R.id.tv_picNum);
                    tv.setText("1/" + url_list.size());
                }
                adapter.notifyDataSetChanged();
                window.showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                break;
            case R.id.ll_play:
                if (!isPlaying) {
                    mPlayer = new MediaPlayer();
                    try {
                        giv_play.setImageResource(R.drawable.play_dyn);
                        tv_play.setText("停止播放");
                        mPlayer.setDataSource(FileName);
                        mPlayer.prepare();
                        mPlayer.start();
                        isPlaying = true;
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mPlayer.release();
                                mPlayer = null;
                                isPlaying = false;
                                tv_play.setText("播放录音");
                                giv_play.setImageResource(R.drawable.play_static);
                            }
                        });
                    } catch (IOException e) {
                        Log.e(TAG, "播放失败");
                    }
                } else {
                    mPlayer.release();
                    mPlayer = null;
                    isPlaying = false;
                    tv_play.setText("播放录音");
                    giv_play.setImageResource(R.drawable.play_static);
                }
                break;
            case R.id.btn_submit:
                //判断是否有录音
                if (new File(FileName).exists()) {
                    //上传录音
                    Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}