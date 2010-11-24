package com.beardedc.pokerblinds;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings 
{
	private long 		m_minutes;
	private long		m_initialBigBlind;
	private long		m_currentBigBlind;
	
	/*************************************************************************/
	
	private static final String PREFS_NAME = "PokerTimerPrefs";
	private static final String PREFS_KEY_MINUTES = "Minutes Blinds Up";
	private static final String PREFS_KEY_BIG_BLIND = "Big Blinds";
	
	private static final long PREF_DEFAULT_MINUTES = 60;
	private static final long PREF_DEFAULT_BIG_BLIND = 50;
	
	/*************************************************************************/
	
	private static AppSettings	m_settings;
	
	SharedPreferences m_sharedPrefs;
	
	public static AppSettings getSettings(Context c)
	{
		if (m_settings == null)
		{
			if (c == null)
				return null;
			else
			{
				m_settings  = new AppSettings();
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
			m_sharedPrefs = 
				c.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE );
			m_minutes = 
				m_sharedPrefs.getLong(PREFS_KEY_MINUTES, PREF_DEFAULT_MINUTES);
			m_initialBigBlind = 
				m_sharedPrefs.getLong(PREFS_KEY_BIG_BLIND, PREF_DEFAULT_BIG_BLIND);
			m_currentBigBlind = m_initialBigBlind;
		}
	}
	
	/*************************************************************************/
	
	// TODO we need to return a value from the saving of the data
	public void save()
	{
		SharedPreferences.Editor editor = m_sharedPrefs.edit();
		editor.putLong(PREFS_KEY_MINUTES, m_minutes);
		editor.putLong(PREFS_KEY_BIG_BLIND, m_initialBigBlind);
		editor.commit();
	}
	
	/*************************************************************************/

	public long getMinutes()
	{
		return m_minutes;
	}

	public void setMinutes(long m_minutes)
	{
		this.m_minutes = m_minutes;
	}
	
	/*************************************************************************/

	public long getInitalBigblind()
	{
		return m_initialBigBlind;
	}

	public void setInitalBigblind(long bigblind)
	{
		m_initialBigBlind = bigblind;
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
	
}
