package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookExerciseEntity;
import com.example.yidiantong.bean.HistoryEntity;

import java.util.List;

// 展示自主学习的错题列表
public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HistoryAdapter";
    public List<HistoryEntity> itemList;  // itemList中保存单元列表,每个单元中有错题列表
    private final Context context;
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public int isRefresh = 0;

    public HistoryAdapter(Context context, List<HistoryEntity> itemList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    // 判断是错题内容还是小标题
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_history_detail_list, null, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 更新quesposi位置错题内容
        ((ItemViewHolder) holder).update(position);
        // item点击事件
        ((ItemViewHolder) holder).fll_detail_list.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // 更新标题+题目 UI渲染数据
    public void loadData(List<HistoryEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }


    //ItemHolder类,更新item中内容
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final TextView tv_title;  //错题编号
        private final TextView tv_date; //得分
        private final TextView tv_xinxi; //得分
        private final LinearLayout fll_detail_list;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            fll_detail_list = itemView.findViewById(R.id.fll_detail_list);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_xinxi = itemView.findViewById(R.id.tv_xinxi);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            HistoryEntity item = itemList.get(pos);
            tv_title.setText(item.getName());
            tv_date.setText(item.getCreateDateStr());
            tv_xinxi.setText("考点：" + item.getPointNum() + "  试题总数：" + item.getQNum() + "  已答试题：" + item.getAnswerQueNum());
        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int pos);
    }

}
