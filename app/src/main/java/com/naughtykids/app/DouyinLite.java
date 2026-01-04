package com.naughtykids.app;

class DouyinLite extends Douyin {
    private static final String TAG = "DouyinLite";
    public static final String AppName = "抖音极速版";
    public static final String PackageName = "com.ss.android.ugc.aweme.lite";
    @Override
    public String getPackageName() {
        return PackageName;
    }
}