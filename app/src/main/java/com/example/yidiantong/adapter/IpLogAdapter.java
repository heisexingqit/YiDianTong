package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yidiantong.R;

import java.util.List;

public class IpLogAdapter extends BaseAdapter {

    private Context context;
    private List<String> logItemList;
    private MyOnclickListener myOnclickListener;

    public IpLogAdapter(Context context, List<String> log_item) {
        this.context = context;
        this.logItemList = log_item;
    }

    public void setMyOnclickListener(MyOnclickListener myOnclickListener) {
        this.myOnclickListener = myOnclickListener;
    }

    @Override
    public int getCount() {
        return logItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return logItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_array_adapter3, null);
        // 拿控件
        TextView tv_item = v.findViewById(R.id.tv_item);
        TextView tv_item_delete = v.findViewById(R.id.tv_item_delete);
        tv_item.setText(logItemList.get(i));
        if (logItemList.size() > 1 && i == logItemList.size() - 1) {
            // -------- //
            //  清除全部
            // -------- //
            tv_item.setTextColor(context.getColor(R.color.blue_btn));
            tv_item_delete.setVisibility(View.GONE);
            tv_item.setOnClickListener(_v -> {
                myOnclickListener.delete_all();
            });
        } else if (logItemList.size() == 1) {
            tv_item_delete.setVisibility(View.GONE);
        } else {
            tv_item.setText(logItemList.get(i));
            tv_item_delete.setOnClickListener(_v -> {
                myOnclickListener.delete_item(i);
            });
        }

        return v;
    }

    public interface MyOnclickListener {
        void delete_item(int pos);

        void delete_all();
    }
}
