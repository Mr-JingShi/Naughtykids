package com.naughtykids.sample.apps;

class JingDong extends DisableRunningApp {
    private static final String TAG = "JingDong";
    public static final String AppName = "京东";
    public static final String PackageName = "com.jingdong.app.mall";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}