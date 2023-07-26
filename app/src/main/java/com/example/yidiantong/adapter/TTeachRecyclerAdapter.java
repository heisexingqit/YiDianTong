package com.example.yidiantong.adapter;

import android.content.Context;
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
import com.example.yidiantong.View.ClickableTextView;
import com.example.yidiantong.bean.THomeItemEntity;
import com.example.yidiantong.bean.TTeachItemEntity;
import com.example.yidiantong.util.JsonUtils;
import com.example.yidiantong.util.StringUtils;

import java.util.List;

public class TTeachRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    //打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public List<TTeachItemEntity> itemList;

    public int isRefresh = 0;

    //是否到底
    public int isDown = 0;

    // 加载失败
    public boolean fail = false;

    // 假0判断
    private int count = 0;

    public TTeachRecyclerAdapter(Context context, List<TTeachItemEntity> itemList) {
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
            View v = layoutInflater.inflate(R.layout.item_t_teach_list, parent, false);
            return new ItemViewHolder(v);
        } else {
            View v = layoutInflater.inflate(R.layout.foot_load_tips, parent, false);
            return new FootViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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


    public void loadData(List<TTeachItemEntity> moreList) {
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
            if (moreList.size() == 12) {
                isDown = 0;
                count = 0;
            } else if (moreList.size() == 0) {
                isDown = 0;
                count++;
            } else {
                isDown = 1;
                count = 0;
            }

            if (count > 1) {
                isDown = 1;
            }
        }
        this.notifyDataSetChanged();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final ImageView iv_icon;
        private final TextView tv_title;
        private final TextView tv_bottom;
        private final TextView tv_date;

        private final TextView tv_num4;
        private final TextView tv_num5;

        private final LinearLayout ll_num45;

        private final ClickableTextView tv_item_assign;
        private final ClickableTextView tv_item_edit;
        private final ClickableTextView tv_item_delete;
        private final ClickableTextView tv_item_params;
        private final LinearLayout ll_item_assign;
        private final LinearLayout ll_item_params;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_num4 = itemView.findViewById(R.id.tv_num4);
            tv_num5 = itemView.findViewById(R.id.tv_num5);
            ll_num45 = itemView.findViewById(R.id.ll_num45);
            tv_item_assign = itemView.findViewById(R.id.tv_item_assign);
            tv_item_edit = itemView.findViewById(R.id.tv_item_edit);
            tv_item_delete = itemView.findViewById(R.id.tv_item_delete);
            tv_item_params = itemView.findViewById(R.id.tv_item_params);
            ll_item_assign = itemView.findViewById(R.id.ll_item_assign);
            ll_item_params = itemView.findViewById(R.id.ll_item_params);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            TTeachItemEntity item = itemList.get(pos);
            //绑定组件

            //设置图标和类型
            int icon_id = 0;
            if (item.getIconUrl().contains("learnPlan")) {
                icon_id = R.drawable.t_learnplan_icon;
                ll_num45.setVisibility(View.VISIBLE);
                tv_num4.setText(JsonUtils.clearString(item.getPropertyCount()));
                tv_num5.setText(item.getResCount());

                tv_item_assign.setVisibility(View.VISIBLE);
                ll_item_assign.setVisibility(View.VISIBLE);
                tv_item_params.setVisibility(View.VISIBLE);
                ll_item_params.setVisibility(View.VISIBLE);
            } else if (item.getIconUrl().contains("weike")) {
                icon_id = R.drawable.t_weike_icon;
                ll_num45.setVisibility(View.VISIBLE);
                tv_num4.setText(JsonUtils.clearString(item.getPropertyCount()));
                tv_num5.setText(item.getResCount());

                tv_item_assign.setVisibility(View.VISIBLE);
                ll_item_assign.setVisibility(View.VISIBLE);
                tv_item_params.setVisibility(View.VISIBLE);
                ll_item_params.setVisibility(View.VISIBLE);
            } else if (item.getIconUrl().contains("learnPack")) {
                icon_id = R.drawable.t_learn_package_icon;
                ll_num45.setVisibility(View.VISIBLE);
                tv_num4.setText(JsonUtils.clearString(item.getPropertyCount()));
                tv_num5.setText(item.getResCount());

                tv_item_assign.setVisibility(View.GONE);
                ll_item_assign.setVisibility(View.GONE);
                tv_item_params.setVisibility(View.VISIBLE);
                ll_item_params.setVisibility(View.VISIBLE);
            } else if (item.getIconUrl().contains("paper")) {
                icon_id = R.drawable.t_paper_icon;
                ll_num45.setVisibility(View.INVISIBLE);

                tv_item_assign.setVisibility(View.VISIBLE);
                ll_item_assign.setVisibility(View.VISIBLE);
                tv_item_params.setVisibility(View.GONE);
                ll_item_params.setVisibility(View.GONE);
            } else {
                Log.d("wen", "教师端-教学页面-未知Item类型");
            }
            iv_icon.setImageResource(icon_id);

            tv_title.setText(item.getName());

            // 设置时间
            tv_date.setText(item.getCreateTime());

            // 设置底行
            tv_bottom.setText(item.getKnowledge());

            // 设置点击事件
            tv_item_assign.setOnClickListener(view -> mItemClickListener.onItemClickAssign(view, pos));
            tv_item_edit.setOnClickListener(view -> mItemClickListener.onItemClickEdit(view, pos));
            tv_item_delete.setOnClickListener(view -> mItemClickListener.onItemClickDelete(view, pos));
            tv_item_params.setOnClickListener(view -> mItemClickListener.onItemClickParams(view, pos));
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
        void onItemClickAssign(View view, int pos);
        void onItemClickEdit(View view, int pos);
        void onItemClickDelete(View view, int pos);
        void onItemClickParams(View view, int pos);
    }
}
