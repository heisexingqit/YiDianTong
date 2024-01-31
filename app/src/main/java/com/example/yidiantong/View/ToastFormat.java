package com.example.yidiantong.View;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yidiantong.R;
import com.example.yidiantong.fragment.MainCourseFragment;
import com.example.yidiantong.util.PxUtils;

public class ToastFormat {
    private Context context;
    private TextView tipsText;
    private Toast toast = null;
    private static final int ContentID = R.id.ErrorTips;
    private static final int LayoutID = R.layout.toast_self;

    public ToastFormat(Context context) {
        this.context = context;
    }


    public void InitToast(){
        if (toast == null) {
            toast = new Toast(context);
            View view = LayoutInflater.from(context).inflate(LayoutID, null, false);
            tipsText = view.findViewById(ContentID);
            toast.setView(view);
        }
    }
    public void setGravity(int gravity){
        toast.setGravity(gravity, 0, 100);
    }
    public void setText(String tips){
        tipsText.setText(tips);
    }
    public void show(){
        toast.show();
    }
    public void setShowTime(int time){
        toast.setDuration(time);
    }
    public void setBackgroundColor(int colorId){
        // 创建一个GradientDrawable
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE); // 设置形状为矩形
        drawable.setCornerRadius(PxUtils.dip2px(context, 12)); // 设置圆角半径
        // 在需要时更改颜色
        drawable.setColor(Color.RED); // 更改颜色为红色
        tipsText.setBackground(drawable);
    }
    public void setTextColor(int color){
        tipsText.setTextColor(context.getResources().getColor(color));
    }
    public void setTextSize(float size){
        tipsText.setTextSize(size);
    }
}
