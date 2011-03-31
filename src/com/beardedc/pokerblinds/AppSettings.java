package com.beardedc.pokerblinds;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings 
{
	private long 		m_minutes;
	private long 		m_SecsRemaining;
	private long		m_initialBigBlind;
	private long		m_currentBigBlind;
	private int			m_iVibrateNumberOfTimes;
	private boolean		m_bDisableVibrate;
	private boolean		m_bApplyUpdateNow;
	
	/*************************************************************************/
	
	private static final String PREFS_NAME = "PokerTimerPrefs";
	private static final String PREFS_KEY_MINUTES = "Minutes Blinds Up";
	private static final String PREFS_KEY_BIG_BLIND = "Big Blinds";
	private static final String PREFS_KEY_SECONDS_REMAINING = "Seconds Remaining";
	private static final String PREFS_KEY_BIG_BLIND_CURRENT = "Big Blind Current";
	private static final String PREFS_KEY_VIBRATE_NUMBER = "number of times to vibrate";
	private static final String PREFS_KEY_VIBRATE_DISABLE = "vibrateDisable";
	private static final String PREFS_KEY_APPLY_UPDATE_NOW = "applyUpdateNow";
	
	private static final long PREF_DEFAULT_MINUTES = 60;
	private static final long PREF_DEFAULT_BIG_BLIND = 50;
	private static final int PREF_DEFAULT_VIBRATE_TIMES = 8;
	private static final boolean PREF_DEFAULT_VIBRATE_DISABLE = false;
	private static final boolean PREF_DEFAULT_APPLY_UPDATE_NOW = false;
	
	/*************************************************************************/
	
	private static AppSettings m_settings;
	
	SharedPreferences m_sharedPrefs;
	
	public static AppSettings getSettings(Context c)
	{
		if (m_settings == null)
		{
			if (c == null)
				return null;
			else
			{
				m_settings = new AppSettings();
				m_settings.load(c);
			}
		}
		
		return m_settings;
	}
	
	/*************************************************************************/
	
	// hide this as we want a Singleton
	private AppSettings()
	{
	}
	
	/*************************************************************************/
	
	private void load(Context c)
	{
		if (c != null)
		{
			m_sharedPrefs =	c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE );
			m_minutes = m_sharedPrefs.getLong(PREFS_KEY_MINUTES, PREF_DEFAULT_MINUTES);
			m_SecsRemaining = m_sharedPrefs.getLong(PREFS_KEY_SECONDS_REMAINING, PREF_DEFAULT_MINUTES);
			m_initialBigBlind = m_sharedPrefs.getLong(PREFS_KEY_BIG_BLIND, PREF_DEFAULT_BIG_BLIND);
			m_currentBigBlind = m_sharedPrefs.getLong(PREFS_KEY_BIG_BLIND_CURRENT, PREF_DEFAULT_BIG_BLIND);
			m_iVibrateNumberOfTimes = m_sharedPrefs.getInt(PREFS_KEY_VIBRATE_NUMBER, PREF_DEFAULT_VIBRATE_TIMES);
			m_bDisableVibrate = m_sharedPrefs.getBoolean(PREFS_KEY_VIBRATE_DISABLE, PREF_DEFAULT_VIBRATE_DISABLE);
			m_bApplyUpdateNow = m_sharedPrefs.getBoolean(PREFS_KEY_APPLY_UPDATE_NOW, PREF_DEFAULT_APPLY_UPDATE_NOW);
		}
	}
	
	/*************************************************************************/
	
	public boolean save()
	{
		SharedPreferences.Editor editor = m_sharedPrefs.edit();
		editor.putLong(PREFS_KEY_MINUTES, m_minutes);
		editor.putLong(PREFS_KEY_BIG_BLIND, m_initialBigBlind);
		editor.putLong(PREFS_KEY_BIG_BLIND_CURRENT, m_currentBigBlind);
		editor.putLong(PREFS_KEY_SECONDS_REMAINING, m_SecsRemaining);
		editor.putInt(PREFS_KEY_VIBRATE_NUMBER, m_iVibrateNumberOfTimes);
		editor.putBoolean(PREFS_KEY_VIBRATE_DISABLE, m_bDisableVibrate);
		editor.putBoolean(PREFS_KEY_APPLY_UPDATE_NOW, m_bApplyUpdateNow);
		return editor.commit();
	}
	
	/*************************************************************************/
	public boolean getApplyUpdateNow()
	{
		return m_bApplyUpdateNow;
	}
	
	/*************************************************************************/
	public void setApplyUpdateNow(boolean bApplyUpdateNow)
	{
		m_bApplyUpdateNow = bApplyUpdateNow;
	}
	
	/*************************************************************************/

	public long getMinutes()
	{
		return m_minutes;
	}

	public void setMinutes(long m_minutes)
	{
		this.m_minutes = m_minutes;
		setSecondsRemaining(m_minutes 
							* CountDownTimerComplex.m_iMultiplierMinutesToSeconds);
	}
	
	public long getSecondsRemaining()
	{
		return m_SecsRemaining;
	}

	public void setSecondsRemaining(long msRemaining)
	{
		this.m_SecsRemaining = msRemaining;
	}
	
	/*************************************************************************/

	public long getInitalBigblind()
	{
		return m_initialBigBlind;
	}

	public void setInitalBigblind(long bigblind)
	{
		m_initialBigBlind = bigblind;
		setCurrentBigBlind(bigblind);
	}
	
	/*************************************************************************/

	public long getCurrentBigBlind()
	{
		return m_currentBigBlind;
	}

	public void setCurrentBigBlind(long bigBlind)
	{
		m_currentBigBlind = bigBlind;
	}
		
	/*************************************************************************/
	
	public int getVibrateRepeat()
	{
		return m_iVibrateNumberOfTimes;
	}
	
	public void setVibrateRepeat(int iRepeatNumber)
	{
		m_iVibrateNumberOfTimes = iRepeatNumber;
	}
	
	/*************************************************************************/
	
	public boolean isVibrateDisabled()
	{
		return m_bDisableVibrate;
	}
	
	public void setVibrateDisable(boolean bDisableVibrate)
	{
		m_bDisableVibrate = bDisableVibrate;
	}
	
}
