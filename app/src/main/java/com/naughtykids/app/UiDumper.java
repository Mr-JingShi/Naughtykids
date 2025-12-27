package com.naughtykids.app;


import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.Rect;

import java.io.File;
import java.io.FileWriter;

public class UiDumper {
    private static final String TAG = "UiAutomatorXmlDumper";

    public static void  dumpNodeTree(AccessibilityNodeInfo root) {
        if (root != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<hierarchy rotation=\"0\">\n");
            dumpNode(root, sb, 0);
            sb.append("</hierarchy>");

            try {
                File file = new File(Utils.getA11y().getExternalFilesDir(null) + "/naughtykids/uiautomator.xml");
                Log.d(TAG, "dumpNodeTree file:" + file.getAbsolutePath());
                file.getParentFile().mkdirs(); // 确保目录存在
                if (!file.exists()) {
                    file.createNewFile();
                }
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(sb.toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "dumpNodeTree error", e);
            }
        }
    }

    private static void dumpNode(AccessibilityNodeInfo node, StringBuilder sb, int depth) {
        if (node == null) return;

        // 缩进
        String indent = "  ".repeat(depth);
        sb.append(indent).append("<node");

        // 获取 bounds
        Rect bounds = new Rect();
        node.getBoundsInScreen(bounds);

        // 基础属性（按 uiautomator dump 顺序）
        appendAttr(sb, "index", String.valueOf(node.getChildCount() > 0 ? 0 : -1)); // uiautomator 中 index 通常为 0 或子序号，简化处理
        appendAttr(sb, "resource-id", safeString(node.getViewIdResourceName()));
        appendAttr(sb, "class", safeString(node.getClassName()));
        appendAttr(sb, "text", safeString(node.getText()));
        appendAttr(sb, "content-desc", safeString(node.getContentDescription()));
        // bounds="[left,top][right,bottom]"
        String boundsStr = String.format("[%d,%d][%d,%d]", bounds.left, bounds.top, bounds.right, bounds.bottom);
        appendAttr(sb, "bounds", boundsStr);
        // 可见性（非标准，但 uiautomator 有时有）
        // uiautomator 不直接输出 visible-to-user
        appendAttr(sb, "visible-to-user", String.valueOf(node.isVisibleToUser()));
        appendAttr(sb, "package", safeString(node.getPackageName()));
        appendAttr(sb, "checkable", String.valueOf(node.isCheckable()));
        appendAttr(sb, "checked", String.valueOf(node.isChecked()));
        appendAttr(sb, "clickable", String.valueOf(node.isClickable()));
        appendAttr(sb, "enabled", String.valueOf(node.isEnabled()));
        appendAttr(sb, "focusable", String.valueOf(node.isFocusable()));
        appendAttr(sb, "focused", String.valueOf(node.isFocused()));
        appendAttr(sb, "scrollable", String.valueOf(node.isScrollable()));
        appendAttr(sb, "long-clickable", String.valueOf(node.isLongClickable()));
        appendAttr(sb, "password", String.valueOf(node.isPassword()));
        appendAttr(sb, "selected", String.valueOf(node.isSelected()));

        // 子节点处理
        int childCount = node.getChildCount();
        if (childCount == 0) {
            sb.append(" />\n");
        } else {
            sb.append(">\n");
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    dumpNode(child, sb, depth + 1);
                    child.recycle(); // ⚠️ 必须 recycle！
                }
            }
            sb.append(indent).append("</node>\n");
        }
    }

    private static void appendAttr(StringBuilder sb, String name, String value) {
        if (value == null) value = "";
        // 转义特殊字符
        value = value.replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
        sb.append(" ").append(name).append("=\"").append(value).append("\"");
    }

    private static String safeString(CharSequence cs) {
        return cs == null ? "" : cs.toString();
    }
}
