package com.example.rungame10.floatballdemo.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rungame10.floatballdemo.Model.SaveLocation;
import com.example.rungame10.floatballdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainFloatWindow extends LinearLayout {
    private final static int MSG_UPDATE_POS = 1;
    private final static int MSG_WINDOW_HIDE = 2;
    private final static int MSG_WINDOW_SHOW = 3;

    private Context context;
    private ImageView ivFloat;
    private boolean isClick;
    private float mTouchStartX, mTouchStartY;        //手指按下时坐标
    private long startTime,endTime;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    private boolean isOnLeft;                //控件是否在左边
    private int[] location = {-100,-100};
    int moveParam;                                  //自动贴边移动数值

    private boolean isHide = false;                 //悬浮球是否隐藏
    private boolean canHide = true;                 //悬浮球能否隐藏

    public MainFloatWindow(Context context){
        this(context,null);
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_POS:
                    //处理贴边方法传递的xy值刷新悬浮窗
                    mParams.x = msg.arg1;
                    mParams.y = msg.arg2;
                    location[0] = location[0] + moveParam;
                    mWindowManager.updateViewLayout(MainFloatWindow.this,mParams);
                    break;
                case MSG_WINDOW_HIDE:
                    //半隐藏悬浮窗
                    if (isOnLeft) {
                        int toX = -getWidth()/2;
                        ObjectAnimator trans = ObjectAnimator.ofFloat(ivFloat,"translationX",0,toX);
                        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivFloat,"alpha",1.0f,0.4f);
                        AnimatorSet set = new AnimatorSet();
                        set.play(trans).with(alpha);
                        set.setDuration(100);
                        set.start();
                    }else {
                        int toX = getWidth()/2;
                        ObjectAnimator trans = ObjectAnimator.ofFloat(ivFloat,"translationX",0,toX);
                        ObjectAnimator alpha = ObjectAnimator.ofFloat(ivFloat,"alpha",1.0f,0.4f);
                        AnimatorSet set = new AnimatorSet();
                        set.play(trans).with(alpha);
                        set.setDuration(100);
                        set.start();
                    }
                    isHide = true;
                    break;
                case MSG_WINDOW_SHOW:
                    //显示悬浮窗
                    ObjectAnimator trans = ObjectAnimator.ofFloat(ivFloat,"translationX",0,0);
                    ObjectAnimator alpha = ObjectAnimator.ofFloat(ivFloat,"alpha",0.4f,1f);
                    AnimatorSet set = new AnimatorSet();
                    set.play(trans).with(alpha);
                    set.setDuration(100);
                    set.start();
                    isHide = false;
                    break;
            }
        }
    };

    public MainFloatWindow(Context context, AttributeSet attrs) {
        super(context,attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.float_layout,this);
        ivFloat = findViewById(R.id.iv);
        if(location[0] == -100 && location[1] == -100){
            //位置未更新
            if(SaveLocation.saveX <= 0){
                isOnLeft = true;
            }else {
                isOnLeft = false;
            }
            waitToHideWindow();
            location[0] = (int)MainFloatWindow.this.getX();
            location[1] = (int)MainFloatWindow.this.getY();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        DisplayMetrics d = context.getResources().getDisplayMetrics();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //手指放下
                startTime = System.currentTimeMillis();
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();

                canHide = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();

                // 如果移动量大于3才移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 3
                        && Math.abs(mTouchStartY - mMoveStartY) > 3) {
                    // 更新浮动窗口位置参数
                    mParams.x = (int) (x - mTouchStartX);
                    mParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mParams);
                    location[0] = mParams.x;
                    location[1] = mParams.y;
                    if(isHide){
                        handler.sendEmptyMessage(MSG_WINDOW_SHOW);
                    }
                    canHide = false;
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                mMoveStartX = event.getX();
                mMoveStartY = event.getY();
                //如果移动量大于0实行自动贴边，否则判断无移动
                if (Math.abs(mTouchStartX - mMoveStartX) > 0
                        && Math.abs(mTouchStartY - mMoveStartY) > 0) {
                    //等待0.5秒后自动贴边
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isOnLeft = (location[0] + getWidth()/2) <=(d.widthPixels/2);
                    moveParam = isOnLeft ? -20 : 20;
                    autoMoveToSide();       //自动贴边
                }else {
                    //无移动，手指放开
                    endTime = System.currentTimeMillis();
                    if(location[0] == -100 && location[1] == -100){
                        //若初始位置未保存
                        mParams.x = (int) (x - mTouchStartX);
                        mParams.y = (int) (y - mTouchStartY);
                        location[0] = mParams.x;
                        location[1] = mParams.y;
                        mWindowManager.updateViewLayout(this, mParams);
                    }
                    //当从点击到弹起小于半秒的时候,则判断为点击,如果超过则不响应点击事件
                    if ((endTime - startTime) > 0.1 * 1000L) {
                        isClick = false;
                    } else {
                        isClick = true;
                    }
                }
                break;
        }//响应点击事件
        if (isClick) {
            if (isHide){
                handler.sendEmptyMessage(MSG_WINDOW_SHOW);
                canHide = true;
            }else{
                //启动切换账号窗口
                canHide = false;
                doClick();
            }
            isClick = false;
        }else {
            waitToHideWindow();
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    private void autoMoveToSide(){
        new Thread(){
            @Override
            public void run(){
                //实现自动检测悬浮窗左右然后实现自动贴边
                DisplayMetrics d = context.getResources().getDisplayMetrics();
                while (true){
                    int newX = location[0];
                    int newY = location[1];
                    if (isOnLeft && newX<=0) {     //已移至最左侧
                        newX = 0;
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                        SaveLocation.saveX = newX;
                        SaveLocation.saveY = newY;
                        //保存当前悬浮窗位置
                        canHide = true;
                        waitToHideWindow();
                        break;
                    }else if(!isOnLeft && newX>=d.widthPixels){     //已移至最右侧
                        newX = d.widthPixels;
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                        SaveLocation.saveX = newX;
                        SaveLocation.saveY = newY;
                        //保存当前悬浮窗位置
                        canHide = true;
                        waitToHideWindow();
                        break;
                    } else {
                        Message message = new Message();
                        message.what = MSG_UPDATE_POS;
                        message.arg1 = newX;
                        message.arg2 = newY;
                        handler.sendMessage(message);
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    private void waitToHideWindow(){
        if (canHide) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (canHide && !isHide) {
                        handler.sendEmptyMessage(MSG_WINDOW_HIDE);
                    } else {
                        interrupt();
                    }
                }
            }.start();
        }
    }

    private void doClick() {
        NotifyDialog notifyDialog = new NotifyDialog(context,"点击了，略略略");
        notifyDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        notifyDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                canHide = true;
                waitToHideWindow();
            }
        });
        notifyDialog.show();

    }
}
