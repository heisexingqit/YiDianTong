package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.yidiantong.View.ClickableImageView;
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.PlaybackEntity;

import java.util.List;

public class PlaybackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "HomeRecyclerAdapter";
    // 打气筒
    private final LayoutInflater layoutInflater;

    private TextView tv_name;

    private PlaybackAdapter.MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<PlaybackEntity> itemList;

    // 是否刷新
    public int isRefresh = 0;

    // 是否到底
    public int isDown = 0;

    // 加载失败
    public boolean fail = false;

    // 假0判断
    private int count = 0;

    private Context mContext;

    public PlaybackAdapter(Context context, List<PlaybackEntity> itemList) {
        mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(PlaybackAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = layoutInflater.inflate(R.layout.playback_item, parent, false);
            return new PlaybackAdapter.ItemViewHolder(v);
        } else {
            View v = layoutInflater.inflate(R.layout.foot_load_tips, parent, false);
            return new PlaybackAdapter.FootViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        if (getItemViewType(position) == 0) {
            ((PlaybackAdapter.ItemViewHolder) holder).update(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemClick(position);
                    }
                }
            });
        } else {
            ((PlaybackAdapter.FootViewHolder) holder).update();
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

    public void loadData(List<PlaybackEntity> moreList) {
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
//            if (moreList.size() >= 24) {
//                isDown = 0;
//                count = 0;
//            } else if (moreList.size() == 0) {
//                isDown = 0;
//                count++;
//            } else {
//                isDown = 1;
//                count = 0;
//            }


//            if (count > 3) {
//                isDown = 1;
//            }
        }
        isDown=1;
        this.notifyDataSetChanged();
    }

    // ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_icon;
        private final TextView tv_title;
        private final TextView tv_bottom;
        private final LinearLayout ll_live_on;
        private final ClickableTextView tv_enter;
        private final TextView tv_time_already;

        private final LinearLayout ll_live_off;
        private final TextView tv_date;
        private final TextView tv_time;

        private final ImageView iv_status;

        // 编辑
        private final ClickableImageView iv_edit;
        private final ClickableImageView iv_delete;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            // 获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            ll_live_on = itemView.findViewById(R.id.ll_live_on);
            tv_enter = itemView.findViewById(R.id.tv_enter);
            tv_time_already = itemView.findViewById(R.id.tv_time_already);
            ll_live_off = itemView.findViewById(R.id.ll_live_off);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_status = itemView.findViewById(R.id.iv_status);
            iv_edit = itemView.findViewById(R.id.iv_edit);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_name = itemView.findViewById(R.id.tv_name);

        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            PlaybackEntity item = itemList.get(pos);
            tv_name.setText(item.getTeacherName());
            //设置图标和类型
            int icon_id = 0;
            if(item.getSubjectName().contains("语文")){
                icon_id = R.drawable.yuwen_icon;
            }else if(item.getSubjectName().contains("数学")){
                icon_id = R.drawable.shuxue_icon;
            }else if(item.getSubjectName().contains("英语")){
                icon_id = R.drawable.yingyu_icon;
            }else if(item.getSubjectName().contains("物理")){
                icon_id = R.drawable.wuli_icon;
            }else if(item.getSubjectName().contains("化学")){
                icon_id = R.drawable.huaxue_icon;
            }else if(item.getSubjectName().contains("生物")){
                icon_id = R.drawable.shengwu_icon;
            }else if(item.getSubjectName().contains("政治")){
                icon_id = R.drawable.zhengzhi_icon;
            }else if(item.getSubjectName().contains("历史")){
                icon_id = R.drawable.lishi_icon;
            }else if(item.getSubjectName().contains("地理")){
                icon_id = R.drawable.dili_icon;
            }else {
                icon_id = R.drawable.other_icon;
            }
            iv_icon.setImageResource(icon_id);
            tv_time.setText(item.getShowTopTime());
            tv_date.setText(item.getShowBottomTime());

            // 设置标题
            tv_title.setText(item.getSubjectName());
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
        void editLiveItem(int pos);
        void deleteLiveItem(int pos);
    }

}
