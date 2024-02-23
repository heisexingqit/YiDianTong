package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BooksDetailAdapter;
import com.example.yidiantong.bean.BookDetailEntity;
import com.example.yidiantong.ui.BookRecoverActivity;
import com.example.yidiantong.ui.BookRecyclerActivity;
import com.example.yidiantong.ui.BookSelectorActivity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.ui.MainPagerActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
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

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String sourceId = "";

    private ActivityResultLauncher<Intent> mResultLauncher;

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
        username = getActivity().getIntent().getStringExtra("username");
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
            int newPos = adapter.quenum[pos];
            BookDetailEntity.errorList item = adapter.errorList.get(newPos);
            questionId = item.getQuestionId();
            intent.putExtra("questionId", questionId);
            intent.putExtra("subjectId", coures_Id);
            intent.putExtra("username", username);
            intent.putExtra("name", course_name);
            intent.putExtra("sourceId", item.getSourceId());
            pos = pos + 1;
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


        // 筛选器部分
        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    Intent intent = result.getData();
                    selectorRefresh(intent.getStringExtra("sourceId"));
                }
            }
        });

        view.findViewById(R.id.fiv_selector).setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), BookSelectorActivity.class);
            intent.putExtra("subjectId", coures_Id);
            intent.putExtra("subjectName", course_name);
            mResultLauncher.launch(intent);
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
                Bundle receivedBundle = (Bundle) message.obj;
                List<BookDetailEntity.errorList> moreList = (List<BookDetailEntity.errorList>) receivedBundle.getSerializable("moreList");
                List<BookDetailEntity.errorList> quesList = (List<BookDetailEntity.errorList>) receivedBundle.getSerializable("quesList");
                List<BookDetailEntity> almostList = (List<BookDetailEntity>) receivedBundle.getSerializable("almostList");


                adapter.loadData(moreList);
                adapter.loadData3(quesList);
                adapter.loadData2(almostList);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        // 获取错题本信息
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_GET_QUESTION + "?userName=" + username + "&subjectId=" + coures_Id + "&currentPage=1&sourceId=" + sourceId;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                String itemString = "";
                String itemStringnew = "";
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONArray error = json.getJSONArray("data"); // 核心数据
                String item = json.getString("data");

                // 标题+所有题目表 【用于UI渲染】
                for (int j = 0; j < error.length(); j++) {
                    Log.e("wen0223", "sourceId: " + error.getJSONObject(j).getString("sourceId"));
                    String alitre0 = error.get(j).toString();
                    alitre0 = alitre0 + ",";
                    itemString += alitre0;

                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[", "");
                    String itemre2 = itemre1.replace("]", "");
                    if (j != error.length() - 1) {
                        itemre2 = itemre2 + ",";
                    }
                    itemString += itemre2;
                }
                // 所有题目表 【用于接口请求】
                for (int j = 0; j < error.length(); j++) {
                    String itemre0 = error.getJSONObject(j).getString("list");
                    String itemre1 = itemre0.replace("[", "");
                    String itemre2 = itemre1.replace("]", "");
                    if (j != error.length() - 1) {
                        itemre2 = itemre2 + ",";
                    }
                    itemStringnew += itemre2;
                }

                itemString = "[" + itemString + "]";
                itemStringnew = "[" + itemStringnew + "]";

                Gson gson = new Gson();
                // ------------------------------#
                // moreList 包含标题，用于UI渲染
                // queList 仅含试题，用于接口请求的定位
                // ------------------------------#
                List<BookDetailEntity.errorList> moreList = gson.fromJson(itemString, new TypeToken<List<BookDetailEntity.errorList>>() {
                }.getType());

                List<BookDetailEntity.errorList> quesList = gson.fromJson(itemStringnew, new TypeToken<List<BookDetailEntity.errorList>>() {
                }.getType());

                //使用Goson框架转换Json字符串为列表
                List<BookDetailEntity> almostList = gson.fromJson(item, new TypeToken<List<BookDetailEntity>>() {
                }.getType());

                Bundle bundle = new Bundle();
                bundle.putSerializable("moreList", (Serializable) moreList);
                bundle.putSerializable("quesList", (Serializable) quesList);
                bundle.putSerializable("almostList", (Serializable) almostList);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = bundle;

                // 发送消息给主线程
                if (moreList == null || moreList.size() == 0 || itemString.equals("[]")) {
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    fll_null.setVisibility(View.GONE);
                }
                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });

        MyApplication.addRequest(request, TAG);

//        request = new StringRequest(mRequestUrl, response -> {
//
//            try {
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//                item = json.getString("data");
//
//                Gson gson = new Gson();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//        });

//        MyApplication.addRequest(request, TAG);

//        request = new StringRequest(mRequestUrl, response -> {
//            try {
//                String itemString = "";
//                String itemStringnew = "";
//                JSONObject json = JsonUtils.getJsonObjectFromString(response);
//                JSONArray error = json.getJSONArray("data");
//                item = json.getString("data");
//                String error1 = json.getString("data");
//
//                // 所有题目表
//                for (int j = 0; j < error.length(); j++) {
//                    String itemre0 = error.getJSONObject(j).getString("list");
//                    String itemre1 = itemre0.replace("[", "");
//                    String itemre2 = itemre1.replace("]", "");
//                    if (j != error.length() - 1) {
//                        itemre2 = itemre2 + ",";
//                    }
//                    itemStringnew += itemre2;
//                }
//
//
//                itemStringnew = "[" + itemStringnew + "]";
//
//                Gson gson = new Gson();
//                //使用Goson框架转换Json字符串为列表
//                List<BookDetailEntity.errorList> moreList = gson.fromJson(itemString, new TypeToken<List<BookDetailEntity.errorList>>() {
//                }.getType());
//                List<BookDetailEntity.errorList> quesList = gson.fromJson(itemStringnew, new TypeToken<List<BookDetailEntity.errorList>>() {
//                }.getType());
//
//                String moreList1 = "[]";
//
//                //封装消息，传递给主线程
//                Message message = Message.obtain();
//                message.obj = quesList;
//
//                //标识线程
//                message.what = 102;
//                handler.sendMessage(message);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }, error -> {
//            Toast.makeText(BookDetailFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
//        });
//        MyApplication.addRequest(request, TAG);

    }

    //慢加载
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()) {
                refreshList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            refreshList();
        }
    }

    private void selectorRefresh(String sourceId){
        this.sourceId = sourceId;
    }

}