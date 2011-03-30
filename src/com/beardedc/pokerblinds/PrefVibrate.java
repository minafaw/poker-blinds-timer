package com.beardedc.pokerblinds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PrefVibrate extends PreferenceSeekBar {
	
	public PrefVibrate(Context context) {
		 super(context);
	}
	
	public PrefVibrate(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PrefVibrate(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		// TODO Auto-generated method stub
		m_settings = AppSettings.getSettings(getContext());
		m_oldValue = m_settings.getVibrateRepeat();
		return super.onCreateView(parent);
	}
	
	@Override
	protected void updatePreference(int newValue) {
		 m_settings.setVibrateRepeat(newValue);
		 m_settings.save();
	}

}
