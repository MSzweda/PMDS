package com.herring.pmds.auto;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.herring.pmds.learning.BayesClassifier;
import com.herring.pmds.learning.KnowledgeDBManager;
import com.herring.pmds.tools.Constants;
import com.herring.pmds.tools.EntryItem;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

//analyzes knowledge database and builds auto schedules database
public class Analyzer 
{

	private Context ctx;
	private KnowledgeDBManager kdb;
	private AutoSchedulesDBManager adb; 
	
	public Analyzer(Context ctx)
	{
		this.ctx = ctx;
	}
	
	
	public void beginAnalyzis()
	{
		kdb = new KnowledgeDBManager(ctx);
		Cursor daysCursor = kdb.getDays();
		kdb.closeDB();
		daysCursor.moveToFirst();
		wipeDatabase();
		while(!daysCursor.isAfterLast())
    	{
			int day = daysCursor.getInt(daysCursor.getColumnIndex(KnowledgeDBManager.WEEKDAY));
			Log.i("PMDS Analyzer", "Analyzing day "+ day);
			
			Cursor data = getEntriesCursorONOFF(day);
			Cursor dataForBayes = getEntriesCursorONOFF(day);
			Log.i("PMDS Analyzer", "Found "+ data.getCount() + " ONOFF entries");
			ArrayList<EntryItem> newEntries = createNewEntriesONOFF(data, day, dataForBayes);
			removeUselessData(newEntries);
			
			Cursor data2 = getEntriesCursorCPU(day);
			Cursor dataForBayes2 = getEntriesCursorCPU(day);
			Log.i("PMDS Analyzer", "Found "+ data2.getCount() + " CPU entries");
			ArrayList<EntryItem> newEntries2 = createNewEntriesCPU(data2, day, dataForBayes2);
			removeUselessData(newEntries2);
			
			Cursor data3 = getEntriesCursorScreen(day);
			Cursor dataForBayes3 = getEntriesCursorScreen(day);
			Log.i("PMDS Analyzer", "Found "+ data3.getCount() + " Screen entries");
			ArrayList<EntryItem> newEntries3 = createNewEntriesScreen(data3, day, dataForBayes3);
			removeUselessData(newEntries3);
			
			daysCursor.moveToNext();
    	}
		Log.i("PMDS Analyzer", "Finished analyzis");
	}
	
	public void performCleanup()
	{
		kdb = new KnowledgeDBManager(ctx);
		int deletedCount = kdb.removeEntriesOlderThanMonth();
		kdb.closeDB();
		Log.i("PMDS Analyzer", "Cleanup performed, deleted "+deletedCount+" entries");
	}
	
	private Cursor getEntriesCursorONOFF(int day)
	{
		kdb = new KnowledgeDBManager(ctx);
		Cursor data = kdb.getEntriesForDayONOFF(day);
		kdb.closeDB();
		return data;
	}
	
	private Cursor getEntriesCursorCPU(int day)
	{
		kdb = new KnowledgeDBManager(ctx);
		Cursor data = kdb.getEntriesForDayCPU(day);
		kdb.closeDB();
		return data;
	}
	
	private Cursor getEntriesCursorScreen(int day)
	{
		kdb = new KnowledgeDBManager(ctx);
		Cursor data = kdb.getEntriesForDayScreen(day);
		kdb.closeDB();
		return data;
	}
	
	private ArrayList<EntryItem> createNewEntriesONOFF(Cursor data, int day, Cursor dataForBayes)
	{
		ArrayList<EntryItem> entries = new ArrayList<EntryItem>();
		
		Log.i("PMDS","entries: "+data.getCount());
		BayesClassifier bc = new BayesClassifier(dataForBayes);
		
		int deviceColumn = data.getColumnIndex(KnowledgeDBManager.DEVICE_TYPE);
		int hourColumn = data.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = data.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		
		data.moveToFirst();
		while(!data.isAfterLast())
		{
			int device = data.getInt(deviceColumn);
			int hour = data.getInt(hourColumn);
			int minute = data.getInt(minuteColumn);
			int decidedAction = bc.makeDecision(device, hour, minute);
			entries.add(new EntryItem(device, decidedAction, day, hour, minute));

			data.moveToNext();
		}
		Log.i("PMDS Analyzer", "Created "+entries.size()+" ON/OFF entries for day "+day);
		return entries;
	}
	
	private ArrayList<EntryItem> createNewEntriesCPU(Cursor data, int day, Cursor dataForBayes)
	{
		ArrayList<EntryItem> entries = new ArrayList<EntryItem>();
		
		Log.i("PMDS","entries: "+data.getCount());
		BayesClassifier bc = new BayesClassifier(dataForBayes, Constants.CPU_DEVICE);
		
		int deviceColumn = data.getColumnIndex(KnowledgeDBManager.DEVICE_TYPE);
		int hourColumn = data.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = data.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		
		data.moveToFirst();
		while(!data.isAfterLast())
		{
			int device = data.getInt(deviceColumn);
			int hour = data.getInt(hourColumn);
			int minute = data.getInt(minuteColumn);
			int decidedAction = bc.makeDecision_CPU(hour, minute);
			entries.add(new EntryItem(device, decidedAction, day, hour, minute));

			data.moveToNext();
		}
		Log.i("PMDS Analyzer", "Created "+entries.size()+" CPU entries for day "+day);
		return entries;
	}
	
	private ArrayList<EntryItem> createNewEntriesScreen(Cursor data, int day, Cursor dataForBayes)
	{
		ArrayList<EntryItem> entries = new ArrayList<EntryItem>();
		
		Log.i("PMDS","entries: "+data.getCount());
		BayesClassifier bc = new BayesClassifier(dataForBayes, Constants.SCREEN_DEVICE);
		
		int deviceColumn = data.getColumnIndex(KnowledgeDBManager.DEVICE_TYPE);
		int hourColumn = data.getColumnIndex(KnowledgeDBManager.HOUR_OF_DAY);
		int minuteColumn = data.getColumnIndex(KnowledgeDBManager.MINUTE_OF_HOUR);
		int actionColumn = data.getColumnIndex(KnowledgeDBManager.ACTION_TYPE);
		
		data.moveToFirst();
		while(!data.isAfterLast())
		{
			int device = data.getInt(deviceColumn);
			int hour = data.getInt(hourColumn);
			int minute = data.getInt(minuteColumn);
			int act = data.getInt(actionColumn);
			int decidedAction = bc.makeDecision_Screen(hour, minute, act);
			entries.add(new EntryItem(device, decidedAction, day, hour, minute));

			data.moveToNext();
		}
		Log.i("PMDS Analyzer", "Created "+entries.size()+" Screen entries for day "+day);
		return entries;
	}
	
	//ooohhh, this is ugly
	private void removeUselessData(ArrayList<EntryItem> entries)
	{
		ArrayList<EntryItem> wifiEntries = new ArrayList<EntryItem>();
		ArrayList<EntryItem> cpuEntries = new ArrayList<EntryItem>();
		ArrayList<EntryItem> btEntries = new ArrayList<EntryItem>();
		ArrayList<EntryItem> screenEntries = new ArrayList<EntryItem>();
		ArrayList<EntryItem> rotationEntries = new ArrayList<EntryItem>();
		
		for(int i=0; i<entries.size(); i++)
		{
			EntryItem entry = entries.get(i);
			if(entry.deviceID == Constants.WIFI_DEVICE)
			{
				wifiEntries.add(entry);
			}
			if(entry.deviceID == Constants.SCREEN_DEVICE)
			{
				screenEntries.add(entry);
			}
			if(entry.deviceID == Constants.CPU_DEVICE)
			{
				cpuEntries.add(entry);
			}
			if(entry.deviceID == Constants.BLUETOOTH_DEVICE)
			{
				btEntries.add(entry);
			}
			if(entry.deviceID == Constants.AUTOROTATION)
			{
				rotationEntries.add(entry);
			}
		}
		
		insertToDB(removeDataHelper(wifiEntries));
		insertToDB(removeDataHelper(cpuEntries));
		insertToDB(removeDataHelper(btEntries));
		insertToDB(removeDataHelper(screenEntries));
		insertToDB(removeDataHelper(rotationEntries));
		
	}
	
	private void insertToDB(ArrayList<EntryItem> entries)
	{
		int counter = 0;
		if(entries.size() > 0)
		{
			adb = new AutoSchedulesDBManager(ctx);
			for(int i=0; i<entries.size(); i++)
			{
				EntryItem entry = entries.get(i);
				if(entry != null)
				{
					adb.addScheduleItem(entry.deviceID, entry.action, entry.day, entry.hour, entry.minute, Constants.ACTIVE_SCHEDULE_MODE);
					counter++;
				}
			}
			adb.closeDB();
		}
		Log.i("PMDS Analyzer", "Added "+counter+" entries to schedule database");
	}
	
	private void wipeDatabase()
	{
		adb = new AutoSchedulesDBManager(ctx);
		adb.wipeDB();
		adb.closeDB();
	}
	
	private ArrayList<EntryItem> removeDataHelper(ArrayList<EntryItem> entries)
	{
		ArrayList<EntryItem> result = entries;
		
		long difference = -1;
		 
		if(entries.size() > 0)
		{
			String time1 = entries.get(0).time;
			for(int i=1; i<entries.size(); i++)
			{
				String time2 = entries.get(i).time;
				
				difference = calculateTimeDifference(time1, time2);
				if(difference != -1 && difference < 1200000)
				{
					result.set(i, null); //I need to keep the indexes (didn't want to use map here) so instead of removing, I null the objects 
				}
				else if(difference != -1 && difference >= 1200000)
				{
					time1 = entries.get(i).time; //prev time gets updated here so the gap doesn't keep growing.
				}
			}
		}
		return result;
	}
	
	private long calculateTimeDifference(String time1, String time2)
	{
		long difference = -1;
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		try 
		{
			Date d1 = df.parse(time1);
			Date d2 = df.parse(time2);
			difference = d2.getTime() - d1.getTime();
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
			Log.e("PMDS Analyzer", "Time parsing gone wrong");
		}
		return difference;
	}
}
