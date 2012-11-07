/**
 * @author azraellong
 * @date 2012-11-6
 */
package com.imatlas.workdayclock;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author azraellong
 * 
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 15;
	private static final String DB_NAME = "alarms.db";
	private static final String TABLE_NAME = "alarm";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DBHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
				+ "(id integer primary key autoincrement, "
//				+ "createTime timestamp, " + "modifyTime timestamp, "
				+ "type int, " + "enable int, " + "alarmTime varchar)";
		db.execSQL(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		Log.v("dbhelper", "db upgrade");
	}

	public Alarm addAlarm(int type, String alarmTime, boolean enable) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues newTaskValues = new ContentValues();
		Alarm alarm = new Alarm(type, alarmTime, enable);
		
		newTaskValues.put("type", alarm.type);
		newTaskValues.put("alarmTime", alarm.alarmTime);
		newTaskValues.put("enable", alarm.enable);
//		newTaskValues.put("createTime", alarm.createTime.toGMTString());
//		newTaskValues.put("modifyTime", alarm.modifyTime.toString());
		long result = db.insert(TABLE_NAME, null, newTaskValues);
		if (result == -1) {
			return null;
		} else {
			alarm.id = result;
			return alarm;
		}

	}

	public void updateAlarm(Alarm alarm) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues newValues = new ContentValues();
		newValues.put("type", alarm.type);
		newValues.put("alarmTime", alarm.alarmTime);
		newValues.put("enable", alarm.enable);
		
		db.update(TABLE_NAME, newValues, "id=" + alarm.id, null);
	}

	public void deleteAlarm(int id) {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_NAME, "id=" + id, null);
	}

	/**
	 * Select All that returns an ArrayList
	 * 
	 * @return the ArrayList for the DB selection
	 */
	public ArrayList<Alarm> listAlarms(){
		SQLiteDatabase db = getReadableDatabase();
		ArrayList<Alarm> list = new ArrayList<Alarm>();
		Cursor cursor = db.query(TABLE_NAME, // Table Name
				null, // Columns to return
				null, // SQL WHERE
				null, // Selection Args
				null, // SQL GROUP BY
				null, // SQL HAVING
				null // SQL ORDER BY
				);
		if (cursor.moveToFirst()) {
			do {
				Alarm alarm = new Alarm();
				int index = 0;
				alarm.id = cursor.getLong(index++);
//				SimpleDateFormat sdf = new SimpleDateFormat();
//				alarm.createTime = sdf.parse(cursor.getString(index++));
//				alarm.modifyTime = sdf.parse(cursor.getString(index++));
				alarm.type = cursor.getInt(index++);
				alarm.enable = cursor.getInt(index++) == 1;
				alarm.alarmTime = cursor.getString(index++);
				Log.v("dbhelper-listAlarms", alarm.toString());
				list.add(alarm);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		Log.v("dbhelper-listAlarms", "size: "+  list.size());
		return list;
	}

}
