package com.example.yidiantong.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksRecyclerAdapter;
import com.example.yidiantong.bean.BookRecyclerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.List;

import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BookRecyclerActivity extends AppCompatActivity implements RecyclerInterface {
    private static final String TAG = "BookRecyclerActivity";
    private String course_name;
    private ViewPager fvp_book_recycle;
    private BooksRecyclerAdapter adapter;
    private int currentItem = 0;
    private int pageCount = 0;


    // 接口参数
    String sourceId;
    String userName;
    String subjectId;
    String questionId;
    private String pos;
    private String num;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recycle);

        fvp_book_recycle = findViewById(R.id.fvp_book_recycle);
        adapter = new BooksRecyclerAdapter(getSupportFragmentManager());
        fvp_book_recycle.setAdapter(adapter);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            this.finish();
        });


        //获取Intent参数
        course_name = getIntent().getStringExtra("name");
        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "错题详情");
        subjectId = getIntent().getStringExtra("subjectId");
        questionId = getIntent().getStringExtra("questionId");
        sourceId = getIntent().getStringExtra("sourceId");
        userName = getIntent().getStringExtra("username");
        pos = getIntent().getStringExtra("pos");

        currentItem = Integer.valueOf(pos);
        fvp_book_recycle.setCurrentItem(currentItem);

        // 学习路径展示表格
//        TableLayout tl_main = findViewById(R.id.tl_main);
//        String jsonString = "{\n" +
//                "  \"tableHead\": [\"ID\", \"当前知识点\", \"前置知识点\", \"         知识点路径       \", \"难度等级\"],\n" +
//                "  \"tableData\": [\n" +
//                "    [1, \"集合的概念\", \"/\", \"集合的概念/第一章 集合与常用逻辑用语/必修一\", \"1\"],\n" +
//                "    [2, \"集合间的基本关系\", \"集合的概念\", \"集合间的基本关系/第一章 集合与常用逻辑用语/必修一\", \"1\"],\n" +
//                "    [3, \"集合的基本运算\", \"集合间的基本关系\", \"集合的基本运算/第一章 集合与常用逻辑用语/必修一\", \"1\"],\n" +
//                "    [4, \"充分条件与必要条件\", \"/\", \"充分条件与必要条件/第一章 集合与常用逻辑用语/必修一\", \"1\"],\n" +
//                "    [5, \"全称量词与存在量词\", \"/\", \"全称量词与存在量词/第一章 集合与常用逻辑用语/必修一\", \"1\"],\n" +
//                "    [6, \"等式性质与不等式性质\", \"/\", \"等式性质与不等式性质/第二章 一元二次函数、方程和不等式/必修一\", \"1\"],\n" +
//                "    [7, \"基本不等式\", \"等式性质与不等式性质\", \"基本不等式/第二章 一元二次函数、方程和不等式/必修一\", \"1\"]\n" +
//                "  ]\n" +
//                "}";
//        try {
//            JSONObject obj = new JSONObject(jsonString);
//            makeTableUI(obj, tl_main);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        loadItems_Net(currentItem);

        //ViewPager滑动变速
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(fvp_book_recycle.getContext(),
                    new AccelerateInterpolator());
            field.set(fvp_book_recycle, scroller);
            scroller.setmDuration(400);
        } catch (Exception e) {

        }

    }

    // 绘制表格（大表格）
    private void makeTableUI(JSONObject data, TableLayout tl_main) throws JSONException {
        /** 表格
         *
         */
        Log.e("wen0309", "refreshHW: ");

        // 表格数据
        // 表头的标题
        JSONArray table_json = data.getJSONArray("tableHead");
        String[] headerTitles = new String[table_json.length()];
        // 将 JSON 数组转换为 Java 数组
        for (int i = 0; i < table_json.length(); ++i) {
            headerTitles[i] = table_json.getString(i);
        }

        table_json = data.getJSONArray("tableData");
        Log.e("wen0306", "makeTableUI: " + table_json.length());
        String[][] contentData = new String[table_json.length()][];

        for (int i = 0; i < table_json.length(); ++i) {
            JSONArray table_js = table_json.getJSONArray(i);
            String[] body_data = new String[table_js.length()];
            for (int j = 0; j < table_js.length(); ++j) {
                body_data[j] = table_js.getString(j);
            }
            contentData[i] = body_data;
        }

        tl_main.removeAllViews();

        // 表头
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        int dp1 = PxUtils.dip2px(this, 1);

        for (int i = 0; i < headerTitles.length; ++i) {
            String title = headerTitles[i];
            TextView headerTextView = new TextView(this);
            // 创建单元格布局参数，并设置外边距
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
            headerTextView.setMaxLines(Integer.MAX_VALUE); // 允许无限换行
            if (i == 0) {
                layoutParams.setMargins(dp1, dp1, dp1, dp1);
            }else{
                layoutParams.setMargins(0, dp1, dp1, dp1);
            }
            headerTextView.setLayoutParams(layoutParams);
            headerTextView.setText(title);
            headerTextView.setTextColor(getResources().getColor(R.color.white));
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setBackgroundColor(Color.parseColor("#a5a5a5"));
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f); // 设置字号为18sp
            headerTextView.setPadding(10, 10, 10, 10);
            headerRow.addView(headerTextView);
        }

        tl_main.addView(headerRow);


        // 表体
        for (int i = 0; i < contentData.length; ++i) {
            String[] row = contentData[i];
            TableRow contentRow = new TableRow(this);
            contentRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < row.length; ++j) {
                String cell = row[j];
                TextView cellTextView = new TextView(this);
                if (cell.equals("null")) {
                    cell = "";
                }
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(PxUtils.dip2px(this, 20), TableRow.LayoutParams.MATCH_PARENT);
                cellTextView.setMaxLines(Integer.MAX_VALUE); // 允许无限换行
                if (j == 0) {
                    layoutParams.setMargins(dp1, 0, dp1, dp1);
                }else{
                    layoutParams.setMargins(0, 0, dp1, dp1);
                }

                cellTextView.setLayoutParams(layoutParams);
                cellTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0f); // 设置字号为18sp
                cellTextView.setText(cell);
                cellTextView.setGravity(Gravity.CENTER);
                cellTextView.setBackgroundResource(R.color.white);
                cellTextView.setPadding(10, 10, 10, 10);
                contentRow.addView(cellTextView);
            }

            tl_main.addView(contentRow);
        }
    }


    @Override
    public void pageLast(String currentpage, String allpage) {
        if (currentItem == 1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经是第一题了");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("ok", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem -= 1;
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    @Override
    public void pageNext(String currentpage, String allpage) {
        int allpage1 = Integer.parseInt(allpage);
        if (currentItem == allpage1) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(this);
            tv.setText("已经最后一题了");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            AlertDialog dialog = builder.create();
            builder.setNegativeButton("ok", null);
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
        } else {
            currentItem += 1;
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    // 更新页面
    @Override
    public void updatepage(String currentpage, String allpage) {
        int currentpage1 = Integer.parseInt(currentpage);
        int allpage1 = Integer.parseInt(allpage);
        if (allpage1 == 1) {
            this.finish();
        } else if (currentpage1 == allpage1) {
            currentpage1 -= 1;
            loadItems_Net(currentpage1);
            //fvp_book_recycle.setCurrentItem(currentItem);
        } else {
            loadItems_Net(currentpage1);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                List<BookRecyclerEntity> list = (List<BookRecyclerEntity>) message.obj;
                adapter.update(list);
            }
        }
    };

    // 获取错题详情信息
    private void loadItems_Net(int pos) {
        String mRequestUrl = Constant.API + Constant.ERROR_QUE_ANSWER_QUESTION + "?sourceId=" + sourceId + "&userName=" + userName + "&subjectId=" + subjectId + "&currentPage=" + pos + "&questionId=" + questionId;

        Log.e("wen0223", "详细信息单题请求" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");

                itemString = "[" + itemString + "]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookRecyclerEntity> itemList = gson.fromJson(itemString, new TypeToken<List<BookRecyclerEntity>>() {
                }.getType());

                Log.e("wen0223", "loadItems_Net: " + itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = itemList;

                //标识线程
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    //Activity中将权限结果回传给Fragment
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 获取到Activity下的Fragment
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }
        // 查找在Fragment中onRequestPermissionsResult方法并调用
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                // 这里就会调用我们Fragment中的onRequestPermissionsResult方法
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

}