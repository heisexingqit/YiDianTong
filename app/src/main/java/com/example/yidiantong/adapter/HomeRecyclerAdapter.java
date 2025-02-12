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
import com.example.yidiantong.bean.HomeItemEntity;

import java.util.List;

// Adapter 则负责数据和 UI 控件之间的数据绑定和交互，以实现数据展示和用户交互功能
public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    // 打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<HomeItemEntity> itemList;

    // 是否刷新
    public int isRefresh = 0;

    // 是否到底
    public int isDown = 0;

    // 加载失败
    public boolean fail = false;


    private Context mContext;

    public HomeRecyclerAdapter(Context context, List<HomeItemEntity> itemList) {
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
            View v = layoutInflater.inflate(R.layout.item_home_list, parent, false);
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
            holder.itemView.setOnClickListener(v -> mItemClickListener.onItemClick(holder.itemView, position));
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

    public void loadData(List<HomeItemEntity> moreList) {
        fail = false;
        if (this.isRefresh == 1) {
            this.itemList.clear();
            this.itemList = moreList;
            this.isRefresh = 0;
            if (moreList.size() == 0 || moreList.size() < 12) {
                isDown = 1;
            }
        } else {
            this.itemList.addAll(moreList);
            if (moreList.size() >= 12) {
                isDown = 0;
            } else {
                isDown = 1;
            }
        }
        this.notifyDataSetChanged();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final ImageView iv_icon;
        private final TextView tv_type;
        private final TextView tv_bottom;
        private final TextView tv_is_live;
        private final TextView tv_title;
        private final ImageView iv_top_icon1;
        private final ImageView iv_top_icon2;
        private final TextView tv_second_line;
        private final TextView tv_date;
        private final TextView tv_teacher;

        private final LinearLayout ll_width;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_is_live = itemView.findViewById(R.id.tv_is_live);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_top_icon1 = itemView.findViewById(R.id.iv_top_icon1);
            iv_top_icon2 = itemView.findViewById(R.id.iv_top_icon2);
            tv_second_line = itemView.findViewById(R.id.tv_second_line);
            tv_date = itemView.findViewById(R.id.tv_date);
            ll_width = itemView.findViewById(R.id.ll_width);
            tv_teacher = itemView.findViewById(R.id.tv_teacher);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            //数据
            HomeItemEntity item = itemList.get(pos);
            //绑定组件

            //设置图标和类型
            int icon_id;
            switch (item.getType()) {
                case "导学案":
                    icon_id = R.drawable.guide_plan_icon;
                    break;
                case "作业":
                    icon_id = R.drawable.homework_icon;
                    break;
                case "通知":
                    icon_id = R.drawable.notice_icon;
                    break;
                case "公告":
                    icon_id = R.drawable.announcement_icon;
                    break;
                case "微课":
                    icon_id = R.drawable.weike_icon;
                    break;
                case "直播课消息":
                    icon_id = R.drawable.live_icon;
                    break;
                case "课堂回放":
                    icon_id = R.drawable.interactive_icon;
                    break;
                case "朗读":
                    icon_id = R.drawable.read_icon;
                    break;
                case "背诵":
                    icon_id = R.drawable.recite_icon;
                    break;
                default:
                    Log.d("wen", "update: " + item.getType());
                    throw new IllegalStateException("Unexpected value: " + item.getType());
            }
            iv_icon.setImageResource(icon_id);
            if (item.getType().equals("直播课消息")) {
                ll_width.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                if (item.getCreaterName().equals("已结束")) {
                    tv_is_live.setText("已结束");
                    tv_is_live.setBackgroundResource(R.color.live_btn_gray);
                } else if (item.getCreaterName().equals("未开始")) {
                    tv_is_live.setText("未开始");
                    tv_is_live.setBackgroundResource(R.color.live_btn_purple);
                }
            }
            tv_type.setText(item.getType());
            tv_title.setText(item.getBottomTitle());

            if (!item.getType().equals("课堂回放")) {
                tv_title.setVisibility(View.VISIBLE);
            } else {
                tv_title.setVisibility(View.INVISIBLE);
            }


            //清除底栏
            if (item.getContent().equals("")) {
                tv_bottom.setVisibility(View.GONE);
            } else {
                tv_bottom.setVisibility(View.VISIBLE);
                tv_bottom.setText(item.getContent());
            }
            //设置时间
            tv_date.setText(item.getTime());

            //设置第二行
            String second_line = item.getCourseName() + item.getTimeStop();
            if (item.getType().equals("课堂回放")) {
                tv_second_line.setText(item.getCourseName());
            } else {
                tv_second_line.setText(second_line);
            }
            //学习内容状态
            if (item.getStatus().length() == 0) {
                if (!item.getType().equals("课堂回放") && !item.getType().equals("朗读") && !item.getType().equals("背诵")) {
                    iv_top_icon1.setVisibility(View.GONE);
                    iv_top_icon2.setVisibility(View.GONE);
                    // 直播类型
                    tv_is_live.setVisibility(View.VISIBLE);
                    tv_teacher.setVisibility(View.GONE);
                } else {
                    iv_top_icon1.setVisibility(View.INVISIBLE);
                    iv_top_icon2.setVisibility(View.GONE);
                    tv_is_live.setVisibility(View.GONE);
                }
                if (item.getType().equals("朗读") || item.getType().equals("背诵")) {
                    tv_teacher.setVisibility(View.VISIBLE);
                    tv_teacher.setText(item.getCreaterName());
                }

            } else {
                // 非直播类型
                tv_is_live.setVisibility(View.GONE);
                tv_teacher.setVisibility(View.VISIBLE);
                tv_teacher.setText(item.getCreaterName());
                switch (Integer.parseInt(item.getStatus())) {
                    case 1:
                    case 5:
                        //未读
                        //新的
                        iv_top_icon2.setVisibility(View.GONE);
                        iv_top_icon1.setVisibility(View.VISIBLE);
                        iv_top_icon1.setImageResource(R.drawable.new_icon);
                        break;
                    case 2:
                        //未批改
                        iv_top_icon1.setVisibility(View.GONE);
                        iv_top_icon2.setVisibility(View.VISIBLE);
                        iv_top_icon2.setImageResource(R.drawable.red_pencil);
                        break;
                    case 3:
                        //已批改
                        iv_top_icon1.setVisibility(View.GONE);
                        iv_top_icon2.setVisibility(View.VISIBLE);
                        iv_top_icon2.setImageResource(R.drawable.green_pencil);
                        break;
                    case 4:
                        //已读
                        iv_top_icon1.setVisibility(View.GONE);
                        iv_top_icon2.setVisibility(View.GONE);
                        break;
                }
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
