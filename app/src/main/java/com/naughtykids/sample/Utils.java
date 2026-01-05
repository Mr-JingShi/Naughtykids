package com.naughtykids.sample;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

public class Utils {
    private static final String TAG = "Utils";
    private static AccessibilityService mA11y;

    private static String mDesktopAppPackageName;
    private static String mSelfAppPackageName;

    static void setA11y(AccessibilityService a11y) {
        mA11y = a11y;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = a11y.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            mDesktopAppPackageName = resolveInfo.activityInfo.packageName;
            Log.d(TAG, "DesktopAppPackageName:" + mDesktopAppPackageName);
        }

        mSelfAppPackageName = a11y.getPackageName();
        Log.d(TAG, "SelfAppPackageName:" + mSelfAppPackageName);
    }

    static AccessibilityService getA11y() {
        return mA11y;
    }

    public static String getDesktopAppPackageName() {
        return mDesktopAppPackageName;
    }

    static String getSelfAppPackageName() {
        return mSelfAppPackageName;
    }

    public static String getAppVersion(String app) {
        try {
            PackageManager pm = mA11y.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(app, 0);
            Log.d(TAG, "App:" + app + " VersionName:" + packageInfo.versionName);
            return packageInfo.versionName; // 如 "26.8.0"
        } catch (Exception e) {
            Log.e(TAG, "Package not found", e);
        }
        return null;
    }

    public static boolean hasApplicationWindow() {
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

    public static void clickHome() {
        AccessibilityService service = Utils.getA11y();
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public static void clickBack(int clickBackCount) {
        for (int i = 0; i < clickBackCount; i++) {
            AccessibilityService service = Utils.getA11y();
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }
}