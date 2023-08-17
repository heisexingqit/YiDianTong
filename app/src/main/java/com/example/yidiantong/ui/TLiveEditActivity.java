package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.adapter.MyArrayAdapter;
import com.example.yidiantong.bean.KeyValueEntity;
import com.example.yidiantong.bean.LiveAddParamsEntity;
import com.example.yidiantong.bean.TLiveItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TLiveEditActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TLiveEditActivity";

    private EditText et_ketang_name;
    private TextView tv_start_time;
    private LinearLayout ll_hour;
    private TextView tv_hour;
    private LinearLayout ll_minute;
    private TextView tv_minute;
    private CheckBox cb_self;
    private LinearLayout ll_divider_group;
    private CheckBox cb_group;
    private LinearLayout ll_subject;
    private LinearLayout ll_ketang;
    private LinearLayout ll_group;
    private ImageView iv_calendar;

    private DateFormat dateFormat;
    private String selectTime;
    private String selectDate;

    private View contentView;
    private PopupWindow window;
    private View contentView2;
    private PopupWindow window2;
    private View contentView3;
    private PopupWindow window3;

    private String mRequestUrl;

    private LiveAddParamsEntity liveParams;
    private List<LiveAddParamsEntity.LiveSubjectEntity> subjectList;
    private Map<String, Integer> sub2Idx = new HashMap<>();
    private RadioGroup rg_subject;
    private Map<String, String> subjectMap = new HashMap<>();
    private String selectedSubject;
    private LinearLayout ll_group_show;
    private ClickableTextView tv_group;

    private MyArrayAdapter adapter;
    private List<String> groupList = new ArrayList<>();
    private Map<String, String> ketangMap = new HashMap<>();
    private List<String> selectedKetang = new ArrayList<>();

    private String selectedGroup;
    private Map<String, String> groupMap = new HashMap<>();

    private AlertDialog.Builder builder;
    private TLiveItemEntity tLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tlive_edit);

        // 默认参数
        tLive = (TLiveItemEntity) getIntent().getSerializableExtra("liveItem");

        // 获取各种组件变量
        builder = new AlertDialog.Builder(this);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        et_ketang_name = findViewById(R.id.et_ketang_name);
        tv_start_time = findViewById(R.id.tv_start_time);
        iv_calendar = findViewById(R.id.iv_calendar);
        iv_calendar.setOnClickListener(this);
        ll_hour = findViewById(R.id.ll_hour);
        ll_hour.setOnClickListener(this);
        tv_hour = findViewById(R.id.tv_hour);
        ll_minute = findViewById(R.id.ll_minute);
        ll_minute.setOnClickListener(this);
        tv_minute = findViewById(R.id.tv_minute);
        cb_self = findViewById(R.id.cb_self);
        cb_group = findViewById(R.id.cb_group);
        ll_subject = findViewById(R.id.ll_subject);
        ll_ketang = findViewById(R.id.ll_ketang);
        ll_group = findViewById(R.id.ll_group);
        ll_divider_group = findViewById(R.id.ll_divide_group);
        ll_group_show = findViewById(R.id.ll_group_show);
        ll_group_show.setOnClickListener(this);
        tv_group = findViewById(R.id.tv_group);
        // 给协作组多选控件设置监听，控制底部协作组显示和隐藏
        cb_group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ll_group.setVisibility(View.VISIBLE);
                    ll_divider_group.setVisibility(View.VISIBLE);
                } else {
                    ll_group.setVisibility(View.GONE);
                    ll_divider_group.setVisibility(View.GONE);
                }
            }
        });
        rg_subject = findViewById(R.id.rg_subject);

        // 给单选组设置监听
        rg_subject.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = findViewById(i);
                selectedSubject = btn.getText().toString();
                showKeTang();
            }
        });
        findViewById(R.id.btn_submit).setOnClickListener(this);

        // 设定时间
        Date time = new Date(Long.valueOf(tLive.getStartDate().getTime()));
        // 指定日期时间格式
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tv_start_time.setText(dateFormat.format(time));

        // 设定名称
        et_ketang_name.setText(tLive.getTitle());

        // 设定课时
        // 正则表达式模式
        String pattern = "(?:(\\d+)小时)?(?:(\\d+)分钟)?";
        // 创建 Pattern 对象
        Pattern regexPattern = Pattern.compile(pattern);
        // 创建 Matcher 对象
        Matcher matcher = regexPattern.matcher(tLive.getHour());
        if (matcher.find()) {
            String hoursGroup = matcher.group(1);
            String minutesGroup = matcher.group(2);

            // 将匹配到的小时和分钟数转换为整数
            if (hoursGroup != null) {
                tv_hour.setText(hoursGroup + "小时");
            }
            if (minutesGroup != null) {
                tv_minute.setText(minutesGroup + "分钟");
            }
        }

        loadItems_Net();

        // 初始化协作组选择适配器，后面更新
        adapter = new MyArrayAdapter(this, groupList);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_calendar:
                // 时间选择器
                timePickerShow(Calendar.getInstance(), string2Calendar(tv_start_time.getText().toString()), tv_start_time);
                break;
            case R.id.ll_hour:
                if (contentView == null) {
                    contentView = LayoutInflater.from(this).inflate(R.layout.menu_tlive_add_hour, null, false);
                    //绑定点击事件
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView tv = (TextView) view;
                            tv_hour.setText(tv.getText().toString());
                            window.dismiss();
                        }
                    };
                    contentView.findViewById(R.id.tv_0).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_1).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_2).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_3).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_4).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_5).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_6).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_7).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_8).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_9).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_10).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_11).setOnClickListener(onClickListener);
                    contentView.findViewById(R.id.tv_12).setOnClickListener(onClickListener);
                    window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window.setTouchable(true);
                }
                window.showAsDropDown(ll_hour, 0, -10);
                break;
            case R.id.ll_minute:
                if (contentView2 == null) {
                    contentView2 = LayoutInflater.from(this).inflate(R.layout.menu_tlive_add_minute, null, false);
                    //绑定点击事件
                    View.OnClickListener onClickListener2 = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView tv = (TextView) view;
                            tv_minute.setText(tv.getText().toString());
                            window2.dismiss();
                        }
                    };
                    contentView2.findViewById(R.id.tv_00).setOnClickListener(onClickListener2);
                    contentView2.findViewById(R.id.tv_10).setOnClickListener(onClickListener2);
                    contentView2.findViewById(R.id.tv_20).setOnClickListener(onClickListener2);
                    contentView2.findViewById(R.id.tv_30).setOnClickListener(onClickListener2);
                    contentView2.findViewById(R.id.tv_40).setOnClickListener(onClickListener2);
                    contentView2.findViewById(R.id.tv_50).setOnClickListener(onClickListener2);
                    window2 = new PopupWindow(contentView2, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window2.setTouchable(true);
                }
                window2.showAsDropDown(ll_minute, 0, -10);
                break;
            case R.id.ll_group_show:
                if (contentView3 == null) {
                    contentView3 = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);
                    ListView lv_homework = contentView3.findViewById(R.id.lv_homework);
                    lv_homework.setBackgroundResource(R.drawable.search_select_bg2);
                    lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 300);

                    lv_homework.setAdapter(adapter);
                    lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedGroup = groupList.get(i);
                            tv_group.setText(selectedGroup);
                            window3.dismiss();
                        }
                    });

                    window3 = new PopupWindow(contentView3, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    window3.setTouchable(true);
                }
                window3.showAsDropDown(ll_group_show, 0, -10);
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    private Calendar string2Calendar(String dateString) {
        Date date = null;
        if (dateString == null || dateString.length() == 0) {
            return Calendar.getInstance();
        }
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public void timePickerShow(Calendar startDate, Calendar setDate, TextView tv) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 2);
        // 时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                date.setSeconds(0);
                selectTime = timeFormat.format(date);
                if (date != null) {
                    tv.setText(selectDate + " " + selectTime);
                }
            }
        }).setType(new boolean[]{false, false, false, true, true, false})
                .setTitleText("时间选择")
                .setRangDate(startDate, endDate)
                .setDate(setDate)
                .build();

        // 日期选择器
        TimePickerView pvData = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                selectDate = dateFormat.format(date);
                pvTime.show();
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startDate, endDate)
                .setSubmitText("下一步")
                .setTitleText("日期选择")
                .setDate(setDate)
                .build();

        pvData.show();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                liveParams = (LiveAddParamsEntity) message.obj;
                // 同步显示学科 单选框
                subjectList = liveParams.getSubjectList();

                ContextThemeWrapper wrapper = new ContextThemeWrapper(TLiveEditActivity.this, R.style.CustomRadioStyle);
                // map快速访问映射
                for (int i = 0; i < subjectList.size(); ++i) {
                    LiveAddParamsEntity.LiveSubjectEntity item = subjectList.get(i);
                    sub2Idx.put(item.getSubjectName(), i);

                    // 绘制单选框或文本ll_subject
                    if (subjectList.size() > 1) {
                        RadioButton radioButton = new RadioButton(wrapper);
                        radioButton.setText(item.getSubjectName());
                        radioButton.setTextSize(13);
                        radioButton.setTextColor(getColor(R.color.purple_700));
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMarginStart(PxUtils.dip2px(TLiveEditActivity.this, 10));
                        radioButton.setLayoutParams(params);
                        rg_subject.addView(radioButton);
                        if (i == 0) {
                            radioButton.setChecked(true);
                            selectedSubject = item.getSubjectName();
                        }
                    } else {
                        rg_subject.setVisibility(View.GONE);
                        TextView textView = new TextView(TLiveEditActivity.this);
                        textView.setText(item.getSubjectName());
                        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        ll_subject.addView(textView);
                    }

                    subjectMap.put(item.getSubjectName(), item.getSubjectId());
                    List<KeyValueEntity> ketangList = item.getKetangList();
                    for (int j = 0; j < ketangList.size(); ++j) {
                        ketangMap.put(ketangList.get(j).getValue(), ketangList.get(j).getKey());
                    }
                }
                List<KeyValueEntity> kvList = liveParams.getmList();
                for (int i = 0; i < kvList.size(); ++i) {
                    groupMap.put(kvList.get(i).getValue(), kvList.get(i).getKey());
                    groupList.add(kvList.get(i).getValue());
                }
                adapter.notifyDataSetChanged();
            } else if (message.what == 101) {
                String isSuccess = (String) message.obj;
                if (isSuccess.equals("success")) {
                    builder.setMessage("修改成功");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //两个一起用
                            Intent intent = new Intent(TLiveEditActivity.this, TLiveListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //登录成功跳转
                            startActivity(intent);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                } else {
                    builder.setMessage("发布失败，请稍后重试");
                    builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                    dialog.show();
                }
            }
        }
    };

    private void showKeTang() {
        ll_ketang.removeAllViews();
        int idx = sub2Idx.get(selectedSubject);
        List<KeyValueEntity> list = subjectList.get(idx).getKetangList();
        selectedKetang.clear();
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CheckBox cb = (CheckBox) compoundButton;
                if (b) {
                    selectedKetang.add(cb.getText().toString());
                } else {
                    selectedKetang.remove(cb.getText().toString());
                }
            }
        };
        for (int i = 0; i < list.size(); ++i) {
            KeyValueEntity item = list.get(i);
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(item.getValue());
            checkBox.setTextSize(13);
            checkBox.setTextColor(getColor(R.color.purple_700));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginStart(PxUtils.dip2px(TLiveEditActivity.this, 10));
            checkBox.setLayoutParams(params);
            ColorStateList colorStateList = getColorStateList(R.color.checkbox_color);
            checkBox.setButtonTintList(colorStateList);
            checkBox.setOnCheckedChangeListener(listener);
            ll_ketang.addView(checkBox);
        }
    }

    private void loadItems_Net() {

        mRequestUrl = Constant.API_LIVE + Constant.T_LIVE_ADD_PARAMS + "?userId=" + MyApplication.username;

        Log.d("wen", "教师添加直播参数" + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                Gson gson = new Gson();
                // 使用Goson框架转换Json字符串为列表
                LiveAddParamsEntity params = gson.fromJson(String.valueOf(json), LiveAddParamsEntity.class);

                Message msg = new Message();
                msg.obj = params;
                msg.what = 100;
                handler.sendMessage(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void submit() {
        // 完整性检查
        String name = et_ketang_name.getText().toString().trim();
        if (name.length() == 0) {
            builder.setMessage("请填写课堂名称");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
            return;
        }

        if (tv_hour.getText().toString().equals("00小时") && tv_minute.getText().toString().equals("00分钟")) {
            builder.setMessage("请填课节时长");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
            return;
        }

        String type = "";
        String ketangStr = "";
        if (cb_self.isChecked() && cb_group.isChecked()) {
            type = "3";
        } else if (cb_self.isChecked()) {
            type = "1";
        } else if (cb_group.isChecked()) {
            type = "2";
        } else {
            builder.setMessage("请选择课堂类型");
            builder.setNegativeButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
            dialog.show();
            return;
        }
        if (type.equals("1") || type.equals("3")) {
            if (selectedKetang.size() == 0) {
                builder.setMessage("请选择我的课堂");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }
            for (int i = 0; i < selectedKetang.size(); ++i) {
                if (i != 0) {
                    ketangStr += ",";
                }
                ketangStr += ketangMap.get(selectedKetang.get(i));
            }
        }

        String groupId = "", groupName = "";
        if (type.equals("2") || type.equals("3")) {
            if (selectedGroup == null) {
                builder.setMessage("请选择协作组");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }

            try {
                groupName = URLEncoder.encode(URLEncoder.encode(selectedGroup, "UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            groupId = groupMap.get(selectedGroup);
        }
        String title = "";
        String subject = "";

        try {
            // 终级大坑：二次编码
            title = URLEncoder.encode(URLEncoder.encode(et_ketang_name.getText().toString(), "UTF-8"), "UTF-8");
            subject = URLEncoder.encode(URLEncoder.encode(selectedSubject, "UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mRequestUrl = Constant.API_LIVE + Constant.T_LIVE_ADD + "?userId=" + MyApplication.username + "&title=" + title + "&subjectId=" + subjectMap.get(selectedSubject) + "&subjectName=" + subject + "&startTime=" + tv_start_time.getText().toString()
                + "&hour=" + tv_hour.getText().toString().substring(0, 2) + "&minutes=" + tv_minute.getText().toString().substring(0, 1)
                + "&type=" + type + "&ketangId=" + ketangStr + "&xzzId=" + groupId + "&xzzName=" + groupName + "&flag=update&roomId=" + tLive.getRoomId();

        Log.d("wen", "教师添加直播课" + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                Message msg = new Message();
                msg.obj = json.getString("status");
                msg.what = 101;
                handler.sendMessage(msg);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }
}