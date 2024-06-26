package com.example.yidiantong.fragment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.HomeworkInterface;
import com.example.yidiantong.util.StringUtils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

public class LearnPlanVideoFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearnPlanVideoFragment";
    private PagingInterface paging;
    private LearnPlanInterface transmit;

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private StuAnswerEntity stuAnswerEntity;

    // 多媒体
    private PlayerView playerView;
    private SimpleExoPlayer player;

    // 观看时间
    private long timeStart;

    public static LearnPlanVideoFragment newInstance(LearnPlanItemEntity learnPlanEntity,int position, int size, StuAnswerEntity stuAnswerEntity) {
        LearnPlanVideoFragment fragment = new LearnPlanVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }
    public static LearnPlanVideoFragment newInstance(LearnPlanItemEntity learnPlanEntity,int position, int size) {
        LearnPlanVideoFragment fragment = new LearnPlanVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }
    public static LearnPlanVideoFragment newInstance(LearnPlanItemEntity learnPlanEntity) {
        LearnPlanVideoFragment fragment = new LearnPlanVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }

    //绑定Activity的接口类，实现调用
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        paging = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        int position = 0, size = 0;
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
            position = getArguments().getInt("position") + 1;
            size = getArguments().getInt("size");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_learn_plan_video, container, false);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getResourceName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        // 多媒体
        playerView = view.findViewById(R.id.pv_content);
        if(player == null){
            // 创建一个ExoPlayer实例
            player = new SimpleExoPlayer.Builder(getActivity()).build();
            // 设置PlayerView用于显示视频
            playerView.setPlayer(player);
            // 创建媒体项
            String audioUrl = learnPlanEntity.getUrl();
            // 判断音频还是视频
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(audioUrl);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);

            if (mimeType != null) {
                if (mimeType.startsWith("audio/")) {
                    // 检查当前播放的媒体类型，如果是音频，设置固定高度
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT);
                    playerView.getLayoutParams().height = PxUtils.dip2px(getActivity(), 90);
                } else if (mimeType.startsWith("video/")) {
                    // 获取屏幕宽度
                    float screenWidth = getResources().getDisplayMetrics().widthPixels;

                    // 计算高度为宽度的 3/4
                    float aspectRatio = 3.0f / 4.0f;
                    int height = Math.round(screenWidth * aspectRatio);

                    // 设置视图的高度
                    ViewGroup.LayoutParams layoutParams = playerView.getLayoutParams();
                    layoutParams.height = height;

                    // 如果是视频，设置 PlayerView 的高度为 WRAP_CONTENT
                    playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                }
            }
            playerView.setUseController(true); // 启用控制器

            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(audioUrl));
            // 隐藏音频切换按钮
            playerView.setShowPreviousButton(false);
            playerView.setShowNextButton(false);
            // 设置媒体项给播放器
            player.setMediaItem(mediaItem);
            // 准备播放器
            player.prepare();

        }else{
            // 设置PlayerView用于显示视频
            playerView.setPlayer(player);
            // 隐藏音频切换按钮
            playerView.setShowPreviousButton(false);
            playerView.setShowNextButton(false);
        }
        TextView tv_question_number = view.findViewById(R.id.tv_question_number);
        int positionLen = String.valueOf(position).length();
        String questionNum = position + "/" + size + "题";
        SpannableString spannableString = StringUtils.getStringWithColor(questionNum, "#6CC1E0", 0, positionLen);
        tv_question_number.setText(spannableString);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                paging.pageLast();
                break;
            case R.id.iv_page_next:
                paging.pageNext();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
        transmit.uploadTime(System.currentTimeMillis() - timeStart);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}