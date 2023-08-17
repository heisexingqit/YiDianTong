package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TMainReportFragment extends Fragment {
    private static final String TAG = "TMainReportFragment";

    private final int[] imgs = {R.drawable.t_teach_board, R.drawable.t_homework_board, R.drawable.t_marked_board};
    private final String timesNames[] = {"有效课次:", "有效次数:", "批阅总数:"};
    private View[] v = new View[3];
    private String username;
    private String userId;
    private TextView tv_num1;
    private TextView tv_num2;
    private TextView tv_num3;
    private TextView tv_num4;


    public static TMainReportFragment newInstance() {
        TMainReportFragment fragment = new TMainReportFragment();
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

        View view = inflater.inflate(R.layout.fragment_t_main_report, container, false);

        LinearLayout ll_parent = view.findViewById(R.id.ll_parent);

        username = MyApplication.username;
        userId = MyApplication.userId;

        /**
         * 三个版块块显示（后期可能用Fragment方式）
         */
        java.util.Date day = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(day);
        /* 课堂授课 */
        v[0] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        // 主题色
        LinearLayout ll_color = v[0].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF47bb3e);
        // 标题
        TextView tv_title = v[0].findViewById(R.id.tv_title);
        tv_title.setText("课堂授课");
        // 图片
        ImageView iv = v[0].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[0]);
        // 描述
        TextView tv_times = v[0].findViewById(R.id.tv_times);
        tv_times.setText("有效课次:");
        GridLayout gl_content = v[0].findViewById(R.id.gl_content);

        // 数据
        String content1[] = {"内容:0", "互动:0", "批注:0", "板书:0"};
        for(int i = 0; i < 4; ++i){
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(i/2, 1, 1.0f);
            params.rowSpec = GridLayout.spec(i%2, 1, 1.0f);

            TextView tv = new TextView(getActivity());
            tv.setText(content1[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setGravity(Gravity.CENTER);
            gl_content.addView(tv, params);
        }
        ll_parent.addView(v[0]);

        /* 布置作业 */
        v[1] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        // 主题色
        ll_color = v[1].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF9518ba);
        // 标题
        tv_title = v[1].findViewById(R.id.tv_title);
        tv_title.setText("布置作业");
        // 图片
        iv = v[1].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[1]);
        // 描述
        tv_times = v[1].findViewById(R.id.tv_times);
        tv_times.setText("有效次数:");
        gl_content = v[1].findViewById(R.id.gl_content);

        // 数据
        String content2[] = {"试题总述:0", "平均刷题数:0"};
        for(int i = 0; i < 2; ++i){
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(0, 2, 1.0f);
            params.rowSpec = GridLayout.spec(i, 1, 1.0f);
            TextView tv = new TextView(getActivity());
            tv.setText(content2[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setGravity(Gravity.CENTER);
            gl_content.addView(tv, params);
        }
        ll_parent.addView(v[1]);

        /* 批阅试题 */
        v[2] = LayoutInflater.from(getActivity()).inflate(R.layout.item_t_statistic_report_board, ll_parent, false);
        // 主题色
        ll_color = v[2].findViewById(R.id.ll_color);
        ll_color.setBackgroundColor(0xFF7c67f4);
        // 标题
        tv_title = v[2].findViewById(R.id.tv_title);
        tv_title.setText("批阅试题");
        // 图片
        iv = v[2].findViewById(R.id.iv_icon);
        iv.setImageResource(imgs[2]);
        // 描述
        tv_times = v[2].findViewById(R.id.tv_times);
        tv_times.setText("有效次数:");
        gl_content = v[2].findViewById(R.id.gl_content);

        // 数据
        String content3[] = {"填空题:0", "其他题:0"};
        for(int i = 0; i < 2; ++i){
            // 定位
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(0, 2, 1.0f);
            params.rowSpec = GridLayout.spec(i, 1, 1.0f);
            TextView tv = new TextView(getActivity());
            tv.setText(content3[i]);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setGravity(Gravity.CENTER);
            gl_content.addView(tv, params);
        }
        ll_parent.addView(v[2]);

        /**
         * 第一部分统计显示
         */
        loadItems_Net();

        tv_num1 = view.findViewById(R.id.tv_report_num1);
        tv_num2 = view.findViewById(R.id.tv_report_num2);
        tv_num3 = view.findViewById(R.id.tv_report_num3);
        tv_num4 = view.findViewById(R.id.tv_report_num4);

        return view;
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                try {
                    JSONObject data = (JSONObject) message.obj;
                    tv_num1.setText(data.getString("ktNum"));
                    tv_num2.setText(data.getString("hdNum"));
                    tv_num3.setText(data.getString("zyNum"));
                    tv_num4.setText(JsonUtils.clearString(data.getString("pgNum")));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                /**
                 * 假0判断移至adapter中，根据refresh一起判断
                 */
            }
        }
    };


    //加载消息条目，包括刷新和加载，通过upDown标识两种状态
    private void loadItems_Net() {
//        if (adapter.isRefresh == 1) {
//            rl_loading.setVisibility(View.VISIBLE);
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        Date nowTime = cal.getTime();
        String end = sdf.format(nowTime);
        cal.add(Calendar.MONTH, -6);
        Date preTime = cal.getTime();
        String start = sdf.format(preTime);

        String mRequestUrl = Constant.API + Constant.T_REPORT_TOP + "?userId=" + username + "&unitId=" + userId + "&startTime=" + start + "&endTime=" + end;

        Log.d("wen", "home: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {

            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                JSONObject data = json.getJSONObject("data");
//
                // 封装消息，传递给主线程
                Message message = Message.obtain();

                // 携带数据
                message.obj = data;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
//            rl_loading.setVisibility(View.GONE);
        });
        MyApplication.addRequest(request, TAG);
    }
}