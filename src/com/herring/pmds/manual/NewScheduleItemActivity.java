package com.herring.pmds.manual;

import com.herring.pmds.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewScheduleItemActivity extends Activity
{
	 @Override
	    public void onCreate(Bundle savedInstanceState) 
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.new_schedule_item);     
	        
	        Button wifiBtn = (Button)findViewById(R.id.wifiBtn);
	        wifiBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  Intent newWifiScreen = new Intent(NewScheduleItemActivity.this, NewWifiActivity.class);
		    		  startActivity(newWifiScreen);
			        }
			});
	        
	        Button cpuBtn = (Button)findViewById(R.id.cpuBtn);
	        cpuBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  Intent newCpuScreen = new Intent(NewScheduleItemActivity.this, NewCPUActivity.class);
		    		  startActivity(newCpuScreen);
			        }
			});
	        
	        Button rotationBtn = (Button)findViewById(R.id.rotationBtn);
	        rotationBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  Intent newRotScreen = new Intent(NewScheduleItemActivity.this, NewAutorotateActivity.class);
		    		  startActivity(newRotScreen);
			        }
			});
	        
	        Button screenBtn = (Button)findViewById(R.id.screenBtn);
	        screenBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  Intent newScreenScreen = new Intent(NewScheduleItemActivity.this, NewScreenActivity.class);
		    		  startActivity(newScreenScreen);
			        }
			});
	        
	        Button bluetoothBtn = (Button)findViewById(R.id.bluetoothBtn);
	        bluetoothBtn.setOnClickListener(new OnClickListener() 
	        {
		    	  public void onClick(View v) 
		    	  {
		    		  Intent newBTScreen = new Intent(NewScheduleItemActivity.this, NewBluetoothActivity.class);
		    		  startActivity(newBTScreen);
			        }
			});

	    }

}
