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

public class NewBluetoothActivity extends Activity
{
	Context ctx;
	ManualSchedulesDBManager db;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_bluetooth); 
	        
	        ctx = getApplicationContext();
	        //db = new ManualSchedulesDBManager(ctx);
	        
	        final TimePicker tp = (TimePicker)findViewById(R.id.btTP);
	        tp.setIs24HourView(true);
	        
	        Button saveBtBtn = (Button)findViewById(R.id.saveBtBtn);
	        saveBtBtn.setOnClickListener(new OnClickListener() 
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
		 CheckBox btActiveCB = (CheckBox)findViewById(R.id.btActiveCB);
		  
		 boolean isActive = btActiveCB.isChecked();
		  RadioButton btOnRB = (RadioButton)findViewById(R.id.btOnRB);
		 
		  int action = Constants.TURN_OFF_ACTION;
		  if(btOnRB.isChecked())
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
		  
		  CheckBox btMonCB = (CheckBox)findViewById(R.id.btMonCB);
		  CheckBox btTueCB = (CheckBox)findViewById(R.id.btTueCB);
		  CheckBox btWedCB = (CheckBox)findViewById(R.id.btWedCB);
		  CheckBox btThuCB = (CheckBox)findViewById(R.id.btThuCB);
		  CheckBox btFriCB = (CheckBox)findViewById(R.id.btFriCB);
		  CheckBox btSatCB = (CheckBox)findViewById(R.id.btSatCB);
		  CheckBox btSunCB = (CheckBox)findViewById(R.id.btSunCB);
		  
		  
		  if(btMonCB.isChecked()) days[Calendar.MONDAY] = true;
		  if(btTueCB.isChecked()) days[Calendar.TUESDAY] = true;
		  if(btWedCB.isChecked()) days[Calendar.WEDNESDAY] = true;
		  if(btThuCB.isChecked()) days[Calendar.THURSDAY] = true;
		  if(btFriCB.isChecked()) days[Calendar.FRIDAY] = true;
		  if(btSatCB.isChecked()) days[Calendar.SATURDAY] = true;
		  if(btSunCB.isChecked()) days[Calendar.SUNDAY] = true;
		  Scheduler scheduler = new Scheduler(ctx);
		  for(int i=1;i<8;i++)
		  {
			  if(days[i]==true)
			  {
				  long id = db.addScheduleItem(Constants.BLUETOOTH_DEVICE, action, i, hour, minute, null, isActive);
				  if(isActive) scheduler.scheduleNewItem(id, Constants.BLUETOOTH_DEVICE, action, i, hour, minute);
			  }
		  }
	 }
}
