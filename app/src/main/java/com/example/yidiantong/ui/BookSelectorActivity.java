package com.example.yidiantong.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BookSelectorAdapter;
import com.example.yidiantong.bean.BookDetailEntity;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.bean.BookSelectorEntity;
import com.example.yidiantong.fragment.BookDetailFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xinlan.imageeditlibrary.editimage.view.imagezoom.easing.Linear;
import androidx.recyclerview.widget.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BookSelectorActivity";

    private TextView tv_start;
    private TextView tv_end;

    private boolean showPicker = false;
    private RadioGroup rg_time;
    private RadioGroup rg_error_times;

    private LinearLayout ll_time_picker;
    private TextView tv_empty;
    private String timeType = "other";
    private CheckBox cb_learn_plan;
    private CheckBox cb_homework;
    private CheckBox cb_class;

    private String subjectId;
    private String subjectName;
    private int errorNum = 1;

    private List<BookSelectorEntity> itemList = new ArrayList<>();
    private BookSelectorAdapter adapter;

    private LinearLayout ll_bottom_divider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_selector);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        ll_bottom_divider = findViewById(R.id.ll_bottom_divider);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 获取学科信息
        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText(subjectName + "错题筛选");

        // 时间选择器
        findViewById(R.id.ll_start).setOnClickListener(this);
        findViewById(R.id.ll_end).setOnClickListener(this);
        findViewById(R.id.btn_confirm).setOnClickListener(this);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        ll_time_picker = findViewById(R.id.ll_time_picker);
        findViewById(R.id.iv_show_hide).setOnClickListener(v -> {
            if (showPicker) {
                showPicker = false;
                ll_time_picker.setVisibility(View.GONE);
                timeType = "";
            } else {
                showPicker = true;
                ll_time_picker.setVisibility(View.VISIBLE);
                // 移除监听器
                rg_time.setOnCheckedChangeListener(null);

                // 将RadioGroup的选中按钮置空
                rg_time.clearCheck();

                // 恢复监听器
                rg_time.setOnCheckedChangeListener(new MyRadioChangeListener());
                timeType = "other";
            }
        });


        // 单选部分
        rg_time = findViewById(R.id.rg_time);
        rg_time.setOnCheckedChangeListener(new MyRadioChangeListener());
        rg_error_times = findViewById(R.id.rg_error_times);
        rg_error_times.setOnCheckedChangeListener(new MyTimesListener());

        // 多选部分
        cb_learn_plan = findViewById(R.id.cb_learn_plan);
        cb_homework = findViewById(R.id.cb_homework);
        cb_class = findViewById(R.id.cb_class);
        cb_learn_plan.setOnCheckedChangeListener(new MyCheckedChangeListener());
        cb_homework.setOnCheckedChangeListener(new MyCheckedChangeListener());
        cb_class.setOnCheckedChangeListener(new MyCheckedChangeListener());

        tv_empty = findViewById(R.id.tv_empty);

        // 列表展示部分
        // 找到 ListView
        ListView lv_main = findViewById(R.id.lv_main);

        // 创建并设置自定义的适配器
        adapter = new BookSelectorAdapter(this, itemList);
        lv_main.setAdapter(adapter);


        // 设置点击事件
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 处理点击事件，可以根据需要进行操作
                BookSelectorEntity selectedItem = itemList.get(position);
                backMessage(selectedItem.getSourceId());
            }
        });

        // 今天时间
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 获取当前日期
        Date currentDate = new Date();

        // 使用Calendar类来获取前一个月的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -1); // 减去一个月
        Date previousMonthDate = calendar.getTime();

        // 设置tv_end为当前日期
        tv_end.setText(sdf.format(currentDate));

        // 设置tv_start为前一个月的日期
        tv_start.setText(sdf.format(previousMonthDate));
        // getListData();

        RadioButton defaultRadioButton = (RadioButton) rg_error_times.getChildAt(0); // 假设默认选中第一个
        defaultRadioButton.setChecked(true);
    }

    // 自定义的RadioB状态变化监听器
    private class MyRadioChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            RadioButton btn = findViewById(id);

            switch (btn.getText().toString()) {
                case "今天":
                    timeType = "today";
                    break;
                case "昨天":
                    timeType = "lastday";
                    break;
                case "最近7天":
                    timeType = "last7days";
                    break;
                case "最近30天":
                    timeType = "last30days";
                    break;
            }

            // 设置选中的RadioButton文本颜色为蓝色
            btn.setTextColor(getResources().getColor(R.color.blue_btn2));

            // 遍历RadioGroup中的所有RadioButton，将未选中的文本颜色设置为黑色
            for (int i = 0; i < rg_time.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) rg_time.getChildAt(i);
                if (radioButton.getId() != id) {
                    radioButton.setTextColor(getResources().getColor(R.color.default_gray));
                }
            }

            getListData();

            showPicker = false;
            ll_time_picker.setVisibility(View.GONE);
            tv_start.setText("");
            tv_end.setText("");
        }
    }
    // 自定义的RadioC状态变化监听器
    private class MyTimesListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int id) {
            RadioButton btn = findViewById(id);

            switch (btn.getText().toString()) {
                case "一次错题":
                    errorNum = 1;
                    break;
                case "二次错题":
                    errorNum = 2;
                    break;
                case "三次及以上错题":
                    errorNum = 3;
                    break;
            }

            // 设置选中的RadioButton文本颜色为蓝色
            btn.setTextColor(getResources().getColor(R.color.blue_btn2));

            // 遍历RadioGroup中的所有RadioButton，将未选中的文本颜色设置为黑色
            for (int i = 0; i < rg_error_times.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) rg_error_times.getChildAt(i);
                Log.e("wen0524", "onCheckedChanged: " + radioButton.getId());
                Log.e("wen0524", "id: " + id);
                if (radioButton.getId() != id) {
                    radioButton.setTextColor(getResources().getColor(R.color.default_gray));
                }
            }

            getListData();
        }
    }

    // 自定义的Checkbox状态变化监听器
    private class MyCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 如果选中，设置文本颜色为红色；否则，设置为默认颜色
            if (isChecked) {
                buttonView.setTextColor(getResources().getColor(R.color.blue_btn2));
            } else {
                buttonView.setTextColor(getResources().getColor(R.color.default_gray));
            }
            getListData();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_start:
                showTimePicker(tv_start);
                break;
            case R.id.ll_end:
                showTimePicker(tv_end);
                break;
            case R.id.btn_confirm:
                getListData();
                break;
        }
    }

    public void showTimePicker(TextView tv) {
        // 获取当前时间
        Calendar setDate = Calendar.getInstance();
        // 创建一个新的 Calendar 对象，并设置为当前时间
        Calendar startData = Calendar.getInstance();
        startData.add(Calendar.YEAR, -10);

        // 日期选择器
        TimePickerView pvData = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                tv.setText(dateFormat.format(date));
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(startData, setDate)
                .setSubmitText("确定")
                .setTitleText("日期选择")
                .setDate(setDate)
                .build();

        pvData.show();
    }

    private void getListData() {
        // 异常特判，三种数据为空情况
        if (!cb_learn_plan.isChecked() && !cb_homework.isChecked() && !cb_class.isChecked()) {
            // 多选一个没选
            this.itemList.clear();
            adapter.notifyDataSetChanged();
            ll_bottom_divider.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            return;
        }
        if (timeType.length() == 0) {
            // 时间没选
            this.itemList.clear();
            adapter.notifyDataSetChanged();
            ll_bottom_divider.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            return;
        }
        if (timeType.equals("other") && (tv_start.getText().length() == 0 || tv_end.getText().length() == 0)) {
            // 时间选择器没选
            this.itemList.clear();
            adapter.notifyDataSetChanged();
            ll_bottom_divider.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            return;
        }
        String sourceType = "";
        if (cb_learn_plan.isChecked()) {
            sourceType = "1";
        }
        if (cb_homework.isChecked()) {
            if (sourceType.length() > 0) {
                sourceType += ",2";
            } else {
                sourceType = "2";
            }
        }
        if (cb_class.isChecked()) {
            if (sourceType.length() > 0) {
                sourceType += ",3";
            } else {
                sourceType = "3";
            }
        }

        // 获取错题本信息
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_SELECTOR + "?timeType=" + timeType +
                "&subjectId=" + subjectId +
                "&userName=" + MyApplication.username +
                "&sourceType=" + sourceType +
                "&startTime=" + tv_start.getText() +
                "&endTime=" + tv_end.getText() +
                "&errorNum=" + errorNum;
        Log.e("0125", "getListData: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Gson gson = new Gson();
                List<BookSelectorEntity> itemList = gson.fromJson(itemString, new TypeToken<List<BookSelectorEntity>>() {
                }.getType());

                if (itemList.size() == 0) {
                    ll_bottom_divider.setVisibility(View.GONE);
                    tv_empty.setVisibility(View.VISIBLE);
                }else{
                    ll_bottom_divider.setVisibility(View.VISIBLE);
                    tv_empty.setVisibility(View.GONE);
                }

                // 更新数据
                this.itemList.clear();
                this.itemList.addAll(itemList);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Log.e(TAG, "volley报错: " + error);
        });

        MyApplication.addRequest(request, TAG);
    }

    private void backMessage(String sourceId) {
        Intent intent = new Intent();
        intent.putExtra("sourceId", sourceId);
        intent.putExtra("errorNum", errorNum);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}