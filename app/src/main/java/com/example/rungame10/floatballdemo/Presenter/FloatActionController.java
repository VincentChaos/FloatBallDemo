package com.example.rungame10.floatballdemo.Presenter;

import android.content.Context;
import android.content.Intent;

import com.example.rungame10.floatballdemo.Intf.FloatCallBack;
import com.example.rungame10.floatballdemo.Service.FloatService;

/**
 * Author:chaim
 * Des:与悬浮窗交互的控制类，真正的实现逻辑不在这
 */
public class FloatActionController {

    public static boolean floatFlag = false;

    private FloatActionController() {
    }

    public static FloatActionController getInstance() {
        return FloatProviderHolder.sInstance;
    }

    // 静态内部类
    private static class FloatProviderHolder {
        private static final FloatActionController sInstance = new FloatActionController();
    }

    private FloatCallBack mFloatCallBack;

    /**
     * 开启服务悬浮窗
     */
    public void startServer(Context context) {
        if (floatFlag){
            Intent intent = new Intent(context, FloatService.class);
            context.startService(intent);
        }
    }

    /**
     * 关闭悬浮窗
     */
    public void stopServer(Context context) {
        Intent intent = new Intent(context, FloatService.class);
        context.stopService(intent);
    }

    /**
     * 注册监听
     */
    public void registerCall(FloatCallBack callBack) {
        mFloatCallBack = callBack;
    }



    /**
     * 悬浮窗的显示
     */
    public void show() {
        if (mFloatCallBack == null) return;
        mFloatCallBack.show();
    }

    /**
     * 悬浮窗的隐藏
     */
    public void hide() {
        if (mFloatCallBack == null) return;
        mFloatCallBack.hide();
    }

}
