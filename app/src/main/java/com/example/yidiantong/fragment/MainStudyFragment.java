package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.ui.HomeworkPagerFinishActivity;
import com.example.yidiantong.ui.LearnPlanPagerActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;
import com.example.yidiantong.util.MyReadWriteLock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainStudyFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainStudyFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window;
    private RecyclerView rv_study;
    private RelativeLayout rl_loading;
    private MyItemDecoration divider;

    //获得实例，并绑定参数
    public static MainStudyFragment newInstance() {
        MainStudyFragment fragment = new MainStudyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String type = "9";

    //列表数据
    private List<HomeItemEntity> itemList = new ArrayList<>();
    HomeRecyclerAdapter adapter;

    //搜索
    private EditText et_search;
    private String searchStr = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // -------------------
    //  checkIn点击后标记
    // -------------------
    private int checkInPos = -1;
    private String checkInType = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_study, container, false);

        //获取组件
        rv_study = view.findViewById(R.id.rv_study);

        //RecyclerView两步必要配置
        rv_study.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_study.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        if (divider == null) {
            divider = new MyItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, false);
            divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        }
        rv_study.addItemDecoration(divider);

        //获取登录传递的参数
        if (username == null) {
            username = MyApplication.username;
        }

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);

        //设置RecyclerViewAdapter
        if (adapter == null) {
            adapter = new HomeRecyclerAdapter(getContext(), itemList);

            //设置item点击事件
            adapter.setmItemClickListener((v, pos) -> {
                Intent intent = null;
                switch (adapter.itemList.get(pos).getType()) {
                    case "作业":
                        if (Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 2) {
                            intent = new Intent(getActivity(), HomeworkPagerFinishActivity.class);
                            intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                            intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                            intent.putExtra("username", username);
                            intent.putExtra("type", "paper");
                            intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                            startActivity(intent);
                        } else {
                            checkInPos = pos;
                            checkInType = "作业";
                            MyReadWriteLock.checkin(adapter.itemList.get(pos).getLearnId(), username, "student", "", handler, getActivity());
                        }
                        break;
                    case "导学案":
                    case "微课":
                        if (Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 2) {
                            intent = new Intent(getActivity(), HomeworkPagerFinishActivity.class);
                            intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                            intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                            intent.putExtra("username", username);
                            if (adapter.itemList.get(pos).getType().equals("导学案")) {
                                intent.putExtra("type", "learnPlan");
                            } else {
                                intent.putExtra("type", "weike");
                            }
                            intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                            startActivity(intent);
                        } else {
                            checkInPos = pos;
                            if (adapter.itemList.get(pos).getType().equals("导学案")) {
                                checkInType = "导学案";
                            } else {
                                checkInType = "微课";
                            }

                            MyReadWriteLock.checkin(adapter.itemList.get(pos).getLearnId(), username, "student", "", handler, getActivity());
                        }
                        break;
                }
            });
        } else {
            rl_loading.setVisibility(View.GONE);
        }

        rv_study.setAdapter(adapter);

        //弹出搜索栏菜单
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        //下拉刷新
        swipeRf = view.findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(() -> {
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        //下拉加载
        rv_study.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //记录当前可见的底部item序号
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == recyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 >= adapter.getItemCount()) {
                    loadItems_Net();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }
        });

        // 请求数据放后面
        if (adapter.itemList.size() == 0) {
            loadItems_Net();
        }

        //搜索栏优化-小键盘回车搜索
        view.findViewById(R.id.tv_search).setOnClickListener(this);
        et_search = view.findViewById(R.id.et_search);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    //先隐藏键盘
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    //其次再做相应操作
                    if (!searchStr.equals(et_search.getText().toString())) {
                        //做相应的操作
                        searchStr = et_search.getText().toString();
                        refreshList();
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        loadItems_Net();
        rv_study.scrollToPosition(0);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_select:
                if (contentView == null) {
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_search_select2, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_all).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_7).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, 20, -20);
                break;
            case R.id.tv_all:
                if (!type.equals("9")) {
                    type = "9";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if (!type.equals("1")) {
                    type = "1";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_2:
                if (!type.equals("2")) {
                    type = "2";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_7:
                if (!type.equals("7")) {
                    type = "7";
                    refreshList();
                }
                refreshList();
                window.dismiss();
                break;
            case R.id.tv_search:
                if (!searchStr.equals(et_search.getText().toString())) {
                    searchStr = et_search.getText().toString();
                    refreshList();
                }
                break;
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                adapter.fail = false;
                // 拿到的数据，可能为0，真0和假0（申请过快）
                List<HomeItemEntity> moreList = (List<HomeItemEntity>) message.obj;
                // 无论什么情况，都是打开进度条遮蔽的
                rl_loading.setVisibility(View.GONE);
                adapter.loadData(moreList);
                // 如果是非0，好办，就加载一下就完事了
                if (moreList.size() > 0) {
                    // 只有非0才翻页，0不算
                    currentPage += 1;
                }
            } else if (message.what == 110) {
                boolean canCheckIn = (Boolean) message.obj;
                if (!canCheckIn) {
                    Toast.makeText(getActivity(), "老师正在批改中，无法进行修改!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent;
                    int pos = checkInPos;

                    if (checkInType.equals("作业")) {

                        // 未批改的
                        intent = new Intent(getActivity(), HomeworkPagerActivity.class);

                        intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                        intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                        intent.putExtra("username", username);
                        intent.putExtra("type", "paper");
                        intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                        startActivity(intent);
                    } else {
                        // 未批改的
                        intent = new Intent(getActivity(), LearnPlanPagerActivity.class);

                        intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                        intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                        intent.putExtra("username", username);
                        if (checkInType.equals("导学案")) {
                            intent.putExtra("type", "learnPlan");
                        } else {
                            intent.putExtra("type", "weike");
                        }
                        intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                        startActivity(intent);
                    }
                }
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            rl_loading.setVisibility(View.VISIBLE);
        }

        String mRequestUrl = Constant.API + Constant.NEW_ITEM + "?currentPage=" + currentPage + "&userId=" + username + "&resourceType=" + type + "&searchStr=" + searchStr;

        Log.d("wen", "study2: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<HomeItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<HomeItemEntity>>() {
                }.getType());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                // 发送消息给主线程
                Log.d("wen", "一个请求数量（12为界限）：" + moreList.size());

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
            rl_loading.setVisibility(View.GONE);
            adapter.fail();
        });

        MyApplication.addRequest(request, TAG);
    }
}
