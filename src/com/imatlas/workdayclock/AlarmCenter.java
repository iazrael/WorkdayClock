/**
 * @author azraellong
 * @date 2012-11-7
 */
package com.imatlas.workdayclock;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author azraellong
 * 
 */
public class AlarmCenter {

	public static void addAlarm(Context context, Alarm alarm) {
		// TODO 时间的判断
		Calendar c = Calendar.getInstance();
		String[] strs = alarm.alarmTime.split(":");
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strs[0]));
		c.set(Calendar.MINUTE, Integer.parseInt(strs[1]));
		Calendar now = Calendar.getInstance();
		if (c.before(now)) {// 设置的时候时间已经过了, 就跳到第二天去
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		Log.v("alarm-center",
				"设置闹钟: " + c.get(Calendar.MONTH) + "-"
						+ c.get(Calendar.DAY_OF_MONTH) + " "
						+ c.get(Calendar.HOUR_OF_DAY) + ":"
						+ c.get(Calendar.MINUTE));
		long timeInMillis = c.getTimeInMillis();
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("alarmId", alarm.id);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);

	}

	public static void cancelAlarm(Context context, Alarm alarm) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Log.v("alarm-center", "取消闹钟: " + alarm.toString());
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("alarmId", alarm.id);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.cancel(pi);
	}

	public static void updateAlarm(Context context, Alarm alarm) {
		Log.v("alarm-center", "更新闹钟: " + alarm.toString());
		if (alarm.enable) {
			AlarmCenter.addAlarm(context, alarm);
		} else {
			AlarmCenter.cancelAlarm(context, alarm);
		}
	}

	

	public static void showNotification(Context context) {
		//弹提示
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		int icon = android.R.drawable.ic_dialog_alert;
		long when = System.currentTimeMillis();
		Intent openintent = new Intent(context, MainActivity.class);
		// 当点击消息时就会向系统发送openintent意图
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				openintent, 0);
		// Notification notification = new Notification(icon, "时间到啦", when);
		Notification.Builder builder = new Notification.Builder(context)
				.setContentIntent(contentIntent)
				.setContentTitle("WorkdayClock")
				.setContentText("时间到啦")
				.setSmallIcon(icon)
				.setTicker("时间到啦")
				.setWhen(when);
		Notification notification = builder.build();

		notificationManager.notify(0, notification);
	}
	
	public static void cancelNotification(Context context) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);// 得到系统服务
		manager.cancel(0);// 取消通知
	}

}
