package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.SPUtils;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.View.ToastFormat;
import com.example.yidiantong.adapter.IpLogAdapter;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.ui.CourseLookActivity;
import com.example.yidiantong.ui.CourseScannerActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.MyViewModel;
import com.example.yidiantong.util.PxUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class MainCourseFragment extends Fragment {

    private static final String TAG = "MainCourseFragment";
    private ImageView iv_saoma;
    private EditText et_ip;
    private Button fbtn_lianjie;
    private ToastFormat format;
    private String username;
    private String ip;

    private LinearLayout ll_ip;
    private ImageView iv_account;

    // ---------------- //
    // ip地址记录历史模块
    // ---------------- //
    private SharedPreferences preferences;
    private View contentView;
    private IpLogAdapter myArrayAdapter;
    private PopupWindow window;
    private ClickableTextView tv_log;
    private String ipString; // 之前的记录数据
    private List<String> ipList; // 之前的记录列表

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public static MainCourseFragment newInstance() {
        MainCourseFragment fragment = new MainCourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_course, container, false);
        MyViewModel myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);


        // 点击扫码图像
        iv_saoma = view.findViewById(R.id.fiv_saoma);
        iv_saoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(getActivity())
                        .setCaptureActivity(CourseScannerActivity.class)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        .setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .setBarcodeImageEnabled(true)// 扫完码之后生成二维码的图片
                        //.setOrientationLocked(false) // 设置方向不锁定
                        .initiateScan();// 初始化扫码
            }

        });

        et_ip = view.findViewById(R.id.et_ip);
        if(savedInstanceState != null){
            et_ip.setText(savedInstanceState.getString("ip"));
        }else{
            et_ip.setText((SPUtils.getInstance().getString("easyip", "")));
        }

        // 初始化toast提示信息
        format = new ToastFormat(MainCourseFragment.this.getContext());
        format.InitToast();
        format.setGravity(Gravity.TOP);

        fbtn_lianjie = view.findViewById(R.id.fbtn_lianjie);
        fbtn_lianjie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_ip.getText().length() == 0) {
                    format.show();
                } else {
                    loadItems_Net();
                }
            }
        });

        ll_ip = view.findViewById(R.id.ll_ip);
        iv_account = view.findViewById(R.id.iv_account);
        // 输入框颜色变化
        et_ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ll_ip.setBackground(getActivity().getDrawable(R.drawable.login_et_border_focus));
                    iv_account.setImageResource(R.drawable.ip_icon_focus);
                } else {
                    ll_ip.setBackground(getActivity().getDrawable(R.drawable.login_et_border_unfocus));
                    iv_account.setImageResource(R.drawable.ip_icon_unfocus);
                }
            }
        });


        // 设置 EditText 的监听器来更新 ViewModel
        et_ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                myViewModel.setIp(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        // 观察 ViewModel 中的数据变化并更新 UI
        myViewModel.getIp().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(!s.equals(et_ip.getText().toString())){
                    et_ip.setText(s);
                }
            }
        });
        // ---------------- //
        // ip地址记录历史模块
        // ---------------- //
//        preferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
//        tv_log = view.findViewById(R.id.tv_log);
//        tv_log.setOnClickListener(v -> showHistoryIps());


        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {

                SPUtils.getInstance().put("easyip", et_ip.getText().toString());
//                String ip = et_ip.getText().toString();
//
//                // 使用 Stream API 进行判断和删除
//                ipList = ipList.stream()
//                        .filter(str -> !str.equals(ip))
//                        .collect(Collectors.toList());
//                String newIpLog;
//                ArrayList<String> editIpList = new ArrayList<>(ipList.subList(0, ipList.size() - 1));
//
//                if (ipString.length() > 0) {
//                    newIpLog = ip + ", " + String.join(", ", editIpList);
//                } else {
//                    newIpLog = ip;
//                }
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("ips", newIpLog);
//                editor.commit();

                turnLookCourse((List<CourseScannerEntity>) message.obj);
            }
        }
    };

    private void turnLookCourse(List<CourseScannerEntity> moreList) {
        Intent intent = new Intent(getActivity(), CourseLookActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("ip", ip);
        intent.putExtra("classname", moreList.get(0).getCourseName());
        intent.putExtra("teaname", moreList.get(0).getTeacherName());
        intent.putExtra("stuname", moreList.get(0).getIntroduction());
        startActivity(intent);
    }

    private void loadItems_Net() {
        String password = MyApplication.password;
        username = MyApplication.username;
        ip = et_ip.getText().toString();

        String mRequestUrl = "http://" + ip + ":8901" + Constant.KETANGPLAYBYSTU + "?userName=" + username + "&password=" + password;
        Log.e("mReq", "" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("learnPlan");
                Log.e("wen0228", "loadItems_Net: " + json);

                itemString = "[" + itemString + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<CourseScannerEntity> moreList = gson.fromJson(itemString, new TypeToken<List<CourseScannerEntity>>() {
                }.getType());
                Log.e("moreList", "" + moreList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = moreList;

                //标识线程
                message.what = 100;
                if (moreList.size() == 0) {
                    format.setText("暂无课程开始");
                    format.show();
                } else {
                    // 判断学生username是否匹配
                    if (moreList.get(0).getIntroduction().equals("unExist")) {
                        format.setText("您不是该课堂学生；或账户填写错误，请检查");
                        format.show();
                    } else if (moreList.get(0).getIntroduction().equals("noKetang")) {
                        format.setText("暂无课程开始");
                        format.show();
                    } else {
                        handler.sendMessage(message);
                    }
                }
            } catch (JSONException e) {
                Log.e("wen20228", "loadItems_Net: " + e);
            }
        }, error -> {
            format.setText("暂无课程开始");
            format.show();
        });
        MyApplication.addRequest(request, TAG);
    }

    // ---------------- //
    // ip地址记录历史模块
    // ---------------- //
    private void showHistoryIps() {
        if (contentView == null) {
            contentView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_homework, null, false);

            ListView lv_homework = contentView.findViewById(R.id.lv_homework);
            lv_homework.getLayoutParams().width = PxUtils.dip2px(getActivity(), 290);

            lv_homework.setAdapter(myArrayAdapter);
            lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // 切换页+消除选项口
                    if (!ipList.get(i).equals("暂无历史记录") && !ipList.get(i).equals("清空历史记录")) {
                        et_ip.setText(ipList.get(i));
                    }
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

            window = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            window.setTouchable(true);
        } else {
            // 仅刷新数据
            ListView lv_homework = contentView.findViewById(R.id.lv_homework);
            lv_homework.setAdapter(myArrayAdapter);
        }

        if (window.isShowing()) {
            window.dismiss();
        } else {
            window.showAsDropDown(ll_ip, 0, 3);
        }
    }

    // 返回扫描结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this.getActivity(), "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this.getActivity(), "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                et_ip.setText(result.getContents());
                SPUtils.getInstance().put("easyip", et_ip.getText().toString());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        // -------------------- //
//        //  ip地址历史记录初始化
//        //  ips缓存在自建文件中
//        //  自动填写使用统一的
//        // -------------------- //
//        // 及时刷新历史记录
//        ipString = preferences.getString("ips", "");
//        // 记录历史IP
//        if (ipString.length() == 0) {
//            ipList = new ArrayList<>();
//            ipList.add("暂无历史记录");
//        } else {
//            ipList = new ArrayList<>(Arrays.asList(ipString.split(", ")));
//            ipList.add("清空历史记录");
//        }
//        myArrayAdapter = new IpLogAdapter(getActivity(), ipList);
//        myArrayAdapter.setMyOnclickListener(new IpLogAdapter.MyOnclickListener() {
//            @Override
//            public void delete_item(int pos) {
//                ipList.remove(pos);
//                if(ipList.size() == 1){
//                    ipList.clear();
//                    ipList.add("暂无历史记录");
//                }
//                myArrayAdapter.notifyDataSetChanged();
//
//                // ---------------- //
//                //  同步到数据表xml
//                // ---------------- //
//                ArrayList<String> editIpList = new ArrayList<>(ipList.subList(0, ipList.size() - 1));
//                Log.e("wen0228", "delete_item: " + editIpList);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("ips", String.join(", ", editIpList));
//                editor.commit();
//            }
//
//            @Override
//            public void delete_all() {
//                ipList.clear();
//                ipList.add("暂无历史记录");
//                myArrayAdapter.notifyDataSetChanged();
//
//                // ---------------- //
//                //  同步到数据表xml
//                // ---------------- //
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("ips", "");
//                editor.commit();
//            }
//        });

    }

}