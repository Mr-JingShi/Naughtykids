package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

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
        }

        mSelfAppPackageName = a11y.getPackageName();
    }

    static AccessibilityService getA11y() {
        return mA11y;
    }

    static String getDesktopAppPackageName() {
        return mDesktopAppPackageName;
    }

    static String getSelfAppPackageName() {
        return mSelfAppPackageName;
    }

    static String getAppVersion(String app) {
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
}