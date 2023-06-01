package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.LearnPlanLinkEntity;

import java.util.List;
import java.util.Map;

public class MyArrayLearnPlanAdapter extends BaseAdapter {

    private Context context;
    private List<String> itemList;

    public MyArrayLearnPlanAdapter(Context context, List<String> list) {
        this.context = context;
        itemList = list ;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_array_adapter, null);
        // 拿控件
        TextView tv_item = v.findViewById(R.id.tv_item);
        tv_item.setText(itemList.get(i));
        return v;
    }
}
