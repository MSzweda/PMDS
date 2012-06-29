package com.herring.pmds.devices;

import android.bluetooth.BluetoothAdapter;

/*
 * class for managing bluetooth state
 */
public class BluetoothDevice 
{
	//Bluetooth adapter object
	static BluetoothAdapter bluetoothAdapter;
	public static void turnOff()
	{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
	    if (bluetoothAdapter.isEnabled()) 
	    {
	    	bluetoothAdapter.disable(); 
	    } 
	}
	
	public static void turnOn()
	{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
	    if (!bluetoothAdapter.isEnabled()) 
	    {
	    	bluetoothAdapter.enable();
	    } 
	}
	
	public static boolean isEnabled()
	{
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
	    if (!bluetoothAdapter.isEnabled()) 
	    {
	    	return false;
	    } 
	    else return true;
	}
}
