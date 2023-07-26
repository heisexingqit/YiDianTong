package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.view.Gravity;
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

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.adapter.TTeachRecyclerAdapter;
import com.example.yidiantong.bean.TTeachItemEntity;
import com.example.yidiantong.ui.THomeworkAddActivity;
import com.example.yidiantong.ui.THomeworkAddCameraActivity;
import com.example.yidiantong.ui.TLearnPlanAddActivity;
import com.example.yidiantong.ui.TPackageAddActivity;
import com.example.yidiantong.ui.TTeachAssginActivity;
import com.example.yidiantong.ui.TTeachEditActivity;
import com.example.yidiantong.ui.TTeachEditHomeworkActivity;
import com.example.yidiantong.ui.TTeachEditPackageActivity;
import com.example.yidiantong.ui.TWeikeAddActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.LoadingPageInterface;
import com.example.yidiantong.util.MyItemDecoration;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TMainTeachFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TMainLatestFragment";
    private ImageView iv_search_select;
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private View addMenuView = null;
    private PopupWindow addMenuWindow;
    private PopupWindow window;
    private RecyclerView rv_home;
    private RelativeLayout rl_loading;
    // 请求连接url
    private String mRequestUrl;
    private MyItemDecoration divider;
    private ClickableImageView iv_add;

    public static TMainTeachFragment newInstance() {
        TMainTeachFragment fragment = new TMainTeachFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    //请求数据参数
    private int currentPage = 1;
    private String username;
    private String type = "all";
//    private String token;

    //列表数据 =》 统一整合到了RecyclerAdapter中，设置为public变量，内部维护
    //private List<HomeItemEntity> itemList = new ArrayList<>();
    TTeachRecyclerAdapter adapter;

    //搜索
    private EditText et_search;
    private String searchStr = "";

    private LoadingPageInterface loadingPageInterface;

    // 参数修改弹窗
    private View changeParamView = null;
    private PopupWindow changeParamWindow;
    private EditText et_introduce;
    private EditText et_goal;
    private EditText et_study_key;
    private EditText et_study_difficulty;
    private EditText et_conclusion;
    private EditText et_expansion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadingPageInterface = (LoadingPageInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_main_teach, container, false);

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

//        if(token == null){
//            token = getActivity().getIntent().getStringExtra("token");
//        }

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);

        //设置RecyclerViewAdapter
        if (adapter == null) {
            adapter = new TTeachRecyclerAdapter(getContext(), new ArrayList<>());
        } else {
            rl_loading.setVisibility(View.GONE);
        }

        rv_home.setAdapter(adapter);

        //弹出搜索栏菜单
        iv_search_select = view.findViewById(R.id.iv_search_select);
        iv_search_select.setOnClickListener(this);

        // 创建选项菜单
        iv_add = view.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);

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
                        tv_search.callOnClick();
                    }
                }
                return false;
            }
        });

        // Item点击事件
        adapter.setmItemClickListener(new TTeachRecyclerAdapter.MyItemClickListener() {
            @Override
            public void onItemClickAssign(View view, int pos) {
                TTeachItemEntity item = adapter.itemList.get(pos);
                Intent intent = new Intent(getActivity(), TTeachAssginActivity.class);
                intent.putExtra("learnPlanId", item.getId());
                intent.putExtra("learnPlanName", item.getName());

                if (item.getIconUrl().contains("learnPlan")) {
                    intent.putExtra("type", "1");
                } else if (item.getIconUrl().contains("weike")) {
                    intent.putExtra("type", "2");
                } else if (item.getIconUrl().contains("learnPack")) {
                    intent.putExtra("type", "3");
                } else if (item.getIconUrl().contains("paper")) {
                    intent.putExtra("type", "paper");
                }
                startActivity(intent);
            }

            @Override
            public void onItemClickEdit(View view, int pos) {
                TTeachItemEntity item = adapter.itemList.get(pos);
                Intent intent = null;

                if (item.getIconUrl().contains("paper")) {
                    intent = new Intent(getActivity(), TTeachEditHomeworkActivity.class);
                } else if (item.getIconUrl().contains("learnPlan")) {
                    intent = new Intent(getActivity(), TTeachEditActivity.class);
                    intent.putExtra("type", "1");
                } else if (item.getIconUrl().contains("weike")) {
                    intent = new Intent(getActivity(), TTeachEditActivity.class);
                    intent.putExtra("type", "2");
                } else {
                    intent = new Intent(getActivity(), TTeachEditPackageActivity.class);
                }

                intent.putExtra("learnPlanId", item.getId());
                intent.putExtra("learnPlanName", item.getName());
                intent.putExtra("xueduan", item.getChannel());
                intent.putExtra("xueduanCode", item.getChannelCode());
                intent.putExtra("xueke", item.getSubject());
                intent.putExtra("xuekeCode", item.getSubjectId());
                intent.putExtra("jiaocaiCode", item.getGradeBook());
                intent.putExtra("jiaocai", item.getGradeBookCode());
                intent.putExtra("banben", item.getTextBook());
                intent.putExtra("banbenCode", item.getTextBookId());
                String zhishidian = item.getKnowledge();

                if (item.getIconUrl().contains("paper")) {
                    intent.putExtra("jiaocai", item.getGradeBook());
                    intent.putExtra("jiaocaiCode", item.getGradeBookCode());
                }

                int lastIndex = zhishidian.lastIndexOf("/");
                int secondLastIndex = -1;
                if (lastIndex != -1) {
                    secondLastIndex = zhishidian.lastIndexOf("/", lastIndex - 1);
                }
                if (secondLastIndex != -1) {
                    zhishidian = zhishidian.substring(secondLastIndex + 1, lastIndex);
                }

                intent.putExtra("zhishidian", zhishidian);
                intent.putExtra("zhishidianId", item.getKnowledgeCode());

                startActivity(intent);
            }

            // 删除选项
            @Override
            public void onItemClickDelete(View view, int pos) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TTeachItemEntity item = adapter.itemList.get(pos);
                        if (item.getIconUrl().contains("paper")) {
                            mRequestUrl = Constant.API + Constant.T_DELETE_PAPER + "?paperId=" + item.getId();
                        } else {
                            mRequestUrl = Constant.API + Constant.T_DELETE_LEARN_PLAN + "?learnPlanId=" + item.getId();
                        }
                        Log.d("wen", "删除URL: " + mRequestUrl);
                        StringRequest request = new StringRequest(mRequestUrl, response -> {

                            try {
                                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                                Log.d("wen", "删除返回信息: " + json);
                                boolean success = json.getBoolean("success");
                                if (success) {
                                    // 封装消息，传递给主线程
                                    Message message = Message.obtain();

                                    //标识线程
                                    message.what = 101;
                                    handler.sendMessage(message);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }, error -> {
                            Log.d("wen", "Volley_Error: " + error.toString());
                        });

                        MyApplication.addRequest(request, TAG);
                        loadingPageInterface.onLoading("删除成功", R.color.progress_green);

                    }
                }).setNegativeButton("取消", null).setMessage("是否确认删除");

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
            }

            @Override
            public void onItemClickParams(View view, int pos) {
                TTeachItemEntity item = adapter.itemList.get(pos);

                if (changeParamView == null) {
                    changeParamView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_t_main_teach_params, null, false);

                    //绑定点击事件
                    et_introduce = changeParamView.findViewById(R.id.et_introduce);
                    et_goal = changeParamView.findViewById(R.id.et_goal);
                    et_study_key = changeParamView.findViewById(R.id.et_study_key);
                    et_study_difficulty = changeParamView.findViewById(R.id.et_study_difficulty);
                    et_conclusion = changeParamView.findViewById(R.id.et_conclusion);
                    et_expansion = changeParamView.findViewById(R.id.et_expansion);
                    changeParamView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changeParamWindow.dismiss();
                        }
                    });

                    changeParamView.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // 保存属性

                            String Goal = et_goal.getText().toString();
                            String Emphasis = et_study_key.getText().toString();
                            String Difficulty = et_study_difficulty.getText().toString();
                            String Summary = et_conclusion.getText().toString();
                            String Extension = et_expansion.getText().toString();
                            String introduction = et_introduce.getText().toString();

                            // 转义
                            try {
                                Goal = URLEncoder.encode(Goal, "UTF-8");
                                Emphasis = URLEncoder.encode(Emphasis, "UTF-8");
                                Difficulty = URLEncoder.encode(Difficulty, "UTF-8");
                                Summary = URLEncoder.encode(Summary, "UTF-8");
                                Extension = URLEncoder.encode(Extension, "UTF-8");
                                introduction = URLEncoder.encode(introduction, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            // 编辑页面确认按钮
                            mRequestUrl = Constant.API + Constant.T_MAIN_TEACH_PARAM_SAVE + "?learnPlanId=" + item.getId() +
                                    "&teacherId=" + username + "&Goal=" + Goal + "&Emphasis=" + Emphasis + "&Difficulty=" + Difficulty +
                                    "&Summary=" + Summary + "&Extension=" + Extension + "&introduction=" + introduction;

                            Log.d("wen", "属性保存URL: " + mRequestUrl);
                            StringRequest request = new StringRequest(mRequestUrl, response -> {
                                JSONObject json = null;
                                try {
                                    json = JsonUtils.getJsonObjectFromString(response);
                                    boolean success = json.getBoolean("success");
                                    if (success) {
                                        // 封装消息，传递给主线程
                                        Message message = Message.obtain();

                                        //标识线程
                                        message.what = 102;
                                        handler.sendMessage(message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }, error -> {
                                Log.d("wen", "Volley_Error: " + error.toString());
                            });
                            MyApplication.addRequest(request, TAG);

                        }
                    });
                    changeParamWindow = new PopupWindow(changeParamView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    changeParamWindow.setTouchable(true);
                }

                // 读取属性
                mRequestUrl = Constant.API + Constant.T_MAIN_TEACH_PARAM_GET + "?learnPlanId=" + item.getId();

                Log.d("wen", "属性获取URL: " + mRequestUrl);
                StringRequest request = new StringRequest(mRequestUrl, response -> {

                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        JSONObject itemString = json.getJSONObject("data");
                        et_introduce.setText(itemString.getString("introduction"));
                        et_goal.setText(itemString.getString("goal"));
                        et_study_key.setText(itemString.getString("emphasis"));
                        et_study_difficulty.setText(itemString.getString("difficulty"));
                        et_conclusion.setText(itemString.getString("summary"));
                        et_expansion.setText(itemString.getString("extension"));
                        changeParamWindow.showAtLocation(rv_home, Gravity.CENTER, 0, 0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);

            }
        });
        return view;
    }

    //刷新列表
    private void refreshList() {
        currentPage = 1;
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
                    contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_t_main_teach_select, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_all).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_paper).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_learnPlan).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_package).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_weike).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_search_select, 20, -20);
                break;
            case R.id.iv_add:
                if (addMenuView == null) {
                    addMenuView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_t_main_teach_add, null, false);
                    //绑定点击事件
                    addMenuView.findViewById(R.id.tv_add_package).setOnClickListener(this);
                    addMenuView.findViewById(R.id.tv_add_learnPlan).setOnClickListener(this);
                    addMenuView.findViewById(R.id.tv_add_weike).setOnClickListener(this);
                    addMenuView.findViewById(R.id.tv_add_homework).setOnClickListener(this);
                    addMenuView.findViewById(R.id.tv_camera_homework).setOnClickListener(this);
                    addMenuWindow = new PopupWindow(addMenuView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    addMenuWindow.setTouchable(true);
                }
                addMenuWindow.showAsDropDown(iv_add, 20, -20);

                break;
            case R.id.tv_all:
                if (!type.equals("all")) {
                    type = "all";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_paper:
                if (!type.equals("paper")) {
                    type = "paper";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_learnPlan:
                if (!type.equals("learnPlan")) {
                    type = "learnPlan";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_package:
                if (!type.equals("package")) {
                    type = "package";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_weike:
                if (!type.equals("weike")) {
                    type = "weike";
                    refreshList();
                }
                window.dismiss();
                break;

            case R.id.tv_search:
                if (!searchStr.equals(et_search.getText().toString())) {
                    Log.d("wen", "搜索内容: " + searchStr);
                    searchStr = et_search.getText().toString();
                    refreshList();
                }
                break;
                /**
                 * 顶部右侧菜单
                 */
            case R.id.tv_add_package:
                startActivity(new Intent(getActivity(), TPackageAddActivity.class));
                addMenuWindow.dismiss();
                break;
            case R.id.tv_add_learnPlan:
                startActivity(new Intent(getActivity(), TLearnPlanAddActivity.class));
                addMenuWindow.dismiss();
                break;
            case R.id.tv_add_weike:
                startActivity(new Intent(getActivity(), TWeikeAddActivity.class));
                addMenuWindow.dismiss();
                break;
            case R.id.tv_add_homework:
                startActivity(new Intent(getActivity(), THomeworkAddActivity.class));
                addMenuWindow.dismiss();
                break;
            case R.id.tv_camera_homework:
                startActivity(new Intent(getActivity(), THomeworkAddCameraActivity.class));
                addMenuWindow.dismiss();
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
                List<TTeachItemEntity> moreList = (List<TTeachItemEntity>) message.obj;
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
            } else if (message.what == 101) {
                Log.d("wen", "handleMessage: 删除后刷新");
                loadingPageInterface.offLoading();
                // 重新刷新
                refreshList();
            } else if (message.what == 102) {
                changeParamWindow.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setNegativeButton("取消", null).setMessage("属性修改成功");
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            rl_loading.setVisibility(View.VISIBLE);
        }

        mRequestUrl = Constant.API + Constant.T_TEACH_ITEM + "?userId=" + username + "&currentPage=" + currentPage + "&type=" + type + "&searchStr=" + searchStr;

        Log.d("wen", "home: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<TTeachItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<TTeachItemEntity>>() {
                }.getType());
                Log.d("wen2", "loadItems_Net: moreList: " + moreList);

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                // 发送消息给主线程
                Log.d("wen", "一个请求数量（12为界限）：" + moreList.size());
                if (moreList.size() < 12 && moreList.size() > 0) {
                    adapter.isDown = 1;
                }

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

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
}