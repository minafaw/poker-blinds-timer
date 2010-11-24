package com.beardedc.pokerblinds;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.TextView;

public class Launch extends Activity implements OnInitListener, IReturnFinished
{
	private TextView m_txtTimer;
	private TextView m_BlindBig;
	private TextView m_BlindSmall;
	private TextToSpeech m_tts;
	private CountdownTimerComplex m_timer;
	private int m_secondsRemaining;
	private AppSettings m_settings;
	private int m_iMultiplierToConvertMinutesToSeconds = 60;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver mReceiver = new ScreenReceiver();
		registerReceiver(mReceiver, filter);

		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		m_txtTimer = (TextView) findViewById(R.id.TextTimer);
		m_BlindBig = (TextView) findViewById(R.id.textViewBigBlind);
		m_BlindSmall = (TextView) findViewById(R.id.TextViewSmallBlind);
		
		updateBlinds(m_settings);
		goTimer((int) m_settings.getMinutes() * m_iMultiplierToConvertMinutesToSeconds);
	}
	
	private void updateBlinds(AppSettings a)
	{
		updateTextView("Big Blind is   : " + a.getCurrentBigBlind(), m_BlindBig);
		updateTextView("Small Blind is : " + (a.getCurrentBigBlind() /2), m_BlindSmall);
	}
	
	private void updateTextView(String toOutput, TextView toUpdate)
	{
		toUpdate.setText(toOutput);
	}

	public void onPause()
	{
		// when the screen is about to turn off
		if (ScreenReceiver.wasScreenOn) {
			// this is the case when onPause() is called by the system due to a screen state change
			// update the time remaining from the timer class.
			m_secondsRemaining = m_timer.cancel_returnSecondsRemaining();
			
			// TODO: start some system alarm thing
			// int iMinutesRemaining = m_secondsRemaining / 60;
			
			System.out.println("SCREEN TURNED OFF");
		}
		super.onPause();
	}
    
    public void onResume()
    {
        // only when screen turns on
        if (!ScreenReceiver.wasScreenOn)
        {
        	goTimer(m_secondsRemaining);
            System.out.println("SCREEN TURNED ON");
        }
        super.onResume();
    }
    
    public void onStop()
    {
    	Log.d(getPackageName(), "onStop");
    	super.onStop();
    }
    
    public void onRestart()
    {
    	Log.d(getPackageName(), "onRestart");
    	super.onRestart();
    }
    
    public void onDestroy()
    {
    	if (m_tts != null)
    	{
	    	m_tts.shutdown();
	    	m_tts = null;
    	}
    	if (m_timer != null)
    		m_timer.cancel();
    	Log.d(getPackageName(), "onDestroy");
    	super.onDestroy();
    }
    
    /* Info: the countdown timer
     * continues to function even if the application is not in
     * the foreground and the speech will be set if the
     * application is not running.
     */
	private void goTimer(int secondsToCountDown)
    {
    	m_timer = new CountdownTimerComplex(secondsToCountDown, m_txtTimer, this);
    	m_timer.startTiming();
    	
    }
	
	public void onInit(int i)
	{
		
	}

	public void jobDone()
	{	
		// increase blinds
		m_settings.setCurrentBigBlind( m_settings.getCurrentBigBlind() * 2);
		
		// update the UI
		updateBlinds(m_settings);
		
		// notify user
		vibrateThePhone();		
		
		// restart timer
		goTimer((int) m_settings.getMinutes() * m_iMultiplierToConvertMinutesToSeconds);
		
	}
	
	private void vibrateThePhone()
	{
		// TODO: justin to complete this section later
		/*
		Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		long[] lVibratePattern = {100,50,100,50, 100,50, 100,50, 100,50, 100,50, 100,50, 100,50, 100,50};
		v.vibrate(lVibratePattern, -1);
		*/
	}
	
}