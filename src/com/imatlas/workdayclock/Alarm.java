/**
 * @author azraellong
 * @date 2012-11-6
 */
package com.imatlas.workdayclock;

/**
 * @author azraellong 闹铃记录类
 */
public class Alarm {
	public long id;

//	public Date createTime;
//
//	public Date modifyTime;

	public int type;

	public boolean enable;
	/**
	 * 进行闹铃的时间
	 */
	public String alarmTime;

	public Alarm() {
//		this.createTime = new Date();
//		this.modifyTime = new Date();
	}

	public Alarm(int type, String alarmTime, boolean enable) {
		this();
		
		this.type = type;
		this.alarmTime = alarmTime;
		this.enable = enable;
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id + ", " + type + ", " + alarmTime  + ", " + enable;
	}
	
}
