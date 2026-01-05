package com.naughtykids.sample.apps;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;
import com.naughtykids.sample.Utils;

import java.util.List;

class DesktopApp extends ThirdPartyApp {
    private static final String TAG = "DestinyApp";

    @Override
    public String getPackageName() {
        return Utils.getDesktopAppPackageName();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged();
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(rootNodeInfo, event);
                break;
            default:
                break;
        }
    }

    private void onWindowStateChanged() {
        if (!Utils.hasApplicationWindow()) {
            OverlayManager.getInstance().smallHide();
        }
    }

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onViewClicked className:" + className);
        if (className.equals(AndroidWidgetTextView)) {
            CharSequence contentDescription = event.getContentDescription();
            if (contentDescription == null) {
                Log.d(TAG, "onViewClicked contentDescription == null");
                return;
            }
            List<CharSequence> texts = event.getText();
            if (texts.isEmpty()) {
                Log.d(TAG, "onViewClicked texts is Empty");
                return;
            }
            if (contentDescription.equals(Kwai.AppName) && texts.get(0).equals(Kwai.AppName)) {
                Log.d(TAG, "onViewClicked 快手");
                checkVersion(Kwai.PackageName);
            } else if (contentDescription.equals(Douyin.AppName) && texts.get(0).equals(Douyin.AppName)) {
                Log.d(TAG, "onViewClicked 抖音");
                checkVersion(Douyin.PackageName);
            }
        }
    }

    public void checkVersion(String packageName) {
        if (AppFactory.getInstance().hasApp(packageName)) {
            ThirdPartyApp thirdPartyApp = AppFactory.getInstance().getApp(packageName);
            if (thirdPartyApp != null) {
                thirdPartyApp.checkVersion();
            }
        }
    }
}