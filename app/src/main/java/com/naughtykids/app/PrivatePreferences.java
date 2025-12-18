package com.naughtykids.app;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.Log;

public class PrivatePreferences {
    private static final String TAG = "PrivatePreferences";
    private static final String DouyinVersion = "DouyinVersion";
    private static final String DouyinLiveGift_Xiaoxinxin = "DouyinLiveGift_Xiaoxinxin";
    private static final String DouyinLiveGift_Liwu = "DouyinLiveGift_Liwu";
    public static String getDouyinVersion() {
        return getString(DouyinVersion, "");
    }
    public static void setDouyinVersion(String value) {
        putString(DouyinVersion, value);
    }

    public static void setDouyinLiveGift_Xiaoxinxin(Rect rect) {
        saveRect(DouyinLiveGift_Xiaoxinxin, rect);
    }
    public static Rect getDouyinLiveGift_Xiaoxinxin() {
        return loadRect(DouyinLiveGift_Xiaoxinxin);
    }
    public static void setDouyinLiveGift_Liwu(Rect rect) {
        saveRect(DouyinLiveGift_Liwu, rect);
    }
    public static Rect getDouyinLiveGift_Liwu() {
        return loadRect(DouyinLiveGift_Liwu);
    }

    private static void saveRect(String key, Rect rect) {
        // 格式: "left,top,right,bottom"
        String str = rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom;
        putString(key, str);
    }
    private static Rect loadRect(String key) {
        String rectStr = getString(key, "");
        if (rectStr.isEmpty()) {
            return null;
        }

        try {
            String[] parts = rectStr.split(",");
            if (parts.length != 4) {
                return null;
            }

            int left = Integer.parseInt(parts[0]);
            int top = Integer.parseInt(parts[1]);
            int right = Integer.parseInt(parts[2]);
            int bottom = Integer.parseInt(parts[3]);

            return new Rect(left, top, right, bottom);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid rect string: " + rectStr, e);
            return null;
        }
    }


    private static SharedPreferences getSharedPreferences() {
        return Utils.getA11y().getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
    }

    private static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    private static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    private static int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    private static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    private static void putBoolean(String key, Boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    private static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }
}