package com.naughtykids.app;

import java.util.HashMap;
import java.util.Map;

final class AppFactory {
    private final Map<CharSequence, ThirdPartyApp> mThirdPartyApps;
    AppFactory() {
        mThirdPartyApps = new HashMap<>();
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
            }

            if (thirdPartyApp != null) {
                mThirdPartyApps.put(packageName, thirdPartyApp);
            }
        }
        return thirdPartyApp;
    }
}