package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BookUpAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.BookUpDetailActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 巩固提升界面, 用于展示巩固提升题目
public class BookUpFragment extends Fragment {
    private static final String TAG = "BookUpFragmentActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮


    //请求数据参数
    private int currentPage = 0;
    private String userName; //用户名
    private String subjectId;  //学科id
    private String course_name;  //学科名

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    private String questionIds;
    BookUpAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;// 题目ID
    private String questionNumber; //题目号

    private String sourceId = "";

    private SharedPreferences preferences;
    private String[] upStuLoadAnswer;

    private ActivityResultLauncher<Intent> mResultLauncher;  //用于处理Intent的返回结果

    public static BookUpFragment newInstance() {
        BookUpFragment fragment = new BookUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("shiti", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_book_up, container, false);
        Log.e("wen","BookUpFragment:巩固提升");
        //顶栏返回按钮
        view.findViewById(R.id.fiv_back).setOnClickListener(v -> {
            getActivity().finish();
        });

        frv_detail = view.findViewById(R.id.frv_detail);
        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getActivity().getIntent().getStringExtra("userName");  //用户名
        subjectId = getActivity().getIntent().getStringExtra("subjectId"); //学科名
        course_name = getActivity().getIntent().getStringExtra("courseName"); //学科id

        TextView tv_title = view.findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "巩固提升");

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);
        fll_null = view.findViewById(R.id.fll_null);

        //刷新题目按钮
        fiv_refresh = view.findViewById(R.id.fiv_refresh);
        fiv_refresh.setVisibility(View.VISIBLE);
        fiv_refresh.setOnClickListener(v -> {
            rl_loading.setVisibility(View.VISIBLE);
            fll_null.setVisibility(View.GONE);
            refreshList();
        });

        //设置RecyclerViewAdapter
        adapter = new BookUpAdapter(getContext(), itemList);
        frv_detail.setAdapter(adapter);

        //refreshList();

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(getActivity(), BookUpDetailActivity.class);
            BookExerciseEntity item = itemList.get(pos);
            intent.putExtra("questionId", item.questionId);
            intent.putExtra("subjectId", subjectId);
            intent.putExtra("username", userName);
            intent.putExtra("name", course_name);
            intent.putExtra("allpage", String.valueOf(itemList.size()));
            intent.putExtra("questionIds", questionIds);
            pos = pos + 1;
            intent.putExtra("pos", String.valueOf(pos));
            startActivity(intent);
        });

        return view;
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
                itemList = (List<BookExerciseEntity>) receivedBundle.getSerializable("itemList");
                adapter.loadData(itemList);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;

                //创建本地数组保存学生作答信息
                SharedPreferences.Editor edit = preferences.edit();
                upStuLoadAnswer = new String[itemList.size()];
                String upArrayString = TextUtils.join(",", upStuLoadAnswer);
                System.out.println("upArrayString：" + upArrayString);
                edit.putString("upStuLoadAnswer", upArrayString);
                edit.apply();
            }
        }
    };

    //发送请求获得错题数据,对请求到的数据进行处理
    private void loadItems_Net() {
        if (adapter.isRefresh == 1) {
            fll_null.setVisibility(View.GONE);
            rl_loading.setVisibility(View.VISIBLE);
        }

//        String response = "{\"code\":20000,\"data\":{\"items\":[{\"id\":\"1\",\"ques_id\":\"PRQUI9001174431\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":1,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第一章 预备知识/2 常用逻辑用语/2.2 全称量词与存在量词/\",\"shiTiShow\":\"<p>下列命题中，错误的是（&nbsp;&nbsp;&nbsp;&nbsp;）</p><p>A．“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image291.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"34\\\" height=\\\"21\\\">”是“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image292.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"97\\\" height=\\\"21\\\">”的必要不充分条件</p><p>B．<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image105.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"43\\\" height=\\\"21\\\">，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image293.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"73\\\" height=\\\"21\\\"></p><p>C．命题“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image103.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"42\\\" height=\\\"21\\\">，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Show.files/image294.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"65\\\" height=\\\"21\\\">”的否定为假命题</p><p>D．“三角形为等腰三角形”是“三角形为正三角形”的必要不充分条件</p>\",\"shiTiAnalysis\":\"<p>对于A选项，解方程<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image292.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"97\\\" height=\\\"21\\\">可得<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image433.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"34\\\" height=\\\"21\\\">或<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image291.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"34\\\" height=\\\"21\\\">，</p><p>所以，“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image291.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"34\\\" height=\\\"21\\\">”是“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image292.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"97\\\" height=\\\"21\\\">”的充分不必要条件，A错；</p><p>对于B选项，当<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image433.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"34\\\" height=\\\"21\\\">时，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image519.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"73\\\" height=\\\"21\\\">，B错；</p><p>对于C选项，对于方程<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image219.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"90\\\" height=\\\"21\\\">，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image520.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"85\\\" height=\\\"21\\\">，即方程<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image219.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"90\\\" height=\\\"21\\\">无实解，</p><p>故命题“<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image016.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"41\\\" height=\\\"21\\\">，<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image294.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"65\\\" height=\\\"21\\\">”为假命题，其否定为真命题，C错；</p><p>对于D选项，“三角形为等腰三角形”<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image521.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"12\\\" height=\\\"21\\\">“三角形为正三角形”，</p><p>但“三角形为等腰三角形”<img src=\\\"http://www.cn901.com/html1/2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001174431/PRQUI9001174431Analysis.files/image522.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"12\\\" height=\\\"21\\\">“三角形为正三角形”，</p><p>所以，“三角形为等腰三角形”是“三角形为正三角形”的必要不充分条件，D对.</p><p>故选：ABC.</p>\",\"questionsSource\":\"2.2.2全称量词与存在量词\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.2-0\",\"score\":5,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"102\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001174431</questionid><standard>答对得分，错不得分</standard><typeformat>多项选择形式</typeformat><itemstyle valuelist=\\\"A,B,C,D\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"checkbox\\\"><item id=\\\"1\\\">ABC</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161097\",\"productId\":\"235042181174431\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.2-0\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3251159,\"markingMachineValue\":\"0\",\"pointNameStr\":\"\",\"typeId\":\"60000538\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":12,\"hours\":11,\"seconds\":1,\"month\":6,\"nanos\":363000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":56,\"time\":1689134161363,\"day\":3},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001174431\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p>ABC</p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"多项选择题\",\"orderNum\":101174431,\"questionIdcopy\":\"PRQUI9001174431\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":18,\"hours\":15,\"seconds\":43,\"month\":3,\"nanos\":440000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":24,\"time\":1681802683440,\"day\":2},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/18/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":50,\"month\":3,\"nanos\":700000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":14,\"time\":1682129690700,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}},{\"id\":\"2\",\"ques_id\":\"PRQUI9001178522\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":12,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第六章 统计/2 抽样的基本方法/2.1 简单随机抽样/\",\"shiTiShow\":\"<p>判断下列是否是随机抽样？</p><p>(1)从自然数中抽取100个研究素数的比例；</p><p>(2)从一箱100个零件中抽取5个进行质量调查；</p><p>(3)在一个班级40人中选5人参加志愿者，其中在身高最高的5人中抽取4个．</p>\",\"shiTiAnalysis\":\"<p>（1）随机抽样是在有限多个个体中进行抽取，所以从自然数中抽取100个研究素数不是随机抽样.</p><p>（2）从一箱100个零件中抽取5个进行质量调查符合随机抽样的特点，在有限多个个体中，通过逐个抽取的方法抽取样本，且每次抽取时各个个体被抽到的概率相等，所以是随机抽样.</p><p>（3）在身高最高的5人中抽取4个不满足每次抽取时各个个体被抽到的概率相等，所以在一个班级40人中选5人参加志愿者，其中在身高最高的5人中抽取4个不是随机抽样.</p>\",\"questionsSource\":\"6.2.1简单随机抽样\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.4-0.2\",\"score\":12,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"106\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001178522</questionid><standard>答对得分，错不得分</standard><typeformat>简答形式</typeformat><itemstyle valuelist=\\\"Office\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"Office\\\"><item id=\\\"1\\\">PRQUI9001178522</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161149\",\"productId\":\"230040211178522\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.4-0.2\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3258752,\"markingMachineValue\":\"1\",\"pointNameStr\":\"\",\"typeId\":\"60000056\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":12,\"hours\":13,\"seconds\":13,\"month\":6,\"nanos\":820000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":12,\"time\":1689138733820,\"day\":3},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"不可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001178522\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p>(1)不是</p><p>(2)是</p><p>(3)不是</p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"解答题\",\"orderNum\":101178522,\"questionIdcopy\":\"PRQUI9001178522\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":21,\"hours\":10,\"seconds\":41,\"month\":3,\"nanos\":617000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":24,\"time\":1682043881617,\"day\":5},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/21/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":31,\"month\":3,\"nanos\":157000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":11,\"time\":1682129491157,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}},{\"id\":\"3\",\"ques_id\":\"PRQUI9001175752\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":1,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第三章 指数运算与指数函数/1 指数幂的拓展/\",\"shiTiShow\":\"<p>若<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image026.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"135\\\" height=\\\"21\\\">，则<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image027.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"22\\\" height=\\\"21\\\">（&nbsp;&nbsp;&nbsp;&nbsp;）</p><p>A．<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image028.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"26\\\" height=\\\"42\\\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; B．<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image029.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"26\\\" height=\\\"42\\\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; C．<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image030.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"19\\\" height=\\\"42\\\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; D．<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Show.files/image031.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"19\\\" height=\\\"42\\\"></p>\",\"shiTiAnalysis\":\"<p>因为<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Analysis.files/image347.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"64\\\" height=\\\"21\\\">，则<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Analysis.files/image348.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"124\\\" height=\\\"42\\\">，即<img src=\\\"http://www.cn901.com/html1/2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001175752/PRQUI9001175752Analysis.files/image349.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"52\\\" height=\\\"42\\\">．</p><p>故选：B.</p>\",\"questionsSource\":\"4.1指数幂的拓展\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.2-0\",\"score\":5,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"101\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001175752</questionid><standard>答对得分，错不得分</standard><typeformat>单项选择形式</typeformat><itemstyle valuelist=\\\"A,B,C,D\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"radio\\\"><item id=\\\"1\\\">B</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161117\",\"productId\":\"234044191175752\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.2-0\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3253610,\"markingMachineValue\":\"0\",\"pointNameStr\":\"\",\"typeId\":\"60000055\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":26,\"hours\":19,\"seconds\":9,\"month\":7,\"nanos\":830000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":32,\"time\":1693049529830,\"day\":6},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001175752\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p>B</p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"单项选择题\",\"orderNum\":101175752,\"questionIdcopy\":\"PRQUI9001175752\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":19,\"hours\":15,\"seconds\":20,\"month\":3,\"nanos\":797000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":39,\"time\":1681889960797,\"day\":3},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/19/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":15,\"month\":3,\"nanos\":547000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":18,\"time\":1682129895547,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}},{\"id\":\"4\",\"ques_id\":\"PRQUI9001178421\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":12,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第六章 统计/1 获取数据的途径/1.3 总体和样本/\",\"shiTiShow\":\"<p>在下面两个问题中，指出总体和样本分别是什么？样本量是多少？</p><p>(1)一项民意调查联络了1000位路人，问他们：“你对该区域的绿化是满意还是不满意？”</p><p>(2)为了解各种品牌尿布的价格行情，一位准妈妈在某超市挑选了10种品牌的尿布，并记录了它们的价格．</p>\",\"shiTiAnalysis\":\"<p>（1）联络1000位路人，对该区域的绿化的满意情况调查，</p><p>总体是绿化满意情况，样本是1000位路人的绿化满意情况，样本量为1000.</p><p>（2）了解各种品牌尿布的价格行情，在某超市挑选了10种品牌的尿布，并记录了它们的价格，</p><p>总体是各品牌尿布的价格，样本是挑选的10种品牌尿布的价格，样本量为10.</p>\",\"questionsSource\":\"6.1.3总体和样本\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.2-0\",\"score\":12,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"106\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001178421</questionid><standard>答对得分，错不得分</standard><typeformat>简答形式</typeformat><itemstyle valuelist=\\\"Office\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"Office\\\"><item id=\\\"1\\\">PRQUI9001178421</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161147\",\"productId\":\"231047211178421\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.2-0\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3258550,\"markingMachineValue\":\"1\",\"pointNameStr\":\"\",\"typeId\":\"60000056\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":12,\"hours\":13,\"seconds\":4,\"month\":6,\"nanos\":530000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":12,\"time\":1689138724530,\"day\":3},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"不可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001178421\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p>(1)总体是绿化满意情况，样本是1000位路人的绿化满意情况，样本量1000；</p><p>(2)总体是各品牌尿布的价格，样本是挑选的10种品牌尿布的价格，样本量10.</p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"解答题\",\"orderNum\":101178421,\"questionIdcopy\":\"PRQUI9001178421\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":21,\"hours\":10,\"seconds\":12,\"month\":3,\"nanos\":757000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":19,\"time\":1682043552757,\"day\":5},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/21/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":31,\"month\":3,\"nanos\":157000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":11,\"time\":1682129491157,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}},{\"id\":\"5\",\"ques_id\":\"PRQUI9001177829\",\"ques_data\":{\"questionInfo\":{\"isUse\":\"1\",\"updateCount\":0,\"answerTime\":1,\"pointName\":\"高中数学(北师大版(2019))/必修第一册/第四章 对数运算与对数函数/4 指数函数、幂函数、对数函数增长的比较/\",\"shiTiShow\":\"<p>若<img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Show.files/image385.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"107\\\" height=\\\"42\\\">，则实数<img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Show.files/image185.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"8\\\" height=\\\"21\\\">的值是______.</p>\",\"shiTiAnalysis\":\"<p><img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Analysis.files/image1033.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"238\\\" height=\\\"42\\\">，</p><p>即<img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Analysis.files/image1034.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"69\\\" height=\\\"21\\\">，解得：<img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Analysis.files/image1035.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"32\\\" height=\\\"42\\\">.</p><p>故答案为：<img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Analysis.files/image1032.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"6\\\" height=\\\"42\\\"></p>\",\"questionsSource\":\"4.4指数函数、幂函数、对数函数增长的比较\",\"questionText\":\"000\",\"subjectId\":\"10017\",\"deleteFlag\":0,\"questionDifferent\":\"0.2-0\",\"score\":5,\"shareTag\":\"99\",\"dictionaryName\":\"\",\"price\":0,\"gradeBookCode\":\"TB00001781\",\"annexText\":\"\",\"baseTypeId\":\"104\",\"checkLink\":9,\"textBookName\":\"北师大版(2019)\",\"priceUnit\":\"1\",\"checkNumber\":1,\"isPortalShow\":\"1\",\"answerText\":\"<?xml version=\\\"1.0\\\" encoding=\\\"gb2312\\\"?><root><answer version=\\\"1.0\\\" systemversion=\\\"1.0\\\" editype=\\\"Form\\\" editversion=\\\"1.0\\\"><questionid>PRQUI9001177829</questionid><standard>答对得分，错不得分</standard><typeformat>填空形式</typeformat><itemstyle valuelist=\\\"Office\\\" groupnum=\\\"1\\\" answernumber=\\\"1\\\" answerType=\\\"Office\\\"><item id=\\\"1\\\">PRQUI9001177829</item></itemstyle></answer></root>\",\"pointCode\":\"ZSD00000000161132\",\"productId\":\"230045201177829\",\"checkDateTimeStr\":\"\",\"analysisControl\":\"\",\"questionid1\":\"\",\"shiTiShow1\":\"\",\"contentMark\":\"10\",\"questionDifficult\":\"0.2-0\",\"questionTextControl\":\"OfficeEditor\",\"click\":0,\"answerControl\":\"OfficeEditor\",\"checkUser\":\"zengzeng\",\"companyId\":100,\"questionKeyword\":\"\",\"dbid\":3257366,\"markingMachineValue\":\"1\",\"pointNameStr\":\"\",\"typeId\":\"60000054\",\"gradeCode\":\"\",\"updateDateTime\":{\"date\":12,\"hours\":13,\"seconds\":47,\"month\":6,\"nanos\":427000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":13,\"time\":1689138827427,\"day\":3},\"answerAnalysisText\":\"\",\"modifiedDiscrimination\":0,\"agentId\":\"\",\"markingMachine\":\"不可计算机判卷\",\"districtCode\":\"610113000 \",\"questionId\":\"PRQUI9001177829\",\"questionIdcopyHistory\":\"\",\"shiTiAnswer\":\"<p><img src=\\\"http://www.cn901.com/html1/2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/PRQUI9001177829/PRQUI9001177829Answer.files/image1032.png\\\"  style=\\\"vertical-align: middle;\\\" width=\\\"6\\\" height=\\\"42\\\"></p>\",\"exposalDate\":null,\"modifiedDifficulty\":0,\"questionCommand\":\"闭卷\",\"typeName\":\"填空题\",\"orderNum\":101177829,\"questionIdcopy\":\"PRQUI9001177829\",\"dbcode\":\"1       \",\"priceDateTime\":null,\"secrecy\":\"\",\"unitId\":\"1101010010001\",\"annexTextCopy1\":\"\",\"annexTextCopy2\":\"\",\"channelCode\":\"C0003\",\"createDate\":{\"date\":20,\"hours\":23,\"seconds\":54,\"month\":3,\"nanos\":853000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":58,\"time\":1682006334853,\"day\":4},\"subjectName\":\"高中数学\",\"webSiteId\":\"1         \",\"gradeName\":\"\",\"auditOpinion\":\"\",\"questionTitle\":\"\",\"questionidcopy1\":\"\",\"textBookId\":\"20212\",\"filePath\":\"2023/04/20/10017/20212/D676DA1546/120375D142/55FFE75510/\",\"questionStandard\":\"答对得分，错不得分\",\"updateUser\":\"zengzeng\",\"priceStatus\":\"1\",\"checkDate\":{\"date\":22,\"hours\":10,\"seconds\":32,\"month\":3,\"nanos\":190000000,\"timezoneOffset\":-480,\"year\":123,\"minutes\":13,\"time\":1682129612190,\"day\":6},\"dictionaryCode\":\"\",\"questionSubNum\":1,\"createDateTimeStr\":\"\",\"usedTime\":0,\"gradeBookName\":\"必修第一册\",\"questionDegrade\":\"了解\",\"annexControl\":\"\",\"updateDateTimeStr\":\"\",\"channelName\":\"高中\",\"createUser\":\"guord\",\"createUserBack\":\"1\"}}}],\"total\":5}}";
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


        // 获取巩固提升题目
        String mRequestUrl = Constant.API+ "/AppServer/ajax/studentApp_getQuestionsGGTS.do?subjectId=" + subjectId;
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
            Toast.makeText(BookUpFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    //慢加载
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isResumed()) {
                refreshList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getUserVisibleHint()) {
//            refreshList();
//        }
        if (loadFirst) {
            refreshList();
            loadFirst = false;
        }
    }

    private void selectorRefresh(String sourceId) {
        this.sourceId = sourceId;
    }

}