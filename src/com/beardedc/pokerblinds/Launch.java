package com.beardedc.pokerblinds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Launch extends Activity implements OnClickListener, IReturnFinished
{
	static final String m_strAlarmBroadcast = "beardedc.pokerBlinksTimer.AlarmAction";
	static final String m_strAlarmFinished = "beardedc.pokerBlinksTimer.AlarmTimerUp";
	private TextView m_txtTimer;
	private TextView m_BlindBig;
	private TextView m_BlindSmall;
	private CountDownTimerComplex m_timer;
	private AppSettings m_settings;
	private int m_iMultiplierToConvertMinutesToSeconds = 60;
	private Button m_pause;
	private AlarmManager m_alarmManager; 
	private PendingIntent m_alarmIntent;
	private long m_lmiliSecsSinceBoot;
	private EditText m_manualBigBlindAlteration;
	private Button m_bigBlindOverride;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(AlarmReceiver.m_strAlarmFinished);
		filter.addAction(AlarmReceiver.m_strAlarmBroadcast);		
		BroadcastReceiver mReceiver = new GeneralReceiver(this);
		registerReceiver(mReceiver, filter);
		
		try{
			m_alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		} catch (Exception e){
			System.out.println("cheese");
		}
		
		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		m_txtTimer = (TextView) findViewById(R.id.TextTimer);
		m_BlindBig = (TextView) findViewById(R.id.textViewBigBlind);
		m_BlindSmall = (TextView) findViewById(R.id.TextViewSmallBlind);
		
		m_manualBigBlindAlteration = (EditText) findViewById(R.id.txtBigBlindOverride);
		
		m_bigBlindOverride = (Button) findViewById(R.id.butManualBigBlindChange);
		m_bigBlindOverride.setOnClickListener(this);
		
		m_pause = (Button)findViewById(R.id.ButtonPause);
		m_pause.setOnClickListener(this);
		
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
		super.onPause();
	}
	
    public void onResume()
    {
        super.onResume();
    }
	
	public void startSystemAlarm(long lmilliSecondsInFuture)
	{
		int iTypeOfAlarm = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		

		
		// store current system time
		m_lmiliSecsSinceBoot = SystemClock.elapsedRealtime();
		
		Intent intentToBroadcast = new Intent();
		intentToBroadcast.setAction(AlarmReceiver.m_strAlarmFinished);
		
		m_alarmIntent = PendingIntent.getBroadcast(
									this.getApplicationContext(), 
									1, intentToBroadcast, 
									PendingIntent.FLAG_ONE_SHOT);
		
		try{m_alarmManager.set(iTypeOfAlarm, m_lmiliSecsSinceBoot + lmilliSecondsInFuture, m_alarmIntent);}
		catch(Exception e)
		{
			System.out.println("Error Occured \n" + e.getMessage());
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
    	m_timer = new CountDownTimerComplex(secondsToCountDown, m_txtTimer, this);
    	m_timer.startTiming();
    	
    }

	public void jobDone()
	{	
		// increase blinds
		m_settings.setCurrentBigBlind( m_settings.getCurrentBigBlind() * 2);
		Boolean bSave = m_settings.save();
		
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
		Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		long[] lVibratePattern = {100,500,100,500, 100,500, 100,500, 100,500, 
				100,500, 100,500, 100,500, 100,500,
				100,500, 100,500, 100,500, 100,500,
				100,500, 100,500, 100,500, 100,500};
		v.vibrate(lVibratePattern, -1);
	}

	public void onClick(View v)
	{
		if (v.getId() == R.id.ButtonPause)
		{
			if (m_timer.isTimerRunning() == true)
			{
				m_timer.pauseTimer();
				m_pause.setText("press to restart");
			} else
			{
				goTimer(m_timer.getSecondsRemaining());
				m_pause.setText("press to pause");
			}
			
		}else if (v.getId() == R.id.butManualBigBlindChange)
		{
			// increase blinds
			String sBigBlindHack = m_manualBigBlindAlteration.getText().toString();
			long lBigBlind = Long.parseLong(sBigBlindHack);
			m_settings.setCurrentBigBlind(lBigBlind);
			Boolean bSave = m_settings.save();
			
			// update the UI
			updateBlinds(m_settings);
		}
	}

	public void intentReceived(String intent) {
		if (intent.equals(Intent.ACTION_SCREEN_OFF))
		{
			m_timer.pauseTimer();
			long lmilliSecondsInFuture = m_timer.getSecondsRemaining() * CountDownTimerComplex.m_iMsMultiplier;
			m_timer.setSecondsRemaining(m_timer.getSecondsRemaining());
			startSystemAlarm(lmilliSecondsInFuture);
			
		} else if (intent.equals(Intent.ACTION_SCREEN_ON))
		{
        	if (m_timer.isTimerRunning() == false){        		
        		long lNewSystemTime = SystemClock.elapsedRealtime();
        		long lDelta = lNewSystemTime - m_lmiliSecsSinceBoot;
        		int iSecsSpentAsleep = (int) (lDelta / CountDownTimerComplex.m_iMsMultiplier);
        		int iSecsStillToTime = m_timer.getSecondsRemaining() - iSecsSpentAsleep;
        		
        		// kill alarm
        		m_alarmManager.cancel(m_alarmIntent);
        		
        		// start normal user ui
        		goTimer(iSecsStillToTime);
        	}
		} else if (intent.equals(m_strAlarmFinished))
		{
			// increase blinds
			m_settings.setCurrentBigBlind( m_settings.getCurrentBigBlind() * 2);
			Boolean bSave = m_settings.save();
			
			// update the UI
			updateBlinds(m_settings);
			
			// notify user
			vibrateThePhone();	
			
			long lmilliSecondsInFuture = (m_settings.getMinutes() * 60) * CountDownTimerComplex.m_iMsMultiplier;
			m_timer.setSecondsRemaining((int) m_settings.getMinutes() * 60);
			startSystemAlarm(lmilliSecondsInFuture);
		}
		
	}
	
}