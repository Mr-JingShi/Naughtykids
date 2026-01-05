package com.naughtykids.sample.apps;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public abstract class ThirdPartyApp {
    static final String TAG = "ThirdPartyApp";
    static final String AndroidAppDialog = "android.app.Dialog";
    static final String AndroidWidgetImageView = "android.widget.ImageView";
    static final String AndroidWidgetTextView = "android.widget.TextView";
    static final String AndroidWidgetFrameLayout = "android.widget.FrameLayout";
    static final String AndroidWidgetLinearLayout = "android.widget.LinearLayout";

    abstract String getPackageName();
    public abstract void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event);
    void checkVersion() {}
}