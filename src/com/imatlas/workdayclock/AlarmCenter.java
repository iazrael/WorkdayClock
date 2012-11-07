/**
 * @author azraellong
 * @date 2012-11-7
 */
package com.imatlas.workdayclock;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author azraellong
 * 
 */
public class AlarmCenter {

	public static void addAlarm(Context context, Alarm alarm) {
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
		PendingIntent pi = PendingIntent.getBroadcast(context, 0/*(int) alarm.id*/,
				intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);

	}
	
	public static void cancelAlarm(Context context, Alarm alarm){
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0/*(int) alarm.id*/,
				intent, 0);
		am.cancel(pi);
	}
}
