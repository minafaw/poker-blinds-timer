package com.beardedc.pokerblinds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// http://thinkandroid.wordpress.com/2010/01/24/handling-screen-off-and-screen-on-intents/
public class ScreenReceiver extends BroadcastReceiver 
{
	public static boolean screenSwitchOffEventOccured = false;
	public static boolean screenSwitchOnEventOccured = false;
	
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
		{
			screenSwitchOffEventOccured = true;
			screenSwitchOnEventOccured = false;
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
		{
			screenSwitchOffEventOccured = true;
			screenSwitchOnEventOccured = false;
		} else {
			screenSwitchOffEventOccured = false;
			screenSwitchOnEventOccured = false;
		}
	}
}