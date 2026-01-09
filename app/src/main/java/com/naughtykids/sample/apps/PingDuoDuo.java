package com.naughtykids.sample.apps;

class PingDuoDuo extends DisableRunningApp {
    private static final String TAG = "PingDuoDuo";
    public static final String AppName = "拼多多";
    public static final String PackageName = "com.xunmeng.pinduoduo";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}