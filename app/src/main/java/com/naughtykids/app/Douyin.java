package com.naughtykids.app;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Douyin extends ThirdPartyApp {
    private static final String TAG = "Douyin";
    public static final String PackageName = "com.ss.android.ugc.aweme";
    private static final String MainActivity = "com.ss.android.ugc.aweme.main.MainActivity";
    private static final String LivePlayActivity = "com.ss.android.ugc.aweme.live.LivePlayActivity";
    private static final String AudiencePortToolbarMoreDialog = "com.bytedance.android.livesdk.chatroom.viewmodule.toolbar.AudiencePortToolbarMoreDialog";
    private static final String LiveStandardSheetDialog = "com.bytedance.android.livesdk.widget.LiveStandardSheetDialog";
    private static final String[] LiveGift = {
            "com.ss.android.ugc.aweme:id/ava",
            "com.ss.android.ugc.aweme:id/h-="
    };

    private static final Map<String, String> mChecksText = new HashMap<>();

    static {
        mChecksText.put("com.ss.android.ugc.aweme:id/guv", "立即支付"); // 橱窗->商品详情
        mChecksText.put("com.ss.android.ugc.aweme:id/f7y", "立即支付");
        mChecksText.put("com.ss.android.ugc.aweme:id/aww", "提交订单"); // 团购->商品详情
        mChecksText.put("com.ss.android.ugc.aweme.cjplugin:id/qx", "输入支付密码"); // 输入秘密
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                onWindowContentChanged(rootNodeInfo);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(rootNodeInfo, event);
                break;
            default:
                break;
        }
    }

    private void onWindowContentChanged(AccessibilityNodeInfo rootNodeInfo) {
        for (String key : mChecksText.keySet()) {
            if (findNode(rootNodeInfo, key, mChecksText.get(key))) {
                //OverlayWindowManager.getInstance().show();
                break;
            }
        }
    }
    private void onWindowStateChanged(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }

        if (className.equals(LiveStandardSheetDialog)) {
            OverlayWindowManager.getInstance().show();
        } else if (className.equals(LivePlayActivity)) {
            for (String gift : LiveGift) {
                List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(gift);
                if (nodeInfos != null && !nodeInfos.isEmpty()) {
                    Rect rect = new Rect();
                    nodeInfos.get(0).getBoundsInScreen(rect);
                    OverlayWindowManager.getInstance().smallShow(rect);
                }
            }
        } else if (className.equals(MainActivity)) {
            OverlayWindowManager.getInstance().smallHide();
        } else if (className.equals(AudiencePortToolbarMoreDialog)) {
            OverlayWindowManager.getInstance().smallHide();
        }
    }

    private boolean findNode(AccessibilityNodeInfo rootNodeInfo, String key, String value) {
        boolean keyEmpty = key == null || key.isEmpty();
        boolean valueEmpty = value == null || value.isEmpty();
        if (keyEmpty || valueEmpty) {
            if (valueEmpty) {
                return findNodeByValue(rootNodeInfo, key);
            } else {
                return findNodeByKey(rootNodeInfo, value);
            }
        }
        return findNodeByKeyAndValue(rootNodeInfo, key, value);
    }

    private static boolean findNodeByKeyAndValue(AccessibilityNodeInfo rootNodeInfo, String key, String value) {
        Log.v(TAG, "findNodeByKeyAndValue key:" + key + " value:" + value);
        List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(key);
        if (nodeInfos == null) {
            Log.v(TAG, "nodeInfos == null");
            return false;
        }
        Log.v(TAG, "找到 key:" + key);
        for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
            List<AccessibilityNodeInfo> sonNodeInfos = nodeInfo.findAccessibilityNodeInfosByText(value);
            if (sonNodeInfos == null) {
                Log.v(TAG, "sonNodeInfos == null");
                return false;
            }
            for (AccessibilityNodeInfo sonNodeInfo : sonNodeInfos) {
                if (sonNodeInfo.isVisibleToUser()/* && sonNodeInfo.isClickable()*/) {
                    Log.v(TAG, "找到 value:" + value);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findNodeByKey(AccessibilityNodeInfo rootNodeInfo, String key) {
        List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(key);
        if (nodeInfos == null) {
            Log.v(TAG, "nodeInfos == null");
            return false;
        }
        return !nodeInfos.isEmpty();
    }
    private static boolean findNodeByValue(AccessibilityNodeInfo rootNodeInfo, String value) {
        List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByText(value);
        if (nodeInfos == null) {
            Log.v(TAG, "sonNodeInfos == null");
            return false;
        }
        return !nodeInfos.isEmpty();
    }
}