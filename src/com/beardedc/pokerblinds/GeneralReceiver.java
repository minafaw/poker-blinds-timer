package com.beardedc.pokerblinds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GeneralReceiver extends BroadcastReceiver 
{
	private IReturnFinished m_irf;
	public GeneralReceiver(IReturnFinished irf)
	{
		m_irf = irf;
	}
	
	@Override
	public void onReceive(Context context, Intent i)
	{
		m_irf.intentReceived(i);
	}
}