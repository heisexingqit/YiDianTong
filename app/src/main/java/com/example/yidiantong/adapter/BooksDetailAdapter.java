package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookDetailEntity;


import java.util.List;

public class BooksDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private static final String TAG = "BooksDetailAdapter";
    private final Context context;
    private final LayoutInflater layoutInflater;

    private BooksDetailAdapter.MyItemClickListener mItemClickListener;

    //item类型，数据
    public List<BookDetailEntity> itemList;
    public List<BookDetailEntity.errorList> errorList;
    public int isRefresh = 0;

    private List<BookDetailEntity.errorList> quesList;



    private BookDetailEntity item;
    private int errorQueNum[] = new int[10];
    private int[] posnum;
    private int[] quenum;
    private int quesposi;

    public BooksDetailAdapter(Context context, List<BookDetailEntity.errorList> errorList, List<BookDetailEntity> itemList, List<BookDetailEntity.errorList> quesList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.errorList = errorList;
        this.itemList = itemList;
        this.quesList = quesList;
    }

    public void setmItemClickListener(BooksDetailAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == 0){
            //Log.e("转换到0","000000000000");
            View v = layoutInflater.inflate(R.layout.item_book_detail_list, null, false);
            return new BooksDetailAdapter.ItemViewHolder(v);
        }else {
            //Log.e("转换到2","222222222222222");
            View v = layoutInflater.inflate(R.layout.top_detail, null, false);
            return new BooksDetailAdapter.TopViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if(getItemViewType(position) == 0){
            //绑定数据
            if(quenum != null){
                for(int i=0; i < quenum.length; i++){
                    if(quenum[i] == position){
                        quesposi = i;
                    }
                }
            }
            ((BooksDetailAdapter.ItemViewHolder)holder).update(quesposi);
            //item点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                private int quesposi1;

                @Override
                public void onClick(View v) {
                    for(int i=0; i < quenum.length; i++){
                        if(quenum[i] == position){
                            quesposi1 = i;
                        }
                    }
                    mItemClickListener.onItemClick(holder.itemView, quesposi1);
                }
            });
        }else {
            ((BooksDetailAdapter.TopViewHolder)holder).update(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        for(int i = 1; i<itemList.size(); i++){
            if(position == posnum[i]){
                return 2;
            }
        }
        if(position == 0){
            return 2;
        }else{
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        int len = errorList.size();
        return len;
    }

    public void loadData(List<BookDetailEntity.errorList> errorList) {
        this.errorList.clear();
        this.errorList.addAll(errorList);
        this.notifyDataSetChanged();
    }

    public void loadData2(List<BookDetailEntity> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        posnum = new int[itemList.size()];
        quenum = new int[quesList.size()];
        int qn = 0;
        posnum[0] = 0;
        for(int i = 0; i<itemList.size();i++){
            String num =itemList.get(i).getErrorQueNum();
            errorQueNum[i]=Integer.valueOf(num);
            if(i != 0){
                posnum[i] = posnum[i-1] + errorQueNum[i-1] + 1;
            }
            for(int n=0; n<errorQueNum[i]; n++){
                quenum[qn] = posnum[i] + n + 1 ;
                qn = qn + 1;
            }
        }
        this.notifyDataSetChanged();
    }

    public void loadData3(List<BookDetailEntity.errorList> quesList) {
        this.quesList.clear();
        this.quesList.addAll(quesList);
        this.notifyDataSetChanged();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final TextView ftv_detail_num;
        private final TextView ftv_detail_score;
        private final WebView fwv_content;
        private final LinearLayout fll_detail_list;
        private final ImageView fiv_detail_mp4;

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
            BookDetailEntity.errorList itemerror = quesList.get(pos);
            // 列表序号
            ftv_detail_num.setText(itemerror.getNum());
            // 列表平均分
            ftv_detail_score.setText("得分: "+itemerror.getStuScore()+" 全班平均分: "+itemerror.getAvgScore()+"/"+itemerror.getScore());
            //题面显示
            String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + itemerror.getShitiShow() + "</body>";
            String html = html_content.replace("#","%23");
            fwv_content.loadData(html, "text/html", "utf-8");
            if(itemerror.getMp4Flag().equals("1")){
                fiv_detail_mp4.setVisibility(View.VISIBLE);
            }else{
                fiv_detail_mp4.setVisibility(View.GONE);
            }
        }
    }


    class TopViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fiv_de_icon;
        private final TextView ftv_de_title;
        private final TextView ftv_de_num;
        private  int posi;
        public TopViewHolder(View v) {
            super(v);
            fiv_de_icon = itemView.findViewById(R.id.fiv_de_icon);
            ftv_de_title = itemView.findViewById(R.id.ftv_de_title);
            ftv_de_num = itemView.findViewById(R.id.ftv_de_num);
        }

        public void update(int pos) {
            if(pos == 0){
                posi = 0;
            }else{
                for(int i=1; i<posnum.length; i++){
                    if(posnum[i] == pos){
                        posi = i;
                    }
                }
            }

            //数据
            if(itemList != null && itemList.size()>0){
                item = itemList.get(posi);
                //设置图标和类型
                int icon_id = -1;
                switch (item.getSourceType()) {
                    case "1":
                        icon_id = R.drawable.guide_plan_icon;
                        break;
                    case "2":
                        icon_id = R.drawable.homework_icon;
                        break;
                    default:
                        break;
                }
                //表头图标
                fiv_de_icon.setImageResource(icon_id);
                // 知识点名称
                ftv_de_title.setText(item.getSourceName());
                // 错题数量
                //SpannableString spannableString = StringUtils.getStringWithColor(position, "#EB3324",0,len);
                ftv_de_num.setText(item.getErrorQueNum());
                ftv_de_num.setTextColor(Color.RED);
            }

        }
    }

    public interface MyItemClickListener{
        void onItemClick(View view, int pos);
    }

}