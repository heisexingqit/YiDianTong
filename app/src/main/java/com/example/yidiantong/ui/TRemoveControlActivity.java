package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.HuDongDialog;
import com.example.yidiantong.View.PswDialog;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class TRemoveControlActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView fiv_dianming;
    private ImageView fiv_rc_xueqing;
    private ImageView fiv_rc_baizhao;
    private ImageButton fib_rc_tiwen;
    private ImageButton fib_rc_suiji;
    private ImageButton fib_rc_qiangda;
    private TextView ftv_rc_break;
    private ImageView fiv_rc_skbcd;
    private Button fbtn_bc_cancle;
    private Button fbtn_bc_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tremove_control);

        // 下课和列表
        ftv_rc_break = findViewById(R.id.ftv_rc_break);
        fiv_rc_skbcd = findViewById(R.id.fiv_rc_skbcd);
        // 左侧按钮
        fib_rc_tiwen = findViewById(R.id.fib_rc_tiwen);
        fib_rc_suiji = findViewById(R.id.fib_rc_suiji);
        fib_rc_qiangda = findViewById(R.id.fib_rc_qiangda);

        // 底部按钮
        fiv_dianming = findViewById(R.id.fiv_rc_jrdm);
        fiv_rc_xueqing = findViewById(R.id.fiv_rc_ckxq);
        fiv_rc_baizhao = findViewById(R.id.fiv_rc_stpz);

        fib_rc_tiwen.setOnClickListener(this);
        fib_rc_suiji.setOnClickListener(this);
        fib_rc_qiangda.setOnClickListener(this);

        fiv_dianming.setOnClickListener(this);
        fiv_rc_xueqing.setOnClickListener(this);
        fiv_rc_baizhao.setOnClickListener(this);

        ftv_rc_break.setOnClickListener(this);
        fiv_rc_skbcd.setOnClickListener(this);

        loadItems_Net();
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {

            }
        }
    };

    // 获得授课一点通消息列表信息
    private void loadItems_Net() {

        String ip = getIntent().getStringExtra("ip");
        String teacherId = getIntent().getStringExtra("teacherId");
        String mRequestUrl =  "http://" + ip + ":8901" + Constant.T_GET_MESSAGE_LIST_BY_TEA + "?userId=" + teacherId;
        Log.e("mReq",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = teacherId;

                //标识线程
                message.what = 100;

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fib_rc_tiwen:
                hudongdialog();
                break;
            case R.id.fib_rc_suiji:
                hudongdialog();
                break;
            case R.id.fib_rc_qiangda:
                hudongdialog();
                break;
            case R.id.fiv_rc_jrdm:

                break;
            case R.id.fiv_rc_ckxq:

                break;
            case R.id.fiv_rc_stpz:

                break;

            // 下课
            case R.id.ftv_rc_break:
                //建立对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View inflater = LayoutInflater.from(this).inflate(R.layout.t_dialog_breakclass, null);
                builder.setView(inflater);
                AlertDialog dialog = builder.create();
                //禁止返回和外部点击
                dialog.setCancelable(false);
                //对话框弹出
                dialog.show();
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams lp = window.getAttributes();
                    lp.gravity = Gravity.CENTER;
                    lp.dimAmount = 0.0f;
                    lp.alpha = 1.0f;
                    window.setAttributes(lp);
                }

                fbtn_bc_cancle = inflater.findViewById(R.id.fbtn_bc_cancle);
                fbtn_bc_confirm = inflater.findViewById(R.id.fbtn_bc_confirm);
                fbtn_bc_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                fbtn_bc_confirm.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        breakclass();
                    }
                });

                break;
            case R.id.fiv_rc_skbcd:

                break;

        }

    }



    // 下课
    private void breakclass() {



    }

    private void hudongdialog() {
        HuDongDialog builder = new HuDongDialog(this);
        Window window = builder.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.dimAmount = 0.0f;
            lp.alpha = 1.0f;
            window.setAttributes(lp);
        }
        builder.show();
    }
}