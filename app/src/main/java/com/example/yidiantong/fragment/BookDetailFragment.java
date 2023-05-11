package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksDetailAdapter;
import com.example.yidiantong.adapter.HomeworkPagerAdapter;
import com.example.yidiantong.bean.BookDetailEntity;


import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.BookRecoverActivity;
import com.example.yidiantong.ui.BookRecyclerActivity;
import com.example.yidiantong.ui.MainBookActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDetailFragment extends Fragment {

    private static final String TAG = "BookDetailActivity";
    private String course_name;
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;

    private RelativeLayout rl_loading;

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String coures_Id;

    //列表数据
    private List<BookDetailEntity> itemList = new ArrayList<>();
    private List<BookDetailEntity.errorList> errorList = new ArrayList<>();
    private List<BookDetailEntity.errorList> quesList = new ArrayList<>();
    BooksDetailAdapter adapter;
    private RecyclerView frv_detail;

    private String itemString = "";
    private String item;
    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    public static BookDetailFragment newInstance() {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);
        //顶栏返回按钮
        view.findViewById(R.id.fiv_back).setOnClickListener(v -> {
            getActivity().finish();
        });

        frv_detail = view.findViewById(R.id.frv_detail);

        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数
        username =  getActivity().getIntent().getStringExtra("username");
        coures_Id = getActivity().getIntent().getStringExtra("subjectId");
        course_name = getActivity().getIntent().getStringExtra("subjectName");
        TextView tv_title = view.findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "错题本");

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);
        fll_null = view.findViewById(R.id.fll_null);

        //设置RecyclerViewAdapter
        adapter = new BooksDetailAdapter(getContext(), errorList, itemList, quesList);
        frv_detail.setAdapter(adapter);

        refreshList();

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(getActivity(), BookRecyclerActivity.class);
            intent.putExtra("sourceId", adapter.itemList.get(pos).getSourceId());
            questionId = adapter.errorList.get(pos).getQuestionId();
            intent.putExtra("questionId", questionId);
            intent.putExtra("subjectId", coures_Id);
            intent.putExtra("username", username);
            intent.putExtra("name", course_name);
            pos = pos +1;
            intent.putExtra("pos", String.valueOf(pos));
            startActivity(intent);
        });

        // 回收站按钮
        fiv_recycle = view.findViewById(R.id.fiv_recycle);
        fiv_recycle.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookRecoverActivity.class);
            intent.putExtra("name", course_name);
            intent.putExtra("username", username);
            intent.putExtra("subjectId", coures_Id);
            // intent.putExtra("sourceId", adapter.itemList.get(0).getSourceId());
            intent.putExtra("questionId", questionId);
            getActivity().startActivity(intent);
        });


        return view;
    }

    //刷新列表
    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        loadItems_Net();
        frv_detail.scrollToPosition(0);
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.loadData((List<BookDetailEntity.errorList>) message.obj);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;
            }else if(message.what == 101){
                adapter.loadData2((List<BookDetailEntity>) message.obj);
            }else {
                adapter.loadData3((List<BookDetailEntity.errorList>) message.obj);
            }
        }
    };


    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if(adapter.isRefresh == 1){
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        // 获取错题本信息
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_GET_QUESTION + "?userName=" + username + "&subjectId=" + coures_Id + "&currentPage=1";
        Log.e("本mRequestUrl",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                String itemString = "";
                String itemStringnew = "";
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONArray error = json.getJSONArray("data");
                item = json.getString("data");
                String error1 = json.getString("data");
                // 标题+所有题目表
                for(int j=0; j<error.length();j++){
                    String alitre0 = error.get(j).toString();
                    alitre0 = alitre0 + ",";
                    itemString += alitre0;
                    Log.e("jitemString","" + itemString);
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[","");
                    String itemre2 = itemre1.replace("]","");
                    if(j != error.length()-1){
                        itemre2 = itemre2 + ",";
                    }
                    itemString += itemre2;
                }
                // 所有题目表
                for(int j=0; j<error.length();j++){
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[","");
                    String itemre2 = itemre1.replace("]","");
                    if(j != error.length()-1){
                        itemre2 = itemre2 + ",";
                    }
                    itemStringnew += itemre2;
                }

                Log.e("itemString",""+itemString);
                itemString = "[" + itemString + "]";
                itemStringnew = "[" + itemStringnew + "]";
                Log.e("itemString",""+itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity.errorList> moreList =gson.fromJson(itemString, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());
                List<BookDetailEntity.errorList> quesList =gson.fromJson(itemStringnew, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());
                Log.e("moreList",""+moreList);
                String moreList1 = "[]";

                //封装消息，传递给主线程
                Message message1 = Message.obtain();
                message1.obj = moreList;

                Message message2 = Message.obtain();
                message2.obj = quesList;
                // 发送消息给主线程
                if(moreList == null){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }else if(itemString.equals(moreList1)){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }else{
                    fll_null.setVisibility(View.GONE);

                }
                //标识线程
                message1.what = 100;
                handler.sendMessage(message1);

                message2.what = 102;
                handler.sendMessage(message2);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
        queue.getCache().clear();

        request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                item = json.getString("data");

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity> almostList = gson.fromJson(item, new TypeToken<List<BookDetailEntity>>() {}.getType());
                Log.e("item",""+item);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = almostList;
                String moreList1 = "[]";
                if(item.equals(moreList1)){
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                }
                //标识线程
                message.what = 101;
                handler.sendMessage(message);
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);


        request = new StringRequest(mRequestUrl, response -> {
            try {
                String itemString = "";
                String itemStringnew = "";
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONArray error = json.getJSONArray("data");
                item = json.getString("data");
                String error1 = json.getString("data");

                // 所有题目表
                for(int j=0; j<error.length();j++){
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[","");
                    String itemre2 = itemre1.replace("]","");
                    if(j != error.length()-1){
                        itemre2 = itemre2 + ",";
                    }
                    itemStringnew += itemre2;
                }


                itemStringnew = "[" + itemStringnew + "]";

                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity.errorList> moreList =gson.fromJson(itemString, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());
                List<BookDetailEntity.errorList> quesList =gson.fromJson(itemStringnew, new TypeToken<List<BookDetailEntity.errorList>>() {}.getType());

                String moreList1 = "[]";

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = quesList;

                //标识线程
                message.what = 102;
                handler.sendMessage(message);

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        queue.add(request);
        queue.getCache().clear();
    }

    //慢加载
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()){
                refreshList();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()){
            refreshList();
        }
    }
}