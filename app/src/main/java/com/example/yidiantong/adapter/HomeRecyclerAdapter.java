package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.ui.HomeworkPagerActivity;
import com.example.yidiantong.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    private final Context context;
    //打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public List<HomeItemEntity> itemList;

    public int isRefresh = 0;

    //是否到底
    public int isDown = 0;
    private FootViewHolder fvh;

    public HomeRecyclerAdapter(Context context, List<HomeItemEntity> itemList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View v = layoutInflater.inflate(R.layout.item_home_list, parent, false);
            return new ItemViewHolder(v);
        }else{
            View v = layoutInflater.inflate(R.layout.foot_load_tips, parent, false);
            fvh = new FootViewHolder(v);
            return fvh;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        if(getItemViewType(position) == 0){
            ((ItemViewHolder)holder).update(position);

            //绑定事件

            //item点击事件
            holder.itemView.setOnClickListener(v-> mItemClickListener.onItemClick(holder.itemView, position));
        }else{
            ((FootViewHolder) holder).update();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < itemList.size()) {
            return 0;
        } else {
            return 1;
        }
    }

    public void fail() {
        fvh.fail();
    }


    @Override
    public int getItemCount() {
        return itemList.size() + 1;
    }


    public void loadData(List<HomeItemEntity> moreList) {
        if (this.isRefresh == 1) {
            this.itemList.clear();
            this.itemList = moreList;
            this.isRefresh = 0;
        } else {
            this.itemList.addAll(moreList);
        }
        this.notifyDataSetChanged();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder{
        //组件变量
        private final ImageView iv_icon;
        private final TextView tv_type;
        private final TextView tv_bottom;
        private final TextView tv_is_live;
        private final TextView tv_title;
        private final ImageView iv_top_icon;
        private final TextView tv_second_line;
        private final TextView tv_date;
        private final LinearLayout ll_width;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_is_live = itemView.findViewById(R.id.tv_is_live);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_top_icon = itemView.findViewById(R.id.iv_top_icon);
            tv_second_line = itemView.findViewById(R.id.tv_second_line);
            tv_date = itemView.findViewById(R.id.tv_date);
            ll_width = itemView.findViewById(R.id.ll_width);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            HomeItemEntity item = itemList.get(pos);
            //绑定组件

            //设置图标和类型
            int icon_id;
            switch (item.getType()){
                case "导学案" :
                    icon_id = R.drawable.guide_plan_icon;
                    break;
                case "作业" :
                    icon_id = R.drawable.homework_icon;
                    break;
                case "通知" :
                    icon_id = R.drawable.notice_icon;
                    break;
                case "公告" :
                    icon_id = R.drawable.announcement_icon;
                    break;
                case "微课":
                    icon_id = R.drawable.live_icon;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getType());
            }
            iv_icon.setImageResource(icon_id);
            tv_type.setText(item.getType());
            tv_title.setText(item.getBottomTitle());
            //清除底栏
            if (item.getContent().equals("")){
                tv_bottom.setVisibility(View.GONE);
            }else{
                tv_bottom.setVisibility(View.VISIBLE);
                tv_bottom.setText(item.getContent());
            }
            //设置时间
            tv_date.setText(item.getTime());
            //设置第二行
            String second_line = item.getCourseName()+item.getTimeStop();
            if(second_line.length() > 27){
                second_line = second_line.substring(0,27) + "...";
            }
            tv_second_line.setText(second_line);

            //默认隐藏直播标志
            tv_is_live.setVisibility(View.INVISIBLE);
            //学习内容状态
            switch (item.getStatus()){
                case 1:
                case 5:
                    //未读
                    //新的
                    iv_top_icon.setVisibility(View.VISIBLE);
                    iv_top_icon.setImageResource(R.drawable.new_icon);
                    break;
                case 2:
                    //已批改
                    iv_top_icon.setVisibility(View.VISIBLE);
                    iv_top_icon.setImageResource(R.drawable.red_pencil);
                    break;
                case 3:
                    //未批改
                    iv_top_icon.setVisibility(View.VISIBLE);
                    iv_top_icon.setImageResource(R.drawable.green_pencil);
                    break;
                case 4:
                    //已读
                    iv_top_icon.setVisibility(View.INVISIBLE);
                    break;
            }

            //ViewGroup.LayoutParams params_pen = iv_top_icon.getLayoutParams();

            //0：直播课，1：作业，未批改，2：作业，已批改，3：导学案，未看，4：导学案，已看
            /*switch (type[pos]){
                case 0:
                    //改图标
                    iv_icon.setImageResource(R.drawable.live_icon);
                    //设置类型名
                    tv_type.setText(context.getResources().getString(R.string.live_news));
                    //调宽类型名
                    ll_width.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    //消除底行
                    tv_bottom.setVisibility(View.GONE);
                    //保留直播中标记
                    //消除题目
                    //tv_title.setVisibility(View.INVISIBLE);
                    //消除上标图片
                    iv_top_icon.setVisibility(View.INVISIBLE);
                    //设置时间
                    tv_date.setText("2023.01");
                    //第二行
                    tv_second_line.setText("[19条]武松打虎第二讲2023-01-28 20:27-09:07");
                    break;
                case 1:
                    //改图标
                    iv_icon.setImageResource(R.drawable.homework_icon);
                    //设置类型名
                    tv_type.setText(context.getResources().getString(R.string.homework));
                    //隐藏直播中标记
                    tv_is_live.setVisibility(View.INVISIBLE);

                    //默认new
                    params_pen.width = Utils.dip2px(context, 25);
                    //设置时间
                    tv_date.setText("2月17日");
                    //第二行
                    tv_second_line.setText("初中数学  截止:2024.01");
                    tv_bottom.setText("初中英语（北师大版）/八年级上册/Free Time/I like playing the guitar");
                    break;

                case 2:
                    //改图标
                    iv_icon.setImageResource(R.drawable.homework_icon);
                    //设置类型名
                    tv_type.setText(context.getResources().getString(R.string.homework));
                    //消除底行
                    tv_bottom.setVisibility(View.GONE);
                    //隐藏直播中标记
                    tv_is_live.setVisibility(View.INVISIBLE);
                    //改上标图片
                    params_pen.width = Utils.dip2px(context, 17);
                    iv_top_icon.setImageResource(R.drawable.red_pencil);

                    //设置时间
                    tv_date.setText("2023.01");
                    tv_second_line.setText("初中数学 截止:2月18日");
                    tv_bottom.setText("得分:39.0分 平均分24.5分");
                    break;
                case 3:
                    //改图标
                    iv_icon.setImageResource(R.drawable.guide_plan_icon);
                    //设置类型名
                    tv_type.setText(context.getResources().getString(R.string.guide_plan));
                    //隐藏直播中标记
                    tv_is_live.setVisibility(View.INVISIBLE);
                    //改上标图片
                    params_pen.width = Utils.dip2px(context, 17);
                    iv_top_icon.setImageResource(R.drawable.green_pencil);
                    //设置时间
                    tv_date.setText("2023.01");
                    tv_second_line.setText("初中语文  截止:2023.01");
                    tv_bottom.setText("小学数学（人教版）/一年级上册/位置/上、下、前、后/");
                    break;
                case 4:
                    //改图标
                    iv_icon.setImageResource(R.drawable.guide_plan_icon);
                    //设置类型名
                    tv_type.setText(context.getResources().getString(R.string.guide_plan));
                    //消除底行
                    tv_bottom.setVisibility(View.GONE);
                    //隐藏直播中标记
                    tv_is_live.setVisibility(View.INVISIBLE);
                    //改上标图片
                    params_pen.width = Utils.dip2px(context, 17);
                    iv_top_icon.setImageResource(R.drawable.red_pencil);
                    //设置时间
                    tv_date.setText("2023.01");
                    tv_bottom.setText("得分:3.0分 平均分2.0分");
                    break;
            }*/

        }
    }

    /*加载进度条视图holder，含绑定方法*/
    class FootViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar pbViewLoadTip;
        private TextView tvViewLoadTip;

        public FootViewHolder(View itemView) {
            super(itemView);
            pbViewLoadTip = itemView.findViewById(R.id.pb_view_load_tip);
            tvViewLoadTip = itemView.findViewById(R.id.tv_view_load_tip);
        }

        public void update() {
            if (isDown == 0) {
                pbViewLoadTip.setVisibility(View.VISIBLE);
                tvViewLoadTip.setText("正在加载更多数据...");
            } else if (isDown == 1) {
                pbViewLoadTip.setVisibility(View.GONE);
                tvViewLoadTip.setText("没有更多数据了");
            } else {
                pbViewLoadTip.setVisibility(View.GONE);
                tvViewLoadTip.setText("");
            }
        }

        public void fail() {
            pbViewLoadTip.setVisibility(View.GONE);
            tvViewLoadTip.setText("数据加载失败");
        }
    }

    public interface MyItemClickListener{
        void onItemClick(View view, int pos);
    }
}
