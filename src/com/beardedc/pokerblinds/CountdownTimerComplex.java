package com.beardedc.pokerblinds;

import android.os.CountDownTimer;
import android.widget.TextView;

public class CountdownTimerComplex extends CountDownTimer{
	static final int m_iMsMultiplier = 1000;
	private Boolean m_bIsTimerRunning;
	private int m_iSecondsToCountDown;
	private int m_iSecondsRemaining;
	IReturnFinished m_callback;
	TextView m_textViewToUpdate;
	private CountDownTimer m_cdt;
	
	public CountdownTimerComplex(int secondsToCount, TextView viewToUpdate, IReturnFinished passBackObject) {
		super(secondsToCount * m_iMsMultiplier, m_iMsMultiplier);
		m_iSecondsToCountDown = secondsToCount;
		m_textViewToUpdate = viewToUpdate;
		m_callback = passBackObject;
	}
	
	public int getSecondsRemaining()
	{
		return m_iSecondsRemaining;
	}
	
	public Boolean isTimerRunning()
	{
		return m_bIsTimerRunning;
	}
	
	public void startTiming()
	{
		m_cdt = super.start();
		m_bIsTimerRunning = true;
	}
	
	public void onTick (long millisUntilFinished)
	{
		m_textViewToUpdate.setText(getHrsMinsSecFromSec(millisUntilFinished / m_iMsMultiplier));
		m_iSecondsRemaining = (int) millisUntilFinished / m_iMsMultiplier;		
	}
	
	private String getHrsMinsSecFromSec(long seconds)
	{
		String sReturn = "Time Remaining: ";
		if (seconds == 0){return sReturn + "00:00:00";}
		
		int ihrs = (int) seconds / 3600;
		int iMins = (int) ((seconds % 3600) / 60);
		int iSecs = (int) ((seconds % 3600) % 60);
		
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
	public void onFinish()
	{
		m_textViewToUpdate.setText(getHrsMinsSecFromSec(0));
		m_bIsTimerRunning = false;
		m_callback.jobDone();
	}
	
	//*************************************************************************
	public void pauseTimer()
	{
		super.cancel();
		m_bIsTimerRunning = false;
	}	
}