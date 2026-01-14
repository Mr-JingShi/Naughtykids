package com.naughtykids.sample;

import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
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
    private static String mSelfAppName;
    static void setA11y(AccessibilityService a11y) {
        mA11y = a11y;

        getDesktopApp();

        mSelfAppPackageName = a11y.getPackageName();
        mSelfAppName = a11y.getPackageManager().getApplicationLabel(a11y.getApplicationInfo()).toString();
        Log.d(TAG, "SelfAppPackageName:" + mSelfAppPackageName + " SelfAppName:" + mSelfAppName);
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
    public static String getSelfAppName() {
        return mSelfAppName;
    }

    private static void getDesktopApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = mA11y.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            mDesktopAppPackageName = resolveInfo.activityInfo.packageName;
            Log.d(TAG, "DesktopAppPackageName:" + mDesktopAppPackageName);
        }
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

    public static void toggleAppIcon(boolean show) {
        PackageManager pm = mA11y.getPackageManager();
        ComponentName componentName = new ComponentName(mA11y, MainActivity.class);
        boolean enable = pm.getComponentEnabledSetting(componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        if (enable != show) {
            Authentication.getInstance().show("AABBCC", result -> {
                pm.setComponentEnabledSetting(
                        componentName,
                        show ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                );

                if (show) {
                    clickHome();
                }
            });
        }
    }
}