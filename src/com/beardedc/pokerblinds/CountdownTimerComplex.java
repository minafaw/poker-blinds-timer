package com.beardedc.pokerblinds;

import java.math.RoundingMode;

import android.os.CountDownTimer;
import android.widget.TextView;

public class CountdownTimerComplex extends CountDownTimer{
	static final int m_iMsMultiplier = 1000;
	public Boolean bIsTimerRunning;
	int m_iSecondsToCountDown;
	int m_iSecondsRemaining;
	IReturnFinished m_callback;
	TextView m_textViewToUpdate;
	private CountDownTimer m_cdt;
	
	public CountdownTimerComplex(int secondsToCount, TextView viewToUpdate, IReturnFinished passBackObject) {
		super(secondsToCount * m_iMsMultiplier, m_iMsMultiplier);
		m_iSecondsToCountDown = secondsToCount;
		m_textViewToUpdate = viewToUpdate;
		m_callback = passBackObject;
	}
	
	public void startTiming()
	{
		m_cdt = super.start();
		bIsTimerRunning = true;
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
	
	private String getDoubleNumberFormat(int i)
	{
		if (i == 0) {return "00";}
		if (i <= 9) {return "0" + i;}
		return String.valueOf(i);		
	}
	
	public void onFinish()
	{
		m_textViewToUpdate.setText(getHrsMinsSecFromSec(0));
		bIsTimerRunning = false;
		m_callback.jobDone();
	}
	
	public int cancel_returnSecondsRemaining()
	{
		super.cancel();
		bIsTimerRunning = false;
		return m_iSecondsRemaining;
	}
	
}