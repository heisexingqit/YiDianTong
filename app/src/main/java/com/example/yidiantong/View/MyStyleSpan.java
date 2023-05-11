package com.example.yidiantong.View;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.StyleSpan;

public class MyStyleSpan extends StyleSpan {

    public MyStyleSpan(int style) {

        super(style);

    }

    @Override

    public int describeContents() {

// TODO Auto-generated method stub

        return super.describeContents();

    }

    @Override

    public int getSpanTypeId() {

        return super.getSpanTypeId();

    }

    @Override

    public int getStyle() {

        return super.getStyle();

    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setFakeBoldText(true);
        super.updateDrawState(ds);

    }

    @Override

    public void updateMeasureState(TextPaint paint) {

        paint.setFakeBoldText(true);
        super.updateMeasureState(paint);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        // TODO Auto-generated method stub
        super.writeToParcel(dest, flags);

    }

}