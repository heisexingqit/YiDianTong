package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.adapter.THomeworkStuRecyclerAdapter;
import com.example.yidiantong.adapter.TLearnPlanPreviewAdapter;
import com.example.yidiantong.bean.THomeworkStudentItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyItemDecoration;
import com.example.yidiantong.util.MyReadWriteLock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

public class THomeworkActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "THomeworkActivity";

    // 接口请求参数
    private String mRequestUrl;
    private String taskId;
    private String teacherId;
    private String type;
    private String status = "0";    // 批改状态，2未批改；4以批改，空或者0代表全部
    private String searchStr = "";
    private String mode = "2";      // 模式：1逐题批阅；2只显示需手工批阅试题
    private String homeworkName;

    // 页面组件
    private Button btn_report;
    private RecyclerView rv_home;
    private MyItemDecoration divider;
    private SwipeRefreshLayout swipeRf;
    private boolean isButtonClick = false;

    // RecycleViewAdapter相关
    private THomeworkStuRecyclerAdapter adapter;

    // PopupWindows相关
    private View contentView;
    private PopupWindow window;
    private View contentViewSetting;
    private PopupWindow windowSetting;
    private ClickableImageView iv_select;
    private ClickableImageView iv_setting;

    private TextView[] _tv = new TextView[3];

    private ActivityResultLauncher<Intent> mResultLauncher;

    // -------------------
    //  checkIn点击后标记
    // -------------------
    private int checkInPos = -1;
    private ClickableImageView iv_preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        setContentView(R.layout.activity_thomework);
        Intent intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        teacherId = intent.getStringExtra("teacherId");
        type = intent.getStringExtra("type");
        homeworkName = intent.getStringExtra("homeworkName");
        iv_preview = findViewById(R.id.iv_preview);
        iv_preview.setOnClickListener(view -> {
            Intent toPreview = null;
            if(type.equals("paper")){
                toPreview = new Intent(this, THomeworkPreviewActivity.class);
            }else{
                toPreview = new Intent(this, TLearnPlanPreviewActivity.class);
            }
            toPreview.putExtra("paperId", taskId);
            toPreview.putExtra("homeworkName", homeworkName);
            startActivity(toPreview);
        });

        // 组件获取
        btn_report = findViewById(R.id.btn_report);
        rv_home = findViewById(R.id.rv_home);

        // 返回按钮
        findViewById(R.id.iv_back).setOnClickListener(view -> finish());
        findViewById(R.id.btn_report).setOnClickListener(this);
        EditText et_search = findViewById(R.id.et_search);
        TextView tv_search = findViewById(R.id.tv_search);
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
        // 搜索框焦点监听器
        findViewById(R.id.ll_search).setOnClickListener(view -> et_search.setFocusable(true));

        iv_select = findViewById(R.id.iv_select);
        iv_setting = findViewById(R.id.iv_setting);
        iv_select.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        // RecyclerView两步必要配置
        rv_home.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_home.setItemAnimator(new DefaultItemAnimator());

        // 添加间隔线
        divider = new MyItemDecoration(this, DividerItemDecoration.VERTICAL, false);
        divider.setDrawable(getResources().getDrawable(R.drawable.divider_deep));
        rv_home.addItemDecoration(divider);

        // 设置RecyclerViewAdapter
        adapter = new THomeworkStuRecyclerAdapter(this, new ArrayList<>());
        rv_home.setAdapter(adapter);

        // 设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            // ----------------------
            // 需要读写锁
            // 判断对未批改部分进行判断
            // ----------------------
            if (adapter.itemList.get(pos).getStatus().equals("2")) {
                // 未批改
                checkInPos = pos;
                MyReadWriteLock.checkin(taskId, adapter.itemList.get(pos).getUserName(), "teacher", teacherId, handler, this);
            } else {
                // 已批改
                Intent intent2 = new Intent(this, THomeworkMarkPagerActivity.class);
                intent2.putExtra("item", adapter.itemList.get(pos));
                intent2.putExtra("stuName", adapter.itemList.get(pos).getUserName());
                intent2.putExtra("taskId", taskId);
                intent2.putExtra("name", adapter.itemList.get(pos).getUserCn());
                intent2.putExtra("stuScore", adapter.itemList.get(pos).getScore());
                intent2.putExtra("scoreCount", adapter.itemList.get(pos).getScoreCount());
                intent2.putExtra("canMark", !adapter.itemList.get(pos).getStatus().equals("5"));
                intent2.putExtra("type", type);
                intent2.putExtra("mode", mode);
                intent2.putExtra("homeworkName", homeworkName);
                mResultLauncher.launch(intent2);
            }
        });

        //下拉刷新
        swipeRf = findViewById(R.id.swipeRf);
        swipeRf.setOnRefreshListener(() -> {
            swipeRf.setRefreshing(true);
            refreshList();
            swipeRf.setRefreshing(false);
        });

        // 请求数据同步
        loadItems_Net();
        getMode();

        //搜索栏优化-小键盘回车搜索
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    //先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus()
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

        // 提交页面回调
        mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == HomeworkPagerActivity.RESULT_OK) {
                    refreshList();
                }
            }
        });
    }

    //刷新列表
    private void refreshList() {
        loadItems_Net();
        rv_home.scrollToPosition(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_report:
                if (isButtonClick) {
                    Intent intent = new Intent(this, THomeworkReportActivity.class);
                    intent.putExtra("username", teacherId);
                    intent.putExtra("taskId", taskId);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
                break;
            case R.id.iv_select:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_t_search_select3, null, false);
                    //绑定点击事件
                    contentView.findViewById(R.id.tv_marked).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_unmarked).setOnClickListener(this);
                    contentView.findViewById(R.id.tv_all_mark).setOnClickListener(this);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(iv_select, 20, -20);
                break;
            case R.id.iv_setting:
                if (contentViewSetting == null) {
                    contentViewSetting = LayoutInflater.from(this).inflate(R.layout.menu_t_setting_select, null, false);
                    //绑定点击事件
                    _tv[1] = contentViewSetting.findViewById(R.id.tv_all);
                    _tv[2] = contentViewSetting.findViewById(R.id.tv_manual);
                    _tv[1].setOnClickListener(this);
                    _tv[2].setOnClickListener(this);
                    if(mode.equals("2")){
                        _tv[2].setBackgroundResource(R.color.main_bg);
                        _tv[1].setBackgroundResource(R.color.white);
                    }else{
                        _tv[2].setBackgroundResource(R.color.white);
                        _tv[1].setBackgroundResource(R.color.main_bg);
                    }
                    // 初始化染色
                   // _tv[Integer.parseInt(mode)].setBackgroundResource(R.color.main_bg);
                   // _tv[Integer.parseInt(mode)].setTextColor(Color.WHITE);

                }

                windowSetting = new PopupWindow(contentViewSetting, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                windowSetting.setTouchable(true);

                windowSetting.showAsDropDown(iv_setting, -200, -20);
                break;
            case R.id.tv_marked:
                // PupupWindows按钮
                if (!status.equals("4")) {
                    status = "4";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_unmarked:
                // PupupWindows按钮
                if (!status.equals("2")) {
                    status = "2";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_all_mark:
                if (!status.equals("0")) {
                    status = "0";
                    refreshList();
                }
                window.dismiss();
                break;
            case R.id.tv_all:
                changeMode("1");
                windowSetting.dismiss();
                break;
            case R.id.tv_manual:
                changeMode("2");
                windowSetting.dismiss();
                break;


        }

    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                List<THomeworkStudentItemEntity> moreList = (List<THomeworkStudentItemEntity>) message.obj;
                if (moreList.size() != 0) {
                    btn_report.setTextColor(Color.WHITE);
                    btn_report.setBackgroundResource(R.drawable.t_homework_report);
                    isButtonClick = true;
                }
                adapter.loadData(moreList);
            } else if (message.what == 110) {
                boolean canCheckIn = (Boolean) message.obj;
                if (!canCheckIn) {
                    Toast.makeText(THomeworkActivity.this, "学生正在修改中，无法进行批改!", Toast.LENGTH_SHORT).show();
                } else {
                    int pos = checkInPos;
                    // 已批改
                    Intent intent2 = new Intent(THomeworkActivity.this, THomeworkMarkPagerActivity.class);
                    intent2.putExtra("item", adapter.itemList.get(pos));
                    intent2.putExtra("stuName", adapter.itemList.get(pos).getUserName());
                    intent2.putExtra("taskId", taskId);
                    intent2.putExtra("name", adapter.itemList.get(pos).getUserCn());
                    intent2.putExtra("stuScore", adapter.itemList.get(pos).getScore());
                    intent2.putExtra("scoreCount", adapter.itemList.get(pos).getScoreCount());
                    intent2.putExtra("canMark", !adapter.itemList.get(pos).getStatus().equals("5"));
                    intent2.putExtra("type", type);
                    intent2.putExtra("mode", mode);
                    intent2.putExtra("homeworkName", homeworkName);

                    mResultLauncher.launch(intent2);
                }
            }
        }
    };

    //加载消息条目，包括刷新和加载，通过upDown标识两种状态

    /**
     * 请求批改信息 将Handler内容写入，速度更快（如遇到报错，再用Handler）
     */
    private void loadItems_Net() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_STUDENT_LIST + "?taskId=" + taskId + "&teacherId="
                + teacherId + "&type=" + type + "&status=" + status + "&searchStr=" + searchStr;

        Log.e(TAG, "loadItems_Net: 请求学生列表");

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Log.e(TAG, "loadItems_Net: 请求学生列表" + itemString);

                Gson gson = new Gson();

                // 使用Goson框架转换Json字符串为列表
                List<THomeworkStudentItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<THomeworkStudentItemEntity>>() {
                }.getType());

                Log.d("wen", "学生列表: " + moreList.size());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                // 标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

            adapter.fail();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 获取模式
    private void getMode() {
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_MOLD + "?userName=" + teacherId + "&mode=" + mode;

        Log.d("wen", "获取模式: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                boolean isSuccess = json.getBoolean("success");
                String mod = json.getString("data");

                if (isSuccess) {
//                    // 封装消息，传递给主线程
//                    Message message = Message.obtain();
//
//                    // 携带数据
//                    message.obj = mode;
//
//                    // 标识线程
//                    message.what = 101;
//                    handler.sendMessage(message);
                    mode = mod;

                } else {
                    Toast.makeText(this, "模式获取失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.d("wen", "geteMode: " + e);
            }
        }, error -> {
            Log.d("wen", "geteMode: " + error.toString());
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // 修改模式
    private void changeMode(String tMode) {
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_CHANGE_MOLD + "?userName=" + teacherId + "&mode=" + tMode;

        Log.d("wen", "设置模式: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                boolean isSuccess = json.getBoolean("success");

                if (isSuccess) {
                    mode = tMode;
                    int last = Integer.parseInt(mode) % 2 + 1;
                    _tv[last].setBackgroundResource(0);
                    _tv[last].setTextColor(getColor(R.color.gray_text));
                    _tv[Integer.parseInt(mode)].setBackgroundResource(R.color.main_bg);
                    _tv[Integer.parseInt(mode)].setTextColor(Color.WHITE);

//                    // 封装消息，传递给主线程
//                    Message message = Message.obtain();
//
//                    // 携带数据
//                    message.obj = mode;
//
//                    // 标识线程
//                    message.what = 101;
//                    handler.sendMessage(message);
                    AlertDialog.Builder builder = new AlertDialog.Builder(THomeworkActivity.this);
                    if (mode.equals("2")) {
                        builder.setTitle("设置批改模式为:只显示需要手工批阅的试题");
                    } else {
                        builder.setTitle("设置批改模式为:显示全部试题");
                    }
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(this, "模式修改失败", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Log.d("wen", "changeMode: " + e);
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }
}