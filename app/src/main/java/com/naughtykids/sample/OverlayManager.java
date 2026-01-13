package com.naughtykids.sample;

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
import java.util.Map;

public class OverlayManager {
    private static String TAG = "OverlayWindowManager";
    private WindowManager mWindowManager;
    private View mFullScreenView;
    private WindowManager.LayoutParams mFullScreenParams;
    private int mClickBackCount;
    private final List<View> mSmallIdleViews = new ArrayList<>();
    private final Map<String, View> mSmallUsingViews = new java.util.HashMap<>();
    private WindowManager.LayoutParams mSmallParams;
    private boolean mFullScreenViewShowing = false;
    private final int[] mViewLocation = new int[2];
    private static class Holder {
        private static final OverlayManager mInstance = new OverlayManager();
    }

    public static OverlayManager getInstance() {
        return OverlayManager.Holder.mInstance;
    }

    public void init() {
        Log.i(TAG, "init");

        mWindowManager = (WindowManager) Utils.getA11y().getSystemService(Context.WINDOW_SERVICE);

        mFullScreenParams = createLayoutParams();
        mFullScreenParams.width = ScreeHelper.getScreenWidth();
        mFullScreenParams.height = ScreeHelper.getScreenHeight();

        mFullScreenView = createView();
        mFullScreenView.setBackgroundColor(Color.TRANSPARENT);
        mFullScreenView.setOnClickListener(v -> {
            Log.i(TAG, "onClick");

            hide();

            Utils.clickBack(mClickBackCount);
        });

        mSmallParams = createLayoutParams();
    }

    boolean isShowing() {
        return mFullScreenViewShowing;
    }

    public void show() {
        if (!mFullScreenViewShowing) {
            mFullScreenViewShowing = true;
            mClickBackCount = 1;
            mWindowManager.addView(mFullScreenView, mFullScreenParams);
        }
    }
    public void setClickBackCount(int count) {
        this.mClickBackCount = count;
    }

    public void hide() {
        if (mFullScreenViewShowing) {
            mFullScreenViewShowing = false;
            mWindowManager.removeView(mFullScreenView);
        }
    }

    public void smallShow(String key, Rect rect) {
        smallShow(key, rect.left, rect.top, rect.width(), rect.height());
    }

    void smallShow(String key, int x, int y, int w, int h) {
        if (x >= 0 && y >= 0 && w >= 0 && h >= 0) {
            View view = mSmallUsingViews.get(key);
            if (view != null) {
                view.getLocationOnScreen(mViewLocation);
                if (mViewLocation[0] == x && mViewLocation[1] == y && view.getWidth() == w && view.getHeight() == h) {
                    return;
                }
            }
            Log.i(TAG, "smallShow id:" + key + " x:" + x + " y:" + y + " w:" + w + " h:" + h);
            mSmallParams.x = x;
            mSmallParams.y = y;
            mSmallParams.width = w;
            mSmallParams.height = h;
            if (view != null) {
                mWindowManager.updateViewLayout(view, mSmallParams);
            } else {
                view = createView();
                mWindowManager.addView(view, mSmallParams);
            }
            mSmallUsingViews.put(key, view);
        }
    }

    public void smallHide(String key) {
        View view = mSmallUsingViews.get(key);
        if (view != null) {
            mWindowManager.removeView(view);
            mSmallIdleViews.add(view);
            mSmallUsingViews.remove(key);
        }
    }

    public void smallHide() {
        for (View view : mSmallUsingViews.values()) {
            mWindowManager.removeView(view);
        }
        mSmallIdleViews.addAll(mSmallUsingViews.values());
        mSmallUsingViews.clear();
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
        params.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
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

    public void onScreenRotation() {
        Log.i(TAG, "onScreenRotation");
        if (mFullScreenViewShowing) {
            mFullScreenParams.width = ScreeHelper.getScreenWidth();
            mFullScreenParams.height = ScreeHelper.getScreenHeight();
            mWindowManager.addView(mFullScreenView, mFullScreenParams);
        }
    }
}
