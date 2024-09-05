package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.adapter.PlaybackAdapter;
import com.example.yidiantong.bean.PlaybackEntity;
import com.example.yidiantong.bean.SchoolYearListEntity;
import com.example.yidiantong.bean.subjectEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlaybackActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private String schoolYearTermId;
    private String schoolYear;
    private PlaybackAdapter adapter;
    private RecyclerView rv_main;
    private int currentPage = 1;
    private SwipeRefreshLayout swipeRf;
    private Handler timeHandler;
    private Runnable refreshRunnable;
    private long refreshIntervalMillis = 20000; // 20秒
    private boolean shouldStopTimer = false; // 标记定时器启动和停止
    private RelativeLayout rl_loading;
    private TextView tv_schoolYearName;
    private ClickableTextView tv_selector;
    private LinearLayout ll_selector;
    private View contentView;
    private LiveEnterDialog dialog;
    private PopupWindow window;
    private PopupWindow window2;
    private List<String> subjectNameList = new ArrayList<>();
    private List<String> schoolYearList = new ArrayList<>();
    MyArrayAdapter  myArrayAdapter = new MyArrayAdapter(this,subjectNameList);
    MyArrayAdapter  myArrayAdapter2 = new MyArrayAdapter(this,schoolYearList);;
    private List<subjectEntity> subjectList;
    private String subjectId="0";
    private LinearLayout ll_start;
    private LinearLayout ll_end;
    private TextView tv_timeStart;
    private TextView tv_timeEnd;
    private String type;
    private TextView tv_clear;
    private String startTime;
    private String endTime;
    private View contentView2;
    private RelativeLayout rl_selector;
    private String keciID;
    private String bottomTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        rv_main = findViewById(R.id.rv_main);
        rl_loading = findViewById(R.id.rl_loading);
        ClickableImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> finish());
        tv_schoolYearName = findViewById(R.id.tv_schoolYearName);
        ll_selector = findViewById(R.id.ll_selector);
        ll_selector .setOnClickListener(this);
        tv_selector = findViewById(R.id.tv_selector);
        ll_start = findViewById(R.id.ll_start);
        ll_end = findViewById(R.id.ll_end);
        tv_timeStart = findViewById(R.id.tv_timeStart);
        tv_timeEnd = findViewById(R.id.tv_timeEnd);
        tv_clear = findViewById(R.id.tv_clear);
        rl_selector = findViewById(R.id.rl_selector);

        ll_start.setOnClickListener(this);
        ll_end.setOnClickListener(this);
        tv_clear.setOnClickListener(this);
        rl_selector.setOnClickListener(this);

        startTime="";
        endTime="";

        String data = this.getIntent().getStringExtra("data");
        //以_分割data
        String[] data_list = data.split("_");
        //学年信息
        schoolYear = data_list[0];
        tv_schoolYearName.setText(schoolYear);
        schoolYearTermId = data_list[1];

        adapter = new PlaybackAdapter(this, new ArrayList<>());
        // 设置item点击事件
        adapter.setmItemClickListener(new PlaybackAdapter.MyItemClickListener() {
            public void onItemClick(int pos) {

                Intent intent = new Intent(PlaybackActivity.this, HudongActivity.class);
                intent.putExtra("keciID", playbackEntityList.get(pos).getKeciId());
                intent.putExtra("stuId", MyApplication.username);
                intent.putExtra("topTitle", playbackEntityList.get(pos).getTopTitle());
                startActivity(intent);
            }

            @Override
            public void editLiveItem(int pos) {

            }
            @Override
            public void deleteLiveItem(int pos) {

            }
        });

        // RecyclerView两步必要配置
        rv_main.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_main.setItemAnimator(new DefaultItemAnimator());

        rv_main.setAdapter(adapter);
        loadItems_Net();
        getSubjectInfo();
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

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_selector:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView.findViewById(R.id.lv_homework);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 120);

                    lv_homework.setAdapter(myArrayAdapter);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //切换页+消除选项口
                            subjectId=subjectList.get(i).getSubjectId();
                            tv_selector.setText(subjectList.get(i).getSubjectName());
                            refreshList();
                            window.dismiss();
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        // 测量并设置ListView的宽度为最宽的列表项的宽度
                        int maxHeight = PxUtils.dip2px(PlaybackActivity.this, 245);
                        // 获取ListView的子项数目
                        int itemCount = lv_homework.getAdapter().getCount();

                        // 计算ListView的高度
                        int listViewHeight = 0;
                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lv_homework.getWidth(), View.MeasureSpec.AT_MOST);

                        for (int i = 0; i < itemCount; i++) {
                            View listItem = lv_homework.getAdapter().getView(i, null, lv_homework);
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                            listViewHeight += listItem.getMeasuredHeight();
                        }

                        // 如果计算出的高度超过最大高度，则设置为最大高度
                        ViewGroup.LayoutParams layoutParams = lv_homework.getLayoutParams();
                        if (listViewHeight > maxHeight) {
                            layoutParams.height = maxHeight;
                        }
                    });
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(ll_selector, 0, 20);
                break;
            case R.id.ll_start:
                Calendar calendar1 = Calendar.getInstance();
                int year = calendar1.get(Calendar.YEAR);
                int month =calendar1.get(Calendar.MONTH);
                int day =calendar1.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog_start = new DatePickerDialog(PlaybackActivity.this,this,year,month,day);
                type = "start";
                datePickerDialog_start.show();
                break;
            case R.id.ll_end:
                Calendar calendar2 = Calendar.getInstance();
                year = calendar2.get(Calendar.YEAR);
                month =calendar2.get(Calendar.MONTH);
                day =calendar2.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog_end = new DatePickerDialog(PlaybackActivity.this,this,year,month,day);
                type = "end";
                datePickerDialog_end.show();
                break;
            case R.id.tv_clear:
                tv_timeStart.setText("开始时间");
                tv_timeEnd.setText("结束日期");
                startTime="";
                endTime="";
                refreshList();
                break;
            case R.id.rl_selector:
                getSchoolYearTerm();
                break;

        }
    }

    //刷新列表
    private void refreshList() {
        currentPage = 1;
        adapter.isRefresh = 1;
        adapter.isDown = 0;
        loadItems_Net();
        rv_main.scrollToPosition(0);
    }

    //请求课堂回放记录
    public void loadItems_Net(){
        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_HUIFANG_RECORD+"&currentPage="+currentPage+"&userId="+ MyApplication.username+"&subjectId="+subjectId+"&schoolYearTermId="+schoolYearTermId+"&startTime="+startTime+"&endTime="+endTime;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                // 使用Goson框架转换Json字符串为列表
                Gson gson = new Gson();
                List<PlaybackEntity> moreList = gson.fromJson(itemString, new TypeToken<List<PlaybackEntity>>() {
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
            rl_loading.setVisibility(View.GONE);
            Log.e("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, "TAG");
    }

    private List<SchoolYearListEntity> schoolYearListEntities;
    private List<PlaybackEntity> playbackEntityList;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                // 拿到的数据，可能为0，真0和假0（申请过快）
                playbackEntityList = (List<PlaybackEntity>) message.obj;
                adapter.loadData(playbackEntityList);
                rl_loading.setVisibility(View.GONE);
                if (playbackEntityList.size() > 0) {
                    // 只有非0才翻页，0不算
                    currentPage += 1;
                }
            }else if(message.what == 101){
                subjectNameList.clear();
               subjectList = (List<subjectEntity>) message.obj;
               for(subjectEntity subjectEntity:subjectList){
                  subjectNameList.add(subjectEntity.getSubjectName());
               }
            }else if(message.what == 102){
                schoolYearList.clear();
                schoolYearListEntities = (List<SchoolYearListEntity>) message.obj;
                for (SchoolYearListEntity schoolYearListEntity:schoolYearListEntities){
                    schoolYearList.add(schoolYearListEntity.getSchoolYearName()+schoolYearListEntity.getSchoolTermName());
                }
                if (contentView2 == null) {
                    contentView2 = LayoutInflater.from(PlaybackActivity.this).inflate(R.layout.menu_homework, null, false);

                    ListView lv_homework = contentView2.findViewById(R.id.lv_homework);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(PlaybackActivity.this, 200);

                    lv_homework.setAdapter(myArrayAdapter2);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //切换页+消除选项口
                            tv_schoolYearName.setText(schoolYearList.get(i));
                            schoolYearTermId=schoolYearListEntities.get(i).getSchoolYearTermId().toString();
                            refreshList();
                            window2.dismiss();
                        }
                    });

                    /**
                     * 设置MaxHeight,先显示才能获取高度
                     */
                    lv_homework.post(() -> {
                        // 测量并设置ListView的宽度为最宽的列表项的宽度
                        int maxHeight = PxUtils.dip2px(PlaybackActivity.this, 245);
                        // 获取ListView的子项数目
                        int itemCount = lv_homework.getAdapter().getCount();

                        // 计算ListView的高度
                        int listViewHeight = 0;
                        int desiredWidth = View.MeasureSpec.makeMeasureSpec(lv_homework.getWidth(), View.MeasureSpec.AT_MOST);

                        for (int i = 0; i < itemCount; i++) {
                            View listItem = lv_homework.getAdapter().getView(i, null, lv_homework);
                            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                            listViewHeight += listItem.getMeasuredHeight();
                        }

                        // 如果计算出的高度超过最大高度，则设置为最大高度
                        ViewGroup.LayoutParams layoutParams = lv_homework.getLayoutParams();
                        if (listViewHeight > maxHeight) {
                            layoutParams.height = maxHeight;
                        }
                    });
                    window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window2.setTouchable(true);
                }

                window2.showAsDropDown( rl_selector, 0, 20);
            }
        }
    };
    //获取学科信息
    public void getSubjectInfo(){
        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_SUBJECT+"?unitId=1101010010001";
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                // 使用Goson框架转换Json字符串为列表
                Gson gson = new Gson();
                List<subjectEntity> moreList = gson.fromJson(itemString, new TypeToken<List<subjectEntity>>() {
                }.getType());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                //标识线程
                message.what = 101;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, "TAG");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //获取时间yyyy-MM-dd HH:mm:ss格式
        String desc =year+"."+(month+1)+"."+dayOfMonth;
        if ("start".equals(type)) {
            tv_timeStart.setText(desc);
            startTime = year + "-" + (month + 1) + "-" + dayOfMonth+" "+"00:00:00";
            if(!endTime.equals("")){
                refreshList();
            }
        } else if ("end".equals(type)) {
            endTime = year + "-" + (month + 1) + "-" + dayOfMonth+" "+"00:00:00";
            tv_timeEnd.setText(desc);
            if(!startTime.equals("")){
                refreshList();
            }
        }
    }
    //获取学年学期数据
    public void getSchoolYearTerm(){
        String mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_YEAR_TERM;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                // 使用Goson框架转换Json字符串为列表
                Gson gson = new Gson();
                List<SchoolYearListEntity> moreList = gson.fromJson(itemString, new TypeToken<List<SchoolYearListEntity>>() {
                }.getType());

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = moreList;

                //标识线程
                message.what = 102;
                handler.sendMessage(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "网络请求失败", Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, "TAG");
    }

}