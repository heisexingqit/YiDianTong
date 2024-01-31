package com.example.yidiantong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.TLearnPlanAddPickPagerAdapter;
import com.example.yidiantong.bean.LearnPlanAddItemEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.PxUtils;
import com.example.yidiantong.util.StringUtils;
import com.example.yidiantong.util.TLearnPlanAddInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TPackagePickAddFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TPackagePickAddFragment";

    // URL
    private String mRequestUrl = "";

    // 记录选题内容
    public List<LearnPlanAddItemEntity> pickList = new ArrayList<>();

    // 当前位置
    private int nowPos = 0;

    // 上一个被选中的Tab块
    private ClickableImageView lastImageView;

    // 当前页码
    private int currentpage = 1;

    // 推荐页码
    private int searchPage = 1;

    // 最后一页
    private boolean isAll = false;

    // LearnPlanId
    private String learnPlanId = "";

    // deviceType
    private String deviceType = "PHONE";


    // 主体ViewPager
    private NoScrollViewPager vp_main;
    // 主体adapter
    private TLearnPlanAddPickPagerAdapter adapter;

    // 顶部按钮
    private TextView tv_count;
    private ClickableImageView iv_add;

    // 底部Tag块
    private LinearLayout ll_bottom_tab;
    private HorizontalScrollView sv_bottom_tab;

    private TextView tv_hide;

    // 请求资源库参数
    private String xueduan = "";
    private String xueke = "";
    private String banben = "";
    private String jiaocai = "";
    private String zhishidian = "";
    private String type = "";
    private String shareTag = "";
    private String xueduanCode = "";
    private String xuekeCode = "";
    private String banbenCode = "";
    private String jiaocaiCode = "";
    private String zhishidianCode = "";
    private String typeValue = "";

    private TLearnPlanAddInterface transmit;

    private RelativeLayout rl_loading;

    public TPackagePickAddFragment(String xd, String xdCode, String xk, String xkCode, String bb, String bbCode, String jc, String jcCode, String zsd, String zsdCode, String type, String typeValue, String shareTag) {
        xueduan = xd;
        xueke = xk;
        banben = bb;
        jiaocai = jc;
        zhishidian = zsd;
        xueduanCode = xdCode;
        xuekeCode = xkCode;
        banbenCode = bbCode;
        jiaocaiCode = jcCode;
        zhishidianCode = zsdCode;
        this.typeValue = typeValue;
        this.type = type;
        this.shareTag = shareTag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        transmit = (TLearnPlanAddInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_package_pick_add, container, false);
        ll_bottom_tab = view.findViewById(R.id.ll_bottom_tab);
        sv_bottom_tab = view.findViewById(R.id.sv_bottom_tab);
        tv_hide = view.findViewById(R.id.tv_hide);

        vp_main = view.findViewById(R.id.vp_main);

        rl_loading = view.findViewById(R.id.rl_loading);

        if (adapter == null) {
            adapter = new TLearnPlanAddPickPagerAdapter(getActivity().getSupportFragmentManager(), new ArrayList<>());
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

        if (adapter.itemList.size() == 0) {
            loadItems_Net();
        }else{
            tv_hide.setVisibility(View.GONE);
            rl_loading.setVisibility(View.GONE);
        }

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
    public void updateItem(String xd, String xdCode, String xk, String xkCode, String bb, String bbCode, String jc, String jcCode, String zsd, String zsdCode, String type, String typeValue, String shareTag) {
        xueduan = xd;
        xueke = xk;
        banben = bb;
        jiaocai = jc;
        zhishidian = zsd;
        xueduanCode = xdCode;
        xuekeCode = xkCode;
        banbenCode = bbCode;
        jiaocaiCode = jcCode;
        zhishidianCode = zsdCode;
        this.typeValue = typeValue;
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
                String qId = adapter.itemList.get(nowPos).getId();
                if (pickList.stream().anyMatch(obj -> obj.getId().equals(qId))) {
                    pickList.removeIf(obj -> obj.getId().equals(qId));
                    iv_add.setImageResource(R.drawable.add_homework);
                } else {
                    pickList.add(adapter.itemList.get(nowPos));
                    iv_add.setImageResource(R.drawable.minus_homework);
                }
                tv_count.setText("(已选择" + pickList.size() + ")");
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showQuestionBlock(List<LearnPlanAddItemEntity> moreList) {

        ll_bottom_tab.removeAllViews();
        for (int i = 0; i < moreList.size(); ++i) {
            LearnPlanAddItemEntity item = moreList.get(i);
            ClickableImageView imageView = new ClickableImageView(getActivity());
            int resId = 0;
            switch (item.getFormat()) {
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
                case "ppt":
                    resId = R.drawable.t_learn_plan_ppt;
                    break;
                case "paper":
                    resId = R.drawable.t_learn_plan_pdf;
                    break;
                case "word":
                    resId = R.drawable.t_learn_plan_word;
                    break;
                case "video":
                    resId = R.drawable.t_learn_plan_video;
                    break;
                case "music":
                    resId = R.drawable.t_learn_plan_music;
                    break;
                case "other":
                    resId = R.drawable.t_learn_plan_other;
                    break;
                default:
                    Log.d("wen", "教师端-授课包-添加-未知类型！！！");

            }
            if (resId == 0) {
                Log.d(TAG, "showQuestionBlock: " + resId);
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
                String qId = item.getId();
                if (pickList.stream().anyMatch(obj -> obj.getId().equals(qId))) {
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
                    String qId = adapter.itemList.get(nowPos).getId();
                    if (pickList.stream().anyMatch(obj -> obj.getId().equals(qId))) {
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
                List<LearnPlanAddItemEntity> moreList = (List<LearnPlanAddItemEntity>) message.obj;
                nowPos = 0;
                vp_main.setCurrentItem(nowPos, false);
                showQuestionBlock(moreList);
                rl_loading.setVisibility(View.GONE);
            }
        }
    };

    private void loadItems_Net() {

        if (StringUtils.hasEmptyString(xueduan, xueke, zhishidian, banben, jiaocai, type, shareTag)) {
            return;
        }
        String xd = "";
        String xk = "";
        String zsd = "";
        String bb = "";
        String jc = "";
        String tvl = "";
        try {
            xd = URLEncoder.encode(xueduan, "UTF-8");
            xk = URLEncoder.encode(xueke, "UTF-8");
            bb = URLEncoder.encode(banben, "UTF-8");
            zsd = URLEncoder.encode(zhishidian, "UTF-8");
            jc = URLEncoder.encode(jiaocai, "UTF-8");
            tvl = URLEncoder.encode(typeValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "loadItems_Net: " + typeValue);

        mRequestUrl = Constant.API + Constant.T_LEARN_PLAN_GET_ALL_RESOURCE + "?userName=" + MyApplication.username + "&type=" + type + "&typeValue=" + tvl
                + "&shareTag=" + shareTag + "&channelCode=" + xueduanCode + "&subjectCode=" + xuekeCode + "&textBookCode=" + banbenCode + "&gradeLevelCode=" + jiaocaiCode
                + "&pointCode=" + zhishidianCode + "&learnPlanId=" + learnPlanId + "&currentPage=" + currentpage + "&searchPage=" + searchPage + "&deviceType=" + deviceType
                + "&channelName=" + xd + "&subjectName=" + xk + "&textBookName=" + bb + "&gradeLevelName=" + jc
                + "&pointName=" + zsd;
        Log.e("0103", "导学案题面获取Url:" + mRequestUrl);
        StringRequest request = new StringRequest(mRequestUrl, response -> {
            try {
                JSONObject json = JsonUtils.getJsonObjectFromString(response);
                String itemString = json.getJSONObject("data").getString("esmodelList");

                Gson gson = new Gson();
                List<LearnPlanAddItemEntity> moreList = gson.fromJson(itemString, new TypeToken<List<LearnPlanAddItemEntity>>() {
                }.getType());
                learnPlanId = json.getJSONObject("data").getString("learnPlanId");
                transmit.setLearnPlanId(learnPlanId);

                // 到头了
                if(moreList.size() < 5){
                    isAll = true;
                }

                if (moreList.size() == 0) {
                    if (currentpage == 1) {
                        tv_hide.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), "已经是最后一页", Toast.LENGTH_SHORT).show();
                    }
                    tv_hide.setVisibility(View.VISIBLE);
                } else{
                    tv_hide.setVisibility(View.GONE);
                }

                Log.d(TAG, "loadItems_Net: " + moreList);
                adapter.update(moreList);

                Message message = Message.obtain();
                message.obj = moreList;
                message.what = 100;
                handler.sendMessage(message);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(getActivity(), "网络连接失败", Toast.LENGTH_SHORT).show();
            Log.d("wen", "Volley_Error: " + error.toString());
        });
        MyApplication.addRequest(request, TAG);
        rl_loading.setVisibility(View.VISIBLE);
    }
}
