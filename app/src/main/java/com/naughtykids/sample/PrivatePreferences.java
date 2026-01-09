package com.naughtykids.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.util.Log;

public class PrivatePreferences {
    private static final String TAG = "PrivatePreferences";
    private static SharedPreferences getSharedPreferences() {
        return Utils.getA11y().getSharedPreferences(TAG, android.content.Context.MODE_PRIVATE);
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

    public static String getString(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    static boolean getBoolean(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    static int getInt(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    public static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    static void putBoolean(String key, Boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }
}