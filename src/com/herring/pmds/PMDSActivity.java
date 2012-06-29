package com.herring.pmds;

import com.herring.pmds.auto.ADateScheduleActivity;
import com.herring.pmds.auto.ADeviceScheduleActivity;
import com.herring.pmds.learning.Learner;
import com.herring.pmds.manual.MDateScheduleActivity;
import com.herring.pmds.manual.MDeviceScheduleActivity;
import com.herring.pmds.manual.ManualSchedulesDBManager;
import com.herring.pmds.manual.NewScheduleItemActivity;
import com.herring.pmds.tools.Constants;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

//Power Managing Device Scheduler

public class PMDSActivity extends Activity 
{

	/*
	 * 
	 * TODO
	 * 
	 * Analyzer - get that knowledge db and analyze it to make schedules
	 * Switching between schedule modes - how to make them inactive? Cancel alarm manager somehow?
	 * Find out how to determine whether it's night or not - give option to shut all devices at night
	 * If enough time - improve manual adding of schedules, namely layouts
	 * comment all the code
	 * 
	 */
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        //radio buttons
        RadioGroup radioG = (RadioGroup)findViewById(R.id.mainRadioGroup);
        radioG.setOnCheckedChangeListener(new OnCheckedChangeListener() 
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) 
            { 
            	if(checkedId == R.id.manualRBtn)
            	{
            		setPreference(false);
            	}
            	else if(checkedId == R.id.autoRBtn)
            	{
            		setPreference(true);
            	}

            }
        });
        
        setRadioButtons();
        
        //checkbox
        CheckBox cB = (CheckBox)findViewById(R.id.learningBtn);
        cB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
            	Learner lrn = new Learner(getApplicationContext());
                if (isChecked)
                {
                	setLearningMode(true);
                	lrn.startLearner();
                }
                else
                {
                	setLearningMode(false);
                	lrn.stopLearner();
                }

            }
        });
        
        setLearningBox();
        
        //menu buttons
        Button manualDeviceBtn = (Button)findViewById(R.id.manualDeviceBtn);
        manualDeviceBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  Intent manualDeviceScreen = new Intent(PMDSActivity.this, MDeviceScheduleActivity.class);
	    		  startActivity(manualDeviceScreen);
	    	  }
      	});
        
        Button manualDateBtn = (Button)findViewById(R.id.manualDateBtn);
        manualDateBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  Intent manualDateScreen = new Intent(PMDSActivity.this, MDateScheduleActivity.class);
	    		  startActivity(manualDateScreen);
	    	  }
      	});
        
        Button newSchedBtn = (Button)findViewById(R.id.newSchedBtn);
        newSchedBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  Intent newSchedScreen = new Intent(PMDSActivity.this, NewScheduleItemActivity.class);
	    		  startActivity(newSchedScreen);
	    	  }
      	});
        
        Button autoDeviceBtn = (Button)findViewById(R.id.autoDeviceBtn);
        autoDeviceBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  Intent autoDeviceScreen = new Intent(PMDSActivity.this, ADeviceScheduleActivity.class);
	    		  startActivity(autoDeviceScreen);
	    	  }
        });
        
        Button autoDateBtn = (Button)findViewById(R.id.autoDateBtn);
        autoDateBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  Intent autoDateScreen = new Intent(PMDSActivity.this, ADateScheduleActivity.class);
	    		  startActivity(autoDateScreen);
	    	  }
        });
        
        //temp dev options
        Button wipeBtn = (Button)findViewById(R.id.wipeBtn);
        wipeBtn.setOnClickListener(new OnClickListener() 
        {
	    	  public void onClick(View v) 
	    	  {
	    		  ManualSchedulesDBManager db = new ManualSchedulesDBManager(getApplicationContext());
	    		  db.wipeDB();
	    		  db.closeDB();
	    	  }
        });

    }
    
    
    /*
     * set the radio buttons
     */
    private void setRadioButtons()
    {
    	if(!checkPreferences())
    	{
    		RadioButton radioButton = (RadioButton) findViewById(R.id.manualRBtn);
    		radioButton.setChecked(true);
    	}
    	else
    	{
    		RadioButton radioButton = (RadioButton) findViewById(R.id.autoRBtn);
    		radioButton.setChecked(true);
    	}
    }
    
    /*
     * set the checkbox
     */
    private void setLearningBox()
    {
    	CheckBox cB = (CheckBox) findViewById(R.id.learningBtn);
    	if(checkLearningMode())
    	{
    		cB.setChecked(true);
    	}
    	else
    	{
    		cB.setChecked(false);
    	}
    }
    
    /*
     * check if preferences exist, and if not - create them
     */
    private boolean checkPreferences()
    {
    	SharedPreferences PMDSSettings = getSharedPreferences(Constants.PMDS_PREFS, MODE_PRIVATE);
    	if(!PMDSSettings.contains("PMDS_MODE"))
    	{
    		SharedPreferences.Editor prefEditor = PMDSSettings.edit();
    		//false = manual
    		//true = automatic
    		prefEditor.putBoolean("PMDS_MODE", false);
    		prefEditor.commit();  
    		return false;
    	}
    	else
    	{
    		if(!PMDSSettings.getBoolean("PMDS_MODE", false))
    		{
    			return false;
    		}
    		else
    		{
    			return true;
    		}
    	}
    }
    
    /*
     * check if preference for learning mode exists
     */
    public boolean checkLearningMode()
    {
    	SharedPreferences PMDSSettings = getSharedPreferences(Constants.PMDS_PREFS, MODE_PRIVATE);
    	if(!PMDSSettings.contains("LEARNING_MODE"))
    	{
    		SharedPreferences.Editor prefEditor = PMDSSettings.edit();
    		prefEditor.putBoolean("LEARNING_MODE", false);
    		prefEditor.commit();  
    		return false;
    	}
    	else
    	{
    		if(!PMDSSettings.getBoolean("LEARNING_MODE", false))
    		{
    			return false;
    		}
    		else
    		{
    			return true;
    		}
    	}
    }
    
    /*
     * set preference for learning mode
     */
    private void setLearningMode(boolean mode)
    {
    	SharedPreferences PMDSSettings = getSharedPreferences(Constants.PMDS_PREFS, MODE_PRIVATE);
    	SharedPreferences.Editor prefEditor = PMDSSettings.edit();
    	prefEditor.putBoolean("LEARNING_MODE", mode);
		prefEditor.commit();  
    }
    
    /*
     * set preference for mode
     * rButton - false if manual, true if automatic
     */
    private void setPreference(boolean rButton)
    {
    	SharedPreferences PMDSSettings = getSharedPreferences(Constants.PMDS_PREFS, MODE_PRIVATE);
    	SharedPreferences.Editor prefEditor = PMDSSettings.edit();
    	prefEditor.putBoolean("PMDS_MODE", rButton);
		prefEditor.commit();  
    }
    

}