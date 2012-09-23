package com.herring.pmds.devices;


import com.herring.pmds.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager.LayoutParams;
public class DummyActivity extends Activity
{
	/** Called when the activity is first created. */
	
	private static final int DELAYED_MESSAGE = 1;

    private Handler handler;

    
    @SuppressLint("HandlerLeak")
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == DELAYED_MESSAGE) {
                	DummyActivity.this.finish();
                }
                super.handleMessage(msg);
            }
        };

        
        Integer action = (Integer) getIntent().getExtras().get("action");
        Log.i("PMDS DummyActivity", "New brightness level: "+action);
        Context context = getApplicationContext();
        
        ScreenDevice sd = new ScreenDevice();
		sd.setScreenBrightness(action, context/*, this*/);
		
		LayoutParams layoutpars = getWindow().getAttributes();
		layoutpars.screenBrightness = action / (float) 255 ;
		getWindow().setAttributes(layoutpars);
		
		Message message = handler.obtainMessage(DELAYED_MESSAGE);
        handler.sendMessageDelayed(message,1000); 
    }
}
