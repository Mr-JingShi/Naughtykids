package com.naughtykids.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HollowView extends View {
    private static final String TAG = "HollowView";
    // 定义要掏空的区域（相对于 View 左上角）
    private Rect hollowArea = new Rect(0, 0, 0, 0);
    private Paint borderPaint;

    public HollowView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setClickable(false);
        setFocusable(false);
        setBackground(null);
        setBackgroundColor(Color.TRANSPARENT);

        // 边框：高亮掏空区域（可选）
        borderPaint = new Paint();
        borderPaint.setColor(Color.RED);
        borderPaint.setStrokeWidth(4);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 在掏空区域“擦除”背景（视觉上透明）
        // 注意：这里不绘制任何东西，保持透明
        // 如果需要边框，可以画个框
        canvas.drawRect(hollowArea, borderPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        Log.i(TAG, "dispatchTouchEvent event:" + event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Log.i(TAG, "onTouchEvent event:" + event);
        int x = (int)event.getX();
        int y = (int)event.getY();

        // 🔑 核心逻辑：如果点击在掏空区域内，返回 false → 事件穿透！
//        if (hollowArea.contains(x, y)) {
//            Log.i(TAG, "onTouchEvent event in hollow area");
//            return false; // 不消费事件，系统会将事件传递给下层窗口
//        }

        // 否则处理其他区域的触摸（如拖动悬浮窗）
        handleOtherTouch(event);
        return false; // 消费事件
    }

    private void handleOtherTouch(MotionEvent event) {
        // 例如：拖动悬浮窗
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // 更新 params.x, params.y 并调用 windowManager.updateViewLayout()
        }
    }

    // 可选：提供方法动态修改掏空区域
    public void setHollowArea(int left, int top, int right, int bottom) {
        hollowArea.set(left, top, right, bottom);
        invalidate(); // 重绘
    }
}
