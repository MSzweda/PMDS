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

/*
 * those New*Activity classes are the ugliest pieces of code I've ever written
 * 
 */
public class NewAutorotateActivity extends Activity
{
	private Context ctx;
	private ManualSchedulesDBManager db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_autorotate); 
	        
	        ctx = getApplicationContext();
	        //db = new ManualSchedulesDBManager(ctx);
	        
	        final TimePicker tp = (TimePicker)findViewById(R.id.rotTP);
	        tp.setIs24HourView(true);
	        
	        Calendar cal=Calendar.getInstance();

	        int hour=cal.get(Calendar.HOUR_OF_DAY);
	        int min=cal.get(Calendar.MINUTE);
	        
	        tp.setCurrentHour(hour);
	        tp.setCurrentMinute(min);
	        
	        
	        Button saveRotBtn = (Button)findViewById(R.id.saveRotBtn);
	        saveRotBtn.setOnClickListener(new OnClickListener() 
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
		 CheckBox rotActiveCB = (CheckBox)findViewById(R.id.rotActiveCB);
		  int isActive = -1;
		 if(rotActiveCB.isChecked())
		 {
			 isActive = Constants.ACTIVE_SCHEDULE_MODE;
		 }
		 else
		 {
			 isActive = Constants.INACTIVE_SCHEDULE_MODE;
		 }
		 
		 //boolean isActive = rotActiveCB.isChecked();
		 
		  RadioButton rotOnRB = (RadioButton)findViewById(R.id.rotOnRB);
		 
		  int action = Constants.TURN_OFF_ACTION;
		  if(rotOnRB.isChecked())
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
		  
		  CheckBox rotMonCB = (CheckBox)findViewById(R.id.rotMonCB);
		  CheckBox rotTueCB = (CheckBox)findViewById(R.id.rotTueCB);
		  CheckBox rotWedCB = (CheckBox)findViewById(R.id.rotWedCB);
		  CheckBox rotThuCB = (CheckBox)findViewById(R.id.rotThuCB);
		  CheckBox rotFriCB = (CheckBox)findViewById(R.id.rotFriCB);
		  CheckBox rotSatCB = (CheckBox)findViewById(R.id.rotSatCB);
		  CheckBox rotSunCB = (CheckBox)findViewById(R.id.rotSunCB);
		  
		  
		  if(rotMonCB.isChecked()) days[Calendar.MONDAY] = true;
		  if(rotTueCB.isChecked()) days[Calendar.TUESDAY] = true;
		  if(rotWedCB.isChecked()) days[Calendar.WEDNESDAY] = true;
		  if(rotThuCB.isChecked()) days[Calendar.THURSDAY] = true;
		  if(rotFriCB.isChecked()) days[Calendar.FRIDAY] = true;
		  if(rotSatCB.isChecked()) days[Calendar.SATURDAY] = true;
		  if(rotSunCB.isChecked()) days[Calendar.SUNDAY] = true;
		  Scheduler scheduler = new Scheduler(ctx);
		  for(int i=1;i<8;i++)
		  {
			  if(days[i]==true)
			  {
				  long id = db.addScheduleItem(Constants.AUTOROTATION, action, i, hour, minute, null, isActive);
				  if(isActive==1) scheduler.scheduleNewItem(Constants.MANUAL_MODE, id, Constants.AUTOROTATION, action, i, hour, minute);
			  }
		  }
	 }
}
