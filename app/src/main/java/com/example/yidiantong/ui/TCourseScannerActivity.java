package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.SPUtils;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.View.ToastFormat;
import com.example.yidiantong.adapter.IpLogAdapter;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TCourseScannerActivity extends AppCompatActivity {

    private ImageView iv_saoma;
    private EditText et_ip;
    private Button fbtn_lianjie;
    private ToastFormat format;
    private TextView ftv_title;

    private LinearLayout ll_ip;
    private ImageView iv_account;

    // 记录历史
    private SharedPreferences preferences;
    private View contentView;
    private IpLogAdapter myArrayAdapter;
    private PopupWindow window;
    private ClickableTextView tv_log;
    private String ipString; // 之前的记录数据
    private List<String> ipList; // 之前的记录列表
    private String learnPlanId;
    private String ketangName;

    private static final String TAG = "TCourseScannerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcourse_scanner);
        //((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            if(MyApplication.online_class){
                // 关闭自动登录
                MyApplication.autoLogin = false;
                // 退出登录
                Intent intent = new Intent(this, LoginActivity.class);
                //两个一起用
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //登录成功跳转
                startActivity(intent);
            }else this.finish();
        });
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("遥控器登录");
        ftv_title.setTypeface(Typeface.DEFAULT_BOLD);
        RelativeLayout bar_top = findViewById(R.id.bar_top);
        if(MyApplication.online_class&&MyApplication.edution.equals("TEACHER")){
            bar_top.setVisibility(View.GONE);
        }
        // 点击扫码图像
        iv_saoma = findViewById(R.id.fiv_saoma);
        iv_saoma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(TCourseScannerActivity.this)
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

        et_ip = findViewById(R.id.et_ip);
        if (savedInstanceState != null) {
            et_ip.setText(savedInstanceState.getString("ip"));
        } else {
            et_ip.setText((SPUtils.getInstance().getString("easyip", "")));
        }

        // 初始化toast提示信息
        format = new ToastFormat(this);
        format.InitToast();
        format.setGravity(Gravity.TOP);

        fbtn_lianjie = findViewById(R.id.fbtn_lianjie);
        fbtn_lianjie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("", "" + et_ip.getText().length());
                if (et_ip.getText().length() == 0) {
                    format.show();

                } else {
                    loadItems_Net();
                }
            }
        });

        ll_ip = findViewById(R.id.ll_ip);
        iv_account = findViewById(R.id.iv_account);
        // 输入框颜色变化
        et_ip.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ll_ip.setBackground(getDrawable(R.drawable.login_et_border_focus));
                    iv_account.setImageResource(R.drawable.ip_icon_focus);
                } else {
                    ll_ip.setBackground(getDrawable(R.drawable.login_et_border_unfocus));
                    iv_account.setImageResource(R.drawable.ip_icon_unfocus);
                }
            }
        });
        // ---------------- //
        // ip地址记录历史模块
        // ---------------- //
        preferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        tv_log = findViewById(R.id.tv_log);
        tv_log.setOnClickListener(v -> showHistoryIps());
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                SPUtils.getInstance().put("easyip", et_ip.getText().toString());

                // 缓存记录构造
                boolean found = false;
                int index = -1;
                for (int i = 0; i < ipList.size(); i++) {
                    String[] parts = ipList.get(i).split(":");
                    if (parts[0].equals(ketangName)) {
                        ipList.set(i, ketangName + ":" + et_ip.getText().toString());
                        found = true;
                        index = i;
                        break;
                    }
                }
                if (!found) {
                    ipList.add(0, ketangName + ":" + et_ip.getText().toString());
                } else if (index != 0) {
                    String temp = ipList.get(index); // 保存修改的项
                    ipList.remove(index); // 移除修改的项
                    ipList.add(0, temp); // 添加到开头
                }
                String newIpLog;
                ArrayList<String> editIpList = new ArrayList<>(ipList.subList(0, ipList.size() - 1));
                newIpLog = String.join(", ", editIpList);

                // 存储记录
                SPUtils.getInstance().put("ips", newIpLog);
                turnLookContro((String) message.obj);
            }
        }
    };

    private void turnLookContro(String teacherId) {
        Intent intent = new Intent(this, TRemoveControlActivity.class);
        intent.putExtra("teacherId", teacherId);
        intent.putExtra("ip", et_ip.getText().toString());
        intent.putExtra("learnPlanId", learnPlanId);
        startActivity(intent);
    }

    private void loadItems_Net() {
        String mRequestUrl = "http://" + et_ip.getText() + ":8901" + Constant.T_CLIENT_KETANG_PLAY_BY_TEA;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String teacherId = json.getString("teacherId");
                String openFlag = json.getString("openFlag");
                learnPlanId = json.getString("learnPlanId");
                ketangName = json.getString("ketangName");
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = teacherId;

                //标识线程
                message.what = 100;
                if (openFlag.equals("true")) {
                    handler.sendMessage(message);
                } else {
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            format.show();
            Log.e("volley", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }


    private void showHistoryIps() {
        if (contentView == null) {
            contentView = LayoutInflater.from(this).inflate(R.layout.menu_homework, null, false);

            ListView lv_homework = contentView.findViewById(R.id.lv_homework);
            lv_homework.getLayoutParams().width = PxUtils.dip2px(this, 350);

            lv_homework.setAdapter(myArrayAdapter);
            lv_homework.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    // 切换页+消除选项
                    if (!ipList.get(i).equals("暂无历史记录") && !ipList.get(i).equals("清空历史记录")) {
                        String clickIp = ipList.get(i);
                        int idx = clickIp.lastIndexOf(":");
                        et_ip.setText(clickIp.substring(idx + 1));
                    }
                    window.dismiss();
                }
            });

            /**
             * 设置MaxHeight,先显示才能获取高度
             */
            lv_homework.post(() -> {
                int maxHeight = PxUtils.dip2px(this, 245);
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
                Toast.makeText(this, "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this.getActivity(), "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                et_ip.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // -------------------- //
        //  ip地址历史记录初始化
        //  ips缓存在SPUtils文件中
        //  自动填写使用统一的
        // -------------------- //
        // 及时刷新历史记录
        ipString = SPUtils.getInstance().getString("ips");
        // 记录历史IP
        if (ipString.length() == 0) {
            ipList = new ArrayList<>();
            ipList.add("暂无历史记录");
        } else {
            ipList = new ArrayList<>(Arrays.asList(ipString.split(", ")));
            ipList.add("清空历史记录");
        }
        myArrayAdapter = new IpLogAdapter(this, ipList);
        myArrayAdapter.setMyOnclickListener(new IpLogAdapter.MyOnclickListener() {
            @Override
            public void delete_item(int pos) {
                ipList.remove(pos);
                if (ipList.size() == 1) {
                    ipList.clear();
                    ipList.add("暂无历史记录");
                }
                myArrayAdapter.notifyDataSetChanged();

                // ---------------- //
                //  同步到数据表xml
                // ---------------- //
                ArrayList<String> editIpList = new ArrayList<>(ipList.subList(0, ipList.size() - 1));

                SPUtils.getInstance().put("ips", String.join(", ", editIpList));
            }

            @Override
            public void delete_all() {
                ipList.clear();
                ipList.add("暂无历史记录");
                myArrayAdapter.notifyDataSetChanged();

                // ---------------- //
                //  同步到数据表xml
                // ---------------- //
                SPUtils.getInstance().put("ips", "");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ip", et_ip.getText().toString());
    }
}