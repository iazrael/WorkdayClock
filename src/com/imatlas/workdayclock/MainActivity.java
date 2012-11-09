package com.imatlas.workdayclock;

import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	DBHelper dbHelper;
	ArrayList<Alarm> alarmList;
	AlarmArrayAdapter alarmAdapter;

	String[] typeArray = new String[] { "workday", "holiday", "custom" };

	void showAlarmForm(final Alarm alarm) {
		MainActivity context = MainActivity.this;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.add_title);
		// 创建弹出的输入框
		View formView = context.getLayoutInflater().inflate(
				R.layout.add_alarm_popup, null);

		final Spinner spinner = (Spinner) formView
				.findViewById(R.id.add_alarm_type);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, typeArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		final TimePicker timePicker = (TimePicker) formView
				.findViewById(R.id.add_alarm_timepicter);
		timePicker.setIs24HourView(true);

		final Switch switch1 = (Switch) formView
				.findViewById(R.id.add_alarm_switch);
		
		builder.setView(formView);
		
		if(alarm == null){//create new one
			Calendar c = Calendar.getInstance();
			timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
			switch1.setChecked(true);
		}else{
			//edit
			switch1.setChecked(alarm.enable);
			spinner.setSelection(alarm.type);
			String[] strs = alarm.alarmTime.split(":");
			timePicker.setCurrentHour(Integer.parseInt(strs[0]));
			timePicker.setCurrentMinute(Integer.parseInt(strs[1]));
		}
		
		
		builder.setPositiveButton(R.string.add_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int type = spinner.getSelectedItemPosition();
						boolean enable = switch1.isChecked();
						int selectedHour = timePicker.getCurrentHour();
						int selectedMinute = timePicker.getCurrentMinute();
						String alarmTime = selectedHour + ":" + selectedMinute;
						if(alarm == null){
							Alarm newAlarm = dbHelper
									.addAlarm(type, alarmTime, enable);
							// Assert.assertNotNull(alarm);
							if (newAlarm != null) {
								alarmAdapter.add(newAlarm);
								Log.v("main-add-alarm", newAlarm.toString());
								AlarmCenter.addAlarm(MainActivity.this, newAlarm);
							}
						}else{
							alarm.enable = enable;
							alarm.alarmTime = alarmTime;
							alarm.type = type;
							Log.v("main-update-alarm", alarm.toString());
							dbHelper.updateAlarm(alarm);
							alarmAdapter.notifyDataSetChanged();
							AlarmCenter.updateAlarm(MainActivity.this, alarm);
						}
					}
				});
		builder.setNegativeButton(R.string.add_cancel, null);
		builder.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbHelper = new DBHelper(this);

		Button newButton = (Button) findViewById(R.id.newButton);
		newButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAlarmForm(null);
			}
		});

		Button editButton = (Button) findViewById(R.id.editButton);
		editButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Button ediButton = (Button) v;
				if (alarmAdapter.isEditing) {
					alarmAdapter.isEditing = false;
					ediButton.setText(R.string.edit_string);
				} else {
					alarmAdapter.isEditing = true;
					ediButton.setText(R.string.save_string);
				}
				alarmAdapter.notifyDataSetChanged();
			}
		});

		ListView vAlarmList = (ListView) findViewById(R.id.alarmList);

		alarmList = dbHelper.listAlarms();
		alarmAdapter = new AlarmArrayAdapter(this, 0, alarmList);

		vAlarmList.setAdapter(alarmAdapter);

		vAlarmList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				Log.d("main", "click list view item at: " + index);
				Alarm alarm = alarmAdapter.getItem(index);
				showAlarmForm(alarm);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v("main", "onStart");
		Context context = MainActivity.this;
		stopService(new Intent(context, MusicService.class));
		AlarmCenter.cancelNotification(context);
	}

	class ViewHolder {
		public TextView alarmType;
		public TextView alarmTime;
		public Switch alarmEnable;
		public Button delteButton;
	}

	class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

		ArrayList<Alarm> alarms;

		private LayoutInflater layoutInflater;

		boolean isEditing = false;

		/**
		 * @param context
		 * @param textViewResourceId
		 * @param objects
		 */
		public AlarmArrayAdapter(Context context, int textViewResourceId,
				ArrayList<Alarm> alarms) {
			super(context, textViewResourceId, alarms);
			this.alarms = alarms;
			this.layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				holder = new ViewHolder();

				convertView = layoutInflater.inflate(R.layout.alarm_list_item,
						null);

				holder.alarmType = (TextView) convertView
						.findViewById(R.id.list_alarm_type);
				holder.alarmTime = (TextView) convertView
						.findViewById(R.id.list_alarm_time);
				holder.alarmEnable = (Switch) convertView
						.findViewById(R.id.list_alarm_enable);
				holder.delteButton = (Button) convertView
						.findViewById(R.id.del_alarm);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			final Alarm alarm = alarms.get(position);

			holder.alarmType.setText(typeArray[alarm.type]);
			holder.alarmTime.setText(alarm.alarmTime);
			holder.alarmEnable.setChecked(alarm.enable);
			if (isEditing) {
				holder.delteButton.setVisibility(View.VISIBLE);
				holder.alarmEnable.setVisibility(View.GONE);

				holder.delteButton
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								// AlertDialog.Builder builder = new
								// AlertDialog.Builder(MainActivity.this);
								// builder.setTitle(R.string.warning_title);
								//
								// builder.setPositiveButton(R.string.add_confirm,
								// new DialogInterface.OnClickListener() {
								// @Override
								// public void onClick(DialogInterface dialog,
								// int which) {
								//
								// }
								// });
								// builder.setNegativeButton(R.string.add_cancel,
								// null);
								// builder.show();
								Log.v("main-delete-alarm", alarm.toString());
								AlarmCenter.cancelAlarm(MainActivity.this,
										alarm);
								dbHelper.deleteAlarm(alarm.id);
								alarmAdapter.remove(alarm);
							}
						});
			} else {
				holder.delteButton.setVisibility(View.GONE);
				holder.alarmEnable.setVisibility(View.VISIBLE);

				holder.alarmEnable
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								if (v instanceof Switch) {
									Switch swt = (Switch) v;
									alarm.enable = swt.isChecked();
									dbHelper.updateAlarm(alarm);
									Log.v("main-update-state", alarm.toString());
									AlarmCenter.updateAlarm(MainActivity.this,
											alarm);

								}

							}
						});
			}

			return convertView;
		}

	}

}
