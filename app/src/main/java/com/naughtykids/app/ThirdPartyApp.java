package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

abstract class ThirdPartyApp {
    static final String TAG = "ThirdPartyApp";
    static final String AndroidAppDialog = "android.app.Dialog";
    static final String AndroidWidgetImageView = "android.widget.ImageView";
    static final String AndroidWidgetTextView = "android.widget.TextView";
    static final String AndroidWidgetFrameLayout = "android.widget.FrameLayout";

    abstract String getPackageName();
    abstract void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event);
    boolean checkVersion() {
        boolean ret = false;
        String packageName = getPackageName();
        String versionName = Utils.getAppVersion(packageName);
        String versionNameInPreferences = PrivatePreferences.getString(packageName, "");
        Log.d(TAG, "versionName:" + versionName + " versionNameInPreferences:" + versionNameInPreferences);
        if (versionNameInPreferences.equals(versionName)) {
            ret = true;
        } else {
            PrivatePreferences.putString(packageName, versionName);
        }
        return ret;
    }

    static boolean hasApplicationWindow() {
        List<AccessibilityWindowInfo> windowInfos = Utils.getA11y().getWindows();
        for (int i = 0; i < windowInfos.size(); i++) {
            AccessibilityWindowInfo windowInfo = windowInfos.get(i);
            if (windowInfo == null) {
                continue;
            }
            if (windowInfo.getType() != AccessibilityWindowInfo.TYPE_SYSTEM) {
                AccessibilityNodeInfo nodeInfo = windowInfo.getRoot();
                if (nodeInfo == null) {
                    continue;
                }
                CharSequence packageName = nodeInfo.getPackageName();
                if (packageName == null) {
                    continue;
                }
                if (!packageName.equals(Utils.getDesktopAppPackageName())
                    && !packageName.equals(Utils.getSelfAppPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
}