package com.naughtykids.sample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Authentication {
    private static final String TAG = "Authentication";
    private WindowManager windowManager;
    private View overlayView;
    private boolean isShowing = false;
    private static final int PASSWORD_LENGTH = 6;
    private StringBuilder inputPassword = new StringBuilder();
    private List<ImageView> dotViews = new ArrayList<>();
    private TextView tvError;
    private String correctPassword;
    private OnPasswordVerifiedListener listener;

    private static class Holder {
        private static final Authentication mInstance = new Authentication();
    }

    public static Authentication getInstance() {
        return Authentication.Holder.mInstance;
    }

    public interface OnPasswordVerifiedListener {
        void onVerified(boolean success);
    }

    void init() {
        this.windowManager = (WindowManager) Utils.getA11y().getSystemService(Context.WINDOW_SERVICE);

        createOverlayView();
    }

    public void show(String correctPassword, OnPasswordVerifiedListener listener) {
        if (isShowing) return;

        this.correctPassword = correctPassword;
        this.listener = listener;

        addOverlayToWindow();
        isShowing = true;
    }

    private void createOverlayView() {
        overlayView = LayoutInflater.from(Utils.getA11y()).inflate(R.layout.password_overlay, null);

        initPasswordDots();
        initKeyboard();
        tvError = overlayView.findViewById(R.id.tv_error);
    }

    private void initPasswordDots() {
        LinearLayout passwordDots = overlayView.findViewById(R.id.password_dots);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            ImageView dot = new ImageView(Utils.getA11y());
            dot.setImageResource(R.drawable.bg_dot_normal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(12), dpToPx(12)
            );
            params.setMargins(dpToPx(6), 0, dpToPx(6), 0);
            passwordDots.addView(dot, params);
            dotViews.add(dot);
        }
    }

    private void initKeyboard() {
        GridLayout keyboard = overlayView.findViewById(R.id.keyboard);

        for (char i = 'A'; i <= 'I'; i++) {
            addButton(keyboard, String.valueOf(i));
        }

        addButton(keyboard, "取消");
        addButton(keyboard, "");
        addButton(keyboard, "删除");
    }
    private void addButton(GridLayout parent, String text) {
        Button btn = new Button(Utils.getA11y());
        if (text.isEmpty()) {
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setEnabled(false);
        } else {
            btn.setText(text);
            btn.setBackgroundResource(R.drawable.bg_round_button);
        }
        btn.setTextSize(20);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        int size = dpToPx(64);
        params.width = size;
        params.height = size;
        size = dpToPx(6);
        params.setMargins(size, size, size, size);
        btn.setLayoutParams(params);

        btn.setOnClickListener(v -> {
            if ("取消".equals(text)) {
                hide();
            } else if ("删除".equals(text)) {
                handleDelete();
            } else {
                handleInput(text);
            }
        });

        parent.addView(btn);
    }

    private void handleInput(String number) {
        if (inputPassword.length() >= PASSWORD_LENGTH) return;

        inputPassword.append(number);
        updateDots();

        if (inputPassword.length() == PASSWORD_LENGTH) {
            verifyPassword();
        }
    }

    private void handleDelete() {
        if (inputPassword.length() > 0) {
            inputPassword.deleteCharAt(inputPassword.length() - 1);
            updateDots();
        }
    }

    private void updateDots() {
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            if (i < inputPassword.length()) {
                dotViews.get(i).setImageResource(R.drawable.bg_dot_filled);
            } else {
                dotViews.get(i).setImageResource(R.drawable.bg_dot_normal);
            }
        }
    }

    private void verifyPassword() {
        boolean success = inputPassword.toString().equals(correctPassword);

        if (success) {
            vibrate(100);
            if (listener != null) {
                listener.onVerified(true);
            }
            hide();
        } else {
            vibrate(300);
            showError("密码错误");
            clearInput();
        }
    }

    private void showError(String message) {
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (tvError != null) tvError.setVisibility(View.GONE);
            }, 2000);
        }
    }

    private void clearInput() {
        inputPassword.setLength(0);
        updateDots();
    }

    private void vibrate(long millis) {
        Vibrator vibrator = (Vibrator) Utils.getA11y().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    private void addOverlayToWindow() {
        WindowManager.LayoutParams params = getWindowLayoutParams();
        try {
            windowManager.addView(overlayView, params);
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onVerified(false);
            }
        }
    }

    private WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        return params;
    }

    public void hide() {
        if (isShowing && overlayView != null) {
            try {
                windowManager.removeViewImmediate(overlayView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isShowing = false;
            clearInput();
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                Utils.getA11y().getResources().getDisplayMetrics()
        );
    }
}
