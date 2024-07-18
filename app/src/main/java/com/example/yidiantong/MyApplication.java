package com.example.yidiantong;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.room.Room;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.database.YDTDatabase;
import com.example.yidiantong.ui.LoginActivity;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    // 全局变量
    public static String username;
    public static String userId;
    public static String cnName;
    public static String token;
    public static String picUrl;
    public static String password;
    public static Boolean autoLogin;
    public static String typeName;
    public static int typeActivity = 0; // 0代表没有 1代表KnowledgeShiTiDetailActivity   2 代表KnowledgeShiTiHistoryActivity

    // 旋转屏幕处理
    public static int currentItem = 0;
    public static boolean isRotate = false;

    private static RequestQueue mQueue;
    //ImageLoader显示图片过程中的参数
    private static DisplayImageOptions mLoaderOptions;
    private static String lastRequestUrl;
    private static long lastRequestTime;

    // 当前版本号
    public static String versionName;

    // 数据库
    public static YDTDatabase database;

    //初始化ImageLoader
    public static void initImageLoader(Context context) {
        //初始化一个ImageLoaderConfiguration配置对象
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(800, 800). // max width, max height，即保存的每个缓存文件的最大长宽
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
    }

    //简单的Get和Set
    public static DisplayImageOptions getLoaderOptions() {
        return mLoaderOptions;
    }

    //将volley请求加入到请求队列,设置超时
    public static void addRequest(Request request, Object tag) {
        /**
         * 延长请求超时
         */
        request.setRetryPolicy(new DefaultRetryPolicy(
                1200000, // 120 seconds timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        /**
//         * 拦截重复请求，时间为1.2s内（降低效率）
//         */
//        long currentTime = System.currentTimeMillis();
//
//        if (!tag.equals("noLimited") && request.getUrl().equals(lastRequestUrl) && currentTime - lastRequestTime < 600) {
//            Log.d(TAG, "Duplicate request, ignored." + request.getUrl());
//            return;
//        }
        request.setTag(tag);
        mQueue.add(request);
//        // 控制 请求速度
//        lastRequestUrl = request.getUrl();
//        lastRequestTime = currentTime;
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
        autoLogin = true;

        //初始化ImageLoader
        initImageLoader(getApplicationContext());

        // 初始化Volley的请求队列，使用okhttp替代volley底层链接
        mQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack(), -1);

        // 获取包管理器
        PackageManager packageManager = getPackageManager();
        // 获取应用的包信息
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 获取版本号和版本名
        versionName = packageInfo.versionName;

        // 数据库对象
        // 1. 学生作答数据表
        database = Room.databaseBuilder(this, YDTDatabase.class, "ydt_db")
                .addMigrations()
                .allowMainThreadQueries()
                .build();
    }

    public static WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();


    @Override
    public void onTerminate() {
        super.onTerminate();
        // 当应用终止时，记录状态或执行清理工作
        Log.d(TAG, "Application is terminating.");
    }

    // 添加一个方法来检查全局变量状态
    public static boolean checkGlobalVariablesStatus() {
        // 假设我们以某个全局变量作为检查点
        if (username == null || username.isEmpty()) {
            // 如果全局变量已清空，返回true
            return true;
        }
        return false;
    }

    // 在Activity的onCreate()方法中调用此检查方法
    public void checkAndHandleGlobalVariables(Activity activity) {
        if (checkGlobalVariablesStatus()) {
            // 如果全局变量已清空，导航回首页或执行其他操作
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

}
