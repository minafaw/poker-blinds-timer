package com.beardedc.pokerblinds;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PreferenceSeekBar extends Preference implements OnSeekBarChangeListener {
	public static int m_maximum 	= 20;
	 public static int m_interval	= 1;
	 
	 private float m_oldValue = 50;
	 private TextView m_TextView_CurrentValue;
	 
	 private AppSettings m_settings;
	 
	 public PreferenceSeekBar(Context context) {
	  super(context);
	 }
	 
	 public PreferenceSeekBar(Context context, AttributeSet attrs) {
	  super(context, attrs);
	 }
	 
	 public PreferenceSeekBar(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	 }
	   
	 @Override
	 protected View onCreateView(ViewGroup parent){
	   LinearLayout layout = new LinearLayout(getContext());
	   layout.setPadding(15, 5, 10, 5);
	   layout.setOrientation(LinearLayout.VERTICAL);
	   
	   m_settings = AppSettings.getSettings(getContext());
	   m_oldValue = m_settings.getVibrateRepeat();
	   
	   // seekbar settings
	   LinearLayout.LayoutParams layout_Seekbar = new LinearLayout.LayoutParams( 
				LinearLayout.LayoutParams.FILL_PARENT,	
				LinearLayout.LayoutParams.FILL_PARENT);
	   layout_Seekbar.gravity = Gravity.RIGHT;
	  
	   // results output
	   LinearLayout.LayoutParams layout_TextView_CurrentValue = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.FILL_PARENT,	
											LinearLayout.LayoutParams.FILL_PARENT);
	   layout_TextView_CurrentValue.gravity = Gravity.CENTER;
	   
	   SeekBar bar = new SeekBar(getContext());
	   bar.setMax(m_maximum);
	   bar.setProgress((int)this.m_oldValue);
	   bar.setLayoutParams(layout_Seekbar);
	   bar.setOnSeekBarChangeListener(this);
	   
	   m_TextView_CurrentValue = new TextView(getContext());
	   m_TextView_CurrentValue.setText((String)getTitle() + " value: " + bar.getProgress() + "");
	   m_TextView_CurrentValue.setTextSize(18);
	   m_TextView_CurrentValue.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
	   m_TextView_CurrentValue.setGravity(Gravity.LEFT);
	   m_TextView_CurrentValue.setLayoutParams(layout_TextView_CurrentValue);
	   
	   layout.addView(this.m_TextView_CurrentValue);
	   layout.addView(bar);
	   layout.setId(android.R.id.widget_frame);
	   
	   return layout; 
	 }
	 
	 public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
	  
	    progress = Math.round(((float)progress)/m_interval)*m_interval;
	  
	    if(!callChangeListener(progress)){
		    seekBar.setProgress((int)this.m_oldValue); 
		    return; 
	    }
	    
	    seekBar.setProgress(progress);
	    this.m_oldValue = progress;
	    this.m_TextView_CurrentValue.setText((String)getTitle() + " value: " + seekBar.getProgress() + "");
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
	    
	    
	 private void updatePreference(int newValue){
		 m_settings.setVibrateRepeat(newValue);
		 m_settings.save();
	 }
	 
	}