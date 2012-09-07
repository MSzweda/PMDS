package com.herring.pmds.learning;

import java.util.Calendar;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Learner 
{
	private Context ctx;
	private static final String LEARNER_ACTION = "com.herring.pmds.LEARNER_ACTION";
	private AlarmManager alarmManager;
	
	public Learner(Context ctx)
	{
		this.ctx = ctx;
		alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
	}
	
	/* 
	 * start the learner, actually an alarm manager
	 */
	public void startLearner()
	{
		Log.i("PMDS Learner", "Learner activated");
		//set the action intent
		Intent intent = new Intent(LEARNER_ACTION);
		
		//get the object with correct time
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		
		//10 minutes =600 000 milliseconds
		long interval = 600000;
		
		//set it so URI can be read
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		
		//set the alarm
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, pendingIntent);
	}
	
	/*
	 * stop the learner, stop the alarm manager
	 */
	public void stopLearner()
	{
		Log.i("PMDS Learner", "Learner deactivated");
		Intent intent = new Intent(LEARNER_ACTION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
}
