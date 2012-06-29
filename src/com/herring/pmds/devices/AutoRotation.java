package com.herring.pmds.devices;

import android.content.Context;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class AutoRotation 
{
	public static void turnOff(Context ctx)
	{
		android.provider.Settings.System.putInt(ctx.getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 0);

	}
	
	public static void turnOn(Context ctx)
	{
		android.provider.Settings.System.putInt(ctx.getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
	}
	
	public static int isEnabled(Context ctx)
	{
		int ret = -1;
		try 
		{
			ret = android.provider.Settings.System.getInt(ctx.getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION);
		} 
		catch (SettingNotFoundException e) 
		{
			Log.w("PMDS", "Rotation settings not found");
		}
		return ret;
	}
	
}
