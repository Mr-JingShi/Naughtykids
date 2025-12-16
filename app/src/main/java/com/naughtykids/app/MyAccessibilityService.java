package com.naughtykids.app;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";

    private OverlayWIndow mOverlayWIndow;

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

        mOverlayWIndow = new OverlayWIndow();
        mOverlayWIndow.init(this);
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
        Log.v(TAG, "收到辅助功能事件:" + event);
        if (mOverlayWIndow.isShowing()) {
            Log.v(TAG, "OverlayWIndow is showing");
            return;
        }
        AccessibilityNodeInfo rootNodeInfo = this.getRootInActiveWindow();
        if (rootNodeInfo == null) {
            Log.v(TAG, "onAccessibilityEvent rootNodeInfo == null");
            return;
        }
        CharSequence packageName = event.getPackageName();
        if (packageName == null) {
            packageName = rootNodeInfo.getPackageName();
        }
        if (packageName.equals("com.naughtykids.app")) {
            Log.v(TAG, "onAccessibilityEvent packageName:" + packageName);
            return;
        }
        if (event.getEventType() != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.v(TAG, "onAccessibilityEvent is not TYPE_WINDOW_CONTENT_CHANGED");
            return;
        }
        if (packageName.equals("com.ss.android.ugc.aweme")) {
            if (Douyin.onAccessibilityEvent(rootNodeInfo)) {
                mOverlayWIndow.show();
            }
        } else if (packageName.equals("com.tencent.mm")) {
            if (Wechat.onAccessibilityEvent(rootNodeInfo)) {
                mOverlayWIndow.show();
            }
        }
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
