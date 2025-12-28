package com.naughtykids.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {
    private static String TAG = "App";
    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate");
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        Log.i(TAG, "attachBaseContext");
    }
}
