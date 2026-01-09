package com.naughtykids.sample.apps;

class TaoBao extends DisableRunningApp {
    private static final String TAG = "TaoBao";
    public static final String AppName = "淘宝";
    public static final String PackageName = "com.taobao.taobao";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}