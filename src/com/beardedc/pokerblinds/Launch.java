package com.beardedc.pokerblinds;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Launch extends Activity implements OnClickListener, IReturnFinished
{
	private TextView m_txtTimer;
	private TextView m_BlindBig;
	private TextView m_BlindSmall;
	private CountDownTimerComplex m_timer;
	private AppSettings m_settings;
	private Button m_pause;
	private EditText m_manualBigBlindAlteration;
	private Button m_bigBlindOverride;
	private Button _settings = null;
	

	//*************************************************************************
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(CountDownTimerComplex.BROADCAST_MESSAGE_TICK);
		filter.addAction(CountDownTimerComplex.BROADCAST_MESSAGE_COMPLETE);		
		BroadcastReceiver mReceiver = new GeneralReceiver(this);
		registerReceiver(mReceiver, filter);
		
		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		m_txtTimer = (TextView) findViewById(R.id.TextTimer);
		m_BlindBig = (TextView) findViewById(R.id.textViewBigBlind);
		m_BlindSmall = (TextView) findViewById(R.id.TextViewSmallBlind);
		_settings = (Button) findViewById(R.id.button_settings);
		
		m_manualBigBlindAlteration = (EditText) findViewById(R.id.txtBigBlindOverride);
		
		m_bigBlindOverride = (Button) findViewById(R.id.butManualBigBlindChange);
		m_bigBlindOverride.setOnClickListener(this);
		
		m_pause = (Button)findViewById(R.id.ButtonPause);
		m_pause.setOnClickListener(this);
		
		m_timer = new CountDownTimerComplex(this.getApplicationContext());
		
		_settings.setOnClickListener(this);
		
		updateBlinds(m_settings);

		m_timer.startTiming((int) m_settings.getSecondsRemaining());
	}
	
	//*************************************************************************
	/**
	 * 
	 */
	private void updateBlinds(AppSettings a)
	{
		updateTextView("Big Blind is   : " + a.getCurrentBigBlind(), m_BlindBig);
		updateTextView("Small Blind is : " + (a.getCurrentBigBlind() /2), m_BlindSmall);
	}
	
	//*************************************************************************
	/**
	 * 
	 */
	private void updateTextView(String toOutput, TextView toUpdate)
	{
		toUpdate.setText(toOutput);
	}
	
	//*************************************************************************
    /**
     * 
     */
	private void vibrateThePhone()
	{
		Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		long[] lVibratePattern = new long[m_settings.getVibrateRepeat() *2];
		int iMilliSeconds;
		for (int i = 0; i < (m_settings.getVibrateRepeat() * 2); i++)
		{
			if (isOdd(i)) {iMilliSeconds = 100;}
			else {iMilliSeconds = 500;}
			lVibratePattern[i] = iMilliSeconds;
		}
		v.vibrate(lVibratePattern, -1);
	}
	
	private boolean isOdd(int i)
	{
		return ((i % 1) == 1);
	}

	//*************************************************************************
	/**
	 * 
	 */
	public void onClick(View v)
	{
		if (v.getId() == R.id.ButtonPause)
		{
			if (m_timer.getIsTimerRunning() == true)
			{
				m_timer.pauseStart();
				m_pause.setText("press to restart");
			} else
			{
				m_timer.pauseStop();
				m_pause.setText("press to pause");
			}
			
		}else if (v.getId() == R.id.butManualBigBlindChange)
		{
			// increase blinds
			String sBigBlindHack = m_manualBigBlindAlteration.getText().toString();
			long lBigBlind = Long.parseLong(sBigBlindHack);
			m_settings.setCurrentBigBlind(lBigBlind);
			m_settings.save();
			
			// update the UI
			updateBlinds(m_settings);
		}else if (v.getId() == R.id.button_settings){
				try{
				Intent settingPrefs = new Intent(this, PreferenceLauncher.class);
				startActivity(settingPrefs);
				} catch (Exception e){
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
		}
	}

	//*************************************************************************
	/**
	 * 
	 */
	public void intentReceived(Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
		{
			m_timer.switchModeToAlarm();
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
		{
        	m_timer.switchModeToCountdown();
        	m_txtTimer.setText(m_timer.getTimeRemainingUIFormat());
		} else if (intent.getAction().equals(CountDownTimerComplex.BROADCAST_MESSAGE_TICK))
		{
			m_txtTimer.setText(m_timer.getTimeRemainingUIFormat());
		} else if (intent.getAction().equals(CountDownTimerComplex.BROADCAST_MESSAGE_COMPLETE))
		{
			// increase blinds
			m_settings.setCurrentBigBlind( m_settings.getCurrentBigBlind() * 2);
			
			// update the UI
			updateBlinds(m_settings);
			
			// update the timer to zero values
			String sUpdateValue = m_timer.getTimeRemainingUIFormat();
			m_txtTimer.setText(sUpdateValue);
			
			// notify user
			vibrateThePhone();
			
			// start the timer again
			m_timer.startTiming((int) m_settings.getMinutes() * CountDownTimerComplex.m_iMultiplierMinutesToSeconds);
		}
	}	
	
    //*************************************************************************
    /**
     * make sure any lingering alarms are cancelled
     */
    @Override
	public void onDestroy()
    {    	
    	if (m_timer != null) m_timer.destroy();
    	super.onDestroy();
    }
    
    @Override
	public void onPause()
    {
    	m_settings.setSecondsRemaining(m_timer.getTimeRemainingInSeconds());
    	m_settings.save();
    	super.onPause();
    }
    
    /*
     * This will be used to make sure we read back the values saved
     * from the settings intent. 
     */
    @Override
    public void onStart(){
    	super.onStart();
    	// TODO implement
    	
    }
    
}