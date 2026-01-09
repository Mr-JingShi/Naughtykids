package com.naughtykids.sample.apps;

class AliPay extends DisableRunningApp {
    private static final String TAG = "AliPay";
    public static final String AppName = "支付宝";
    public static final String PackageName = "com.eg.android.AlipayGphone";

    @Override
    public String getPackageName() {
        return PackageName;
    }
}