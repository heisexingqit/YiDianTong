package com.example.yidiantong.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.bean.LearnPlanAddItemEntity;
import com.example.yidiantong.bean.StuAnswerEntity;
import com.example.yidiantong.util.ImageUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class TLearnPlanAddPPTFragment extends Fragment {

    private static final String TAG = "TLearnPlanAddPPTItemFragment";

    // 接口需要
    private LearnPlanAddItemEntity learnPlanEntity;
    private LinearLayout ll_bottom_tab;
    private HorizontalScrollView sv_bottom_tab;
    private PhotoView pv_content;

    private int nowPos = 0;
    private ClickableImageView lastImageView;
    private List<String> picUrlList;

    public static TLearnPlanAddPPTFragment newInstance(LearnPlanAddItemEntity learnPlanEntity) {
        TLearnPlanAddPPTFragment fragment = new TLearnPlanAddPPTFragment();
        Bundle args = new Bundle();
        args.putSerializable("learnPlanEntity", learnPlanEntity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 取出携带的参数
        if (getArguments() != null) {
            learnPlanEntity = (LearnPlanAddItemEntity)getArguments().getSerializable("learnPlanEntity");
            picUrlList = learnPlanEntity.getPptList();
        }

        // 获取View
        View view = inflater.inflate(R.layout.fragment_t_learn_plan_add_ppt, container, false);

        // 题目类型
        TextView tv_question_type = view.findViewById(R.id.tv_question_type);
        tv_question_type.setText(learnPlanEntity.getName());
        tv_question_type.setTextSize(18);
        tv_question_type.setTextColor(Color.BLACK);

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

    private void showBottomBar() {

        ll_bottom_tab.removeAllViews();
        for (int i = 0; i < picUrlList.size(); ++i) {
            ClickableImageView imageView = new ClickableImageView(getActivity());
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

//            ImageRequest imageRequest = new ImageRequest(picUrlList.get(i), new Response.Listener<Bitmap>() {
//                @Override
//                public void onResponse(Bitmap response) {
//                    // 在这里处理成功响应的逻辑，response参数即为获取到的图片
//                    Log.d("wen", "onResponse: 1");
//                    response = ImageUtils.compressBitmap(response, (float) 0.2);
//                    imageView.setImageBitmap(response);
//                }
//            }, 0, 0, null, null, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    // 在这里处理错误响应的逻辑
//                }
//            });
//            MyApplication.addRequest(imageRequest, TAG);
            Glide.with(this).load(picUrlList.get(i)).into(imageView);

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
}