package com.naughtykids.app;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

abstract class ThirdPartyApp {
    public static final String TAG = "ThirdPartyApp";

    abstract void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event);

    static void traverseAllNodes(AccessibilityNodeInfo node) {
        if (node == null) return;

        Log.d(TAG, "traverseAllNodes node:" + node);

        // 遍历所有子节点
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                traverseAllNodes(child); // 递归
                child.recycle(); // 回收
            }
        }
    }

    static boolean hasApplicationWindow() {
        List<AccessibilityWindowInfo> windowInfos = Utils.getA11y().getWindows();
        for (int i = 0; i < windowInfos.size(); i++) {
            AccessibilityWindowInfo windowInfo = windowInfos.get(i);
            if (windowInfo.getType() != AccessibilityWindowInfo.TYPE_SYSTEM) {
                AccessibilityNodeInfo nodeInfo = windowInfo.getRoot();
                CharSequence packageName = nodeInfo.getPackageName();
                if (!packageName.equals(Utils.getDesktopAppPackageName())
                    && !packageName.equals(Utils.getSelfAppPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
}