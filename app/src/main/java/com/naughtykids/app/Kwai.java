package com.naughtykids.app;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

class Kwai extends ThirdPartyApp {
    private static final String TAG = "Kwai";
    public static final String AppName = "快手";
    public static final String PackageName = "com.smile.gifmaker";
    private final List<String> mLiveGift = new ArrayList<>();
    private boolean mInnerLiveActivity = false;

    Kwai() {
        mLiveGift.add(getResIdPrefix() + ":id/live_bottom_bar_guide_gift_view");
        mLiveGift.add(getResIdPrefix() + ":id/live_gift");
        mLiveGift.add(getResIdPrefix() + ":id/live_audience_bottom_bar_fans_group_entrance_icon");
    }

    @Override
    public String getPackageName() {
        return PackageName;
    }

    public String getLiveActivity() {
        return "com.kuaishou.live.core.basic.activity.LiveSlideActivityTablet";
    }

    public String getHomeActivity() {
        return "com.yxcorp.gifshow.HomeActivity";
    }

    public String getResIdPrefix() {
        return PackageName;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onWindowStateChanged(rootNodeInfo, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(rootNodeInfo, event);
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
        Log.d(TAG, "onWindowStateChanged className:" + className + " LiveActivity:" + getLiveActivity());
        if (className.equals(getHomeActivity())) {
            mInnerLiveActivity = false;
            OverlayWindowManager.getInstance().smallHide();
        } else if (className.equals(getLiveActivity())) {
            mInnerLiveActivity = true;
            for (String gift : mLiveGift) {
                Log.v(TAG, "gift:" + gift);
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
                    mInnerLiveActivity = true;
                    onViewScrolled(rootNodeInfo, event);
                }
            }
        }
    }

    private void onViewScrolled(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        if (mInnerLiveActivity) {
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

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onViewClicked className:" + className);
    }
}