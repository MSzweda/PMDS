package com.herring.pmds.learning;

import com.herring.pmds.tools.Constants;

import android.database.Cursor;

public class BayesClassifier 
{
	/*
	 * there's action, device, hour an minute
	 * 
	 * P(action=turn_on), P(action=turn_off) - probability of turning a device on or off 
	 * [for a given day; for example, it's monday, and we have 5 entries on record. 3 are for on, 2 for off.
	 * it doesn't matter which device gets turned on/off, P(action=turn_on) = 3/5, P(action=turn_off) = 2/5]
	 * 
	 * P(action=turn_on | device=devID), P(action=turn_off | device=devID) - probability of turning devID on/off (events are independent)
	 * 
	 * P(action=turn_on | time=desiredTime), P(action=turn_off | time=desiredTime) - probability of a device 
	 * turning off/on on that exact time, doesn't matter what device it is (events are independent)
	 * 
	 * P(device=devID, time=desiredTime | action=turn_on) = P(action=turn_on | device=devID) *  P(action=turn_on | time=desiredTime)
	 * P(device=devID, time=desiredTime | action=turn_off) = P(action=turn_off | device=devID) *  P(action=turn_off | time=desiredTime)
	 * 
	 * and the final numbers we care about are:
	 * P = P(device=devID, time=desiredTime | action=turn_on) * P(action=turn_on)
	 * P = P(device=devID, time=desiredTime | action=turn_off) * P(action=turn_off)
	 * 
	 * we're deciding by whichever P is higher
	 * 
	 * in case there's a situation when one of the calculated probabilities = 1, we can't set the other one to 0
	 * it's going to be set to 0,001
	 * 
	 */
	
	private Cursor dataCursor;
	
	private int entryCount = -1;
	
	private double P__TURN_ON = -1;
	private double P__TURN_OFF = -1;
	
	private int ON_COUNT = -1;
	private int OFF_COUNT = -1;
	
	private double P__A1 = -1;
	private double P__A2 = -1;
	private double P__A3 = -1;
	
	private int A1_COUNT = -1;
	private int A2_COUNT = -1;
	private int A3_COUNT = -1;
	
	private int actionColumn;
	
	public BayesClassifier(Cursor entryCursor)
	{
		this.dataCursor = entryCursor;
		this.entryCount = dataCursor.getCount();
		
		this.actionColumn = dataCursor.getColumnIndex(KnowledgeDBManager.ACTION_TYPE);
		
		ON_COUNT = getActionCount(Constants.TURN_ON_ACTION);
		OFF_COUNT = entryCount-ON_COUNT;
		P__TURN_ON = ON_COUNT/entryCount;
		P__TURN_OFF = OFF_COUNT/entryCount;
	}
	
	public BayesClassifier(Cursor entryCursor, int devtype)
	{
		this.dataCursor = entryCursor;
		this.entryCount = dataCursor.getCount();
		
		this.actionColumn = dataCursor.getColumnIndex(KnowledgeDBManager.ACTION_TYPE);
		
		if(devtype == Constants.CPU_DEVICE)
		{
			A1_COUNT = getActionCount(Constants.POWERSAVE_MODE);
			A2_COUNT = getActionCount(Constants.CONSERVATIVE_MODE);
			A3_COUNT = entryCount - A1_COUNT - A2_COUNT;
		}
		if(devtype == Constants.SCREEN_DEVICE)
		{
			A1_COUNT = getActionCountForScreen(0, 84);
			A2_COUNT = getActionCountForScreen(85, 170);
			A3_COUNT = entryCount - A1_COUNT - A2_COUNT;
		}
		P__A1 = A1_COUNT / entryCount;
		P__A2 = A2_COUNT / entryCount;
		P__A3 = A3_COUNT / entryCount;	
	}
	
	private int getActionCount(int action)
	{
		int counter = 0;
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)==action)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		return counter;
	}
	
	private int getActionCountForScreen(int bottom, int top)
	{
		int counter = 0;
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)>=bottom && dataCursor.getInt(actionColumn)<=top)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		return counter;
	}
	
	private double P_action_device(int action, int device)
	{
		double p;
		int counter = 0;
		
		int deviceColumn = dataCursor.getColumnIndex(KnowledgeDBManager.DEVICE_TYPE);
		
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)==action && dataCursor.getInt(deviceColumn)==device)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		if(action == Constants.TURN_OFF_ACTION)
		{
			p = counter/OFF_COUNT;
		}
		else
		{
			p = counter/ON_COUNT;
		}
		
		if(p==(double)0) p = 0.001;
		
		return p;
	}
	
	private double P_action_time(int action, int hour, int minute)
	{
		double p;
		int counter = 0;
		
		int hourColumn = dataCursor.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = dataCursor.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)==action && dataCursor.getInt(hourColumn)==hour && dataCursor.getInt(minuteColumn)==minute)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		if(action == Constants.TURN_OFF_ACTION)
		{
			p = counter/OFF_COUNT;
		}
		else
		{
			p = counter/ON_COUNT;
		}
		
		if(p==(double)0) p = 0.001;
		
		return p;
	}
	
	private double P_action_time_CPU(int action, int hour, int minute)
	{
		double p;
		int counter = 0;
		
		int hourColumn = dataCursor.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = dataCursor.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)==action && dataCursor.getInt(hourColumn)==hour && dataCursor.getInt(minuteColumn)==minute)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		if(action == Constants.POWERSAVE_MODE)
		{
			p = counter/A1_COUNT;
		}
		else if (action == Constants.CONSERVATIVE_MODE)
		{
			p = counter/A2_COUNT;
		}
		else
		{
			p = counter/A3_COUNT;
		}
		
		if(p==(double)0) p = 0.001;
		
		return p;
	}
	
	private double P_action_time_Screen(int bottom, int top, int hour, int minute)
	{
		double p;
		int counter = 0;
		
		int hourColumn = dataCursor.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = dataCursor.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		dataCursor.moveToFirst();
		while(!dataCursor.isAfterLast())
    	{
			if(dataCursor.getInt(actionColumn)>=bottom && dataCursor.getInt(actionColumn)<=top && dataCursor.getInt(hourColumn)==hour && dataCursor.getInt(minuteColumn)==minute)
			{
				counter++;
			}
    		dataCursor.moveToNext();
    	}
		if(bottom == 0 && top == 84)
		{
			p = counter/A1_COUNT;
		}
		else if (bottom == 85 && top == 170)
		{
			p = counter/A2_COUNT;
		}
		else
		{
			p = counter/A3_COUNT;
		}
		
		if(p==(double)0) p = 0.001;
		
		return p;
	}
	
	private double P_X_action(int action, int device, int hour, int minute)
	{
		return P_action_device(action, device) * P_action_time(action, hour, minute);
	}
	
	private double calculateP(int action, int device, int hour, int minute)
	{
		if(action == Constants.TURN_OFF_ACTION)
		{
			return P_X_action(action, device, hour, minute) * P__TURN_OFF;
		}
		else
		{
			return P_X_action(action, device, hour, minute) * P__TURN_ON;
		}
	}
	
	private double calculateP_CPU(int action,  int hour, int minute)
	{
		if(action == Constants.POWERSAVE_MODE)
		{
			return P_action_time_CPU(action, hour, minute) * P__A1;
		}
		else if(action == Constants.CONSERVATIVE_MODE)
		{
			return P_action_time_CPU(action, hour, minute) * P__A2;
		}
		else
		{
			return P_action_time_CPU(action, hour, minute) * P__A3;
		}
	}
	
	private double calculateP_Screen(int bottom, int top,  int hour, int minute)
	{
		if(bottom == 0 && top == 84)
		{
			return P_action_time_Screen(bottom, top, hour, minute) * P__A1;
		}
		else if(bottom == 85 && top == 170)
		{
			return P_action_time_Screen(bottom, top, hour, minute) * P__A2;
		}
		else
		{
			return P_action_time_Screen(bottom, top, hour, minute) * P__A3;
		}
	}
	
	public int makeDecision(int device, int hour, int minute)
	{
		int retVal = -1;
		if(P__TURN_ON != (double)1 && P__TURN_OFF != (double)1)
		{
			double P_turnOn = calculateP(Constants.TURN_ON_ACTION, device, hour, minute);
			double P_turnOff = calculateP(Constants.TURN_OFF_ACTION, device, hour, minute);
			if(P_turnOn > P_turnOff) retVal =  1;
			else if(P_turnOn < P_turnOff) retVal = 0;
			else retVal = 0; //seriously, if it's equal, better to just turn it off
		}
		
		if(P__TURN_ON == (double)1) retVal = 1;
		if(P__TURN_OFF == (double)1) retVal = 0;

		return retVal;
	}
	
	public int makeDecision_CPU(int hour, int minute)
	{
		int retVal = -1;
		if(P__A1 != (double)1 && P__A2 != (double)1 && P__A3 != (double)1)
		{
			double P_a1 = calculateP_CPU(Constants.POWERSAVE_MODE, hour, minute);
			double P_a2 = calculateP_CPU(Constants.CONSERVATIVE_MODE, hour, minute);
			double P_a3 = calculateP_CPU(Constants.ONDEMAND_MODE, hour, minute);
			if(P_a1 >= P_a2 && P_a1 >= P_a3) retVal = Constants.POWERSAVE_MODE;
			else if(P_a2 > P_a1 && P_a2 >= P_a3) retVal = Constants.CONSERVATIVE_MODE;
			else if(P_a3 > P_a1 && P_a3 > P_a2) retVal = Constants.ONDEMAND_MODE;
			else retVal = Constants.POWERSAVE_MODE;
		}
		
		if(P__A1 == (double)1) retVal = Constants.POWERSAVE_MODE;
		if(P__A2 == (double)1) retVal = Constants.CONSERVATIVE_MODE;
		if(P__A3 == (double)1) retVal = Constants.ONDEMAND_MODE;

		return retVal;
	}
	
	public int makeDecision_Screen(int hour, int minute, int screenVal)
	{
		int retVal = -1;
		if(P__A1 != (double)1 && P__A2 != (double)1 && P__A3 != (double)1)
		{
			double P_a1 = calculateP_Screen(0, 84, hour, minute);
			double P_a2 = calculateP_Screen(85, 170, hour, minute);
			double P_a3 = calculateP_Screen(171, 255, hour, minute);
			if(P_a1 >= P_a2 && P_a1 >= P_a3) 
			{
				if(screenVal >= 0 && screenVal <=84) retVal = screenVal;
				else retVal = 0;
			}
			else if(P_a2 > P_a1 && P_a2 >= P_a3)
			{
				if(screenVal >= 84 && screenVal <=170) retVal = screenVal;
				else retVal = 128;
			}
			else if(P_a3 > P_a1 && P_a3 > P_a2) 
			{
				if(screenVal >= 171 && screenVal <=255) retVal = screenVal;
				else retVal = 255;
			}
			else retVal = 0;
		}
		
		if(P__A1 == (double)1) retVal = 0;
		if(P__A2 == (double)1) retVal = 128;
		if(P__A3 == (double)1) retVal = 255;

		return retVal;
	}
	

}
