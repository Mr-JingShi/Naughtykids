package com.naughtykids.sample.apps;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;

class XiaoHongShu extends ThirdPartyApp {
    private static final String TAG = "XiaoHongShu";
    public static final String AppName = "小红书";
    public static final String PackageName = "com.xingin.xhs";
    public static final String AlphaAudienceActivityV2 = "com.xingin.alpha.audience.v2.AlphaAudienceActivityV2";

    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(rootNodeInfo, event);
                break;
            default:
                break;
        }
    }

    private void onWindowStateChanged(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onWindowStateChanged className:" + className);
        if (className.equals(AlphaAudienceActivityV2)) {
            OverlayManager.getInstance().show();
        }
    }
}