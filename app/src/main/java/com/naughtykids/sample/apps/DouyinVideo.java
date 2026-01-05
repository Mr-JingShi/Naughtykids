package com.naughtykids.sample.apps;

class DouyinVideo extends Douyin {
    private static final String TAG = "DouyinVideo";
    public static final String AppName = "抖音精选";
    public static final String PackageName = "com.ss.android.yumme.video";
    public static final String DetailActivity = "com.ss.android.ugc.aweme.detail.ui.DetailActivity";
    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public String getLivePlayActivity() {
        return DetailActivity;
    }
}