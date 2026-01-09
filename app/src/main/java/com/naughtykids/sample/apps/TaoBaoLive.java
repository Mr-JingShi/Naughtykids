package com.naughtykids.sample.apps;

class TaoBaoLive extends TaoBao {
    private static final String TAG = "TaoBaoLive";
    public static final String AppName = "点淘";
    public static final String PackageName = "com.taobao.live";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}