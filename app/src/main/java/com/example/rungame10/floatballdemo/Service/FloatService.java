package com.example.rungame10.floatballdemo.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.rungame10.floatballdemo.Intf.FloatCallBack;
import com.example.rungame10.floatballdemo.Presenter.FloatActionController;
import com.example.rungame10.floatballdemo.Presenter.FloatingManager;
import com.example.rungame10.floatballdemo.Receiver.HomeWatcherReceiver;

public class FloatService extends Service implements FloatCallBack {
    /**
     * home键监听
     */
    private HomeWatcherReceiver mHomeKeyReceiver;

    @Override
    public void onCreate(){
        super.onCreate();
        FloatActionController.getInstance().registerCall(this);
        //注册广播接收者
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeKeyReceiver,homeFilter);
        //初始化悬浮窗UI
        FloatingManager.createFloatWindow(this);
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //移除悬浮窗
        FloatingManager.removeFloatWindowManager();
        //注销广播接收者
        if (null != mHomeKeyReceiver) {
            unregisterReceiver(mHomeKeyReceiver);
        }
    }

    /////////////////////////////////////////////////////////实现接口////////////////////////////////////////////////////

    @Override
    public void show() {
        FloatingManager.show();
    }

    @Override
    public void hide() {
        FloatingManager.hide();
    }

}
