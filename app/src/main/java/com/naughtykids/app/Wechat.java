package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

class Wechat extends ThirdPartyApp {
    private static final String TAG = "Wechat";
    public static final String PackageName = "com.tencent.mm";
    private static final String LuckyMoney = "红包";
    // 红包
    private static final String LuckyMoneyNewPrepareUI = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNewPrepareUI";
    private static final String Gift = "礼物";
    // 礼物，一般情况下，找不到AppBrandPluginUI，而是找到com.tencent.mm.ui.chatting.ChattingUI
    // private static final String AppBrandPluginUI = "com.tencent.mm.plugin.appbrand.ui.AppBrandPluginUI";
    private static final String Transfer = "转账";
    private static final String RemittanceUI = "com.tencent.mm.plugin.remittance.ui.RemittanceUI";
    private static final String LiveUI = "com.tencent.mm.plugin.finder.feed.ui.FinderLiveVisitorWithoutAffinityUI";
    Wechat() {
        checkVersion();
    }

    @Override
    public void checkVersion() {
        String packageName = getPackageName();
        String versionName = Utils.getAppVersion(packageName);
        Log.d(TAG, "versionName:" + versionName);
    }

    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(rootNodeInfo, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(rootNodeInfo, event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                // onWindowContentChanged(rootNodeInfo, event);
                break;
            default:
                break;
        }
    }

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onViewClicked className:" + className);
        if (className.equals(AndroidWidgetLinearLayout)) {
            List<CharSequence> texts = event.getText();
            if (texts.contains(LuckyMoney) || texts.contains(Gift)) {
                OverlayWindowManager.getInstance().show();
            } else if (texts.contains(Transfer)) {
                OverlayWindowManager.getInstance().show();
                OverlayWindowManager.getInstance().setClickBackCount(2);
            }
        }
    }
    private void onWindowStateChanged(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onWindowStateChanged className:" + className);
        if (className.equals(LuckyMoneyNewPrepareUI)/* || className.equals(AppBrandPluginUI)*/) {
            OverlayWindowManager.getInstance().show();
        } else if (className.equals(RemittanceUI)) {
            OverlayWindowManager.getInstance().show();
            OverlayWindowManager.getInstance().setClickBackCount(2);
        } else if (className.equals(LiveUI)) {
           UiDumper.dumpNodeTree(rootNodeInfo);
        } else {

        }
    }
    private void onWindowContentChanged(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onWindowContentChanged className:" + className);
    }
}