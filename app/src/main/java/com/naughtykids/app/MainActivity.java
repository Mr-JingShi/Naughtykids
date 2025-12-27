package com.naughtykids.app;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static String TAG = "MainActivity";
    private static int REQUEST_CODE_OVERLAY_PERMISSION = 1001;
    private static int REQUEST_CODE_NOTIFICATION_PERMISSION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate");
        super.onCreate(savedInstanceState);

        processNotificationsPermission();
        processFloatWindow();
        processPowerSavePermission();
    }

    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    private void processFloatWindow() {
        if (!hasOverlayPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (!hasOverlayPermission()) {
                Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_LONG).show();
            }
        }  else if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            Log.i(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
            if (hasNotificationPermission()) {
                Toast.makeText(this, "Notification permission was granted.", Toast.LENGTH_LONG).show();
                processAccessibilityService();
            } else {
                Toast.makeText(this, "Notification permission request was denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "MainActivity onDestroy");
    }

    /**
     * 处理省电优化权限
     * @return
     */
    private void processPowerSavePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("低电量优化策略");
                builder.setMessage("请允许熊孩子应用忽略低电量优化策略，以确保熊孩子应用能在低电量时正常运行");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("开启", (DialogInterface dialog, int which) -> {
                    Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                });
                builder.show();
            }
        }
    }

    private void processAccessibilityService()  {
        if (!isAccessibilityEnabled(MyAccessibilityService.class)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("需要无障碍权限才能使用该功能");
            builder.setPositiveButton("确定", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            });
            builder.setNegativeButton("取消", (dialog, which) -> {
                Toast.makeText(this, "请授予无障碍权限", Toast.LENGTH_LONG).show();
            });
            builder.show();
        }
    }

    /**
     * 判断辅助功能是否开启
     * @param serviceClass
     * @return
     */
    private boolean isAccessibilityEnabled(Class<? extends AccessibilityService> serviceClass) {
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        if (enabledServices == null || enabledServices.isEmpty()) {
            return false;
        }

        String targetServiceId = new ComponentName(this, serviceClass).flattenToString();
        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getId().equals(targetServiceId)) {
                return true;
            }
        }
        return false;
    }

    public void processNotificationsPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("需要通知权限才能使用该功能");
            builder.setPositiveButton("确定", (dialog, which) -> {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, REQUEST_CODE_NOTIFICATION_PERMISSION);
            });
            builder.setNegativeButton("取消", (dialog, which) -> {
                Toast.makeText(this, "请授予通知权限", Toast.LENGTH_LONG).show();
            });
            builder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult requestCode:" + requestCode + " permissions:" + Arrays.toString(permissions) + " grantResults:" + Arrays.toString(grantResults));
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission was granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Notification permission request was denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean hasNotificationPermission() {
        // https://developer.android.com/develop/ui/views/notifications/notification-permission?hl=zh-cn
        // Android 13（API 级别 33）及更高版本支持用于从应用发送非豁免（包括前台服务 [FGS]）通知的运行时权限：POST_NOTIFICATIONS。此更改有助于用户专注于最重要的通知。
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }
}
