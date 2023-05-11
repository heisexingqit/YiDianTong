package com.example.yidiantong.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;

import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookDetailEntity;

import com.example.yidiantong.util.RemoveInterface;


import java.util.List;

public class BooksRecoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "BooksRecoverAdapter";

    private final Context context;
    private final LayoutInflater layoutInflater;
    private RemoveInterface pageing;

    //item类型，数据
    public List<BookDetailEntity> itemList;
    public List<BookDetailEntity.errorList> errorList;
    private List<BookDetailEntity.errorList> quesList;
    private BookDetailEntity item;

    // 标题数组，题目数组定义
    private int errorQueNum[] = new int[10];
    private int[] posnum;
    private int[] quenum;
    private int quesposi;

    public BooksRecoverAdapter(Context context, List<BookDetailEntity.errorList> errorList,List<BookDetailEntity> itemList, List<BookDetailEntity.errorList> quesList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.errorList = errorList;
        this.itemList = itemList;
        this.quesList = quesList;
        pageing = (RemoveInterface) context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            View v = layoutInflater.inflate(R.layout.item_book_recover_list, null, false);
            return new BooksRecoverAdapter.ItemViewHolder(v);
        }else{
            View v = layoutInflater.inflate(R.layout.top_detail, null, false);
            return new BooksRecoverAdapter.TopViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == 0){
            //绑定数据
            if(quenum != null){
                Log.e("position",""+position);
                Log.e("quenum",""+quenum.length);
                for(int i=0; i < quenum.length; i++){
                    if(quenum[i] == position){
                        quesposi = i;
                    }
                }
            }
            ((BooksRecoverAdapter.ItemViewHolder)holder).update(quesposi);
        }else {
            ((BooksRecoverAdapter.TopViewHolder)holder).update(position);
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
        return errorList.size();
    }

    public void loadData(List<BookDetailEntity.errorList> errorList) {
        this.errorList.clear();
        this.errorList.addAll(errorList);
        this.notifyDataSetChanged();
    }

    public void loadData2(List<BookDetailEntity.errorList> quesList) {
        this.quesList.clear();
        this.quesList.addAll(quesList);
        Log.e("recquesList",""+quesList);
        this.notifyDataSetChanged();
    }

    public void loadData3(List<BookDetailEntity> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        posnum = new int[itemList.size()]; // 标题
        quenum = new int[quesList.size()]; // 题目
        Log.e("quesList.size()",""+quesList.size());
        int qn = 0;
        posnum[0] = 0;
        for(int i = 0; i<itemList.size();i++){
            String num =itemList.get(i).getErrorQueNum();
            Log.e("num",""+num);
            errorQueNum[i]=Integer.valueOf(num);

            if(i != 0){
                posnum[i] = posnum[i-1] + errorQueNum[i-1] + 1;
                Log.e("posnum[i]",""+posnum[i]);
            }
            Log.e("posnum[i]",""+posnum[i]);
            Log.e("errorQueNum[i]",""+errorQueNum[i]);
            for(int n=0; n < errorQueNum[i]; n++){
                Log.e("qn",""+qn);
                quenum[qn] = posnum[i] + n + 1;
                qn = qn + 1;
            }
        }
        this.notifyDataSetChanged();
    }



    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //组件变量
        private final WebView fwv_content;
        private final ImageView fiv_recover;
        private BookDetailEntity.errorList itemerror;
        private int pos_update;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            fwv_content = itemView.findViewById(R.id.fwv_content);
            fiv_recover = itemView.findViewById(R.id.fiv_recover);
            fiv_recover.setOnClickListener(this);
        }

        //数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {

            pos_update = pos;
            if(quesList != null && quesList.size()>0){
                itemerror = quesList.get(pos);
                //题面显示
                String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + itemerror.getShitiShow() + "</body>";
                String html = html_content.replace("#","%23");
                fwv_content.loadData(html, "text/html", "utf-8");
            }
        }

        @Override
        public void onClick(View v) {
            //建立对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
            //自定义title样式
            TextView tv = new TextView(context);
            String questionId = itemerror.getQuestionId();
            tv.setText("是否恢复本题？");    //内容
            tv.setTextSize(17);//字体大小
            tv.setPadding(30, 40, 30, 40);//位置
            tv.setTextColor(Color.parseColor("#000000"));//颜色
            //设置title组件
            builder.setCustomTitle(tv);
            builder.setMessage("确认后本试题将被移出回收站，恢复到错题本原来位置。");
            AlertDialog dialog = builder.create();
            // 按钮
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pageing.restore(questionId);
                    pageing.update();
                }
            });
            //禁止返回和外部点击
            builder.setCancelable(false);
            //对话框弹出
            builder.show();
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

}
