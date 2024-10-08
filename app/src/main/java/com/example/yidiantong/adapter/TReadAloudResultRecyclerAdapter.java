package com.example.yidiantong.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.StringRequest;
import com.example.yidiantong.MyApplication;
import com.example.yidiantong.R;
import com.example.yidiantong.bean.ReadTaskAudioEntity;
import com.example.yidiantong.bean.ZYRecordAnswerEntity;
import com.example.yidiantong.util.Constant;
import com.example.yidiantong.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TReadAloudResultRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // 上下文
    private final Context mContext;

    // 打气筒
    private final LayoutInflater layoutInflater;

    private MyItemClickListener mItemClickListener;

    // item类型，数据
    public List<ZYRecordAnswerEntity> itemList;


    public TReadAloudResultRecyclerAdapter(Context context, List<ZYRecordAnswerEntity> itemList) {
        mContext = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.itemList = itemList;
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
        }

        // 数据更新放在这里(频繁调用，不能放一次性操作，例如绑定点击事件)
        public void update(int pos) {
            ZYRecordAnswerEntity item = itemList.get(pos);
            tv_No.setText("(" + (pos + 1) + ")");
            // 数据更新
            tv_time.setText(item.time + "″"); // TODO 参数不全
           if (item.status.equals("2")) {
                // 语音识别成功，找到出处
                tv_scores.setText(item.score + " 分");
               tv_standard_answer.setText(item.text);
               ll_hide.setVisibility(View.VISIBLE);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                for (int i = 0; i < item.list.size(); i++) {
                    ZYRecordAnswerEntity.TextStatus ts = item.list.get(i);
                    String text = ts.text;
                    int status = Integer.parseInt(ts.status);

                    // 创建一个新的SpannableString，并设置颜色
                    SpannableString currentText = new SpannableString(text + " ");
                    int color = status == 0 ? Color.RED : Color.parseColor("#2cbb73");
                    currentText.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // 将当前的SpannableString追加到总的字符串中
                    if (spannableStringBuilder.length() > 0) {
                        spannableStringBuilder.append(" ");
                    }
                    spannableStringBuilder.append(currentText);
                }

                // 设置给TextView
                tv_stu_answer.setText(spannableStringBuilder);
           } else if (item.status.equals("3")) {
                // 未找到出处
                tv_scores.setText(item.score + " 分");
                ll_hide.setVisibility(View.VISIBLE);
                tv_stu_answer.setText(item.content);
                // 创建一个SpannableString对象
                SpannableString spannableString = new SpannableString("未查询到原文");

                // 设置文本的颜色为红色
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 将SpannableString设置给TextView
                tv_standard_answer.setText(spannableString);
            } else {
                // 语音识别失败
                tv_scores.setText(item.score);
                ll_hide.setVisibility(View.GONE);
                // 创建一个SpannableString对象
                SpannableString spannableString = new SpannableString("语音转义成文本异常。");

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
            ll_redo.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mItemClickListener.redoVedio(pos);
                }
            });
            ll_delete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    mItemClickListener.deleteVedio(pos);
                }
            });

        }
    }

    // 点击事件接口
    public interface MyItemClickListener {
        void deleteVedio(int pos);

        void redoVedio(int pos);

        void playVedio(int pos, ImageView iv_icon);
    }
}
