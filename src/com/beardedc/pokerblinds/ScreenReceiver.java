package com.beardedc.pokerblinds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
public class ScreenReceiver extends BroadcastReceiver 
{
	public static boolean wasScreenOn = true;
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
		{
			// do whatever you need to do here
			wasScreenOn = false;
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
		{
			// and do whatever you need to do here
			wasScreenOn = true;
		}
	}
}