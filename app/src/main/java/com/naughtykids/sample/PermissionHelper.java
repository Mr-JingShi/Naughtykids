package com.naughtykids.sample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

/**
 * 权限工具类
 */
public class PermissionHelper {
    private static final String TAG = "PermissionHelper";
    public static final int REQ_CODE_MIN = 1001;
    public static final int REQ_CODE_NOTIFICATION = REQ_CODE_MIN;
    public static final int REQ_CODE_OVERLAY = REQ_CODE_MIN + 1;
    public static final int REQ_CODE_BATTERY = REQ_CODE_MIN + 2;
    public static final int REQ_CODE_ACCESSIBILITY = REQ_CODE_MIN + 3;
    public static final int REQ_CODE_MAX = REQ_CODE_MIN + 4;
    private static int mRequestCode = REQ_CODE_MIN;

    public static void requestPermissions(Activity activity) {
        for (int i = mRequestCode; i < REQ_CODE_MAX; i++, mRequestCode++) {
            boolean hasPermission = true;
            switch (mRequestCode) {
                case REQ_CODE_NOTIFICATION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!hasNotificationPermission(activity)) {
                            hasPermission = false;
                            requestNotificationPermission(activity);
                        }
                    }
                    break;
                case REQ_CODE_OVERLAY:
                    if (!hasOverlayPermission(activity)) {
                        hasPermission = false;
                        requestOverlayPermission(activity);
                    }
                    break;
                case REQ_CODE_BATTERY:
                    if (!hasBatteryOptimization(activity)) {
                        hasPermission = false;
                        requestIgnoreBatteryOptimization(activity);
                    }
                    break;
                case REQ_CODE_ACCESSIBILITY:
                    if (!isAccessibilityEnabled(activity)) {
                        hasPermission = false;
                        requestAccessibilityPermission(activity);
                    }
                    break;
                default:
                    break;
            }
            if (!hasPermission) {
                break;
            }
        }
    }
    private static void requestNext(Activity activity) {
        mRequestCode++;
        requestPermissions(activity);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean hasNotificationPermission(Activity activity) {
        return activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static void requestNotificationPermission(Activity activity) {
        if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            dialog(activity, "通知", intent, REQ_CODE_NOTIFICATION);
        } else {
            activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_CODE_NOTIFICATION);
        }
    }

    private static boolean hasOverlayPermission(Activity activity) {
        return Settings.canDrawOverlays(activity);
    }

    public static void requestOverlayPermission(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        dialog(activity, "悬浮窗", intent, REQ_CODE_OVERLAY);
    }

    private static boolean hasBatteryOptimization(Activity activity) {
        final PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(activity.getPackageName());
    }

    public static void requestIgnoreBatteryOptimization(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        dialog(activity, "忽略电池优化", intent, REQ_CODE_BATTERY);
    }

    private static boolean isAccessibilityEnabled(Activity activity) {
        AccessibilityManager am = (AccessibilityManager) activity.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getId().contains(activity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private static void requestAccessibilityPermission(Activity activity)  {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        dialog(activity, "无障碍服务", intent, REQ_CODE_ACCESSIBILITY);
    }

    private static void dialog(Activity activity, String message, Intent intent, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(String.format("请授权%s权限", message));
        builder.setMessage(String.format("熊孩子应用需要%s权限才能正常运行", message));
        builder.setPositiveButton("确定", (dialog, which) -> {
            activity.startActivityForResult(intent, requestCode);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            Toast.makeText(activity, String.format("请授予%s权限", message), Toast.LENGTH_LONG).show();
        });
        builder.show();
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
        requestNext(activity);
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + " permissions:" + Arrays.toString(permissions) + " grantResults:" + Arrays.toString(grantResults));
        requestNext(activity);
    }
}
