package com.beardedc.pokerblinds;

import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Launch extends Activity implements OnInitListener, OnClickListener
{
	private TextView m_txtTimer;
	private TextToSpeech m_tts;
	private CountdownTimerComplex m_timer;
	private TextView m_textbigBlind;
	private int m_secondsRemaining;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        
        Button saveButt = (Button)findViewById(R.id.ButtonSave);
        saveButt.setOnClickListener(this);
        
        //Button butSwitchToMain = (Button)findViewById(R.id.SwitchToMainScreen);
       // butSwitchToMain.setOnClickListener(this);

        m_textbigBlind = (TextView)findViewById(R.id.TextViewSetBigBlind);
    }
    
    public void onClick(View v)
    {
    	// do something   	
    	if (  v.getId() == R.id.ButtonSave)
    	{
    		setContentView(R.layout.main);
            m_txtTimer = (TextView)findViewById(R.id.TextTimer);
            m_tts = new TextToSpeech(this, this);
            String name = getPackageName();
            m_txtTimer.setText(name);
            Log.d(name, "onCreate Called my moma");
            goTimer(20);
    	} 
    	else
    	{
    		m_textbigBlind.setText("Button clicked");	
    	}
    }
    
    public void onPause()
    {
        // when the screen is about to turn off
        if (ScreenReceiver.wasScreenOn) {
            // this is the case when onPause() is called by the system due to a screen state change
        	// update the time remaining from the timerclass.
        	m_secondsRemaining = m_timer.cancel_returnSecondsRemaining();
        	
        	// TODO: start some system alarm thing
        	//         	int iMinutesRemaining = m_secondsRemaining / 60;
        	
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
    	m_timer = new CountdownTimerComplex(secondsToCountDown, m_txtTimer);
    	m_timer.startTiming();
    	
    }
    
    public void onInit(int status)
    {
   	

    }
    
    private class CountdownTimerComplex extends CountDownTimer{
    	int m_iSecondsToCountDown;
    	int m_iSecondsRemaining;
    	static final int m_iMsMultiplier = 1000;
    	TextView m_textViewToUpdate;
    	CountDownTimer m_cdt;
		public CountdownTimerComplex(int secondsToCount, TextView viewToUpdate) {
			super(secondsToCount * m_iMsMultiplier, m_iMsMultiplier);
			m_iSecondsToCountDown = secondsToCount;
			m_textViewToUpdate = viewToUpdate;
		}
		
		public void startTiming()
		{
			m_cdt = super.start();
		}
		
		public void onTick (long millisUntilFinished)
		{
			m_iSecondsRemaining = (int) (millisUntilFinished / m_iMsMultiplier);
			m_textViewToUpdate.setText("Seconds Remaining: " + m_iSecondsRemaining);
		}
		
		public void onFinish()
		{
			m_tts.setPitch(1.5f);
			m_tts.setLanguage(Locale.ENGLISH);
			m_tts.speak(
						"Hello how are you today?",
						android.speech.tts.TextToSpeech.QUEUE_FLUSH,
						null);
			m_textViewToUpdate.setText("All done");
		}
		
		public int cancel_returnSecondsRemaining()
		{
			super.cancel();
			return m_iSecondsRemaining;
		}
    	
    }
    
    // http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
    private static class ScreenReceiver extends BroadcastReceiver 
    {
        public static boolean wasScreenOn = true;
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {
                // do whatever you need to do here
                wasScreenOn = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                // and do whatever you need to do here
                wasScreenOn = true;
            }
        }
    }
    
}