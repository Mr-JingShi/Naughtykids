package com.naughtykids.app;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

class Kwai extends ThirdPartyApp {
    private static final String TAG = "Kwai";
    public static final String PackageName = "com.smile.gifmaker";
    private static final String LiveSlideActivityTablet = "com.kuaishou.live.core.basic.activity.LiveSlideActivityTablet";
    private final List<String> mLiveGift = new ArrayList<>();
    private boolean mInnerLiveSlideActivityTablet = false;

    Kwai() {
        mLiveGift.add("com.smile.gifmaker:id/live_bottom_bar_guide_gift_view");
        mLiveGift.add("com.smile.gifmaker:id/live_gift");
        mLiveGift.add("com.smile.gifmaker:id/live_audience_bottom_bar_fans_group_entrance_icon");
    }

    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(rootNodeInfo, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:

                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                onViewScrolled(rootNodeInfo, event);
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
        if (className.equals(LiveSlideActivityTablet)) {
            mInnerLiveSlideActivityTablet = true;
            for (String gift : mLiveGift) {
                List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(gift);
                if (nodeInfos != null) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                        CharSequence nodeClassName = nodeInfo.getClassName();
                        if (nodeClassName != null && nodeClassName.equals(AndroidWidgetImageView)) {
                            Log.v(TAG, "找到 gift:" + gift + " " + nodeInfo);
                            Rect rect = new Rect();
                            nodeInfo.getBoundsInScreen(rect);
                            OverlayWindowManager.getInstance().smallShow(gift, rect);
                        }
                    }
                }
            }
        } else if (className.equals(AndroidWidgetFrameLayout)) {
            List<CharSequence> texts = event.getText();
            for (CharSequence text : texts) {
                Log.d(TAG, "onWindowStateChanged text:" + text);
                if (texts.contains("直播")) {
                    Log.d(TAG, "onWindowStateChanged rootNodeInfo:" + rootNodeInfo);
                    mInnerLiveSlideActivityTablet = true;
                    onViewScrolled(rootNodeInfo, event);
                }
            }
        }
    }

    private void onViewScrolled(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        if (mInnerLiveSlideActivityTablet) {
            for (String gift : mLiveGift) {
                if (!overlayNodebyKey(rootNodeInfo, gift)) {
                    OverlayWindowManager.getInstance().smallHide(gift);
                }
            }
        }
    }

    static boolean overlayNodebyKey(AccessibilityNodeInfo rootNodeInfo, String key) {
        boolean result = false;
        List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(key);
        if (nodeInfos != null) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                OverlayWindowManager.getInstance().smallShow(key, rect);
                result = true;
            }
        }
        return result;
    }
}