package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class OverlayWindowManager {
    private static String TAG = "OverlayWindowManager";
    private WindowManager mWindowManager;
    private View mFullScreenView;
    private WindowManager.LayoutParams mFullScreenParams;
    private final List<View> mSmallViews = new ArrayList<>();
    private boolean mFullScreenViewShowing = false;

    private static class Holder {
        private static final OverlayWindowManager mInstance = new OverlayWindowManager();
    }

    public static OverlayWindowManager getInstance() {
        return OverlayWindowManager.Holder.mInstance;
    }

    public void init() {
        Log.i(TAG, "init");

        mWindowManager = (WindowManager) Utils.getA11y().getSystemService(Context.WINDOW_SERVICE);

        mFullScreenParams = createLayoutParams();
        Display display = mWindowManager.getDefaultDisplay();
        DisplayMetrics realMetrics = new DisplayMetrics();
        display.getRealMetrics(realMetrics);
        mFullScreenParams.width = realMetrics.widthPixels;
        mFullScreenParams.height = realMetrics.heightPixels;

        mFullScreenView = createView();
        mFullScreenView.setBackgroundColor(Color.LTGRAY);
        mFullScreenView.setOnClickListener(v -> {
            Log.i(TAG, "onClick");

            hide();

            AccessibilityService service = Utils.getA11y();
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        });
    }

    boolean isShowing() {
        return mFullScreenViewShowing;
    }

    void show() {
        if (!mFullScreenViewShowing) {
            mFullScreenViewShowing = true;
            mWindowManager.addView(mFullScreenView, mFullScreenParams);
        }
    }

    void hide() {
        if (mFullScreenViewShowing) {
            mFullScreenViewShowing = false;
            mWindowManager.removeView(mFullScreenView);
        }
    }

    void smallShow(Rect rect) {
        Log.i(TAG, "smallShow rect:" + rect);
        smallShow(rect.left, rect.top, rect.width(), rect.height());
    }

    void smallShow(int x, int y, int w, int h) {
        Log.i(TAG, "smallShow x:" + x + " y:" + y + " w:" + w + " h:" + h);
        if (x >= 0 && y >= 0 && w >= 0 && h >= 0) {
            for (View view : mSmallViews) {
                if (view.getX() == x
                    && view.getY() == y
                    && view.getWidth() == w
                    && view.getHeight() == h) {
                    return;
                }
            }
            WindowManager.LayoutParams params = createLayoutParams();
            params.x = x;
            params.y = y;
            params.width = w;
            params.height = h;
            View view = createView();
            view.setBackgroundColor(Color.BLUE);
            mWindowManager.addView(view, params);
            mSmallViews.add(view);
        }
    }

    void smallHide() {
        for (int i = mSmallViews.size() - 1; i >= 0; --i) {
            mWindowManager.removeView(mSmallViews.get(i));
            mSmallViews.remove(i);
        }
    }

    private View createView() {
        return new View(Utils.getA11y());
    }

    private WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP
                ? WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                : WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                | WindowManager.LayoutParams.FLAG_SECURE;
        params.format = PixelFormat.RGBA_8888;
        params.dimAmount = 0.0f;
        params.alpha = 1.0f;
        params.gravity = Gravity.TOP | Gravity.START;
        return params;
    }
}
