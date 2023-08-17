package com.example.yidiantong.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.LearnPlanItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.LearnPlanInterface;
import com.example.yidiantong.util.PagingInterface;
import com.example.yidiantong.util.HomeworkInterface;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class LearnPlanPPTFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LearnPlanPPTFragment";
    private PagingInterface paging;
    private LearnPlanInterface transmit;

    // 接口需要
    private LearnPlanItemEntity learnPlanEntity;
    private StuAnswerEntity stuAnswerEntity;
    private LinearLayout ll_bottom_tab;
    private HorizontalScrollView sv_bottom_tab;
    private PhotoView pv_content;

    private int nowPos = 0;
    private ClickableImageView lastImageView;
    private List<String> picUrlList;

    // 观看时间
    private long timeStart;

    public static LearnPlanPPTFragment newInstance(LearnPlanItemEntity learnPlanEntity, StuAnswerEntity stuAnswerEntity) {
        LearnPlanPPTFragment fragment = new LearnPlanPPTFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        args.putSerializable("stuAnswerEntity", stuAnswerEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        paging = (PagingInterface) context;
        transmit = (LearnPlanInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + learnPlanEntity);
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanItemEntity)getArguments().getSerializable("learnPlanEntity");
            stuAnswerEntity = (StuAnswerEntity) getArguments().getSerializable("stuAnswerEntity");
            picUrlList = learnPlanEntity.getPptList();
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_learn_plan_ppt, container, false);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getResourceName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

        //翻页组件
        ImageView iv_pager_last = view.findViewById(R.id.iv_page_last);
        ImageView iv_pager_next = view.findViewById(R.id.iv_page_next);
        iv_pager_last.setAlpha(0.9f);
        iv_pager_next.setAlpha(0.9f);
        iv_pager_last.setOnClickListener(this);
        iv_pager_next.setOnClickListener(this);

        ll_bottom_tab = view.findViewById(R.id.ll_bottom_tab);
        sv_bottom_tab = view.findViewById(R.id.sv_bottom_tab);
        pv_content = view.findViewById(R.id.pv_content);
        // 延迟初始化获取组件宽度
        ViewTreeObserver vto = sv_bottom_tab.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onGlobalLayout() {
                ImageLoader.getInstance().displayImage(picUrlList.get(nowPos), pv_content, MyApplication.getLoaderOptions());
                sv_bottom_tab.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        showBottomBar();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_page_last:
                paging.pageLast();
                break;
            case R.id.iv_page_next:
                paging.pageNext();
                break;
        }
    }

    private void showBottomBar() {

        ll_bottom_tab.removeAllViews();
        for (int i = 0; i < picUrlList.size(); ++i) {
            Log.d("wen", "showBottomBar: " + picUrlList.get(i));
            ClickableImageView imageView = new ClickableImageView(getActivity());
            imageView.setAdjustViewBounds(true);
            ImageLoader.getInstance().displayImage(picUrlList.get(i), imageView, MyApplication.getLoaderOptions());
            imageView.setPadding(4,4,4,4);

            if (i == nowPos) {
                imageView.setBackgroundResource(R.drawable.learn_plan_ppt_border);
                lastImageView = imageView;
            }

            imageView.setTag(i);
            imageView.setOnClickListener(v -> {
                if (lastImageView != v) {
                    lastImageView.setBackgroundResource(0);
                    v.setBackgroundResource(R.drawable.learn_plan_ppt_border);
                    nowPos = (int) v.getTag();
                    ImageLoader.getInstance().displayImage(picUrlList.get(nowPos), pv_content, MyApplication.getLoaderOptions());
                }
                lastImageView = (ClickableImageView) v;
            });
            ll_bottom_tab.addView(imageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        transmit.uploadTime(System.currentTimeMillis() - timeStart);
    }
}