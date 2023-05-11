package com.example.yidiantong.View;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;


public class ClickableImageView extends AppCompatImageView {
    public ClickableImageView(@NonNull Context context) {
        super(context);
    }

    public ClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ClickableImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    private void updateView(boolean pressed) {
        if (pressed) {
            this.setAlpha(0.2f);
        } else {
            this.setAlpha(1.0f);
        }
    }
}
