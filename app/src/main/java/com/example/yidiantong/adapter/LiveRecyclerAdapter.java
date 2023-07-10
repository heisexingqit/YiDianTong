package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.LiveItemEntity;

import java.util.List;

public class LiveRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    // 打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<LiveItemEntity> itemList;

    // 是否刷新
    public int isRefresh = 0;

    // 是否到底
    public int isDown = 0;

    // 加载失败
    public boolean fail = false;

    // 假0判断
    private int count = 0;

    private Context mContext;

    public LiveRecyclerAdapter(Context context, List<LiveItemEntity> itemList) {
        mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = layoutInflater.inflate(R.layout.item_live_list, parent, false);
            return new ItemViewHolder(v);
        } else {
            View v = layoutInflater.inflate(R.layout.foot_load_tips, parent, false);
            return new FootViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        if (getItemViewType(position) == 0) {
            ((ItemViewHolder) holder).update(position);
        } else {
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
        fail = true;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {

        return itemList.size() + 1;
    }

    public void loadData(List<LiveItemEntity> moreList) {
        fail = false;
        if (this.isRefresh == 1) {
            count = 0;
            this.itemList.clear();
            this.itemList = moreList;
            this.isRefresh = 0;
            if (moreList.size() == 0) {
                isDown = 1;
            }
        } else {
            this.itemList.addAll(moreList);
            if (moreList.size() >= 24) {
                isDown = 0;
                count = 0;
            } else if (moreList.size() == 0) {
                isDown = 0;
                count++;
            } else {
                isDown = 1;
                count = 0;
            }

            if (count > 3) {
                isDown = 1;
            }
        }
        this.notifyDataSetChanged();
    }

    // ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_icon;
        private final TextView tv_is_live;
        private final TextView tv_title;
        private final TextView tv_time1;
        private final TextView tv_time2;
        private final TextView tv_teacher;
        private final TextView tv_subject;
        private final ClickableTextView tv_enter;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // 获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_is_live = itemView.findViewById(R.id.tv_is_live);
            tv_title = itemView.findViewById(R.id.tv_title);

            tv_time1 = itemView.findViewById(R.id.tv_time1);
            tv_time2 = itemView.findViewById(R.id.tv_time2);
            tv_subject = itemView.findViewById(R.id.tv_subject);
            tv_teacher = itemView.findViewById(R.id.tv_teacher);
            tv_enter = itemView.findViewById(R.id.tv_enter);

        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            LiveItemEntity item = itemList.get(pos);

            //设置图标和类型
            int icon_id = 0;
            if(item.getSubject().contains("语文")){
                icon_id = R.drawable.yuwen_icon;
            }else if(item.getSubject().contains("数学")){
                icon_id = R.drawable.shuxue_icon;
            }else if(item.getSubject().contains("英语")){
                icon_id = R.drawable.yingyu_icon;
            }else if(item.getSubject().contains("物理")){
                icon_id = R.drawable.wuli_icon;
            }else if(item.getSubject().contains("化学")){
                icon_id = R.drawable.huaxue_icon;
            }else if(item.getSubject().contains("生物")){
                icon_id = R.drawable.shengwu_icon;
            }else if(item.getSubject().contains("政治")){
                icon_id = R.drawable.zhengzhi_icon;
            }else if(item.getSubject().contains("历史")){
                icon_id = R.drawable.lishi_icon;
            }else if(item.getSubject().contains("地理")){
                icon_id = R.drawable.dili_icon;
            }else {
                icon_id = R.drawable.other_icon;
            }
            iv_icon.setImageResource(icon_id);

            if(item.getStatus().equals("3")){
                tv_is_live.setText("已结束");
                tv_is_live.setBackgroundColor(mContext.getColor(R.color.live_btn_gray));
                tv_enter.setVisibility(View.GONE);
            }else{
                tv_is_live.setText("直播中");
                tv_is_live.setBackgroundResource(R.color.live_red);
                tv_enter.setVisibility(View.VISIBLE);
            }
            tv_enter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!itemList.get(pos).getStatus().equals("3")){
                        mItemClickListener.onItemClick(pos);
                    }
                }
            });

            // 设置标题
            tv_title.setText(item.getTitle());

            // 设置时间
            tv_time1.setText(item.getTime1());
            tv_time2.setText(item.getTime2());

            // 设置底行
            tv_subject.setText("学科:" + item.getSubject());
            tv_teacher.setText("教师:" + item.getTeacherName());
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
            if (fail) {
                pbViewLoadTip.setVisibility(View.GONE);
                tvViewLoadTip.setText("数据加载失败");
            }
        }
    }

    public interface MyItemClickListener {
        void onItemClick(int pos);
    }
}
