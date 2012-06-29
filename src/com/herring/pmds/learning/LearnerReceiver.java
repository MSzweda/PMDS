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

public class LearnerReceiver extends BroadcastReceiver
{
	KnowledgeDBManager db; 
	Context ctx;
	
	int day;
	int hour;
	int minute;
	
	long time;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
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
		
		boolean currentState = WiFiDevice.isEnabled(ctx);
		
		if(!currentState && lastState==1)
		{
			db.addScheduleItem(Constants.WIFI_DEVICE, Constants.TURN_OFF_ACTION, day, hour, minute, true, time);
		}
		if(currentState && lastState==0)
		{
			db.addScheduleItem(Constants.WIFI_DEVICE, Constants.TURN_ON_ACTION, day, hour, minute, true, time);
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
		
		boolean currentState = BluetoothDevice.isEnabled();
		
		if(!currentState && lastState==1)
		{
			db.addScheduleItem(Constants.BLUETOOTH_DEVICE, Constants.TURN_OFF_ACTION, day, hour, minute, true, time);
		}
		if(currentState && lastState==0)
		{
			db.addScheduleItem(Constants.BLUETOOTH_DEVICE, Constants.TURN_ON_ACTION, day, hour, minute, true, time);
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
		
		int currentState = ScreenDevice.getScreenBrightness(ctx);
		
		if(currentState != lastState && lastState != -1)
		{
			db.addScheduleItem(Constants.SCREEN_DEVICE, currentState, day, hour, minute, true, time);
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
		
		int currentState = AutoRotation.isEnabled(ctx);
		
		if(currentState != lastState && lastState != -1)
		{
			db.addScheduleItem(Constants.AUTOROTATION, currentState, day, hour, minute, true, time);
		}
		
	}
	
	private void checkCPU()
	{
		CPUDevice cpu = new CPUDevice();
		String mode = cpu.getMode();
		
		if(mode.contentEquals(Constants.CONSERVATIVE_MODE_STR))
		{
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.CONSERVATIVE_MODE, day, hour, minute, true, time);
		}
		if(mode.contentEquals(Constants.POWERSAVE_MODE_STR))
		{
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.POWERSAVE_MODE, day, hour, minute, true, time);
		}
		if(mode.contentEquals(Constants.ONDEMAND_MODE_STR))
		{
			db.addScheduleItem(Constants.CPU_DEVICE, Constants.ONDEMAND_MODE, day, hour, minute, true, time);
		}
	}

}
