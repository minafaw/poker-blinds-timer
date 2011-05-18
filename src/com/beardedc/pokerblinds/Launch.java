package com.beardedc.pokerblinds;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class Launch extends Activity implements IReturnFinished
{
	private TextView m_txtTimer;
	private TextView m_BlindBig;
	private TextView m_BlindSmall;
	private CountDownTimerComplex m_timer;
	private AppSettings m_settings;
	private String pauseText, startText;
	private MenuItem _menuItem_Pause = null;
	private MenuItem _menuItem_Settings = null;

	//*************************************************************************
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setupIntents();
		
		setupUI();
		
		updateBlinds();
		
		m_timer = new CountDownTimerComplex(this.getApplicationContext());
		
		if (m_timer.getIsTimerRunning()) m_timer.startTiming((int) m_settings.getSecondsRemaining());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (m_timer.getIsTimerRunning() == true){
			_menuItem_Pause = menu.add(pauseText);
		} else{
			_menuItem_Pause = menu.add(startText);
		}
		_menuItem_Settings = menu.add("Settings");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.equals(_menuItem_Settings))
		{
			try
			{
				Intent settingPrefs = new Intent(this, PreferenceLauncher.class);
				startActivity(settingPrefs);
				return true;
			} catch (Exception e)
			{
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		} else if (item.equals(_menuItem_Pause))
		{
			if (m_timer.getIsTimerRunning() == true)
			{
				m_timer.pauseStart();
				_menuItem_Pause.setTitle(startText);
			} else
			{
				m_timer.pauseStop();
				_menuItem_Pause.setTitle(pauseText);
			}
			return true;
		}
		return false;
	}

	private void setupUI() 
	{
		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		m_txtTimer = (TextView) findViewById(R.id.TextTimer);
		m_BlindBig = (TextView) findViewById(R.id.BigBlindValue);
		m_BlindSmall = (TextView) findViewById(R.id.SmallBlindValue);
		
		pauseText = getString(R.string.pauseTimer);
		startText = getString(R.string.startTimer);

		//m_bigBlindOverride.setOnClickListener(this);
		
	}

	// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
	private void setupIntents() 
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(CountDownTimerComplex.BROADCAST_MESSAGE_TICK);
		filter.addAction(CountDownTimerComplex.BROADCAST_MESSAGE_COMPLETE);		
		BroadcastReceiver mReceiver = new GeneralReceiver(this);
		registerReceiver(mReceiver, filter);
	}
	
	//*************************************************************************
	/**
	 * 
	 */
	private void updateBlinds()
	{
		long bigBlind = m_settings.getCurrentBigBlind();
		long smallBlind = bigBlind / 2;
		updateTextView(Long.toString(bigBlind), m_BlindBig);
		updateTextView(Long.toString(smallBlind), m_BlindSmall);
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
		if (m_settings.isVibrateDisabled() == false)
		{
			Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			long lVibratePattern[] = new long[m_settings.getVibrateRepeat() *2];
			int iMilliSeconds;
			int max = m_settings.getVibrateRepeat() * 2;
			for (int i = 0; i < max; i++)
			{
				if (isOdd(i)) {iMilliSeconds = 100;}
				else {iMilliSeconds = 500;}
				lVibratePattern[i] = iMilliSeconds;
			}
			v.vibrate(lVibratePattern, -1);
		}
	}
	
	private boolean isOdd(int i)
	{
		return ((i % 1) == 1);
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
			updateBlinds();
			
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
    	
    	// if we are suppose to do stuff
    	if (m_settings.getApplyUpdateNow()) 
    	{
    		updateBlinds();
    		
    		// destroy current timer
    		if (m_timer != null & m_timer.getIsTimerRunning())
    		{
        		m_timer.destroy();
        		m_timer = new CountDownTimerComplex(this);
        		m_timer.startTiming((int) m_settings.getMinutes() * 60);
    		}
    		
    		m_settings.setApplyUpdateNow(false);
    		m_settings.save();
    	}
    	super.onStart();
    }
    
}