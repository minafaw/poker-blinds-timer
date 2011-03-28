package com.beardedc.pokerblinds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class ActivitySettings extends Activity implements OnClickListener, OnSeekBarChangeListener
{
	private AppSettings m_settings;
	
	private EditText	m_bigBlind;
	private EditText	m_minutes;
	private String		m_validationErr;
	private SeekBar		m_seekBarVibrate;
	private int			m_iVibrateTimes;
	
	/*************************************************************************/
	
	private static final int DIALOG_ERR = 0;
	
	/*************************************************************************/

	public void onCreate(Bundle savedInstanceState)
	{
		m_settings = AppSettings.getSettings(this.getApplicationContext());
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        initControls();
	}
	
	/*************************************************************************/
	@Override
	protected void onPrepareDialog (int id, Dialog dialog)
	{
		AlertDialog d = (AlertDialog)dialog;
		d.setMessage(m_validationErr);
	}
	
	/*************************************************************************/
	
	protected Dialog onCreateDialog(int id)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(m_validationErr)
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() 
		       {
		    	   public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });

		AlertDialog alert = builder.create();
		
		return alert;
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
        
        m_seekBarVibrate = (SeekBar) findViewById(R.id.seekBarVibrate);
        m_seekBarVibrate.setOnSeekBarChangeListener(this);
        
        Button saveButt = (Button)findViewById(R.id.ButtonSave);
        saveButt.setOnClickListener(this);
	}
	
	/*************************************************************************/

	public void onClick(View v)
	{
		if (  v.getId() == R.id.ButtonSave)
		{
			String validIfNull = isValid();
			if (null != validIfNull)
			{
				showDialog(DIALOG_ERR);
			}
			else
			{
				// here read the values and if good allow the save
				m_settings.save();
				
				// here open the new activity Rob's activity
				Intent launchIntent = 
					new Intent(this.getApplicationContext(), Launch.class);
				startActivity(launchIntent);
			}
		}
	}
	
	/*************************************************************************/
	
	private String isValid()
	{
		m_validationErr = "";
		String bigBlind = m_bigBlind.getText().toString();
		Log.d("Blind", bigBlind);
		m_validationErr = isValidBlind(bigBlind); 
		if (m_validationErr != null)
			return m_validationErr;

		String mins = m_minutes.getText().toString();
		Log.d("Mins", mins);
		m_validationErr = isValidMinutes(mins);
		if (m_validationErr != null)
			return m_validationErr;
		
		m_settings.setMinutes(Long.parseLong(mins));
		m_settings.setInitalBigblind(Long.parseLong(bigBlind));
		m_settings.setVibrateRepeat(m_iVibrateTimes);
		
		return null;
	}
	
	/*************************************************************************/
	
	// TODO Add code to check range of blinds
	private String isValidBlind(String blind)
	{
		if (blind.equals(""))
			return "The big blind must be set";

		long l = Long.parseLong(blind);
		if (l % 2 != 0)
			return "The big blind must be divisible by 2, i.e. for the small blind :>";
		else if (l <= 0 )
			return "The blind blind must be greater than 0";
				
		return null;
	}
	
	/*************************************************************************/
	
	private String isValidMinutes(String mins)
	{
		if (mins.equals(""))
			return "The minutes must be set";

		long l = Long.parseLong(mins);
		if (l < 1)
			return "The minutes must be greater than 0";
		
		return null;
	}

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

	public void onStartTrackingTouch(SeekBar seekBar) {}

	public void onStopTrackingTouch(SeekBar seekBar) 
	{
		if (seekBar.getId() == m_seekBarVibrate.getId()) 
		{
			// update the value we now have stored
			m_iVibrateTimes = seekBar.getProgress();
			
			// show value to user
			try
			{
				CharSequence cs = "Vibrate set to: " + String.valueOf(m_iVibrateTimes);
				Toast.makeText(this.getApplicationContext(), cs, Toast.LENGTH_SHORT).show();
			} catch (Exception e)
			{
				return;
			}
			
		}
	}
	
	/*************************************************************************/
}
