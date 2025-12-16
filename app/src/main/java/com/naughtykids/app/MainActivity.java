package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.List;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static String TAG = "MainActivity";
    private static int REQUEST_CODE_OVERLAY_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate");
        super.onCreate(savedInstanceState);

        processFloatWindow();
        processPowerSavePermission();
        processAccessibilityService();
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
            if (hasOverlayPermission()) {

            } else {
                Toast.makeText(this, "请授予悬浮窗权限", Toast.LENGTH_LONG).show();
            }
            finish();
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
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
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
}
