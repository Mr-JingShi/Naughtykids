package com.naughtykids.sample.apps;

import com.naughtykids.sample.Utils;

import java.util.HashMap;
import java.util.Map;

public final class AppFactory {
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
    public ThirdPartyApp getApp(CharSequence packageName) {
        ThirdPartyApp thirdPartyApp = mThirdPartyApps.get(packageName);
        if (thirdPartyApp == null) {
            if (packageName.equals(Utils.getDesktopAppPackageName())) {
                thirdPartyApp = new DesktopApp();
            } else if (packageName.equals(Douyin.PackageName)) {
                thirdPartyApp = new Douyin();
            } else if (packageName.equals(DouyinLite.PackageName)) {
                thirdPartyApp = new DouyinLite();
            } else if (packageName.equals(DouyinLive.PackageName)) {
                thirdPartyApp = new DouyinLive();
            } else if (packageName.equals(DouyinVideo.PackageName)) {
                thirdPartyApp = new DouyinVideo();
            } else if (packageName.equals(DouyinMall.PackageName)) {
                thirdPartyApp = new DouyinMall();
            } else if (packageName.equals(Wechat.PackageName)) {
                thirdPartyApp = new Wechat();
            } else if (packageName.equals(Kwai.PackageName)) {
                thirdPartyApp = new Kwai();
            } else if (packageName.equals(KwaiNebula.PackageName)) {
                thirdPartyApp = new KwaiNebula();
            } else if (packageName.equals(TaoBao.PackageName)) {
                thirdPartyApp = new TaoBao();
            } else if (packageName.equals(TaoBaoLive.PackageName)) {
                thirdPartyApp = new TaoBaoLive();
            } else if (packageName.equals(JingDong.PackageName)) {
                thirdPartyApp = new JingDong();
            } else if (packageName.equals(PingDuoDuo.PackageName)) {
                thirdPartyApp = new PingDuoDuo();
            } else if (packageName.equals(Tmall.PackageName)) {
                thirdPartyApp = new Tmall();
            } else if (packageName.equals(Bilibili.PackageName)) {
                thirdPartyApp = new Bilibili();
            } else if (packageName.equals(XiaoHongShu.PackageName)) {
                thirdPartyApp = new XiaoHongShu();
            } else if (packageName.equals(XiGuaVideo.PackageName)) {
                thirdPartyApp = new XiGuaVideo();
            } else if (packageName.equals(VipShop.PackageName)) {
                thirdPartyApp = new VipShop();
            } else if (packageName.equals(MeiTuan.PackageName)) {
                thirdPartyApp = new MeiTuan();
            } else if (packageName.equals(AliPay.PackageName)) {
                thirdPartyApp = new AliPay();
            } else if (packageName.equals(DangDang.PackageName)) {
                thirdPartyApp = new DangDang();
            }

            if (thirdPartyApp != null) {
                mThirdPartyApps.put(packageName, thirdPartyApp);
            }
        }
        return thirdPartyApp;
    }
}