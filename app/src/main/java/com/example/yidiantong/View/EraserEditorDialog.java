package com.example.yidiantong.View;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.example.yidiantong.R;
import com.example.yidiantong.util.PxUtils;

public class EraserEditorDialog extends Dialog implements SeekBar.OnSeekBarChangeListener {
    private SeekBar mSeekBarBrush;

    private EraserEditorDialog.MyInterface myInterface;

    private Context mContext;
    public void setMyInterface(EraserEditorDialog.MyInterface myInterface) {
        this.myInterface = myInterface;
    }

    public EraserEditorDialog(@NonNull Context context) {
        super(context);

        mContext = context;

        setContentView(R.layout.eraser_editor_dialog);

        mSeekBarBrush = findViewById(R.id.sb_brush);

        mSeekBarBrush.setOnSeekBarChangeListener(this);

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
    }
}
