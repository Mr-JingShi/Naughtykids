package com.naughtykids.sample.apps;

import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.OverlayManager;
import com.naughtykids.sample.PrivatePreferences;
import com.naughtykids.sample.Utils;

import java.util.ArrayList;
import java.util.List;

class Douyin extends ThirdPartyApp {
    private static final String TAG = "Douyin";
    public static final String AppName = "抖音";
    public static final String PackageName = "com.ss.android.ugc.aweme";
    private static final String MainActivity = "com.ss.android.ugc.aweme.main.MainActivity";
    private static final String LivePlayActivity = "com.ss.android.ugc.aweme.live.LivePlayActivity";
    private static final String AudiencePortToolbarMoreDialog = "com.bytedance.android.livesdk.chatroom.viewmodule.toolbar.AudiencePortToolbarMoreDialog";
    private static final String LiveStandardSheetDialog = "com.bytedance.android.livesdk.widget.LiveStandardSheetDialog";
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
        checkVersion();

        mLiveGift.add(Xiaoxinxin);
        mLiveGift.add(Liwu);

        mChecksText.add("立即支付"); // 橱窗->商品详情
        mChecksText.add("立即支付");
        mChecksText.add("提交订单"); // 团购->商品详情
        mChecksText.add("输入支付密码"); // 输入秘密
    }

    @Override
    public void checkVersion() {
        String packageName = getPackageName();
        String versionName = Utils.getAppVersion(packageName);
        String versionNameInPreferences = PrivatePreferences.getString(packageName, "");
        Log.d(TAG, "versionName:" + versionName + " versionNameInPreferences:" + versionNameInPreferences);
        if (versionNameInPreferences.equals(versionName)) {
            mResId_XiaoXinXin = PrivatePreferences.getString(getLiveGiftXiaoxinxin(), "");
            mResId_Liwu = PrivatePreferences.getString(getLiveGiftLiwu(), "");
        } else {
            PrivatePreferences.putString(packageName, versionName);
            mResId_XiaoXinXin = null;
            mResId_Liwu = null;
        }
    }

    @Override
    public String getPackageName() {
        return PackageName;
    }

    public String getLivePlayActivity() {
        return LivePlayActivity;
    }

    public String getLiveGiftXiaoxinxin() {
        return getPackageName() + "." + Xiaoxinxin;
    }
    public String getLiveGiftLiwu() {
        return getPackageName() + "." + Liwu;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        switch (event.getEventType()) {
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
        Log.d(TAG, "onWindowStateChanged className:" + className);
        if (className.equals(LiveStandardSheetDialog)) {
            OverlayManager.getInstance().show();
        } else if (className.equals(getLivePlayActivity())) {
            mInnerLivePlayActivity = true;
            Log.d(TAG, "onWindowStateChanged mResId_XiaoXinXin:" + mResId_XiaoXinXin + " mResId_Liwu:" + mResId_Liwu);
            if (!TextUtils.isEmpty(mResId_XiaoXinXin) && !TextUtils.isEmpty(mResId_Liwu)) {
                overlayNodebyKey(rootNodeInfo, mResId_XiaoXinXin);
                overlayNodebyKey(rootNodeInfo, mResId_Liwu);
            } else {
                OverlayManager.getInstance().show();
                List<AccessibilityNodeInfo> nodeInfos = new ArrayList<>();
                if (findNodeByContentDescription(rootNodeInfo, mLiveGift, nodeInfos)) {
                    for (AccessibilityNodeInfo nodeInfo : nodeInfos) {
                        Log.d(TAG, "onWindowStateChanged nodeInfo:" + nodeInfo);
                        Rect rect = new Rect();
                        nodeInfo.getBoundsInScreen(rect);
                        OverlayManager.getInstance().smallShow(nodeInfo.getViewIdResourceName(), rect);
                    }
                }
                OverlayManager.getInstance().hide();
            }
        } else if (className.equals(MainActivity)) {
            mInnerLivePlayActivity = false;
            OverlayManager.getInstance().smallHide();
        } else if (className.equals(AudiencePortToolbarMoreDialog)) {
            OverlayManager.getInstance().smallHide();
        } else if (className.equals(AndroidAppDialog)) {
            for (CharSequence text : event.getText()) {
                Log.d(TAG, "onWindowStateChanged text:" + text);
                // 直播间点击发生表情
                if (text != null && (text.equals(TalkSomething) || text.equals(TalkSomethingAndJoin))) {
                    OverlayManager.getInstance().smallHide();
                    break;
                }
            }
        }
    }

    private void onViewClicked(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        CharSequence className = event.getClassName();
        if (className == null) {
            Log.v(TAG, "className == null");
            return;
        }
        Log.d(TAG, "onViewClicked className:" + className);
        if (className.equals(AndroidWidgetTextView)) {
            List<CharSequence> texts = event.getText();
            if (texts.contains(TalkSomething) || texts.contains(TalkSomethingAndJoin)) {
                OverlayManager.getInstance().smallHide();
            }
        }
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
                            PrivatePreferences.putString(getLiveGiftXiaoxinxin(), viewIdResourceName);
                        } else if (contentDescription.equals(Liwu)) {
                            mResId_Liwu = viewIdResourceName;
                            PrivatePreferences.putString(getLiveGiftLiwu(), viewIdResourceName);
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
                            PrivatePreferences.putString(getLiveGiftXiaoxinxin(), viewIdResourceName);
                        } else if (contentDescription.equals(Liwu)) {
                            mResId_Liwu = viewIdResourceName;
                            PrivatePreferences.putString(getLiveGiftLiwu(), viewIdResourceName);
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
                OverlayManager.getInstance().smallShow(key, rect);
                result = true;
            }
        }
        return result;
    }

    private void onViewScrolled(AccessibilityNodeInfo rootNodeInfo, AccessibilityEvent event) {
        if (mInnerLivePlayActivity) {
            if (!TextUtils.isEmpty(mResId_XiaoXinXin) && !TextUtils.isEmpty(mResId_Liwu)) {
                boolean result = overlayNodebyKey(rootNodeInfo, mResId_XiaoXinXin);
                result |= overlayNodebyKey(rootNodeInfo, mResId_Liwu);
                if (!result) {
                    OverlayManager.getInstance().smallHide();
                }
            }
        }
    }
}