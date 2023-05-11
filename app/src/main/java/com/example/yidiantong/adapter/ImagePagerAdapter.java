package com.example.yidiantong.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {
    private static final String TAG = "ImagePagerAdapter";

    private List<String> urlList;

    private Context mContent;

    //点击事件
    private MyItemClickListener clickListener;

    public ImagePagerAdapter(Context mContent, List<String> urlList) {
        this.urlList = urlList;
        this.mContent = mContent;
    }

    public void setClickListener(MyItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View v = LayoutInflater.from(mContent).inflate(R.layout.item_show_photo, null, false);

        PhotoView pv_content = v.findViewById(R.id.pv_content);
        /**
         * ImageLoader 请求图片并缓存
         */
        ImageLoader.getInstance().displayImage(urlList.get(position), pv_content, MyApplication.getLoaderOptions());

        // 注：XUI版PhotoView无法设置点击事件
        pv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick();
                Log.d(TAG, "onClick: ");
            }
        });


        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    // 将container中的view和返回的object关联起来
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void updateData(List<String> list) {
        urlList = list;
        notifyDataSetChanged();
    }

    //点击dismiss
    public interface MyItemClickListener {
        void onItemClick();
    }
}
