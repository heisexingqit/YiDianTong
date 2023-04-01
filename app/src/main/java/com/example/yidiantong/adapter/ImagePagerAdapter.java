package com.example.yidiantong.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.yidiantong.R;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

    private List<String> urlList;

    private List<View> viewList = new ArrayList<>();

    //点击事件
    private MyItemClickListener clickListener;

    public ImagePagerAdapter(Context mContent, List<String> urlList) {
        this.urlList = urlList;

        for (int i = 0; i < urlList.size(); ++i) {
            View v = LayoutInflater.from(mContent).inflate(R.layout.item_show_web, null, false);
            WebView wv = v.findViewById(R.id.wv_content);
            wv.loadData("<head>\n" +
                    "    <style>\n" +
                    "        * {\n" +
                    "            padding: 0px;\n" +
                    "            margin: 0px;\n" +
                    "        }" +
                    "    </style>" +
                    "    </head>" +
                    "<img onclick=\"bigimage(this)\" src=\"" + urlList.get(i) + "\" style=\"width:100%;\"/>", "text/html", "utf-8");
            wv.setFocusable(false);
            wv.setClickable(false);
            LinearLayout ll = v.findViewById(R.id.ll_content);
            ll.setPadding(0, 0, 0, 0);

            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick();
                }
            });
            viewList.add(v);
        }
    }

    public void setClickListener(MyItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getCount() {
        return urlList.size();
    }

    // 将container中的view和返回的object关联起来
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    //点击dismiss
    public interface MyItemClickListener {
        void onItemClick();
    }
}
