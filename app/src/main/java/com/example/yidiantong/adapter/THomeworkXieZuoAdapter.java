package com.example.yidiantong.adapter;

import android.animation.FloatEvaluator;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.HomeworkXieZuoEntity;
import com.example.yidiantong.util.PxUtils;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.List;

public class THomeworkXieZuoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<HomeworkXieZuoEntity> itemList;
    private LayoutInflater layoutInflater;
    private Context mContext;
    public List<HomeworkXieZuoEntity.Ketang> selectKTList;
    public int fl_width;


    public THomeworkXieZuoAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        mContext = context;
        itemList = new ArrayList<>();
        selectKTList = new ArrayList<>();
        fl_width = 0;
    }

    public void update(List<HomeworkXieZuoEntity> itemList) {
        this.itemList = itemList;
        selectKTList.clear();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_homework_xiezuo, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).update(position, holder);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_main;
        private CheckBox cb_main;
        private FlexboxLayout fl_main;
        private List<TextView> tv_name_list;
        private boolean isAction;
        private int selectListNum;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_main = itemView.findViewById(R.id.tv_main);
            cb_main = itemView.findViewById(R.id.cb_main);
            fl_main = itemView.findViewById(R.id.fl_main);
            tv_name_list = new ArrayList<>();
            isAction = true;
            selectListNum = 0;
        }

        public void update(int pos, RecyclerView.ViewHolder holder) {
            HomeworkXieZuoEntity item = itemList.get(pos);
            fl_main.removeAllViews();
            Log.e("wen0606", "update: " + item.teacherName + "列表: " + item.ketangList.toString());
            selectListNum = 0;
            tv_main.setText(item.teacherName);
            for (int i = 0; i < item.ketangList.size(); ++i) {
                HomeworkXieZuoEntity.Ketang kt = item.ketangList.get(i);
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_t_homework_add_block, fl_main, false);
                TextView tv_name = view.findViewById(R.id.tv_name);
                tv_name_list.add(tv_name);
                tv_name.setText(kt.value);
                tv_name.setTag(kt.key);
                unselectedTv(tv_name);
                if (fl_width > 0) {
                    ViewGroup.LayoutParams params = tv_name.getLayoutParams();
                    params.width = fl_width / 3 - PxUtils.dip2px(mContext, 15);
                    tv_name.setLayoutParams(params);
                }
                tv_name.setOnClickListener(v -> {

                    for (int j = 0; j < selectKTList.size(); ++j) {
                        HomeworkXieZuoEntity.Ketang skt = selectKTList.get(j);
                        if (skt.key.equals(v.getTag())) {
                            unselectedTv((TextView) v);
                            selectKTList.remove(j);
                            selectListNum--;
                            if (cb_main.isChecked()) {
                                isAction = false;
                                cb_main.setChecked(false);
                                isAction = true;
                            }
                            Log.e("wen0606", "update: 移除了" + selectListNum + " / " + item.ketangList.size());
                            return;
                        }
                    }
                    for (int j = 0; j < item.ketangList.size(); ++j) {
                        HomeworkXieZuoEntity.Ketang uskt = item.ketangList.get(j);
                        if (uskt.key.equals(v.getTag())) {
                            selectedTv((TextView) v);
                            selectKTList.add(uskt);
                            selectListNum++;
                            if (selectListNum == item.ketangList.size() && !cb_main.isChecked()) {
                                isAction = false;
                                cb_main.setChecked(true);
                                isAction = true;
                            }
                            Log.e("wen0606", "update: 添加了" + selectListNum + " / " + item.ketangList.size());
                            return;
                        }
                    }
                });
                cb_main.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isAction) {

                            if (b) {
                                // 全选
                                // UI层面
                                for (int i = 0; i < tv_name_list.size(); ++i) {
                                    selectedTv(tv_name_list.get(i));
                                }
                                // 逻辑层面
                                for (int i = 0; i < item.ketangList.size(); ++i) {
                                    HomeworkXieZuoEntity.Ketang kt = item.ketangList.get(i);
                                    int j;
                                    for (j = 0; j < selectKTList.size(); ++j) {
                                        HomeworkXieZuoEntity.Ketang skt = selectKTList.get(j);
                                        if (kt == skt) {
                                            break;
                                        }
                                    }
                                    if (j == selectKTList.size()) {
                                        selectListNum++;
                                        selectKTList.add(kt);
                                    }
                                }
                            } else {
                                // 全不选
                                // UI层面
                                for (int i = 0; i < tv_name_list.size(); ++i) {
                                    unselectedTv(tv_name_list.get(i));
                                }
                                for (int i = 0; i < item.ketangList.size(); ++i) {
                                    HomeworkXieZuoEntity.Ketang kt = item.ketangList.get(i);
                                    for (int j = 0; j < selectKTList.size(); ++j) {
                                        HomeworkXieZuoEntity.Ketang skt = selectKTList.get(j);
                                        if (kt == skt) {
                                            selectListNum--;
                                            selectKTList.remove(skt);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                cb_main.setChecked(false);
                fl_main.addView(view);
            }

            fl_main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    // 在这里获取fb_mark的宽度并设置给tv_num
                    for (int i = 0; i < tv_name_list.size(); ++i) {
                        TextView tv = tv_name_list.get(i);
                        ViewGroup.LayoutParams params = tv.getLayoutParams();
//                        Log.e("wen0605", "onGlobalLayout: " + fl_main.getWidth() + tv.getText().toString());
                        fl_width = fl_main.getWidth();
                        params.width = fl_main.getWidth() / 3 - PxUtils.dip2px(mContext, 15);
                        tv.setLayoutParams(params);

                        // 不要忘记移除监听器以避免多次调用
                        fl_main.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        private void selectedTv(TextView tv) {
            tv.setBackgroundResource(R.drawable.t_homework_add_select);
            tv.setTextColor(mContext.getColor(R.color.red));
        }

        private void unselectedTv(TextView tv) {
            tv.setBackgroundResource(R.drawable.t_homework_add_unselect);
            tv.setTextColor(mContext.getColor(R.color.default_gray));
        }

    }
}