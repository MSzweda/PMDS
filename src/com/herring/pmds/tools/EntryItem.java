package com.herring.pmds.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EntryItem 
{
	public int deviceID;
	public int day;
	public int hour;
	public int minute;
	public int action;
	public String time;
	
	public EntryItem(int dId, int action, int day, int hour, int minute)
	{
		this.deviceID = dId;
		this.day = day;
		this.action = action;
		this.minute = minute;
		this.hour = hour;	
		setTime(hour,minute);
	}
	
	private void setTime(int hour, int minute)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, hour);  //24 hour format
    	cal.set(Calendar.MINUTE, minute);
    	cal.set(Calendar.SECOND, 0);
    	
    	DateFormat df = new SimpleDateFormat("HH:mm:ss");
    	time = df.format(cal.getTime());
	}
}
