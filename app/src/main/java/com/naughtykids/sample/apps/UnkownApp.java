package com.naughtykids.sample.apps;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.Utils;

import java.util.List;

class UnkownApp extends ThirdPartyApp {
    private static final String TAG = "UnkownApp";

    @Override
    public String getPackageName() {
        return Utils.getDesktopAppPackageName();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(rootNodeInfo, event);
                break;
            default:
                break;
        }
    }

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onViewClicked className:" + className);
        if (className.equals(AndroidViewGroup)) {
            List<CharSequence> texts = event.getText();
            if (texts.isEmpty()) {
                Log.d(TAG, "onViewClicked texts is Empty");
                return;
            }
            String text = texts.get(0).toString();
            if (text.equals(Utils.getSelfAppName())) {
                Log.d(TAG, "onViewClicked " + Utils.getSelfAppName());
                Utils.toggleAppIcon(true);
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