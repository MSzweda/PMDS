package com.herring.pmds;

import com.herring.pmds.tools.Constants;
import com.herring.pmds.tools.Scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



public class BootBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Scheduler scheduler = new Scheduler(context);
		
		SharedPreferences PMDSSettings = context.getSharedPreferences(Constants.PMDS_PREFS, Context.MODE_PRIVATE);
		if(!PMDSSettings.getBoolean("PMDS_MODE", false))
		{
			scheduler.makeFromManualDatabase();	
		}
		else
		{			
			scheduler.makeFromAutoDatabase();	
		}
		
	}

}
