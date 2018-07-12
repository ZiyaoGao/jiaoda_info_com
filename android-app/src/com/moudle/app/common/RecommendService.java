package com.moudle.app.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import com.moudle.app.AppContext;
import com.moudle.app.AppException;
import com.moudle.app.R;

/**
 * Author: konbluesky
 * E-mail: konbluesky@163.com
 * Create: 14-1-8 - 15:04
 */
public class RecommendService extends Service {

    public static final String ACTION_START = "com.moudle.service.RecommendService.start";
    public static final String ACTION_SET_POLLING_DELAY = "com.moudle.service.RecommendService.setDelay";
    public static final String ACTION_SET_SOUND = "com.moudle.service.RecommendService.setSound";
    public static final String ACTION_SET_VIBRATION = "com.moudle.service.RecommendService.setSound";
    public static final String ACTION_CLOSE = "com.moudle.service.RecommendService.Close";
    public static final String TAG = "RecommendService";
    public static final String PARAM_DELAYTIME = "delaytime";
    public static final String PARAM_SOUNDFLAG = "soundflag";
    public static final String PARAM_VIBRATION = "vibrationflag";
    private NotificationManager mManager;
    private Notification mNotification;
    private AppContext rContext;
    private Handler handler;
    private Runnable polling = new Polling();
    private RecommendReceiver recommendReceiver;
    private long defaultDelay = 60 * 1000;

    @Override
    public void onCreate() {
        Log.d(TAG, "RecommendService onCreate");
        super.onCreate();
        initNotification();
        initOrdestoryBroadcast();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "RecommendService onStart" + intent.getAction());
        if (handler == null) {
            handler = new Handler();
            handler.post(polling);
        }
        return START_REDELIVER_INTENT;
//        return START_NOT_STICKY;
    }

    /**
     * 注册销毁服务
     */
    private void initOrdestoryBroadcast() {
        if (recommendReceiver == null) {
            Log.d(TAG, "initBroadcast");
            recommendReceiver = new RecommendReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CLOSE);
            filter.addAction(ACTION_SET_POLLING_DELAY);
            filter.addAction(ACTION_SET_SOUND);
            filter.addAction(ACTION_START);
            filter.addAction(ACTION_SET_VIBRATION);
            registerReceiver(recommendReceiver, filter);
        } else {
            /**
             * 因为出现两次destory
             * Exception: Unable to stop service com.moudle.app.common.RecommendService@42d2d118: java.lang.IllegalArgumentException: Receiver not registered: com.moudle.app.common.RecommendService$RecommendReceiver@42d2dc10
             */
            Log.d(TAG, "destoryBroadcast");
            try {
                unregisterReceiver(recommendReceiver);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().contains("Receiver not registered")) {
                    // Ignore this exception. This is exactly what is desired
                } else {
                    // unexpected, re-throw
                    throw e;
                }
            }
        }

    }

    private void initNotification() {
        rContext = ((AppContext) getApplicationContext());
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int icon = R.drawable.icon;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "您有一条新的消息,请注意查看";
        //取消默认声音，否则会覆盖指定的提示音
//        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "关闭service");
        try {
            stopSelf();
            /**
             * mNotification 注销
             */
            mNotification = null;
        } catch (Exception e) {
            AppException.run(new Exception("stop polling thread happened this Exception.", e));
        } finally {
            if (handler != null) {
                handler.removeCallbacks(polling);
            }
            //手工回收资源
            stopTimeTask();
            /*
            * 此处如果不注销，日志中会跑warn异常
            * 26994-26994/com.moudle.app:recommend E/ActivityThread﹕ Service com.moudle.app.common.RecommendService has
            * leaked IntentReceiver com.moudle.app.common.RecommendService$CloseReceiver@42051c18
            * that was originally registered here. Are you missing a call to unregisterReceiver()?
            * */
            initOrdestoryBroadcast();
        }
    }

    /**
     * 轮询线程
     */
    private Timer timer;
    private TimerTask timertask;

    private class Polling implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "Polling-开始轮询");
            if (rContext.isRecommends()) {
                startTimeTask();
            } else {
                stopTimeTask();
            }
        }
    }


    public void startTimeTask() {

    }

    public void stopTimeTask() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (timertask != null) {
            timertask.cancel();
            timertask = null;
        }
    }


    /**
     * 注册广播用于关闭service
     * 负责：
     * 1.注销service
     * 2.重置轮询间隔时间
     */
    public class RecommendReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "RecommendReceiver 接收到action:" + action);
            if (action.equals(ACTION_CLOSE)) {
                onDestroy();
            } else if (action.equals(ACTION_SET_POLLING_DELAY)) {
//                Log.d(TAG,ACTION_SET_POLLING_DELAY);
                long delt = intent.getLongExtra(PARAM_DELAYTIME, 0);
                if (delt != 0) {
                    defaultDelay = delt;
                }
                stopTimeTask();
                startTimeTask();
            } else if (action.equals(ACTION_SET_SOUND)) {
//                Log.d(TAG,"RecommendReceiver 接收到actio1111111111n:"+action);
                boolean flag = intent.getBooleanExtra(PARAM_SOUNDFLAG, false);
                if (mNotification != null) {
                    if (flag) {
                        mNotification.sound = Uri.parse("android.resource://" + rContext.getPackageName() + "/" + R.raw.notificationsound);
                    } else {
                        mNotification.sound = null;
                    }
                }
            } else if (action.equals(ACTION_SET_VIBRATION)) {
                boolean flag = intent.getBooleanExtra(PARAM_VIBRATION, false);
                if (mNotification != null) {
                    if (flag) {
                        mNotification.vibrate = new long[]{ 0, 10, 20, 30 };
                    } else {
                        mNotification.vibrate = null;
                    }
                }

            } else if (action.equals(ACTION_START)) {
                //......
            }
        }
    }

}
