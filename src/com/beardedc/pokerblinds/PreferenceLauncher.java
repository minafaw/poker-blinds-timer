package com.beardedc.pokerblinds;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferenceLauncher extends PreferenceActivity implements OnPreferenceClickListener {
	
	private AppSettings _settings = AppSettings.getSettings(this);
	private Preference _pref_Vibrate_Disabled;
	private Preference _pref_BigBlind;
	
	private static String M_PREF_VIBRATE_DISABLED = "prefCheckBoxVibrate";
	private static String M_PREF_BLIND_VALUE = "editTextBigBlind";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		
		try
		{
			// vibration disabled setting
			_pref_Vibrate_Disabled = (Preference) findPreference(PreferenceLauncher.M_PREF_VIBRATE_DISABLED);
			_pref_Vibrate_Disabled.setOnPreferenceClickListener(this);
			
			_pref_BigBlind = (Preference) findPreference(PreferenceLauncher.M_PREF_BLIND_VALUE);
			_pref_BigBlind.setOnPreferenceClickListener(this);
			
		}
		catch (Exception e){
			Toast.makeText(this.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
    }
    
	public boolean onPreferenceClick(Preference prefClicked) {
		if (prefClicked.getKey() == PreferenceLauncher.M_PREF_VIBRATE_DISABLED)
		{
			_settings.setVibrateDisable(!_settings.isVibrateDisabled());
		} else if (prefClicked.getKey() == PreferenceLauncher.M_PREF_BLIND_VALUE)
		{
			String sValue = "0";
			_pref_BigBlind.getSharedPreferences().getString(_pref_BigBlind.getKey(), sValue);
			Long bigBlind = Long.getLong(sValue);
			_settings.setInitalBigblind(bigBlind);
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