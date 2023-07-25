package com.example.yidiantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ToastFormat;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TCourseScannerActivity extends AppCompatActivity {

    private ImageView iv_saoma;
    private EditText et_ip;
    private Button fbtn_lianjie;
    private ToastFormat format;
    private TextView ftv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcourse_scanner);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });
        ftv_title = findViewById(R.id.ftv_title);
        ftv_title.setText("遥控器登录");
        ftv_title.setTypeface(Typeface.DEFAULT_BOLD);

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

        // 初始化toast提示信息
        format = new ToastFormat(this);
        format.InitToast();
        format.setGravity(Gravity.TOP);

        fbtn_lianjie = findViewById(R.id.fbtn_lianjie);
        fbtn_lianjie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("",""+et_ip.getText().length());
                if(et_ip.getText().length() == 0){
                    format.show();

                }else {
                    loadItems_Net();
                }
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                turnLookContro((String) message.obj);
            }
        }
    };

    private void turnLookContro(String teacherId) {
        Intent intent= new Intent(this, TRemoveControlActivity.class);
        intent.putExtra("teacherId",teacherId);
        intent.putExtra("ip",et_ip.getText().toString());
        startActivity(intent);
    }

    private void loadItems_Net() {
        String mRequestUrl =  "http://" + et_ip.getText() + ":8901" + Constant.T_CLIENT_KETANG_PLAY_BY_TEA ;
        Log.e("mReq",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String teacherId = json.getString("teacherId");
                String openFlag = json.getString("openFlag");
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = teacherId;

                //标识线程
                message.what = 100;
                if(openFlag.equals("true")){
                    handler.sendMessage(message);
                }else{
                    return;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            format.show();
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }


    // 返回扫描结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
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

}