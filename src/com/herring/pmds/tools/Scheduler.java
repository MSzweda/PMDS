package com.herring.pmds.tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


import com.herring.pmds.PMDSAlarmReceiver;
import com.herring.pmds.auto.AutoSchedulesDBManager;
import com.herring.pmds.manual.ManualSchedulesDBManager;

public class Scheduler 
{
	private ManualSchedulesDBManager Mdb;
	private AutoSchedulesDBManager Adb;
	private Context ctx;
	
	public Scheduler(Context ctx)
	{
		this.ctx = ctx;
	}
	
	/*
	 * load data from database and schedule events
	 */
	public void makeFromManualDatabase()
	{
		Mdb = new ManualSchedulesDBManager(ctx);
        Cursor schedules = Mdb.getAllActiveSchedules();
        if(schedules.getCount()>0)
        {
        	//move to the first position
        	schedules.moveToFirst();
        	
        	//get column indexes
        	int idColumn = schedules.getColumnIndex(ManualSchedulesDBManager.SCHEDULE_ROWID);
        	int deviceColumn = schedules.getColumnIndex(ManualSchedulesDBManager.DEVICE_TYPE);
        	int actionColumn = schedules.getColumnIndex(ManualSchedulesDBManager.ACTION_TYPE);
        	int weekdayColumn = schedules.getColumnIndex(ManualSchedulesDBManager.WEEKDAY);
        	int hourColumn = schedules.getColumnIndex(ManualSchedulesDBManager.HOUR_OF_DAY);
        	int minuteColumn = schedules.getColumnIndex(ManualSchedulesDBManager.MINUTE_OF_HOUR);
        	
        	//read each row until you move after the last row
        	while(!schedules.isAfterLast())
        	{
        		String uri = constructURI(schedules.getInt(idColumn),schedules.getInt(deviceColumn),schedules.getInt(actionColumn), Constants.MANUAL_MODE);
        		constructAlarmManager(uri,schedules.getInt(weekdayColumn),schedules.getInt(hourColumn),schedules.getInt(minuteColumn));
        		schedules.moveToNext();
        	}
        }
        Mdb.closeDB();
	}
	
	/*
	 * load data from database and schedule events
	 */
	public void makeFromAutoDatabase()
	{
		Adb = new AutoSchedulesDBManager(ctx);
        Cursor schedules = Adb.getAllActiveSchedules();
        if(schedules.getCount()>0)
        {
        	//move to the first position
        	schedules.moveToFirst();
        	
        	//get column indexes
        	int idColumn = schedules.getColumnIndex(ManualSchedulesDBManager.SCHEDULE_ROWID);
        	int deviceColumn = schedules.getColumnIndex(ManualSchedulesDBManager.DEVICE_TYPE);
        	int actionColumn = schedules.getColumnIndex(ManualSchedulesDBManager.ACTION_TYPE);
        	int weekdayColumn = schedules.getColumnIndex(ManualSchedulesDBManager.WEEKDAY);
        	int hourColumn = schedules.getColumnIndex(ManualSchedulesDBManager.HOUR_OF_DAY);
        	int minuteColumn = schedules.getColumnIndex(ManualSchedulesDBManager.MINUTE_OF_HOUR);
        	
        	//read each row until you move after the last row
        	while(!schedules.isAfterLast())
        	{
        		String uri = constructURI(schedules.getInt(idColumn),schedules.getInt(deviceColumn),schedules.getInt(actionColumn), Constants.AUTO_MODE);
        		constructAlarmManager(uri,schedules.getInt(weekdayColumn),schedules.getInt(hourColumn),schedules.getInt(minuteColumn));
        		schedules.moveToNext();
        	}
        }
        Adb.closeDB();
	}
	
	/*
	 * construct URI for broadcast
	 * example:
	 * pmdsalarm:sid/0/device/2/action/1
	 * sid - so receiver can check if schedule item is active
	 * device - type of device, 2 is CPI
	 * action - type of action, 1 is set to powersave
	 */
	private String constructURI(int schedID, int device, int action, int mode)
	{
		String base = "";
		if(mode == Constants.AUTO_MODE)
		{
			base = "pmdsautoalarm:";
		}
		else if(mode == Constants.MANUAL_MODE)
		{
			base = "pmdsmanualalarm:";
		}
		//String base = "pmdsalarm:";
		String idSTR = "sid/"+schedID+"/";
		String deviceSTR = "device/"+device+"/";
		String actionSTR = "action/"+action+"/";
	
		Log.i("PMDS Scheduler", "Uri: "+base + idSTR + deviceSTR + actionSTR);
		
		return base + idSTR + deviceSTR + actionSTR;
	}
	
	/*
	 * calculate the initial time and set Calendar object to it
	 */
	private Calendar getScheduleTime(int weekday, int hour, int minute)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		
		int today = cal.get(Calendar.DAY_OF_WEEK);
		int curr_hour = cal.get(Calendar.HOUR_OF_DAY);
		int curr_minute = cal.get(Calendar.MINUTE);
		
		int dayOffset = -1;
    	if(weekday>today)
    	{
    		dayOffset = weekday - today;
    	}
    	else if(weekday<today)
    	{
    		dayOffset = 7 + weekday - today;
    	}
    	else if(weekday==today)
    	{
    		if(curr_hour < hour)
    		{
    			dayOffset = weekday - today;
    		}
    		else if(curr_hour > hour)
    		{
    			dayOffset = 7 + weekday - today;
    		}
    		else if(curr_hour == hour)
    		{
    			if(curr_minute > minute)
    			{
    				dayOffset = 7 + weekday - today;
    			}
    			else if(curr_minute <= minute)
    			{
    				dayOffset = weekday - today;
    			}
    		}
    	}
    	cal.add(Calendar.DATE, dayOffset);
    	cal.set(Calendar.HOUR_OF_DAY, hour);  //24 hour format
    	cal.set(Calendar.MINUTE, minute);
    	cal.set(Calendar.SECOND, 0);
    	
    	DateFormat df = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm:ss");
    	Log.i("PMDS Scheduler", "Alarm set to: "+df.format(cal.getTime()));
    	
    	return cal;
	}

	/*
	 * create the alarm manager
	 */
	private void constructAlarmManager(String uri, int weekday, int hour, int minute)
	{
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		
		//direct to receiver
		Intent intent = new Intent(ctx, PMDSAlarmReceiver.class);
		//set uri as data, so it can be filtered by the "pmds*alarm:" schema
		intent.setData(Uri.parse(uri));
		
		//get the object with correct time
		Calendar cal = getScheduleTime(weekday, hour, minute);
		
		
		//1 week = 604 800 000 milliseconds
		long interval = 604800000;
		
		//set it so URI can be read
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
		//set the alarm
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, pendingIntent);
	}
	
	
	/*
	 * schedule new item
	 */
	public void scheduleNewItem(int mode, long id, int device, int action, int weekday, int hour, int minute)
	{
		String uri = constructURI((int)id,device,action, mode);
		constructAlarmManager(uri,weekday,hour,minute);
	}
	
	public void cancelAllPendingAlarms()
	{
		Intent intent = new Intent(ctx, PMDSAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
		pendingIntent.cancel();
	}
}
