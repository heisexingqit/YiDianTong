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

import java.util.List;

// 展示举一反三的错题列表
public class BookExerciseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BookExerciseAdapter";
    public List<BookExerciseEntity> itemList;  // itemList中保存单元列表,每个单元中有错题列表
    private final Context context;
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public int isRefresh = 0;



    public BookExerciseAdapter(Context context, List<BookExerciseEntity> itemList) {
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
        View v = layoutInflater.inflate(R.layout.item_book_detail_list, null, false);
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
    public void loadData(List<BookExerciseEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }


    //ItemHolder类,更新item中内容
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final TextView ftv_detail_num;  //错题编号
        private final TextView ftv_detail_score; //得分
        private final WebView fwv_content;   //题目内容
        private final LinearLayout fll_detail_list;
        private final ImageView fiv_detail_mp4;//编号后图像,实际没有用

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            ftv_detail_num = itemView.findViewById(R.id.ftv_detail_num);
            ftv_detail_score = itemView.findViewById(R.id.ftv_detail_score);
            fll_detail_list = itemView.findViewById(R.id.fll_detail_list);
            fwv_content = itemView.findViewById(R.id.fwv_content);
            fiv_detail_mp4 = itemView.findViewById(R.id.fiv_detail_mp4);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            BookExerciseEntity itemerror = itemList.get(pos);
            // 列表序号即错题序号
            ftv_detail_num.setText("题目" + (pos + 1));

            //错题内容显示
            String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + itemerror.getShiTiShow() + "</body>";
            String html = html_content.replace("#", "%23");
            fwv_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            ftv_detail_score.setVisibility(View.GONE);
            fiv_detail_mp4.setVisibility(View.GONE);
        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int pos);
    }

}
