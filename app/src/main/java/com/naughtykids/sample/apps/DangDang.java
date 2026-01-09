package com.naughtykids.sample.apps;

class DangDang extends DisableRunningApp {
    private static final String TAG = "DangDang";
    public static final String AppName = "当当";
    public static final String PackageName = "com.dangdang.buy2";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}