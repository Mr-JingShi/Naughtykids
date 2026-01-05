package com.naughtykids.sample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class KeepAliveService extends Service {
    private static final String TAG = "KeepAliveService";
    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init();

        Log.d(TAG, "Service on create");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_ID = "A11Y_KEEP_ALIVE";
            final int NOTIFICATION_ID = 1;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Accessibility Keep Alive", NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("无障碍服务运行中")
                    .setContentText("正在监听界面事件")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();

            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }
}
