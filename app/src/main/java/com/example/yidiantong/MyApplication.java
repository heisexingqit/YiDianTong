package com.example.yidiantong;

import android.app.Application;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class MyApplication extends Application {


    //ImageLoader显示图片过程中的参数
    private static DisplayImageOptions mLoaderOptions;
    private static RequestQueue mQueue;
    //主界面浮动按钮图片参数
    private static DisplayImageOptions mFlatOptions;

    //初始化ImageLoader
    public static void initImageLoader(Context context) {
        //初始化一个ImageLoaderConfiguration配置对象
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.
                Builder(context).
                memoryCacheExtraOptions(480, 800). // max width, max height，即保存的每个缓存文件的最大长宽
                        denyCacheImageMultipleSizesInMemory().
                threadPriority(Thread.NORM_PRIORITY - 2).
                diskCacheFileNameGenerator(new Md5FileNameGenerator()).
                tasksProcessingOrder(QueueProcessingType.FIFO).
                build();
        //用ImageLoaderConfiguration配置对象完成ImageLoader的初始化，单例
        ImageLoader.getInstance().init(config);
        //示图片过程中的参数
        mLoaderOptions = new DisplayImageOptions.Builder().
                showImageOnLoading(R.mipmap.no_image).//正加载，显示no_image
                        showImageOnFail(R.mipmap.no_image).//加载失败时
                        showImageForEmptyUri(R.mipmap.no_image).//加载的Uri为空
                        imageScaleType(ImageScaleType.EXACTLY_STRETCHED).
                //displayer(new RoundedBitmapDisplayer(360)).//是否设置为圆角，弧度为多少
                        cacheInMemory(true).//是否进行缓冲
                        cacheOnDisk(true).
                considerExifParams(true).
                build();

        mFlatOptions = new DisplayImageOptions.Builder().
                showImageOnLoading(R.mipmap.no_image).//正加载，显示no_image
                        showImageOnFail(R.mipmap.no_image).//加载失败时
                        showImageForEmptyUri(R.mipmap.no_image).//加载的Uri为空
                        imageScaleType(ImageScaleType.EXACTLY_STRETCHED).
                displayer(new RoundedBitmapDisplayer(360)).//是否设置为圆角，弧度为多少
                        cacheInMemory(true).//是否进行缓冲
                        cacheOnDisk(true).
                considerExifParams(true).
                build();
    }

    //将volley请求加入到请求队列,设置超时
    public static void addRequest(Request request, Object tag) {
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    //简单的Get和Set
    public static DisplayImageOptions getLoaderOptions() {
        return mLoaderOptions;
    }

    public static DisplayImageOptions getmFlatOptions() {
        return mFlatOptions;
    }

    public static RequestQueue getHttpQueue() {
        return mQueue;
    }

    public static void removeRequest(Object tag) {
        mQueue.cancelAll(tag);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化ImageLoader
        initImageLoader(getApplicationContext());
        //初始化Volley的请求队列，使用okhttp替代volley底层链接
        mQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());
    }
}
