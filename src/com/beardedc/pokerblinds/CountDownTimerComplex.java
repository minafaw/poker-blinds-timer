package com.beardedc.pokerblinds;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.SystemClock;

public class CountDownTimerComplex{
	final static String BROADCAST_MESSAGE_TICK = "beardedc.pokerBlinksTimer.timerTick";
	final static String BROADCAST_MESSAGE_COMPLETE = "beardedc.pokerBlinksTimer.timerComplete";
	final static int m_iMultiplierMilli = 1000;
	final static int m_iMultiplierMinutesToSeconds = 60;
	
	private Boolean m_bIsTimerRunning = false;
	private Boolean m_bUseCountdownTimer = true;
	
	private Context m_contextToIssueUpdates;
	
	private CountDownTimer m_timer;
	
	private long m_milliRemainingToTime;
	private long m_milliSecsRelativeToAlarmStart;
	
	private AlarmManager m_alarmManager;
	private PendingIntent m_alarmIntent;
	
	//*************************************************************************
	public CountDownTimerComplex(Context contextToIssueUpdatesUnder)
	{
		m_contextToIssueUpdates = contextToIssueUpdatesUnder;
	}
	
	//*************************************************************************
	public Boolean getIsTimerRunning()
	{
		return m_bIsTimerRunning;
	}
	
	//*************************************************************************
	public String getTimeRemainingUIFormat()
	{
		return getHrsMinsSecFromMilliSec(m_milliRemainingToTime);		
	}
	
	//*************************************************************************
	public long getTimeRemainingInSeconds()
	{
		return (int) m_milliRemainingToTime / m_iMultiplierMilli;
	}
	
	//*************************************************************************
	public long getTimeRemainingInMilliSeconds()
	{
		return m_milliRemainingToTime;
	}
	
	//*************************************************************************
	public Boolean startTiming(int secondsToCount)
	{
		
		m_milliRemainingToTime = secondsToCount * m_iMultiplierMilli;
		Boolean bReturn = false;
		
		if (m_bUseCountdownTimer)
		{
			bReturn = startCountdownTimer(m_milliRemainingToTime);
		}else 
		{
			bReturn = startAlarmTimer(m_milliRemainingToTime);
		}
		
		return bReturn;
	}
	
	//*************************************************************************
	/**
	 * 
	 */
	public void switchModeToAlarm()
	{
		m_bUseCountdownTimer = false;
		if (m_bIsTimerRunning == false) return;
		
		cancelCountdownTimer();
		startAlarmTimer(m_milliRemainingToTime);
	}
	
	//*************************************************************************
	/**
	 * 
	 */
	public void switchModeToCountdown()
	{
		m_bUseCountdownTimer = true;
		if (m_bIsTimerRunning == false) return;
		
		// work out how long the alarm was timing for
		m_milliRemainingToTime = m_milliRemainingToTime - ( SystemClock.elapsedRealtime() - m_milliSecsRelativeToAlarmStart);
		
		cancelAlarmTimer();
		startCountdownTimer(m_milliRemainingToTime);
	}	
	
	//*************************************************************************
	private Boolean startCountdownTimer(long milliSecondsToTime)
	{
		try
		{
			m_timer =  new CountDownTimer(milliSecondsToTime, m_iMultiplierMilli)
			{
				@Override
				public void onTick(long millisUntilFinished)
				{
					m_milliRemainingToTime = millisUntilFinished;
					if (BROADCAST_MESSAGE_TICK != null)
					{
						m_contextToIssueUpdates.sendBroadcast(new Intent(BROADCAST_MESSAGE_TICK));	
					}
				}
				@Override
				public void onFinish()
				{
					m_bIsTimerRunning = false;
					m_milliRemainingToTime = 0;
					if (BROADCAST_MESSAGE_COMPLETE != null)
					{
						m_contextToIssueUpdates.sendBroadcast(new Intent(BROADCAST_MESSAGE_COMPLETE));
					}
				}
			};
			m_timer.start();
			m_bIsTimerRunning = true;
		} catch (Exception failedToStartTimer)
		{
			System.out.println("Failed to start default timer \n" + failedToStartTimer.getMessage());
			return false;
		}
		return true;
	}
	
	//*************************************************************************
	/**
	 * This will construct a new alarm intent for the alarm manager
	 * This should be then passed into an alarm manager
	 */
	private Boolean startAlarmTimer(long milliSecondsToTime)
	{
		try
		{
			m_milliSecsRelativeToAlarmStart = SystemClock.elapsedRealtime();
			m_alarmManager = (AlarmManager) m_contextToIssueUpdates.getSystemService(Context.ALARM_SERVICE);
			m_alarmIntent = PendingIntent.getBroadcast(
									m_contextToIssueUpdates, 
									1, new Intent(BROADCAST_MESSAGE_COMPLETE), 
									PendingIntent.FLAG_ONE_SHOT);
			m_alarmManager.set(
							AlarmManager.ELAPSED_REALTIME_WAKEUP, 
							m_milliSecsRelativeToAlarmStart + milliSecondsToTime, 
							m_alarmIntent);
			m_bIsTimerRunning = true;
		} catch(Exception e)
		{
			System.out.println("Failed to start alarm manager for countdown \n" + e.getMessage());
			return false;
		}
		return true;
	}
	
	//*************************************************************************
	/**
	 * This will action pausing the timer class, as currently this is only 
	 * interactable through the user interface then this will by poxy mean
	 * that the UI is functioning so the software is not in hibernate mode
	 */
	public void pauseStart()
	{
		// if we are not running, then nothing to do
		if (m_bIsTimerRunning == false) return;
		m_timer.cancel();
		m_bIsTimerRunning = false;
	}
	
	//*************************************************************************
	/**
	 * This can only by performed by user through the UI, therefore this code
	 * will only restart the timer class and not the alarm class. 
	 */
	public void pauseStop()
	{
		// if we are already running, then nothing to do
		if (m_bIsTimerRunning == true) return;
		
		startCountdownTimer(m_milliRemainingToTime);
		m_bIsTimerRunning = true;
	}

	//*************************************************************************
	private String getHrsMinsSecFromMilliSec(long milliSecs)
	{
		if (milliSecs == 0){return "00:00:00";}
		
		long seconds = milliSecs / m_iMultiplierMilli;
		long secondsInHour = m_iMultiplierMinutesToSeconds * m_iMultiplierMinutesToSeconds;
		int ihrs = (int) (seconds / secondsInHour);
		int iMins = (int) ((seconds % secondsInHour) / m_iMultiplierMinutesToSeconds);
		int iSecs = (int) ((seconds % secondsInHour) % m_iMultiplierMinutesToSeconds);
		
		return getDoubleNumberFormat(ihrs) + ":" 
				+ getDoubleNumberFormat(iMins) + ":" 
				+ getDoubleNumberFormat(iSecs);		
	}
	
	//*************************************************************************
	private String getDoubleNumberFormat(int i)
	{
		if (i == 0) {return "00";}
		if (i <= 9) {return "0" + i;}
		return String.valueOf(i);		
	}

	//*************************************************************************
	public void destroy() {
		if (m_bIsTimerRunning == false) return;
		
		if (m_bUseCountdownTimer == true) {cancelCountdownTimer();}
		else {cancelAlarmTimer();}
		
	}
	
	//*************************************************************************
	private void cancelCountdownTimer()
	{
		m_timer.cancel();
	}
	
	//*************************************************************************
	private void cancelAlarmTimer()
	{
		m_alarmManager.cancel(m_alarmIntent);
	}
	
}