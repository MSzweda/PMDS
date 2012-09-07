package com.herring.pmds.manual;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ManualSchedulesDBManager 
{

	private SQLiteDatabase db;
	//database name
    public final String DBNAME = "manual_schedules.db";
    public final int DBVERSION = 1;
    
    //logger table components
    public final String SCHEDULES_TABLE = "schedules"; 	//table name
    public final static String SCHEDULE_ROWID = "_id";			//row id
    public final static String DEVICE_TYPE = "device";			//device id, i.e. 0=wifi, 1=screen
    public final static String ACTION_TYPE = "action";			//action, i.e. 0=turn off, 1=turn on, or brightness level
    public final static String WEEKDAY = "weekday";			//what day of the week it should be applied on, MONDAY=0, SUNDAY=6
    public final static String HOUR_OF_DAY = "hour";			//HOUR - what time it should be applied at
    public final static String MINUTE_OF_HOUR = "minute";		//MINUTE - what time it should be applied at
    public final String SPECIFIC_DATE = "date";			//0 if not set, if set it means it will be a one-time only thing
    public final static String IS_ACTIVE = "active";			//1 if element is active, 0 if not


	//private Context context;
    private SchedulesDatabaseHelper helper;
    
    //public access to database from other classes
    public ManualSchedulesDBManager(Context context)
	{
		//this.context = context;
 
		// create or open the database
		helper = new SchedulesDatabaseHelper(context);
		this.db = helper.getWritableDatabase();
	}
    
    public void closeDB()
	{
		db.close();
	}
    
    //this class handles the database creation, opening and upgrading
    private class SchedulesDatabaseHelper extends SQLiteOpenHelper
	{
        //the constructor of this class
        public SchedulesDatabaseHelper(Context context)
        {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            //create the database 
            String createBatInfoTable =
                    "CREATE TABLE "+
                    SCHEDULES_TABLE +
                    " (" +
                    SCHEDULE_ROWID 	+ " integer primary key autoincrement not null," +
                    DEVICE_TYPE 	+ " integer," +
                    ACTION_TYPE 	+ " integer," +
                    WEEKDAY 		+ " integer," +
                    HOUR_OF_DAY 	+ " integer," +
                    MINUTE_OF_HOUR 	+ " integer," +
                    SPECIFIC_DATE 	+ " TIMESTAMP," +
                    IS_ACTIVE 		+ " integer" +
                    ");";
            

            // execute the queries and create the tables
            db.execSQL(createBatInfoTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // I don't plan on upgrading so I can leave it blank for now
        }

	} // end of the SchedulesDatabaseHelper private class

    
    //add an item to the database
    public long addScheduleItem(int deviceType, int actionType, int weekday, int hour, int minute, String timestring, int isActive)
    {
    	long id = -1;
    	ContentValues values = new ContentValues();
        values.put(DEVICE_TYPE, deviceType);
        values.put(ACTION_TYPE, actionType);
        values.put(WEEKDAY, weekday);
        values.put(HOUR_OF_DAY, hour);
        values.put(MINUTE_OF_HOUR, minute);
        values.put(SPECIFIC_DATE, timestring);
        values.put(IS_ACTIVE, isActive);
        
        try
        {
            id = db.insert(SCHEDULES_TABLE, null, values);
        }
        catch(Exception e)	{
            Log.e("PMDS DB ERROR", e.toString());
            e.printStackTrace();
        }
        
        return id;
    }
    
    //delete an item from the database
    public int deleteScheduleItem(int itemID)
    {
    	Log.i("PMDSManualDBMan", "Deleting item id: "+itemID);
    	return db.delete(SCHEDULES_TABLE, SCHEDULE_ROWID +"="+itemID, null);
    }
    
    //delete all schedules for given device
    public int deleteScheduleItemsForDevice(int deviceType)
    {
    	return db.delete(SCHEDULES_TABLE, DEVICE_TYPE +"="+deviceType, null);
    }
    
    //delete all schedules for given day
    public int deleteScheduleItemsForDate(int weekday)
    {
    	return db.delete(SCHEDULES_TABLE, WEEKDAY +"="+weekday, null);
    }
    
    //delete schedule for specific date
    public int deleteSpecificDateSchedule(String timestring)
    {
    	return db.delete(SCHEDULES_TABLE, SPECIFIC_DATE +"=\""+timestring+"\"", null);
    }
    
    //change the activity state of an item
    public int changeActiveState(int itemID, int state)
    {
    	Log.i("PMDSManualDBMan", "Changing activity state for schedule id: "+itemID);
    	ContentValues values = new ContentValues();
    	values.put(IS_ACTIVE, state);
    	return db.update(SCHEDULES_TABLE, values, SCHEDULE_ROWID + "=" + itemID, null);
    }
    
    //change activity state of all items for given device
    public int changeActiveStateForDeviceSchedules(int deviceType, int state)
    {
    	Log.i("PMDSManualDBMan", "Changing activity state for device type: "+deviceType);
    	ContentValues values = new ContentValues();
    	values.put(IS_ACTIVE, state);
    	return db.update(SCHEDULES_TABLE, values, DEVICE_TYPE + "=" + deviceType, null);
    }
    
    //change activity state of all items for given day of the week
    public int changeActiveStateForDateSchedules(int weekday, int state)
    {
    	Log.i("PMDSManualDBMan", "Changing activity state for day: "+weekday);
    	ContentValues values = new ContentValues();
    	values.put(IS_ACTIVE, state);
    	return db.update(SCHEDULES_TABLE, values, WEEKDAY + "=" + weekday, null);
    }
    
    //check activity state
    public int checkActivityState(int sid)
    {
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, new String[] {IS_ACTIVE},
    			SCHEDULE_ROWID + "=" +sid, null, WEEKDAY, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		return cursor.getInt(0);
    }
    
    //retrieve all schedules for given device
    public Cursor getAllSchedulesByDevice(int deviceType)
    {
    	/*Cursor cursor = db.rawQuery("select * from "+SCHEDULES_TABLE+" where "+DEVICE_TYPE+"="+deviceType+" order by "+WEEKDAY+", "+HOUR_OF_DAY+";",null);
    	if(cursor!=null)
    	{
    		cursor.moveToFirst();
    	}
    	return cursor;*/
    	
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, null,
    			DEVICE_TYPE + "=" +deviceType, null, WEEKDAY, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
    }
    
    //retrieve all schedules for given day
    public Cursor getAllSchedulesByDate(int weekday)
    {	
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, null,
    			WEEKDAY + "=" +weekday, null, null, null, HOUR_OF_DAY, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
    }
    
    //retrieve all active schedules
    public Cursor getAllActiveSchedules()
    {
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, null,
    			IS_ACTIVE + "= 1", null, null, null, null, null);
    	if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
    }
    

    public int getClosestWeekday(int weekday)
    {
    	int answer = -1;
    	Cursor cursor = db.rawQuery("select min(weekday) from "+SCHEDULES_TABLE+" where "+WEEKDAY+">="+weekday+";", null);
    	cursor.moveToFirst();
    	if(cursor.getCount()>0) 
		{
    		answer = cursor.getInt(0);
		}
    	else
    	{
    		cursor = db.rawQuery("select min(weekday) from "+SCHEDULES_TABLE+";", null);
    		cursor.moveToFirst();
    		if(cursor.getCount()>0) answer = cursor.getInt(0);
    	}
		return answer;
    	
    }
    
    public int getClosestHour(int weekday, int hour, int minute)
    {
    	int answer = -1;
    	Cursor cursor = db.rawQuery("select min(hour) from "+SCHEDULES_TABLE+" where "+WEEKDAY+">="+weekday+" and "+HOUR_OF_DAY+">=hour;", null);
    	cursor.moveToFirst();
    	return answer;
    }
    
    public int wipeDB()
    {
    	Log.i("PMDSManualDBManager", "Database wiped");
    	return db.delete(SCHEDULES_TABLE, null, null);
    }
    
    public Cursor getDevices()
    {
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, new String[] {DEVICE_TYPE},
    			null, null, null, null, null, null);
    	if (cursor != null) {
			cursor.moveToFirst();
		}
    	return cursor;
    }
    
    public Cursor getDays()
    {
    	Cursor cursor = db.query(true, SCHEDULES_TABLE, new String[] { WEEKDAY },
    			null, null, null, null, null, null);
    	if (cursor != null) {
			cursor.moveToFirst();
		}
    	return cursor;
    }
    
    
    

}
