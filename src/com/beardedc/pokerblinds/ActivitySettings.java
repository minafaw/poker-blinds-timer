package com.beardedc.pokerblinds;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivitySettings extends Activity implements OnClickListener
{
	private AppSettings m_settings;
	
	private EditText	m_bigBlind;
	private EditText	m_minutes;
	
	private String		m_validationErr;

	public void onCreate(Bundle savedInstanceState)
	{
		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initControls();
	}
	
	/*************************************************************************/
	
	private void initControls()
	{
		Long minutes = m_settings.getMinutes();
		
		Log.d("Loading, value of minutes:" , minutes.toString());
        
		m_minutes = (EditText)findViewById(R.id.EditTextMinutes);
		m_minutes.setText(minutes.toString());
        
        Long bigBlind = m_settings.getInitalBigblind();
        m_bigBlind = (EditText)findViewById(R.id.EditTextBlind);
        m_bigBlind.setText(bigBlind.toString());
        
        Button saveButt = (Button)findViewById(R.id.ButtonSave);
        saveButt.setOnClickListener(this);
	}
	
	/*************************************************************************/

	public void onClick(View v)
	{
		if (  v.getId() == R.id.ButtonSave)
		{
			String s = isValid();
			Log.d("moo", s);
			// here read the values and if good allow the save
		}
		
	}
	
	/*************************************************************************/
	
	private String isValid()
	{
		m_validationErr = "";
		String bigBlind = m_bigBlind.getText().toString();
		if (isValidBlind(bigBlind) == false)
		{
			return "The big blinds must be divisible by 2 and greater than 0";
		}
			
		return bigBlind;
	}
	
	/*************************************************************************/
	
	// TODO Add code to check range of blinds
	private boolean isValidBlind(String blind)
	{
		if (blind.equals(""))
		{
			m_validationErr = "The big blind must be set";
			return false;
		}
		//else if ()
				
		return true;
	}
	
	
	
	/*************************************************************************/
}
