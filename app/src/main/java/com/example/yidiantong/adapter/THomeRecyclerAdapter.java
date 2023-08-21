package com.example.yidiantong.adapter;

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
import com.example.yidiantong.bean.HomeItemEntity;
import com.example.yidiantong.bean.THomeItemEntity;

import java.util.List;

public class THomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    //打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public List<THomeItemEntity> itemList;

    public int isRefresh = 0;

    //是否到底
    public int isDown = 0;

    // 加载失败
    public boolean fail = false;

    // 假0判断
    private int count = 0;

    public THomeRecyclerAdapter(Context context, List<THomeItemEntity> itemList) {
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
            View v = layoutInflater.inflate(R.layout.item_t_home_list, parent, false);
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
            //绑定事件
            //item点击事件
            holder.itemView.setOnClickListener(v -> {
                        mItemClickListener.onItemClick(holder.itemView, position);
                        ((ItemViewHolder) holder).tv_unread.setVisibility(View.INVISIBLE);
                    }
            );
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


    public void loadData(List<THomeItemEntity> moreList) {
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
            if (moreList.size() >= 12) {
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
        private final TextView tv_second_line;
        private final TextView tv_date;

        private final TextView tv_num4;
        private final TextView tv_num5;

        private final LinearLayout ll_num45;
        private final TextView tv_is_live;
        public final TextView tv_unread;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_second_line = itemView.findViewById(R.id.tv_second_line);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_num4 = itemView.findViewById(R.id.tv_num4);
            tv_num5 = itemView.findViewById(R.id.tv_num5);
            ll_num45 = itemView.findViewById(R.id.ll_num45);
            tv_is_live = itemView.findViewById(R.id.tv_is_live);
            tv_unread = itemView.findViewById(R.id.tv_unread);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            THomeItemEntity item = itemList.get(pos);
            //绑定组件

            //设置图标和类型
            int icon_id;
            switch (item.getfType()) {
                case "1":
                    icon_id = R.drawable.t_learnplan_icon;
                    ll_num45.setVisibility(View.VISIBLE);
                    tv_num4.setText(item.getfNum4());
                    tv_num5.setText(item.getfNum5());
                    tv_is_live.setVisibility(View.GONE);
                    break;
                case "2":
                    icon_id = R.drawable.t_homework_icon;
                    ll_num45.setVisibility(View.GONE);
                    tv_is_live.setVisibility(View.GONE);
                    break;
                case "3":
                    icon_id = R.drawable.t_notice_icon;
                    ll_num45.setVisibility(View.GONE);
                    tv_is_live.setVisibility(View.GONE);
                    break;
                case "4":
                    icon_id = R.drawable.t_article_icon;
                    ll_num45.setVisibility(View.GONE);
                    tv_is_live.setVisibility(View.GONE);
                    break;
                case "7":
                    icon_id = R.drawable.t_weike_icon;
                    ll_num45.setVisibility(View.VISIBLE);
                    tv_num4.setText(item.getfNum4());
                    tv_num5.setText(item.getfNum5());
                    tv_is_live.setVisibility(View.GONE);
                    break;
                case "10":
                    icon_id = R.drawable.t_live_icon;
                    ll_num45.setVisibility(View.GONE);
                    tv_is_live.setVisibility(View.VISIBLE);
                    if (item.getfFlag().equals("3")) {
                        tv_is_live.setText("已结束");
                        tv_is_live.setBackgroundResource(R.color.live_btn_gray);
                    } else if (item.getfFlag().equals("1")) {
                        tv_is_live.setText("未开始");
                        tv_is_live.setBackgroundResource(R.color.live_btn_purple);
                    } else {
                        tv_is_live.setText("直播中");
                        tv_is_live.setBackgroundResource(R.color.live_red);
                    }

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + item.getfType());

            }

            // 未阅小球
            if (item.getfNumber() != null) {
                if (!item.getfNumber().equals("0") && !item.getfType().equals("10")) {
                    tv_unread.setVisibility(View.VISIBLE);
                    tv_unread.setText(item.getfNumber());
                } else {
                    tv_unread.setVisibility(View.INVISIBLE);
                }
            } else {
                tv_unread.setVisibility(View.INVISIBLE);
            }

            iv_icon.setImageResource(icon_id);

            tv_title.setText(item.getfName());

            // 设置时间
            tv_date.setText(item.getfTime());

            if (item.getfType().equals("10")) {
                // 设置第二行
                tv_second_line.setText("[" + item.getfNumber() + "条]" + item.getfDescription());
                // 设置底层
                tv_bottom.setVisibility(View.GONE);
            } else {
                // 设置第二行
                tv_second_line.setText(item.getfDescription());

                // 设置底行
                String bottomStr = item.getfNum1() + " " + item.getfNum2() + " " + item.getfNum3();
                tv_bottom.setText(bottomStr);
            }
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
        void onItemClick(View view, int pos);
    }
}
