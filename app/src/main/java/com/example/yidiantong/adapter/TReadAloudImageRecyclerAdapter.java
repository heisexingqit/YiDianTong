package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class TReadAloudImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上下文
    private final Context mContext;

    // 打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<String> itemList;


    public TReadAloudImageRecyclerAdapter(Context context, List<String> itemList) {
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
        View v = layoutInflater.inflate(R.layout.item_t_read_aloud_image, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        ((ItemViewHolder) holder).update(position);
        // 给 itemView 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final ImageView iv_image; // 名字
        private final ImageView iv_delete; // 分数


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            iv_image = itemView.findViewById(R.id.iv_image);
            iv_delete = itemView.findViewById(R.id.iv_delete);
        }

        // 数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            // 数据
            String imageUrl = itemList.get(pos);
            ImageLoader.getInstance().displayImage(imageUrl, iv_image, MyApplication.getLoaderOptions());

            // 绑定组件
            iv_delete.setOnClickListener(v -> mItemClickListener.deleteImage(pos));
        }
    }


    // 点击事件接口
    public interface MyItemClickListener {
        void onItemClick(View view, int pos);
        void deleteImage(int pos);
    }
}
