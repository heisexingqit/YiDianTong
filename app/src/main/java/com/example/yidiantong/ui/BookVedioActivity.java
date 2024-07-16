package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class BookVedioActivity extends AppCompatActivity {

    private StyledPlayerView f_spv;
    private ExoPlayer mPlayer;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_vedio);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });

        // 设置视频
        mPlayer = new ExoPlayer.Builder(this.getApplication()).build();
        f_spv = findViewById(R.id.f_spv);
        f_spv.setPlayer(mPlayer);
        Uri playUri = Uri.parse("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4");
        MediaItem item = MediaItem.fromUri(playUri);
        mPlayer.setMediaItem(item);
        mPlayer.setPlayWhenReady(true);
        mPlayer.prepare();


        mPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch(playbackState){
                    //加载缓存且还未准备好时触发
                    case Player.STATE_BUFFERING:
                        Toast.makeText(getApplicationContext(), "buffering", Toast.LENGTH_SHORT).show();
                        break;

                    //视频准备完毕时触发
                    case Player.STATE_READY:
                        Toast.makeText(getApplicationContext(), "ready", Toast.LENGTH_SHORT).show();
                        break;

                    //视频播放结束时触发
                    case Player.STATE_ENDED:
                        Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                //播放状态改变时触发
                if(isPlaying){
                    Toast.makeText(getApplicationContext(), "play", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.pause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }
}