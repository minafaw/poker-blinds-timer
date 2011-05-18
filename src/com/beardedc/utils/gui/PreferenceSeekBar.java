package com.beardedc.utils.gui;

import com.beardedc.pokerblinds.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PreferenceSeekBar extends Preference implements OnSeekBarChangeListener{
	private static String TAG 				= "PreferenceSeekBar";
	private static boolean _logStuff 		= true;
	protected int m_maximum 				= 20;
	protected int m_interval				= 1;
	protected int m_minimum     			= 1;
	protected int m_current;
	protected SeekBar m_bar;
	protected LinearLayout _layout_view_Overall;
	
	protected int m_oldValue = 5;
	protected TextView m_TextView_CurrentValue;
	
	protected void log(String error){if (_logStuff) Log.e(TAG, error); }
	
	public PreferenceSeekBar(Context context)
	{
		super(context);
		log("class being constructed");
	}
	
	public PreferenceSeekBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setXMLDefaults(attrs, context);
		log("class being constructed");
	}
	
	public PreferenceSeekBar(Context context, AttributeSet attrs, int defStyle)
	{
		 super(context, attrs, defStyle);
		 setXMLDefaults(attrs, context);
		 log("class being constructed");
		 
	}

	private void setXMLDefaults(AttributeSet attrs, Context c)
	{
		try{
			TypedArray a = c.obtainStyledAttributes(attrs,R.styleable.PreferenceSeekBarXML);
			m_minimum = a.getInt(R.styleable.PreferenceSeekBarXML_sliderMin, 1);
			m_interval = a.getInt(R.styleable.PreferenceSeekBarXML_increment, 1);
			m_maximum = a.getInt(R.styleable.PreferenceSeekBarXML_sliderMax, 60) - m_minimum;
			
		}catch (Exception e){
			m_maximum = 60;
			m_minimum = 1;
			m_interval = 1;
		}
	}
	
	@Override
	protected View onCreateView(ViewGroup parent)
	{
		log("entered onCreateView");
		log("KeyValue of Preference: " + getKey());
		
		_layout_view_Overall = new LinearLayout(getContext());
		_layout_view_Overall.setPadding(15, 5, 10, 5);
		_layout_view_Overall.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout layoutUserResults = getUserViewLayout();
		LinearLayout.LayoutParams layoutBar = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.FILL_PARENT,
											LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutBar.gravity = Gravity.RIGHT;
		
		m_bar = new SeekBar(getContext());
		m_bar.setMax(m_maximum);
		m_bar.setProgress(m_oldValue - m_minimum);
		m_bar.setLayoutParams(layoutBar);
		m_bar.setOnSeekBarChangeListener(this);
		
		_layout_view_Overall.addView(layoutUserResults);
		_layout_view_Overall.addView(m_bar);
		
		return _layout_view_Overall; 
	}
	
	private LinearLayout getUserViewLayout()
	{
		LinearLayout layout = new LinearLayout(getContext());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		
		// title box
		LinearLayout.LayoutParams layout_Title= new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.WRAP_CONTENT,	
										LinearLayout.LayoutParams.WRAP_CONTENT);
		layout_Title.gravity = Gravity.LEFT;
		TextView title = new TextView(getContext());
		title.setTextSize(18);
		title.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		title.setText((String)getTitle());
		title.setLayoutParams(layout_Title);
		
		// current value box
		LinearLayout.LayoutParams layout_CurrentValue = new LinearLayout.LayoutParams(
										LinearLayout.LayoutParams.WRAP_CONTENT,	
										LinearLayout.LayoutParams.WRAP_CONTENT);
		layout_CurrentValue.gravity = Gravity.RIGHT;
		m_TextView_CurrentValue = new TextView(getContext());
		m_TextView_CurrentValue.setText(Integer.toString(m_oldValue));
		m_TextView_CurrentValue.setTextSize(16);
		m_TextView_CurrentValue.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
		m_TextView_CurrentValue.setGravity(Gravity.CENTER);
		m_TextView_CurrentValue.setLayoutParams(layout_CurrentValue);
		
		layout.addView(title);
		layout.addView(m_TextView_CurrentValue);
		
		return layout;
	}
	
	@Override 
	protected Object onGetDefaultValue(TypedArray ta,int index)
	{
		int dValue = (int)ta.getInt(index,50);
		return validateValue(dValue);
	}
	 
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		int temp = restoreValue ? getPersistedInt(50) : (Integer)defaultValue; 
		if(!restoreValue) persistInt(temp); 
		m_oldValue = temp;
	}
	
	private int validateValue(int value)
	{
		if(value > m_maximum)
			value = m_maximum;
		else if(value < 0)
			value = 0;
		else if(value % m_interval != 0)
			value = Math.round(((float)value)/m_interval)*m_interval;
		return value;  
	}
	
	public int getCurrentProgress(){ return m_current;}
	public void setCurrentProgress(int i){m_oldValue = i; m_current = i;}

	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser)
	{
		// apply offset (minimum value)
		progress = progress + m_minimum;
		progress = Math.round(((float)progress)/m_interval)*m_interval;
		m_current = progress;
		m_TextView_CurrentValue.setText(Integer.toString(m_current));
		if (fromUser == false) m_oldValue = m_current;
	}

	public void onStartTrackingTouch(SeekBar seekBar) {}
	
	public void onStopTrackingTouch(SeekBar seekBar)
	{
		if (m_oldValue != m_current) 
		{
			m_oldValue = m_current;
			callChangeListener(m_current);
		}
	}
	
	@Override
	public View getView(View convertView, ViewGroup parent)
	{
		if (this._layout_view_Overall == null)
			convertView = onCreateView(parent);
		else
			convertView = this._layout_view_Overall;
		
		return convertView;
	} 
	
	@Override
	public void onDependencyChanged(Preference dependency, boolean disableDependent)
	{
		super.onDependencyChanged(dependency, disableDependent);
		toggleViewDisable(disableDependent);
	}
	
	public void toggleViewDisable(boolean disable)
	{
		log("££3 Disabled this slider: " + disable);
		if(this._layout_view_Overall != null)
		{
			log("££4 : we are here ");
			this._layout_view_Overall.setEnabled(!disable);
			this.m_bar.setEnabled(!disable);
		} 
	}
}