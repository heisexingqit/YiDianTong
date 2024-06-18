package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BookAutoAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class KnowledgeShiTiActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgeShiTiActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮
    private ClickableImageView fiv_select_stu;//选择学生按钮
    private TextView tv_knowledge_name;
    private LinearLayout ll_kaodian_filtrate;

    //请求数据参数
    private int currentPage = -1;
    private String userName; //用户名
    private String reqName;  //请求用户名
    private String xueduan;  //请求类型
    private String subjectId;  //学科id
    private String banben;  //学科id
    private String jiaocai;  //学科id
    private String unitId;  //考点
    private String course_name;  //学科名
    private String zhishidian;  //知识点
    private String zhishidianId;  //知识点id

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    private List<String> stuList = new ArrayList<>();//学生列表
    private List<ExamPoint> examPoints = new ArrayList<>();//考点列表
    private List<ExamPoint> selectedExamPoints = new ArrayList<>();//已选考点列表
    private String questionIds;
    BookAutoAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String pointIds = "";

    private SharedPreferences preferences;
    private String[] autoStuLoadAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_shiti);

        preferences = getSharedPreferences("shiti", MODE_PRIVATE);

        //顶栏返回按钮
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            finish();
        });


        frv_detail = findViewById(R.id.frv_detail);
        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getIntent().getStringExtra("userName");  //用户名

        subjectId = getIntent().getStringExtra("subjectId"); //学科名
        course_name = getIntent().getStringExtra("courseName"); //学科id
        zhishidian = getIntent().getStringExtra("zhishidian"); //知识点
        zhishidianId = getIntent().getStringExtra("zhishidianId"); //知识点id
        xueduan = getIntent().getStringExtra("xueduanId"); //学段
        banben = getIntent().getStringExtra("banbenId"); //版本
        jiaocai = getIntent().getStringExtra("jiaocaiId"); //教材
        unitId = getIntent().getStringExtra("unitId"); //考点
//        reqName = userName;

        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("知识点试题");
        tv_knowledge_name = findViewById(R.id.tv_knowledge_name);
        tv_knowledge_name.setText(zhishidian);
        ll_kaodian_filtrate = findViewById(R.id.ll_kaodian_filtrate);
        //点击后,页面弹出图层,显示章节下的考点,每个考点占一行,并且前面有一个复选框,默认全部为选中状态
        ll_kaodian_filtrate.setOnClickListener(v -> showCheckableSubjectsDialog());

        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        fll_null = findViewById(R.id.fll_null);

        //发送请求获取学生列表
        getStudentList();
        //加载学生按钮
        fiv_select_stu = findViewById(R.id.fiv_select_stu);
        fiv_select_stu.setVisibility(View.VISIBLE);
        fiv_select_stu.setOnClickListener(v -> {
            //将学生集合转换为数组
            showStusDialog();
        });

        //刷新题目按钮
        fiv_refresh = findViewById(R.id.fiv_refresh);
        fiv_refresh.setVisibility(View.VISIBLE);
        fiv_refresh.setOnClickListener(v -> {
            rl_loading.setVisibility(View.VISIBLE);
            fll_null.setVisibility(View.GONE);
            refreshList();
        });


        //设置RecyclerViewAdapter
        adapter = new BookAutoAdapter(this, itemList);
        frv_detail.setAdapter(adapter);

        //refreshList();


        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(this, KnowledgeShiTiDetailActivity.class);
            BookExerciseEntity item = itemList.get(pos);
            intent.putExtra("questionId", item.questionId); //题目id
            intent.putExtra("subjectId", subjectId);  // 学科id
            intent.putExtra("username", userName);  // 用户名
            intent.putExtra("name", course_name);  // 学科名
            intent.putExtra("allpage", String.valueOf(itemList.size()));  // 总页数
            intent.putExtra("questionIds", questionIds);  // 题目id
            pos = pos + 1;
            intent.putExtra("pos", String.valueOf(pos));
            startActivity(intent);
        });

    }

    //展示学生列表
    private void showStusDialog() {
        String[] stuArr = new String[this.stuList.size()];
        for (int i = 0; i < this.stuList.size(); i++) {
            stuArr[i] = this.stuList.get(i);
        }
        System.out.println("学生列表：" + Arrays.toString(stuArr));
        //使用AlertDialog显示学生列表以供选择
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择一个学生");
        builder.setItems(stuArr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reqName = stuArr[which];
                loadItems_Net();
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //加载学生列表
    private void getStudentList() {
        String mRequestUrl = "http://www.cn901.com:8111//AppServer/ajax/studentApp_getStuList.do";
        Log.d("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                stuList = gson.fromJson(itemString, new TypeToken<List<String>>() {
                }.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            System.out.println(error.toString());
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }


    //考点复选框事件
    private void showCheckableSubjectsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择考点");

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (ExamPoint examPoint : examPoints) {
            View itemView = inflater.inflate(R.layout.item_exam_point, null);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox);
            TextView textName = itemView.findViewById(R.id.textName);
            TextView masteryLevel = itemView.findViewById(R.id.masteryLevel);

            textName.setText(examPoint.getPointName());
            masteryLevel.setText(examPoint.getInfo());
            checkBox.setChecked(selectedExamPoints.contains(examPoint));

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!selectedExamPoints.contains(examPoint)) {
                        selectedExamPoints.add(examPoint);
                    }
                } else {
                    selectedExamPoints.remove(examPoint);
                }
            });

            layout.addView(itemView);
        }
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(layout);
        builder.setView(scrollView);
        builder.setPositiveButton("确定", (dialog, which) -> {
            // 将选中的考点中的dbid拼接到pointIds中
            pointIds = "";
            StringBuilder sb = new StringBuilder();
            for (ExamPoint examPoint : selectedExamPoints) {
                sb.append(examPoint.getDbid()).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            pointIds = sb.toString();
            loadItems_Net();
        });
        builder.setNegativeButton("取消", null);
        builder.show();

    }
    //获取考点列表
    private void getKaoDianList() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        //如果集合中有数据，直接显示对话框
        if (examPoints != null && examPoints.size() > 0) {
//            showCheckableSubjectsDialog();
            loadItems_Net();
            return;
        }
        String mRequestUrl = "http://www.cn901.com:8111//AppServer/ajax/studentApp_getPointsByCatalogId.do?" +
                "catalogId=" + zhishidianId + "&stuId=" + userName + "&channelCode=" + xueduan + "&subjectCode=" + subjectId
                + "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&unitId=" + unitId;
        Log.d("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                examPoints = gson.fromJson(itemString, new TypeToken<List<ExamPoint>>() {
                }.getType());
                //默认全选,并将考点dbid拼接到pointIds中
                selectedExamPoints.addAll(examPoints);
                pointIds = "";
                StringBuilder sb = new StringBuilder();
                for (ExamPoint examPoint : selectedExamPoints) {
                    sb.append(examPoint.getDbid()).append(",");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                pointIds = sb.toString();
                loadItems_Net();
//                showCheckableSubjectsDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            System.out.println(error.toString());
            Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }


    //刷新列表
    private void refreshList() {
        currentPage++;
        adapter.isRefresh = 1;
        getKaoDianList();
//        loadItems_Net();
        frv_detail.scrollToPosition(0);
    }

    // what:100 加载错题信息
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                Bundle receivedBundle = (Bundle) message.obj;
                // 获得三个列表信息
                itemList = (List<BookExerciseEntity>) receivedBundle.getSerializable("itemList");
                adapter.loadData(itemList);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;

                //创建本地数组保存学生作答信息
                SharedPreferences.Editor editor = preferences.edit();
                autoStuLoadAnswer = new String[itemList.size()];
                String autoArrayString = TextUtils.join(",", autoStuLoadAnswer);
                System.out.println("autoArrayString:" + autoArrayString);
                editor.putString("autoStuLoadAnswer", autoArrayString);
                editor.apply();
            }
        }
    };

    //发送请求获得试题数据,对请求到的数据进行处理
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }
        String mRequestUrl = "http://www.cn901.net:8111/AppServer/ajax/studentApp_getQuestionsZZXX.do?" +
                "userId=" + userName + "&subjectId=" + subjectId + "&catalogId=" + zhishidianId + "&pointIds=" + pointIds + "&currentPage=" + currentPage;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);
                Alert(message1);
                String itemString = json.getString("data");
                //TODO 汇报结束要进行修改
//                itemString = "[]";
                //当试题列表为空时,需要跳转中间页进行处理
                if (itemString.equals("[]") || itemString.equals("") || itemString.equals("null")) {
                    Intent intent = new Intent(this, OnlineTestNullActivity.class);
                    intent.putExtra("userName", userName);
                    intent.putExtra("subjectId", subjectId);
                    intent.putExtra("courseName", course_name);
                    intent.putExtra("flag", "自主学习");
                    startActivity(intent);
                    finish();
                }

                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {
                }.getType());
                questionIds = "";
                for (int i = 0; i < itemList.size(); i++) {
                    if (i == itemList.size() - 1) {
                        questionIds += itemList.get(i).questionId;
                    } else {
                        questionIds += itemList.get(i).questionId + ",";
                    }
                }
                System.out.println("questionIds:" + questionIds);
                System.out.println("itemList:" + itemList);

                Bundle bundle = new Bundle();
                bundle.putSerializable("itemList", (Serializable) itemList);
                //封装消息，传递给主线程
                Message message = Message.obtain();
                message.obj = bundle;

                // 发送消息给主线程
                if (itemList == null || itemList.size() == 0 || itemString.equals("[]")) {
                    fll_null.setVisibility(View.VISIBLE);
                    return;
                } else {
                    fll_null.setVisibility(View.GONE);
                }
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

    private void Alert(String alert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        //自定义title样式
        TextView tv = new TextView(this);
        tv.setText(alert);    //内容
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loadFirst) {
//            loadItems_Net();
            refreshList();
            loadFirst = false;
        }

    }

    private static class ExamPoint {
        private String pointName;
        private String info;
        private String dbid;
        private String pointCode;

        public ExamPoint(String pointName, String info, String dbid, String pointCode) {
            this.pointName = pointName;
            this.info = info;
            this.dbid = dbid;
            this.pointCode = pointCode;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getDbid() {
            return dbid;
        }

        public void setDbid(String dbid) {
            this.dbid = dbid;
        }

        public String getPointCode() {
            return pointCode;
        }

        public void setPointCode(String pointCode) {
            this.pointCode = pointCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            ExamPoint examPoint = (ExamPoint) obj;
            return pointName.equals(examPoint.pointName);
        }

        @Override
        public int hashCode() {
            return pointName.hashCode();
        }
    }
}