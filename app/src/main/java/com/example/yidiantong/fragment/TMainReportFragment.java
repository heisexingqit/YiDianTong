package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TMainReportFragment extends Fragment implements View.OnClickListener {

    private TextView[] time = new TextView[3];
    private TextView[] tv_num = new TextView[3];
    private TextView tv_qu[] = new TextView[2];
    private TextView tv_hw[] = new TextView[2];
    private TextView tv_class[] = new TextView[4];

    private String todyDateString;

    //  学期信息类
    class Semester {
        public String startTime; // 起始时间
        public String endTime; // 结束时间

        public Semester(String startTime, String endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    private static final String TAG = "TMainReportFragment";

    private final int[] imgs = {R.drawable.t_teach_board, R.drawable.t_homework_board, R.drawable.t_marked_board};
    private View[] v = new View[3];
    private String username;
    private String userId;
    private TextView tv_num1;
    private TextView tv_num2;
    private TextView tv_num3;
    private TextView tv_num4;

    private MyArrayAdapter myArrayAdapter;
    private List<String> semesterList = new ArrayList<>();
    private Map<String, Semester> semesterMap = new HashMap<>();
    private View topTimeListView;
    private PopupWindow window;
    private TextView tv_top_time;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static TMainReportFragment newInstance() {
        TMainReportFragment fragment = new TMainReportFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_main_report, container, false);

        view.findViewById(R.id.back).setOnClickListener(v -> {
            getActivity().finish();
        });

        LinearLayout ll_parent = view.findViewById(R.id.ll_parent);

        username = MyApplication.username;
        userId = MyApplication.userId;

        /**
         * 三个版块块显示（后期可能用Fragment方式）
         */
        // 获取屏幕宽度
        int screenWidth = getScreenWidth();
        // 计算高度为宽度的1.2倍
        int height = (int) (screenWidth * 1.1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                height
        );
        /** 1课堂授课
         *
         */
        v[0] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        v[0].setLayoutParams(layoutParams);

        // 时间选择器
        time[0] = v[0].findViewById(R.id.tv_date);
        v[0].findViewById(R.id.iv_calendar).setOnClickListener(v -> {
            try {
                Date dt = dateFormat.parse(time[0].getText().toString());
                // 创建一个 Calendar 对象并将 Date 设置进去
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dt);
                // 刷新动作要在timePick方法内部
                timePickerShow(calendar, time[0], 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });


        // 主题色
        LinearLayout ll_color = v[0].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF47bb3e);
        // 标题
        TextView tv_title = v[0].findViewById(R.id.tv_title);
        tv_title.setText("课堂授课");
        // 图片
        ImageView iv = v[0].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[0]);
        // 描述
        TextView tv_times = v[0].findViewById(R.id.tv_times);
        tv_times.setText("有效课次:");
        GridLayout gl_content = v[0].findViewById(R.id.gl_content);

        // 数据Tv颜色
        tv_num[0] = v[0].findViewById(R.id.tv_times_num);
        tv_num[0].setTextColor(0xFF47bb3e);

        // 数据
        String content1[] = {"内容: 0", "批注: 0", "互动: 0", "板书: 0"};
        for (int i = 0; i < 4; ++i) {
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(i / 2, 1, 1.0f);
            params.rowSpec = GridLayout.spec(i % 2, 1, 1.0f);

            tv_class[i] = new TextView(getActivity());
            tv_class[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv_class[i].setTextColor(getResources().getColor(R.color.gray_new));
            tv_class[i].setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            tv_class[i].setPadding(0, 0, 18, 0);
            textViewFontChange(tv_class[i], content1[i], 4);
            gl_content.addView(tv_class[i], params);
        }
        ll_parent.addView(v[0]);

        /** 2布置作业
         *
         */
        v[1] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        v[1].setLayoutParams(layoutParams);

        // 时间选择器
        time[1] = v[1].findViewById(R.id.tv_date);
        v[1].findViewById(R.id.iv_calendar).setOnClickListener(v -> {
            // 将字符串时间解析为 Date 对象

            try {
                Date date = dateFormat.parse(time[1].getText().toString());
                // 创建一个 Calendar 对象并将 Date 设置进去
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // 刷新动作要在timePick方法内部
                timePickerShow(calendar, time[1], 1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        // 主题色
        ll_color = v[1].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF9518ba);
        // 标题
        tv_title = v[1].findViewById(R.id.tv_title);
        tv_title.setText("布置作业");
        // 图片
        iv = v[1].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[1]);
        // 描述
        tv_times = v[1].findViewById(R.id.tv_times);
        tv_times.setText("有效次数:");
        gl_content = v[1].findViewById(R.id.gl_content);

        // 数据Tv颜色
        tv_num[1] = v[1].findViewById(R.id.tv_times_num);
        tv_num[1].setTextColor(0xFF9518ba);
        textViewFontChange(tv_num[1], "0 / 0",  2);

        // 数据
        String content2[] = {"试题总数: 0", "平均题数: 0"};
        for (int i = 0; i < 2; ++i) {
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(0, 2, 1.0f);
            params.rowSpec = GridLayout.spec(i, 1, 1.0f);
            tv_hw[i] = new TextView(getActivity());
            tv_hw[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv_hw[i].setTextColor(getResources().getColor(R.color.gray_new));
            tv_hw[i].setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textViewFontChange(tv_hw[i], content2[i], 6);
            gl_content.addView(tv_hw[i], params);
        }
        ll_parent.addView(v[1]);


        /** 3批阅试题
         *
         */
        v[2] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        v[2].setLayoutParams(layoutParams);

        // 时间选择器
        time[2] = v[2].findViewById(R.id.tv_date);
        v[2].findViewById(R.id.iv_calendar).setOnClickListener(v -> {
            // 将字符串时间解析为 Date 对象
            try {
                Date date = dateFormat.parse(time[2].getText().toString());
                // 创建一个 Calendar 对象并将 Date 设置进去
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                // 刷新动作要在timePick方法内部
                timePickerShow(calendar, time[2], 2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        // 主题色
        ll_color = v[2].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF7c67f4);
        // 标题
        tv_title = v[2].findViewById(R.id.tv_title);
        tv_title.setText("批阅试题");
        // 图片
        iv = v[2].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[2]);
        // 描述
        tv_times = v[2].findViewById(R.id.tv_times);
        tv_times.setText("有效次数:");
        gl_content = v[2].findViewById(R.id.gl_content);

        // 数据Tv颜色
        tv_num[2] = v[2].findViewById(R.id.tv_times_num);
        tv_num[2].setTextColor(0xFF7c67f4);
        // 数据
        String content3[] = {"填空题: 0", "其他题: 0"};
        for (int i = 0; i < 2; ++i) {
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(0, 2, 1.0f);
            params.rowSpec = GridLayout.spec(i, 1, 1.0f);
            tv_qu[i] = new TextView(getActivity());
            tv_qu[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv_qu[i].setTextColor(getResources().getColor(R.color.gray_new));
            tv_qu[i].setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textViewFontChange(tv_qu[i], content3[i], 5);
            gl_content.addView(tv_qu[i], params);
        }
        ll_parent.addView(v[2]);


        // ---------------------#
        //  组件获取，要在load之前
        // ---------------------#
        tv_num1 = view.findViewById(R.id.tv_report_num1);
        tv_num2 = view.findViewById(R.id.tv_report_num2);
        tv_num3 = view.findViewById(R.id.tv_report_num3);
        tv_num4 = view.findViewById(R.id.tv_report_num4);

        // 顶部
        tv_top_time = view.findViewById(R.id.top_time);
        tv_top_time.setOnClickListener(v -> { // 顶部时间控件
            timeListShow();
        });
        // 默认时间
        // 获取当前日期
        Date currentDate = new Date();
        // 将日期转换为字符串
        todyDateString = dateFormat.format(currentDate);
        for (int i = 0; i < 3; ++i) {
            time[i].setText(todyDateString);
            int t = i;
            v[i].findViewById(R.id.tv_left).setOnClickListener(v -> {
                timeToDay(time[t], -1);
                switch (t) {
                    case 0:
                        loadClassInfo();
                        break;
                    case 1:
                        loadHWInfo();
                        break;
                    case 2:
                        loadQuInfo();
                        break;
                }
            });
            v[i].findViewById(R.id.tv_right).setOnClickListener(v -> {
                timeToDay(time[t], 1);
                switch (t) {
                    case 0:
                        loadClassInfo();
                        break;
                    case 1:
                        loadHWInfo();
                        break;
                    case 2:
                        loadQuInfo();
                        break;
                }
            });
        }


        // 数据加载 =》刷新
        loadSemester();
        loadTopInfo();
        loadClassInfo();
        loadHWInfo();
        loadQuInfo();

        // --------------------#
        //  刷新数据逻辑：
        //  1、请求数据 load
        //  2、数据显示 handler
        //  3、UI刷新方法 refresh
        // --------------------#


        return view;
    }

    private void timeToDay(TextView tv, int day) {
        try {

            if (day == 1 && tv.getText().toString().equals(todyDateString)) {
                Toast.makeText(getActivity(), "已经是最后一天", Toast.LENGTH_SHORT).show();
                return;
            }
            Date date = dateFormat.parse(tv.getText().toString());
            // 使用 Calendar 对象进行日期计算
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, day);

            // 获取上一天的日期并格式化为字符串
            Date previousDay = calendar.getTime();
            tv.setText(dateFormat.format(previousDay));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 处理UI刷新
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                // 更新顶部数据
                try {
                    refreshTop((JSONObject) message.obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.what == 101) {
                // 更新课堂数据
                try {
                    refreshClass((JSONObject) message.obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.what == 102) {
                // 更新作业数据
                try {
                    refreshHW((JSONObject) message.obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (message.what == 103) {
                // 更新批阅数据
                try {
                    refreshQu((JSONObject) message.obj);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
    };

    @Override
    public void onClick(View view) {

    }

    private void timeListShow() {
        if (topTimeListView == null) {
            // 初次创建，需要设置布局
            myArrayAdapter = new MyArrayAdapter(getActivity(), semesterList);
            topTimeListView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_homework, null, false);

            ListView lv_homework = topTimeListView.findViewById(R.id.lv_homework);
            // 设置视图宽度
            lv_homework.getLayoutParams().width = PxUtils.dip2px(getActivity(), 180);

            lv_homework.setAdapter(myArrayAdapter);
            lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // 顶部时间选择触发刷新
                    tv_top_time.setText(semesterList.get(i));
                    loadTopInfo();
                    window.dismiss();
                }
            });
            /**
             * 设置MaxHeight,先显示才能获取高度
             */
            lv_homework.post(() -> {
                int maxHeight = PxUtils.dip2px(getActivity(), 245);
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


            window = new PopupWindow(topTimeListView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            window.setTouchable(true);
        }

        window.showAsDropDown(tv_top_time, 5, 5);

    }

    public void timePickerShow(Calendar setDate, TextView tv, int pos) {
        Calendar endDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -2);

        // 日期选择器
        TimePickerView pvData = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String selectDate = dateFormat.format(date);
                if (date != null) {
                    tv.setText(selectDate);
                    switch (pos) {
                        case 0:
                            loadClassInfo();
                            break;
                        case 1:
                            loadHWInfo();
                            break;
                        case 2:
                            loadQuInfo();
                            break;
                    }

                }
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, endDate)
                .setSubmitText("完成")
                .setTitleText("日期选择")
                .setDate(setDate)
                .build();

        pvData.show();
    }

    // -------------------#
    //  四模块 数据加载逻辑
    // -------------------#

    // 静态数据，加载一次即可
    private void loadSemester() {
        Log.e("wenee", "loadSemester: " + userId);

        String mRequestUrl = Constant.API + Constant.T_REPORT_SEMESTER + "?unitId=1101010010001";
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Log.e("wenee", "loadSemester: " + json);


                JSONArray data = json.getJSONArray("data");

                semesterList.clear(); // 同步前先清空

                for (int i = 0; i < data.length(); ++i) {
                    JSONObject o = data.getJSONObject(i);
                    semesterList.add(o.getString("name"));
                    semesterMap.put(o.getString("name"), new Semester(o.getString("yearTermStartTime").split("\\.")[0], o.getString("yearTermEndTime").split("\\.")[0]));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    // 顶部数据请求
    private void loadTopInfo() {

        Semester semester = semesterMap.get(tv_top_time.getText().toString());
        String start = "";
        String end = "";
        if (semester != null) {
            start = semester.startTime;
            end = semester.endTime;
        }
        String mRequestUrl = Constant.API + Constant.T_REPORT_TOP + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e(TAG, "loadTopInfo: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
                Log.e("wenee", "loadTopInfo: " + data);
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadClassInfo() {
        String start = time[0].getText().toString() + " 00:00:00";
        String end = time[0].getText().toString() + " 23:59:59";

        String mRequestUrl = Constant.API + Constant.T_REPORT_CLASS + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e(TAG, "loadClassInfo: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
                Log.e("0108", "loadClassInfo: " + data);
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 101;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadHWInfo() {
        String start = time[1].getText().toString() + " 00:00:00";
        String end = time[1].getText().toString() + " 23:59:59";

        String mRequestUrl = Constant.API + Constant.T_REPORT_HW + "?userID=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e(TAG, "loadHWInfo: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
                Log.e("0108", "loadHWInfo: " + data);
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 102;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadQuInfo() {
        String start = time[2].getText().toString() + " 00:00:00";
        String end = time[2].getText().toString() + " 23:59:59";

        String mRequestUrl = Constant.API + Constant.T_REPORT_QU + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e(TAG, "loadQuInfo: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
                Log.e("0108", "loadQuInfo: " + data);
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 103;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    // -------------------#
    //  四模块UI更新逻辑
    // -------------------#

    // 顶部刷新UI
    private void refreshTop(JSONObject data) throws JSONException {
        tv_num1.setText(data.getString("ktNum"));
        tv_num2.setText(data.getString("hdNum"));
        tv_num3.setText(data.getString("zyNum"));
        tv_num4.setText(JsonUtils.clearString(data.getString("pgNum")));
    }

    // 课堂授课刷新UI
    private void refreshClass(JSONObject data) throws JSONException {
        String status = data.getString("status");
        BarChart bc_main = v[1].findViewById(R.id.bc_mian);
        LinearLayout ll_show = v[2].findViewById(R.id.ll_show);
        TextView tv_empty = v[2].findViewById(R.id.tv_empty);
        TableLayout tl_main = v[2].findViewById(R.id.tl_main);
        bc_main.setScaleEnabled(false);
        bc_main.setTouchEnabled(false);

        if (status.equals("no")) {
            ll_show.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            // 空数据
            textViewFontChange(tv_class[0], "内容: 0", 4);
            textViewFontChange(tv_class[1], "批注: 0", 4);
            textViewFontChange(tv_class[2], "互动: 0", 4);
            textViewFontChange(tv_class[3], "版本: 0", 4);
            tv_num[0].setText("0");
            return;
        } else {
            ll_show.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }

        // 绘制柱状图

        String x_data[] = {"提问", "随机", "抢答"};
        String y_data[] = {"5", "1", "3"};
        BarChart barChart = bc_main;
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 10)); // 第一个柱子的位置和高度
        barEntries.add(new BarEntry(1, 20)); // 第二个柱子的位置和高度
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Label 1");
        labels.add("Label 2");
        // 添加更多标签...

//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setValueFormatter(new MyValueFormatterX(labels));
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setGranularity(1f);
//
//        YAxis yAxis = barChart.getAxisLeft();
//        yAxis.setValueFormatter(new DefaultAxisValueFormatter(0)); // 设置 Y 轴为整数
//
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Label");
//        barDataSet.setColor(Color.GREEN);
//
//        BarData barData = new BarData(barDataSet);
//        barData.setValueFormatter(new DefaultValueFormatter(0)); // 设置标注为整数
//
//        barChart.setData(barData);
//        barChart.setFitBars(true);
//        barChart.animateY(2000); // 可选的动画效果


        // 绘制表格
        makeTableUI(data, tl_main);
    }

    // 布置作业刷新UI
    private void refreshHW(JSONObject data) throws JSONException {
        String status = data.getString("status");
        BarChart bc_main = v[1].findViewById(R.id.bc_mian);
        LinearLayout ll_show = v[2].findViewById(R.id.ll_show);
        TextView tv_empty = v[2].findViewById(R.id.tv_empty);
        TableLayout tl_main = v[2].findViewById(R.id.tl_main);
        bc_main.setScaleEnabled(false);
        bc_main.setTouchEnabled(false);

        if (status.equals("no")) {
            ll_show.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            // 空数据
            textViewFontChange(tv_hw[0], "试题总数: 0", 6);
            textViewFontChange(tv_hw[1], "平均题数: 0", 6);
            textViewFontChange(tv_num[1], "0 / 0", 2);
            return;
        } else {
            ll_show.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        // 直接数据
        textViewFontChange(tv_qu[0], "试题总数: " + data.getString("queSumNum"), 6);
        textViewFontChange(tv_qu[1], "平均题数: " + data.getString("queAvgNum"), 6);

        String effectiveKeciNum = data.getString("effectiveKeciNum");
        String sumKeciNum = data.getString("sumKeciNum");

        textViewFontChange(tv_num[1], effectiveKeciNum + " / " + sumKeciNum, effectiveKeciNum.length() + 1);
        // 绘图数据
        // 图1
        JSONArray Xlist = data.getJSONArray("classNameList");
        JSONArray Ylist = data.getJSONArray("X1List");

        // 图二
        Ylist = data.getJSONArray("X2List");

        // 表格
        makeTableUI(data, tl_main);

    }

    // 批阅试题刷新UI
    private void refreshQu(JSONObject data) throws JSONException {
        String status = data.getString("status");
        BarChart bc_main = v[2].findViewById(R.id.bc_mian);
        LinearLayout ll_show = v[2].findViewById(R.id.ll_show);
        TextView tv_empty = v[2].findViewById(R.id.tv_empty);
        TableLayout tl_main = v[2].findViewById(R.id.tl_main);
        bc_main.setScaleEnabled(false);
        bc_main.setTouchEnabled(false);

        // 没数据直接清空
        if (status.equals("no")) {
            ll_show.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            // 空数据
            textViewFontChange(tv_qu[0], "填空题: 0", 5);
            textViewFontChange(tv_qu[1], "其他题: 0", 5);
            tv_num[2].setText("0");
            return;
        } else {
            ll_show.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }
        // 直接数据
        textViewFontChange(tv_qu[0], "填空题: " + data.getString("tkNum"), 5);
        textViewFontChange(tv_qu[1], "其他题: " + data.getString("qtNum"), 5);

        tv_num[2].setText(data.getString("sumNum"));

        // 绘图数据
        JSONArray Xlist = data.getJSONArray("Xlist");
        JSONArray Ylist = data.getJSONArray("Ylist");

        /** 绘图
         *
         */
        BarChart chart = v[2].findViewById(R.id.bc_mian);
        // 默认配置 -----------------------------#
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setHighlightFullBarEnabled(false);
        // Y轴配置
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setValueFormatter(new MyValueFormatter());
        leftAxis.setGranularity(1);
        leftAxis.setAxisMinimum(0);
        chart.getAxisRight().setEnabled(false);
        // X轴配置
        XAxis xLabels = chart.getXAxis();
        xLabels.setDrawGridLines(false);
        xLabels.setGranularity(1);
        xLabels.setPosition(XAxis.XAxisPosition.BOTTOM);
        Log.e("0109", "refreshQu: " + Xlist.toString());
        xLabels.setValueFormatter(new MyValueFormatterX(Xlist));
        // legend配置
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(12f);
        // 默认配置 -----------------------------#

        // 我的数据
        List<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < Xlist.length(); ++i) {
            JSONArray dataT = Ylist.getJSONObject(0).getJSONArray("data");
            JSONArray dataQ = Ylist.getJSONObject(1).getJSONArray("data");
            values.add(new BarEntry(i, new float[]{dataQ.getInt(i), dataT.getInt(i)}));
        }

        BarDataSet set1;
        set1 = new BarDataSet(values, "");
        set1.setDrawIcons(false);
        set1.setColors(new ArrayList<>(Arrays.asList(Color.parseColor("#f8a45e"), Color.parseColor("#7db4e9"))));
        set1.setStackLabels(new String[]{"其他题", "填空题"});
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData dataB = new BarData(dataSets);
        dataB.setValueFormatter(new MyValueFormatter());
        dataB.setValueTextColor(Color.WHITE);
        dataB.setValueTextSize(20);

        chart.setData(dataB);
        chart.setFitBars(true);
        chart.invalidate();

        // 绘制表格
        makeTableUI(data, tl_main);
    }

    private void makeTableUI(JSONObject data, TableLayout tl_main) throws JSONException {
        /** 表格
         *
         */

        // 表格数据
        // 表头的标题
        JSONArray table_json = data.getJSONArray("tableHead");
        String[] headerTitles = new String[table_json.length()];
        // 将 JSON 数组转换为 Java 数组
        for (int i = 0; i < table_json.length(); ++i) {
            headerTitles[i] = table_json.getString(i);
        }

        table_json = data.getJSONArray("tableData");
        String[][] contentData = new String[table_json.length()][];

        for (int i = 0; i < table_json.length(); ++i) {
            JSONArray table_js = table_json.getJSONArray(i);
            String[] body_data = new String[table_js.length()];
            for (int j = 0; j < table_js.length(); ++j) {
                body_data[j] = table_js.getString(j);
            }
            contentData[i] = body_data;
        }

        tl_main.removeAllViews();

        // 添加表头
        Drawable divider = getResources().getDrawable(R.drawable.table_border_divider);

        TableRow headerRow = new TableRow(getActivity());
        headerRow.setDividerDrawable(divider);
        headerRow.setShowDividers(TableRow.SHOW_DIVIDER_MIDDLE);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        for (String title : headerTitles) {
            TextView headerTextView = new TextView(getActivity());
            headerTextView.setText(title);
            headerTextView.setTextColor(getResources().getColor(R.color.white));
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setBackgroundColor(Color.parseColor("#a5a5a5"));
            headerTextView.setPadding(5, 0, 5, 0);
            headerRow.addView(headerTextView);
        }

        tl_main.addView(headerRow);


        // 表体
        for (String[] row : contentData) {
            TableRow contentRow = new TableRow(getActivity());
            contentRow.setDividerDrawable(divider);
            contentRow.setShowDividers(TableRow.SHOW_DIVIDER_MIDDLE);
            contentRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (String cell : row) {
                TextView cellTextView = new TextView(getActivity());
                cellTextView.setText(cell);
                cellTextView.setGravity(Gravity.CENTER);
                cellTextView.setBackgroundResource(R.color.white);
                cellTextView.setPadding(5, 0, 5, 0);
                contentRow.addView(cellTextView);
            }

            tl_main.addView(contentRow);
        }
    }

    private void textViewFontChange(TextView textView, String originalText, int startId) {
        // 创建一个SpannableString
        SpannableString spannableString = new SpannableString(originalText);

        // 尺寸
        spannableString.setSpan(new AbsoluteSizeSpan(25, true), startId, originalText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        // 变色
        int color = ContextCompat.getColor(getActivity(), R.color.gray_new);
        spannableString.setSpan(new ForegroundColorSpan(color), startId, originalText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        // 粗体
//        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startId, originalText.length(), SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);

        // 将SpannableString应用到TextView
        textView.setText(spannableString);
    }

    private class MyValueFormatterX extends ValueFormatter {
        JSONArray xList;

        public MyValueFormatterX(JSONArray xList) {
            this.xList = xList;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            String xValue = "";
            try {
                xValue = xList.getString((int) value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return xValue;
        }
    }

    private class MyValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.valueOf((int) value);
        }

        @Override
        public String getFormattedValue(float value) {
            if ((int) value == 0) {
                return "";
            } else {
                return String.valueOf((int) value);
            }
        }
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }
}