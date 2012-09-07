package com.herring.pmds.tools;

public class Constants 
{
	
	public static final String PMDS_PREFS = "PMDSPreferences";
			
	//devices
	public final static int WIFI_DEVICE = 0;
	public final static int SCREEN_DEVICE = 1;
	public final static int CPU_DEVICE = 2;
	public final static int BLUETOOTH_DEVICE = 3;
	public final static int AUTOROTATION = 4;
	
	//actions
	public final static int TURN_OFF_ACTION = 0;
	public final static int TURN_ON_ACTION = 1;
	
	//days of week
	/*public final static int MONDAY = 0;
	public final static int TUESDAY = 1;
	public final static int WEDNESDAY = 2;
	public final static int THURSDAY = 3;
	public final static int FRIDAY = 4;
	public final static int SATURDAY = 5;
	public final static int SUNDAY = 6; */
	//will use Calendar instead
			
	//activity modes
	public final static int INACTIVE_SCHEDULE_MODE = 0;
	public final static int ACTIVE_SCHEDULE_MODE = 1;
	
	
	//modes
	public static final String ONDEMAND_MODE_STR = "ondemand";
	public static final String POWERSAVE_MODE_STR = "powersave";
	public static final String CONSERVATIVE_MODE_STR = "conservative";
	
	public static final int ONDEMAND_MODE = 0;
	public static final int POWERSAVE_MODE = 1;
	public static final int CONSERVATIVE_MODE = 2;
	
	public static final int MANUAL_MODE = 0;
	public static final int AUTO_MODE = 1;
}
