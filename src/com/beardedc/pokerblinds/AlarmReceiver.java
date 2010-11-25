package com.beardedc.pokerblinds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver
{
	static final String m_strAlarmBroadcast = "beardedc.pokerBlinksTimer.AlarmAction";
	static final String m_strAlarmFinished = "beardedc.pokerBlinksTimer.AlarmTimerUp";
	private IReturnFinished m_irf;
	
	public AlarmReceiver(IReturnFinished irf)
	{
		m_irf = irf;
	}
	
	public void onReceive(Context context, Intent intent)
	{
		if (intent.getAction().equals(m_strAlarmFinished))
		{
			m_irf.jobDone();
		}
	}
}