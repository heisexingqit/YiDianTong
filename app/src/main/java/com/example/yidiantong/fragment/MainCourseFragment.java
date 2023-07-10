package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ToastFormat;
import com.example.yidiantong.bean.BookDetailEntity;
import com.example.yidiantong.bean.BookInfoEntity;
import com.example.yidiantong.bean.CourseScannerEntity;
import com.example.yidiantong.ui.CourseLookActivity;
import com.example.yidiantong.ui.CourseScannerActivity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.ui.HomeworkPagerFinishActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class MainCourseFragment extends Fragment {

    private ImageView iv_saoma;
    private EditText et_ip;
    private Button fbtn_lianjie;
    private ToastFormat format;


    public static MainCourseFragment newInstance() {
        MainCourseFragment fragment = new MainCourseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_main_course, container, false);

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

        // 初始化toast提示信息
        format = new ToastFormat(MainCourseFragment.this.getContext());
        format.InitToast();
        format.setGravity(Gravity.TOP);

        fbtn_lianjie = view.findViewById(R.id.fbtn_lianjie);
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

        return view;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                turnLookCourse((List<CourseScannerEntity>) message.obj);
            }
        }
    };

    private void turnLookCourse(List<CourseScannerEntity> moreList) {
        Intent intent= new Intent(getActivity(), CourseLookActivity.class);
        intent.putExtra("classname",moreList.get(0).getCourseName());
        intent.putExtra("teaname",moreList.get(0).getTeacherName());
        intent.putExtra("stuname",moreList.get(0).getIntroduction());
        startActivity(intent);
    }

    private void loadItems_Net() {
        String password = getActivity().getIntent().getStringExtra("password");
        String username = getActivity().getIntent().getStringExtra("username");
        Log.e("et_ip",""+et_ip.getText());
        String mRequestUrl =  "http://" + et_ip.getText() + ":8901" + Constant.KETANGPLAYBYSTU +  "?userName=" + username + "&password=" + password;
        Log.e("mReq",""+mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("learnPlan");
                itemString = "[" + itemString +"]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<CourseScannerEntity> moreList = gson.fromJson(itemString, new TypeToken<List<CourseScannerEntity>>() {}.getType());
                Log.e("moreList",""+moreList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = moreList;

                //标识线程
                message.what = 100;
                if(moreList.size() == 0){
                    format.show();
                    return;
                }else {
                    handler.sendMessage(message);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            format.show();
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }


    // 返回扫描结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this.getActivity(), "取消扫描", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(this.getActivity(), "扫描内容:" + result.getContents(), Toast.LENGTH_LONG).show();
                et_ip.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




}