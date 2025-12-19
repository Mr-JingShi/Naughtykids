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
    private final List<View> mSmallUsingViews = new ArrayList<>();
    private final List<View> mSmallIdleViews = new ArrayList<>();
    private WindowManager.LayoutParams mSmallParams;
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

        mSmallParams = createLayoutParams();
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
        smallShow(rect.left, rect.top, rect.width(), rect.height());
    }

    void smallShow(int x, int y, int w, int h) {
        if (x >= 0 && y >= 0 && w >= 0 && h >= 0) {
            for (View view : mSmallUsingViews) {
                if (view.getX() == x
                    && view.getY() == y
                    && view.getWidth() == w
                    && view.getHeight() == h) {
                    return;
                }
            }
            Log.i(TAG, "smallShow x:" + x + " y:" + y + " w:" + w + " h:" + h);
            mSmallParams.x = x;
            mSmallParams.y = y;
            mSmallParams.width = w;
            mSmallParams.height = h;
            View view = createView();
            mWindowManager.addView(view, mSmallParams);
            mSmallUsingViews.add(view);
        }
    }

    void smallHide() {
        for (int i = mSmallUsingViews.size() - 1; i >= 0; --i) {
            mWindowManager.removeView(mSmallUsingViews.get(i));
            mSmallIdleViews.add(mSmallUsingViews.remove(i));
        }
    }

    private View createView() {
        View view = null;
        if (mSmallIdleViews.isEmpty()) {
            view = new View(Utils.getA11y());
            view.setBackgroundColor(Color.BLUE);
        } else {
            view = mSmallIdleViews.remove(0);
        }
        return view;
    }

    private static WindowManager.LayoutParams createLayoutParams() {
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
