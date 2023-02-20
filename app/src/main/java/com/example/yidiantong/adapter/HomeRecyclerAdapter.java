package com.example.yidiantong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    private Context context;
    //打气筒
    private LayoutInflater layoutInflater;

    private int type = 0;

    public HomeRecyclerAdapter(Context context, int type) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.type = type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_home_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        ((ItemViewHolder)holder).update();
        return;
    }

    @Override
    public int getItemCount() {
        //顶一个数量
        return 20;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        //组件变量
        //...

        private TextView tv_live;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_live = itemView.findViewById(R.id.tv_live);
            //获取组件
            //...
        }

        public void update() {
            //绑定组件
            //...
            if(type == 1){
                tv_live.setVisibility(View.GONE);
            }
        }
    }
}
