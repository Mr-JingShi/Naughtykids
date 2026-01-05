package com.naughtykids.sample.apps;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;

import java.util.ArrayList;
import java.util.List;

class Bilibili extends ThirdPartyApp {
    private static final String TAG = "Bilibili";
    public static final String AppName = "哔哩哔哩";
    public static final String PackageName = "tv.danmaku.bili";
    static final String LiveRoomActivityV3 = "com.bilibili.bililive.room.ui.roomv3.LiveRoomActivityV3";
    static final List<String> mLiveGift = new ArrayList<>();

    static {
        mLiveGift.add("tv.danmaku.bili:id/iv_gift_icon");
        mLiveGift.add("tv.danmaku.bili:id/live_send_gift");
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
            default:
                break;
        }
    }

    private void onWindowStateChanged(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onWindowStateChanged className:" + className);
        if (className.equals(LiveRoomActivityV3)) {
            for (String gift : mLiveGift) {
                Log.v(TAG, "gift:" + gift);
                List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(gift);
                if (nodeInfos != null) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                        CharSequence nodeClassName = nodeInfo.getClassName();
                        if (nodeClassName != null) {
                            Log.v(TAG, "找到 gift:" + gift + " " + nodeInfo);
                            Rect rect = new Rect();
                            nodeInfo.getBoundsInScreen(rect);
                            OverlayManager.getInstance().smallShow(gift, rect);
                        }
                    }
                }
            }
        }
    }
}