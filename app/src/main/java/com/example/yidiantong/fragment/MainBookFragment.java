package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksAdapter;
import com.example.yidiantong.bean.BookInfoEntity;
import com.example.yidiantong.ui.MainBookActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainBookFragment extends Fragment {
    private static final String TAG = "MainBookFragment";
    private View contentView = null;
    private RecyclerView frv_channel;
    //错题本列表数据
    private List<BookInfoEntity> itemList = new ArrayList<>();
    private String subjectId; // 学科id
    //错题本适配器
    BooksAdapter adapter;
    private String username;//当前用户名

    private SharedPreferences preferences;
    private String[] stuLoadAnswer;

    //获得实例，并绑定参数
    public static MainBookFragment newInstance(){
        MainBookFragment fragment = new MainBookFragment();
        Bundle args = new Bundle();
        //将参数传递给fragment
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 将布局文件fragment_mian_book转化为视图对象
        View view = inflater.inflate(R.layout.fragment_mian_book, container, false);
        //获取RecyclerView组件
        frv_channel = view.findViewById(R.id.frv_channel);
        //RecyclerView两步必要配置
        frv_channel.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        frv_channel.setItemAnimator(new DefaultItemAnimator());
        //获取登录传递的参数
        username = MyApplication.username;
        //设置RecyclerViewAdapter
        adapter = new BooksAdapter(getContext(), new ArrayList<>());

//        // 展示课程信息
//        loadItems_Net();

        //将适配器与RecyclerView关联起来，这样rv_home可以通过适配器来获取数据并展示在界面上
        frv_channel.setAdapter(adapter);
        //在滚动过程中，视图不会被缓存起来以供重复使用。这样做的好处是可以减少内存的占用
        frv_channel.setItemViewCacheSize(0);

        // 创建具有两列的网格布局,frv_channel中的子项会按照网格布局的方式排列，每行显示两个子项
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        frv_channel.setLayoutManager(layoutManager);

        preferences = getActivity().getSharedPreferences("book", Context.MODE_PRIVATE);
        //设置学科item点击事件
        adapter.setmItemClickListener((v, pos) -> {

            Intent intent = new Intent(getActivity(), MainBookActivity.class);
            subjectId = adapter.itemList.get(pos).getSubjectId();
            intent.putExtra("subjectName", adapter.itemList.get(pos).getSubjectName());  //学科名
            intent.putExtra("subjectId", subjectId); //学科id
            intent.putExtra("username", username);   //当前用户名
            startActivity(intent);
            if(adapter.itemList.get(pos).getStatus().equals("1")){
                reload();
                adapter.itemList.get(pos).setStatus("0"); // 修改通知item的状态为已读（4）
                adapter.notifyDataSetChanged();  // 刷新列表信息
            }

            SharedPreferences.Editor edit = preferences.edit();
            int length = Integer.parseInt(adapter.itemList.get(pos).errorQueNum);
            stuLoadAnswer = new String[length];
            String arrayString  = TextUtils.join(",", stuLoadAnswer);
            System.out.println("arrayString: " + arrayString);
            edit.putString("stuLoadAnswer", arrayString);
            edit.apply();

        });

        return view;
    }

    // 主要用于处理来自其他线程的消息，根据消息的类型进行相应的界面更新或操作。
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                //加载学科数据
                adapter.loadData((List<BookInfoEntity>) message.obj);
            }
        }
    };

    //请求item数据，加载消息条目，包括刷新和加载
    //网络请求等耗时操作不能在主线程中执行，否则会阻塞ui线程，故会在后台线程中执行相关操作Android 提供了 Handler 机制，可以用来在不同线程之间传递消息和操作
    //Handler 可以在后台线程中发送消息，然后在主线程中接收这些消息，并在主线程中执行相关操作。
    private void loadItems_Net() {
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_SUBJECT + "?token=1" + "&userName=" + username;
        Log.e("mReq",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookInfoEntity> moreList = gson.fromJson(itemString, new TypeToken<List<BookInfoEntity>>() {}.getType());
                for(int i = 0; i < moreList.size(); ++i){
                    Log.e("wen0223", "loadItems_Net: " + moreList.get(i));
                }
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = moreList;
                //标识线程
                message.what = 100;
                handler.sendMessage(message);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());
        });
        // 设置重试策略
        int socketTimeout = 120000; // 2分钟
        RetryPolicy policy = new DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

//
//    private final Handler handler2 = new Handler(Looper.getMainLooper()) {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @SuppressLint("NotifyDataSetChanged")
//        @Override
//        public void handleMessage(Message message) {
//            super.handleMessage(message);
//            if (message.what == 100) {
//                int f = (int) message.obj;
//                if (f == 0) {
//                    Toast.makeText(getContext(), "已读状态修改失败", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(getContext(), "已读状态修改成功", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    };

    // 发送请求修改数据集中当前用户的学科错题本为已读状态
    public void reload() {
        //点击学科，有未读试题时才调用该接口,修改错题已读状态
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_UPDATE_STATUS + "?token=1" +"&userName=" +username + "&subjectId=" + subjectId;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//                //结果信息
//                Boolean isSuccess = json.getBoolean("success");
//                Message msg = Message.obtain();
//                if (isSuccess) {
//                    msg.obj = 1;
//                } else {
//                    msg.obj = 0;
//                }
//                msg.what = 100;
//                handler2.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());
        });
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems_Net();
    }

}