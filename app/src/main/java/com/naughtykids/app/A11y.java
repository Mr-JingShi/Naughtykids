package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class A11y extends AccessibilityService {
    private static final String TAG = "A11y";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service on create");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.d(TAG, "Service on start");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service on unbind");

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "Service on rebind");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Service on task removed");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "Service connected");
        super.onServiceConnected();

        Intent fgIntent = new Intent(this, KeepAliveService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(fgIntent);
        } else {
            startService(fgIntent);
        }

        Utils.setA11y(this);
        OverlayWindowManager.getInstance().init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service on start command");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.v(TAG, "收到键盘事件:" + event);
        return false;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.v(TAG, "收到辅助功能事件:" + event);
        }
        if (OverlayWindowManager.getInstance().isShowing()) {
            Log.v(TAG, "OverlayWIndow is showing");
            return;
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            if (!ThirdPartyApp.hasApplicationWindow()) {
                OverlayWindowManager.getInstance().smallHide();
            }
            return;
        }

        AccessibilityNodeInfo rootNodeInfo = this.getRootInActiveWindow();
        if (rootNodeInfo == null) {
            Log.v(TAG, "onAccessibilityEvent rootNodeInfo == null");
            return;
        }
        CharSequence packageName = event.getPackageName();
        if (packageName == null) {
            Log.v(TAG, "onAccessibilityEvent packageName == null");
            return;
        }
        if (packageName.equals(Utils.getSelfAppPackageName())) {
            Log.v(TAG, "self event");
            return;
        }

        ThirdPartyApp thirdPartyApp = AppFactory.getInstance().getApp(packageName);
        if (thirdPartyApp != null) {
            thirdPartyApp.onAccessibilityEvent(rootNodeInfo, event);
        }

        rootNodeInfo.recycle();
    }

    @Override
    protected boolean onGesture(int gestureId) {
        return super.onGesture(gestureId);
    }
    @Override
    public void onInterrupt() {
        Log.e(TAG, "服务被Interrupt");
    }
}
