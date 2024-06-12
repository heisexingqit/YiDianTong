package com.example.yidiantong.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.adapter.THomeworkXieZuoAdapter;
import com.example.yidiantong.bean.HomeworkXieZuoEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TTeachAssginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TTeachAssginActivity";

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
    private List<TextView> lastKetang = new ArrayList<>();
    private List<String> ketang = new ArrayList<>(); // 课堂数据
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

    private RelativeLayout rl_submitting;

    private String learnPlanId;
    private String learnPlanName;
    private String type;
    private String typeCamera;
    private String jsonString;
    private TextView tv_title;

    private boolean isFirst = true;
    private RadioGroup rg_homework, rg_assign;

    private int zouyeType = 1;
    private int zouyeFlag = 1;
    private TextView tv_kt;
    private Map<String, String> xiezuoMap = new LinkedHashMap<>();
    private TextView lastXieZuo;
    private String xiezuo;
    private RecyclerView rv_xiezuo;
    private THomeworkXieZuoAdapter adapter;
    private ConstraintLayout cl_type;
    private TextView tv_bz;

    private Boolean assignFlag = false;
    private RadioButton rb_assign1;
    private RadioButton rb_homework1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tteach_assgin);
        Intent intent = getIntent();

        learnPlanId = intent.getStringExtra("learnPlanId");
        learnPlanName = intent.getStringExtra("learnPlanName");
        type = intent.getStringExtra("type");
        typeCamera = intent.getStringExtra("typeCamera");

        TextView tv_name = findViewById(R.id.tv_name);
        tv_name.setText(learnPlanName);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        tv_bz = findViewById(R.id.tv_bz);
        cl_type = findViewById(R.id.cl_type);
        tv_kt = findViewById(R.id.tv_kt);
        rv_xiezuo = findViewById(R.id.rv_xiezuo);
        rv_xiezuo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_xiezuo.setItemAnimator(new DefaultItemAnimator());
        rg_homework = findViewById(R.id.rg_homework);
        rg_assign = findViewById(R.id.rg_assign);
        tv_start = findViewById(R.id.tv_start);
        tv_end = findViewById(R.id.tv_end);
        fl_ketang = findViewById(R.id.fl_ketang);
        fl_class = findViewById(R.id.fl_class);
        tv_ketang_null = findViewById(R.id.tv_ketang_null);
        tv_ketang = findViewById(R.id.tv_ketang);
        tv_ketang.setOnClickListener(this);
        iv_ketang = findViewById(R.id.iv_ketang);
        iv_ketang.setOnClickListener(this);
        tv_class = findViewById(R.id.tv_class);
        tv_group = findViewById(R.id.tv_group);
        tv_person = findViewById(R.id.tv_person);
        tv_class_null = findViewById(R.id.tv_class_null);
        btn_reset = findViewById(R.id.btn_reset);
        btn_confirm = findViewById(R.id.btn_confirm);
        LinearLayout ll_zuoye_type = findViewById(R.id.ll_zuoye_type);
        lastPopBtn = tv_class;

        tv_title = findViewById(R.id.tv_title);
        if (type.equals("1")) {
            tv_title.setText("布置导学案");
            ll_zuoye_type.setVisibility(View.GONE);
        } else if (type.equals("2")) {
            tv_title.setText("布置微课");
            ll_zuoye_type.setVisibility(View.GONE);
        } else {
            tv_title.setText("布置试卷");
        }
        tv_class.setOnClickListener(this);
        tv_group.setOnClickListener(this);
        tv_person.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        // 当前时间
        Calendar startDate = Calendar.getInstance();

        // 指定日期时间格式
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        tv_start.setText(dateFormat.format(startDate.getTime()));
        tv_end.setText(getTomorrow2359(tv_start.getText().toString()));

        findViewById(R.id.iv_start).setOnClickListener(this);
        findViewById(R.id.iv_end).setOnClickListener(this);

        loadKeTang(); // 第一步
        loadXieZuo(); // 第一步2


        // 遮蔽
        rl_submitting = findViewById(R.id.rl_submitting);
        TextView tv_submitting = findViewById(R.id.tv_submitting);
        tv_submitting.setText("保存中...");
        loadPickList(); // 获取关键信息


        // 单选框
        rg_homework.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_homework1:
                        zouyeType = 1;
                        break;
                    case R.id.rb_homework2:
                        zouyeType = 2;
                        break;
                }
            }
        });
        rg_assign.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                Log.e("wen0605", "onCheckedChanged: " + i);
                if (assignFlag) {
                    switch (i) {
                        case R.id.rb_assign1:
                            zouyeFlag = 1;
                            changeUI();
                            break;
                        case R.id.rb_assign2:
                            zouyeFlag = 2;
                            changeUI();
                            break;
                    }
                }
            }
        });
        rb_homework1 = findViewById(R.id.rb_homework1);
        rb_homework1.setChecked(true);
        zouyeType = 0;
        assignFlag = false;
        rb_assign1 = findViewById(R.id.rb_assign1);
        rb_assign1.setChecked(true);
        assignFlag = true;

        // 列表
        rv_xiezuo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_xiezuo.setItemAnimator(new DefaultItemAnimator());

        // Adapter
        adapter = new THomeworkXieZuoAdapter(this);
        rv_xiezuo.setAdapter(adapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void changeUI() {
        if (zouyeFlag == 1) {
            // 我的课堂UI
            tv_kt.setText("选择课堂:");
            tv_bz.setText("布置给:");
            cl_type.setVisibility(View.VISIBLE);
            rv_xiezuo.setVisibility(View.GONE);
            tv_class_null.setVisibility(View.VISIBLE);
            isFirst = true;
            ketang.clear();
            tv_class.callOnClick();
            showKeTang();
        } else {
            // 协作组课堂UI
            tv_kt.setText("选择协作组:");
            tv_bz.setText("布置范围:");
            cl_type.setVisibility(View.GONE);
            rv_xiezuo.setVisibility(View.VISIBLE);
            tv_class_null.setVisibility(View.GONE);
            showXieZuo();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) { // 第五步
        switch (view.getId()) {
            case R.id.iv_start:
                // 时间选择器
                timePickerShow(Calendar.getInstance(), string2Calendar(tv_start.getText().toString()), tv_start);
                break;
            case R.id.iv_end:
                // 时间选择器
                timePickerShow(string2Calendar(tv_start.getText().toString()), string2Calendar(tv_end.getText().toString()), tv_end);
                break;
//            case R.id.iv_ketang:
//                if (isShow) {
//                    fl_ketang.removeAllViews();
//                    iv_ketang.setImageResource(R.drawable.down_icon);
//                    isShow = false;
//                } else {
//                    showKeTang();
//                    iv_ketang.setImageResource(R.drawable.up_icon);
//                    isShow = true;
//                }
//
//                break;
            case R.id.tv_class:
                changePopBtn(tv_class);
                pos = 0;
                showClass();
                break;
            case R.id.tv_group:
                if (ketang.size() > 1) {
                    Toast.makeText(this, "按小组布置时，只能选择一个课堂!", Toast.LENGTH_SHORT).show();
                    break;
                }
                changePopBtn(tv_group);
                pos = 1;
                loadClass();
                break;
            case R.id.tv_person:
                if (ketang.size() > 1) {
                    Toast.makeText(this, "按个人布置时，只能选择一个课堂!", Toast.LENGTH_SHORT).show();
                    break;
                }
                changePopBtn(tv_person);
                pos = 2;
                loadClass();
                break;
            case R.id.tv_ketang:
//                iv_ketang.callOnClick();
                break;
            case R.id.btn_reset:
                // 当前时间
                Calendar startDate = Calendar.getInstance();
                // 指定日期时间格式
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                tv_start.setText(dateFormat.format(startDate.getTime()));
                tv_end.setText(getTomorrow2359(tv_start.getText().toString()));
                ketang.clear();
                isFirst = true;
                rb_homework1.setChecked(true);
                rb_assign1.setChecked(true);
                pos = 0;
                break;
            case R.id.btn_confirm:
                assignType = "2";
                submit();
                break;
        }
    }

    // 数据整理
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submit() {
        if (zouyeFlag == 1) {

            if (ketang.size() == 0 || (pos == 1 && group.size() == 0) || (pos == 2 && person.size() == 0)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请选择以上属性");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }

            String classGroupIds = "", classGroupNames = "";
            String ids = "", names = "";
            StringBuilder result = new StringBuilder();
            StringBuilder result2 = new StringBuilder();
            StringBuilder result3 = new StringBuilder();
            StringBuilder result4 = new StringBuilder();
            String leanType = "";
            switch (pos) {
                case 0:
                    ids = "";
                    names = "";
                    classGroupIds = "";
                    classGroupNames = "";
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
                            result.append(";"); // 在每个值之前添加逗号和空格
                        }
                        result.append(id);

                        if (result2.length() > 0) {
                            result2.append(";");
                        }
                        result2.append(name);

                        if (result3.length() > 0) {
                            result3.append(";");
                        }
                        result3.append(groupMap.get(key));

                        if (result4.length() > 0) {
                            result4.append(";");
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
                            result.append(","); // 在每个值之前添加逗号和空格
                        }
                        result.append(id);

                        if (result2.length() > 0) {
                            result2.append(",");
                        }
                        result2.append(name);
                    });
                    clas.forEach(key -> {
                        if (result3.length() > 0) {
                            result3.append(",");
                        }
                        result3.append(classMap.get(key));

                        if (result4.length() > 0) {
                            result4.append(",");
                        }
                        result4.append(key);
                    });
                    ids = result.toString();
                    names = result2.toString();
                    classGroupIds = result3.toString();
                    classGroupNames = result4.toString();
                    break;
            }

            result.setLength(0);
            // 处理课堂名和课堂id
            ketang.forEach(key -> {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(ketangMap.get(key));
            });
            String ketangIds = result.toString();
            String ketangName = String.join(",", ketang);

            submit(tv_start.getText().toString() + ":00", tv_end.getText().toString() + ":00", ketangName, ketangIds, classGroupNames, classGroupIds, assignType, ids, names, leanType, "save", zouyeType, zouyeFlag, "", "");
        } else {
            if (xiezuo == null || xiezuo.length() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请选择协作组");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }
            if (adapter.selectKTList.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("请选择布置范围");
                builder.setNegativeButton("关闭", null);
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                dialog.show();
                return;
            }

            StringBuilder keName = new StringBuilder();
            StringBuilder keId = new StringBuilder();


            for (int i = 0; i < adapter.selectKTList.size(); ++i) {
                HomeworkXieZuoEntity.Ketang kt = adapter.selectKTList.get(i);
                if (i != 0) {
                    keName.append(",");
                    keId.append(",");
                }
                String v = "";
                try {
                    v = URLEncoder.encode(kt.value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                keName.append(v);
                keId.append(kt.key);

            }

            submit(tv_start.getText().toString() + ":00", tv_end.getText().toString() + ":00", keName.toString(), keId.toString(), "", "", assignType, "", "", "70", "save", zouyeType, zouyeFlag, xiezuo, xiezuoMap.get(xiezuo));
        }
    }

    public void timePickerShow(Calendar startDate, Calendar setDate, TextView tv) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 2);
        // 时间选择器
        TimePickerView pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
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
            nowBtn.setTextColor(this.getColor(R.color.white));
            lastPopBtn.setBackgroundResource(0);
            lastPopBtn.setTextColor(this.getColor(R.color.default_gray));
            lastPopBtn = nowBtn;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadXieZuo() {
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ASSIGN_GET_XZ + "?teacherId=" + MyApplication.username;
        xiezuoMap.clear();
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject object = data.getJSONObject(i);
                    xiezuoMap.put(object.getString("value"), object.getString("key"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showXieZuo() {
        // 清空
        fl_ketang.removeAllViews();
        // 判断课堂是被否选择
        if (xiezuoMap.size() == 0) {
            tv_ketang_null.setVisibility(View.VISIBLE);
        } else {
            tv_ketang_null.setVisibility(View.GONE);
        }
        lastXieZuo = null;
        xiezuo = "";
        // 绘制课堂块
        xiezuoMap.forEach((name, id) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_ketang, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);

            // 点击
            tv_name.setOnClickListener(v -> {
                if (lastXieZuo != null && lastXieZuo == v) {
                    return;
                }
                if (lastXieZuo != null) {
                    unselectedTv(lastXieZuo);
                }
                selectedTv((TextView) v);
                lastXieZuo = (TextView) v;
                xiezuo = ((TextView) v).getText().toString();
                loadXZClass();
            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_ketang.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_ketang.addView(view);

        });
    }

    private void loadXZClass() {
        mRequestUrl = Constant.API + Constant.T_HOMEWORK_ASSIGN_GET_XZ_CLASS + "?roomId=" + xiezuoMap.get(xiezuo);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Gson gson = new Gson();
                //使用Gson框架转换Json字符串为列表
                List<HomeworkXieZuoEntity> itemList = gson.fromJson(itemString, new TypeToken<List<HomeworkXieZuoEntity>>() {
                }.getType());

                adapter.update(itemList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
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
                showKeTang(); // 第二步
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("volley", "Volley_Error: " + error.toString());

        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showKeTang() {
        // 清空
        fl_ketang.removeAllViews();
        // 判断课堂是被否选择
        if (ketangMap.size() == 0) {
            tv_ketang_null.setVisibility(View.VISIBLE);
        } else {
            tv_ketang_null.setVisibility(View.GONE);
        }
        lastKetang.clear();

        ketangMap.forEach((name, id) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_ketang, false);
            TextView tv_name = view.findViewById(R.id.tv_name);
            tv_name.setText(name);
            // 渲染说明渲染
            if (ketang.contains(name)) {
                selectedTv(tv_name);
                lastKetang.add(tv_name);
            }
            // 点击  第三步
            tv_name.setOnClickListener(v -> {
                switch (pos) {
                    case 0:
                        // 清除已选
                        if (ketang.contains(tv_name.getText().toString())) {
                            unselectedTv(tv_name);
                            ketang.remove(tv_name.getText().toString());
                            lastKetang.remove(tv_name);
                            // 判空
                            if (ketang.size() == 0) {
                                tv_class_null.setText("请先选择课堂");
                                tv_class_null.setVisibility(View.VISIBLE);
                            }
//                    fl_class.removeAllViews();
                        } else {
                            // 新选择
                            ketang.add(tv_name.getText().toString());
                            selectedTv(tv_name);
                            lastKetang.add(tv_name);
                            if (ketang.size() > 0) {
//                        tv_class_null.setText("请先选择课堂");
                                tv_class_null.setVisibility(View.GONE);
                            }
                        }
                        break;
                    case 1:
                    case 2:
                        // 清除已选
//                    fl_class.removeAllViews();
                        // 列表长度限制为1.
                        if (!ketang.contains(tv_name.getText().toString())) {
                            selectedTv(tv_name);

                            ketang.clear();
                            ketang.add(tv_name.getText().toString());
                            // 修改lastKetang组件
                            if (lastKetang.size() != 0) {
                                unselectedTv(lastKetang.get(0));
                                lastKetang.clear();
                            }
                            lastKetang.add(tv_name);
                            // 加载班级信息
                            loadClass(); // 第三步
                        }
                        break;

                }

            });
            ViewGroup.LayoutParams params = tv_name.getLayoutParams();
            params.width = fl_ketang.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
            tv_name.setLayoutParams(params);
            fl_ketang.addView(view);
            if (isFirst) {
                tv_name.callOnClick();
            }
        });
        isFirst = false;
    }

    /**
     * 集成三种发布方法为一体
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadClass() {
        clas.clear();       //记录班级选中项
        group.clear();      // 记录小组选中项
        person.clear();     // 记录个人选中项
        classMap.clear();   // 班级部分数据
        groupMap.clear();   // 小组部分数据
        personMap.clear();  // 个人部分数据
        for (int idx = 0; idx < ketang.size(); ++idx) {
            String ketang_name = ketang.get(idx);
            mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_KETANG_ITEM + "?keTangId=" + ketangMap.get(ketang_name);
            StringRequest request = new StringRequest(mRequestUrl, response -> {

                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);

                    JSONObject data = json.getJSONObject("data");
                    JSONArray classList = data.getJSONArray("classList");
                    for (int i = 0; i < classList.length(); ++i) {
                        JSONObject object = classList.getJSONObject(i);
                        // classMap表示班级名-》班级id
                        classMap.put(object.getString("value"), object.getString("id"));
                        clas.add(object.getString("value")); // 班级自动选中
                        // classMapStuNames表示班级名-》学生名【,】
                        classMapStuNames.put(object.getString("value"), object.getString("name"));
                        // classMapStuIds表示班级名-》学生id【,】
                        classMapStuIds.put(object.getString("value"), object.getString("ids"));
                        String[] idArray = object.getString("ids").split(",");
                        String[] nameArray = object.getString("name").split(",");
                        // personMap表示该班级的学生名-》学生id
                        for (int j = 0; j < idArray.length; j++) {
                            personMap.put(nameArray[j], idArray[j]);
                        }
                    }

                    JSONArray groupList = data.getJSONArray("groupList");

                    for (int i = 0; i < groupList.length(); ++i) {
                        JSONObject object = groupList.getJSONObject(i);
                        // 表示小组名-》班级id
                        groupMap.put(object.getString("value"), object.getString("id"));
                        // 表示小组名-》学生名【,】
                        groupMapStuNames.put(object.getString("value"), object.getString("name"));
                        // 表示小组名-》学生id【,】
                        groupMapStuIds.put(object.getString("value"), object.getString("ids"));
                    }

                    showClass(); // 第四步

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.d("wen", "Volley_Error: " + error.toString());
            });
            MyApplication.addRequest(request, TAG);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showClass() {
        fl_class.removeAllViews();
        if (ketang.size() == 0) {
            tv_class_null.setText("请先选择课堂");
            tv_class_null.setVisibility(View.VISIBLE);
            return;
        }

        // 判断1：是否为空？
        switch (pos) {
            case 0:
                tv_class_null.setVisibility(View.GONE);
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

        // 判断2：非空渲染按钮
        switch (pos) {
            case 0:
//                classMap.forEach((name, id) -> {
//                    View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_class, false);
//                    TextView tv_name = view.findViewById(R.id.tv_name);
//                    tv_name.setText(name);
//                    if (clas.contains(name)) {
//                        tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
//                    }
//                    tv_name.setOnClickListener(v -> {
//                        if (clas.contains(tv_name.getText().toString())) {
//                            tv_name.setBackgroundResource(R.color.light_gray3);
//                            clas.remove(tv_name.getText().toString());
//                        } else {
//                            tv_name.setBackgroundResource(R.drawable.t_homework_add_select);
//                            clas.add(tv_name.getText().toString());
//                        }
//                    });
//                    ViewGroup.LayoutParams params = tv_name.getLayoutParams();
//                    params.width = fl_class.getWidth() / 3 - PxUtils.dip2px(view.getContext(), 15);
//                    tv_name.setLayoutParams(params);
//                    fl_class.addView(view);
//                });
                break;
            case 1:
                groupMap.forEach((name, id) -> {
                    View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_class, false);
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
                    params.width = fl_class.getWidth() / 4 - PxUtils.dip2px(view.getContext(), 15);
                    tv_name.setLayoutParams(params);
                    fl_class.addView(view);
                });
                break;
            case 2:
                personMap.forEach((name, id) -> {
                    View view = LayoutInflater.from(this).inflate(R.layout.item_t_homework_add_block, fl_class, false);
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
                    params.width = fl_class.getWidth() / 5 - PxUtils.dip2px(view.getContext(), 15);
                    tv_name.setLayoutParams(params);
                    fl_class.addView(view);
                });
                break;
        }
    }

    // 最终提交
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void submit(String startTime, String endTime, String ketang, String ketangId, String clas, String classId, String assignType, String stuIds, String stuNames, String learnType, String flag, int zouyeType, int zouyeFlag, String xiezuozuId, String xiezuozuName) {
        if (zouyeFlag == 1) {
            String lpn = "";
            String json_zh = "";

            if (type.equals("paper")) {
                // 试卷类型
                try {
                    ketang = URLEncoder.encode(ketang, "UTF-8");
                    clas = URLEncoder.encode(clas, "UTF-8");
                    stuNames = URLEncoder.encode(stuNames, "UTF-8");
                    lpn = URLEncoder.encode(learnPlanName, "UTF-8");
                    json_zh = URLEncoder.encode(jsonString, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (typeCamera != null) {
                    mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_ASSIGN + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" +
                            "&subjectCode=" + "&subjectName=" +
                            "&textBookCode=" + "&textBookName=" +
                            "&gradeLevelCode=" + "&gradeLevelName=" +
                            "&pointCode=" + "&introduction=" +
                            "&userName=" + MyApplication.username + "&paperName=" + lpn +
                            "&paperId=" + learnPlanId + "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classOrGroupId=" + classId +
                            "&classOrGroupName=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&learnType=" + learnType + "&flag=" + flag + "&jsonStr=";

                } else {
                    mRequestUrl = Constant.API + Constant.T_HOMEWORK_ASSIGN_SAVE + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" +
                            "&subjectCode=" + "&subjectName=" +
                            "&textBookCode=" + "&textBookName=" +
                            "&gradeLevelCode=" + "&gradeLevelName=" +
                            "&pointCode=" + "&introduction=" +
                            "&userName=" + MyApplication.username + "&paperName=" + lpn +
                            "&paperId=" + learnPlanId + "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classOrGroupId=" + classId +
                            "&classOrGroupName=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&learnType=" + learnType + "&flag=" + flag + "&jsonStr=";

                }

                StringRequest request = new StringRequest(mRequestUrl, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        boolean success = json.getBoolean("success");
                        String msg = json.getString("message");


                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(learnPlanName);
                        if (success) {
                            builder.setMessage("作业布置成功");

                        } else {
                            builder.setMessage(msg);
                        }
                        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rl_submitting.setVisibility(View.GONE);
                                Intent toHome = new Intent(TTeachAssginActivity.this, TMainPagerActivity.class);
                                //两个一起用
                                toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(toHome);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);
                rl_submitting.setVisibility(View.VISIBLE);

            } else {
                // 其他类型
                String xueduan = "";
                String xueke = "";
                String banben = "";
                String jiaocai = "";
                String zhishidian = "";

                // 导学案专属参数
                String learnPlanType = "";
                String classHours = "";
                String studyHours = "";
                String Introduce = "";
                String Goal = "";
                String Emphasis = "";
                String Difficulty = "";
                String Extension = "";
                String Summary = "";

                Log.d("wen", "内容串: " + jsonString);

                try {
                    ketang = URLEncoder.encode(ketang, "UTF-8");
                    clas = URLEncoder.encode(clas, "UTF-8");
                    stuNames = URLEncoder.encode(stuNames, "UTF-8");
                    lpn = URLEncoder.encode(learnPlanName, "UTF-8");
                    Introduce = URLEncoder.encode(Introduce, "UTF-8");
                    Goal = URLEncoder.encode(Goal, "UTF-8");
                    Emphasis = URLEncoder.encode(Emphasis, "UTF-8");
                    Difficulty = URLEncoder.encode(Difficulty, "UTF-8");
                    Extension = URLEncoder.encode(Extension, "UTF-8");

                    mRequestUrl = Constant.API + Constant.T_LEARN_PLAN_ASSIGN_SAVE + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") +
                            "&subjectCode=" + "&subjectName=" + URLEncoder.encode(xueke, "UTF-8") +
                            "&textBookCode=" + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") +
                            "&gradeLevelCode=" + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8") +
                            "&pointCode=" + "&pointName=" + URLEncoder.encode(zhishidian, "UTF-8") +

                            "&type=" + type + "&learnPlanType=" + learnPlanType + "&classHours=" + classHours +
                            "&studyHours=" + studyHours + "&Introduce=" + Introduce + "&Goal=" + Goal +
                            "&Emphasis=" + Emphasis + "&Difficulty=" + Difficulty + "&Summary=" + Summary + "&Extension=" + Extension +

                            "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classIds=" + classId +
                            "&className=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&roomType=" + learnType +

                            "&userName=" + MyApplication.username + "&learnPlanId=" + learnPlanId +
                            "&learnPlanName=" + lpn + "&flag=" + flag + "&jsonStr=";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("wen", "URL: " + mRequestUrl);

                StringRequest request = new StringRequest(mRequestUrl, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        Log.d(TAG, "submit: " + json);
                        boolean success = json.getBoolean("success");
                        String msg = json.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(learnPlanName);

                        if (success) {
                            if (type.equals("1")) {
                                builder.setMessage("导学案布置成功");
                            } else {
                                builder.setMessage("微课布置成功");
                            }
                        } else {
                            builder.setMessage(msg);
                        }
                        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rl_submitting.setVisibility(View.GONE);
                                Intent toHome = new Intent(TTeachAssginActivity.this, TMainPagerActivity.class);
                                //两个一起用
                                toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                startActivity(toHome);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);
                rl_submitting.setVisibility(View.VISIBLE);
            }

            try {
                // 休眠2秒钟，避免请求过快被丢弃
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            // 新的协作组方式
            String lpn = "";
            String json_zh = "";
            if (type.equals("paper")) {
                // 试卷类型
                try {
                    ketang = URLEncoder.encode(ketang, "UTF-8");
                    clas = URLEncoder.encode(clas, "UTF-8");
                    stuNames = URLEncoder.encode(stuNames, "UTF-8");
                    lpn = URLEncoder.encode(learnPlanName, "UTF-8");
                    json_zh = URLEncoder.encode(jsonString, "UTF-8");
                    xiezuozuName = URLEncoder.encode(xiezuozuName, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                if (typeCamera != null) {
                    mRequestUrl = Constant.API + Constant.T_HOMEWORK_CAMERA_ASSIGN + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" +
                            "&subjectCode=" + "&subjectName=" +
                            "&textBookCode=" + "&textBookName=" +
                            "&gradeLevelCode=" + "&gradeLevelName=" +
                            "&pointCode=" + "&introduction=" +
                            "&userName=" + MyApplication.username + "&paperName=" + lpn +
                            "&paperId=" + learnPlanId + "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classOrGroupId=" + classId +
                            "&classOrGroupName=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&learnType=" + learnType + "&flag=" + flag + "&jsonStr=" + "&zouyeType=" + zouyeType + "&zouyeFlag=" + zouyeFlag + "&xiezuozuId=" + xiezuozuId + "&xiezuozuName=" + xiezuozuName;

                } else {
                    mRequestUrl = Constant.API + Constant.T_HOMEWORK_ASSIGN_SAVE + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" +
                            "&subjectCode=" + "&subjectName=" +
                            "&textBookCode=" + "&textBookName=" +
                            "&gradeLevelCode=" + "&gradeLevelName=" +
                            "&pointCode=" + "&introduction=" +
                            "&userName=" + MyApplication.username + "&paperName=" + lpn +
                            "&paperId=" + learnPlanId + "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classOrGroupId=" + classId +
                            "&classOrGroupName=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&learnType=" + learnType + "&flag=" + flag + "&jsonStr=" + "&zouyeType=" + zouyeType + "&zouyeFlag=" + zouyeFlag + "&xiezuozuId=" + xiezuozuId + "&xiezuozuName=" + xiezuozuName;
                }

                StringRequest request = new StringRequest(mRequestUrl, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        boolean success = json.getBoolean("success");
                        String msg = json.getString("message");

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(learnPlanName);
                        if (success) {
                            builder.setMessage("作业布置成功");

                        } else {
                            builder.setMessage(msg);
                        }
                        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rl_submitting.setVisibility(View.GONE);
                                Intent toHome = new Intent(TTeachAssginActivity.this, TMainPagerActivity.class);
                                //两个一起用
                                toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(toHome);
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);
                rl_submitting.setVisibility(View.VISIBLE);

            } else {
                // 其他类型
                String xueduan = "";
                String xueke = "";
                String banben = "";
                String jiaocai = "";
                String zhishidian = "";

                // 导学案专属参数
                String learnPlanType = "";
                String classHours = "";
                String studyHours = "";
                String Introduce = "";
                String Goal = "";
                String Emphasis = "";
                String Difficulty = "";
                String Extension = "";
                String Summary = "";

                Log.d("wen", "内容串: " + jsonString);

                try {
                    lpn = URLEncoder.encode(learnPlanName, "UTF-8");
                    Introduce = URLEncoder.encode(Introduce, "UTF-8");
                    Goal = URLEncoder.encode(Goal, "UTF-8");
                    Emphasis = URLEncoder.encode(Emphasis, "UTF-8");
                    Difficulty = URLEncoder.encode(Difficulty, "UTF-8");
                    Extension = URLEncoder.encode(Extension, "UTF-8");

                    mRequestUrl = Constant.API + Constant.T_LEARN_PLAN_ASSIGN_SAVE + "?assignType=" + assignType +
                            "&channelCode=" + "&channelName=" + URLEncoder.encode(xueduan, "UTF-8") +
                            "&subjectCode=" + "&subjectName=" + URLEncoder.encode(xueke, "UTF-8") +
                            "&textBookCode=" + "&textBookName=" + URLEncoder.encode(banben, "UTF-8") +
                            "&gradeLevelCode=" + "&gradeLevelName=" + URLEncoder.encode(jiaocai, "UTF-8") +
                            "&pointCode=" + "&pointName=" + URLEncoder.encode(zhishidian, "UTF-8") +

                            "&type=" + type + "&learnPlanType=" + learnPlanType + "&classHours=" + classHours +
                            "&studyHours=" + studyHours + "&Introduce=" + Introduce + "&Goal=" + Goal +
                            "&Emphasis=" + Emphasis + "&Difficulty=" + Difficulty + "&Summary=" + Summary + "&Extension=" + Extension +

                            "&startTime=" + startTime + "&endTime=" + endTime +
                            "&keTangId=" + ketangId + "&keTangName=" + ketang + "&classIds=" + classId +
                            "&className=" + clas + "&stuIds=" + stuIds + "&stuNames=" + stuNames +
                            "&roomType=" + learnType +

                            "&userName=" + MyApplication.username + "&learnPlanId=" + learnPlanId +
                            "&learnPlanName=" + lpn + "&flag=" + flag + "&jsonStr=" + "&zouyeType=" + zouyeType + "&learnPlanFlag=" + zouyeFlag + "&xiezuozuId=" + xiezuozuId + "&xiezuozuName=" + xiezuozuName;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("wen", "URL: " + mRequestUrl);

                StringRequest request = new StringRequest(mRequestUrl, response -> {
                    try {
                        JSONObject json = JsonUtils.getJsonObjectFromString(response);
                        Log.d(TAG, "submit: " + json);
                        boolean success = json.getBoolean("success");
                        String msg = json.getString("message");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle(learnPlanName);

                        if (success) {
                            if (type.equals("1")) {
                                builder.setMessage("导学案布置成功");
                            } else {
                                builder.setMessage("微课布置成功");
                            }
                        } else {
                            builder.setMessage(msg);
                        }
                        builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                rl_submitting.setVisibility(View.GONE);
                                Intent toHome = new Intent(TTeachAssginActivity.this, TMainPagerActivity.class);
                                //两个一起用
                                toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                startActivity(toHome);
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.setCanceledOnTouchOutside(false); // 防止用户点击对话框外部关闭对话框
                        dialog.show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.d("wen", "Volley_Error: " + error.toString());
                });
                MyApplication.addRequest(request, TAG);
                rl_submitting.setVisibility(View.VISIBLE);
            }

            try {
                // 休眠2秒钟，避免请求过快被丢弃
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    // 获取选题信息
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadPickList() {
        if (type.equals("paper")) {
            // 获取试卷内容
            mRequestUrl = Constant.API + Constant.T_GET_PAPER_PICKLIST + "?paperId=" + learnPlanId;

            StringRequest request = new StringRequest(mRequestUrl, response -> {
                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);

                    jsonString = json.getString("data");
                    Log.d("wen", "loadPickList: " + jsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.d("wen", "Volley_Error: " + error.toString());
            });
            MyApplication.addRequest(request, TAG);
        } else {
            mRequestUrl = Constant.API + Constant.T_GET_LEARN_PLAN_PICKLIST + "?learnPlanId=" + learnPlanId + "&deviceType=PAD";

            StringRequest request = new StringRequest(mRequestUrl, response -> {
                try {
                    JSONObject json = JsonUtils.getJsonObjectFromString(response);

                    jsonString = json.getString("data");
                    Log.d("wen", "loadPickList: " + jsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.d("wen", "Volley_Error: " + error.toString());
            });
            MyApplication.addRequest(request, TAG);
        }
    }

    private void selectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_select);
        tv.setTextColor(getColor(R.color.red));
    }

    private void unselectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_unselect);
        tv.setTextColor(getColor(R.color.default_gray));
    }
}
