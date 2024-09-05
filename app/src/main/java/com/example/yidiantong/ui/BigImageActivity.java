package com.example.yidiantong.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yidiantong.R;
import com.github.chrisbanes.photoview.PhotoView;
public class BigImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image);

        PhotoView photoView = findViewById(R.id.photoView);
        RelativeLayout rl_loading =findViewById(R.id.rl_loading);
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // 使用Glide加载图片，并监听加载过程
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        finish();
                        Toast.makeText(BigImageActivity.this, "资源加载错误", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        rl_loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);
        // 设置点击监听器以关闭Activity
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed(); // 否则，结束当前Activity
    }
}