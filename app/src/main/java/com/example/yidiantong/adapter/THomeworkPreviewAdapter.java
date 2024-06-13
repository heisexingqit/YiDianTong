package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.HomeworkPreviewEntity;

import java.util.List;

public class THomeworkPreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater layoutInflater;

    //item类型，数据
    public List<HomeworkPreviewEntity> itemList;

    public THomeworkPreviewAdapter(Context context, List<HomeworkPreviewEntity> itemList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    public void update(List<HomeworkPreviewEntity> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_t_homework_preview, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).update(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_timian;
        private final WebView wv_timian;
        private final WebView wv_answer;
        private final WebView wv_analysis;
        private final LinearLayout ll_show;
        private final LinearLayout ll_analysis;
        private final LinearLayout ll_timian;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_timian = itemView.findViewById(R.id.tv_timian);
            wv_timian = itemView.findViewById(R.id.wv_timian);
            wv_answer = itemView.findViewById(R.id.wv_answer);
            wv_analysis = itemView.findViewById(R.id.wv_analysis);
            ll_show = itemView.findViewById(R.id.ll_show);
            ll_analysis = itemView.findViewById(R.id.ll_analysis);
            ll_timian = itemView.findViewById(R.id.ll_timian);
        }

        public void update(int pos) {
            HomeworkPreviewEntity item = itemList.get(pos);
            tv_timian.setText(item.questionName);
            wv_timian.loadDataWithBaseURL(null, item.questionContent, "text/html", "utf-8", null);
            wv_answer.loadDataWithBaseURL(null, item.answer, "text/html", "utf-8", null);
            if (item.analysis == null || item.analysis.length() == 0) {
                ll_analysis.setVisibility(View.GONE);
            } else {
                wv_analysis.loadDataWithBaseURL(null, item.analysis, "text/html", "utf-8", null);
            }
            if (item.show) {
                ll_show.setVisibility(View.VISIBLE);
            } else {
                ll_show.setVisibility(View.GONE);
            }

            wv_timian.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 这里处理你的点击事件
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (item.show) {
                            item.show = false;
                            ll_show.setVisibility(View.GONE);
                        } else {
                            item.show = true;
                            ll_show.setVisibility(View.VISIBLE);
                        }
                    }
                    return false; // 返回 false 以便让 WebView 继续处理触摸事件
                }
            });
        }
    }

}
