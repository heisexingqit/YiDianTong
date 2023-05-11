package com.example.yidiantong.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

/**
 * ScrollView 监听
 * https://www.cnblogs.com/Jieth/p/9192611.html
 */

public class ReadScrollView extends ScrollView {

    public ReadScrollView(Context context) {
        super(context);
    }

    public ReadScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *
     * @param scrollX
     * @param scrollY
     * @param clampedX
     * @param clampedY
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        View view = this.getChildAt(0);

        if (this.getHeight() + this.getScrollY() == view.getHeight()) {  //通过监听滑动位置+屏幕高度=View渲染高度
            if (scrollChangedListener != null) {
                scrollChangedListener.onScrollChangedBottom();
            }
        }else if(scrollY==0){

            if (scrollChangedListener != null) {
                scrollChangedListener.onScrollChangedTop();
            }

        }

    }


    private OnScrollChangedListener scrollChangedListener;

    public void setScrollChangedListener(OnScrollChangedListener scrollChangedListener) {
        this.scrollChangedListener = scrollChangedListener;
    }

    /**
     *
     */
    public interface OnScrollChangedListener {
        //到达顶部
        void onScrollChangedTop();

        //到达底部
        void onScrollChangedBottom();
        //监听变化
//        void onScrollChanged(int l,int t,int oldl,int oldt);
    }
}
