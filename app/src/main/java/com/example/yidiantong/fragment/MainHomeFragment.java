package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.BuildConfig;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.HomeRecyclerAdapter;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.ui.HomeworkPagerFinishActivity;
import com.example.yidiantong.ui.LearnPlanPagerActivity;
import com.example.yidiantong.ui.LiveListActivity;
import com.example.yidiantong.ui.NoticeLookActivity;
import com.example.yidiantong.ui.PlaybackActivity;
import com.example.yidiantong.ui.ReadAloudLookActivity;
import com.example.yidiantong.ui.ResourceFolderActivity;
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

public class MainHomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MainHomeFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window;
    private RecyclerView rv_home;
    private RelativeLayout rl_loading;
    // 请求连接url
    private String mRequestUrl;
    private MyItemDecoration divider;
    private ImageView iv_folder_ball;
    private String bottomTitle;
    private String keciID;

    //获得实例，并绑定参数
    public static MainHomeFragment newInstance() {
        MainHomeFragment fragment = new MainHomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String type = "all";

    //列表数据 =》 统一整合到了RecyclerAdapter中，设置为public变量，内部维护
    //private List<HomeItemEntity> itemList = new ArrayList<>();
    HomeRecyclerAdapter adapter;

    //搜索
    private EditText et_search;
    private String searchStr = "";

    // -------------------
    //  checkIn点击后标记
    // -------------------
    private int checkInPos = -1;
    private String checkInType = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);
        //获取组件
        rv_home = view.findViewById(R.id.rv_home);

        //RecyclerView两步必要配置
        rv_home.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());

        //添加间隔线
        if (divider == null) {
            divider = new MyItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, false);
            divider.setDrawable(getActivity().getResources().getDrawable(R.drawable.divider_deep));
        }
        rv_home.addItemDecoration(divider);

        //获取登录传递的参数
        if (username == null) {
            username = MyApplication.username;
        }

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);

        //设置RecyclerViewAdapter
        if (adapter == null) {
            adapter = new HomeRecyclerAdapter(getContext(), new ArrayList<>());

            //设置item点击事件
            adapter.setmItemClickListener((v, pos) -> {
                Intent intent;
                switch (adapter.itemList.get(pos).getType()) {
                    case "作业":
                        if (Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 2) {
                            Log.e(TAG, "onCreateView: 改过的");
                            intent = new Intent(getActivity(), HomeworkPagerFinishActivity.class);
                            intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                            intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                            intent.putExtra("username", username);
                            intent.putExtra("type", "paper");
                            intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                            startActivity(intent);
                        } else {
                            Log.e("0110", "onCreateView: 没改的");
                            checkInPos = pos;
                            checkInType = "作业";
                            loadTaskStatus(pos);
                            //MyReadWriteLock.checkin(adapter.itemList.get(pos).getLearnId(), username, "student", "", handler, getActivity());
                        }
                        break;
                    case "导学案":
                    case "微课":
                        if (Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 2) {
                            intent = new Intent(getActivity(), HomeworkPagerFinishActivity.class);
                            intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                            intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                            intent.putExtra("username", username);
                            intent.putExtra("type", "learnPlan");
                            intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                            startActivity(intent);
                        } else {
                            checkInPos = pos;
                            if (adapter.itemList.get(pos).getType().equals("导学案")) {
                                checkInType = "导学案";
                            } else {
                                checkInType = "微课";
                            }
                            loadTaskStatus(pos);
                            //MyReadWriteLock.checkin(adapter.itemList.get(pos).getLearnId(), username, "student", "", handler, getActivity());
                        }
                        break;
                    case "直播课消息":
                        intent = new Intent(getActivity(), LiveListActivity.class);
                        intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                        startActivity(intent);
                        break;
                    case "通知":
                        Intent intent_t;
                       {
                            System.out.println("syq^_^:通知");
                            intent_t = new Intent(getActivity(), NoticeLookActivity.class);
                            intent_t.putExtra("noticetype", adapter.itemList.get(pos).getType());  // 类型
                            intent_t.putExtra("noticetime", adapter.itemList.get(pos).getTime());   // 发布时间
                            intent_t.putExtra("noticeAuthor", adapter.itemList.get(pos).getCreaterName());  // 创建者
                            intent_t.putExtra("noticeTitle", adapter.itemList.get(pos).getBottomTitle());     // 标题
                            intent_t.putExtra("noticecotent", adapter.itemList.get(pos).getCourseName()); // 内容
                        }
                        startActivity(intent_t);
                        reload(adapter.itemList.get(pos).getType(), adapter.itemList.get(pos).getLearnId());
                        adapter.itemList.get(pos).setStatus("4");
                        adapter.notifyDataSetChanged();
                        break;
                    case "公告":
                        Intent intent_g;
                        intent_g = new Intent(getActivity(), NoticeLookActivity.class);
                        intent_g.putExtra("noticetype", adapter.itemList.get(pos).getType());  // 类型
                        intent_g.putExtra("noticetime", adapter.itemList.get(pos).getTime());   // 发布时间
                        intent_g.putExtra("noticeAuthor", adapter.itemList.get(pos).getCreaterName());  // 创建者
                        intent_g.putExtra("noticeTitle", adapter.itemList.get(pos).getBottomTitle());     // 标题
                        intent_g.putExtra("noticecotent", adapter.itemList.get(pos).getCourseName()); // 内容
                        startActivity(intent_g);
                        reload(adapter.itemList.get(pos).getType(), adapter.itemList.get(pos).getLearnId()); //修改已读状态
                        adapter.itemList.get(pos).setStatus("4");
                        adapter.notifyDataSetChanged();
                        break;
//                    case "互动课堂":
//                        intent = new Intent(getActivity(), HudongActivity.class);
//                        intent.putExtra("keciID", adapter.itemList.get(pos).getLearnId());
//                        intent.putExtra("stuId", username);
//                        intent.putExtra("BottomTitle", adapter.itemList.get(pos).getBottomTitle());
//                        startActivity(intent);
//                        break;
                    case "课堂回放":
                        checkYearTerm();
                        break;
                    case "朗读":
                        intent = new Intent(getActivity(), ReadAloudLookActivity.class);
                        intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                        intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                        boolean isNew = true;
                        if(!adapter.itemList.get(pos).getStatus().equals("1") && !adapter.itemList.get(pos).getStatus().equals("5")){
                            isNew = false;
                        }
                        intent.putExtra("isNew", isNew);
                        startActivity(intent);

                        break;


                }
            });
        } else {
            rl_loading.setVisibility(View.GONE);
        }

        rv_home.setAdapter(adapter);

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

        //上拉加载
        rv_home.setOnScrollListener(new RecyclerView.OnScrollListener() {
            //记录当前可见的底部item序号
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 >= adapter.getItemCount()) {
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

        //慢加载，请求数据放后面
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

        // 资料夹
        view.findViewById(R.id.iv_folder).setOnClickListener(this);

        // 资料夹红点
        iv_folder_ball = view.findViewById(R.id.iv_folder_ball);
        iv_folder_ball.setVisibility(View.INVISIBLE);
        return view;
    }

    private final Handler handler2 = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                // 成功
            }
        }
    };

    // 修改已读状态
    private void reload(String type, String classTimeId) {
        int type_mode;
        if (type.equals("通知")) {
            type_mode = 3;
        } else {
            type_mode = 4;
        }
        String mRequestUrl = Constant.API + Constant.READ_NOTICE + "?userName=" + username + "&type=" + type_mode + "&classTimeId=" + classTimeId + "&callback=ha";
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

    //刷新列表
    public void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        loadItems_Net();
        Log.e("wen0531", "refreshList: 刷新成功");
        rv_home.scrollToPosition(0);
    }

    @SuppressLint({"NonConstantResourceId", "InflateParams"})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_select:
                if (contentView == null) {
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_search_select, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_all).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_3).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_4).setOnClickListener(this);
//                    contentView.findViewById(R.id.tv_11).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_7).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, 20, -20);
                break;
            case R.id.tv_all:
                if (!type.equals("all")) {
                    type = "all";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if (!type.equals("1")) {
                    type = "1";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_2:
                if (!type.equals("2")) {
                    type = "2";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_3:
                if (!type.equals("3")) {
                    type = "3";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_4:
                if (!type.equals("4")) {
                    type = "4";
                    refreshList();
                }
                window.dismiss();
                break;
//            case R.id.tv_11:
//                if (!type.equals("11")) {
//                    type = "11";
//                    refreshList();
//                }
//                window.dismiss();
//                break;
            case R.id.tv_7:
                if (!type.equals("7")) {
                    type = "7";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_search:
                if (!searchStr.equals(et_search.getText().toString())) {
                    searchStr = et_search.getText().toString();
                    refreshList();
                }
                break;
            case R.id.iv_folder:
                startActivity(new Intent(getActivity(), ResourceFolderActivity.class));
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
                if (moreList.size() > 0) {
                    // 只有非0才翻页，0不算
                    currentPage += 1;
                }
                /**
                 * 假0判断移至adapter中，根据refresh一起判断
                 */
            } else if (message.what == 101) {
                iv_folder_ball.setVisibility(View.VISIBLE);
            } else if (message.what == 110) {
                boolean canCheckIn = (Boolean) message.obj;
                Log.e("0110", "学生请求修改结果: " + canCheckIn);
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

            }else if(message.what == 102){
                int TaskStatus =(int) message.obj;
                int pos = checkInPos;
                if(TaskStatus==4){
                    Intent intent = new Intent(getActivity(), HomeworkPagerFinishActivity.class);
                    intent.putExtra("learnPlanId", adapter.itemList.get(pos).getLearnId());
                    intent.putExtra("title", adapter.itemList.get(pos).getBottomTitle());
                    intent.putExtra("username", username);
                    intent.putExtra("type", "paper");
                    intent.putExtra("isNew", Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 1 || Integer.parseInt(adapter.itemList.get(pos).getStatus()) == 5);
                    startActivity(intent);
                }else{
                    MyReadWriteLock.checkin(adapter.itemList.get(pos).getLearnId(), username, "student", "", handler, getActivity());
                }
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        Log.e(TAG, "loadItems_Net: 加载");

        if (adapter.isRefresh == 1) {
            rl_loading.setVisibility(View.VISIBLE);
        }
        // 获取本地app的版本名称
        String versionName = BuildConfig.VERSION_NAME;

        mRequestUrl = Constant.API + Constant.NEW_ITEM + "?currentPage=" + currentPage + "&userId=" + username + "&resourceType=" + type + "&searchStr=" + searchStr + "&source=RN" + "&unitId=1101010010001";
//        mRequestUrl = Constant.API + Constant.NEW_ITEM +"?userId="+ username+"&source=RN&currentPage=" + currentPage + "&resourceType=" + type+"&unitId=1101010010001";
        Log.e("wen", "home: " + mRequestUrl);

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


    private void loadIsRead() {
        iv_folder_ball.setVisibility(View.INVISIBLE);

        mRequestUrl = Constant.API + Constant.GET_RESOURCE_IS_READ + "?userId=" + MyApplication.username;

        Log.d("wen", "isRead: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                int unreadCount = json.getInt("data");
                Boolean isSuccess = json.getBoolean("success");
                if (unreadCount > 0 && isSuccess) {
                    Message msg = Message.obtain();
                    msg.what = 101;
                    handler.sendMessage(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
            rl_loading.setVisibility(View.GONE);
            adapter.fail();
        });
        MyApplication.addRequest(request, TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 小红点动态修改
        loadIsRead();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 在屏幕方向变化时，确保ViewPager的项位置不变
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 处理横向屏幕方向
//            MyApplication.isRotate = true;
            Log.e(TAG, "onConfigurationChanged: 横屏");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 处理纵向屏幕方向
//            MyApplication.isRotate = true;
            Log.e(TAG, "onConfigurationChanged: 竖屏");
        }
    }
    private void loadTaskStatus(int pos) {
        String type;
        if(adapter.itemList.get(pos).getType().equals("作业")){
            type ="paper";
        }else if(adapter.itemList.get(pos).getType().equals("导学案")){
            type ="learnPlan";
        }else {
            type ="weike";
        }


        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_STATUS + "?taskId=" +adapter.itemList.get(pos).getLearnId() +"&stuId="+MyApplication.username +"&type="+type;

        Log.d("wen", "isRead: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                int TaskStatus = json.getInt("data");
                Boolean isSuccess = json.getBoolean("success");
                if (TaskStatus>0&&isSuccess) {
                    // 封装消息，传递给主线程
                    Message message = Message.obtain();

                    // 携带数据
                    message.obj = TaskStatus;

                    //标识线程
                    message.what = 102;
                    handler.sendMessage(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
            rl_loading.setVisibility(View.GONE);
            adapter.fail();
        });
        MyApplication.addRequest(request, TAG);
    }
    //判断是否存在学年学期
    private void checkYearTerm() {
        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_HUIFANG;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String data = json.getString("data");
                Log.e("0124", "检查版本: " + json);
                if(data.equals("-1")){
                    Toast.makeText(getActivity(), "该学校未设置学年学期数据，无法查看课堂回放", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getActivity(), PlaybackActivity.class);
                intent.putExtra("data", data);
                intent.putExtra("keciID", keciID);
                intent.putExtra("BottomTitle", bottomTitle);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }
}