package com.moudle.app.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author: konbluesky
 * E-mail: konbluesky@163.com
 * Create: 14-1-8 - 20:56
 */
public class PollingUtils {

    private static Calendar calendar = Calendar.getInstance();// 代表当前时间的日历

    /**
     * 通过AlarmManager设置定时任务
     *
     * @param context
     * @param begintime
     * @param endtime
     * @param cls
     * @param action
     */
    public static void startPollingService(final Context context, String begintime, String endtime, final Class<?> cls, final String action) {
        //获取AlarmManager系统服务
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        //包装需要执行Service的Intent
        Intent intent = new Intent(context, cls);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //触发服务的起始时间，根据传来的时间启动
//          long triggerAtTime = getNextAlarmTime(begintime);
        //@TODO 即时定时任务启动 测试用。
        long triggerAtTime = SystemClock.elapsedRealtime();


        /**
         * miui 中为了节省电量，将AlarmManager 中的repeat时间 固定为5分钟。其他rom情况未知
         * 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
         * AlarmManager.RTC_WAKEUP时会取5的倍数。
         * AlarmManager.RTC 时，可以正常执行。
         * http://www.oschina.net/question/261246_140677
         */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, AlarmManager.INTERVAL_DAY, pendingIntent);


        /**
         * 发起定时广播，到时间后自动关闭service
         */
        Intent stopIntent = new Intent(RecommendService.ACTION_CLOSE);
        PendingIntent pendingStop = PendingIntent.getBroadcast(context, 0, stopIntent, 0);
        long stoptime = getNextAlarmTime(endtime);
        manager.set(AlarmManager.RTC_WAKEUP, stoptime, pendingStop);

        //测试覆盖一个任务（生效）
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2*60000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                AlarmManager manager = (AlarmManager) context
//                        .getSystemService(Context.ALARM_SERVICE);
//
//                //包装需要执行Service的Intent
//                Intent intent = new Intent(context, cls);
//                intent.setAction(action);
//                PendingIntent pendingIntent = PendingIntent.getService(context, 0,
//                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                //触发服务的起始时间，根据传来的时间启动
//                //  long triggerAtTime = getNextAlarmTime(begintime);
//                //@TODO 即时启动 测试用。
//                long triggerAtTime = SystemClock.elapsedRealtime();
//                manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,60*1000, pendingIntent);
//            }
//        }).start();


        //测试终止一个任务，测试无效
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(60000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                AlarmManager manager = (AlarmManager) context
//                        .getSystemService(Context.ALARM_SERVICE);
//
//                //包装需要执行Service的Intent
//                Intent intent = new Intent(context, cls);
//                intent.setAction(action);
//                PendingIntent pendingIntent = PendingIntent.getService(context, 0,
//                        intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                pendingIntent.cancel();
////                manager.cancel(pendingIntent);
//            }
//        }).start();


//        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime,
//                AlarmManager.INTERVAL_DAY, pendingIntent);


        //@TODO 测试修改delay时间的生效 测试用
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Log.d(RecommendService.TAG,"模拟发送修改delay时间");
//                    Thread.sleep(30000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d(RecommendService.TAG,"发起修改delay时间广播");
//                Intent stopIntent=new Intent(RecommendService.ACTION_SET_POLLING_DELAY);
//                stopIntent.putExtra(RecommendService.PARAM_DELAYTIME,5000L);
//                context.sendBroadcast(stopIntent);
//            }
//        }).start();

    }

    //停止轮询服务
    public static void stopPollingService(Context context, Class<?> cls, String action) {
        AlarmManager manager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, cls);
        //要cancel掉之前的intent，action要与之前set的一样
        intent.setAction(RecommendService.ACTION_START);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //取消正在执行的服务
        /**
         * 考虑到 manager.cancel 会有失效的可能，原因可能是pendingIntent不是同一个对象，不知道和底层是不是相关联的。
         * 所以当关闭定时任务时，实际操作是将定时任务的重复周期定为day*365(即一年),因为经测试覆盖pendingIntent 会立即生效。
         * 如果manager.cancel 能够正常终止定时任务则该pendingIntent会被终止，否则任务将延至下一年
         * @TODO 关于AlarmManager的终止方法 有待调查。
         * 参考《说说PendingIntent的内部机制》
         * http://my.oschina.net/youranhongcha/blog/196933
         * http://my.oschina.net/youranhongcha/blog/149564#OSC_h2_3
         * 得知需要保证pendingIntent的对象和当初启动任务时的对象一致，才能成功cancel
         * 因为程序中service在manifest文件中配置的是由单独进程运行，所以涉及到跨进程通信
         * 目前遇到的问题：无法保存AlarmManager启动时候传入的pendintent
         */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_DAY * 365, pendingIntent);
        manager.cancel(pendingIntent);
        //通过发起广播，关闭service
        Intent stopIntent = new Intent(action);
        context.sendBroadcast(stopIntent);
    }


    public static Long getNextAlarmTime(String time) {
        /*
        * 1.当time大于当前时间则time为下一次关闭service的时间。
        * 2.当time小与当前时间则需要为time+1，推移到第二天该时间段。
        * */
        if (StringUtils.isEmpty(time)) return 0L;
        Long nexttime = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date d = null;
        Date currDate = new Date();
        try {
            d = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, d.getHours());
        calendar.set(Calendar.MINUTE, d.getMinutes());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (currDate.getTime() < calendar.getTimeInMillis()) {
            nexttime = calendar.getTimeInMillis();
        } else {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
            nexttime = calendar.getTimeInMillis();
        }
        Log.d("RecommendService", "PollingUtils:getNextAlarmTime--" + new Date(nexttime));
        return nexttime;
    }

    /**
     * 设置推送通知的声音
     *
     * @param context
     * @param b
     */
    public static void setRecommendSound(Context context, boolean b) {
        //通过发起广播，关闭service
        Intent soundIntent = new Intent(RecommendService.ACTION_SET_SOUND);
        soundIntent.putExtra(RecommendService.PARAM_SOUNDFLAG, b);
        context.sendBroadcast(soundIntent);
    }

    /**
     * 设置推送震动
     *
     * @param context
     * @param b
     */
    public static void setRecommendVibration(Context context, boolean b) {
        //通过发起广播，关闭service
        Intent soundIntent = new Intent(RecommendService.ACTION_SET_VIBRATION);
        soundIntent.putExtra(RecommendService.PARAM_VIBRATION, b);
        context.sendBroadcast(soundIntent);
    }

}
