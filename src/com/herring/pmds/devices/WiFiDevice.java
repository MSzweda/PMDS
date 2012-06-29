package com.herring.pmds.devices;

import android.content.Context;
import android.net.wifi.WifiManager;


/* Class for managing wifi state
 *
 *	requires permissions:
 *	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 */
public class WiFiDevice 
{
	//WiFiManager object
	static WifiManager wm;
	
	//call to turn off the wifi device, if it's turned on
	public static void turnOff(Context ctx)
	{
		wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) 
		{
			wm.setWifiEnabled(false);
		}
	}
	
	//call to turn on the wifi device, if it's turned off
	public static void turnOn(Context ctx)
	{
		wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) 
		{
			wm.setWifiEnabled(true);
		}
	}
	
	public static boolean isEnabled(Context ctx)
	{
		wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) 
		{
			return false;
		}
		else return true;
	}

}
