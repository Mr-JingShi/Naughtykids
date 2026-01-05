package com.naughtykids.sample.apps;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.naughtykids.sample.Utils;

class TaoBaoLive extends TaoBao {
    private static final String TAG = "TaoBaoLive";
    public static final String AppName = "点淘";
    public static final String PackageName = "com.taobao.live";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}