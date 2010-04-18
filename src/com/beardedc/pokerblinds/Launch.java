package com.beardedc.pokerblinds;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class Launch extends Activity 
{
	private TextView m_txtTimer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.main);
        // Here get the elements we need
        m_txtTimer = (TextView)findViewById(R.id.TextTimer);
        goTimer();
    }
    
    private void goTimer()
    {
    	new CountDownTimer(600000, 1000)
    	{
    		public void onTick(long msUntilDone)
    		{
    			m_txtTimer.setText("Time: " + msUntilDone / 1000);
    		}
    		public void onFinish()
    		{
    			m_txtTimer.setText("All done");
    		}
    	}.start();
    }
}