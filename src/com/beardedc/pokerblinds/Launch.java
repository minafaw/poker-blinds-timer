package com.beardedc.pokerblinds;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.TextView;

public class Launch extends Activity implements OnInitListener 
{
	private TextView m_txtTimer;
	private TextToSpeech m_tts;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.main);
        // Here get the elements we need
        m_txtTimer = (TextView)findViewById(R.id.TextTimer);
        m_tts = new TextToSpeech(this, this);
        goTimer();
    }
    
    /* Info: the countdown timer
     * continues to function even if the application is not in
     * the foreground and the speech will be set if the
     * application is not running.
     */
    private void goTimer()
    {
    	new CountDownTimer(5000, 1000)
    	{
    		public void onTick(long msUntilDone)
    		{
    			m_txtTimer.setText("Time: " + msUntilDone / 1000);
    		}
    		public void onFinish()
    		{
    			m_tts.setPitch(0.8f);
    			// TODO: Cannot bring in QUEUE_FLUSH for some reason
    	    	// need to find out what needs imported
    			m_tts.speak("The blinds have gone up",
    					android.speech.tts.TextToSpeech.QUEUE_FLUSH, null);
    			m_txtTimer.setText("All done");
    		}
    	}.start();
    }
    
    public void onInit(int status)
    {
   	

    }

}