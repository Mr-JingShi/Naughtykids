package com.naughtykids.app;

import java.util.HashMap;
import java.util.Map;

final class AppFactory {
    private final Map<CharSequence, ThirdPartyApp> mThirdPartyApps;
    private static class Holder {
        private static final AppFactory mInstance = new AppFactory();
    }

    public static AppFactory getInstance() {
        return AppFactory.Holder.mInstance;
    }

    private AppFactory() {
        mThirdPartyApps = new HashMap<>();
    }

    boolean hasApp(CharSequence packageName) {
        return mThirdPartyApps.containsKey(packageName);
    }
    ThirdPartyApp getApp(CharSequence packageName) {
        ThirdPartyApp thirdPartyApp = mThirdPartyApps.get(packageName);
        if (thirdPartyApp == null) {
            if (packageName.equals(Utils.getDesktopAppPackageName())) {
                thirdPartyApp = new DesktopApp();
            } else if (packageName.equals(Douyin.PackageName)) {
                thirdPartyApp = new Douyin();
            } else if (packageName.equals(Wechat.PackageName)) {
                thirdPartyApp = new Wechat();
            } else if (packageName.equals(Kwai.PackageName)) {
                thirdPartyApp = new Kwai();
            } else if (packageName.equals(KwaiNebula.PackageName)) {
                thirdPartyApp = new KwaiNebula();
            } else if (packageName.equals(TaoBao.PackageName)) {
                thirdPartyApp = new TaoBao();
            } else if (packageName.equals(JingDong.PackageName)) {
                thirdPartyApp = new JingDong();
            } else if (packageName.equals(PingDuoDuo.PackageName)) {
                thirdPartyApp = new PingDuoDuo();
            } else if (packageName.equals(Tmall.PackageName)) {
                thirdPartyApp = new Tmall();
            } else if (packageName.equals(Bilibili.PackageName)) {
                thirdPartyApp = new Bilibili();
            }

            if (thirdPartyApp != null) {
                mThirdPartyApps.put(packageName, thirdPartyApp);
            }
        }
        return thirdPartyApp;
    }
}