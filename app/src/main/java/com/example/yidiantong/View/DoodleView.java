package com.example.yidiantong.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.yidiantong.R;

import java.io.IOException;
import java.io.OutputStream;


public class DoodleView extends View {
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Bitmap mDoodleBitmap;
    private Canvas mDoodleCanvas;

    private RectF mDrawRect; // 绘制涂鸦的区域

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
        mDrawRect = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = mBitmap != null ? mBitmap.getHeight() * width / mBitmap.getWidth() : 0;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            mDrawRect.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, null, mDrawRect, null);
        }else {
            canvas.drawColor(Color.WHITE);
        }
        if (mDoodleBitmap != null) {
            canvas.drawBitmap(mDoodleBitmap, 0, 0, null);
        }
        canvas.drawPath(mPath, mPaint);
    }

    public void clear() {
        mPath.reset();
        if (mDoodleBitmap != null) {
            mDoodleCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mBitmap != null) {
            float scale = (float) w / mBitmap.getWidth();
            int height = (int) (mBitmap.getHeight() * scale);
            mBitmap = Bitmap.createScaledBitmap(mBitmap, w, height, false);
            mDoodleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mDoodleCanvas = new Canvas(mDoodleBitmap);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                mDoodleCanvas.drawPath(mPath, mPaint);
                break;
        }
        invalidate();
        return true;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        if (mBitmap != null) {
            mBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        requestLayout();
        invalidate();
    }

    public boolean save(Uri uri) {
        if (mBitmap == null) {
            return false;
        }

        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(uri);
            // 计算绘制涂鸦的区域
            float left = mDrawRect.left / mBitmap.getWidth();
            float top = mDrawRect.top / mBitmap.getHeight();
            float right = mDrawRect.right / mBitmap.getWidth();
            float bottom = mDrawRect.bottom / mBitmap.getHeight();

            Bitmap savedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(savedBitmap);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.drawPath(mPath, mPaint);

            // 绘制涂鸦的区域也要按比例进行缩放
            RectF drawRect = new RectF(left * savedBitmap.getWidth(), top * savedBitmap.getHeight(),
                    right * savedBitmap.getWidth(), bottom * savedBitmap.getHeight());
            // 删除这一行：canvas.drawBitmap(savedBitmap, null, new Rect(0, 0, savedBitmap.getWidth(), savedBitmap.getHeight()), null);

            // 裁剪绘制涂鸦的区域并保存
            Bitmap croppedBitmap = Bitmap.createBitmap(savedBitmap, (int) drawRect.left, (int) drawRect.top,
                    (int) (drawRect.right - drawRect.left), (int) (drawRect.bottom - drawRect.top));
            return croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


