package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.yidiantong.R;
import com.example.yidiantong.View.mySlantedTextView;
import com.example.yidiantong.bean.BookInfoEntity;
import com.example.yidiantong.bean.SelectCourseEntity;

import java.util.List;

public class SelectCouresAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "SelectCouresAdapter";
    private final Context context;
    private final LayoutInflater layoutInflater;
    public List<SelectCourseEntity> itemList;

    public SelectCouresAdapter(Context context, List<SelectCourseEntity> itemList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void setmItemClickListener(SelectCouresAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    private SelectCouresAdapter.MyItemClickListener mItemClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0) {
            View v = layoutInflater.inflate(R.layout.item_select_course, parent, false);
            return new SelectCouresAdapter.ItemViewHolder(v);
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //绑定数据
        if(getItemViewType(position) == 0){
            ((SelectCouresAdapter.ItemViewHolder)holder).update(position);
            //绑定事件
            //item点击事件
            holder.itemView.findViewById(R.id.ftv_sc_selsect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.itemView.findViewById(R.id.ftv_sc_selsect), position);

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void loadData(List<SelectCourseEntity> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        this.notifyDataSetChanged();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView ftv_sc_mode;
        private final TextView ftv_sc_name;
        private final TextView ftv_sc_selsect;
        private final TextView ftv_sc_time;
        private final TextView ftv_sc_type;
        private final TextView ftv_sc_subject_name;
        private final LinearLayout fll_sc_selected;
        private final mySlantedTextView fst_sc_status;
        //组件变量

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            ftv_sc_mode = itemView.findViewById(R.id.ftv_sc_mode);
            ftv_sc_name = itemView.findViewById(R.id.ftv_sc_name);
            ftv_sc_selsect = itemView.findViewById(R.id.ftv_sc_selsect);
            ftv_sc_time = itemView.findViewById(R.id.ftv_sc_time);
            ftv_sc_type = itemView.findViewById(R.id.ftv_sc_type);
            ftv_sc_subject_name = itemView.findViewById(R.id.ftv_sc_subject_name);
            fll_sc_selected = itemView.findViewById(R.id.fll_sc_selected);
            fst_sc_status = itemView.findViewById(R.id.fst_sc_status);
        }

        public void update(int pos) {
            //数据
            SelectCourseEntity item = itemList.get(pos);
            switch (item.getMode()){
                case "1":
                    ftv_sc_mode.setText("3+1+2");
                    break;
                case "2":
                    ftv_sc_mode.setText("六选三");
                    break;
                case "3":
                    ftv_sc_mode.setText("七选三");
                    break;
            }

            String time = "时间："+ item.getStartTimeStr() + "至\r\n" + item.getEndTimeStr();
            ftv_sc_time.setText(time);

            if(item.getType().equals("1")){
                ftv_sc_type.setText("正式选");
            }else {
                ftv_sc_type.setText("模拟选");
            }

            if(item.getSubjectComposeName().equals("未选科")){
                fll_sc_selected.setVisibility(View.GONE);
            }else{
                fll_sc_selected.setVisibility(View.VISIBLE);
                //ftv_sc_subject_name.setText(item.getSubjectComposeName());
                SpannableString spanString = new SpannableString(item.getSubjectComposeName());
                ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);//红
                spanString.setSpan(span, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan span1 = new ForegroundColorSpan(Color.parseColor("#33cc33"));//绿
                spanString.setSpan(span1, 3, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan span2 = new ForegroundColorSpan(Color.parseColor("#cc9900"));//黄
                spanString.setSpan(span2, 6, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ftv_sc_subject_name.setText(spanString);
            }

            switch (item.getStatus()){
                case "1":
                    fst_sc_status.setText("未发布");
                    // 其他设置选项
//                  fst_sc_status.setMode(TAG_LEFT_BAR)
//                        .setBackground(Color.parseColor("#ff6677"))
//                        .setTextColor(Color.parseColor("#000000"))
//                        .setSlantedHeight(50)
//                        .setTextSize(29);
                    break;
                case "2":
                    fst_sc_status.setText("已发布");
                    break;
                case "3":
                    fst_sc_status.setText("已暂停");
                    break;
                case "4":
                    fst_sc_status.setText("已截止");
                    break;
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
