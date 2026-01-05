package com.naughtykids.sample.apps;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;
import com.naughtykids.sample.Utils;

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
    // 转账
    private static final String RemittanceUI = "com.tencent.mm.plugin.remittance.ui.RemittanceUI";
    // 直播间
    private static final String FinderLiveVisitorWithoutAffinityUI = "com.tencent.mm.plugin.finder.feed.ui.FinderLiveVisitorWithoutAffinityUI";
    private static final String Service = "服务";
    // 服务-收付款、钱包等页面
    private static final String MallIndexUIv2 = "com.tencent.mm.plugin.mall.ui.MallIndexUIv2";
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
                OverlayManager.getInstance().show();
            } else if (texts.contains(Transfer)) {
                OverlayManager.getInstance().show();
                OverlayManager.getInstance().setClickBackCount(2);
            } else if (texts.contains(Service)) {
                OverlayManager.getInstance().show();
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
            OverlayManager.getInstance().show();
        } else if (className.equals(RemittanceUI)) {
            OverlayManager.getInstance().show();
            OverlayManager.getInstance().setClickBackCount(2);
        } else if (className.equals(FinderLiveVisitorWithoutAffinityUI)) {
            OverlayManager.getInstance().clearBackgroundColor();
            OverlayManager.getInstance().show();
        } else if (className.equals(MallIndexUIv2)) {
            OverlayManager.getInstance().show();
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