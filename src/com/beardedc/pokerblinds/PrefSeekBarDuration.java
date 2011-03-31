package com.beardedc.pokerblinds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class PrefSeekBarDuration extends PreferenceSeekBar {

	public PrefSeekBarDuration(Context context) {
		 super(context);
	}
	
	public PrefSeekBarDuration(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PrefSeekBarDuration(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
	}
	
	@Override
	protected View onCreateView(ViewGroup parent) {
		// TODO Auto-generated method stub
		m_oldValue = m_settings.getMinutes();
		return super.onCreateView(parent);
	}
	
	@Override
	protected void updatePreference(int newValue) {
		 m_settings.setMinutes(newValue);
		 m_settings.save();
	}

}
