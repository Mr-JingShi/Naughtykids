package com.naughtykids.sample.apps;

import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;
import com.naughtykids.sample.Utils;

import java.util.ArrayList;
import java.util.List;

class Kwai extends ThirdPartyApp {
    private static final String TAG = "Kwai";
    public static final String AppName = "快手";
    public static final String PackageName = "com.smile.gifmaker";
    private final List<String> mLiveGift = new ArrayList<>();
    private final List<String> mLiveActivities = new ArrayList<>();
    private boolean mInnerLiveActivity = false;
    Kwai() {
        checkVersion();

        mLiveGift.add(getResIdPrefix() + ":id/live_quick_handle_kds_bar_outline_container"); // 直播-冲冲喜欢你
        mLiveGift.add(getResIdPrefix() + ":id/live_right_bottom_pendant_container"); // 直播-双旦活动
        mLiveGift.add(getResIdPrefix() + ":id/live_bottom_bar_guide_gift_view");
        mLiveGift.add(getResIdPrefix() + ":id/live_gift"); // 直播-礼物
        mLiveGift.add(getResIdPrefix() + ":id/live_gift_battle_bottom_bar_view"); // 直播-打比赛-送出小可爱
        mLiveGift.add(getResIdPrefix() + ":id/live_audience_bottom_bar_fans_group_entrance_icon");

        mLiveActivities.add("com.kuaishou.live.core.basic.activity.LiveSlideActivity");
        mLiveActivities.add("com.kuaishou.live.core.basic.activity.LiveSlideActivityTablet");
        mLiveActivities.add("com.yxcorp.gifshow.detail.PhotoDetailActivity");
        mLiveActivities.add("com.yxcorp.gifshow.detail.PhotoDetailActivityTablet");
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

    public List<String> getLiveActivity() {
        return mLiveActivities;
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
            OverlayManager.getInstance().smallHide();
        } else if (getLiveActivity().contains(className.toString())) {
            mInnerLiveActivity = true;
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
                    OverlayManager.getInstance().smallHide(gift);
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
                OverlayManager.getInstance().smallShow(key, rect);
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