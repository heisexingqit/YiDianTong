package com.example.yidiantong.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yidiantong.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class PlayMp4Activity extends AppCompatActivity implements Player.Listener{
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private TextView text_error;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mp4);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        String url = getIntent().getStringExtra("url");
        // 初始化播放器
        initPlayer(url);
    }
    private void initPlayer(String url) {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(this).build();
            player.addListener(this); // 添加监听器
            playerView = findViewById(R.id.player_view);
            playerView.setPlayer(player);
        }

        // 创建数据源工厂
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                Util.getUserAgent(this, "MyApplication"),
                null);

        // 创建媒体项
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));

        // 准备播放器
        player.setMediaItem(mediaItem, /* resetPosition= */ true);
        player.prepare();
        player.setPlayWhenReady(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }  @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // Handle player state changes if needed.
    }
    @Override
    public void onPlayerError(PlaybackException error) {
        // 当播放器遇到错误时调用此方法
        Toast.makeText(this, "视频资源出错", Toast.LENGTH_SHORT).show();
        finish();
        player.release(); // 释放播放器资源
        player = null; // 清空引用，防止内存泄漏
    }
}