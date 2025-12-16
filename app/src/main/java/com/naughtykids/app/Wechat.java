package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

class Wechat {
    private static final String TAG = "Wechat";
    private static final String[] KEY_WORDS = {
            "塞钱进红包",
            "转账金额"
    };
    static boolean onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo) {
        if (rootNodeInfo != null && rootNodeInfo.isVisibleToUser()) {
            for (String keyWord : KEY_WORDS) {
                List<AccessibilityNodeInfo> nodeInfos  = rootNodeInfo.findAccessibilityNodeInfosByText(keyWord);
                if (nodeInfos == null) {
                    continue;
                }
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    Log.v(TAG, "找到 keyWord:" + keyWord + " " + nodeInfo);
                    return true;
                }
            }
        }
        return false;
    }
}