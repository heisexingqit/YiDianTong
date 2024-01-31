package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yidiantong.R;
import com.example.yidiantong.bean.BookSelectorEntity;

import java.util.List;

public class BookSelectorAdapter extends BaseAdapter {
    private List<BookSelectorEntity> itemList;
    private Context context;

    public BookSelectorAdapter(Context context, List<BookSelectorEntity> itemList) {
        this.itemList = itemList;
        this.context = context;
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_selector_item, null);
        TextView tv_name = view.findViewById(R.id.tv_name);
        TextView tv_num = view.findViewById(R.id.tv_num);
        tv_name.setText(itemList.get(i).getSourceName());
        tv_num.setText(itemList.get(i).getNum());
        return view;
    }
}
