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

public class BooksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BooksAdapter";
    private final Context context;
    private final LayoutInflater layoutInflater;
    public List<BookInfoEntity> itemList;

    private MyItemClickListener mItemClickListener;

    public BooksAdapter(Context context, List<BookInfoEntity> itemList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(BooksAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = layoutInflater.inflate(R.layout.item_book_list, parent, false);
            return new BooksAdapter.ItemViewHolder(v);
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        if(getItemViewType(position) == 0){
            ((BooksAdapter.ItemViewHolder)holder).update(position);
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

    public void loadData(List<BookInfoEntity> moreList) {
        this.itemList.clear();
        this.itemList.addAll(moreList);
        this.notifyDataSetChanged();
    }


    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final TextView ftv_book_name;
        private final TextView ftv_book_number;
        private final ImageView fiv_book_redball;
        private final ImageView fiv_book_pic;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            ftv_book_name = itemView.findViewById(R.id.ftv_book_name);
            ftv_book_number = itemView.findViewById(R.id.ftv_book_number);
            fiv_book_redball = itemView.findViewById(R.id.fiv_book_redball);
            fiv_book_pic = itemView.findViewById(R.id.fiv_book_pic);
        }

        public void update(int pos) {
            //数据
            BookInfoEntity item = itemList.get(pos);

            //设置课程图片
            int icon_name = -1;
            switch (item.getSubjectName()) {

                case "语文":
                    icon_name = R.drawable.yuwen_icon;
                    break;
                case "数学":
                    icon_name = R.drawable.shuxue_icon;
                    break;
                case "英语":
                    icon_name = R.drawable.yingyu_icon;
                    break;
                case "物理":
                    icon_name = R.drawable.wuli_icon;
                    break;
                case "化学":
                    icon_name = R.drawable.huaxue_icon;
                    break;
                case "生物":
                    icon_name = R.drawable.shengwu_icon;
                    break;
                case "政治":
                    icon_name = R.drawable.zhengzhi_icon;
                    break;
                case "历史":
                    icon_name = R.drawable.lishi_icon;
                    break;
                case "地理":
                    icon_name = R.drawable.dili_icon;
                    break;
                case "其它":
                    icon_name = R.drawable.other_icon;
                    break;
                default:
                    break;
            }
            fiv_book_pic.setImageResource(icon_name);
            ftv_book_name.setText(item.getSubjectName());
            ftv_book_number.setText(item.getErrorQueNum()+ "道错题");

            //默认隐藏红点标志
            switch (item.getStatus()){
                case "0":
                    fiv_book_redball.setVisibility(View.VISIBLE);
                    break;
                case "1":
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
