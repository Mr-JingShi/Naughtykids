package com.naughtykids.sample.apps;

class Tmall extends DisableRunningApp {
    private static final String TAG = "Tmall";
    public static final String AppName = "天猫";
    public static final String PackageName = "com.tmall.wireless";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}