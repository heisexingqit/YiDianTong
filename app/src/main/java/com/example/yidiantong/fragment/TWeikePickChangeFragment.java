package com.example.yidiantong.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.NoScrollViewPager;
import com.example.yidiantong.adapter.TLearnPlanAddPickPagerAdapter;
import com.example.yidiantong.bean.LearnPlanAddItemEntity;
import com.example.yidiantong.util.PxUtils;

import java.util.Collections;
import java.util.List;

public class TWeikePickChangeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TWeikePickChangeFragment";

    // 引用AddFragment中的
    public List<LearnPlanAddItemEntity> pickList;

    // 当前位置
    private int nowPos = 0;

    // 上一个被选中的Tab块
    private ClickableImageView lastImageView;

    // 主体ViewPager
    private NoScrollViewPager vp_main;
    // 主体adapter
    private TLearnPlanAddPickPagerAdapter adapter;

    // 顶部按钮
    private TextView tv_count;

    // 底部Tag块
    private LinearLayout ll_bottom_tab;
    private HorizontalScrollView sv_bottom_tab;
    private ClickableImageView iv_right;
    private ClickableImageView iv_left;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_t_weike_pick_change, container, false);

        // 大小调节
        iv_left = view.findViewById(R.id.iv_left);
        iv_right = view.findViewById(R.id.iv_left);


        ll_bottom_tab = view.findViewById(R.id.ll_bottom_tab);
        sv_bottom_tab = view.findViewById(R.id.sv_bottom_tab);

        vp_main = view.findViewById(R.id.vp_main);
        if (adapter == null) {
            adapter = new TLearnPlanAddPickPagerAdapter(getActivity().getSupportFragmentManager(), pickList);
        }
        vp_main.setAdapter(adapter);
        adapter.update(pickList);

        // 顶部交换按钮
        view.findViewById(R.id.iv_left_ex).setOnClickListener(this);
        view.findViewById(R.id.iv_right_ex).setOnClickListener(this);
        tv_count = view.findViewById(R.id.tv_count);

        view.findViewById(R.id.iv_minus).setOnClickListener(this);
        tv_count.setText((nowPos + 1) + "/" + pickList.size());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.notifyDataSetChanged();
        // 初始化获取组件宽度
        ViewTreeObserver vto = sv_bottom_tab.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onGlobalLayout() {
                // 延迟刷新页面
                showQuestionBlock(adapter.itemList);
                adapter.notifyDataSetChanged();

                sv_bottom_tab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_ex:
                if (nowPos != 0) {
                    Collections.swap(pickList, nowPos - 1, nowPos);
                    adapter.notifyDataSetChanged();
                    nowPos -= 1;
                    vp_main.setCurrentItem(nowPos, false);
                    tv_count.setText((nowPos + 1) + "/" + pickList.size());
                    showQuestionBlock(pickList);
                }
                break;
            case R.id.iv_right_ex:
                if (nowPos != pickList.size() - 1) {
                    Collections.swap(pickList, nowPos + 1, nowPos);
                    adapter.notifyDataSetChanged();
                    nowPos += 1;
                    vp_main.setCurrentItem(nowPos, false);
                    tv_count.setText((nowPos + 1) + "/" + pickList.size());
                    showQuestionBlock(pickList);
                }
                break;
            case R.id.iv_minus:
                pickList.remove(nowPos);
                nowPos = Math.min(pickList.size() - 1, nowPos);
                adapter.notifyDataSetChanged();
                showQuestionBlock(pickList);

                vp_main.setCurrentItem(nowPos, false);
                tv_count.setText((nowPos + 1) + "/" + pickList.size());
                break;
        }
    }


    public void updateItem(List<LearnPlanAddItemEntity> list) {
        pickList = list;
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
                    Log.d("wen", "教师端-微课-添加-未知类型！！！");
            }
            if (resId == 0) {
                Log.d(TAG, "showQuestionBlock: " + resId);
            }

            int dp_1 = PxUtils.dip2px(getActivity(), 1);
            int pxMargin = 3 * dp_1;
            iv_left.setVisibility(View.INVISIBLE);
            iv_right.setVisibility(View.INVISIBLE);
            int myWidth = sv_bottom_tab.getWidth() / 5 - 2 * pxMargin - 2 * dp_1;
            iv_left.setVisibility(View.GONE);
            iv_right.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(myWidth, myWidth);
            params.setMargins(pxMargin, pxMargin, pxMargin, pxMargin);
            imageView.setPadding(pxMargin, pxMargin, pxMargin, pxMargin);
            imageView.setLayoutParams(params);
            imageView.setImageResource(resId);
            if (i == nowPos) {
                imageView.setBackgroundResource(R.drawable.t_homework_add_border2);
                lastImageView = imageView;
            }

            imageView.setTag(i);
            imageView.setOnClickListener(v -> {
                if (lastImageView != v) {
                    lastImageView.setBackgroundResource(0);
                    v.setBackgroundResource(R.drawable.t_homework_add_border2);
                    nowPos = (int) v.getTag();
                    vp_main.setCurrentItem(nowPos, false);
                    tv_count.setText((nowPos + 1) + "/" + pickList.size());
                }
                lastImageView = (ClickableImageView) v;
            });
            ll_bottom_tab.addView(imageView);
        }
    }
}