package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class OverlayWIndow {
    private static String TAG = "OverlayWIndow";
    Context mContext;
    private WindowManager mWindowManager;

    private View mView;
    private WindowManager.LayoutParams mWindowParams;
    private boolean mShowing = false;

    public void init(Context context) {
        Log.i(TAG, "init");
        mContext = context;

        mWindowManager = (WindowManager)context.getSystemService(
                Context.WINDOW_SERVICE);

        mWindowParams = createLayoutParams();
        mView = createView();
        mView.setOnClickListener(v -> {
            Log.i(TAG, "onClick");

            hide();

            AccessibilityService service = (AccessibilityService)mContext;
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        });
    }

    boolean isShowing() {
        return mShowing;
    }

    void show() {
        if (!mShowing) {
            mShowing = true;
            mWindowManager.addView(mView, mWindowParams);
        }
    }

    void hide() {
        if (mShowing) {
            mShowing = false;
            mWindowManager.removeView(mView);
        }
    }

    private View createView() {
        View view = new View(mContext);
        view.setBackgroundColor(Color.LTGRAY);
        return view;
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        windowParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                | WindowManager.LayoutParams.FLAG_SECURE;
        windowParams.format = PixelFormat.RGBA_8888;
        //背景明暗度0~1，数值越大背景越暗，只有在flags设置了WindowManager.LayoutParams.FLAG_DIM_BEHIND 这个属性才会生效
        windowParams.dimAmount = 0.0f;
        //透明度0~1，数值越大越不透明
        windowParams.alpha = 1.0f;
        windowParams.gravity = Gravity.TOP | Gravity.LEFT;

        Display display = mWindowManager.getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        display.getRealMetrics(realMetrics);
        windowParams.width = realMetrics.widthPixels;
        windowParams.height = realMetrics.heightPixels;
        return windowParams;
    }
}
