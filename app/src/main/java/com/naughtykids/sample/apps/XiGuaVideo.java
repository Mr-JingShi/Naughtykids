package com.naughtykids.sample.apps;

class XiGuaVideo extends Douyin {
    private static final String TAG = "XiGuaVideo";
    public static final String AppName = "西瓜视频";
    public static final String PackageName = "com.ss.android.article.video";
    public static final String LivePlayerActivity = "com.ixigua.openliveplugin.live.LivePlayerActivity";
    @Override
    public String getPackageName() {
        return PackageName;
    }

    @Override
    public String getLivePlayActivity() {
        return LivePlayerActivity;
    }
}