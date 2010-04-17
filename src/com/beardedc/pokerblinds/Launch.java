package com.beardedc.pokerblinds;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;

public class Launch extends Activity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.main);

        Chronometer cn = (Chronometer) findViewById(R.id.BlindTimer);
        cn.setBase(SystemClock.elapsedRealtime() - 2000);
        cn.start();
    }
}