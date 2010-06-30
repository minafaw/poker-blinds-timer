package com.beardedc.pokerblinds;

import java.util.Locale;

import android.app.Activity;
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
	private CountDownTimer m_timer;
	private long m_bigBlind;
	private long m_timerHours;
	private long m_timerMinutes;
	private TextView m_textbigBlind;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        Button saveButt = (Button)findViewById(R.id.ButtonSave);
        saveButt.setOnClickListener(this);
        
        Button butSwitchToMain = (Button)findViewById(R.id.SwitchToMainScreen);
        butSwitchToMain.setOnClickListener(this);

        m_textbigBlind = (TextView)findViewById(R.id.TextViewSetBigBlind);
    }
    
    public void onClick(View v)
    {
    	// do something   	
    	if (  v.getId() == R.id.SwitchToMainScreen){
    		
    		setContentView(R.layout.main);
            m_txtTimer = (TextView)findViewById(R.id.TextTimer);
            m_tts = new TextToSpeech(this, this);
            //m_tts.setLanguage(Locale.FRANCE);
            String name = getPackageName();
            m_txtTimer.setText(name);
            Log.d(name, "onCreate Called my moma");
            goTimer();
    	} else{
    		m_textbigBlind.setText("Button clicked");	
    	}
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
    @SuppressWarnings("unused")
	private void goTimer()
    {
    	m_timer = new CountDownTimer(5000, 1000)
    	{
    		public void onTick(long msUntilDone)
    		{
    			m_txtTimer.setText("Time: " + msUntilDone / 1000);
    		}
    		public void onFinish()
    		{
    			m_tts.setPitch(1.5f);
    			// TODO: Cannot bring in QUEUE_FLUSH for some reason
    	    	// need to find out what needs imported
    			//m_tts.setSpeechRate( 0.5f);
    			m_tts.setLanguage(Locale.ENGLISH);
    			m_tts.speak("Hello how are you today?",
    					android.speech.tts.TextToSpeech.QUEUE_FLUSH,  null);
    			m_txtTimer.setText("All done");
    			//goTimer();
    		}
    	}.start();
    }
    
    public void onInit(int status)
    {
   	

    }

}