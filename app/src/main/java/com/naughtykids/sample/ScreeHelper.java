package com.naughtykids.sample;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

/**
 * 屏幕工具类
 */
class ScreeHelper {
    private static final String TAG = "ScreeHelper";
    private static int mWidth;
    private static int mHeight;
    private static int mRotation = -1;
    static void rotationListener() {
        DisplayManager displayManager = (DisplayManager) Utils.getA11y().getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(new DisplayManager.DisplayListener() {
            @Override
            public void onDisplayAdded(int displayId) {}

            @Override
            public void onDisplayRemoved(int displayId) {}

            @Override
            public void onDisplayChanged(int displayId) {
                if (displayId == Display.DEFAULT_DISPLAY) {
                    if (setScreenSize(displayManager)) {
                        OverlayManager.getInstance().onScreenRotation();
                    }
                }
            }
        }, null);

        setScreenSize(displayManager);
    }

    private static boolean setScreenSize(DisplayManager displayManager) {
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        if (display != null) {
            int rotation = display.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;

                boolean isLandscape = (mRotation == Surface.ROTATION_90 || mRotation == Surface.ROTATION_270);
                Point outSize = new Point();
                display.getRealSize(outSize);
                mWidth = isLandscape ? outSize.y : outSize.x;
                mHeight = isLandscape ? outSize.x : outSize.y;
                return true;
            }
        }
        return false;
    }

    static int getScreenWidth() {
        return mWidth;
    }

    static int getScreenHeight() {
        return mHeight;
    }
}
