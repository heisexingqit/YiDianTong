package com.example.yidiantong.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.ui.TWeikeAddPickActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class TWeikePickAssignFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "TWeikePickAssignFragmen";

    private String selectDate, selectTime;
    private TextView tv_start;
    private TextView tv_end;
    private String mRequestUrl;

    private Map<String, String> ketangMap = new LinkedHashMap<>();
    private Map<String, String> classMap = new LinkedHashMap<>();
    private Map<String, String> classMapStuNames = new LinkedHashMap<>();
    private Map<String, String> classMapStuIds = new LinkedHashMap<>();
    private Map<String, String> groupMap = new LinkedHashMap<>();
    private Map<String, String> groupMapStuNames = new LinkedHashMap<>();
    private Map<String, String> groupMapStuIds = new LinkedHashMap<>();
    private Map<String, String> personMap = new LinkedHashMap<>();
    private TextView lastKetang;
    private String ketang = "";
    private List<String> clas = new ArrayList<>();
    private List<String> group = new ArrayList<>();
    private List<String> person = new ArrayList<>();
    private FlexboxLayout fl_ketang;
    private TextView tv_ketang_null;
    private TextView tv_ketang;
    private ImageView iv_ketang;
    private boolean isShow = false;
    private int pos = 0;

    private ClickableTextView tv_class;
    private ClickableTextView tv_group;
    private ClickableTextView tv_person;
    private ClickableTextView lastPopBtn;

    private TextView tv_class_null;
    private FlexboxLayout fl_class;
    private ClickableTextView btn_reset;
    private ClickableTextView btn_confirm;
    private ClickableTextView tv_save;
    private SimpleDateFormat dateFormat;

    private String assignType = "";

    private TWeikeAddPickActivity transmit;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        transmit = (TWeikeAddPickActivity) context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_weike_pick_assign, container, false);
        tv_start = view.findViewById(R.id.tv_start);
        tv_end = view.findViewById(R.id.tv_end);
        fl_ketang = view.findViewById(R.id.fl_ketang);
        fl_class = view.findViewById(R.id.fl_class);
        tv_ketang_null = view.findViewById(R.id.tv_ketang_null);
        tv_ketang = view.findViewById(R.id.tv_ketang);
        tv_ketang.setOnClickListener(this);
        iv_ketang = view.findViewById(R.id.iv_ketang);
        iv_ketang.setOnClickListener(this);
        tv_class = view.findViewById(R.id.tv_class);
        tv_group = view.findViewById(R.id.tv_group);
        tv_person = view.findViewById(R.id.tv_person);
        tv_class_null = view.findViewById(R.id.tv_class_null);
        btn_reset = view.findViewById(R.id.btn_reset);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        tv_save = view.findViewById(R.id.tv_save);
        lastPopBtn = tv_class;

        tv_class.setOnClickListener(this);
        tv_group.setOnClickListener(this);
        tv_person.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        // 当前时间
        Calendar startDate = Calendar.getInstance();

        // 指定日期时间格式
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        tv_start.setText(dateFormat.format(startDate.getTime()));
        tv_end.setText(getTomorrow2359(tv_start.getText().toString()));

        view.findViewById(R.id.iv_start).setOnClickListener(this);
        view.findViewById(R.id.iv_end).setOnClickListener(this);

        loadKeTang();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_start:
                // 时间选择器
                timePickerShow(Calendar.getInstance(), string2Calendar(tv_start.getText().toString()), tv_start);
                break;
            case R.id.iv_end:
                // 时间选择器
                timePickerShow(string2Calendar(tv_start.getText().toString()), string2Calendar(tv_end.getText().toString()), tv_end);
                break;
            case R.id.iv_ketang:
                if (isShow) {
                    fl_ketang.removeAllViews();
                    iv_ketang.setImageResource(R.drawable.down_icon);
                    isShow = false;
                } else {
                    showKeTang();
                    iv_ketang.setImageResource(R.drawable.up_icon);
                    isShow = true;
                }

                break;
            case R.id.tv_class:
                changePopBtn(tv_class);
                pos = 0;
                showClass();
                break;
            case R.id.tv_group:
                changePopBtn(tv_group);
                pos = 1;
                showClass();
                break;
            case R.id.tv_person:
                changePopBtn(tv_person);
                pos = 2;
                showClass();
                break;
            case R.id.tv_ketang:
                iv_ketang.callOnClick();
                break;
            case R.id.btn_reset:
                tv_start.setText("");
                tv_end.setText("");
                ketang = "";
                tv_ketang_null.setVisibility(View.GONE);
                iv_ketang.setImageResource(R.drawable.down_icon);
                fl_ketang.removeAllViews();
                tv_ketang.setText("");
                changePopBtn(tv_class);
                pos = 0;
                showClass();
                break;
            case R.id.btn_confirm:
                assignType = "1";
                submit();
                break;
            case R.id.tv_save:
                assignType = "3";
                submit();
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submit() {
        if(assignType.equals("1")){
            if(StringUtils.hasEmptyString(tv_start.getText().toString(), tv_end.getText().toString(), ketang) || (pos == 0 && clas.size() == 0) || (pos == 1 && group.size() == 0) || (pos == 2 && person.size() == 0)){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("请选择以上属性");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }
        }

        String classGroupIds = "", classGroupNames = "";
        String ids = "", names = "";
        StringBuilder result = new StringBuilder();
        StringBuilder result2 = new StringBuilder();
        StringBuilder result3 = new StringBuilder();
        StringBuilder result4 = new StringBuilder();
        String leanType = "0";
        switch (pos) {
            case 0:
                clas.forEach(key -> {
                    String id = classMapStuIds.get(key);
                    String name = classMapStuNames.get(key);
                    if (result.length() > 0) {
                        result.append(", "); // 在每个值之前添加逗号和空格
                    }
                    result.append(id);

                    if (result2.length() > 0) {
                        result2.append(", ");
                    }
                    result2.append(name);

                    if(result3.length() > 0){
                        result3.append(", ");
                    }
                    result3.append(classMap.get(key));

                    if(result4.length() > 0){
                        result4.append(", ");
                    }
                    result4.append(key);

                });
                ids = result.toString();
                names = result2.toString();
                classGroupIds = result3.toString();
                classGroupNames = result4.toString();
                leanType = "70";
                break;
            case 1:
                result.setLength(0);
                result2.setLength(0);
                result3.setLength(0);
                result4.setLength(0);
                group.forEach(key -> {
                    String id = groupMapStuIds.get(key);
                    String name = groupMapStuNames.get(key);
                    if (result.length() > 0) {
                        result.append(", "); // 在每个值之前添加逗号和空格
                    }
                    result.append(id);

                    if (result2.length() > 0) {
                        result2.append(", ");
                    }
                    result2.append(name);

                    if(result3.length() > 0){
                        result3.append(", ");
                    }
                    result3.append(groupMap.get(key));

                    if(result4.length() > 0){
                        result4.append(", ");
                    }
                    result4.append(key);
                });
                ids = result.toString();
                names = result2.toString();
                classGroupIds = result3.toString();
                classGroupNames = result4.toString();
                leanType = "50";
                break;
            case 2:
                result.setLength(0);
                result2.setLength(0);
                result3.setLength(0);
                result4.setLength(0);
                person.forEach(key -> {
                    String id = personMap.get(key);
                    String name = key;
                    if (result.length() > 0) {
                        result.append(", "); // 在每个值之前添加逗号和空格
                    }
                    result.append(id);

                    if (result2.length() > 0) {
                        result2.append(", ");
                    }
                    result2.append(name);

                    if(result3.length() > 0){
                        result3.append(", ");
                    }
                });
                ids = result.toString();
                names = result2.toString();
                break;
        }
        try {
            transmit.submit(tv_start.getText().toString() + ":00", tv_end.getText().toString() + ":00", ketang, ketangMap.get(ketang), classGroupNames, classGroupIds, assignType, ids, names, leanType, "save");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void timePickerShow(Calendar startDate, Calendar setDate, TextView tv) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 2);
        // 时间选择器
        TimePickerView pvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                selectTime = timeFormat.format(date);
                if (date != null) {
                    tv.setText(selectDate + " " + selectTime);
                    if (tv == tv_start) {
                        tv_end.setText(getTomorrow2359(tv.getText().toString()));
                    } else {
                        if (tv.getText().toString().compareTo(tv_start.getText().toString()) <= 0) {
                            Calendar cal = string2Calendar(tv_start.getText().toString());
                            cal.add(Calendar.SECOND, 1);
                            tv.setText(dateFormat.format(cal.getTime()));
                        }
                    }
                }
            }
        }).setType(new boolean[]{false, false, false, true, true, false})
                .setTitleText("时间选择")
                .setRangDate(startDate, endDate)
                .setDate(setDate)
                .build();

        // 日期选择器
        TimePickerView pvData = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
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

    private String getTomorrow2359(String dateString) {
        String result = "";
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // 将日期加一天
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            // 设置时间为23:59
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);

            // 格式化为字符串
            result = dateFormat.format(calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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

    /**
     * 按钮切换动画
     *
     * @param nowBtn 新点击的按钮对象
     */
    private void changePopBtn(ClickableTextView nowBtn) {
        if (nowBtn != lastPopBtn) {
            nowBtn.setBackgroundResource(R.drawable.t_homework_add_pick);
            nowBtn.setTextColor(getActivity().getColor(R.color.white));
            lastPopBtn.setBackgroundResource(0);
            lastPopBtn.setTextColor(getActivity().getColor(R.color.default_gray));
            lastPopBtn = nowBtn;
        }
    }

    /**
     * 请求批改信息 将Handler内容写入，速度更快（如遇到报错，再用Handler）
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadKeTang() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_KETANG + "?userName=" + MyApplication.username;
        Log.d("wen", "loadKeTang: " + mRequestUrl);
        ketangMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    ketangMap.put(object.getString("keTangName"), object.getString("keTangId"));
                }
                Log.d("wen", "课堂: " + ketangMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showKeTang() {
        if (ketangMap.size() == 0) {
            tv_ketang_null.setVisibility(View.VISIBLE);
        }
        lastKetang = null;
        ketangMap.forEach((name, id) -> {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_homework_add_block, fl_ketang, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            if (name.equals(ketang)) {
                tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                lastKetang = tv_name;
            }
            tv_name.setOnClickListener(v -> {
                ketang = (String) tv_name.getText();

                if (lastKetang != null) {
                    lastKetang.setBackgroundResource(R.color.light_gray3);
                }

                if (lastKetang == tv_name) {
                    ketang = "";
                    lastKetang = null;
                    // 初始化布置配置
                    tv_class_null.setText("请先选择课堂");
                    tv_class_null.setVisibility(View.VISIBLE);
                    fl_class.removeAllViews();

                } else {
                    ketang = tv_name.getText().toString();
                    tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    lastKetang = tv_name;
                    loadClass();
                }
                clas.clear();
                group.clear();
                person.clear();
                tv_ketang.setText(ketang);
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_ketang.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
            tv_name.setLayoutParams(params);
            fl_ketang.addView(view);
        });
    }

    /**
     * 集成三种发布方法为一体
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadClass() {

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_KETANG_ITEM + "?keTangId=" + ketangMap.get(ketang);
        Log.d("wen", "loadClass: " + mRequestUrl);
        classMap.clear();
        groupMap.clear();
        personMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
                Log.d("wen", "班级总: " + data);
                JSONArray classList = data.getJSONArray("classList");
                for (int i = 0; i < classList.length(); ++i) {
                    JSONObject object = classList.getJSONObject(i);
                    classMap.put(object.getString("value"), object.getString("id"));
                    classMapStuNames.put(object.getString("value"), object.getString("name"));
                    classMapStuIds.put(object.getString("value"), object.getString("ids"));
                    String[] idArray = object.getString("ids").split(",");
                    String[] nameArray = object.getString("name").split(",");

                    for (int j = 0; j < idArray.length; j++) {
                        personMap.put(nameArray[j], idArray[j]);
                    }
                }

                JSONArray groupList = data.getJSONArray("groupList");

                for (int i = 0; i < groupList.length(); ++i) {
                    JSONObject object = groupList.getJSONObject(i);
                    groupMap.put(object.getString("value"), object.getString("id"));
                    groupMapStuNames.put(object.getString("value"), object.getString("name"));
                    groupMapStuIds.put(object.getString("value"), object.getString("ids"));
                }

                Log.d("wen", "班级: " + classMap);
                showClass();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showClass() {
        fl_class.removeAllViews();
        if (ketang.length() == 0) {
            tv_class_null.setText("请先选择课堂");
            tv_class_null.setVisibility(View.VISIBLE);
            return;
        }
        switch (pos) {
            case 0:
                if (classMap.size() == 0) {
                    tv_class_null.setText("班级列表未获取到或者为空");
                    tv_class_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    tv_class_null.setVisibility(View.GONE);
                }
                break;
            case 1:
                if (groupMap.size() == 0) {
                    tv_class_null.setText("您还没有创建小组，可以前往电脑端进行创建");
                    tv_class_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    tv_class_null.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (personMap.size() == 0) {
                    tv_class_null.setText("个人列表未获取到或者为空");
                    tv_class_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    tv_class_null.setVisibility(View.GONE);
                }
                break;
        }
        switch (pos) {
            case 0:
                classMap.forEach((name, id) -> {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_homework_add_block, fl_class, false);
                    TextView tv_name = view.findViewById(R.id.tv_name);
                    tv_name.setText(name);
                    if (clas.contains(name)) {
                        tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    }
                    tv_name.setOnClickListener(v -> {
                        if (clas.contains(tv_name.getText().toString())) {
                            tv_name.setBackgroundResource(R.color.light_gray3);
                            clas.remove(tv_name.getText().toString());
                        } else {
                            tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                            clas.add(tv_name.getText().toString());
                        }
                    });
                    ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                    params.width = fl_class.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 14);
                    tv_name.setLayoutParams(params);
                    fl_class.addView(view);
                });
                break;
            case 1:
                groupMap.forEach((name, id) -> {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_homework_add_block, fl_class, false);
                    TextView tv_name = view.findViewById(R.id.tv_name);
                    tv_name.setText(name);
                    if (group.contains(name)) {
                        tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    }
                    tv_name.setOnClickListener(v -> {
                        if (group.contains(tv_name.getText().toString())) {
                            tv_name.setBackgroundResource(R.color.light_gray3);
                            group.remove(tv_name.getText().toString());
                        } else {
                            tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                            group.add(tv_name.getText().toString());
                        }
                    });
                    ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                    params.width = fl_class.getWidth() / 4 - PxUtils.dip2px(view.getContext(), 14);
                    tv_name.setLayoutParams(params);
                    fl_class.addView(view);
                });
                break;
            case 2:
                personMap.forEach((name, id) -> {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_homework_add_block, fl_class, false);
                    TextView tv_name = view.findViewById(R.id.tv_name);
                    tv_name.setText(name);
                    if (person.contains(name)) {
                        tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                    }
                    tv_name.setOnClickListener(v -> {
                        if (person.contains(tv_name.getText().toString())) {
                            tv_name.setBackgroundResource(R.color.light_gray3);
                            person.remove(tv_name.getText().toString());
                        } else {
                            tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
                            person.add(tv_name.getText().toString());
                        }
                    });
                    ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                    params.width = fl_class.getWidth() / 4 - PxUtils.dip2px(view.getContext(), 14);
                    tv_name.setLayoutParams(params);
                    fl_class.addView(view);
                });
                break;
        }
    }
}