package com.beardedc.pokerblinds;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

abstract class PreferenceSeekBar extends Preference implements OnSeekBarChangeListener {
	protected int m_maximum 	= 20;
	protected int m_interval	= 1;
	protected int m_minimum     = 0;
	
	public static String M_STR_NAMESPACE="android";
	public static String M_STR_MAX = "defaultValue";
	
	protected float m_oldValue;
	private TextView m_TextView_CurrentValue;
	
	protected AppSettings m_settings;
	
	public PreferenceSeekBar(Context context) {
		super(context);
		m_settings = AppSettings.getSettings(getContext());
	}
	
	public PreferenceSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_settings = AppSettings.getSettings(getContext());
		setXMLDefaults(attrs, context);
	}
	
	public PreferenceSeekBar(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		 m_settings = AppSettings.getSettings(getContext());
		 setXMLDefaults(attrs, context);
	}
	
	 private void setXMLDefaults(AttributeSet attrs, Context c) {
		 try{
			 
			 TypedArray a = c.obtainStyledAttributes(attrs,R.styleable.PreferenceSeekBarXML);
			 m_maximum = a.getInt(R.styleable.PreferenceSeekBarXML_sliderMax, 60);
			 m_minimum = a.getInt(R.styleable.PreferenceSeekBarXML_sliderMin, 1);
			 m_interval = a.getInt(R.styleable.PreferenceSeekBarXML_increment, 1);
			 
		 }catch (Exception e){
			 m_maximum = 60;
			 m_minimum = 1;
			 m_interval = 1;
		 }
		 	 
	}
	
	@Override
	protected View onCreateView(ViewGroup parent){
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setPadding(15, 5, 10, 5);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout layoutUserResults = getUserViewLayout();
		LinearLayout.LayoutParams layoutBar = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.FILL_PARENT,
											LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutBar.gravity = Gravity.RIGHT;
		
		SeekBar bar = new SeekBar(getContext());
		bar.setMax(m_maximum);
		bar.setProgress((int) m_oldValue);
		bar.setLayoutParams(layoutBar);
		bar.setOnSeekBarChangeListener(this);
		
		layout.addView(layoutUserResults);
		layout.addView(bar);
		layout.setId(android.R.id.widget_frame);
		
		return layout; 
	 }

	private LinearLayout getUserViewLayout() {
		 LinearLayout layout = new LinearLayout(getContext());
		 layout.setOrientation(LinearLayout.HORIZONTAL);
		 
		 // title box
		 LinearLayout.LayoutParams layout_Title= new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,	
											LinearLayout.LayoutParams.WRAP_CONTENT);
		 layout_Title.gravity = Gravity.LEFT;
		 TextView title = new TextView(getContext());
		 title.setText((String)getTitle());
		 title.setLayoutParams(layout_Title);
		 
		 // current value box
		 LinearLayout.LayoutParams layout_CurrentValue = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,	
											LinearLayout.LayoutParams.WRAP_CONTENT);
		 layout_CurrentValue.gravity = Gravity.RIGHT;
		 m_TextView_CurrentValue = new TextView(getContext());
		 m_TextView_CurrentValue.setText(Float.toString(m_oldValue));
		 m_TextView_CurrentValue.setTextSize(18);
		 m_TextView_CurrentValue.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
		 m_TextView_CurrentValue.setGravity(Gravity.CENTER);
		 m_TextView_CurrentValue.setLayoutParams(layout_CurrentValue);
		 
		 layout.addView(title);
		 layout.addView(m_TextView_CurrentValue);
		 layout.setId(android.R.id.widget_frame);
		 
		 return layout;
	}
	 
	 public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		
		 progress = Math.round(((float)progress)/m_interval)*m_interval;
	  
	    if(!callChangeListener(progress)){
		    seekBar.setProgress((int) m_oldValue); 
		    return; 
	    }
	    
	    seekBar.setProgress(progress);
	    m_oldValue = progress;
	    m_TextView_CurrentValue.setText(Integer.toString(seekBar.getProgress()));
	    
	    updatePreference(progress);
	  
	    notifyChanged();
	 }

	 public void onStartTrackingTouch(SeekBar seekBar) {
	 }

	 public void onStopTrackingTouch(SeekBar seekBar) {
	 }
	 
	 
	 @Override 
	 protected Object onGetDefaultValue(TypedArray ta,int index){
	  
	  int dValue = (int)ta.getInt(index,50);
	  return validateValue(dValue);
	 }
	 

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
	 
	 int temp = restoreValue ? getPersistedInt(50) : (Integer)defaultValue;
	 
	  if(!restoreValue) persistInt(temp);
	 
	  this.m_oldValue = temp;
	}
	 
	 
	private int validateValue(int value){
		
		if(value > m_maximum)
			value = m_maximum;
		else if(value < 0)
			value = 0;
		else if(value % m_interval != 0)
			value = Math.round(((float)value)/m_interval)*m_interval;
		
		return value;  
	}
	
	abstract protected void updatePreference(int newValue);
	 
}