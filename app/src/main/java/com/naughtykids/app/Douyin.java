package com.naughtykids.app;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

class Douyin extends ThirdPartyApp {
    private static final String TAG = "Douyin";
    public static final String PackageName = "com.ss.android.ugc.aweme";
    private static final String MainActivity = "com.ss.android.ugc.aweme.main.MainActivity";
    private static final String LivePlayActivity = "com.ss.android.ugc.aweme.live.LivePlayActivity";
    private static final String AudiencePortToolbarMoreDialog = "com.bytedance.android.livesdk.chatroom.viewmodule.toolbar.AudiencePortToolbarMoreDialog";
    private static final String LiveStandardSheetDialog = "com.bytedance.android.livesdk.widget.LiveStandardSheetDialog";
    private static final String AndroidAppDialog = "android.app.Dialog";
    private static final String AndroidWidgetImageView = "android.widget.ImageView";
    private static final String AndroidxViewPager = "androidx.viewpager.widget.ViewPager";
    private static final String TalkSomething = "说点什么...";
    private static final String TalkSomethingAndJoin = "说点什么，参与聊话题...";
    private static final String Xiaoxinxin = "小心心";
    private static final String Liwu = "礼物";
    private final List<String> mLiveGift = new ArrayList<>();
    private final List<String> mChecksText = new ArrayList<>();
    private String mResId_XiaoXinXin;
    private String mResId_Liwu;
    private boolean mInnerLivePlayActivity = false;

    Douyin() {
        String versionName = Utils.getAppVersion(PackageName);
        Log.d(TAG, "versionName:" + versionName);
        String versionNameInPreferences = PrivatePreferences.getDouyinVersion();
        if (versionNameInPreferences.equals(versionName)) {
            mResId_XiaoXinXin = PrivatePreferences.getDouyinLiveGift_Xiaoxinxin();
            mResId_Liwu = PrivatePreferences.getDouyinLiveGift_Liwu();
        } else {
            PrivatePreferences.setDouyinVersion(versionName);
        }

        mLiveGift.add(Xiaoxinxin);
        mLiveGift.add(Liwu);

        mChecksText.add("立即支付"); // 橱窗->商品详情
        mChecksText.add("立即支付");
        mChecksText.add("提交订单"); // 团购->商品详情
        mChecksText.add("输入支付密码"); // 输入秘密
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
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                onViewClicked(rootNodeInfo, event);
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                onViewScrolled(rootNodeInfo, event);
            default:
                break;
        }
    }

    private void onWindowContentChanged(AccessibilityNodeInfo rootNodeInfo) {
        for (String text : mChecksText) {
            if (findNodeByValue(rootNodeInfo, text)) {
                OverlayWindowManager.getInstance().show();
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
        Log.d(TAG, "onWindowStateChanged className:" + className);
        if (className.equals(LiveStandardSheetDialog)) {
            OverlayWindowManager.getInstance().show();
        } else if (className.equals(LivePlayActivity)) {
            mInnerLivePlayActivity = true;
            if (!TextUtils.isEmpty(mResId_XiaoXinXin) && !TextUtils.isEmpty(mResId_Liwu)) {
                overlayNodebyKey(rootNodeInfo, mResId_XiaoXinXin);
                overlayNodebyKey(rootNodeInfo, mResId_Liwu);
            } else {
                OverlayWindowManager.getInstance().show();
                List<AccessibilityNodeInfo> nodeInfos = new ArrayList<>();
                if (findNodeByContentDescription(rootNodeInfo, mLiveGift, nodeInfos)) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                        Log.d(TAG, "onWindowStateChanged nodeInfo:" + nodeInfo);
                        Rect rect = new Rect();
                        nodeInfo.getBoundsInScreen(rect);
                        OverlayWindowManager.getInstance().smallShow(rect);
                    }
                }
                OverlayWindowManager.getInstance().hide();
            }
        } else if (className.equals(MainActivity)) {
            OverlayWindowManager.getInstance().smallHide();
        } else if (className.equals(AudiencePortToolbarMoreDialog)) {
            OverlayWindowManager.getInstance().smallHide();
        } else if (className.equals(AndroidAppDialog)) {
            for (CharSequence text : event.getText()) {
                Log.d(TAG, "onWindowStateChanged text:" + text);
                // 直播间点击发生表情
                if (text != null && (text.equals(TalkSomething) || text.equals(TalkSomethingAndJoin))) {
                    OverlayWindowManager.getInstance().smallHide();
                    break;
                }
            }
        }
    }

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            Log.v(TAG, "source == null");
            return;
        }
        String viewId = source.getViewIdResourceName();
        if (viewId != null) {
            Log.d("Click", "View ID: " + viewId);
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

    boolean findNodeByContentDescription(AccessibilityNodeInfo rootNode, List<String> descriptions, List<AccessibilityNodeInfo> nodeInfos) {
        if (rootNode == null) return false;

        boolean descriptionEquals = false;
        CharSequence contentDescription = rootNode.getContentDescription();
        if (contentDescription != null && contentDescription.length() > 0) {
            for (String description : descriptions) {
                if (contentDescription.equals(description)) {
                    String viewIdResourceName = rootNode.getViewIdResourceName();
                    CharSequence className = rootNode.getClassName();
                    if (viewIdResourceName != null
                        && !viewIdResourceName.isEmpty()
                        && className != null
                        && className.equals(AndroidWidgetImageView)) {
                        nodeInfos.add(rootNode);
                        if (contentDescription.equals(Xiaoxinxin)) {
                            mResId_XiaoXinXin = viewIdResourceName;
                            PrivatePreferences.setDouyinLiveGift_Xiaoxinxin(viewIdResourceName);
                        } else if (contentDescription.equals(Liwu)) {
                            mResId_Liwu = viewIdResourceName;
                            PrivatePreferences.setDouyinLiveGift_Liwu(viewIdResourceName);
                        }
                        if (nodeInfos.size() == descriptions.size()) {
                            return true;
                        }
                    }

                    descriptionEquals = true;
                    break;
                }
            }
        }

        int childCount = rootNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo sonNode = rootNode.getChild(i);
            if (descriptionEquals) {
                String viewIdResourceName = sonNode.getViewIdResourceName();
                CharSequence className = sonNode.getClassName();
                if (viewIdResourceName != null
                    && !viewIdResourceName.isEmpty()
                    && className != null
                    && className.equals(AndroidWidgetImageView)) {
                    Rect rootRect = new Rect();
                    rootNode.getBoundsInScreen(rootRect);
                    Rect sonRect = new Rect();
                    sonNode.getBoundsInScreen(sonRect);
                    if (rootRect.equals(sonRect)) {
                        nodeInfos.add(sonNode);

                        if (contentDescription.equals(Xiaoxinxin)) {
                            mResId_XiaoXinXin = viewIdResourceName;
                            PrivatePreferences.setDouyinLiveGift_Xiaoxinxin(viewIdResourceName);
                        } else if (contentDescription.equals(Liwu)) {
                            mResId_Liwu = viewIdResourceName;
                            PrivatePreferences.setDouyinLiveGift_Liwu(viewIdResourceName);
                        }

                        if (nodeInfos.size() == descriptions.size()) {
                            return true;
                        }
                    }
                }
            }
            if (findNodeByContentDescription(sonNode, descriptions, nodeInfos)) {
                return true;
            }
        }
        return false;
    }

    static boolean overlayNodebyKey(AccessibilityNodeInfo rootNodeInfo, String key) {
        boolean result = false;
        List<AccessibilityNodeInfo> nodeInfos = rootNodeInfo.findAccessibilityNodeInfosByViewId(key);
        if (nodeInfos != null) {
            for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                Log.d(TAG, "onWindowStateChanged nodeInfo:" + nodeInfo);
                Rect rect = new Rect();
                nodeInfo.getBoundsInScreen(rect);
                OverlayWindowManager.getInstance().smallShow(rect);
                result = true;
            }
        }
        return result;
    }

    private void onViewScrolled(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        if (mInnerLivePlayActivity) {
            if (!TextUtils.isEmpty(mResId_XiaoXinXin) && !TextUtils.isEmpty(mResId_Liwu)) {
                OverlayWindowManager.getInstance().smallHide();
                overlayNodebyKey(rootNodeInfo, mResId_XiaoXinXin);
                overlayNodebyKey(rootNodeInfo, mResId_Liwu);
            }
        }
    }
}