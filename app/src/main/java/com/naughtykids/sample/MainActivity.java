package com.naughtykids.sample;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static String TAG = "MainActivity";
    private static final String DISCLAYER = "disclaimer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "MainActivity onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.hideAppIcon).setOnClickListener(v -> Utils.toggleAppIcon(false));

        // 免责弹窗
        if (PrivatePreferences.getBoolean(this, DISCLAYER, true)) {
            disclaimer();
        } else {
            PermissionHelper.requestPermissions(MainActivity.this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "MainActivity onDestroy");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    private void disclaimer() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.disclaimer_title)
                .setMessage(R.string.disclaimer)
                .setPositiveButton(R.string.disclaimer_confirm, (dialog, which) -> {
                    PermissionHelper.requestPermissions(MainActivity.this);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.disclaimer_cancel, (dialog, which) -> {
                    PrivatePreferences.putBoolean(MainActivity.this, DISCLAYER, false);
                    PermissionHelper.requestPermissions(MainActivity.this);
                    dialog.dismiss();
                })
                .setCancelable(false)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
