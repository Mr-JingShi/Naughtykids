package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.Map;

class Douyin {
    private static final String TAG = "Douyin";
    private static Map<String, String> mChecks;
    static {
        mChecks = new java.util.HashMap<>();
        mChecks.put("com.ss.android.ugc.aweme:id/f7y", "立即支付");
        mChecks.put("com.ss.android.ugc.aweme:id/guv", "立即支付");
        mChecks.put("com.ss.android.ugc.aweme:id/aww", "提交订单");
        mChecks.put("com.ss.android.ugc.aweme.cjplugin:id/qx", "输入支付密码");
    }
    public static boolean onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo) {
        for (String key : mChecks.keySet()) {
            String value = mChecks.get(key);
            List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(key);
            if (nodeInfos == null) {
                Log.v(TAG, "onAccessibilityEvent nodeInfos == null");
                continue;
            }
            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                List<AccessibilityNodeInfo>  sonNodeInfos = nodeInfo.findAccessibilityNodeInfosByText(value);
                if (sonNodeInfos == null) {
                    Log.v(TAG, "onAccessibilityEvent sonNodeInfos == null");
                    continue;
                }
                for (AccessibilityNodeInfo sonNodeInfo : sonNodeInfos) {
                    Log.v(TAG, "onAccessibilityEvent sonNodeInfo:" + sonNodeInfo);
                    if (sonNodeInfo.isVisibleToUser()) {
                        Log.v(TAG, "找到 keyWord:" + value + " " + sonNodeInfo);
                        return true;
                    }
                }
            }
        }
        Log.v(TAG, "onAccessibilityEvent not found");
        return false;
    }
}