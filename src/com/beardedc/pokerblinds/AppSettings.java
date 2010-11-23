package com.beardedc.pokerblinds;

public class AppSettings 
{
	private int 	_minutes;
	private int		_initalBigblind;
	private int		_currentBigBlind;
	
	public int get_minutes() 
	{
		return _minutes;
	}
	
	public void set_minutes(int _minutes) 
	{
		this._minutes = _minutes;
	}
	
	public int get_bigblind() 
	{
		return _initalBigblind;
	}
	
	public void set_bigblind(int _bigblind) 
	{
		this._initalBigblind = _bigblind;
	}
	
}
