package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.View.LiveEnterDialog;
import com.example.yidiantong.adapter.TLiveRecyclerAdapter;
import com.example.yidiantong.bean.LiveItemEntity;
import com.example.yidiantong.bean.TLiveItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.navigationdemo.MainActivity_tea;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TLiveListActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TLiveListActivity";

    private EditText et_search;
    private ClickableTextView tv_search;
    private LinearLayout ll_selecor;
    private RecyclerView rv_main;

    private View contentView;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window;

    private String type = "0";
    private String mRequestUrl;
    private String searchStr = "";
    private int currentPage = 1;
    private TLiveRecyclerAdapter adapter;
    private RelativeLayout rl_loading;
    private TextView tv_selector;
    private LiveEnterDialog dialog;

    private Handler timeHandler;
    private Runnable refreshRunnable;
    private long refreshIntervalMillis = 20000; // 20秒
    private boolean shouldStopTimer = false; // 标记定时器启动和停止

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tlive_list);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        // 获取组件
        et_search = findViewById(R.id.et_search);
        findViewById(R.id.iv_add).setOnClickListener(this);
        tv_search = findViewById(R.id.tv_search);
        ll_selecor = findViewById(R.id.ll_selector);
        rv_main = findViewById(R.id.rv_main);
        ClickableImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> finish());
        ll_selecor.setOnClickListener(this);
        rl_loading = findViewById(R.id.rl_loading);
        // 搜索按钮点击
        tv_search.setOnClickListener(view -> {
            searchStr = et_search.getText().toString();
            refreshList();
        });
        // 搜索按钮隐藏显示
        et_search.setOnFocusChangeListener((view, b) -> {
            if (b) {
                tv_search.setVisibility(View.VISIBLE);
            } else {
                tv_search.setVisibility(View.GONE);
            }
        });
        tv_selector = findViewById(R.id.tv_selector);

        // RecyclerView两步必要配置
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());


        // 设置RecyclerViewAdapter
        adapter = new TLiveRecyclerAdapter(this, new ArrayList<>());

        // 设置item点击事件
        adapter.setmItemClickListener(new TLiveRecyclerAdapter.MyItemClickListener() {
            public void onItemClick(int pos) {

                // 设置Dialog
                dialog = new LiveEnterDialog(TLiveListActivity.this);
                // 设置Title
                dialog.setTitle(adapter.itemList.get(pos).getTitle());
                dialog.getWindow().setGravity(Gravity.CENTER);
                // 尺寸设置
                Window window = dialog.getWindow();
                // 获取屏幕宽度
                int screenWidth = getResources().getDisplayMetrics().widthPixels;

                // 计算宽度和边距
                int dialogWidth = (int) (screenWidth * 0.8); // 80% 的屏幕宽度

                // 设置对话框的宽度和边距
                ViewGroup.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = dialogWidth;
                window.setAttributes((WindowManager.LayoutParams) layoutParams);
                // 监听选项进行设置
                dialog.setMyInterface(new LiveEnterDialog.MyInterface() {
                    @Override
                    public void submit(boolean isCamera, boolean isMicro) {
                        Intent intent = new Intent(TLiveListActivity.this, MainActivity_tea.class);
                        TLiveItemEntity item = adapter.itemList.get(pos);
                        String roomName = item.getTitle();
                        String roomId = item.getRoomId();
                        String subjectId = item.getSubjectId();
                        String userSig = "null";
                        String ketangId = item.getKetangId();
                        String ketangName = item.getKetangName();
                        String userHead = MyApplication.picUrl;
                        String classAlreadtStartTime = item.getStartDate().getTime();
                        Log.d(TAG, "已开始时间: " + classAlreadtStartTime);
                        String params = MyApplication.username + "-@-" + MyApplication.cnName + "-@-" + roomId + "-@-" + roomName + "-@-" + subjectId + "-@-" + ketangId + "-@-" + ketangName + "-@-" + userHead + "-@-" + isCamera + "-@-" + isMicro + "-@-" + classAlreadtStartTime;
                        intent.putExtra("params", params);
                        // 关闭定时器
                        shouldStopTimer = true;
                        startActivity(intent);
                        // 关闭对话框
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void editLiveItem(int pos) {
                Intent editIntent = new Intent(TLiveListActivity.this, TLiveEditActivity.class);
                editIntent.putExtra("liveItem", adapter.itemList.get(pos));
                startActivity(editIntent);
            }

            @Override
            public void deleteLiveItem(int pos) {
                String mRequestUrl = Constant.API_LIVE + Constant.T_LIVE_DELETE + "?roomId=" + adapter.itemList.get(pos).getRoomId();
                Log.d("wen", "deleteLiveItem: " + mRequestUrl);
                StringRequest request = new StringRequest(mRequestUrl, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        AlertDialog.Builder builder = new AlertDialog.Builder(TLiveListActivity.this);
                        builder.setMessage("删除成功");
                        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                refreshList();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(TLiveListActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.d("wen", "Volley_Error: " + error.toString());
                });

                MyApplication.addRequest(request, TAG);
            }
        });

        rv_main.setAdapter(adapter);

        loadItems_Net();

        //下拉刷新
        swipeRf = findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(() -> {
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        // 上拉加载
        rv_main.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //记录当前可见的底部item序号
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 >= adapter.getItemCount() && adapter.isDown == 0) {
                    loadItems_Net();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert lm != null;
                lastVisibleItem = lm.findLastVisibleItemPosition();
            }
        });

        // 定时刷新
        timeHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (shouldStopTimer) {
                    timeHandler.removeCallbacks(this);
                } else {
                    refreshList();
                    // 重复调度下一次刷新
                    timeHandler.postDelayed(this, refreshIntervalMillis);
                }
            }
        };
    }

    //刷新列表
    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
        rv_main.scrollToPosition(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_selector:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_search_select3, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_0).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_3).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(ll_selecor, 0, 0);
                break;
            case R.id.tv_0:
                if (!type.equals("0")) {
                    type = "0";
                    tv_selector.setText("全部");
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if (!type.equals("1")) {
                    type = "1";
                    tv_selector.setText("直播中");
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_2:
                if (!type.equals("2")) {
                    type = "2";
                    tv_selector.setText("未开始");
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_3:
                if (!type.equals("3")) {
                    type = "3";
                    tv_selector.setText("已结束");
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.iv_add:
                startActivity(new Intent(this, TLiveAddActivity.class));
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
                List<TLiveItemEntity> moreList = (List<TLiveItemEntity>) message.obj;
                // 无论什么情况，都是打开进度条遮蔽的
                rl_loading.setVisibility(View.GONE);
                adapter.loadData(moreList);
                if (moreList.size() > 0) {
                    // 只有非0才翻页，0不算
                    currentPage += 1;
                }
                /**
                 * 假0判断移至adapter中，根据refresh一起判断
                 */
            }
        }
    };

    private void loadItems_Net() {

        if (adapter.isRefresh == 1) {
            rl_loading.setVisibility(View.VISIBLE);
        }

        mRequestUrl = Constant.API + Constant.T_LIVE_ITEM +
                "?currentPage=" + currentPage + "&userId=" + MyApplication.username + "&type=" + type + "&searchStr=" + searchStr + "&userCn=" + MyApplication.cnName + "&currentRole=1&userPhoto=" + MyApplication.picUrl;

        Log.d("wen", "教师直播课列表" + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("list");

                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<LiveItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<TLiveItemEntity>>() {
                }.getType());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                // 发送消息给主线程
                if (moreList.size() < 24 && moreList.size() > 0) {
                    adapter.isDown = 1;
                }
                Log.d(TAG, "总共" + moreList.size());

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
            rl_loading.setVisibility(View.GONE);
            adapter.fail();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    protected void onResume() {
        // 开启定时刷新
        shouldStopTimer = false;
        handler.postDelayed(refreshRunnable, refreshIntervalMillis);
        Log.d("wen", "onResume: 开启定时器");
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 在Activity不可见时停止定时刷新
        shouldStopTimer = true;
        Log.d("wen", "onPause: 关闭定时器");
        super.onPause();
    }
}