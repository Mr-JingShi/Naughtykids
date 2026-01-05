package com.naughtykids.sample.apps;

class DouyinLive extends Douyin {
    private static final String TAG = "DouyinLive";
    public static final String AppName = "抖音火山版";
    public static final String PackageName = "com.ss.android.ugc.live";
    @Override
    public String getPackageName() {
        return PackageName;
    }
}