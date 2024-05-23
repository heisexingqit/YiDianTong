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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookDetailEntity;

import java.util.List;

public class BooksDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BooksDetailAdapter";
    private final Context context;
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    //item类型，数据
    public List<BookDetailEntity> itemList;  // itemList中保存单元列表,每个单元中有错题列表
    public List<BookDetailEntity.errorList> errorList;  // errorList中保存错题列表,每个元素是一个错题
    private List<BookDetailEntity.errorList> quesList;  // quesList中保存错题列表,每个元素是一个错题
    public int isRefresh = 0;
    private BookDetailEntity item;
    private int errorQueNum[];  // 数组大小为单元个数,内容为每个单元有多少个错题
    public int[] posnum; // 数组大小为单元个数,内容为记录每单元标题部分在errorList中的id
    public int[] quenum; // 大小为错题数,内容为每道题在errorList中的id
    private int quesposi;

    private boolean isShowErrorNum;


    public BooksDetailAdapter(Context context, List<BookDetailEntity.errorList> errorList, List<BookDetailEntity> itemList,
                              List<BookDetailEntity.errorList> quesList, boolean isShowErrorNum) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.errorList = errorList;
        this.itemList = itemList;
        this.quesList = quesList;
        this.isShowErrorNum = isShowErrorNum;
    }

    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    // 判断是错题内容还是小标题
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            //Log.e("转换到0","000000000000");
            View v = layoutInflater.inflate(R.layout.item_book_detail_list, null, false);
            return new ItemViewHolder(v);
        } else {
            //Log.e("转换到2","222222222222222");
            View v = layoutInflater.inflate(R.layout.top_detail, null, false);
            return new TopViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (getItemViewType(position) == 0) {
            //绑定数据
            if (quenum != null) {
                for (int i = 0; i < quenum.length; i++) {
                    if (quenum[i] == position) {
                        quesposi = i;
                    }
                }
            }
            // 更新quesposi位置错题内容
            ((ItemViewHolder) holder).update(quesposi);
            // item点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                private int quesposi1;
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < quenum.length; i++) {
                        if (quenum[i] == position) {
                            quesposi1 = i;
                        }
                    }
                    mItemClickListener.onItemClick(holder.itemView, quesposi1);
                }
            });
        } else {
            ((TopViewHolder) holder).update(position);
        }
    }

    // 利用posnum[]判断是否是小标题类型
    @Override
    public int getItemViewType(int position) {
        for (int i = 1; i < itemList.size(); i++) {
            if (position == posnum[i]) {
                return 2;  // 标题类型
            }
        }
        if (position == 0) {
            return 2;   // 小标题类型
        } else {
            return 0;  // 错题类型
        }
    }

    @Override
    public int getItemCount() {
        int len = errorList.size();
        return len;
    }

    // 更新标题+题目 UI渲染数据
    public void loadData(List<BookDetailEntity.errorList> errorList) {
        this.errorList.clear();
        this.errorList.addAll(errorList);
        this.notifyDataSetChanged();
    }

    /*
    * 整理所有数据，构建索引数组
    * errorQueNum:每个单元的错题数量
    * posnum:每个单元的起始位置,即单元条目在列表中的位置 4 7 9
    * quenum:每个错题在整个列表中的位置1 2 3 5 6 8 10*/
    public void loadData2(List<BookDetailEntity> itemList)  {
        this.itemList.clear();
        this.itemList.addAll(itemList);

        // 这个数组用于存储每个条目中的错题数量。在加载数据时，会遍历每个条目，并将其对应的错题数量存储在这个数组中。
        errorQueNum = new int[itemList.size()];
        //这个数组用于存储每个单元的起始位置。在加载数据时，会根据每个单元的错题数量和前一个单元的起始位置计算出当前单元的起始位置，并将其存储在这个数组中。
        posnum = new int[itemList.size()];
        //这个数组用于存储每个错题在整个列表中的位置。在加载数据时，会遍历每个单元的错题，并将每个错题在整个列表中的位置存储在这个数组中。
        quenum = new int[quesList.size()]; // 大小为错题数,内容为每道题在errorList中的id
        int qn = 0;
        posnum[0] = 0; // 第一个单元标题，所在的errorList中的下标
        for (int i = 0; i < itemList.size(); i++) {
            String num = itemList.get(i).getErrorQueNum(); //单元中的错题数
            String sourceId = itemList.get(i).getSourceId();// 知识点id
            errorQueNum[i] = Integer.valueOf(num);// 记录第i个单元的错题数
            if (i != 0) {
                posnum[i] = posnum[i - 1] + errorQueNum[i - 1] + 1;
            }
            for (int j = 0; j < errorQueNum[i]; j++) {
                errorList.get(posnum[i] + j + 1).setSourceId(sourceId);//将当前错题的sourceId设为单元id
                quenum[qn] = posnum[i] + j + 1;
                qn = qn + 1;
            }
        }
        for(int i = 0; i < itemList.size(); ++i){
            Log.e("0124", "单元 " + i + " 的映射位置（相对于标题+题面列表）: " + posnum[i]);
        }
        for(int i = 0; i < quesList.size(); ++i){
            Log.e("0124", "第 " + i + " 题的映射位置（相对于标题+题面列表）: " + quenum[i]);
        }

        this.notifyDataSetChanged();
    }

    // 更新题目 接口请求数据
    public void loadData3(List<BookDetailEntity.errorList> quesList) {
        this.quesList.clear();
        this.quesList.addAll(quesList);
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
            BookDetailEntity.errorList itemerror = quesList.get(pos);
            // 列表序号即错题序号
            ftv_detail_num.setText("题目" + itemerror.getNum());
            // 列表平均分
            ftv_detail_score.setText("得分: "+itemerror.getStuScore()+" 全班均分: "+itemerror.getAvgScore()+"/"+itemerror.getScore());
            //错题内容显示
            String html_content = "<body style=\"color: rgb(117, 117, 117); font-size: 15px;line-height: 30px;\">" + itemerror.getShitiShow() + "</body>";
            String html = html_content.replace("#", "%23");
            fwv_content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            if (itemerror.getMp4Flag().equals("1")) {
                fiv_detail_mp4.setVisibility(View.VISIBLE);
            } else {
                fiv_detail_mp4.setVisibility(View.GONE);
            }
            if (isShowErrorNum) {
                ftv_detail_score.setVisibility(View.VISIBLE);
            } else {
                ftv_detail_score.setVisibility(View.GONE);
            }
        }
    }


    class TopViewHolder extends RecyclerView.ViewHolder {
        private final ImageView fiv_de_icon;// 标题栏图标
        private final TextView ftv_de_title;// 标题
        private final TextView ftv_de_num;//  错题数
        private final TextView ftv_de_righy;
        private int posi;

        public TopViewHolder(View v) {
            super(v);
            fiv_de_icon = itemView.findViewById(R.id.fiv_de_icon);
            ftv_de_title = itemView.findViewById(R.id.ftv_de_title);
            ftv_de_num = itemView.findViewById(R.id.ftv_de_num);
            ftv_de_righy = itemView.findViewById(R.id.ftv_de_righy);
        }

        // 更新标题栏内容
        public void update(int pos) {
            if (pos == 0) {
                posi = 0;
            } else {
                for (int i = 1; i < posnum.length; i++) {
                    if (posnum[i] == pos) {
                        posi = i;
                    }
                }
            }

            //数据
            if (itemList != null && itemList.size() > 0) {
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
                    case "3":
                        icon_id = R.drawable.class_icon;
                        break;
                    default:
                        Log.e("0122", "update: " + item.getSourceType());
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
                if (isShowErrorNum) {
                    ftv_de_num.setVisibility(View.VISIBLE);
                    ftv_de_righy.setVisibility(View.VISIBLE);
                } else {
                    ftv_de_num.setTextColor(Color.TRANSPARENT);
                    ftv_de_righy.setTextColor(Color.TRANSPARENT);
                    fiv_de_icon.setVisibility(View.GONE);
                    // 获取TextView的布局参数
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ftv_de_title.getLayoutParams();
                    // 设置TextView在其父布局中水平居中
                    layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    // 应用布局参数
                    ftv_de_title.setLayoutParams(layoutParams);
                }
            }
        }
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int pos);
    }

}
