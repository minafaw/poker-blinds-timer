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
	final static int m_iMultiplierSecondsInHour = 3600;
	
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
	public long getSecondsRemaining()
	{
		return (int) m_milliRemainingToTime / m_iMultiplierMilli;
	}
	
	//*************************************************************************
	public Boolean getIsTimerRunning()
	{
		return m_bIsTimerRunning;
	}
	
	//*************************************************************************
	public String getTimeRemainingUIFormat()
	{
		return getHrsMinsSecFromSec(m_milliRemainingToTime);
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
		m_bUseCountdownTimer = true;
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
		m_bUseCountdownTimer = false;
		if (m_bIsTimerRunning == false) return;
		
		// work out how long the alarm was timing for
		long lDelta = SystemClock.elapsedRealtime() - m_milliSecsRelativeToAlarmStart;
		
		cancelAlarmTimer();
		startCountdownTimer(lDelta);
	}	
	
	//*************************************************************************
	private Boolean startCountdownTimer(long milliSecondsToTime)
	{
		try
		{
			m_timer =  new CountDownTimer(milliSecondsToTime, m_iMultiplierMilli)
			{
				public void onTick(long millisUntilFinished)
				{
					m_milliRemainingToTime = millisUntilFinished;
					if (BROADCAST_MESSAGE_TICK != null)
					{
						m_contextToIssueUpdates.sendBroadcast(new Intent(BROADCAST_MESSAGE_TICK));	
					}
				}
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
	private String getHrsMinsSecFromSec(long milliSecs)
	{
		String sReturn = "Time Remaining: ";
		if (milliSecs == 0){return sReturn + "00:00:00";}
		
		long seconds = milliSecs / m_iMultiplierMilli;
		int ihrs = (int) seconds / m_iMultiplierSecondsInHour;
		int iMins = (int) ((seconds % m_iMultiplierSecondsInHour) / m_iMultiplierMinutesToSeconds);
		int iSecs = (int) ((seconds % m_iMultiplierSecondsInHour) % m_iMultiplierMinutesToSeconds);
		
		sReturn = sReturn  
					+ getDoubleNumberFormat(ihrs) + ":" 
					+ getDoubleNumberFormat(iMins) + ":" 
					+ getDoubleNumberFormat(iSecs);		
		return sReturn;
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