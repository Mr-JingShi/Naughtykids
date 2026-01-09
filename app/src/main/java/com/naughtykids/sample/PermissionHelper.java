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

public class PermissionHelper {
    private static final String TAG = "PermissionHelper";
    public static final int REQ_CODE_MIN = 1001;
    public static final int REQ_CODE_NOTIFICATION = REQ_CODE_MIN;
    public static final int REQ_CODE_OVERLAY = REQ_CODE_MIN + 1;
    public static final int REQ_CODE_BATTERY = REQ_CODE_MIN + 2;
    public static final int REQ_CODE_ACCESSIBILITY = REQ_CODE_MIN + 3;
    public static final int REQ_CODE_MAX = REQ_CODE_MIN + 4;
    private final Activity mActivity;
    private int mRequestCode = REQ_CODE_MIN;
    public PermissionHelper(Activity activity) {
        this.mActivity = activity;
    }

    public void requestPermissions() {
        for (int i = mRequestCode; i < REQ_CODE_MAX; i++, mRequestCode++) {
            boolean hasPermission = true;
            switch (mRequestCode) {
                case REQ_CODE_NOTIFICATION:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!hasNotificationPermission()) {
                            hasPermission = false;
                            requestNotificationPermission();
                        }
                    }
                    break;
                case REQ_CODE_OVERLAY:
                    if (!hasOverlayPermission()) {
                        hasPermission = false;
                        requestOverlayPermission();
                    }
                    break;
                case REQ_CODE_BATTERY:
                    if (!hasBatteryOptimization()) {
                        hasPermission = false;
                        requestIgnoreBatteryOptimization();
                    }
                    break;
                case REQ_CODE_ACCESSIBILITY:
                    if (!isAccessibilityEnabled()) {
                        hasPermission = false;
                        requestAccessibilityPermission();
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
    private void requestNext() {
        mRequestCode++;
        requestPermissions();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean hasNotificationPermission() {
        return mActivity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestNotificationPermission() {
        if (mActivity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, mActivity.getPackageName());
            dialog("通知", intent, REQ_CODE_NOTIFICATION);
        } else {
            mActivity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQ_CODE_NOTIFICATION);
        }
    }

    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(mActivity);
    }

    public void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
        dialog("悬浮窗", intent, REQ_CODE_OVERLAY);
    }

    private boolean hasBatteryOptimization() {
        final PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
        return pm.isIgnoringBatteryOptimizations(mActivity.getPackageName());
    }

    public void requestIgnoreBatteryOptimization() {
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
        dialog("忽略电池优化", intent, REQ_CODE_BATTERY);
    }

    private boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) mActivity.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getId().contains(mActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void requestAccessibilityPermission()  {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        dialog("无障碍服务", intent, REQ_CODE_ACCESSIBILITY);
    }

    private void dialog(String message, Intent intent, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(String.format("请授权%s权限", message));
        builder.setMessage(String.format("熊孩子应用需要%s权限才能正常运行", message));
        builder.setPositiveButton("确定", (dialog, which) -> {
            mActivity.startActivityForResult(intent, requestCode);
        });
        builder.setNegativeButton("取消", (dialog, which) -> {
            Toast.makeText(mActivity, String.format("请授予%s权限", message), Toast.LENGTH_LONG).show();
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
        requestNext();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + " permissions:" + Arrays.toString(permissions) + " grantResults:" + Arrays.toString(grantResults));
        requestNext();
    }
}
