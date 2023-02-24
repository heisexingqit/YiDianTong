package com.example.yidiantong.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "HomeRecyclerAdapter";
    private Context context;
    //打气筒
    private LayoutInflater layoutInflater;

    private MyListener myListener;

    private int type = 0;

    public HomeRecyclerAdapter(Context context, int type, MyListener myListener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.type = type;
        this.myListener = myListener;
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
        ((ItemViewHolder)holder).update(position);
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
        private LinearLayout ll_main;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_live = itemView.findViewById(R.id.tv_live);
            ll_main = itemView.findViewById(R.id.ll_main);
            //获取组件
            //...
        }

        public void update(int pos) {
            //绑定组件
            //...
            if(type == 1){
                tv_live.setVisibility(View.GONE);
            }
            ll_main.setOnClickListener(v -> {
                myListener.onClick(pos);
            });
        }
    }

    public interface MyListener{
        void onClick(int pos);
    }

}
