package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.ui.TBellLookNoticeActivity;
import com.example.yidiantong.ui.TBellNoticeSubmitActivity;
import com.example.yidiantong.ui.THomeworkAddActivity;
import com.example.yidiantong.ui.THomeworkActivity;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.THomeRecyclerAdapter;
import com.example.yidiantong.bean.THomeItemEntity;
import com.example.yidiantong.ui.THomeworkCameraAddActivity;
import com.example.yidiantong.ui.TLearnPlanAddActivity;
import com.example.yidiantong.ui.TLiveListActivity;
import com.example.yidiantong.ui.TPackageAddActivity;
import com.example.yidiantong.ui.TReadAloudAddActivity;
import com.example.yidiantong.ui.TWeikeAddActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 教师端首页最新Fragment
public class TMainLatestFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TMainLatestFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private View contentView2 = null;
    private SwipeRefreshLayout swipeRf;
    private PopupWindow window, window2;
    private RecyclerView rv_home;
    private RelativeLayout rl_loading;
    // 请求连接url
    private String mRequestUrl;
    private MyItemDecoration divider;
    private ClickableImageView iv_add;

    public static TMainLatestFragment newInstance() {
        TMainLatestFragment fragment = new TMainLatestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 0;
    private String username;
    private String type = "new";
    private String resourceType = "";

    //列表数据 =》 统一整合到了RecyclerAdapter中，设置为public变量，内部维护
    //private List<HomeItemEntity> itemList = new ArrayList<>();
    THomeRecyclerAdapter adapter;

    //搜索
    private EditText et_search;
    private String searchStr = "";

    // Activity页面切换
    private ChangePageInterface changePageInterface;

    public boolean isRefresh = false; // 回调刷新列表


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        changePageInterface = (ChangePageInterface) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_main_latest, container, false);



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
            adapter = new THomeRecyclerAdapter(getContext(), new ArrayList<>());

            //设置item点击事件
            adapter.setmItemClickListener((v, pos) -> {
                Intent intent = null;
                switch (adapter.itemList.get(pos).getfType()) {
                    case "7":
                    case "1":
                        intent = new Intent(getActivity(), THomeworkActivity.class);
                        intent.putExtra("teacherId", username);
                        intent.putExtra("taskId", adapter.itemList.get(pos).getfId());
                        if (adapter.itemList.get(pos).getfType().equals("7")) {
                            intent.putExtra("type", "weike");
                        } else {
                            intent.putExtra("type", "learnPlan");
                        }
                        intent.putExtra("homeworkName", adapter.itemList.get(pos).getfName());
                        startActivity(intent);
                        break;
                    case "2":
                        intent = new Intent(getActivity(), THomeworkActivity.class);
                        intent.putExtra("teacherId", username);
                        intent.putExtra("taskId", adapter.itemList.get(pos).getfId());
                        intent.putExtra("type", "paper");
                        intent.putExtra("homeworkName", adapter.itemList.get(pos).getfName());
                        startActivity(intent);
                        break;
                    case "3":
                        Intent intent1;
                        intent1 = new Intent(getActivity(), TBellLookNoticeActivity.class);
                        intent1.putExtra("classTimeId", adapter.itemList.get(pos).getfId());
                        intent1.putExtra("noticetype", adapter.itemList.get(pos).getfType());
                        intent1.putExtra("noticetime", adapter.itemList.get(pos).getfTime());
                        startActivity(intent1);
                        break;
                    case "4":
                        Intent intent2;
                        intent2 = new Intent(getActivity(), TBellLookNoticeActivity.class);
                        intent2.putExtra("classTimeId", adapter.itemList.get(pos).getfId());
                        intent2.putExtra("noticetype", adapter.itemList.get(pos).getfType());
                        intent2.putExtra("noticetime", adapter.itemList.get(pos).getfTime());
                        intent2.putExtra("isWithDraw", adapter.itemList.get(pos).getfNumber());
                        if(adapter.itemList.get(pos).getfFlag().equals("2")){
                            reload(adapter.itemList.get(pos).getfId());
                        }
                        startActivity(intent2);
                        break;
                    case "10":
                        intent = new Intent(getActivity(), TLiveListActivity.class);
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
        ClickableTextView tv_search = view.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(this);
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
                        tv_search.callOnClick();
                    }
                }
                return false;
            }
        });

        iv_add = view.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        return view;
    }

    //刷新列表
    private void refreshList() {
        currentPage = 0;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
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
                    contentView.findViewById(R.id.tv_7).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, 20, -20);
                break;
            case R.id.iv_add:
                if (contentView2 == null) {
                    contentView2 = LayoutInflater.from(getActivity()).inflate(R.layout.menu_t_home_add, null, false);
                    //绑定点击事件
                    contentView2.findViewById(R.id.tv_add_package).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_my_package).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_learnPlan).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_weike).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_homework).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_read_aloud).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_select_learnPlan).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_select_weike).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_select_paper).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_camera_homework).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_notice).setOnClickListener(this);
                    contentView2.findViewById(R.id.tv_add_announcement).setOnClickListener(this);
                    //非管理员隐藏发布公告
                    if(!MyApplication.typeName.equals("ADMIN_TEACHER")){
                        contentView2.findViewById(R.id.tv_add_announcement).setVisibility(View.GONE);
                    }

                    window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window2.setTouchable(true);
                }
                window2.showAsDropDown(iv_add, 20, -20);
                break;
            case R.id.tv_all:
                if (!resourceType.equals("")) {
                    resourceType = "";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_1:
                if (!resourceType.equals("1")) {
                    resourceType = "1";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_2:
                if (!resourceType.equals("2")) {
                    resourceType = "2";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_3:
                if (!resourceType.equals("3")) {
                    resourceType = "3";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_4:
                if (!resourceType.equals("4")) {
                    resourceType = "4";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_7:
                if (!resourceType.equals("7")) {
                    resourceType = "7";
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

            /**
             * 顶部右侧菜单
             */
            case R.id.tv_add_package:
                startActivity(new Intent(getActivity(), TPackageAddActivity.class));
                window2.dismiss();
                break;
            case R.id.tv_add_learnPlan:
                startActivity(new Intent(getActivity(), TLearnPlanAddActivity.class));
                window2.dismiss();
                break;
            case R.id.tv_add_weike:
                startActivity(new Intent(getActivity(), TWeikeAddActivity.class));
                window2.dismiss();
                break;
            case R.id.tv_add_homework:
                startActivity(new Intent(getActivity(), THomeworkAddActivity.class));
                window2.dismiss();
                break;
            case R.id.tv_add_read_aloud:
                // 跳转到布置朗读任务页面
                startActivity(new Intent(getActivity(), TReadAloudAddActivity.class));
                window2.dismiss();  //关闭添加窗口
                break;
            case R.id.tv_my_package:
                window2.dismiss();
                changePageInterface.changePage(2, "package");
                break;
            case R.id.tv_select_learnPlan:
                window2.dismiss();
                changePageInterface.changePage(2, "learnPlan");
                break;
            case R.id.tv_select_weike:
                window2.dismiss();
                changePageInterface.changePage(2, "weike");
                break;
            case R.id.tv_select_paper:
                window2.dismiss();
                changePageInterface.changePage(2, "paper");
                break;
            case R.id.tv_camera_homework:
                startActivity(new Intent(getActivity(), THomeworkCameraAddActivity.class));
                window2.dismiss();
                break;
            case R.id.tv_add_notice:
                Intent intent;
                intent = new Intent(getActivity(), TBellNoticeSubmitActivity.class);
                intent.putExtra("type", "通知");
                startActivity(intent);
                window2.dismiss();
                break;
            case R.id.tv_add_announcement:
                Intent intent1;
                intent1 = new Intent(getActivity(), TBellNoticeSubmitActivity.class);
                intent1.putExtra("type", "公告");
                startActivity(intent1);
                window2.dismiss();
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
                List<THomeItemEntity> moreList = (List<THomeItemEntity>) message.obj;
                // 无论什么情况，都是打开进度条遮蔽的
                rl_loading.setVisibility(View.GONE);
                // 如果是非0，好办，就加载一下就完事了
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

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            rl_loading.setVisibility(View.VISIBLE);
        }

        mRequestUrl = Constant.API + Constant.T_NEW_ITEM + "?userID=" + username + "&resourceType=" + resourceType + "&currentPage=" + currentPage + "&type=" + type + "&searchStr=" + searchStr + "&source=RN";

        Log.d("wen", "教师主页: " + mRequestUrl);
        Log.e(TAG, "loadItems_Net: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getJSONObject("data").getString("list");
                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<THomeItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<THomeItemEntity>>() {
                }.getType());

                Log.d("wen", "主页条目: " + moreList.size());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                // 发送消息给主线程
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

    public interface ChangePageInterface {
        void changePage(int position, String type);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            refreshList();
            isRefresh = false;
        }
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
    private void reload(String classTimeId) {
        int type_mode;
        if (type.equals("通知")) {
            type_mode = 3;
        } else {
            type_mode = 4;
        }
        String mRequestUrl = Constant.API + Constant.READ_NOTICE + "?userName=" + username + "&type=" + 4 + "&classTimeId=" + classTimeId + "&callback=ha";
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
}
