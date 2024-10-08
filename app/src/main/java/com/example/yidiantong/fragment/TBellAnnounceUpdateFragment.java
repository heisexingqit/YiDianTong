package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.Manager.CustomDatePicker;
import com.example.yidiantong.Manager.DatePicker;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookDetailEntity;
import com.example.yidiantong.bean.TBellNoticeEntity;
import com.example.yidiantong.bean.TBellUpdateEntity;
import com.example.yidiantong.ui.TMainPagerActivity;
import com.example.yidiantong.ui.TTeachAssginActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.DateFormatUtils;
import com.example.yidiantong.util.JsonUtils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TBellAnnounceUpdateFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final Object TAG = "TBellAnnounceSubmitFragment";
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

    // 日期和时间
    private TextView mTvSelectedDate, mTvSelectedTime;
    private CustomDatePicker mDatePicker;
    private DatePicker mTimerPicker;
    private String endTime;
    private String endDatestamp;
    private String newDate;
    private CheckBox fcb_bd_tea;
    private CheckBox fcb_bd_stu;
    private Button fb_bell_cancle;
    private Button fb_bell_confirm;

    // 判断内容在那里为空，-1均不为空，0标题，1班级，2内容
    private int nullmode = -1;
    // 判断对象，0全部，1全部老师，2全部学生
    private int all_tea_stu = -1;
    // 1为选中，2为未选中
    private int[] allmode = {0, 0};
    // 判断时间，1为即时，2为定时
    private int timemode = 1;
    private TextInputEditText fet_bell_content;
    private String mRequestUrl1;
    private String setDate = null;
    private RelativeLayout frl_bell_0;
    private RelativeLayout frl_bell_1;
    private String setDate1;

    public TBellAnnounceUpdateFragment() {
        // Required empty public constructor
    }

    public static TBellAnnounceUpdateFragment newInstance() {
        TBellAnnounceUpdateFragment fragment = new TBellAnnounceUpdateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t_bell_announce_update, container, false);
        fet_bell_name = view.findViewById(R.id.fet_bell_name);
        ftv_bell_class_name = view.findViewById(R.id.ftv_bell_class_name);
        ftv_bellclass_null = view.findViewById(R.id.ftv_bellclass_null);
        ffl_bellclass = view.findViewById(R.id.ffl_bellclass);

        frg_bell = view.findViewById(R.id.frg_bell);
        frb_bell_now = view.findViewById(R.id.frb_bell_now);
        frb_bell_scheduled = view.findViewById(R.id.frb_bell_scheduled);
        fev_bell_time = view.findViewById(R.id.fev_bell_time);
        fll_bell_time = view.findViewById(R.id.fll_bell_time);

        fcb_bd_tea = view.findViewById(R.id.fcb_bd_tea);
        fcb_bd_stu = view.findViewById(R.id.fcb_bd_stu);
        fet_bell_content = view.findViewById(R.id.fet_bell_content);

        // 确认/取消按钮
        frl_bell_0 = getActivity().findViewById(R.id.frl_bell_0);
        frl_bell_0.setVisibility(View.VISIBLE);
        frl_bell_1 = getActivity().findViewById(R.id.frl_bell_1);
        frl_bell_1.setVisibility(View.GONE);
        fb_bell_cancle = getActivity().findViewById(R.id.fb_bell_cancle);
        fb_bell_confirm = getActivity().findViewById(R.id.fb_bell_confirm);
        fb_bell_cancle.setOnClickListener(this);

        fb_bell_confirm.setOnClickListener(this);

        // 初始化
        loadItems_Net();

        // 设置单选时间按钮监听
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

        // 设置复选对象按钮监听
        fcb_bd_tea.setOnCheckedChangeListener(this);
        fcb_bd_stu.setOnCheckedChangeListener(this);

        return view;
    }


    private List<TBellUpdateEntity.TBellAnnounceUpdateEntity> tBellAnnounceUpdateEntity;
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                tBellAnnounceUpdateEntity = (List<TBellUpdateEntity.TBellAnnounceUpdateEntity>) message.obj;
                showView(tBellAnnounceUpdateEntity);
            }
        }
    };

    private void showView(List<TBellUpdateEntity.TBellAnnounceUpdateEntity> tBellAnnounceUpdateEntity) {
        Log.e("daozheli", "");
        Log.e("", "" + tBellAnnounceUpdateEntity.get(0).getTitle());
        fet_bell_name.setText(tBellAnnounceUpdateEntity.get(0).getTitle());
        int flag = tBellAnnounceUpdateEntity.get(0).getType();
        if (flag == 0) {
            allmode[0] = 1;
            allmode[1] = 1;
            fcb_bd_tea.setButtonDrawable(R.drawable.muti_select);
            fcb_bd_stu.setButtonDrawable(R.drawable.muti_select);
        } else if (flag == 1) {
            allmode[0] = 1;
            allmode[1] = 2;
            fcb_bd_tea.setButtonDrawable(R.drawable.muti_select);
        } else {
            allmode[0] = 2;
            allmode[1] = 1;
            fcb_bd_stu.setButtonDrawable(R.drawable.muti_select);
        }
        setDate1 = tBellAnnounceUpdateEntity.get(0).getSetDate();
        if (setDate1.equals("null")) {
            timemode = 1;
            frb_bell_now.setButtonDrawable(R.drawable.bell_time_select);
        } else {
            timemode = 2;
            frb_bell_now.setButtonDrawable(R.drawable.bell_time_unselect);
            frb_bell_scheduled.setButtonDrawable(R.drawable.bell_time_select);
            fev_bell_time.setText(setDate1);
            fll_bell_time.setBackgroundResource(R.drawable.bell_time_focus);
            fev_bell_time.setClickable(true);
            textclick();
        }
        fet_bell_content.setText(tBellAnnounceUpdateEntity.get(0).getContent());
    }

    private void loadItems_Net() {
        String noticeId = getActivity().getIntent().getStringExtra("noticeId");
        String mRequestUrl = Constant.API + Constant.T_BELL_GET_NOTICE_INFO + "?noticeId=" + noticeId + "&type=4";
        Log.d("", "getinfo: " + mRequestUrl);

        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String data = json.getString("data");
                String data1 = "[" + data + "]";
                Log.e("data2", "" + data1);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<TBellUpdateEntity.TBellAnnounceUpdateEntity> updateList = gson.fromJson(data1, new TypeToken<List<TBellUpdateEntity.TBellAnnounceUpdateEntity>>() {
                }.getType());
                Log.e("updateList", "" + updateList);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = updateList;

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

    // 初始化日期和时间，创建定时发布点击事件
    private void textclick() {
        initDatePicker();
        initTimerPicker();
        fev_bell_time.setOnClickListener(this);
    }

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
                Log.e("在公告中的", "");
                // 判断对象
                if (allmode[0] == 1 && allmode[1] == 1) {
                    all_tea_stu = 0;
                } else if (allmode[0] == 1 && allmode[1] == 2) {
                    all_tea_stu = 1;
                } else if (allmode[0] == 2 && allmode[1] == 1) {
                    all_tea_stu = 2;
                } else {
                    all_tea_stu = -1;
                }
                Log.e("all_tea_stu", "" + all_tea_stu);

                // 判断信息是否为空
                if (fet_bell_name.length() == 0) {
                    nullmode = 0;
                } else if (all_tea_stu == -1) {
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
                    tv.setText("请选择发布对象！");    //内容
                } else if (nullmode == 2) {
                    tv.setText("请先输入内容！");    //内容
                } else if (nullmode == 3) {
                    tv.setText("请设置定时发布时间！");
                }
//                }else {
//                    tv.setText("发表成功");    //内容
//                }

                tv.setTextSize(17);//字体大小
                tv.setPadding(30, 40, 30, 40);//位置
                tv.setTextColor(Color.parseColor("#000000"));//颜色
                //设置title组件
                builder.setCustomTitle(tv);
                //禁止返回和外部点击
                builder.setCancelable(false);
                if(nullmode != -1){
                    builder.setNegativeButton("确定", null);
                    //对话框弹出
                    builder.show();
                }else{
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

    private void submit() {
        String content = fet_bell_content.getText().toString();
        String title = fet_bell_name.getText().toString();
        Integer type = all_tea_stu;
        Integer setDateFlag = timemode;
        String noticeId = getActivity().getIntent().getStringExtra("noticeId");
        if (setDateFlag == 2) {
            setDate = fev_bell_time.getText().toString();
        }

        mRequestUrl1 = Constant.API + Constant.T_BELL_SAVE_MANAGE_ANN + "?userName=" + MyApplication.username + "&userCN=" + MyApplication.cnName + "&content=" + content + "&title=" + title + "&type=" + type + "&setDateFlag=" + setDateFlag + "&setDate=" + setDate + "&saveOrUpdate=" + "update" + "&noticeId=" + noticeId;
        Log.d("", "submit: " + mRequestUrl1);

        StringRequest request = new StringRequest(mRequestUrl1, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                //结果信息
                Boolean isSuccess = json.getBoolean("success");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                if (isSuccess) {
                    builder.setTitle("修改成功");
                } else {
                    builder.setTitle("修改失败，请稍后重试");
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
        Log.e("时间", "" + DateFormatUtils.long2Str(endTimestamp, false));
        //fev_bell_time.setText(endDatestamp);

        // 通过时间戳初始化日期，毫秒级别
        mDatePicker = new CustomDatePicker(getActivity(), new CustomDatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                newDate = DateFormatUtils.long2Str(timestamp, false);
                mTimerPicker.show(endTime);
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
        long endTimestamp = DateFormatUtils.str2Long(endTime, true);

        // 通过日期字符串初始化日期，格式请用：yyyy-MM-dd HH:mm
        mTimerPicker = new DatePicker(getActivity(), new DatePicker.Callback() {
            @Override
            public void onTimeSelected(long timestamp) {
                String newtime = DateFormatUtils.long2Str(timestamp, true).substring(0, 5);
                fev_bell_time.setText(newDate + " " + newtime);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.e("点击了", "");
        if (buttonView.isChecked()) {

            if (buttonView == fcb_bd_tea) {
                if (allmode[0] == 1) {
                    fcb_bd_tea.setButtonDrawable(R.drawable.muti_unselect);
                    allmode[0] = 2;
                    Log.e("点教师变白", "");
                } else {
                    fcb_bd_tea.setButtonDrawable(R.drawable.muti_select);
                    allmode[0] = 1;
                    Log.e("点教师变蓝", "");
                }
            } else {
                if (allmode[1] == 1) {
                    fcb_bd_stu.setButtonDrawable(R.drawable.muti_unselect);
                    allmode[1] = 2;
                    Log.e("点学生变白", "");
                } else {
                    fcb_bd_stu.setButtonDrawable(R.drawable.muti_select);
                    allmode[1] = 1;
                    Log.e("点学生变蓝", "");
                }
            }
        }
        if (!buttonView.isChecked()) {
            if (buttonView == fcb_bd_tea) {
                if (allmode[0] == 2) {
                    fcb_bd_tea.setButtonDrawable(R.drawable.muti_select);
                    allmode[0] = 1;
                } else {
                    fcb_bd_tea.setButtonDrawable(R.drawable.muti_unselect);
                    allmode[0] = 2;
                }
            } else {
                if (allmode[1] == 2) {
                    fcb_bd_stu.setButtonDrawable(R.drawable.muti_select);
                    allmode[1] = 1;
                } else {
                    fcb_bd_stu.setButtonDrawable(R.drawable.muti_unselect);
                    allmode[1] = 2;
                }
            }
        }

    }

}