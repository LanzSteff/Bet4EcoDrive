package com.example.bet4ecodrive;

import java.util.Set;

import org.apache.cordova.DroidGap;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.webkit.WebView;

public class Bluetooth extends Activity {
	private WebView mAppView;
	private DroidGap mGap;
	public BluetoothAdapter bluetoothAdapter;
	
	public Bluetooth(DroidGap gap, WebView view) {
		mAppView = view;
		mGap = gap;
	}
	
	public void connectToBluetooth() {
		boolean bool = hasBluetooth();
		System.out.println("BT Done: hasBluetooth()");
		if(bool) {
			enableBluetooth();
			findDevice();
		}
	}
	
	public boolean hasBluetooth() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(bluetoothAdapter == null) {
			System.err.println("Device is not supporting Bluetooth.");
			return false;
		}
		return true;
	}
	
	public void enableBluetooth() {
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
	}
	
	public void findDevice() {
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device : pairedDevices) {
			String name = device.getName();
			String macAddress = device.getAddress();
			System.out.println("Name: " + name);
			System.out.println("Mac-Address: " + macAddress);
		}
	}
	
	
}