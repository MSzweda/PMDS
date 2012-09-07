package com.herring.pmds.manual;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.herring.pmds.R;
import com.herring.pmds.tools.Constants;
import com.herring.pmds.tools.Scheduler;

public class NewCPUActivity extends Activity
{
	private Context ctx;
	private ManualSchedulesDBManager db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_cpu); 
	        
	        ctx = getApplicationContext();
	       // db = new ManualSchedulesDBManager(ctx);
	        
	        final TimePicker tp = (TimePicker)findViewById(R.id.cpuTP);
	        tp.setIs24HourView(true);
	        
	        Calendar cal=Calendar.getInstance();

	        int hour=cal.get(Calendar.HOUR_OF_DAY);
	        int min=cal.get(Calendar.MINUTE);
	        
	        tp.setCurrentHour(hour);
	        tp.setCurrentMinute(min);
	        
	        Button saveCpuBtn = (Button)findViewById(R.id.saveCpuBtn);
	        saveCpuBtn.setOnClickListener(new OnClickListener() 
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
		 CheckBox cpuActiveCB = (CheckBox)findViewById(R.id.cpuActiveCB);
		  
		 int isActive = -1;
		 if(cpuActiveCB.isChecked())
		 {
			 isActive = Constants.ACTIVE_SCHEDULE_MODE;
		 }
		 else
		 {
			 isActive = Constants.INACTIVE_SCHEDULE_MODE;
		 }
		 
		  int action = Constants.ONDEMAND_MODE;
		  
		  Spinner spinner = (Spinner)findViewById(R.id.cpuSpinner);
		  String mode = (String)spinner.getSelectedItem();
		  if(mode.contentEquals(Constants.CONSERVATIVE_MODE_STR)) action=Constants.CONSERVATIVE_MODE;
		  if(mode.contentEquals(Constants.POWERSAVE_MODE_STR)) action=Constants.POWERSAVE_MODE;
		  
		  int hour = tp.getCurrentHour();
		  int minute = tp.getCurrentMinute();
		  
	
		  boolean[] days = new boolean[8];
		  for(int i=1;i<8;i++)
		  {
			  days[i]=false;
		  }
		  
		  CheckBox cpuMonCB = (CheckBox)findViewById(R.id.cpuMonCB);
		  CheckBox cpuTueCB = (CheckBox)findViewById(R.id.cpuTueCB);
		  CheckBox cpuWedCB = (CheckBox)findViewById(R.id.cpuWedCB);
		  CheckBox cpuThuCB = (CheckBox)findViewById(R.id.cpuThuCB);
		  CheckBox cpuFriCB = (CheckBox)findViewById(R.id.cpuFriCB);
		  CheckBox cpuSatCB = (CheckBox)findViewById(R.id.cpuSatCB);
		  CheckBox cpuSunCB = (CheckBox)findViewById(R.id.cpuSunCB);
		  
		  
		  if(cpuMonCB.isChecked()) days[Calendar.MONDAY] = true;
		  if(cpuTueCB.isChecked()) days[Calendar.TUESDAY] = true;
		  if(cpuWedCB.isChecked()) days[Calendar.WEDNESDAY] = true;
		  if(cpuThuCB.isChecked()) days[Calendar.THURSDAY] = true;
		  if(cpuFriCB.isChecked()) days[Calendar.FRIDAY] = true;
		  if(cpuSatCB.isChecked()) days[Calendar.SATURDAY] = true;
		  if(cpuSunCB.isChecked()) days[Calendar.SUNDAY] = true;
		  Scheduler scheduler = new Scheduler(ctx);
		  for(int i=1;i<8;i++)
		  {
			  if(days[i]==true)
			  {
				  long id = db.addScheduleItem(Constants.CPU_DEVICE, action, i, hour, minute, null, isActive);
				  if(isActive==1) scheduler.scheduleNewItem(Constants.MANUAL_MODE, id, Constants.CPU_DEVICE, action, i, hour, minute);
			  }
		  }
	 }

}
