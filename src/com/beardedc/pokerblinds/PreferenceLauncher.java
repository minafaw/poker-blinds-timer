package com.beardedc.pokerblinds;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferenceLauncher extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	
	private AppSettings _settings = AppSettings.getSettings(this);
	private Preference _pref_Vibrate_Disabled;
	private Preference _pref_BigBlind_Current;
	private Preference _pref_BigBlind_Start;
	private Preference _pref_ApplyNow;
	private Preference _pref_ApplyLater;
	
	private boolean _b_vibrateDisabled;
	
	private static String M_PREF_VIBRATE_DISABLED = "prefCheckBoxVibrate";
	private static String M_PREF_BLIND_VALUE_CURRENT = "editTextBigBlind_Current";
	private static String M_PREF_BLIND_VALUE_START = "editTextBigBlind_Start";
	private static String M_PREF_APPLY_NOW = "prefApplySettings";
	private static String M_PREF_APPLY_LATER = "prefApplySettingsNextRound";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		try
		{
			// vibration disabled setting
			_pref_Vibrate_Disabled = (Preference) findPreference(PreferenceLauncher.M_PREF_VIBRATE_DISABLED);
			_pref_Vibrate_Disabled.setOnPreferenceClickListener(this);
			
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
		catch (Exception e){
			Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
    }
    
	public boolean onPreferenceClick(Preference prefClicked) {
		
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

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		 if (preference.getKey().equals(M_PREF_BLIND_VALUE_CURRENT))
		{
			_settings.setCurrentBigBlind(Long.getLong(newValue.toString()));
			
		} else if (preference.getKey().equals(M_PREF_BLIND_VALUE_START))
		{
			_settings.setInitalBigblind(Long.getLong(newValue.toString()));
			
		}
		return true;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		_settings.save();
		super.onDestroy();
	}
    
}