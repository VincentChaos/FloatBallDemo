package com.example.rungame10.floatballdemo.Presenter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;


import com.example.rungame10.floatballdemo.Model.SaveLocation;
import com.example.rungame10.floatballdemo.View.MainFloatWindow;

/**
 * Author:chaim
 * Des:悬浮窗统一管理，与悬浮窗交互的真正实现
 */
//悬浮窗管理类
public class FloatingManager {

    private static MainFloatWindow mainFloatWindow;
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams mParams;
    private static boolean hasShown;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     */
    public static void createFloatWindow(Context context) {
        mParams = new WindowManager.LayoutParams();
        WindowManager windowManager = getWindowManager(context);
        mainFloatWindow = new MainFloatWindow(context);
        if (Build.VERSION.SDK_INT >= 24) { /*android7.0不能用TYPE_TOAST*/
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else { /*以下代码块使得android6.0之后的用户不必再去手动开启悬浮窗权限*/
            String packName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", packName));
            if (permission) {
                mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }
        }

        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        mParams.gravity = Gravity.START | Gravity.TOP;

        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        windowManager.getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels;
        //如无保存XY值，以屏幕左上角为原点，设置x、y初始值，相对于gravity
        if(SaveLocation.saveX == -100 && SaveLocation.saveY == -100) {
            mParams.x = 0;
            mParams.y = (int)screenHeight / 2;
        }else {
            mParams.x = SaveLocation.saveX;
            mParams.y = SaveLocation.saveY;
        }
        //设置悬浮窗口长宽数据
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mainFloatWindow.setParams(mParams);
        windowManager.addView(mainFloatWindow, mParams);
        hasShown = true;
    }

    /**
     * 移除悬浮窗
     */
    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mainFloatWindow.isAttachedToWindow();
        }
        if (hasShown && isAttach && windowManager != null)
            windowManager.removeView(mainFloatWindow);
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (windowManager == null) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return windowManager;
    }

    public static void hide() {
        if (hasShown)
            windowManager.removeViewImmediate(mainFloatWindow);
        hasShown = false;
    }

    public static void show() {
        if (!hasShown) {
            windowManager.addView(mainFloatWindow, mParams);
        }
        hasShown = true;
    }
}
