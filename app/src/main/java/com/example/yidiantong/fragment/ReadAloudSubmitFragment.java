package com.example.yidiantong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.TReadAloudResultRecyclerAdapter;
import com.example.yidiantong.bean.ReadTaskResultEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.HomeworkInterface2;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PagingInterface;

import org.json.JSONException;
import org.json.JSONObject;


public class ReadAloudSubmitFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ReadAloudSubmitFragment";
    private TReadAloudResultRecyclerAdapter adapter;
    private ReadTaskResultEntity readTaskResult;
    private ImageView iv_empty;

    public ReadAloudSubmitFragment() {
    }

    private PagingInterface pageing;
    private HomeworkInterface2 homework;

    private LinearLayout ll_look_result;
    private LinearLayout ll_start_read;
    private LinearLayout ll_my_videos;
    private String recordId;
    private String imageId;
    private MediaPlayer mPlayer;
    private LinearLayout ll_pageing;
    //是否正在播放
    private boolean isPlaying = false;
    private ImageView last_iv_icon;

    // 是否返回
    private boolean isBack = false;
    private int total;
    private int pagePos;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pageing = (PagingInterface) context;
        homework = (HomeworkInterface2) context;
    }

    public static ReadAloudSubmitFragment newInstance(ReadTaskResultEntity readTaskResult, int total, int pagePos) {
        ReadAloudSubmitFragment fragment = new ReadAloudSubmitFragment();
        Bundle args = new Bundle();
        args.putSerializable("readTaskResult", readTaskResult);
        args.putInt("total", total);
        args.putInt("pagePos", pagePos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            readTaskResult = (ReadTaskResultEntity) getArguments().getSerializable("readTaskResult");
            total = getArguments().getInt("total");
            pagePos = getArguments().getInt("pagePos");
        }
        Log.e("wen1025",  "onCreate：" + pagePos);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_aloud_submit, container, false);

        iv_empty = view.findViewById(R.id.iv_empty);
        if (readTaskResult.ZYRecordAnswerList != null && readTaskResult.ZYRecordAnswerList.size() > 0) {
            iv_empty.setVisibility(View.GONE);
        }

        Log.e("wen1025", "onCreateView：" + pagePos);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);
        RecyclerView rv_main = view.findViewById(R.id.rv_main);
        rv_main.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());
        ll_pageing = view.findViewById(R.id.ll_pageing);
        if (total == 1) {
            ll_pageing.setVisibility(View.GONE);
        }
        // 创建一个 List<String>
        adapter = new TReadAloudResultRecyclerAdapter(getActivity(), readTaskResult.ZYRecordAnswerList);
        adapter.setIsNew(readTaskResult.isNew);
        adapter.setmItemClickListener(new TReadAloudResultRecyclerAdapter.MyItemClickListener() {
            @Override
            public void deleteVedio(int pos) {
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

            @Override
            public void redoVedio(int pos) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("是否重读？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isBack = true;
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

            @Override
            public void playVedio(int pos, ImageView iv_icon) {
                playViewByView(pos, iv_icon);
            }
        });

        rv_main.setAdapter(adapter);
        return view;
    }

    private void deleteAudio(int pos) {
        stopAudioIfPlay();
        homework.onLoading("录音删除中...");
        // 跟读作业列表
        String mRequestUrl = Constant.API + Constant.DELETE_AUDIO + "?id=" + readTaskResult.ZYRecordAnswerList.get(pos).id;

        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String status = json.getString("success");
                if ("true".equals(status)) {
                    Toast.makeText(getActivity(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    if (isBack) {
                        Intent intent = new Intent();
                        intent.putExtra("currentItem", pagePos);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    } else {
                        homework.refreshData();
                    }
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

    private void playViewByView(int pos, ImageView iv_icon) {
        // 如果当前正在播放，则停止并释放播放器
        if(last_iv_icon == iv_icon && isPlaying){
            stopAudioIfPlay();
            return;
        }
        stopAudioIfPlay();
        Log.e("wen1025", "playViewByView：" + pagePos);

        last_iv_icon = iv_icon;
        // 创建新的 MediaPlayer 实例
        mPlayer = new MediaPlayer();
        try {
            homework.onLoading("音频加载中...");
            mPlayer.setDataSource(readTaskResult.ZYRecordAnswerList.get(pos).path);  // 设置数据源为 URL

            // 异步准备播放器
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();  // 当准备好时开始播放
                    homework.offLoading();
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
        Log.e("wen1025", "stopAudioIfPlay: isPlaying = " + isPlaying + ", mPlayer = " + (mPlayer != null) + " ,pos = " + pagePos);
        if (isPlaying && mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            isPlaying = false;
            last_iv_icon.setImageResource(R.drawable.play_recording_off);  // 更改播放按钮图
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (!isVisibleToUser) {
            // 当 Fragment 不可见时，调用 stopAudioIfPlay() 方法
            stopAudioIfPlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAudioIfPlay();
    }
}