package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
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
import java.util.Collections;
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
    private ClickableImageView refresh;

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
        LinearLayout ll_parent = view.findViewById(R.id.ll_parent);

        username = MyApplication.username;
        userId = MyApplication.userId;

        /**
         * 三个版块块显示（后期可能用Fragment方式）
         */
        // 获取屏幕宽度
//        int screenWidth = getScreenWidth();
        // 计算高度为宽度的1.2倍
//        int height = (int) (screenWidth * 1.1);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                height
//        );
        /** 1课堂授课
         *
         */
        v[0] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
//        v[0].setLayoutParams(layoutParams);

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
//        v[1].setLayoutParams(layoutParams);

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
        textViewFontChange(tv_num[1], "0 / 0", 2);

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
//        v[2].setLayoutParams(layoutParams);

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
        tv_times.setText("批阅总数:");
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
        refresh = view.findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(v -> {
            loadSemester();
            loadClassInfo();
            loadHWInfo();
            loadQuInfo();
        });

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
            if(getActivity() == null){
                return;
            }
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
                    Log.e("wen0309", "handleMessage: " + e);
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
            lv_homework.getLayoutParams().width = PxUtils.dip2px(getActivity(), 200);

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
        String mRequestUrl = Constant.API + Constant.T_REPORT_SEMESTER + "?unitId=1101010010001";
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");

                semesterList.clear(); // 同步前先清空
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject o = data.getJSONObject(i);
                    semesterList.add(o.getString("name"));
                    semesterMap.put(o.getString("name"), new Semester(o.getString("yearTermStartTime").split("\\.")[0], o.getString("yearTermEndTime").split("\\.")[0]));
                }
                if (semesterList.size() > 0) {
                    tv_top_time.setText(semesterList.get(0));
                    loadTopInfo(); // 有时间数据后再请求顶部数据
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
//            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: loadSemester " + error.toString());
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
        Log.e("wen0307", "loadTopInfo: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                JSONObject data = json.getJSONObject("data");
//                Log.e("wen0307", "loadTopInfo: " + json);
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
//            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: loadTopInfo " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private void loadClassInfo() {
        String start = time[0].getText().toString() + " 00:00:00";
        // 将end时间改为start时间的下一天
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String end = sdf.format(calendar.getTime());

        String mRequestUrl = Constant.API + Constant.T_REPORT_CLASS + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String testString =
                        "{\n" +
                                "    \"status\": \"yes\",\n" +
                                "    \"hdNum\": 88,\n" +
                                "    \"xList\": [\n" +
                                "        \"提问\",\n" +
                                "        \"抢答\",\n" +
                                "        \"随机\",\n" +
                                "        \"连答\"\n" +
                                "    ],\n" +
                                "    \"contentNum\": 4,\n" +
                                "    \"effectiveKeci\": 3,\n" +
                                "    \"yList\": [\n" +
                                "        1,\n" +
                                "        2,\n" +
                                "        10,\n" +
                                "        3\n" +
                                "    ],\n" +
                                "    \"tableData\": [\n" +
                                "        [\n" +
                                "            \"1\",\n" +
                                "            \"2020级高二(1)班,\",\n" +
                                "            \"2021-12-28 11:20:26.0\",\n" +
                                "            \"1\",\n" +
                                "            \"3\",\n" +
                                "            \"8\",\n" +
                                "            \"3\",\n" +
                                "            \"1.0\"\n" +
                                "        ],\n" +
                                "        [\n" +
                                "            \"2\",\n" +
                                "            \"2020级高二(1)班,\",\n" +
                                "            \"2021-12-28 10:30:11.0\",\n" +
                                "            \"2\",\n" +
                                "            \"2\",\n" +
                                "            null,\n" +
                                "            \"4\",\n" +
                                "            \"1.0\"\n" +
                                "        ],\n" +
                                "        [\n" +
                                "            \"3\",\n" +
                                "            \"2020级高二(1)班,\",\n" +
                                "            \"2021-12-27 11:19:11.0\",\n" +
                                "            \"1\",\n" +
                                "            \"8\",\n" +
                                "            \"1\",\n" +
                                "            \"3\",\n" +
                                "            \"1.0\"\n" +
                                "        ]\n" +
                                "    ],\n" +
                                "    \"sumKeci\": 3,\n" +
                                "    \"tableHead\": [\n" +
                                "        \"序号\",\n" +
                                "        \"班级\",\n" +
                                "        \"时间\",\n" +
                                "        \"内容\",\n" +
                                "        \"批注\",\n" +
                                "        \"板书\",\n" +
                                "        \"互动\",\n" +
                                "        \"有效课次\"\n" +
                                "    ],\n" +
                                "    \"pzNum\": 13,\n" +
                                "    \"bsNum\": 9\n" +
                                "}";
//                JSONObject data = new JSONObject(testString);
                JSONObject data = json.getJSONObject("data");
                Log.e("wen0312", "loadClassInfo: " + data);

                // 将String转为JSONObject
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
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadHWInfo() {
        String start = time[1].getText().toString() + " 00:00:00";
        // 将end时间改为start时间的下一天
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String end = sdf.format(calendar.getTime());

        String mRequestUrl = Constant.API + Constant.T_REPORT_HW + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e("wen0308", "loadHWURL: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String testString =
                        "   {\n" +
                                "    \"effectiveKeciNum\": \"2.10\",\n" +
                                "    \"queSumNum\": 30,\n" +
                                "    \"queAvgNum\": 7,\n" +
                                "    \"sumKeciNum\": 4,\n" +
                                "    \"status\": \"yes\",\n" +
                                "    \"X1List\": [\n" +
                                "        {\n" +
                                "            \"data\": [\n" +
                                "                1,\n" +
                                "                3,\n" +
                                "                5,\n" +
                                "                7\n" +
                                "            ],\n" +
                                "            \"name\": \"提交率\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"data\": [\n" +
                                "                2,\n" +
                                "                4,\n" +
                                "                6,\n" +
                                "                8\n" +
                                "            ],\n" +
                                "            \"name\": \"批改率\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"X2List\": [\n" +
                                "        {\n" +
                                "            \"data\": [\n" +
                                "                30,\n" +
                                "                30,\n" +
                                "                104,\n" +
                                "                104\n" +
                                "            ],\n" +
                                "            \"name\": \"总分\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"data\": [\n" +
                                "                22.4,\n" +
                                "                27.5,\n" +
                                "                32.5,\n" +
                                "                88.5\n" +
                                "            ],\n" +
                                "            \"name\": \"平均分\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"data\": [\n" +
                                "                74.67,\n" +
                                "                91.67,\n" +
                                "                31.25,\n" +
                                "                85.1\n" +
                                "            ],\n" +
                                "            \"name\": \"得分率\"\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"tableData\": [\n" +
                                "        [\n" +
                                "            \"0\",\n" +
                                "            \"高二(4)班物理\",\n" +
                                "            \"2021-12-20 14:32\",\n" +
                                "            \"2021-12-21 23:59\",\n" +
                                "            \"5\",\n" +
                                "            \"88.10%\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"22.4/30.0\",\n" +
                                "            \"74.67%\",\n" +
                                "            \"0.70\"\n" +
                                "        ],\n" +
                                "        [\n" +
                                "            \"1\",\n" +
                                "            \"高二(1)班物理\",\n" +
                                "            \"2021-12-20 14:32\",\n" +
                                "            \"2021-12-21 23:59\",\n" +
                                "            \"5\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"27.5/30.0\",\n" +
                                "            \"91.67%\",\n" +
                                "            \"0.70\"\n" +
                                "        ],\n" +
                                "        [\n" +
                                "            \"2\",\n" +
                                "            \"高二(4)班物理\",\n" +
                                "            \"2021-12-16 16:30\",\n" +
                                "            \"2021-12-18 23:59\",\n" +
                                "            \"10\",\n" +
                                "            \"4.76%\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"32.5/104.0\",\n" +
                                "            \"31.25%\",\n" +
                                "            \"0.00\"\n" +
                                "        ],\n" +
                                "        [\n" +
                                "            \"3\",\n" +
                                "            \"高二(1)班物理\",\n" +
                                "            \"2021-12-16 16:30\",\n" +
                                "            \"2021-12-18 23:59\",\n" +
                                "            \"10\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"100.00%\",\n" +
                                "            \"88.5/104.0\",\n" +
                                "            \"85.10%\",\n" +
                                "            \"0.70\"\n" +
                                "        ]\n" +
                                "    ],\n" +
                                "    \"tableHead\": [\n" +
                                "        \"序号\",\n" +
                                "        \"班级\",\n" +
                                "        \"开始时间\",\n" +
                                "        \"结束时间\",\n" +
                                "        \"试题数量\",\n" +
                                "        \"提交率\",\n" +
                                "        \"批改率\",\n" +
                                "        \"平均分/总分\",\n" +
                                "        \"得分率\",\n" +
                                "        \"有效次数\"\n" +
                                "    ],\n" +
                                "    \"classNameList\": [\n" +
                                "        \"高二(1)班\",\n" +
                                "        \"高二(2)班\",\n" +
                                "        \"高二(3)班\",\n" +
                                "        \"高二(4)班\"\n" +
                                "    ]\n" +
                                "}";
//                JSONObject data = new JSONObject(testString);
                JSONObject data = json.getJSONObject("data");
                Log.e("wen0321", "loadHWInfo: " + data);

                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 102;
                handler.sendMessage(message);

            } catch (JSONException e) {
                Log.e("json", "loadHWInfo: " + e);

            }

        }, error -> {
//            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: loadHWInfo " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void loadQuInfo() {
        String start = time[2].getText().toString() + " 00:00:00";
        // 将end时间改为start时间的下一天
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(start));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String end = sdf.format(calendar.getTime());

        String mRequestUrl = Constant.API + Constant.T_REPORT_QU + "?userId=" + username + "&unitId=1101010010001" + "&startTime=" + start + "&endTime=" + end;
        Log.e("wen0307", "loadQuInfo: " + mRequestUrl);

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
//            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: loadQuInfo " + error.toString());
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

    // 课堂授课刷新UI 【第二模块】
    private void refreshClass(JSONObject data) throws JSONException {
        String status = data.getString("status");
        BarChart bc_main = v[0].findViewById(R.id.bc_mian);
        LinearLayout ll_show = v[0].findViewById(R.id.ll_show);
        TextView tv_empty = v[0].findViewById(R.id.tv_empty);
        TableLayout tl_main = v[0].findViewById(R.id.tl_main);
        bc_main.setScaleEnabled(false);
        bc_main.setTouchEnabled(false);

        if (status.equals("no")) {
            ll_show.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            // 空数据
            textViewFontChange(tv_class[0], "内容: 0", 4);
            textViewFontChange(tv_class[1], "批注: 0", 4);
            textViewFontChange(tv_class[2], "互动: 0", 4);
            textViewFontChange(tv_class[3], "板书: 0", 4);
            tv_num[0].setText("0");
            return;
        } else {
            Log.e("wen0308", "refreshClass: " + data);
            ll_show.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);
        }

        // 直接数据
        textViewFontChange(tv_class[0], "内容: " + data.getString("contentNum"), 4);
        textViewFontChange(tv_class[1], "批注: " + data.getString("pzNum"), 4);
        textViewFontChange(tv_class[2], "互动: " + data.getString("hdNum"), 4);
        textViewFontChange(tv_class[3], "板书: " + data.getString("bsNum"), 4);

        String effectiveKeciNum = data.getString("effectiveKeci");
        String sumKeciNum = data.getString("sumKeci");

        textViewFontChange(tv_num[0], effectiveKeciNum + " / " + sumKeciNum, effectiveKeciNum.length() + 1);

        // 绘制柱状图
        List<BarEntry> entries = new ArrayList<>();
        JSONArray yValues = data.getJSONArray("yList");
        for (int i = 0; i < yValues.length(); i++) {
            entries.add(new BarEntry((float) (i + 0.5), yValues.getInt(i)));
        }
        // Create a dataset
        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#a7cf93")); // Set color for the bars

        // Set xValues below the bars
        XAxis xAxis = bc_main.getXAxis();

        // 确保X轴标签与柱子对齐
        xAxis.setGranularity(1f); // 设置最小间隔为1，避免刻度间距导致标签错位
        xAxis.setLabelCount(yValues.length(), false); // 设置X轴标签数量与数据点个数一致，但不启用自动换行
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true); // 尝试居中对齐标签

        // 确保X轴标签与柱子对齐
        xAxis.setValueFormatter(new MyXAxisValueFormatter(data.getJSONArray("xList"))); // Custom XAxisValueFormatter
        xAxis.setDrawGridLines(false);
        YAxis yAxisLeft = bc_main.getAxisLeft();
        yAxisLeft.setAxisMinimum(0f); // 设置Y轴的最小值为0
        // 获取右侧Y轴并设置为不可见
        YAxis yAxisRight = bc_main.getAxisRight();
        yAxisRight.setEnabled(false);
        // Create a BarData object and assign it to BarChart
        BarData barData = new BarData(dataSet);
        barData.setValueFormatter(new MyValueFormatter());
        barData.setValueTextSize(20);
        bc_main.setData(barData);
        bc_main.getDescription().setEnabled(false); // Disable description
        bc_main.setDrawValueAboveBar(true); // Set values below bars
        // 关闭图例显示
        bc_main.getLegend().setEnabled(false);
        // Refresh the chart
        bc_main.invalidate();

        // 绘制表格
        makeTableUI(data, tl_main);
    }

    // 布置作业刷新UI 【第三板块】
    private void refreshHW(JSONObject data) throws JSONException {
        String status = data.getString("status");
        HorizontalBarChart hbc_mian = v[1].findViewById(R.id.hbc_mian);
        BarChart chart = v[1].findViewById(R.id.bc_mian);
        chart.setVisibility(View.GONE);
        CombinedChart combinedChart = v[1].findViewById(R.id.cbc_mian);
        LinearLayout ll_show = v[1].findViewById(R.id.ll_show);
        TextView tv_empty = v[1].findViewById(R.id.tv_empty);
        TableLayout tl_main = v[1].findViewById(R.id.tl_main);
        RelativeLayout rl_show = v[1].findViewById(R.id.rl_show);
        rl_show.setVisibility(View.VISIBLE);
        hbc_mian.setVisibility(View.VISIBLE);
        hbc_mian.setScaleEnabled(false);
        hbc_mian.setTouchEnabled(false);
        combinedChart.setVisibility(View.VISIBLE);
        combinedChart.setScaleEnabled(false);
        combinedChart.setTouchEnabled(false);
        Log.e("wen0321", "data" + data.toString());
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
        textViewFontChange(tv_hw[0], "试题总数: " + data.getString("queSumNum"), 6);
        textViewFontChange(tv_hw[1], "平均题数: " + data.getString("queAvgNum"), 6);

        String effectiveKeciNum = data.getString("effectiveKeciNum");
        String sumKeciNum = data.getString("sumKeciNum");

        textViewFontChange(tv_num[1], effectiveKeciNum + " / " + sumKeciNum, effectiveKeciNum.length() + 1);
        // 绘图数据
        // 图1
        JSONArray Xlist = data.getJSONArray("classNameList");
        JSONArray Ylist1 = data.getJSONArray("X1List");
        List<Float> submitRates = new ArrayList<>();
        List<Float> correctRates = new ArrayList<>();
        JSONObject Ylist1_1 = Ylist1.getJSONObject(0);
        JSONObject Ylist1_2 = Ylist1.getJSONObject(1);
        // 构建数据集 提交率和批改率
        for (int i = 0; i < Xlist.length(); i++) {
            if (Ylist1_1.get("name").equals("提交率")) {
                submitRates.add((float) Ylist1_1.getJSONArray("data").getDouble(i));
                correctRates.add((float) Ylist1_2.getJSONArray("data").getDouble(i));
            } else {
                submitRates.add((float) Ylist1_2.getJSONArray("data").getDouble(i));
                correctRates.add((float) Ylist1_1.getJSONArray("data").getDouble(i));
            }
        }

        // 顺序逆向一下
        Collections.reverse(submitRates);
        Collections.reverse(correctRates);
        JSONArray classNameList = data.getJSONArray("classNameList");
        // reverseX顺序逆向一下
        List<String> XlistReverse = new ArrayList<>();
        for (int i = classNameList.length() - 1; i >= 0; i--) {
            XlistReverse.add(classNameList.getString(i));
        }
        // 还原为JSONArray
        JSONArray XlistReverseJson = new JSONArray(XlistReverse);

        List<BarEntry> submitEntries = new ArrayList<>();
        List<BarEntry> correctEntries = new ArrayList<>();

        for (int i = 0; i < Xlist.length(); i++) {
            // 提交率数据集
            submitEntries.add(new BarEntry(i, submitRates.get(i)));
            // 批改率数据集
            correctEntries.add(new BarEntry(i, correctRates.get(i)));
        }

        BarDataSet set1 = new BarDataSet(submitEntries, "提交率");
        set1.setColor(Color.parseColor("#7ec6c8")); // 可以自定义颜色
        set1.setValueFormatter(new PersentValueFormatter()); // 应用格式化器
        BarDataSet set2 = new BarDataSet(correctEntries, "批改率");
        set2.setColor(Color.parseColor("#fac9d2")); // 可以自定义颜色
        set2.setValueFormatter(new PersentValueFormatter()); // 应用格式化器
        // 如果想在柱状图上方显示数值，可以启用以下设置
        set1.setDrawValues(true);
        set2.setDrawValues(true);
        // 计算组间距、柱子间距和柱子宽度
        float barWidth = 0.35f; // 条目宽度
        float groupSpace = 0.13f; // 组之间的间隔
        float barSpace = 0.08f; // 组内条目之间的间隔
        // 计算条目之间的间距总和
        // 计算起始位置，以便第一个柱子的中心与X轴标签对齐
        BarData barData = new BarData(set2, set1);
        barData.setBarWidth(barWidth); // 设置柱子宽度
        hbc_mian.setData(barData);
        // 分组柱状图设置
        hbc_mian.groupBars(0, groupSpace, barSpace);
        // 获取图表的描述并设置为空
        Description description = hbc_mian.getDescription();
        description.setText(""); // 设置描述文本为空
        // x轴设置
        XAxis xAxis = hbc_mian.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(XlistReverse.size());
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(XlistReverseJson));
        xAxis.setDrawGridLines(false); // 设置不绘制网格线

        // 确保左边Y轴的最小值设置为0
        YAxis yAxisLeft = hbc_mian.getAxisLeft();
        yAxisLeft.setEnabled(false);
        yAxisLeft.setAxisMinimum(0f); // 确保右边Y轴的最小值也是0
        yAxisLeft.setSpaceBottom(0f); // 设置底部空间为0
        yAxisLeft.setValueFormatter(new PersentValueFormatter());
        yAxisLeft.setDrawAxisLine(false);
        // 保持刻度线绘制
        yAxisLeft.setDrawGridLines(true);

        YAxis yAxisRight = hbc_mian.getAxisRight();
        yAxisRight.setValueFormatter(new PersentValueFormatter());
        yAxisRight.setAxisMinimum(0f); // 确保右边Y轴的最小值也是0
        yAxisRight.setSpaceBottom(0f); // 设置底部空间为0
        yAxisRight.setDrawAxisLine(false);
        // 保持刻度线绘制
        yAxisRight.setDrawGridLines(true);


        // 图例
        Legend legend = hbc_mian.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(10f); // 设置图标大小
        legend.setTextSize(12f); // 设置文本大小
        LegendEntry[] legendEntries = new LegendEntry[2];
        legendEntries[0] = new LegendEntry("提交率", Legend.LegendForm.CIRCLE, 10f, Float.NaN, null, set1.getColor());
        legendEntries[1] = new LegendEntry("批改率", Legend.LegendForm.CIRCLE, 10f, Float.NaN, null, set2.getColor());
        hbc_mian.getLegend().setCustom(legendEntries);
        // 刷新图表
        hbc_mian.invalidate();


        // 图二========================================================================================================

        // 修改CombinedData对象以同时保存BarData和LineData
        CombinedData cobData = new CombinedData();

        JSONArray X2List = data.getJSONArray("X2List");
        JSONArray totalScores = null;
        JSONArray avgScores = null;
        JSONArray scoreRates = null;
        for (int i = 0; i < X2List.length(); i++) {
            if (X2List.getJSONObject(i).getString("name").equals("总分")) {
                totalScores = X2List.getJSONObject(i).getJSONArray("data");
            } else if (X2List.getJSONObject(i).getString("name").equals("平均分")) {
                avgScores = X2List.getJSONObject(i).getJSONArray("data");
            } else if (X2List.getJSONObject(i).getString("name").equals("得分率")) {
                scoreRates = X2List.getJSONObject(i).getJSONArray("data");
            }
        }

        cobData.setData(generateBarData(totalScores, avgScores)); // 添加柱状图数据
        cobData.setData(generateLineData(scoreRates)); // 添加折线图数据

        combinedChart.setData(cobData); // 将组合数据设置到图表上

        // 启用右侧Y轴来显示折线数据集
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setEnabled(true);
        rightAxis.setValueFormatter(new PersentValueFormatter());
        rightAxis.setAxisMinimum(0f); // 右侧Y轴起始值设置为0
        // 根据scoreRates数据集确定并设置右侧Y轴的最大值
        rightAxis.setAxisMaximum(180f);
        rightAxis.setDrawAxisLine(false);


        // 调整左侧Y轴来显示柱状数据集
        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setAxisMinimum(0f); // 左侧Y轴起始值设置为0
        leftAxis.setAxisMaximum(120f);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setTextColor(getResources().getColor(R.color.main_blue));

        // 根据totalScores和avgScores数据集确定并设置左侧Y轴的最大值
//        leftAxis.setAxisMaximum(/* 从totalScores和avgScores中计算最大值并在此设置 */);

        // 调整X轴以匹配X2list数据
        xAxis = combinedChart.getXAxis();
        xAxis.setDrawGridLines(false);
        // 使用XlistReverseJson设置X轴标签
        xAxis.setValueFormatter(new MyXAxisValueFormatter(classNameList));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(-0.5f); // 设置X轴的最小值
        xAxis.setGranularity(1f); // 设置X轴的刻度间隔
        xAxis.setAxisMaximum(classNameList.length() - 0.5f); // 设置X轴的最大值
        xAxis.setLabelCount(classNameList.length()); // 调整标签数量

        combinedChart.getDescription().setEnabled(false);

        Legend legend2 = combinedChart.getLegend();

        // 获取图表中的数据集以修改它们的图例形状
        List<LegendEntry> legendEntries2 = new ArrayList<>();

        // 对于柱状图数据集（总分和平均分），设置图例为圆点
        LegendEntry totalScoreLegendEntry = new LegendEntry();
        totalScoreLegendEntry.form = Legend.LegendForm.CIRCLE;
        totalScoreLegendEntry.label = "总分";
        totalScoreLegendEntry.formColor = Color.parseColor("#feeb9b");

        LegendEntry avgScoreLegendEntry = new LegendEntry();
        avgScoreLegendEntry.form = Legend.LegendForm.CIRCLE;
        avgScoreLegendEntry.label = "平均分";
        avgScoreLegendEntry.formColor = Color.parseColor("#c6eecc");

        // 对于折线图数据集（得分率），保留默认样式（线加点）
        LegendEntry scoreRateLegendEntry = new LegendEntry();
        scoreRateLegendEntry.form = Legend.LegendForm.LINE;
        scoreRateLegendEntry.label = "得分率";
        scoreRateLegendEntry.formColor = Color.parseColor("#7f80d7");

        // 将自定义的图例条目添加到图例条目列表中
        legendEntries2.add(totalScoreLegendEntry);
        legendEntries2.add(avgScoreLegendEntry);
        legendEntries2.add(scoreRateLegendEntry);
        legend2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // 将图例放置在底部
        legend2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // 在中间对齐
        legend2.setFormSize(10f); // 设置图标大小
        legend2.setTextSize(10f); // 设置文本大小
        legend2.setTypeface(Typeface.DEFAULT_BOLD);
        // 将自定义的图例条目列表设置到图例中

        legend2.setCustom(legendEntries2);
        combinedChart.setExtraOffsets(0, 0, 0, 10);
        // 刷新图表
        combinedChart.invalidate();

        // 表格
        makeTableUI(data, tl_main);
    }

    // 批阅试题刷新UI 【第四板块】
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
        JSONArray dataT = Ylist.getJSONObject(0).getJSONArray("data");
        JSONArray dataQ = Ylist.getJSONObject(1).getJSONArray("data");
        for (int i = 0; i < Xlist.length(); ++i) {
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
        dataB.setBarWidth(0.6f); // 设置柱子的宽度为分配空间的 90%
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
        Log.e("wen0309", "refreshHW: ");

        // 表格数据
        // 表头的标题
        JSONArray table_json = data.getJSONArray("tableHead");
        String[] headerTitles = new String[table_json.length()];
        // 将 JSON 数组转换为 Java 数组
        for (int i = 0; i < table_json.length(); ++i) {
            headerTitles[i] = table_json.getString(i);
        }

        table_json = data.getJSONArray("tableData");
        Log.e("wen0306", "makeTableUI: " + table_json.length());
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

        // 表头
        TableRow headerRow = new TableRow(getActivity());
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        int dp1 = PxUtils.dip2px(getActivity(), 1);

        for (int i = 0; i < headerTitles.length; ++i) {
            String title = headerTitles[i];
            TextView headerTextView = new TextView(getActivity());
            // 创建单元格布局参数，并设置外边距
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                layoutParams.setMargins(dp1, dp1, dp1, dp1);
            }else{
                layoutParams.setMargins(0, dp1, dp1, dp1);
            }
            headerTextView.setLayoutParams(layoutParams);
            headerTextView.setText(title);
            headerTextView.setTextColor(getResources().getColor(R.color.white));
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setBackgroundColor(Color.parseColor("#a5a5a5"));
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f); // 设置字号为18sp
            headerTextView.setPadding(10, 0, 10, 0);
            headerRow.addView(headerTextView);
        }

        tl_main.addView(headerRow);


        // 表体
        for (int i = 0; i < contentData.length; ++i) {
            String[] row = contentData[i];
            TableRow contentRow = new TableRow(getActivity());
            contentRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < row.length; ++j) {
                String cell = row[j];
                TextView cellTextView = new TextView(getActivity());
                if (cell.equals("null")) {
                    cell = "";
                }
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                if (j == 0) {
                    layoutParams.setMargins(dp1, 0, dp1, dp1);
                }else{
                    layoutParams.setMargins(0, 0, dp1, dp1);
                }
                cellTextView.setLayoutParams(layoutParams);
                cellTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f); // 设置字号为18sp
                cellTextView.setText(cell);
                cellTextView.setGravity(Gravity.CENTER);
                cellTextView.setBackgroundResource(R.color.white);
                cellTextView.setPadding(10, 0, 10, 0);
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
        int color = getContext().getColor(R.color.gray_new);
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

            return String.valueOf((int) value);

        }
    }

    public class MyXAxisValueFormatter extends ValueFormatter {

        private final JSONArray mValues;

        public MyXAxisValueFormatter(JSONArray values) {
            this.mValues = values;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            int index = (int) value;
            if (index >= 0 && index < mValues.length()) {
                try {
                    return mValues.getString(index);
                } catch (JSONException e) {
                    Log.e("wen0308", "getAxisLabel: " + e);
                }
            }
            return "";
        }
    }

    private class PersentValueFormatter extends ValueFormatter {

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return String.format("%.0f%%", value);
        }

        @Override
        public String getFormattedValue(float value) {
            // 修改这里来格式化浮点数，比如保留一位小数
            return String.format("%.0f%%", value);
        }
    }

    private BarData generateBarData(JSONArray totalScores, JSONArray avgScores) {

        ArrayList<BarEntry> entriesTotal = new ArrayList<>();
        ArrayList<BarEntry> entriesAvg = new ArrayList<>();

        // Loop through the data and add entries
        for (int index = 0; index < totalScores.length(); index++) {
            try {
                entriesTotal.add(new BarEntry(index, (float) totalScores.getDouble(index)));
                entriesAvg.add(new BarEntry(index, (float) avgScores.getDouble(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        BarDataSet setTotal = new BarDataSet(entriesTotal, "总分");
        setTotal.setColor(Color.parseColor("#feeb9b"));
        BarDataSet setAvg = new BarDataSet(entriesAvg, "平均分");
        setAvg.setColor(Color.parseColor("#c6eecc"));

        // We will need half of the bar width to offset the two datasets
        float barSpace = 0.05f; // x2 dataset
        float barWidth = 0.3f; // x2 dataset
        float groupSpace = 1.0f - (barWidth + barSpace) * 2;

        // (barWidth + barSpace) * 2 + groupSpace = 1, meaning we have room for two bars and spacing
        BarData d = new BarData(setTotal, setAvg);
        d.setBarWidth(barWidth);

        d.setBarWidth(barWidth); // 设置柱子宽度
        // Make this BarData object grouped
        d.groupBars(-0.5f, groupSpace, barSpace); // Start at x = 0
        d.setDrawValues(false); // 隐藏数值标注

        return d;
    }

    private LineData generateLineData(JSONArray scoreRates) {

        ArrayList<Entry> entriesRate = new ArrayList<>();

        // Loop through the data and add entries
        for (int index = 0; index < scoreRates.length(); index++) {
            // Here we add the entries for the line dataset
            try {
                entriesRate.add(new Entry(index, (float) scoreRates.getDouble(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        LineDataSet setRate = new LineDataSet(entriesRate, "得分率");
        setRate.setColor(Color.parseColor("#7f80d7"));
        setRate.setLineWidth(2.5f);
        setRate.setCircleColor(Color.parseColor("#7f80d7"));
        setRate.setCircleHoleColor(Color.parseColor("#7f80d7")); // 设置点的内部颜色为白色
        setRate.setCircleRadius(4f);
        setRate.setFillColor(Color.parseColor("#7f80d7"));
        setRate.setMode(LineDataSet.Mode.CUBIC_BEZIER); // 修改为直线模式，不填充
        setRate.setDrawValues(false); // 隐藏数值标注

        LineData lineData = new LineData(setRate);
        return lineData;
    }

//
//    private int getScreenWidth() {
//        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        return metrics.widthPixels;
//    }

}