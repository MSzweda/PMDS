package com.herring.pmds.manual;


import java.util.Calendar;

import com.herring.pmds.R;
import com.herring.pmds.tools.Constants;
import com.herring.pmds.tools.Scheduler;


import android.app.Activity;
import android.content.Context;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TimePicker;

public class NewWifiActivity extends Activity
{

	private Context ctx;
	private ManualSchedulesDBManager db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_wifi); 
	        
	        ctx = getApplicationContext();
	        
	        
	        final TimePicker tp = (TimePicker)findViewById(R.id.wifiTP);
	        tp.setIs24HourView(true);
	        
	        Calendar cal=Calendar.getInstance();

	        int hour=cal.get(Calendar.HOUR_OF_DAY);
	        int min=cal.get(Calendar.MINUTE);
	        
	        tp.setCurrentHour(hour);
	        tp.setCurrentMinute(min);
	        
	        Button saveWifiBtn = (Button)findViewById(R.id.saveWifiBtn);
	        saveWifiBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  db = new ManualSchedulesDBManager(ctx);
		    		  saveToDB(tp);
		    		  db.closeDB();
		    		  finish();
		    	  }
	        });
	        
	    }
	 
	 private void saveToDB(TimePicker tp)
	 {
		 CheckBox wifiActiveCB = (CheckBox)findViewById(R.id.wifiActiveCB);
		  
		 
		 int isActive = -1;
		 if(wifiActiveCB.isChecked())
		 {
			 isActive = Constants.ACTIVE_SCHEDULE_MODE;
		 }
		 else
		 {
			 isActive = Constants.INACTIVE_SCHEDULE_MODE;
		 }
		 
		  RadioButton wifiOnRB = (RadioButton)findViewById(R.id.wifiOnRB);
		 
		  int action = Constants.TURN_OFF_ACTION;
		  if(wifiOnRB.isChecked())
		  {
			  action = Constants.TURN_ON_ACTION;
		  }
		  int hour = tp.getCurrentHour();
		  int minute = tp.getCurrentMinute();
		  
	
		  boolean[] days = new boolean[8];
		  for(int i=1;i<8;i++)
		  {
			  days[i]=false;
		  }
		  
		  CheckBox wifiMonCB = (CheckBox)findViewById(R.id.wifiMonCB);
		  CheckBox wifiTueCB = (CheckBox)findViewById(R.id.wifiTueCB);
		  CheckBox wifiWedCB = (CheckBox)findViewById(R.id.wifiWedCB);
		  CheckBox wifiThuCB = (CheckBox)findViewById(R.id.wifiThuCB);
		  CheckBox wifiFriCB = (CheckBox)findViewById(R.id.wifiFriCB);
		  CheckBox wifiSatCB = (CheckBox)findViewById(R.id.wifiSatCB);
		  CheckBox wifiSunCB = (CheckBox)findViewById(R.id.wifiSunCB);
		  
		  
		  if(wifiMonCB.isChecked()) days[Calendar.MONDAY] = true;
		  if(wifiTueCB.isChecked()) days[Calendar.TUESDAY] = true;
		  if(wifiWedCB.isChecked()) days[Calendar.WEDNESDAY] = true;
		  if(wifiThuCB.isChecked()) days[Calendar.THURSDAY] = true;
		  if(wifiFriCB.isChecked()) days[Calendar.FRIDAY] = true;
		  if(wifiSatCB.isChecked()) days[Calendar.SATURDAY] = true;
		  if(wifiSunCB.isChecked()) days[Calendar.SUNDAY] = true;
		  Scheduler scheduler = new Scheduler(ctx);
		  for(int i=1;i<8;i++)
		  {
			  if(days[i]==true)
			  {
				  long id = db.addScheduleItem(Constants.WIFI_DEVICE, action, i, hour, minute, null, isActive);
				  if(isActive==1) scheduler.scheduleNewItem(Constants.MANUAL_MODE, id, Constants.WIFI_DEVICE, action, i, hour, minute);
			  }
		  }
	 }
}
