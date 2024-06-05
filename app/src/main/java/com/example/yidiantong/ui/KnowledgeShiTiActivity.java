package com.example.yidiantong.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class KnowledgeShiTiActivity extends AppCompatActivity {
    private static final String TAG = "KnowledgeShiTiActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮
    private TextView tv_knowledge_name;

    //请求数据参数
    private int currentPage = 0;
    private String userName; //用户名
    private String subjectId;  //学科id
    private String course_name;  //学科名
    private String zhishidian;  //知识点
    private String zhishidianId;  //知识点id

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    private String questionIds;
    BookAutoAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String sourceId = "";

    private SharedPreferences preferences;
    private String[] autoStuLoadAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_book_exercise);

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

        TextView tv_title = findViewById(R.id.ftv_title);
        tv_title.setText("知识点试题");
        tv_knowledge_name = findViewById(R.id.tv_knowledge_name);
        tv_knowledge_name.setText(zhishidian);

        //加载页
        rl_loading = findViewById(R.id.rl_loading);
        fll_null = findViewById(R.id.fll_null);

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

    //刷新列表
    private void refreshList() {
        currentPage++;
        adapter.isRefresh = 1;
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
                itemList = (List<BookExerciseEntity>)receivedBundle.getSerializable("itemList");
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
        String mRequestUrl = Constant.API+"/AppServer/ajax/studentApp_getQuestionsZZXX.do?subjectId=" + subjectId + "&catalogId=" + zhishidianId;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getString("data");
                Log.d("wen0501", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
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

//        String response = "{\"code\":20000,\"data\":{\"items\":[{\"id\":\"1\",\"ques_id\":\"PRQUI9001174518\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":1,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第一章 预备知识/3 不等式/3.1 不等式性质/\",\"shiTiShow\":\"<p>若<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image021.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"60\\\" height=\\\"21\\\">，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image010.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"38\\\" height=\\\"21\\\">，则下列不等式成立的是（&nbsp;&nbsp;&nbsp;&nbsp;）</p><p>A．<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image037.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"47\\\" height=\\\"21\\\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; B．<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image178.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"125\\\" height=\\\"21\\\"></p><p>C．<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image179.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"80\\\" height=\\\"42\\\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; D．<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Show.files/image180.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"48\\\" height=\\\"42\\\"></p>\",\"shiTiAnalysis\":\"<p>对于A，由<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image021.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"60\\\" height=\\\"21\\\">，则<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image037.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"47\\\" height=\\\"21\\\">，故A正确；</p><p>对于B，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image504.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"477\\\" height=\\\"21\\\">，</p><p>由<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image021.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"60\\\" height=\\\"21\\\">，所以<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image505.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"125\\\" height=\\\"21\\\">，故B错误；</p><p>对于C，由<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image021.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"60\\\" height=\\\"21\\\">，可得<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image039.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"31\\\" height=\\\"42\\\">，所以<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image506.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"57\\\" height=\\\"42\\\">，</p><p>所以<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image507.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"80\\\" height=\\\"42\\\">，故C错误；</p><p>对于D，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image508.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"204\\\" height=\\\"42\\\">，</p><p>由<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image021.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"60\\\" height=\\\"21\\\">，则<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image509.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"72\\\" height=\\\"42\\\">，即<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174518/PRQUI9001174518Analysis.files/image180.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"48\\\" height=\\\"42\\\">，故D正确.</p><p>故选：AD.</p>\",\"questionsSource\":\"2.3.1不等式性质\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.4-0.2\",\"score\":5,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"102\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001174518</questionid><standard>答对得分，错不得分</standard><typeformat>多项选择形式</typeformat><itemstyle valuelist=\\\"A,B,C,D\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"checkbox\\\"><item id=\\\"1\\\">AD</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161099\",\"productId\":\"230044181174518\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.4-0.2\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3251333,\"markingMachineValue\":\"0\",\"pointNameStr\":\"\",\"typeId\":\"60000538\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":25,\"hours\":11,\"seconds\":15,\"month\":7,\"nanos\":913000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":42,\"time\":1692934935913,\"day\":5},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001174518\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p>AD</p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"多项选择题\",\"orderNum\":101174518,\"questionIdcopy\":\"PRQUI9001174518\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":18,\"hours\":15,\"seconds\":45,\"month\":3,\"nanos\":593000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":31,\"time\":1681803105593,\"day\":2},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":50,\"month\":3,\"nanos\":700000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":14,\"time\":1682129690700,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}}],\"total\":1}}";
//        try {
//            String itemString = "";
//            JSONObject json = new JSONObject(response);
//            JSONObject dataObject = json.getJSONObject("data");
//            JSONArray itemsArray = dataObject.getJSONArray("items");
//            for (int i = 0; i < itemsArray.length(); i++) {
//                JSONObject firstItem = itemsArray.getJSONObject(i);
//                JSONObject quesDataObject = firstItem.getJSONObject("ques_data");
//                JSONObject questionInfoObject = quesDataObject.getJSONObject("questionInfo");
//                if (i == itemsArray.length() - 1) {
//                    itemString += questionInfoObject.toString();
//                } else {
//                    itemString += questionInfoObject.toString() + ",";
//                }
//                System.out.println("itemString:" + itemString);
//                Log.d("wen0501", "itemString: " + itemString);
//            }
//            itemString = "[" + itemString + "]";
//            Gson gson = new Gson();
//            //使用Goson框架转换Json字符串为列表
//            itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
//            questionIds = "";
//            for (int i = 0; i < itemList.size(); i++) {
//                if (i == itemList.size() - 1) {
//                    questionIds += itemList.get(i).questionId;
//                } else {
//                    questionIds += itemList.get(i).questionId + ",";
//                }
//            }
//            System.out.println("questionIds:" + questionIds);
//            System.out.println("itemList:" + itemList);
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("itemList", (Serializable) itemList);
//            //封装消息，传递给主线程
//            Message message = Message.obtain();
//            message.obj = bundle;
//
//            // 发送消息给主线程
//            if (itemList == null || itemList.size() == 0 || itemString.equals("[]")) {
//                fll_null.setVisibility(View.VISIBLE);
//                return;
//            } else {
//                fll_null.setVisibility(View.GONE);
//            }
//            //标识线程
//            message.what = 100;
//            handler.sendMessage(message);
//        }catch (JSONException e) {
//            e.printStackTrace();
//        }
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


    private void selectorRefresh(String sourceId) {
        this.sourceId = sourceId;
    }

}