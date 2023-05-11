package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;
import com.example.yidiantong.util.PxUtils;

public class DrawEditorDialog extends Dialog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBarBrush;

    private MyInterface myInterface;

    private Context mContext;

    public void setMyInterface(MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    public DrawEditorDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setContentView(R.layout.draw_editor_dialog);

        mSeekBarBrush = findViewById(R.id.sb_brush);

        mSeekBarBrush.setOnSeekBarChangeListener(this);

        findViewById(R.id.civ_color1).setOnClickListener(this);
        findViewById(R.id.civ_color2).setOnClickListener(this);
        findViewById(R.id.civ_color3).setOnClickListener(this);
        findViewById(R.id.civ_color4).setOnClickListener(this);
        findViewById(R.id.civ_color5).setOnClickListener(this);
        findViewById(R.id.civ_color6).setOnClickListener(this);
        findViewById(R.id.civ_color7).setOnClickListener(this);
        findViewById(R.id.civ_color8).setOnClickListener(this);
        findViewById(R.id.civ_color9).setOnClickListener(this);
        findViewById(R.id.civ_color10).setOnClickListener(this);
        findViewById(R.id.civ_color11).setOnClickListener(this);
        findViewById(R.id.civ_color12).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Drawable drawable = view.getBackground();

        if (drawable instanceof ColorDrawable) {
            myInterface.changeColor(((ColorDrawable) drawable).getColor());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        myInterface.changeBrush(i);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int getProgress(){
        return mSeekBarBrush.getProgress();
    }

    public void addPadding() {
        LinearLayout ll_root = findViewById(R.id.ll_root);
        ll_root.setPadding(PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,20), PxUtils.dip2px(mContext,40));
    }

    public interface MyInterface{
        void changeBrush(int progress);
        void changeColor(int colorInt);
    }

}

