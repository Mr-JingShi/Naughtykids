package com.naughtykids.app;

class KwaiNebula extends Kwai {
    private static final String TAG = "Kwai";
    public static final String AppName = "快手极速版";
    public static final String PackageName = "com.kuaishou.nebula";
    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public String getResIdPrefix() {
        return "com.kuaishou.nebula.live_audience_plugin";
    }
}