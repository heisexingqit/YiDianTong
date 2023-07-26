package com.example.yidiantong.fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.android.volley.RequestQueue;
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
    //列表数据
    private List<BookInfoEntity> itemList = new ArrayList<>();
    private String subjectId;

    public static MainBookFragment newInstance(){
        MainBookFragment fragment = new MainBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    BooksAdapter adapter;
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mian_book, container, false);
        frv_channel = view.findViewById(R.id.frv_channel);

        //RecyclerView两步必要配置
        frv_channel.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        frv_channel.setItemAnimator(new DefaultItemAnimator());

        //获取登录传递的参数
        username = MyApplication.username;

        //设置RecyclerViewAdapter
        adapter = new BooksAdapter(getContext(), itemList);

        // 展示课程信息
        loadItems_Net();

        frv_channel.setAdapter(adapter);
        frv_channel.setItemViewCacheSize(0);
        // 设置网格
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        frv_channel.setLayoutManager(layoutManager);

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(getActivity(), MainBookActivity.class);
            subjectId = adapter.itemList.get(pos).getSubjectId();
            intent.putExtra("subjectName", adapter.itemList.get(pos).getSubjectName());
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("username", username);
            startActivity(intent);
            reload();
        });

        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.loadData((List<BookInfoEntity>) message.obj);
            }
        }
    };

    //加载消息条目
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
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    private final Handler handler2 = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                int f = (int) message.obj;
                if (f == 0) {
                    //Toast.makeText(getContext(), "已读状态修改失败", Toast.LENGTH_SHORT).show();
                }else {
                    //Toast.makeText(getContext(), "已读状态修改成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    // 修改已读状态
    public void reload() {
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_UPDATE_STATUS + "?token=1" +"&userName=" +username + "&subjectId=" + subjectId;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                Message msg = Message.obtain();
                if (isSuccess) {
                    msg.obj = 1;
                } else {
                    msg.obj = 0;
                }
                msg.what = 100;
                handler2.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
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