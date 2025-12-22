package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

class Wechat extends ThirdPartyApp {
    private static final String TAG = "Wechat";
    public static final String PackageName = "com.tencent.mm";
    private static final String[] KEY_WORDS = {
            "塞钱进红包",
            "转账金额"
    };

    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        if (rootNodeInfo != null && rootNodeInfo.isVisibleToUser()) {
            for (String keyWord : KEY_WORDS) {
                List<AccessibilityNodeInfo> nodeInfos  = rootNodeInfo.findAccessibilityNodeInfosByText(keyWord);
                if (nodeInfos == null) {
                    continue;
                }
                for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                    Log.v(TAG, "找到 keyWord:" + keyWord + " " + nodeInfo);
                    return;
                }
            }
        }
    }
}