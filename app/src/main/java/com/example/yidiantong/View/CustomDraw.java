package com.example.yidiantong.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

public class CustomDraw extends View {

    private Paint paint;
    private Path path;
    private List<Pair<Path, PathAttributes>> paths; // 存储绘制的路径及其画笔属性
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
    private ScaleGestureDetector scaleGestureDetector;
    private float contentRotationAngle = 0f;
    private PointF initialTouch = new PointF(); // 用于保存初始触摸点的坐标
    private Matrix rotationMatrix;
    private int originalWidth = 0;
    private int mode=0;
    private static final long DOUBLE_TAP_TIMEOUT = 200; // 毫秒
    private boolean isSingleTap = true;
    private Handler handler = new Handler();
    private int left;
    private int top;
    private int scaledWidth;
    private int scaledHeight;
    private int bitmapWidth;
    private int bitmapHeight;
    private float scaleY;


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

        // 获取视图和图片的尺寸
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        int viewHeight = getHeight();
        int viewWidth = getWidth();
        // 计算绘制背景图片时的缩放比例，使其完全显示在视图上
        float scaleX = (float) originalWidth / bitmapWidth;
        scaleY = scaleX;
        Matrix resizeMatrix = new Matrix();
        resizeMatrix.postScale(scaleX, scaleX); // 缩放图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, resizeMatrix, true);
        this.backgroundBitmap = resizedBitmap;
        scaledWidth= backgroundBitmap.getWidth();
        int left = (viewHeight - scaledWidth) / 2;
        matrix.postTranslate(left,0);
        invalidate();
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
        isMoveEnabled = enabled;
    }

    public boolean getMoveEnabled() {
        return isMoveEnabled;
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        paths = new ArrayList<>();
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener());
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
            canvas.drawBitmap(backgroundBitmap,0,0,null);
        }
//        if (backgroundBitmap != null) {
//            int bitmapWidth = backgroundBitmap.getWidth();
//            int bitmapHeight = backgroundBitmap.getHeight();
//            int viewHeight = getHeight();
//            int viewWidth = getWidth();
//            // 计算绘制背景图片时的缩放比例，使其完全显示在视图上
//            float scaleX = (float) originalWidth / bitmapWidth;
//            float scale = scaleX;
//            scaledWidth = (int) (bitmapWidth * scale);
//            scaledHeight = (int) (bitmapHeight * scale);
//            left = (viewWidth - scaledWidth) / 2;
//            top = (viewHeight - scaledHeight) / 2;
//            canvas.drawBitmap(backgroundBitmap, null, new Rect(left, top, left + scaledWidth, top + scaledHeight), null);
//        }
        // 绘制所有路径
        for (Pair<Path, PathAttributes> pair : paths) {
            paint.setColor(pair.second.getColor()); // 设置画笔颜色为路径对应的颜色
            paint.setStrokeWidth(pair.second.getStrokeWidth()); // 设置画笔粗细为路径对应的粗细
            canvas.drawPath(pair.first, paint); // 绘制路径
        }
        // 恢复画布状态
        canvas.restore();
    }

    //设置画笔颜色
    public void setPenColor(int color) {
        paint.setColor(color);
    }

    //设置画笔粗细
    public void setPenStrokeWidth(float strokeWidth) {
        paint.setStrokeWidth(strokeWidth);
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
            paths.remove(paths.size() - 1); // 移除最后一个路径及其属性
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

    //处理控件大小
    public void makeSquare() {
        ViewTreeObserver observer = this.getViewTreeObserver();
        if (observer.isAlive()) {
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewGroup.LayoutParams layoutParams = getLayoutParams();
                    int height = getHeight();
                    // 如果原始宽度为0，则保存原始宽度
                    if (originalWidth == 0) {
                        originalWidth = getWidth();
                    }
                    layoutParams.width = height;
                    Log.d("HSK", "height" + height);
                    Log.d("HSK", "width" + layoutParams.width);
                    setLayoutParams(layoutParams);

                    // 移除监听器，防止多次调用
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (isDrawingEnabled) {
//            float x = event.getX();
//            float y = event.getY();
//            // 检查触摸坐标是否在背景图的范围内
//            if (!isWithinBackgroundBounds(x, y)) {
//                return false; // 如果在背景范围外，退出onTouchEvent
//            }
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    // 对触摸点进行逆变换
//                    float[] downPoint = {x, y};
//                    Matrix savedMatrix = new Matrix(matrix);
//                    savedMatrix.invert(savedMatrix);
//                    savedMatrix.mapPoints(downPoint);
//                    path = new Path();
//                    path.moveTo(downPoint[0], downPoint[1]);
//                    paths.add(new Pair<>(path, new PathAttributes(paint.getColor(), paint.getStrokeWidth()))); // 记录当前画笔的颜色和粗细
//                    currentPathIndex++;
//                    return true;
//                case MotionEvent.ACTION_MOVE:
//                    // 对触摸点进行逆变换
//                    float[] movePoint = {x, y};
//                    savedMatrix = new Matrix(matrix);
//                    savedMatrix.invert(savedMatrix);
//                    savedMatrix.mapPoints(movePoint);
//                    path.lineTo(movePoint[0], movePoint[1]);
//                    // 恢复原始矩阵
//                    break;
//                default:
//                    return false;
//            }
//            // 强制重绘视图
//            invalidate();
//        }
//        if (isMoveEnabled) {
//            switch (event.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    if (event.getPointerCount() == 1) {
//                        isSingleTap = true;//单指操作
//                        startPoint.set(event.getX(), event.getY());//获取手指坐标
//                        currentPoint.set(startPoint);
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (isSingleTap) {
//                                    // 如果在规定时间内没有第二个手指按下，则切换为单指拖动模式
//                                    mode = 1;
//                                }
//                            }
//                        }, DOUBLE_TAP_TIMEOUT);
//                    }else  mode = 2;
//                    break;
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    if (event.getPointerCount() == 2) {
//                        mode = 2; // 确保两根手指时进入缩放模式
//                    }
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if (mode == 1) { // 单指拖动
//                        float dx = event.getX() - currentPoint.x;
//                        float dy = event.getY() - currentPoint.y;
//                        if (Math.hypot(dx, dy) >= touchSlop) {
//                            matrix.postTranslate(dx, dy);
//                            invalidate();
//                            currentPoint.set(event.getX(), event.getY());
//                        }
//                    } else if (mode == 2) {
//                        scaleGestureDetector.onTouchEvent(event);
//                    }
//
//                    Log.d("hsk0520", "onTouchEvent: "+mode);
//                    //双指缩放
//                    break;
//                case MotionEvent.ACTION_UP:
//                    mode = 0;
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        return true;
//
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrawingEnabled) {
            float x = event.getX();
            float y = event.getY();
            // 检查触摸坐标是否在背景图的范围内
            if (!isWithinBackgroundBounds(x, y)) {
                return false; // 如果在背景范围外，退出onTouchEvent
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 对触摸点进行逆变换
                    float[] downPoint = {x, y};
                    matrix.invert(matrix);
                    matrix.mapPoints(downPoint);
                    path = new Path();
                    path.moveTo(downPoint[0], downPoint[1]);
                    paths.add(new Pair<>(path, new PathAttributes(paint.getColor(), paint.getStrokeWidth()))); // 记录当前画笔的颜色和粗细
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
        if (isMoveEnabled) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (event.getPointerCount() == 1) {
                        isSingleTap = true;//单指操作
                        startPoint.set(event.getX(), event.getY());//获取手指坐标
                        currentPoint.set(startPoint);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isSingleTap) {
                                    // 如果在规定时间内没有第二个手指按下，则切换为单指拖动模式
                                    mode = 1;
                                    Log.d("HSK1", "mode: " + mode);
                                }
                            }
                        }, DOUBLE_TAP_TIMEOUT);
                        Log.d("HSK1", "mode: " + mode);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == 1) { // 单指拖动
                        float dx = event.getX() - currentPoint.x;
                        float dy = event.getY() - currentPoint.y;
                        if (Math.hypot(dx, dy) >= touchSlop) {
                            matrix.postTranslate(dx, dy);
                            invalidate();
                            currentPoint.set(event.getX(), event.getY());
                        }
                    }
                    //双指缩放
                    scaleGestureDetector.onTouchEvent(event);
                    break;
                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;
                default:
                    mode = 0;
            }
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    startPoint.set(event.getX(), event.getY());
//                    currentPoint.set(startPoint);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    float dx = event.getX() - currentPoint.x;
//                    float dy = event.getY() - currentPoint.y;
//                    if (Math.sqrt(dx * dx + dy * dy) >= touchSlop) {
//                        // 移动背景图片
//                        matrix.postTranslate(dx, dy);
//                        invalidate();
//                        currentPoint.set(event.getX(), event.getY());
//                    }
//                    break;
//            }

        }

        return true;

    }

    // 检查触摸坐标是否在背景图的范围内
    private boolean isWithinBackgroundBounds(float x, float y) {
        if (backgroundBitmap == null) return false;

        // 使用Matrix计算变换后的图片边界
        RectF bitmapRect = new RectF(0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
        matrix.mapRect(bitmapRect);

        // 直接检查变换后的图片边界内是否包含触摸点
        return bitmapRect.contains(x, y);
//        if (backgroundBitmap != null) {
//            int bitmapWidth = backgroundBitmap.getWidth();
//            int bitmapHeight = backgroundBitmap.getHeight();
//            int viewHeight = getHeight();
//            int viewWidth = getWidth();
//            // 计算绘制背景图片时的缩放比例，使其完全显示在视图上
//            float scaleX = (float) originalWidth / bitmapWidth;
//            float scale = scaleX;
//            int scaledWidth = (int) (bitmapWidth * scale);
//            int scaledHeight = (int) (bitmapHeight * scale);
//            int left = (viewWidth - scaledWidth) / 2;
//            int top = (viewHeight - scaledHeight) / 2;
//            // 计算应用变换后的背景图的范围
//            RectF backgroundBounds = new RectF();
//            matrix.mapRect(backgroundBounds, new RectF(left, top, left + scaledWidth, top + scaledHeight));
//
//            return backgroundBounds.contains(x, y);
//        }
        //return false;
    }

    // ScaleGestureListener 类用于处理捏合手势
    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            scaleFactor *= detector.getScaleFactor(); // 获取缩放因子
//            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, maxScaleFactor)); // 限制缩放比例
//
//            // 计算缩放中心点，即两指间的中点
//            PointF midPoint = new PointF(detector.getFocusX(), detector.getFocusY());
//
//            // 应用缩放时，确保以缩放中心点为中心进行缩放
//            matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), midPoint.x, midPoint.y);
//
//            invalidate(); // 通知视图需要重绘
//            return true;
//        }
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor(); // 从检测器获取缩放因子
            scaleFactor = Math.max(minScaleFactor, Math.min(scaleFactor, maxScaleFactor)); // 确保 scaleFactor 在范围内
            matrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY());
            invalidate(); // 重绘视图
            return true;
        }
    }

    //保存绘制后的照片
    public Bitmap getDrawnBitmap() {
        Bitmap drawnBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(drawnBitmap);

        // 重要步骤：复制一份原始变换矩阵，因为我们不希望修改实际用于显示的matrix
        Matrix savedMatrix = new Matrix(matrix);

        // 应用当前变换矩阵到新的Canvas上，确保线条位置正确
        //canvas.setMatrix(savedMatrix);

        // 绘制背景图片，此时背景也会受到matrix影响，包含所有变换
        canvas.drawBitmap(backgroundBitmap, 0,0,null);

        // 绘制所有路径，这些路径已经应用了matrix，因此直接绘制即可
        for (Pair<Path, PathAttributes> pair : paths) {
            paint.setColor(pair.second.getColor());
            paint.setStrokeWidth(pair.second.getStrokeWidth());
            canvas.drawPath(pair.first, paint);
        }

        // 请确保处理完所有绘制后，释放资源（如果有的话）

        return drawnBitmap;
        // 创建一个 Bitmap，宽高与背景图片一致
//        Bitmap drawnBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(drawnBitmap);
//        canvas.setMatrix(matrix);
//
//        // 绘制背景图片
//        //canvas.translate(-transformedBackgroundRect.left, -transformedBackgroundRect.top);
//        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
//
//        // 绘制所有路径
//        for (Pair<Path, PathAttributes> pair : paths) {
//            paint.setColor(pair.second.getColor()); // 设置画笔颜色为路径对应的颜色
//            paint.setStrokeWidth(pair.second.getStrokeWidth()); // 设置画笔粗细为路径对应的粗细
//            canvas.drawPath(pair.first, paint); // 绘制路径
//        }
//        return drawnBitmap;
    }

    //存储画笔的颜色和粗细
    public class PathAttributes {
        private int color;
        private float strokeWidth;

        public PathAttributes(int color, float strokeWidth) {
            this.color = color;
            this.strokeWidth = strokeWidth;
        }

        public int getColor() {
            return color;
        }

        public float getStrokeWidth() {
            return strokeWidth;
        }
    }
}
