package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.adapter.BookExerciseAdapter;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.ui.BookExerciseDetailActivity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 举一反三界面, 用于展示举一反三题目
public class BookExerciseFragment extends Fragment {
    private static final String TAG = "BookExerciseFragment";
    private View contentView = null;
    private SwipeRefreshLayout swipeRf;
    private Boolean loadFirst = true;

    private RelativeLayout rl_loading;
    private ClickableImageView fiv_refresh;//刷新题目按钮
    private TextView tv_knowledge_name;//原试题考点

    //请求数据参数
    private int currentPage = -1;
    private String userName; //用户名
    private String course_Id;  //学科id
    private String course_name;  //学科名
    private String questionIdd; //举一反三题目id

    //列表数据
    private List<BookExerciseEntity> itemList = new ArrayList<>();
    private String questionIds;//全部题目id
    BookExerciseAdapter adapter;
    private RecyclerView frv_detail;

    private LinearLayout fll_null;
    private ImageView fiv_recycle;
    private String questionId;

    private String sourceId = "";

    private SharedPreferences preferences;
    private String[] exerciseStuLoadAnswer;

    private ActivityResultLauncher<Intent> mResultLauncher;  //用于处理Intent的返回结果

    public static BookExerciseFragment newInstance() {
        BookExerciseFragment fragment = new BookExerciseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences = getActivity().getSharedPreferences("shiti", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_book_exercise, container, false);
        //顶栏返回按钮
        view.findViewById(R.id.fiv_back).setOnClickListener(v -> {
            getActivity().finish();
        });

        frv_detail = view.findViewById(R.id.frv_detail);
        //RecyclerView两步必要配置
        frv_detail.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        frv_detail.setItemAnimator(new DefaultItemAnimator());

        //获取Intent参数,设置学科错题本最上面的内容
        userName = getActivity().getIntent().getStringExtra("userName"); //用户名
        course_Id = getActivity().getIntent().getStringExtra("subjectId");  //学科id
        course_name = getActivity().getIntent().getStringExtra("courseName");  //学科名
        questionIdd = getActivity().getIntent().getStringExtra("questionId"); //举一反三题目id

        TextView tv_title = view.findViewById(R.id.ftv_title);
        tv_title.setText("举一反三");

        tv_knowledge_name = view.findViewById(R.id.tv_knowledge_name);

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
        adapter = new BookExerciseAdapter(getContext(), itemList);
        frv_detail.setAdapter(adapter);

        //refreshList();

        //设置item点击事件
        adapter.setmItemClickListener((v, pos) -> {
            Intent intent = new Intent(getActivity(), BookExerciseDetailActivity.class);
            BookExerciseEntity item = itemList.get(pos);
            intent.putExtra("questionId", item.questionId); //题目id
            intent.putExtra("subjectId", course_Id);  // 学科id
            intent.putExtra("username", userName);  // 用户名
            intent.putExtra("name", course_name);  // 学科名
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
                // 获得列表信息
                itemList = (List<BookExerciseEntity>) receivedBundle.getSerializable("itemList");
                adapter.loadData(itemList);
                rl_loading.setVisibility(View.GONE);
                currentPage += 1;

                //创建本地数组保存学生作答信息
                SharedPreferences.Editor edit = preferences.edit();
                exerciseStuLoadAnswer = new String[itemList.size()];
                String exerciseArrayString = TextUtils.join(",", exerciseStuLoadAnswer);
                System.out.println("exerciseArrayString：" + exerciseArrayString);
                edit.putString("exerciseStuLoadAnswer", exerciseArrayString);
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
        String mRequestUrl = Constant.API + Constant.GET_JUYIFANSAN + "?userId=" + userName + "&questionId="
                + questionIdd + "&currentPage=" + currentPage;
        Log.e("wen0223", "loadItems_Net: " + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String message1 = json.getString("message");
                Log.d("song0321", "message: " + message1);
                String[] split = message1.split("@_@");
                //弹出警告框,将数组第一个元素作为警告框内容,第二个元素作为原试题考点
                Alert(split[0]);
                tv_knowledge_name.setText("原试题考点: " + split[1]);
                String itemString = json.getString("data");
                Log.d("song0321", "itemString: " + itemString);
                Gson gson = new Gson();
                //使用Goson框架转换Json字符串为列表
                itemList = gson.fromJson(itemString, new TypeToken<List<BookExerciseEntity>>() {}.getType());
                questionIds = "";//试题id数组
                for (int i = 0; i < itemList.size(); i++) {
                    if (i == itemList.size() - 1) {
                        questionIds += itemList.get(i).getQuestionId();
                    } else {
                        questionIds += itemList.get(i).getQuestionId() + ",";
                    }
                }
                System.out.println("questionIds：" + questionIds);
                Log.e("wen0321", "itemList: " + itemList);

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
            Toast.makeText(BookExerciseFragment.this.getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
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