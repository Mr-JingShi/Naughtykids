package com.naughtykids.app;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

class DesktopApp extends ThirdPartyApp {
    private static final String TAG = "DestinyApp";
    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged();
                break;
            default:
                break;
        }
    }

    private void onWindowStateChanged() {
        if (!ThirdPartyApp.hasApplicationWindow()) {
            OverlayWindowManager.getInstance().smallHide();
        }
    }
}