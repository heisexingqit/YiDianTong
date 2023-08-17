package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.THomeworkStudentItemEntity;
import com.example.yidiantong.util.PxUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class THomeworkStuRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上下文
    private final Context mContext;

    // 打气筒
    private final LayoutInflater layoutInflater;

    private THomeworkStuRecyclerAdapter.MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<THomeworkStudentItemEntity> itemList;

    // 加载失败
    public boolean fail = false;

    // 没有数据
    public boolean isDown = false;

    public boolean isChange;

    public THomeworkStuRecyclerAdapter(Context context, List<THomeworkStudentItemEntity> itemList) {
        mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;

    }

    public void setmItemClickListener(THomeworkStuRecyclerAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View v = layoutInflater.inflate(R.layout.item_t_homework_stu, parent, false);
            return new THomeworkStuRecyclerAdapter.ItemViewHolder(v);
        } else {
            View v = layoutInflater.inflate(R.layout.foot_load_tips, parent, false);
            return new THomeworkStuRecyclerAdapter.FootViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        if (getItemViewType(position) == 0) {
            ((THomeworkStuRecyclerAdapter.ItemViewHolder) holder).update(position);
            //绑定事件
            //item点击事件
            holder.itemView.setOnClickListener(v -> mItemClickListener.onItemClick(holder.itemView, position));
        } else {
            ((THomeworkStuRecyclerAdapter.FootViewHolder) holder).update();
        }
    }

    public void loadData(List<THomeworkStudentItemEntity> moreList) {
        fail = false;
        this.itemList.clear();
        this.itemList = moreList;
        if(moreList.size() == 0){
            isDown = true;
        }
        this.notifyDataSetChanged();
    }

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

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final ShapeableImageView iv_icon; // 头像
        private final TextView tv_title; // 名字
        private final TextView tv_bottom; // 第二行
        private final TextView tv_mark;
        private final ImageView iv_pen;
        private final TextView tv_late;
        private final TextView tv_date;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_bottom = itemView.findViewById(R.id.tv_bottom);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_mark = itemView.findViewById(R.id.tv_mark);
            iv_pen = itemView.findViewById(R.id.iv_pen);
            tv_late = itemView.findViewById(R.id.tv_late);
        }

        // 数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            // 数据
            THomeworkStudentItemEntity item = itemList.get(pos);
            // 绑定组件

            // 获取图片
            ImageLoader.getInstance().displayImage(item.getUserPhoto(), iv_icon, MyApplication.getLoaderOptions());

            // 设置学生姓名
            tv_title.setText(item.getUserCn());

            // 设置时间
            tv_date.setText(item.getOptionTimeStr());

            // 是否迟交
            tv_late.setText(item.getDescription());

            // 设置批改状态
            switch (item.getStatus()){
                case "2":
                    iv_pen.setImageResource(R.drawable.green_pencil);
                    tv_mark.setTextColor(mContext.getColor(R.color.green_pencil));
                    tv_mark.setText("未批改");
                    tv_bottom.setVisibility(View.GONE);
                    break;
                case "4":
                case "5":
                    iv_pen.setImageResource(R.drawable.red_pencil);
                    tv_mark.setTextColor(mContext.getColor(R.color.red_pencil));
                    tv_mark.setText("已批改");
                    // 设置第二行得分
                    tv_bottom.setVisibility(View.VISIBLE);
                    tv_bottom.setText("得分: " + item.getScore() + " / " + item.getScoreCount());
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

            if(itemList.size() == 0) {
                if(isDown){
                    pbViewLoadTip.setVisibility(View.GONE);
                    tvViewLoadTip.setText("暂时没有学生提交答案");
                    tvViewLoadTip.setTextSize(15);
                }else{
                    pbViewLoadTip.setVisibility(View.VISIBLE);
                    tvViewLoadTip.setText("正在加载更多数据...");
                }
            } else {
                pbViewLoadTip.setVisibility(View.GONE);
                tvViewLoadTip.setText("");
            }
            if(fail){
                pbViewLoadTip.setVisibility(View.GONE);
                tvViewLoadTip.setText("数据加载失败");
            }
        }
    }


    // 点击事件接口
    public interface MyItemClickListener {
        void onItemClick(View view, int pos);
    }
}
