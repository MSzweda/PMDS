package com.herring.pmds;

import com.herring.pmds.auto.AutoSchedulesDBManager;
import com.herring.pmds.devices.AutoRotation;
import com.herring.pmds.devices.BluetoothDevice;
import com.herring.pmds.devices.CPUDevice;
import com.herring.pmds.devices.DummyActivity;
import com.herring.pmds.devices.WiFiDevice;
import com.herring.pmds.manual.ManualSchedulesDBManager;
import com.herring.pmds.tools.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class PMDSAlarmReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		SharedPreferences PMDSSettings = context.getSharedPreferences(Constants.PMDS_PREFS, Context.MODE_PRIVATE);
		
		String uribase = intent.getData().getScheme();
		
		String uri = intent.getData().getSchemeSpecificPart();
		
		Log.i("PMDSAlarmReceiver", "uribase: "+uribase);
		Log.i("PMDSAlarmReceiver", "Received uri: "+uri);
		
		String delims = "[/]";
		String[] tokens = uri.split(delims);

		int sid = Integer.parseInt(tokens[1]);
		int devType = Integer.parseInt(tokens[3]);
		int action = Integer.parseInt(tokens[5]);
		
		
		//false is manual, true if automatic
		if(!PMDSSettings.getBoolean("PMDS_MODE", false) && uribase.contentEquals("pmdsmanualalarm"))
		{
			ManualSchedulesDBManager db;
			db = new ManualSchedulesDBManager(context);
			
			if(db.checkActivityState(sid) == 1)
			{
				if(devType == Constants.WIFI_DEVICE)
				{
					if(action == Constants.TURN_OFF_ACTION) WiFiDevice.turnOff(context);
					if(action == Constants.TURN_ON_ACTION) WiFiDevice.turnOn(context);
				}
				else if(devType == Constants.CPU_DEVICE)
				{
					CPUDevice cpu = new CPUDevice();
					if(action == Constants.ONDEMAND_MODE) cpu.setMode(Constants.ONDEMAND_MODE_STR);
					if(action == Constants.POWERSAVE_MODE) cpu.setMode(Constants.POWERSAVE_MODE_STR);
					if(action == Constants.CONSERVATIVE_MODE) cpu.setMode(Constants.CONSERVATIVE_MODE_STR);
				}
				else if(devType == Constants.BLUETOOTH_DEVICE)
				{
					if(action == Constants.TURN_OFF_ACTION) BluetoothDevice.turnOff();
					if(action == Constants.TURN_ON_ACTION) BluetoothDevice.turnOn();
				}
				else if(devType == Constants.SCREEN_DEVICE)
				{
					Intent screenActiv = new Intent(context, DummyActivity.class);
					screenActiv.putExtra("action", action);
		    		context.startActivity(screenActiv);
				}
				else if(devType == Constants.AUTOROTATION)
				{
					if(action == Constants.TURN_OFF_ACTION) AutoRotation.turnOff(context);
					if(action == Constants.TURN_ON_ACTION) AutoRotation.turnOn(context);
				}
			}
			db.closeDB();
		}
		else if (PMDSSettings.getBoolean("PMDS_MODE", false) && uribase.contentEquals("pmdsautoalarm"))
		{			
			AutoSchedulesDBManager db;
			db = new AutoSchedulesDBManager(context);
			
			if(db.checkActivityState(sid) == 1)
			{
				if(devType == Constants.WIFI_DEVICE)
				{
					if(action == Constants.TURN_OFF_ACTION) WiFiDevice.turnOff(context);
					if(action == Constants.TURN_ON_ACTION) WiFiDevice.turnOn(context);
				}
				else if(devType == Constants.CPU_DEVICE)
				{
					CPUDevice cpu = new CPUDevice();
					if(action == Constants.ONDEMAND_MODE) cpu.setMode(Constants.ONDEMAND_MODE_STR);
					if(action == Constants.POWERSAVE_MODE) cpu.setMode(Constants.POWERSAVE_MODE_STR);
					if(action == Constants.CONSERVATIVE_MODE) cpu.setMode(Constants.CONSERVATIVE_MODE_STR);
				}
				else if(devType == Constants.BLUETOOTH_DEVICE)
				{
					if(action == Constants.TURN_OFF_ACTION) BluetoothDevice.turnOff();
					if(action == Constants.TURN_ON_ACTION) BluetoothDevice.turnOn();
				}
				else if(devType == Constants.SCREEN_DEVICE)
				{
					Intent screenActiv = new Intent(context, DummyActivity.class);
					screenActiv.putExtra("action", action);
		    		context.startActivity(screenActiv);
				}
				else if(devType == Constants.AUTOROTATION)
				{
					if(action == Constants.TURN_OFF_ACTION) AutoRotation.turnOff(context);
					if(action == Constants.TURN_ON_ACTION) AutoRotation.turnOn(context);
				}
			}
			db.closeDB();	
		}
		
	}

}
