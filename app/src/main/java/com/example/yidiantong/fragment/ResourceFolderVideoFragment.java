package com.example.yidiantong.fragment;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.HomeworkInterface;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.LoadEventInfo;
import com.google.android.exoplayer2.source.MediaLoadData;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

public class ResourceFolderVideoFragment extends Fragment {
    private static final String TAG = "ResourceFolderVideoFragment";

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;

    // 多媒体
    private PlayerView playerView;
    private SimpleExoPlayer player;

    public static ResourceFolderVideoFragment newInstance(LearnPlanItemEntity learnPlanEntity) {
        ResourceFolderVideoFragment fragment = new ResourceFolderVideoFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_resource_folder_video, container, false);

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
                    playerView.getLayoutParams().height = PxUtils.dip2px(getActivity(), 90);
                    playerView.setControllerHideOnTouch(false);
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
            // 设置媒体项给播放器
            player.setMediaItem(mediaItem);
            // 隐藏音频切换按钮
            playerView.setShowPreviousButton(false);
            playerView.setShowNextButton(false);
            // 准备播放器
            player.prepare();

        }else{
            // 设置PlayerView用于显示视频
            playerView.setPlayer(player);
            // 隐藏音频切换按钮
            playerView.setShowPreviousButton(false);
            playerView.setShowNextButton(false);
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }
}