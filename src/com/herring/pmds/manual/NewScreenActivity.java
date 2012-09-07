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
import android.widget.SeekBar;
import android.widget.TimePicker;

public class NewScreenActivity extends Activity
{
	private Context ctx;
	private ManualSchedulesDBManager db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_screen); 
	        
	        ctx = getApplicationContext();
	        //db = new ManualSchedulesDBManager(ctx);
	        
	        final TimePicker tp = (TimePicker)findViewById(R.id.screenTP);
	        tp.setIs24HourView(true);
	        Calendar cal=Calendar.getInstance();

	        int hour=cal.get(Calendar.HOUR_OF_DAY);
	        int min=cal.get(Calendar.MINUTE);
	        
	        tp.setCurrentHour(hour);
	        tp.setCurrentMinute(min);
	        
	        
	        Button saveScreenBtn = (Button)findViewById(R.id.saveScreenBtn);
	        saveScreenBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  db = new ManualSchedulesDBManager(ctx);
		    		  saveToDB(tp);
		    		  db.closeDB();
		    		  finish();
		    	  }
	        });
	        //db.closeDB();
	    }
	 
	 private void saveToDB(TimePicker tp)
	 {
		 CheckBox screenActiveCB = (CheckBox)findViewById(R.id.screenActiveCB);
		  

		 int isActive = -1;
		 if(screenActiveCB.isChecked())
		 {
			 isActive = Constants.ACTIVE_SCHEDULE_MODE;
		 }
		 else
		 {
			 isActive = Constants.INACTIVE_SCHEDULE_MODE;
		 }
		  SeekBar screenBar = (SeekBar)findViewById(R.id.screenBar);
		  int action = screenBar.getProgress();
		  
		  int hour = tp.getCurrentHour();
		  int minute = tp.getCurrentMinute();
		  
	
		  boolean[] days = new boolean[8];
		  for(int i=1;i<8;i++)
		  {
			  days[i]=false;
		  }
		  
		  CheckBox screenMonCB = (CheckBox)findViewById(R.id.screenMonCB);
		  CheckBox screenTueCB = (CheckBox)findViewById(R.id.screenTueCB);
		  CheckBox screenWedCB = (CheckBox)findViewById(R.id.screenWedCB);
		  CheckBox screenThuCB = (CheckBox)findViewById(R.id.screenThuCB);
		  CheckBox screenFriCB = (CheckBox)findViewById(R.id.screenFriCB);
		  CheckBox screenSatCB = (CheckBox)findViewById(R.id.screenSatCB);
		  CheckBox screenSunCB = (CheckBox)findViewById(R.id.screenSunCB);
		  
		  
		  if(screenMonCB.isChecked()) days[Calendar.MONDAY] = true;
		  if(screenTueCB.isChecked()) days[Calendar.TUESDAY] = true;
		  if(screenWedCB.isChecked()) days[Calendar.WEDNESDAY] = true;
		  if(screenThuCB.isChecked()) days[Calendar.THURSDAY] = true;
		  if(screenFriCB.isChecked()) days[Calendar.FRIDAY] = true;
		  if(screenSatCB.isChecked()) days[Calendar.SATURDAY] = true;
		  if(screenSunCB.isChecked()) days[Calendar.SUNDAY] = true;
		  Scheduler scheduler = new Scheduler(ctx);
		  for(int i=1;i<8;i++)
		  {
			  if(days[i]==true)
			  {
				  long id = db.addScheduleItem(Constants.SCREEN_DEVICE, action, i, hour, minute, null, isActive);
				  if(isActive==1) scheduler.scheduleNewItem(Constants.MANUAL_MODE, id, Constants.SCREEN_DEVICE, action, i, hour, minute);
			  }
		  }
	 }
}
