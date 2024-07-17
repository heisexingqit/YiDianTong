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
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class KnowledgeShiTiActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgeShiTiActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮
    private ClickableImageView fiv_select_stu;//选择学生按钮
    private TextView tv_knowledge_name;
    private TextView tv_massage;
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
    private List<Catalog> catalogs = new ArrayList<>();//考点列表
    private int examPointNum = 0;
    //    private List<ExamPoint> examPoints = new ArrayList<>();//考点列表
    private HashSet<String> selectedExamPoints = new HashSet<>();//已选考点列表
    private String questionIds;
    BookAutoAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String pointIds = "";

    private SharedPreferences preferences;
    private String[] autoStuLoadAnswer;
    private boolean isUpdatingAllCheckBoxes = false;
    private boolean isClickAllCheckBoxes = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_shiti);
        ((MyApplication)getApplication()).checkAndHandleGlobalVariables(this);
        preferences = getSharedPreferences("shiti", MODE_PRIVATE);

        tv_massage = findViewById(R.id.tv_massage);
//        tv_massage.setOnClickListener(v -> getKaoDianList());

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
        findViewById(R.id.fiv_back).setOnClickListener(v -> {
            Intent intent = new Intent(this, KnowledgeStudyChangeActivity.class);
            intent.putExtra("userName", userName);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("courseName", course_name);
            intent.putExtra("zhishidian", zhishidian);
            intent.putExtra("zhishidianId", zhishidianId);
            intent.putExtra("xueduanId", xueduan);
            intent.putExtra("banbenId", banben);
            intent.putExtra("jiaocaiId", jiaocai);
            intent.putExtra("unitId", unitId);
            startActivity(intent);
            finish();
        });

        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("知识点试题");
        tv_knowledge_name = findViewById(R.id.tv_knowledge_name);
        tv_knowledge_name.setText(zhishidian);
        ll_kaodian_filtrate = findViewById(R.id.ll_kaodian_filtrate);
        //点击后,页面弹出图层,显示章节下的考点,每个考点占一行,并且前面有一个复选框,默认全部为选中状态
//        ll_kaodian_filtrate.setOnClickListener(v -> showCheckableSubjectsDialog());
        ll_kaodian_filtrate.setOnClickListener(v -> getKaoDianList());

        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        fll_null = findViewById(R.id.fll_null);

        //发送请求获取学生列表
        /*getStudentList();*/
        //加载学生按钮
        /*fiv_select_stu = findViewById(R.id.fiv_select_stu);
        fiv_select_stu.setVisibility(View.VISIBLE);
        fiv_select_stu.setOnClickListener(v -> {
            //将学生集合转换为数组
            showStusDialog();
        });*/

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
            intent.putExtra("itemList", (Serializable) itemList);  // 知识点
            pos = pos + 1;
            intent.putExtra("pos", String.valueOf(pos));
            startActivity(intent);
        });

    }

    //展示学生列表
    /*private void showStusDialog() {
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
                userName = stuArr[which];
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
    }*/

    //加载学生列表
    /*private void getStudentList() {
        String mRequestUrl = Constant.API + "//AppServer/ajax/studentApp_getStuList.do";
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
    }*/


    //考点复选框事件
    private void showCheckableSubjectsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setPadding(16, 16, 16, 16);
        TextView title = new TextView(this);
        title.setText("选择考点");
        title.setTextSize(20);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));

        CheckBox selectAllCheckBox = new CheckBox(this);
        selectAllCheckBox.setText("全选");
        if (selectedExamPoints.size() == examPointNum) {
            selectAllCheckBox.setChecked(true);
        }

        headerLayout.addView(title);
        headerLayout.addView(selectAllCheckBox);
        mainLayout.addView(headerLayout);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (Catalog catalog : catalogs) {
            View catalogView = inflater.inflate(R.layout.item_catalog, null);
            TextView catalogName = catalogView.findViewById(R.id.catalogName);
            TextView catalogInfo = catalogView.findViewById(R.id.catalogInfo);
            catalogName.setText(catalog.getCatalogName());
            catalogInfo.setText(catalog.getInfo());
            layout.addView(catalogView);

            for (ExamPoint examPoint : catalog.getList()) {
                View itemView = inflater.inflate(R.layout.item_exam_point, null);
                CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                TextView textName = itemView.findViewById(R.id.textName);
                TextView masteryLevel = itemView.findViewById(R.id.masteryLevel);

                textName.setText(examPoint.getPointName() + "(" + examPoint.getDbid() + ")");
                masteryLevel.setText(examPoint.getInfo());
                checkBox.setChecked(selectedExamPoints.contains(examPoint.getDbid()));

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Log.d("syq", "onCheckedChanged: 我点击了复选框" + isChecked);
                    if (isChecked) {
                        if (!selectedExamPoints.contains(examPoint.getDbid())) {
                            selectedExamPoints.add(examPoint.getDbid());
                            System.out.println("选中的考点：" + selectedExamPoints.size() + " " + examPointNum);
                        }
                    } else {
                        selectedExamPoints.remove(examPoint.getDbid());
                    }
                    // 将其他与examPoint.getDbid()相同的复选框选中状态设置为与当前复选框相同
                    for (int i = 0; i < layout.getChildCount(); i++) {
                        View view = layout.getChildAt(i);
                        // 只处理包含复选框的子视图
                        if (view instanceof ViewGroup) {
                            CheckBox cb = view.findViewById(R.id.checkBox);
                            TextView tv = view.findViewById(R.id.textName);
                            if (cb != null && cb != checkBox && tv.getText().toString().contains(examPoint.getPointName())) {
                                cb.setChecked(isChecked);
                            }
                        }
                    }


                    if (!isClickAllCheckBoxes) {
                        Log.d("syq", "onCheckedChanged: 我走到了1号" + selectedExamPoints.size() + " " + examPointNum);
                        if (selectedExamPoints.size() == examPointNum) {
                            selectAllCheckBox.setChecked(true);
                        } else {
                            //防止一个一个取消复选框时，全选按钮都不选中
                            isUpdatingAllCheckBoxes = true;
                            selectAllCheckBox.setChecked(false);
                        }
                    }

                });
                layout.addView(itemView);
            }
        }

        //全选按钮事件,当全选按钮被选中时，将所有考点的复选框选中,否则取消选中
        selectAllCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("syq", "onCheckedChanged: 我点击了全选按钮" + isChecked);
            if (isUpdatingAllCheckBoxes) {
                isUpdatingAllCheckBoxes = false;
            } else {
                isClickAllCheckBoxes = true; // 防止点击全选按钮时，触发单个复选框的事件
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View itemView = layout.getChildAt(i);
                    // 只处理包含复选框的子视图
                    if (itemView instanceof ViewGroup) {
                        CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                        if (checkBox != null) {
                            checkBox.setChecked(isChecked);
                        }
                    }
                }
                selectedExamPoints.clear();
                if (isChecked) {
                    for (Catalog catalog : catalogs) {
                        for (ExamPoint examPoint : catalog.getList()) {
                            selectedExamPoints.add(examPoint.getDbid());
                        }
                    }
                }
                isClickAllCheckBoxes = false;
            }
        });

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(layout);
        mainLayout.addView(scrollView);
        builder.setView(mainLayout);
        builder.setPositiveButton("确定", (dialog, which) -> {
            // 将选中的考点中的dbid拼接到pointIds中
            pointIds = "";
            StringBuilder sb = new StringBuilder();
            for (String examPoint : selectedExamPoints) {
                sb.append(examPoint).append(",");
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
        String mRequestUrl = Constant.API + "//AppServer/ajax/studentApp_getPointsByCatalogId.do?" +
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
                catalogs = gson.fromJson(itemString, new TypeToken<List<Catalog>>() {
                }.getType());

                // TODO 去掉,py算法掌握度后面的字符串 && 对考点信息info进行处理, 保留两位小数
                /*for (ExamPoint examPoint : examPoints) {
                    String info = examPoint.getInfo();
                    // 清除两边的空格
                    info = info.trim();

                    // 去掉,py算法掌握度后面的字符串
//                    int index1 = info.indexOf(",py算法掌握度");
//                    info = info.substring(0, index1) + "】";

                    // 对考点信息info进行处理, 保留两位小数
                    // 例如：【掌握度:0.5555,有效答题2,正确率0.4286%,py算法掌握度】
                    int index2 = info.indexOf(":");
                    int index3 = info.indexOf(",");
                    Double value = Double.valueOf(info.substring(index2 + 1, index3));
                    DecimalFormat df = new DecimalFormat("0.00");
                    info = info.substring(0, index2 + 1) + df.format(value) + info.substring(index3);
                    System.out.println(info);
                    examPoint.setInfo(info);
                }*/

                if (selectedExamPoints == null || selectedExamPoints.size() == 0) {
                    //第一次默认全选,并将全部考点dbid拼接到pointIds中
                    for (Catalog catalog : catalogs) {
                        for (ExamPoint examPoint : catalog.getList()) {
                            selectedExamPoints.add(examPoint.getDbid());
                        }
                    }
                    examPointNum = selectedExamPoints.size();
                }
                pointIds = "";
                StringBuilder sb = new StringBuilder();
                for (String examPoint : selectedExamPoints) {
                    sb.append(examPoint).append(",");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                pointIds = sb.toString();
//                loadItems_Net();
                showCheckableSubjectsDialog();
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
//        getKaoDianList();
        loadItems_Net();
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
        String mRequestUrl = Constant.API + Constant.GET_ZIZHUXUEXI +
                "?userId=" + userName + "&subjectId=" + subjectId + "&catalogId=" + zhishidianId;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);
                // 根据返回的message展示tv_massage的内容
                if (message1.equals("选择章节已掌握")) {
                    tv_massage.setText("重新选择章节进行学习");
                    // 进行页面跳转 跳转到KnowledgePointActivity
                    tv_massage.setOnClickListener(v -> {
                        Intent intent = new Intent(this, KnowledgePointActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("xueduanId", xueduan);
                        intent.putExtra("xuekeId", subjectId);
                        intent.putExtra("xueke", course_name);
                        intent.putExtra("banbenId", banben);
                        intent.putExtra("jiaocaiId", jiaocai);
                        startActivity(intent);
                        finish();
                    });
                } else if (message1.equals("选择考点已掌握")) {
                    tv_massage.setText("继续下一个考点的学习");
                    tv_massage.setOnClickListener(v -> getKaoDianList());
                } else {
                    tv_massage.setText("选择考点下暂无试题");
                    // 红色字体
                    tv_massage.setTextColor(Color.parseColor("#FF0000"));
                }
                //Alert(message1);
                String itemString = json.getString("data");
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

    private static class Catalog {
        private String catalogName;
        private String catalogCode;
        private String info;
        private List<ExamPoint> list;

        public Catalog(String catalogName, String catalogCode, String info, List<ExamPoint> list) {
            this.catalogName = catalogName;
            this.catalogCode = catalogCode;
            this.info = info;
            this.list = list;
        }

        public String getCatalogName() {
            return catalogName;
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        public String getCatalogCode() {
            return catalogCode;
        }

        public void setCatalogCode(String catalogCode) {
            this.catalogCode = catalogCode;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public List<ExamPoint> getList() {
            return list;
        }

        public void setList(List<ExamPoint> list) {
            this.list = list;
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