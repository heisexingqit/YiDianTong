package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BookUpAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.OnlineTestNullActivity;
import com.example.yidiantong.ui.BookUpDetailActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// 巩固提升界面, 用于展示巩固提升题目
public class BookUpFragment extends Fragment {
    private static final String TAG = "BookUpFragmentActivity";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮
    private ClickableImageView fiv_select_stu;//选择学生按钮


    //请求数据参数
    private int currentPage = -1;
    private String userName; //用户名
    private String reqName;  //请求用户名
    private String subjectId;  //学科id
    private String course_name;  //学科名

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();//试题列表
    private List<String> stuList = new ArrayList<>();//学生列表
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
        reqName = userName;

        TextView tv_title = view.findViewById(R.id.ftv_title);
        tv_title.setText(course_name + "巩固提升");

        //加载页
        rl_loading = view.findViewById(R.id.rl_loading);
        fll_null = view.findViewById(R.id.fll_null);

        //发送请求获取学生列表
        getStudentList();
        //加载学生按钮
        fiv_select_stu = view.findViewById(R.id.fiv_select_stu);
        fiv_select_stu.setVisibility(View.VISIBLE);
        fiv_select_stu.setOnClickListener(v -> {
            //将学生集合转换为数组
            String[] stuArr = new String[this.stuList.size()];
            for (int i = 0; i < this.stuList.size(); i++) {
                stuArr[i] = this.stuList.get(i);
            }
            System.out.println("学生列表：" + Arrays.toString(stuArr));
            //使用AlertDialog显示学生列表以供选择,右上角增加取消按钮
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        });

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
                stuList = gson.fromJson(itemString, new TypeToken<List<String>>() {}.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            System.out.println(error.toString());
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
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
        String mRequestUrl = Constant.API + Constant.GET_GONGGUTISHENG + "?userId=" + reqName +"&subjectId=" + subjectId + "&currentPage=" + currentPage;
        Log.d("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);
                Alert(message1);
                String itemString = json.getString("data");
                //TODO 打包前要修改
//                itemString = "[]";
                //当试题列表为空时,需要跳转中间页进行处理
                if (itemString.equals("[]") || itemString.equals("") || itemString == null) {
                    Intent intent = new Intent(getActivity(), OnlineTestNullActivity.class);
                    intent.putExtra("userName", userName); // 用户名
                    intent.putExtra("subjectId", subjectId); // 学科ID
                    intent.putExtra("courseName", course_name);  // 课程名
                    intent.putExtra("flag", "巩固提升"); // 巩固提升,用于在线测试判别
                    startActivity(intent);
                    getActivity().finish();
                }
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
            System.out.println(error.toString());
            Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
        });
        MyApplication.addRequest(request, TAG);
    }

    private void Alert(String alert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), AlertDialog.THEME_HOLO_LIGHT);
        //自定义title样式
        TextView tv = new TextView(getContext());
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