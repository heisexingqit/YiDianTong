package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.THomeworkAddPickPagerAdapter;
import com.example.yidiantong.bean.THomeworkAddEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class THomeworkPickAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "THomeworkPickAddFragmen";

    // URL
    private String mRequestUrl = "";

    // 记录选题内容
    public List<THomeworkAddEntity> pickList = new ArrayList<>();

    // 当前位置
    private int nowPos = 0;

    // 上一个被选中的Tab块
    private ClickableImageView lastImageView;

    // 当前页码
    private int currentpage = 1;

    // 最后一页
    private boolean isAll = false;


    // 主体ViewPager
    private NoScrollViewPager vp_main;
    // 主体adapter
    private THomeworkAddPickPagerAdapter adapter;

    // 顶部按钮
    private TextView tv_count;
    private ClickableImageView iv_add;

    // 底部Tag块
    private LinearLayout ll_bottom_tab;
    private HorizontalScrollView sv_bottom_tab;

    private TextView tv_hide;

    // 请求试题库参数
    String xueduan = "";
    String xueke = "";
    String banben = "";
    String jiaocai = "";
    String zhishidian = "";
    String type = "";
    String shareTag = "";

    private LinearLayout ll_loading;
    private LinearLayout ll_loading2;

    public THomeworkPickAddFragment(String xd, String xk, String bb, String jc, String zsd, String type, String shareTag) {
        xueduan = xd;
        xueke = xk;
        banben = bb;
        jiaocai = jc;
        zhishidian = zsd;
        this.type = type;
        this.shareTag = shareTag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.e("wen052321", "onCreateView: 启动");
        View view = inflater.inflate(R.layout.fragment_t_homework_pick_add, container, false);
        ll_loading = view.findViewById(R.id.ll_loading);
        ll_loading2 = view.findViewById(R.id.ll_loading2);

        ll_bottom_tab = view.findViewById(R.id.ll_bottom_tab);
        sv_bottom_tab = view.findViewById(R.id.sv_bottom_tab);
        tv_hide = view.findViewById(R.id.tv_hide);

        vp_main = view.findViewById(R.id.vp_main);

        if (adapter == null) {
            adapter = new THomeworkAddPickPagerAdapter(getActivity().getSupportFragmentManager(), new ArrayList<>());
        }
        vp_main.setAdapter(adapter);
        vp_main.setCurrentItem(nowPos);

        // 底部翻页按钮
        view.findViewById(R.id.iv_left).setOnClickListener(this);
        view.findViewById(R.id.iv_right).setOnClickListener(this);

        // 添加本题
        tv_count = view.findViewById(R.id.tv_count);
        iv_add = view.findViewById(R.id.iv_add);
        iv_add.setOnClickListener(this);
        Log.e("wen", "启动页面");
        Log.e("wen", "列表长度 " + adapter.itemList.size());

        // 判断数据是否已存在
        if (adapter.itemList.size() == 0) {
            loadItems_Net();
        } else {
            ll_loading.setVisibility(View.GONE);// 解除遮挡
            ll_loading2.setVisibility(View.GONE);// 解除遮挡
            tv_hide.setVisibility(View.GONE);
        }
        Log.e("wen0523", "onCreateView: " + adapter.itemList.size());

        tv_count.setText("(已选择" + pickList.size() + ")");

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 延迟初始化获取组件宽度
        ViewTreeObserver vto = sv_bottom_tab.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onGlobalLayout() {
                if (adapter.itemList.size() != 0) {
                    showQuestionBlock(adapter.itemList);
                    adapter.notifyDataSetChanged();
                }
                sv_bottom_tab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    // 更新试题内容方法
    public void updateItem(String xd, String xk, String bb, String jc, String zsd, String type, String shareTag) {
        Log.e("wen0523", "updateItem: " + xd + " " + xk + " " + bb + " "+ jc + " " + zsd + " " + type + " " + shareTag);
        xueduan = xd;
        xueke = xk;
        banben = bb;
        jiaocai = jc;
        zhishidian = zsd;
        this.type = type;
        this.shareTag = shareTag;
        currentpage = 1;
        loadItems_Net();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                if (currentpage == 1) {
                    Toast.makeText(getActivity(), "已经是第一页", Toast.LENGTH_SHORT).show();
                } else {
                    currentpage -= 1;
                    loadItems_Net();
                }
                break;
            case R.id.iv_right:
                if (!isAll) {
                    currentpage += 1;
                    loadItems_Net();
                } else {
                    Toast.makeText(getActivity(), "已经最后一页", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_add:
                if (adapter.itemList == null || adapter.itemList.size() == 0) {
                    break;
                }
                String qId = adapter.itemList.get(nowPos).getQuestionId();
                if (pickList.stream().anyMatch(obj -> obj.getQuestionId().equals(qId))) {
                    pickList.removeIf(obj -> obj.getQuestionId().equals(qId));
                    sortPickList();
                    iv_add.setImageResource(R.drawable.add_homework);
                } else {
                    Log.e("wen052321", "onClick: " + pickList);
                    pickList.add(adapter.itemList.get(nowPos));
                    sortPickList();
                    iv_add.setImageResource(R.drawable.minus_homework);
                }
                tv_count.setText("(已选择" + pickList.size() + ")");
                break;

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showQuestionBlock(List<THomeworkAddEntity> moreList) {

        ll_bottom_tab.removeAllViews();
        for (int i = 0; i < moreList.size(); ++i) {
            THomeworkAddEntity item = moreList.get(i);
            ClickableImageView imageView = new ClickableImageView(getActivity());
            int resId = 0;
            switch (item.getBaseTypeId()) {
                case "101":
                    resId = R.drawable.t_homework_101;
                    break;
                case "102":
                    resId = R.drawable.t_homework_102;
                    break;
                case "103":
                    resId = R.drawable.t_homework_103;
                    break;
                case "104":
                    resId = R.drawable.t_homework_104;
                    break;
                case "105":
                    resId = R.drawable.t_homework_105;
                    break;
                case "106":
                    resId = R.drawable.t_homework_106;
                    break;
                case "107":
                    resId = R.drawable.t_homework_107;
                    break;
                case "108":
                    resId = R.drawable.t_homework_108;
                    break;
                case "109":
                    resId = R.drawable.t_homework_109;
                    break;
                case "110":
                    resId = R.drawable.t_homework_110;
                    break;
                case "111":
                    resId = R.drawable.t_homework_111;
                    break;
                case "112":
                    resId = R.drawable.t_homework_112;
                    break;
                default:
                    Log.d("wen", "意外类型: " + item);
            }

            int dp_1 = PxUtils.dip2px(getActivity(), 1);
            int pxMargin = 3 * dp_1;
            int myWidth = sv_bottom_tab.getWidth() / 5 - 2 * pxMargin - 2 * dp_1;
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(myWidth, myWidth);
            params.setMargins(pxMargin, pxMargin, pxMargin, pxMargin);
            imageView.setPadding(pxMargin, pxMargin, pxMargin, pxMargin);
            imageView.setLayoutParams(params);
            imageView.setImageResource(resId);
            if (i == nowPos) {
                imageView.setBackgroundResource(R.drawable.t_homework_add_border2);
                lastImageView = imageView;
                String qId = item.getQuestionId();
                if (pickList.stream().anyMatch(obj -> obj.getQuestionId().equals(qId))) {
                    iv_add.setImageResource(R.drawable.minus_homework);
                } else {
                    iv_add.setImageResource(R.drawable.add_homework);
                }
            }

            imageView.setTag(i);
            imageView.setOnClickListener(v -> {
                if (lastImageView != v) {
                    lastImageView.setBackgroundResource(0);
                    v.setBackgroundResource(R.drawable.t_homework_add_border2);
                    nowPos = (int) v.getTag();
                    vp_main.setCurrentItem(nowPos, false);
                    String qId = adapter.itemList.get(nowPos).getQuestionId();
                    if (pickList.stream().anyMatch(obj -> obj.getQuestionId().equals(qId))) {
                        iv_add.setImageResource(R.drawable.minus_homework);
                    } else {
                        iv_add.setImageResource(R.drawable.add_homework);
                    }
                }
                lastImageView = (ClickableImageView) v;
            });
            ll_bottom_tab.addView(imageView);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 100) {
                List<THomeworkAddEntity> moreList = (List<THomeworkAddEntity>) message.obj;
                nowPos = 0;
                vp_main.setCurrentItem(nowPos, false);
                showQuestionBlock(moreList);
                ll_loading.setVisibility(View.GONE);// 解除遮挡
                ll_loading2.setVisibility(View.GONE);// 解除遮挡
            }
        }
    };

    private void loadItems_Net() {
        Log.e("wen", "loadItems_Net: " + xueduan);
        Log.e("wen", "loadItems_Net: " + xueke);
        Log.e("wen", "loadItems_Net: " + zhishidian);
        Log.e("wen", "loadItems_Net: " + banben);
        Log.e("wen", "loadItems_Net: " + jiaocai);
        Log.e("wen", "loadItems_Net: " + type);
        Log.e("wen", "loadItems_Net: " + shareTag);

        if (StringUtils.hasEmptyString(xueduan, xueke, zhishidian, banben, jiaocai, type, shareTag)) {
            return;
        }
        ll_loading.setVisibility(View.VISIBLE);// 遮挡
        ll_loading2.setVisibility(View.VISIBLE);// 遮挡

        mRequestUrl = Constant.API + Constant.T_HOMEWORK_GET_ALL_QUESTIONS + "?channelCode=" + xueduan + "&subjectCode=" + xueke +
                "&textBookCode=" + banben + "&gradeLevelCode=" + jiaocai + "&pointCode=" + zhishidian + "&currentpage=" + currentpage +
                "&teacherId=" + MyApplication.username + "&questionTypeName=" + type + "&shareTag=" + shareTag;
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);

                String itemString = json.getString("data");
                Gson gson = new Gson();
                List<THomeworkAddEntity> moreList = gson.fromJson(itemString, new TypeToken<List<THomeworkAddEntity>>() {
                }.getType());

                if (moreList.size() < 5)
                    isAll = true;


                if (moreList.size() == 0) {
                    if (currentpage == 1) {
                        tv_hide.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                        currentpage -= 1;
                    }
                    isAll = true;
                    ll_loading.setVisibility(View.GONE);// 解除遮挡
                    ll_loading2.setVisibility(View.GONE);// 解除遮挡
                    if(currentpage != 1){
                        return;
                    }
                } else {
                    tv_hide.setVisibility(View.GONE);
                }

                adapter.update(moreList);

                Message message = Message.obtain();
                message.obj = moreList;
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            ll_loading.setVisibility(View.GONE);// 解除遮挡
            ll_loading2.setVisibility(View.GONE);// 解除遮挡
            Log.e("volley", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
    }

    // 创建一个自定义比较器
    Comparator<THomeworkAddEntity> customComparator = new Comparator<THomeworkAddEntity>() {
        @Override
        public int compare(THomeworkAddEntity obj1, THomeworkAddEntity obj2) {
            // 你需要根据对象的 propertyName 属性来比较
            // 假设 propertyName 是 String 类型
            String propertyName1 = ((THomeworkAddEntity) obj1).getBaseTypeId();
            String propertyName2 = ((THomeworkAddEntity) obj2).getBaseTypeId();

            // 这里根据 propertyName 进行比较，这里使用字符串比较，你可以根据属性类型自定义比较
            return propertyName1.compareTo(propertyName2);
        }
    };

    private void sortPickList() {
        // 使用自定义比较器进行排序
        Collections.sort(pickList, customComparator);
    }
}