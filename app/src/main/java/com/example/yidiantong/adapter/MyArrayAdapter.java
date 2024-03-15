package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yidiantong.R;

import java.util.List;

public class MyArrayAdapter extends BaseAdapter {

    private Context context;
    private List<String> question_types;

    public MyArrayAdapter(Context context, List<String> question_types) {
        this.context = context;
        this.question_types = question_types;
    }

    public void update(List<String> question_types){
        this.question_types = question_types;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return question_types.size();
    }

    @Override
    public Object getItem(int i) {
        return question_types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_array_adapter2, null);
        // 拿控件
        TextView tv_item = v.findViewById(R.id.tv_item);
        tv_item.setText(question_types.get(i));
        return v;
    }
}
