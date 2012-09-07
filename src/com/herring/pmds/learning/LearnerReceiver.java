package com.herring.pmds.learning;

import java.util.Calendar;

import com.herring.pmds.devices.AutoRotation;
import com.herring.pmds.devices.BluetoothDevice;
import com.herring.pmds.devices.CPUDevice;
import com.herring.pmds.devices.ScreenDevice;
import com.herring.pmds.devices.WiFiDevice;
import com.herring.pmds.tools.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class LearnerReceiver extends BroadcastReceiver
{
	private KnowledgeDBManager db; 
	private Context ctx;
	
	private int day;
	private int hour;
	private int minute;
	
	private long time;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Log.i("PMDS LearnerReceiver", "Learner alarm received");
		this.ctx = context;
		Calendar cal = Calendar.getInstance();
		time = System.currentTimeMillis();
		cal.setTimeInMillis(time);
		day = cal.get(Calendar.DAY_OF_WEEK);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minute = cal.get(Calendar.MINUTE);
		checkDevices();
	}
	
	private void checkDevices()
	{
		db = new KnowledgeDBManager(ctx);
		
		checkWifi();
		checkBluetooth();
		checkScreen();
		checkAutoRotation();
		checkCPU();
		
		db.closeDB();
		
	}
	
	
	private void checkWifi()
	{
		int lastState = -1;
		
		Cursor wifiC = db.getLastEntryForDevice(Constants.WIFI_DEVICE);
		if(wifiC.getCount()>0)
		{
			lastState = wifiC.getInt(wifiC.getColumnIndex(KnowledgeDBManager.ACTION_TYPE));
		}
		Log.i("PMDS LearnerReceiver", "Last state for wifi: "+lastState);
		boolean currentState = WiFiDevice.isEnabled(ctx);
		
		if(!currentState && (lastState==1 || lastState==-1))
		{
			Log.i("PMDS LearnerReceiver", "Adding wifi entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.TURN_OFF_ACTION);
			db.addScheduleItem(Constants.WIFI_DEVICE, Constants.TURN_OFF_ACTION, day, hour, minute, time);
		}
		if(currentState && (lastState==0 || lastState==-1))
		{
			Log.i("PMDS LearnerReceiver", "Adding wifi entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.TURN_ON_ACTION);
			db.addScheduleItem(Constants.WIFI_DEVICE, Constants.TURN_ON_ACTION, day, hour, minute, time);
		}
	}
	
	private void checkBluetooth()
	{
		int lastState = -1;
		
		Cursor btC = db.getLastEntryForDevice(Constants.BLUETOOTH_DEVICE);
		if(btC.getCount()>0)
		{
			lastState = btC.getInt(btC.getColumnIndex(KnowledgeDBManager.ACTION_TYPE));
		}
		Log.i("PMDS LearnerReceiver", "Last state for bluetooth: "+lastState);
		boolean currentState = BluetoothDevice.isEnabled();
		
		if(!currentState && (lastState==1 || lastState==-1))
		{
			Log.i("PMDS LearnerReceiver", "Adding bluetooth entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.TURN_OFF_ACTION);
			db.addScheduleItem(Constants.BLUETOOTH_DEVICE, Constants.TURN_OFF_ACTION, day, hour, minute, time);
		}
		if(currentState && (lastState==0 || lastState==-1))
		{
			Log.i("PMDS LearnerReceiver", "Adding bluetooth entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.TURN_ON_ACTION);
			db.addScheduleItem(Constants.BLUETOOTH_DEVICE, Constants.TURN_ON_ACTION, day, hour, minute, time);
		}
	}
	
	private void checkScreen()
	{
		int lastState = -1;
		
		Cursor screenC = db.getLastEntryForDevice(Constants.SCREEN_DEVICE);
		if(screenC.getCount()>0)
		{
			lastState = screenC.getInt(screenC.getColumnIndex(KnowledgeDBManager.ACTION_TYPE));
		}
		Log.i("PMDS LearnerReceiver", "Last state for screen: "+lastState);
		
		int currentState = ScreenDevice.getScreenBrightness(ctx);
		
		if(currentState != lastState || lastState == -1)
		{
			Log.i("PMDS LearnerReceiver", "Adding screen entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ currentState);
			db.addScheduleItem(Constants.SCREEN_DEVICE, currentState, day, hour, minute, time);
		}
	}
	
	private void checkAutoRotation()
	{
		int lastState = -1;
		
		Cursor screenC = db.getLastEntryForDevice(Constants.AUTOROTATION);
		if(screenC.getCount()>0)
		{
			lastState = screenC.getInt(screenC.getColumnIndex(KnowledgeDBManager.ACTION_TYPE));
		}
		Log.i("PMDS LearnerReceiver", "Last state for autorotation: "+lastState);
		
		int currentState = AutoRotation.isEnabled(ctx);
		
		if(currentState != lastState || lastState == -1)
		{
			Log.i("PMDS LearnerReceiver", "Adding autorotation entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ currentState);
			db.addScheduleItem(Constants.AUTOROTATION, currentState, day, hour, minute, time);
		}
		
	}
	
	private void checkCPU()
	{
		int lastState = -1;
		
		Cursor cpuC = db.getLastEntryForDevice(Constants.CPU_DEVICE);
		if(cpuC.getCount()>0)
		{
			lastState = cpuC.getInt(cpuC.getColumnIndex(KnowledgeDBManager.ACTION_TYPE));
		}
		Log.i("PMDS LearnerReceiver", "Last state for cpu: "+lastState);
		
		CPUDevice cpu = new CPUDevice();
		String mode = cpu.getMode();
		
		if(mode.contentEquals(Constants.CONSERVATIVE_MODE_STR) && lastState != Constants.CONSERVATIVE_MODE)
		{
			Log.i("PMDS LearnerReceiver", "Adding cpu entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.CONSERVATIVE_MODE);
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.CONSERVATIVE_MODE, day, hour, minute, time);
		}
		else if(mode.contentEquals(Constants.POWERSAVE_MODE_STR) && lastState != Constants.POWERSAVE_MODE)
		{
			Log.i("PMDS LearnerReceiver", "Adding cpu entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.POWERSAVE_MODE);
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.POWERSAVE_MODE, day, hour, minute, time);
		}
		else if(mode.contentEquals(Constants.ONDEMAND_MODE_STR) && lastState != Constants.ONDEMAND_MODE)
		{
			Log.i("PMDS LearnerReceiver", "Adding cpu entry for "+ day+"/"+hour+":"+minute+"(day/hour:minute) and action: "+ Constants.ONDEMAND_MODE);
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.ONDEMAND_MODE, day, hour, minute, time);
		}
	}

}
