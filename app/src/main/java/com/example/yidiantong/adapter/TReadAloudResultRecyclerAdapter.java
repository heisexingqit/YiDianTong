package com.example.yidiantong.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.ZYRecordAnswerEntity;
import com.example.yidiantong.util.TimeUtil;
import com.google.android.exoplayer2.C;

import java.util.List;

public class TReadAloudResultRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上下文
    private final Context mContext;

    // 打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<ZYRecordAnswerEntity> itemList;

    private boolean isNew;
    private View v;


    public TReadAloudResultRecyclerAdapter(Context context, List<ZYRecordAnswerEntity> itemList) {
        mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }
    public void setIsNew(boolean isNew){
        this.isNew = isNew;
    }

    public void setmItemClickListener(MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void update(List<ZYRecordAnswerEntity> itemList) {
        this.itemList = itemList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_read_aloud_result, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //绑定数据
        ((ItemViewHolder) holder).update(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //ItemHolder类
    class ItemViewHolder extends RecyclerView.ViewHolder {
        //组件变量
        private final TextView tv_No; // 序号
        private final TextView tv_scores; // 分数
        private final TextView tv_time; // 时长
        private final TextView tv_stu_answer; // 学生答案
        private final TextView tv_standard_answer; // 学生标准答案
        private final ImageView iv_icon; // 图标
        private final LinearLayout ll_redo; // 重读
        private final LinearLayout ll_delete; // 删除
        private final LinearLayout ll_hide; // 删除
        private final LinearLayout ll_play; // 播放
        private final ImageView iv_redo;
        private final TextView tv_redo;
        private final ImageView iv_delete;
        private final TextView tv_delete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //获取组件
            tv_No = itemView.findViewById(R.id.tv_No);
            tv_scores = itemView.findViewById(R.id.tv_scores);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_stu_answer = itemView.findViewById(R.id.tv_stu_answer);
            tv_standard_answer = itemView.findViewById(R.id.tv_standard_answer);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            ll_redo = itemView.findViewById(R.id.ll_redo);
            ll_delete = itemView.findViewById(R.id.ll_delete);
            ll_hide = itemView.findViewById(R.id.ll_hide);
            ll_play = itemView.findViewById(R.id.ll_play);
            iv_redo = itemView.findViewById(R.id.iv_redo);
            tv_redo = itemView.findViewById(R.id.tv_redo);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_delete = itemView.findViewById(R.id.tv_delete);
        }

        // 数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            ZYRecordAnswerEntity item = itemList.get(pos);
            tv_No.setText("(" + (pos + 1) + ")");
            // 数据更新
            tv_time.setText(TimeUtil.getRecordTime(Integer.parseInt(item.time)));
            if (item.status.equals("2")) {
                // 语音识别成功，找到出处
                tv_scores.setText(item.score + " 分");

                // 原文List
                tv_standard_answer.setText(createSpannableStringBuilder(item.yuanwenlist, false));

                ll_hide.setVisibility(View.VISIBLE);

                // 语音List
                tv_stu_answer.setText(createSpannableStringBuilder(item.yuyinlist, true));
            } else if (item.status.equals("3")) {
                // 未找到出处
                tv_scores.setText(item.score + " 分");
                ll_hide.setVisibility(View.VISIBLE);
                tv_stu_answer.setText(createSpannableStringBuilder(item.yuyinlist, false));

                // 创建一个SpannableString对象
                SpannableString spannableString = new SpannableString("原文中没有找到您朗读的内容");

                // 设置文本的颜色为红色
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 将SpannableString设置给TextView
                tv_standard_answer.setText(spannableString);
            } else if (item.status.equals("4")) {
                // 语音识别失败
                tv_scores.setText(item.score);
                ll_hide.setVisibility(View.GONE);
                // 创建一个SpannableString对象
                SpannableString spannableString = new SpannableString("语音没有识别出内容");

                // 设置文本的颜色为红色
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 将SpannableString设置给TextView
                tv_stu_answer.setText(spannableString);
            } else {
                // 语音识别失败
                tv_scores.setText(item.score);
                ll_hide.setVisibility(View.GONE);
                // 创建一个SpannableString对象
                SpannableString spannableString = new SpannableString("语音正在转写");

                // 设置文本的颜色为红色
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 将SpannableString设置给TextView
                tv_stu_answer.setText(spannableString);
            }

            ll_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.playVedio(pos, iv_icon);
                }
            });
            ll_redo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.redoVedio(pos);
                }
            });
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.deleteVedio(pos);
                }
            });
            if(!isNew){
                ll_redo.setEnabled(false);
                ll_delete.setEnabled(false);
                iv_delete.setImageResource(R.drawable.delete_recording_gray);
                iv_redo.setImageResource(R.drawable.redo_recording_gray);
                tv_delete.setTextColor(Color.parseColor("#9c9c9c"));
                tv_redo.setTextColor(Color.parseColor("#9c9c9c"));
            }

        }
    }

    // 通用方法，生成 SpannableStringBuilder
    private SpannableStringBuilder createSpannableStringBuilder(List<ZYRecordAnswerEntity.TextStatus> textStatusList, boolean hasStatus) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (ZYRecordAnswerEntity.TextStatus ts : textStatusList) {
            String text = ts.text.trim();  // 去掉 text 两端的多余空格
            int color;
            if(hasStatus){
                int status = Integer.parseInt(ts.status);
                // 如果 status 为空或者 null，使用默认颜色 "#2cbb73"
                color = status == 0 ? Color.RED : Color.parseColor("#2cbb73");
            }else{
                color = Color.BLACK;
            }

            // 创建一个新的 SpannableString，并设置颜色
            SpannableString currentText = new SpannableString(text);
            currentText.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 如果不是第一个元素，才追加一个空格
            if (spannableStringBuilder.length() > 0) {
                spannableStringBuilder.append(" ");
            }

            // 将当前的 SpannableString 追加到总的字符串中
            spannableStringBuilder.append(currentText);
        }
        return spannableStringBuilder;
    }

    // 点击事件接口
    public interface MyItemClickListener {
        void deleteVedio(int pos);

        void redoVedio(int pos);

        void playVedio(int pos, ImageView iv_icon);
    }
}
