/**
 * @author azraellong
 * @date 2012-11-7
 */
package com.imatlas.workdayclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author azraellong
 *
 */
public class AlarmReceiver extends BroadcastReceiver {
	
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		long alarmId = intent.getLongExtra("alarmId", 0);
		System.out.println("收到个时间通知, id: " + alarmId);
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("时间到");
//		builder.setPositiveButton("确定", null);
//		builder.show();
//		Toast.makeText(context, "收到个时间通知",Toast.LENGTH_LONG).show();
		//闹铃
//		AlarmCenter.ring(context);
		context.startService(new Intent(context, MusicService.class));
		//弹提示
		AlarmCenter.showNotification(context);
//		Context main = MainActivity.this;
	}

}
