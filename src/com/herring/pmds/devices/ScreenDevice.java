package com.herring.pmds.devices;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class ScreenDevice 
{
	private Window window;

	/*set system screen brightness setting
	 * level - the level to which set the screen brightness
	 * ctx - appication's context
	 * act - activity object needed for the screen level setting to work
	 */
	public void setScreenBrightness(int level, Context ctx, Activity act)
	{

		android.provider.Settings.System.putInt(ctx.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS,
		     level);
		window = act.getWindow();  
		LayoutParams layoutpars = window.getAttributes();
		layoutpars.screenBrightness = level / (float) 255 ;
		window.setAttributes(layoutpars);
	}
	
	public static int getScreenBrightness(Context ctx)
	{
		int ret = -1;
		try 
		{
			ret = android.provider.Settings.System.getInt(ctx.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
		} 
		catch (SettingNotFoundException e) 
		{
			Log.w("PMDS", "Screen settings not found");
		}
		return ret;
	}
	
}
