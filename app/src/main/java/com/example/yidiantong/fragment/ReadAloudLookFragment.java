package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.ReadTaskAudioEntity;
import com.example.yidiantong.bean.ReadTaskEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.HomeworkInterface2;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import com.yanzhenjie.permission.runtime.Permission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ReadAloudLookFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReadAloudLookFragment";

    private static final int REQUEST_CODE_MICROPHONE = 3;

    private LinearLayout ll_btn_panel;
    private LinearLayout ll_look_result;
    private LinearLayout ll_start_read;
    private LinearLayout ll_my_recording;
    private TextView tv_recording_num;
    private TextView tv_recording_num2;

    private RelativeLayout rl_reading_panel;
    private LinearLayout ll_my_recording_panel;

    private FlexboxLayout fl_video;
    private TextView tv_duration;
    private ImageView iv_reading_gif;

    private String mRequestUrl;
    private ImageView iv_close;
    private ImageView iv_read_result;

    private MediaPlayer mPlayer;
    private MediaRecorder mRecorder;
    private int fl_video_width;

    public ReadAloudLookFragment() {
    }

    private ReadTaskEntity readTask;
    private PagingInterface pageing;
    private HomeworkInterface2 homework;
    private Timer timer;
    private int audioTime;
    private LinearLayout ll_playing;
    private RelativeLayout rl_stop;
    private TextView tv_time;
    private ImageView iv_delete;
    private Button btn_submit;
    private String audioPath;
    private ImageView iv_icon;
    private RelativeLayout rl_block;
    private LinearLayout ll_pageing;

    private int pos;
    private int total;
    //是否正在播放
    private boolean isPlaying = false;
    private ImageView last_iv_icon;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
        homework = (HomeworkInterface2) context;
    }

    public static ReadAloudLookFragment newInstance(ReadTaskEntity readTask, int pos, int total) {
        ReadAloudLookFragment fragment = new ReadAloudLookFragment();
        Bundle args = new Bundle();
        args.putSerializable("readTask", readTask);
        args.putInt("pos", pos);
        args.putInt("total", total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            readTask = (ReadTaskEntity) getArguments().getSerializable("readTask");
            pos = getArguments().getInt("pos");
            total = getArguments().getInt("total");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_aloud_look, container, false);

        //设置录音文件保存的路径
        audioPath = getActivity().getExternalFilesDir(null) + "/stu_recording.mp3";
        // 主图片修改
        ImageView iv_main = view.findViewById(R.id.iv_main);
        Glide.with(getActivity()).load(readTask.imageUrl).into(iv_main);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);
        ll_pageing = view.findViewById(R.id.ll_pageing);
        if (total == 1) {
            ll_pageing.setVisibility(View.GONE);
        }

        /* 底部三大模块 */
        // 主按钮面板
        ll_btn_panel = view.findViewById(R.id.ll_btn_panel);
        ll_look_result = view.findViewById(R.id.ll_look_result);
        ll_start_read = view.findViewById(R.id.ll_start_read);
        ll_my_recording = view.findViewById(R.id.ll_my_recording);
        tv_recording_num = view.findViewById(R.id.tv_recording_num);
        tv_recording_num2 = view.findViewById(R.id.tv_recording_num2);

        // 录制中面板
        rl_reading_panel = view.findViewById(R.id.rl_reading_panel);
        tv_duration = view.findViewById(R.id.tv_duration);
        iv_reading_gif = view.findViewById(R.id.iv_reading_gif);
        ll_playing = view.findViewById(R.id.ll_playing);
        ll_playing.setOnClickListener(this);
        tv_time = view.findViewById(R.id.tv_time);
        iv_delete = view.findViewById(R.id.iv_delete);
        iv_delete.setOnClickListener(this);
        btn_submit = view.findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        iv_icon = view.findViewById(R.id.iv_icon);
        rl_stop = view.findViewById(R.id.rl_stop);
        rl_block = view.findViewById(R.id.rl_block);
        rl_block.setOnClickListener(this);

        // 我的录音面板
        ll_my_recording_panel = view.findViewById(R.id.ll_my_recording_panel);
        fl_video = view.findViewById(R.id.fl_video);
        iv_close = view.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        iv_read_result = view.findViewById(R.id.iv_read_result);
        iv_read_result.setOnClickListener(this);
        // 主面板初始化
        ll_look_result.setOnClickListener(this);
        ll_start_read.setOnClickListener(this);
        ll_my_recording.setOnClickListener(this);


        return view;
    }

    public void switchPanel(int type) {
        stopAudioIfPlay();
        ll_btn_panel.setVisibility(View.GONE);
        rl_reading_panel.setVisibility(View.GONE);
        ll_my_recording_panel.setVisibility(View.GONE);
        switch (type) {
            case 1:
                ll_btn_panel.setVisibility(View.VISIBLE);
                break;
            case 2:
                rl_reading_panel.setVisibility(View.VISIBLE);
                break;
            case 3:
                ll_my_recording_panel.setVisibility(View.VISIBLE);
                break;
        }
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
            // 主页跳转
            case R.id.ll_start_read:
                startRecording();
                break;
            case R.id.ll_my_recording:
                showRecording();
                break;
            case R.id.iv_close:
                switchPanel(1);
                break;
            case R.id.ll_look_result:
            case R.id.iv_read_result:
                if (audioList.size() == 0) {
                    Toast.makeText(getActivity(), "没有朗读记录", Toast.LENGTH_SHORT).show();
                } else {
                    jumpToResult();
                }
                break;
            case R.id.ll_playing:
                stopRecoding();
                break;
            case R.id.iv_delete:
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否删除录音？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new File(audioPath).delete();
                                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                switchPanel(1);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.btn_submit:
                uploadAudioFile();
                break;
            case R.id.rl_block:
                if (!isPlaying) {
                    mPlayer = new MediaPlayer();
                    try {
                        iv_icon.setImageResource(R.drawable.play_recording_on);
                        mPlayer.setDataSource(audioPath);
                        mPlayer.prepare();
                        mPlayer.start();
                        isPlaying = true;
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mPlayer.release();
                                mPlayer = null;
                                isPlaying = false;
                                iv_icon.setImageResource(R.drawable.play_recording_off);
                            }
                        });
                    } catch (IOException e) {
                        iv_icon.setImageResource(R.drawable.play_recording_off);
                        Log.e(TAG, "播放失败" + e);
                    }
                } else {
                    mPlayer.release();
                    isPlaying = false;
                    iv_icon.setImageResource(R.drawable.play_recording_off);
                }
                break;
        }
    }

    private void stopRecoding() {
        ll_playing.setVisibility(View.GONE);
        rl_stop.setVisibility(View.VISIBLE);
        tv_time.setText(audioTime + "″");
        // 停止计时器
        if (timer != null) {
            timer.cancel(); // 取消定时器
            timer.purge(); // 清理已取消的任务
            timer = null; // 确保GC可以回收
        }
        // 停止录音
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    // 开始录制的页面UI相关，包括录音组件相关
    private void startRecording() {
        // 显示正在录制
        switchPanel(2);
        ll_playing.setVisibility(View.VISIBLE);
        rl_stop.setVisibility(View.GONE);
        // 开启定时器，每秒更新数字
        if (timer != null) {
            timer.cancel(); // 取消旧的定时器
        }
        timer = new Timer();
        audioTime = 0;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                audioTime++;
                // 更新UI（假设有一个方法来更新UI）
                tv_duration.post(new Runnable() {
                    @Override
                    public void run() {
                        // 更新UI
                        tv_duration.setText("(" + audioTime + "秒)");

                    }
                });
            }
        }, 0, 1000);  // 延迟0秒，每1000毫秒（1秒）执行一次
        permissionMico();
    }

    // 查看录制结果面板
    private void showRecording() {
        fl_video.removeAllViews();
        switchPanel(3);
        final ViewTreeObserver observer = fl_video.getViewTreeObserver();
        fl_video_width = fl_video.getWidth();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 移除监听器，避免多次触发
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fl_video.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    fl_video.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                fl_video_width = fl_video.getWidth();
                if (fl_video_width > 0) { // 确保宽度大于0
                    for (int i = 0; i < audioList.size(); i++) {
                        addOneVedioBlock(i, audioList.get(i).time);
                    }
                }
            }
        });
    }


    private void jumpToResult() {
        stopAudioIfPlay();
        homework.jumpToSubmit();
    }


    private void addOneVedioBlock(int pos, String time) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_read_aloud_vedio_block, fl_video, false);
        TextView tv_id = view.findViewById(R.id.tv_id);

        tv_id.setText(String.valueOf(pos + 1));
        TextView tv_time = view.findViewById(R.id.tv_time);
        ImageView iv_icon = view.findViewById(R.id.iv_icon);
        ImageView iv_delete = view.findViewById(R.id.iv_delete);

        tv_time.setText(time + "″");

        View.OnClickListener playVideoListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudioByView(pos, iv_icon);
            }
        };
        // 点击事件，播放
        tv_time.setOnClickListener(playVideoListener);
        iv_icon.setOnClickListener(playVideoListener);

        // 点击事件，删除
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否删除录音？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAudio(pos);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });


        RelativeLayout rl_block = view.findViewById(R.id.rl_block);
        ViewGroup.LayoutParams params = rl_block.getLayoutParams();
        params.width = fl_video_width / 3 - PxUtils.dip2px(view.getContext(), 15);
        rl_block.setLayoutParams(params);
        fl_video.addView(view);
    }

    private void startMico() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);  // 使用优化的音源
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  // 使用高质量输出格式
        mRecorder.setOutputFile(audioPath);  // 设置输出路径
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  // 使用更高质量的AAC编码
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed ---" + e.getMessage());
        }
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private List<ReadTaskAudioEntity> audioList;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case 100:
                    audioList = (List<ReadTaskAudioEntity>) message.obj;
                    refreshNum();
                    homework.offLoading();
                    break;

            }
        }
    };

    private void refreshNum() {
        tv_recording_num.setText(String.valueOf(audioList.size()));
        tv_recording_num2.setText("(" + audioList.size() + ")");
    }

    private void loadItems_Net() {
        // 图片录音列表
        String mRequestUrl = Constant.API + Constant.GET_AUDIO_LIST + "?recordId=" + readTask.recordId + "&imageId=" + readTask.imageId + "&stuId=" + MyApplication.username;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Gson gson = new Gson();
                // 使用Gson框架转换Json字符串为列表
                List<ReadTaskAudioEntity> itemList = gson.fromJson(itemString, new TypeToken<List<ReadTaskAudioEntity>>() {
                }.getType());
                if (itemList == null) {
                    itemList = new ArrayList<>();
                }
                Message msg = new Message();
                msg.what = 100;
                msg.obj = itemList;
                handler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
//                rl_loading.setVisibility(View.GONE);
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
//            rl_loading.setVisibility(View.GONE);
        });
        MyApplication.addRequest(request, TAG);
    }

    private void uploadAudioFile() {
        homework.onLoading("录音上传中...");
        Uri fileUri = Uri.fromFile(new File(audioPath));
        String mRequestUrl = Constant.API + Constant.UPLOAD_AUDIO;


        InputStream inputStream = null;
        byte[] bytes = new byte[0];
        Map<String, String> params = new HashMap<>();

        try {
            inputStream = getActivity().getContentResolver().openInputStream(fileUri);
            bytes = getBytes(inputStream);

            String base64String = Base64.encodeToString(bytes, Base64.NO_WRAP);
            params.put("recordId", readTask.recordId);
            params.put("imageId", readTask.imageId);
            params.put("stuId", MyApplication.username);
            params.put("stuName", URLEncoder.encode(MyApplication.cnName, "UTF-8"));
            params.put("baseCode", base64String);
            params.put("second", String.valueOf(audioTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringRequest request = new StringRequest(Request.Method.POST, mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String url = json.getString("data");
                Boolean isSuccess = json.getBoolean("success");
                if (isSuccess) {
                    mp3ToPcm(url);
                } else {
                    homework.offLoading();
                    Toast.makeText(getActivity(), "录音上传失败!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                homework.offLoading();
                e.printStackTrace();
            }
        }, error -> {
            homework.offLoading();
            Log.e("volley", "Volley_Error: " + error.toString());
            Toast.makeText(getActivity(), "上传出错!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        MyApplication.addRequest(request, TAG);
    }

    private void mp3ToPcm(String url) {
        // 跟读作业列表
        String mRequestUrl = Constant.API_NEW + Constant.PM3_TO_PCM_TO_TEXT + "?id=" + url;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String status = json.getString("status");
                if ("success".equals(status)) {
                    Toast.makeText(getActivity(), "录音上传成功!", Toast.LENGTH_SHORT).show();
                    switchPanel(1);
                    loadItems_Net();
                } else {
                    Toast.makeText(getActivity(), "录音上传失败!", Toast.LENGTH_SHORT).show();
                    homework.offLoading();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "上传出错!", Toast.LENGTH_SHORT).show();
                homework.offLoading();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
            homework.offLoading();
        });
        MyApplication.addRequest(request, TAG);
    }

    private void deleteAudio(int pos) {
        stopAudioIfPlay();
        homework.onLoading("录音删除中...");
        // 跟读作业列表
        String mRequestUrl = Constant.API + Constant.DELETE_AUDIO + "?id=" + audioList.get(pos).id;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String status = json.getString("success");
                if ("true".equals(status)) {
                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    loadItems_Net();
                } else {
                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    homework.offLoading();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "删除出错", Toast.LENGTH_SHORT).show();
                homework.offLoading();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void playAudioByView(int pos, ImageView iv_icon) {
        // 如果当前正在播放，则停止并释放播放器
        stopAudioIfPlay();
        if (last_iv_icon == iv_icon) {
            return;
        }
        last_iv_icon = iv_icon;
        // 创建新的 MediaPlayer 实例
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioList.get(pos).url);  // 设置数据源为 URL

            // 异步准备播放器
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();  // 当准备好时开始播放
                    isPlaying = true;
                    iv_icon.setImageResource(R.drawable.play_recording_on);  // 更改图标为暂停
                }
            });

            // 设置播放完成后的处理逻辑
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mPlayer = null;
                    isPlaying = false;
                    iv_icon.setImageResource(R.drawable.play_recording_off);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopAudioIfPlay() {
        if (isPlaying && mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
            last_iv_icon.setImageResource(R.drawable.play_recording_off);  // 更改播放按钮图标
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAudioIfPlay();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems_Net(); // 刷新音频数据
        switchPanel(1); // 重置UI
    }

    private void permissionMico() {
        // 权限请求
        AndPermission.with(this)
                .runtime()
                .permission(Permission.Group.MICROPHONE, Permission.Group.STORAGE)
                .onGranted(new Action<List<String>>() {
                    // 获得权限后
                    @Override
                    public void onAction(List<String> data) {
                        startMico();
                    }
                }).onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        //判断是否点了永远拒绝，不再提示
                        if (AndPermission.hasAlwaysDeniedPermission(getActivity(), data)) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("权限被禁用")
                                    .setMessage("录音权限被禁用，请到APP设置页面手动开启！")
                                    .setPositiveButton("跳转", (dialog, which) -> {
                                        AndPermission.with(ReadAloudLookFragment.this)
                                                .runtime()
                                                .setting()
                                                .start(REQUEST_CODE_MICROPHONE);
                                    })
                                    .setNegativeButton("取消", (dialog, which) -> {

                                    })
                                    .show();
                        }
                    }
                })
                .rationale(rMico)
                .start();
    }

    /**
     * 第三方权限申请包-自定义权限提示，出现在首次拒绝后。
     */
    private Rationale rMico = new Rationale() {
        @Override
        public void showRationale(Context context, Object data, RequestExecutor executor) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("提示")
                    .setMessage("开启麦克风权限才能跟读！")
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
        switch (requestCode) {
            case REQUEST_CODE_MICROPHONE:
                if (AndPermission.hasPermissions(this, Permission.Group.MICROPHONE, Permission.Group.STORAGE)) {
                    // 有对应的权限
                    Toast.makeText(getActivity(), "麦克风权限已获取！", Toast.LENGTH_SHORT).show();
                } else {
                    // 没有对应的权限
                    Toast.makeText(getActivity(), "麦克风权限未获取！", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}