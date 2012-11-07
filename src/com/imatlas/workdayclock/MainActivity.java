package com.imatlas.workdayclock;

import java.util.ArrayList;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	DBHelper dbHelper;
	ArrayList<Alarm> alarmList;
	AlarmArrayAdapter alarmAdapter;

	String[] typeArray = new String[] { "workday", "holiday", "custom" };

	void showNewAlarmForm() {
		MainActivity context = MainActivity.this;

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.add_title);

		// 创建弹出的输入框
		TableLayout layout = new TableLayout(context);
		// first row
		TableRow row = new TableRow(context);
		layout.addView(row);
		TextView textView = new TextView(context);
		textView.setText("Type");
		row.addView(textView);

		final Spinner spinner = new Spinner(context);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, typeArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		row.addView(spinner);

		// second row
		row = new TableRow(context);
		layout.addView(row);

		textView = new TextView(context);
		textView.setText("Time");
		row.addView(textView);

		final TimePicker timePicker = new TimePicker(context);
		timePicker.setIs24HourView(true);
		row.addView(timePicker);

		// third row
		row = new TableRow(context);
		layout.addView(row);

		textView = new TextView(context);
		textView.setText("Enable");
		row.addView(textView);

		final Switch switch1 = new Switch(context);
		switch1.setChecked(true);
		row.addView(switch1);

		builder.setView(layout);

		builder.setPositiveButton(R.string.add_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						int type = spinner.getSelectedItemPosition();
						boolean enable = switch1.isChecked();
						int selectedHour = timePicker.getCurrentHour();
						int selectedMinute = timePicker.getCurrentMinute();
						String alarmTime = selectedHour + ":" + selectedMinute;
						Alarm alarm = dbHelper
								.addAlarm(type, alarmTime, enable);
						// Assert.assertNotNull(alarm);
						if (alarm != null) {
							alarmAdapter.add(alarm);
							Log.v("main-add-alarm", alarm.toString());
							AlarmCenter.addAlarm(MainActivity.this, alarm);
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
				showNewAlarmForm();
			}
		});

		ListView vAlarmList = (ListView) findViewById(R.id.alarmList);

		alarmList = dbHelper.listAlarms();
		alarmAdapter = new AlarmArrayAdapter(this, 0, alarmList);

		vAlarmList.setAdapter(alarmAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class ViewHolder {
		public TextView alarmType;
		public TextView alarmTime;
		public Switch alarmEnable;
	}

	class AlarmArrayAdapter extends ArrayAdapter<Alarm> {

		ArrayList<Alarm> alarms;

		private LayoutInflater layoutInflater;

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

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			final Alarm alarm = alarms.get(position);

			holder.alarmType.setText(typeArray[alarm.type]);
			holder.alarmTime.setText(alarm.alarmTime);
			holder.alarmEnable.setChecked(alarm.enable);
			holder.alarmEnable.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v instanceof Switch) {
						Switch swt = (Switch) v;
						alarm.enable = swt.isChecked();
						dbHelper.updateAlarm(alarm);
						Log.v("main-update-state", alarm.toString());
						// TODO 更新闹铃任务
					}

				}
			});

			return convertView;
		}

	}

}
