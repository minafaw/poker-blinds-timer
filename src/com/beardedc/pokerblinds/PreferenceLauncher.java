package com.beardedc.pokerblinds;

import com.beardedc.pokerblinds.R;
import com.beardedc.utils.gui.PreferenceSeekBar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;

public class PreferenceLauncher extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	
	public final static String TAG = "PreferenceLauncher";
	public final static boolean _logStuff = true;
	private AppSettings _settings = AppSettings.getSettings(this);
	private CheckBoxPreference _pref_Vibrate_Disabled;
	private Preference _pref_BigBlind_Current;
	private Preference _pref_BigBlind_Start;
	private Preference _pref_ApplyNow;
	private Preference _pref_ApplyLater;
	private com.beardedc.utils.gui.PreferenceSeekBar _pref_Duration;
	private com.beardedc.utils.gui.PreferenceSeekBar _pref_Vibration;
	
	private boolean _b_vibrateDisabled;
	
	private static String M_PREF_VIBRATE_DISABLED = "prefCheckBoxVibrate";
	private static String M_PREF_BLIND_VALUE_CURRENT = "editTextBigBlind_Current";
	private static String M_PREF_BLIND_VALUE_START = "editTextBigBlind_Start";
	private static String M_PREF_APPLY_NOW = "prefApplySettings";
	private static String M_PREF_APPLY_LATER = "prefApplySettingsNextRound";
	private static String M_PREF_TIMER_DURATION = "prefSliderMinutes";
	private static String M_PREF_VIBRATE_COUNT = "prefSliderVibrateCount";
	
	private void log(String message){ if (_logStuff) Log.e(TAG, message); }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try
		{
			addPreferencesFromResource(R.xml.pref);
			
			_pref_Duration = (PreferenceSeekBar) findPreference(PreferenceLauncher.M_PREF_TIMER_DURATION);
			_pref_Duration.setOnPreferenceChangeListener(this);
			_pref_Duration.setCurrentProgress(_settings.getMinutes());
			
			_pref_Vibration = (PreferenceSeekBar) findPreference(PreferenceLauncher.M_PREF_VIBRATE_COUNT);
			_pref_Vibration.setOnPreferenceChangeListener(this);
			_pref_Vibration.setCurrentProgress(_settings.getVibrateRepeat());
			
			_pref_Vibrate_Disabled = (CheckBoxPreference) findPreference(PreferenceLauncher.M_PREF_VIBRATE_DISABLED);
			_pref_Vibrate_Disabled.setOnPreferenceClickListener(this);
			
			log("££1");
			SharedPreferences sp = _pref_Vibrate_Disabled.getSharedPreferences();
			if (sp.getBoolean(PreferenceLauncher.M_PREF_VIBRATE_DISABLED, true) == false)
			{
				log("££2");
				_pref_Vibration.toggleViewDisable(false);
			}
			
			_pref_ApplyNow = (Preference) findPreference(PreferenceLauncher.M_PREF_APPLY_NOW);
			_pref_ApplyNow.setOnPreferenceClickListener(this);
			
			_pref_ApplyLater = (Preference) findPreference(PreferenceLauncher.M_PREF_APPLY_LATER);
			_pref_ApplyLater.setOnPreferenceClickListener(this);

			_pref_BigBlind_Current = (Preference) findPreference(PreferenceLauncher.M_PREF_BLIND_VALUE_CURRENT);
			_pref_BigBlind_Current.setOnPreferenceChangeListener(this);
			
			_pref_BigBlind_Start = (Preference) findPreference(PreferenceLauncher.M_PREF_BLIND_VALUE_START);
			_pref_BigBlind_Start.setOnPreferenceChangeListener(this);
			
			_b_vibrateDisabled = _settings.isVibrateDisabled();
		}
		catch (Exception e)
		{
			String sNull = "NullPointerException";
			String error;
			if (e.getMessage() == null) 
				{error = sNull;}
			else 
				{error = sNull;}
			log(error);
		}
		
    }
    
	public boolean onPreferenceClick(Preference prefClicked) {
		
		log("Preference Clicked: " + prefClicked.getKey());
		
		if (prefClicked.getKey().equals(M_PREF_VIBRATE_DISABLED))
		{			
			_b_vibrateDisabled = !_b_vibrateDisabled;
			_settings.setVibrateDisable(_b_vibrateDisabled);
		
		} else if (prefClicked.getKey().equals(M_PREF_APPLY_NOW))
		{
			_settings.setApplyUpdateNow(true);
			finish();
			
		} else if (prefClicked.getKey().equals(M_PREF_APPLY_LATER))
		{
			finish();
		}
		
		return true;
	}

	public boolean onPreferenceChange(Preference preference, Object newValue)
	{
		long lNew;
		log("Preference Changed: " + preference.getKey());
		
		try
		{
			if (preference.getKey().equals(M_PREF_BLIND_VALUE_CURRENT))
			{
				lNew = getLongValue(newValue);
				_settings.setCurrentBigBlind(lNew);
				 
			} else if (preference.getKey().equals(M_PREF_BLIND_VALUE_START))
			{
				lNew = getLongValue(newValue);
				_settings.setInitalBigblind(lNew);
				 
			} else if (preference.getKey().equals(M_PREF_TIMER_DURATION))
			{
				PreferenceSeekBar sb = (PreferenceSeekBar) preference;
				_settings.setMinutes(sb.getCurrentProgress());
				
			} else if (preference.getKey().equals(M_PREF_VIBRATE_COUNT))
			{
				PreferenceSeekBar sb = (PreferenceSeekBar) preference;
				_settings.setVibrateRepeat(sb.getCurrentProgress());
			}
		} catch (Exception e) { log(e.getMessage()); }
		
		return true;
	}
	
	private long getLongValue(Object newValue) throws Exception{
		String s = newValue.toString();
		long l = Long.parseLong(s);
		return l;
	}

	@Override
	protected void onDestroy() {
		_settings.save();
		super.onDestroy();
	}

    
}