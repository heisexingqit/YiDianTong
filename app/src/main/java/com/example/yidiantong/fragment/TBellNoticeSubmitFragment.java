package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.Manager.CustomDatePicker;
import com.example.yidiantong.Manager.DatePicker;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.ui.TMainPagerActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.DateFormatUtils;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TBellNoticeSubmitFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "TBellNoticeSubmitFragme";

    private EditText fet_bell_name;
    private TextView ftv_bell_class_name;
    private TextView ftv_bellclass_null;
    private FlexboxLayout ffl_bellclass;
    private RadioButton frb_bell_now;
    private RadioButton frb_bell_scheduled;
    private RadioGroup frg_bell;
    private LinearLayout fll_bell_time;
    private String mRequestUrl;
    private TextView fev_bell_time;

    private Map<String, String> classMap = new HashMap<>();
    private View view;
    private List<String> bellclass = new ArrayList<>();
    private TextView lastbellclass;

    // 日期和时间
    private TextView mTvSelectedDate, mTvSelectedTime;
    private CustomDatePicker mDatePicker;
    private DatePicker mTimerPicker;
    private String endTime;
    private String endDatestamp;
    private String newDate;

    // 判断内容在那里为空，-1均不为空，0标题，1班级，2内容
    private int nullmode = -1;
    // 判断对象，0全部，1全部老师，2全部学生
    private int all_tea_stu = -1;
    // 1为选中，2为未选中
    private int[] allmode = {2, 2};
    // 判断时间，0为即时，1为定时
    private int timemode = 1;
    private EditText fet_bell_content;
    private String mRequestUrl1;
    private String setDate = null;
    private Button fb_bell_cancle;
    private Button fb_bell_confirm;
    private RelativeLayout frl_bell_0;
    private RelativeLayout frl_bell_1;

    public TBellNoticeSubmitFragment() {
        // Required empty public constructor
    }

    public static TBellNoticeSubmitFragment newInstance() {
        TBellNoticeSubmitFragment fragment = new TBellNoticeSubmitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_bell_notice_submit, container, false);

        fet_bell_name = view.findViewById(R.id.fet_bell_name);
        ftv_bell_class_name = view.findViewById(R.id.ftv_bell_class_name);
        ftv_bellclass_null = view.findViewById(R.id.ftv_bellclass_null);
        ffl_bellclass = view.findViewById(R.id.ffl_bellclass);

        frg_bell = view.findViewById(R.id.frg_bell);
        frb_bell_now = view.findViewById(R.id.frb_bell_now);
        frb_bell_scheduled = view.findViewById(R.id.frb_bell_scheduled);
        fev_bell_time = view.findViewById(R.id.fev_bell_time);
        fll_bell_time = view.findViewById(R.id.fll_bell_time);
        frg_bell.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.frb_bell_now:
                        frb_bell_now.setButtonDrawable(R.drawable.bell_time_select);
                        frb_bell_scheduled.setButtonDrawable(R.drawable.bell_time_unselect);
                        fll_bell_time.setBackgroundResource(R.drawable.bell_time_unfocus);
                        fev_bell_time.setText("");
                        fev_bell_time.setClickable(false);
                        timemode = 1;
                        break;
                    case R.id.frb_bell_scheduled:
                        frb_bell_now.setButtonDrawable(R.drawable.bell_time_unselect);
                        frb_bell_scheduled.setButtonDrawable(R.drawable.bell_time_select);
                        fll_bell_time.setBackgroundResource(R.drawable.bell_time_focus);
                        fev_bell_time.setClickable(true);
                        timemode = 2;
                        textclick();
                        break;
                }
            }
        });

        loadClass();

        fet_bell_content = view.findViewById(R.id.fet_bell_content);

        frl_bell_0 = getActivity().findViewById(R.id.frl_bell_0);
        frl_bell_0.setVisibility(View.GONE);
        frl_bell_1 = getActivity().findViewById(R.id.frl_bell_1);
        frl_bell_1.setVisibility(View.VISIBLE);
        fb_bell_cancle = getActivity().findViewById(R.id.fb_bell_cancle);
        fb_bell_confirm = getActivity().findViewById(R.id.fb_bell_confirm);
        fb_bell_cancle.setOnClickListener(this);
        fb_bell_confirm.setOnClickListener(this);

        return view;
    }

    // 初始化日期和时间，创建定时发布点击事件
    private void textclick() {
        initDatePicker();
        initTimerPicker();
        fev_bell_time.setOnClickListener(this);
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            showClass((Map<String, String>) message.obj);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadClass() {

        mRequestUrl = Constant.API + Constant.T_BELL_ADD_CLASS + "?userName=" + MyApplication.username;
        Log.d("", "loadclass: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONArray data = json.getJSONArray("data");
                for (int i = data.length() - 1; i >= 0; i--) {
                    JSONObject object = data.getJSONObject(i);
                    classMap.put(object.getString("name"), object.getString("id"));
                }

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = classMap;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showClass(Map<String, String> obj) {

        classMap = obj;

        if (classMap.size() == 0) {
            ftv_bellclass_null.setVisibility(View.VISIBLE);
        }
        classMap.forEach((name, id) -> {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_homework_add_block, ffl_bellclass, false);
            TextView ftv_bell_class = view.findViewById(R.id.tv_name);
            ftv_bell_class.setText(name);
            if (bellclass.contains(name)) {
                selectedTv(ftv_bell_class);
            }
            ftv_bell_class.setOnClickListener(v -> {
                String className = (String) ftv_bell_class.getText().toString();

                if (bellclass.contains(className)) {
                    bellclass.remove(className);
                    unselectedTv(ftv_bell_class);
                } else {
                    bellclass.add(className);
                    selectedTv(ftv_bell_class);
                }

                ftv_bell_class_name.setText(String.join(",", bellclass));

            });
            ViewGroup.LayoutParams params = ftv_bell_class.getLayoutParams();
            params.width = ffl_bellclass.getWidth() / 2 - PxUtils.dip2px(view.getContext(), 15);
            ftv_bell_class.setLayoutParams(params);
            ffl_bellclass.addView(view);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fev_bell_time:
                // 日期和时间弹框
                //mTimerPicker.show(endTime);
                mDatePicker.show(endDatestamp);
                break;
            case R.id.fb_bell_cancle:
                getActivity().finish();
                break;
            case R.id.fb_bell_confirm:

                //判断信息是否为空
                if (fet_bell_name.length() == 0) {
                    nullmode = 0;
                } else if (ftv_bell_class_name.length() == 0) {
                    nullmode = 1;
                } else if (fet_bell_content.length() == 0) {
                    nullmode = 2;
                } else if (timemode == 2 && fev_bell_time.length() == 0) {
                    nullmode = 3;
                } else {
                    nullmode = -1;
                }
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                //自定义title样式
                TextView tv = new TextView(getActivity());
                if (nullmode == 0) {
                    tv.setText("请输入标题！");    //内容
                } else if (nullmode == 1) {
                    tv.setText("请选择班级！");    //内容
                } else if (nullmode == 2) {
                    tv.setText("请先输入内容！");    //内容
                } else if (nullmode == 3) {
                    tv.setText("请设置定时发布时间！");
                }
//                else {
//                    tv.setText("发布成功");    //内容
//                }
                tv.setTextSize(17);//字体大小
                tv.setPadding(30, 40, 30, 40);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv);
                //禁止返回和外部点击
                builder.setCancelable(false);
                if (nullmode != -1) {
                    builder.setNegativeButton("确定", null);
                    //对话框弹出
                    builder.show();
                } else {
                    submit();
                }
                break;
        }
    }

    private void gotolast() {
        Intent toHome = new Intent(getActivity(), TMainPagerActivity.class);
        //两个一起用
        toHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(toHome);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submit() {

        String className = null;
        String content = null;
        String title = null;
        String userCn = null;
        try {
            className = URLEncoder.encode(ftv_bell_class_name.getText().toString(), "UTF-8");
            content = URLEncoder.encode(fet_bell_content.getText().toString(), "UTF-8");
            title = URLEncoder.encode(fet_bell_name.getText().toString(), "UTF-8");
            userCn = URLEncoder.encode(MyApplication.cnName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Integer setDateFlag = timemode;
        if (setDateFlag == 2) {
            setDate = fev_bell_time.getText().toString();
        }
        StringBuilder result = new StringBuilder();
        bellclass.forEach(item -> {
            if (result.length() > 0) {
                result.append(",");
            }
            result.append(classMap.get(item));
        });


        mRequestUrl1 = Constant.API + Constant.T_BELL_SAVE_MANAGE_NOTICE + "?classId=" + result + "&className=" + className + "&userName=" + MyApplication.username + "&userCN=" + userCn + "&content=" + content + "&title=" + title + "&setDateFlag=" + setDateFlag + "&setDate=" + setDate + "&saveOrUpdate=" + "save";
        Log.e("0110", "submit: " + mRequestUrl1);
        StringRequest request = new StringRequest(mRequestUrl1, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                if (isSuccess) {
                    builder.setTitle("发布成功");
                } else {
                    builder.setTitle("发布失败，请稍后重试");
                }
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotolast();
                    }
                });
                //禁止返回和外部点击
                builder.setCancelable(false);
                builder.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d("", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    private void initDatePicker() {
        long beginTimestamp = DateFormatUtils.str2Long("2099-01-01", false);
        long endTimestamp = System.currentTimeMillis();
        endDatestamp = DateFormatUtils.long2Str(endTimestamp, false);
        //fev_bell_time.setText(endDatestamp);

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = new CustomDatePicker(getActivity(), new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                newDate = DateFormatUtils.long2Str(timestamp, false);
                mTimerPicker.show(System.currentTimeMillis());
            }
        }, endTimestamp, beginTimestamp);
        // 允许点击屏幕或物理返回键关闭
        mDatePicker.setCancelable(true);

        // 允许循环滚动
        mDatePicker.setScrollLoop(true);
        // 允许滚动动画
        mDatePicker.setCanShowAnim(true);
    }

    private void initTimerPicker() {

        long beginTimestamp = DateFormatUtils.str2Long("2009-01-01 00:00:00", true);
        long endTimestamp = System.currentTimeMillis();

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new DatePicker(getActivity(), new DatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                String newtime = DateFormatUtils.long2Str(timestamp, true).substring(0, 5);
                fev_bell_time.setText(newDate + " " + newtime + ":00");
            }
        }, beginTimestamp, endTimestamp);
        // 允许点击屏幕或物理返回键关闭
        mTimerPicker.setCancelable(true);
        // 显示时和分
        mTimerPicker.setCanShowPreciseTime(true);
        // 允许循环滚动
        mTimerPicker.setScrollLoop(true);
        // 允许滚动动画
        mTimerPicker.setCanShowAnim(true);
    }

    private void selectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_select);
        tv.setTextColor(getActivity().getColor(R.color.red));
    }

    private void unselectedTv(TextView tv) {
        tv.setBackgroundResource(R.drawable.t_homework_add_unselect);
        tv.setTextColor(getActivity().getColor(R.color.default_gray));
    }
}