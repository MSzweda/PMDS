package com.herring.pmds.devices;


import com.herring.pmds.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
public class DummyActivity extends Activity
{
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy);
        Integer action = (Integer) getIntent().getExtras().get("action");
        Context context = getApplicationContext();
        ScreenDevice sd = new ScreenDevice();
		sd.setScreenBrightness(action, context, this);
		finish();
 
    }
}
