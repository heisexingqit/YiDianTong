package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookInfoEntity;

import java.util.List;

public class AutoStudyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AutoStudyAdapter";
    private final Context context;
    private final LayoutInflater layoutInflater;
    public List<BookInfoEntity> itemList;
    private MyItemClickListener mItemClickListener;

    public AutoStudyAdapter(Context context, List<BookInfoEntity> itemList) {
        this.context = context;
        // 获取layoutInflater实例，用于在onCreateViewHolder方法中创建布局视图
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    // 项点击监听器，将外部的监听器传递进来，在适配器中处理RecyclerView的点击事件
    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    // 通过 LayoutInflater 创建视图，并将其绑定到 ViewHolder 中
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = layoutInflater.inflate(R.layout.item_book_list, parent, false);
            return new ItemViewHolder(v);
        }else{
            return null;
        }
    }

    // 数据绑定到 ViewHolder 中的视图，并更新列表项的内容
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        if(getItemViewType(position) == 0){
            ((ItemViewHolder)holder).update(position);
            //绑定事件
            //item点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.itemView, position);
                    v.findViewById(R.id.fiv_book_redball).setVisibility(View.INVISIBLE);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // 加载数据
    public void loadData(List<BookInfoEntity> moreList) {
        this.itemList.clear();
        this.itemList.addAll(moreList);
        this.notifyDataSetChanged();
    }


    //ItemHolder类，初始化item视图更新item内容,item是选项卡
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final ImageView fiv_book_pic; //学科图标
        private final TextView ftv_book_name; // 学科名
        private final TextView ftv_book_number; // 错题数量
        private final ImageView fiv_book_redball; // 是否存在新错题
        //获取组件
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ftv_book_name = itemView.findViewById(R.id.ftv_book_name);
            ftv_book_number = itemView.findViewById(R.id.ftv_book_number);
            fiv_book_redball = itemView.findViewById(R.id.fiv_book_redball);
            fiv_book_pic = itemView.findViewById(R.id.fiv_book_pic);
        }

        public void update(int pos) {
            //数据
            BookInfoEntity item = itemList.get(pos);

            // 设置知识点数
            int randomNumber = 0;

            //设置课程图片
            int icon_name = -1;
            switch (item.getSubjectName()) {
                case "语文":
                    icon_name = R.drawable.yuwen_icon;
                    randomNumber = 50;
                    break;
                case "数学":
                    icon_name = R.drawable.shuxue_icon;
                    randomNumber = 89;
                    break;
                case "英语":
                    icon_name = R.drawable.yingyu_icon;
                    randomNumber = 76;
                    break;
                case "物理":
                    icon_name = R.drawable.wuli_icon;
                    randomNumber = 83;
                    break;
                case "化学":
                    icon_name = R.drawable.huaxue_icon;
                    randomNumber = 76;
                    break;
                case "生物":
                    icon_name = R.drawable.shengwu_icon;
                    randomNumber = 55;
                    break;
                case "政治":
                    icon_name = R.drawable.zhengzhi_icon;
                    randomNumber = 81;
                    break;
                case "历史":
                    icon_name = R.drawable.lishi_icon;
                    randomNumber = 74;
                    break;
                case "地理":
                    icon_name = R.drawable.dili_icon;
                    randomNumber = 84;
                    break;
                case "其它":
                    icon_name = R.drawable.other_icon;
                    randomNumber = 0;
                    break;
                default:
                    break;
            }
            fiv_book_pic.setImageResource(icon_name);
            ftv_book_name.setText(item.getSubjectName());

            ftv_book_number.setText(randomNumber+ "个知识点");
            //默认隐藏红点标志
            switch (item.getStatus()){
                case "1":
                    fiv_book_redball.setVisibility(View.VISIBLE);
                    break;
                case "0":
                    fiv_book_redball.setVisibility(View.INVISIBLE);

            }
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


    public interface MyItemClickListener{
        void onItemClick(View view, int pos);
    }

}
