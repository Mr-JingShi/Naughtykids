package com.naughtykids.sample.apps;

class DouyinMall extends Douyin {
    private static final String TAG = "DouyinMall";
    public static final String AppName = "抖音商城";
    public static final String PackageName = "com.ss.android.ugc.livelite";
    @Override
    public String getPackageName() {
        return PackageName;
    }
}