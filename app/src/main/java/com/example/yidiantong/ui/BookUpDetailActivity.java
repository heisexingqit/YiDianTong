package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.adapter.BooksRecyclerAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.FixedSpeedScroller;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.RecyclerInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BookUpDetailActivity extends AppCompatActivity implements RecyclerInterface {
    private static final String TAG = "BookUpDetailActivity";
    private String course_name;  //课程名称
    private ViewPager fvp_book_recycle;  //可滑动ViewPager
    private BooksRecyclerAdapter adapter;  //ViewPager适配器
    private int currentItem = 0;  //当前ViewPager位置
    private int pageCount = 0;
    private boolean exerciseType = true ;  //是否是巩固提升


    // 接口参数
    String sourceId;  //单元ID
    String userName;  //用户名
    String subjectId;  //学科ID
    String questionId; //题目ID
    String allpage;
    private String pos; //题目位置
    private String[] questionIds;//题目ID数组


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_up_detail);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
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
        tv_title.setText("巩固提升题目详情");
        subjectId = getIntent().getStringExtra("subjectId");  //学科ID
        questionId = getIntent().getStringExtra("questionId");  //题目ID
        sourceId = getIntent().getStringExtra("sourceId");  //单元ID
        userName = getIntent().getStringExtra("username");  //用户名
        allpage = getIntent().getStringExtra("allpage");
        pos = getIntent().getStringExtra("pos");
        questionIds = getIntent().getStringExtra("questionIds").split(",");

        currentItem = Integer.valueOf(pos);
        fvp_book_recycle.setCurrentItem(currentItem);

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

    // 回到上一题
    @Override
    public void pageLast(String currentpage, String allpage) {
        System.out.println("减一前currentItem ^-^:" + currentItem);
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
            System.out.println("减一后currentItem ^-^:" + currentItem);
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }
    }

    // 前往下一题
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

    // 将错题标记掌握移至错题本时更新页面
    @Override
    public void updatepage(String currentpage, String allpage) {
        currentItem = Integer.parseInt(currentpage);
        int allpage1 = Integer.parseInt(allpage);
        if(allpage1 == 1){  //只有一题时
            this.finish();
        }else if(currentItem == allpage1) {
            currentItem -= 1;
            // 延迟1秒
            try {
                Thread.sleep(1000); // 延迟1秒，单位为毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadItems_Net(currentItem);
            //fvp_book_recycle.setCurrentItem(currentItem);
        }else{
            // 延迟0.1秒
            try {
                Thread.sleep(1000); // 延迟1秒，单位为毫秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadItems_Net(currentItem);
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
                List<BookExerciseEntity> list = (List<BookExerciseEntity>)message.obj;
                String currentpage = String.valueOf(currentItem);
                adapter.update2(list,2,userName,subjectId,course_name,exerciseType,currentpage,allpage);
            }
        }
    };

    // 获取错题详情信息
    private void loadItems_Net(int pos) {
//        String mRequestUrl = Constant.API + Constant.ERROR_QUE_ANSWER_QUESTION + "?sourceId=" + sourceId +"&userName=" +userName +"&subjectId=" + subjectId +"&currentPage=" + pos + "&questionId=" + questionId;
        String mRequestUrl = "http://www.cn901.com//ShopGoods/ajax/learnPlan_getQuestion.do"
                + "?questionId=" + questionIds[pos-1];
        Log.e("wen0223", "详细信息单题请求" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("questionInfo");
                //使用正则表达式对返回结果新增选项设置个数
                Pattern pattern = Pattern.compile("answernumber=(\\\\*)\"(\\d+)");
                Matcher m1 = pattern.matcher(itemString);
                int answerNumber = 1;//选项个数
                while (m1.find()) {
                    answerNumber = Integer.parseInt(m1.group(2));
                    break;
                }
                itemString = "["+itemString+"]";
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                List<BookExerciseEntity> itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
                for (BookExerciseEntity item : itemList) {
                    //设置选项个数
                    item.setAnswerNumber(answerNumber);
                    //将试题答案格式化，利用正则表达式去掉<>标签及里面的内容
                    String answer = item.getShiTiAnswer();
                    String regEx = "<[^>]+>";
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(answer);
                    String answerStr = m.replaceAll("").trim();
                    item.setShiTiAnswer(answerStr);
                }
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