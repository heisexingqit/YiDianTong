package com.example.yidiantong.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.github.chrisbanes.photoview.PhotoView;


public class ResourceFolderImgFragment extends Fragment {

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    public static ResourceFolderImgFragment newInstance(LearnPlanItemEntity learnPlanEntity) {
        ResourceFolderImgFragment fragment = new ResourceFolderImgFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_resource_folder_img, container, false);

        PhotoView photoView = view.findViewById(R.id.photoView);
        RelativeLayout rl_loading =view.findViewById(R.id.rl_loading);

        String imageUrl = learnPlanEntity.getUrl();

        // 使用Glide加载图片，并监听加载过程
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        rl_loading.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);
        return view;
    }
}