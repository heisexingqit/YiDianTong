package com.example.yidiantong.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CustomDraw extends View {

    private Paint paint;
    private Path path;
    private List<Path> paths; // 存储绘制的路径
    private int currentPathIndex = -1; // 当前路径的索引
    private Bitmap backgroundBitmap;//背景
    private boolean isDrawingEnabled = true;
    private Matrix matrix = new Matrix();
    private PointF startPoint = new PointF();
    private PointF currentPoint = new PointF();
    private int touchSlop = 20; // 触摸移动的最小距离
    private boolean isMoveEnabled = false;//是否允许移动
    private float scaleFactor = 1.0f;
    private float maxScaleFactor = 3.0f;//放大的最大倍数
    private float minScaleFactor = 0.5f;//缩小的最大倍数


    public CustomDraw(Context context) {
        super(context);
        init();
    }

    public CustomDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDraw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    // 设置背景图片
    public void setBackgroundBitmap(Bitmap bitmap) {
        this.backgroundBitmap = bitmap;
        invalidate(); // 重新绘制视图
    }
    // 启用或禁用绘画模式
    public void setDrawingEnabled(boolean enabled) {
        isDrawingEnabled = enabled;
    }
    public boolean getDrawingEnabled() {
        return isDrawingEnabled;
    }
    // 启用或禁用移动背景图片的功能
    public void setMoveEnabled(boolean enabled) {
        isMoveEnabled = enabled ;
    }
    public boolean getMoveEnabled() {
        return isMoveEnabled ;
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        paths = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 保存画布状态
        canvas.save();
        // 变换画布
        canvas.concat(matrix);
        // 绘制背景图片
        if (backgroundBitmap != null) {
            canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        }
        // 绘制所有路径
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
        // 恢复画布状态
        canvas.restore();
    }
    // 放大背景图片
    public void zoomIn() {
        if (scaleFactor < maxScaleFactor) {
            scaleFactor *= 1.1f;
            matrix.postScale(1.1f, 1.1f);
            invalidate();
        }
    }

    // 缩小背景图片
    public void zoomOut() {
        if (scaleFactor > minScaleFactor) {
            scaleFactor *= 0.9f;
            matrix.postScale(0.9f, 0.9f);
            invalidate();
        }
    }
    // 撤销上一步绘画操作
    public void undo() {
        if (currentPathIndex >= 0) {
            paths.remove(currentPathIndex);
            currentPathIndex--;
            invalidate(); // 重新绘制视图
        }
    }
    // 清空所有绘画操作
    public void clear() {
        paths.clear();
        currentPathIndex = -1;
        invalidate(); // 重新绘制视图
    }
    // 旋转方法
    public void rotateBackground() {
        float currentRotation = this.getRotation(); // 获取当前旋转角度
        float newRotation = currentRotation + 90; // 将旋转角度增加90度
        this.setRotation(newRotation); // 设置新的旋转角度
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrawingEnabled) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 对触摸点进行逆变换
                    float[] downPoint = {x, y};
                    matrix.invert(matrix);
                    matrix.mapPoints(downPoint);
                    path = new Path();
                    path.moveTo(downPoint[0], downPoint[1]);
                    paths.add(path);
                    currentPathIndex++;
                    // 恢复原始矩阵
                    matrix.invert(matrix);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    // 对触摸点进行逆变换
                    float[] movePoint = {x, y};
                    matrix.invert(matrix);
                    matrix.mapPoints(movePoint);
                    path.lineTo(movePoint[0], movePoint[1]);
                    // 恢复原始矩阵
                    matrix.invert(matrix);
                    break;
                default:
                    return false;
            }
            // 强制重绘视图
            invalidate();
        }
        if(isMoveEnabled){
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startPoint.set(event.getX(), event.getY());
                    currentPoint.set(startPoint);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - currentPoint.x;
                    float dy = event.getY() - currentPoint.y;
                    if (Math.sqrt(dx * dx + dy * dy) >= touchSlop) {
                        // 移动背景图片
                        matrix.postTranslate(dx, dy);
                        invalidate();
                        currentPoint.set(event.getX(), event.getY());
                    }
                    break;
            }

        }

        return true;

    }
    //保存绘制后的照片
    public Bitmap getDrawnBitmap() {
        // 创建一个 Bitmap，宽高与背景图片一致
        Bitmap drawnBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(drawnBitmap);

        // 绘制背景图片
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);

        // 绘制所有路径
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }

        return drawnBitmap;
    }
}
