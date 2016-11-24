package com.example.bet4ecodrive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;
import javax.microedition.io.StreamConnection;
import org.apache.cordova.DroidGap;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;



public class Bet4EcoDriveController extends Activity {
	private final static String OBDMAC = "00:06:71:00:00:06";
	private final static UUID OBDUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	public InputStream in_stream;
	public OutputStream out_stream;
	private final static int REQUEST_ENABLE_BT = 1;
	private WebView mAppView;
	private DroidGap mGap;
	public BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private boolean connected;
	StreamConnection client;
	private BluetoothSocket sockFallback = null;
	public BluetoothSocket finalSocket = null;
	public boolean running = false;
	public int speed = 0, rpm = 0;
	public byte[] testByte = null;

	// Create a constructor with an DroidGap and WebView object for classes where methods should be later used in JavaScript
	public Bet4EcoDriveController(DroidGap gap, WebView view) {
		mAppView = view;
		mGap = gap;
	}
	
	// Hint: You need the "@JavascritInterface" before each method that should be called from a JavaScript file (at least in most never Android versions)
	// Check if a bluetooth adapter is available
	@JavascriptInterface
	public boolean checkBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    return false;
		}
		else {
			return true;
		}
		
	}
	
	// If not enabled, enable the bluetooth adapter
	@JavascriptInterface
	public boolean enableBluetooth() {
		if(!mBluetoothAdapter.isEnabled()) {
			mBluetoothAdapter.enable();
		}
		return true;
	}
	
	// Create bluetooth connection 
	@JavascriptInterface
	public boolean connect() {
		device = mBluetoothAdapter.getRemoteDevice(OBDMAC);
		socket = null;
		connected = false;

		BluetoothSocket tmp = null;
		
		try {
		      // Instantiate a bluetooth socket for the remote device and connect it using the mac address from the obd-device and the uuid
		      tmp = device.createRfcommSocketToServiceRecord(OBDUUID);
		      socket = tmp;
		      socket.connect();
		      connected = true;
		      System.out.println(socket.isConnected());
		      if(socket.isConnected())
		    	  finalSocket = socket;
		     
		      // Initialize the OBD dongle
		      init_dongle();
		      get_data();
		      
		} catch (Exception e1) {
		      Class<?> clazz = socket.getRemoteDevice().getClass();
		      Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
				
		      try {
		    	  // Try an alternative way to instantiate a bluetooth socket for the remote device and connect it
		    	  Method m = clazz.getMethod("createInsecureRfcommSocket", paramTypes);
				  Object[] params = new Object[]{Integer.valueOf(1)};
				  mBluetoothAdapter.cancelDiscovery();
				  sockFallback = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
				  sockFallback.connect();
				  connected = true;
				  System.out.println(sockFallback.isConnected());
				  if(sockFallback.isConnected())
				       	finalSocket = sockFallback;
				  
				// Initialize the OBD dongle
				  init_dongle();
				  
			  } catch (Exception e2) {
				  Log.e("Bluetooth connection failed:", e2.getMessage());
				  connected = false;
			  }
		 }
        return connected;
	}
	
	// Close the socket and reconnect
	@JavascriptInterface
	private void reconnect() {
		try {
			socket.close();
			sockFallback.close();
			finalSocket.close();
			out_stream.close();
			int count = 1;
			while (!connected && count < 10) {
				connect();
				count++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// Initialize the OBD dongle
	@JavascriptInterface
	private void init_dongle() {
		byte[] ATZ = {'A', 'T', 'Z', '\r', '\n'};		// Reset
	 	byte[] ATE0 = {'A', 'T', 'E', '0','\r', '\n'};	// Echo off
	 	byte[] ATH0 = {'A', 'T', 'H', '0','\r', '\n'};	// Headers off
	 	byte[] ECU = {'0', '1', '0', '0','\r', '\n'};	// Check ECU connection

	 	try {
	 		out_stream = finalSocket.getOutputStream();
	 		out_stream.write(ATZ);
	 		out_stream.flush();
			Thread.sleep(200);
			read_data();
			out_stream.write(ATE0);
			out_stream.flush();
			read_data();
			out_stream.write(ATH0);
			out_stream.flush();
			read_data();
			out_stream.write(ECU);
			out_stream.flush();
	 		
			if (new String(read_data(), "us-ascii").contains("ERROR"))
				connected = false;
			running = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Read bytes and write it into an array
	@JavascriptInterface
	public byte[] read_data() {
		byte[] bytesRead = null;
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int b;
			while ((b = finalSocket.getInputStream().read()) != -1) {
				if (b == '>')
					break;
				buf.write(b);
			}
			bytesRead = buf.toByteArray();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return bytesRead;
    }
	
	// Get RPM data
	@JavascriptInterface
	public int get_rpm() {
		byte[] RPM = {'0', '1', '0', 'C', '\r', '\n'};	// RPM (2 byte hex / 4)
		try {
			out_stream.write(RPM);
			out_stream.flush();
			int rpm_temp = evaluate_data(read_data());
			if (rpm_temp != -1)
				rpm = rpm_temp;
		}
		catch (Exception e) {
			e.printStackTrace();
			reconnect();
		}
		return rpm;
	}
	
	
	// Print RPM data
	@JavascriptInterface
	public void get_data() {
		while(running) {
			byte[] RPM = {'0', '1', '0', 'C', '\r', '\n'};	// RPM (2 byte hex / 4)

			try {
				out_stream.write(RPM);
				out_stream.flush();
				int rpm_temp = evaluate_data(read_data());
				if (rpm_temp != -1)
					rpm = rpm_temp;
				System.out.println("RPM: " + rpm);
			}
			catch (Exception e) {
				e.printStackTrace();
				reconnect();
			}
			try {
				Thread.sleep(1000);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Evaluates the byte array and parse the bytes to return the RPM value
	private int evaluate_data(byte[] b) {
		String b_str = "";
		int res = 0;
		try {
			b_str = new String(b, "us-ascii");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		b_str = b_str.replaceAll(" ", "");
		b_str = b_str.replaceAll("\r", "");
		b_str = b_str.replaceAll("\n", "");
		if (b_str.length() == 6) {
			b_str = b_str.substring(4,6);
			res = Integer.parseInt(b_str, 16);
		}
		else if (b_str.length() == 8) {
			b_str = b_str.substring(4,8);
			res = (Integer.parseInt(b_str, 16))/4;
		}
		else
			res = -1;
		return res;
	}
	
	// Find bluetooth device
	@JavascriptInterface
	public String findDevice() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		for(BluetoothDevice device : pairedDevices) {
			String name = device.getName();
			String macAddress = device.getAddress();
			String uuid = device.getUuids()[0].toString();
			System.out.println("Name: " + name);
			System.out.println("Mac-Address: " + macAddress);
			return name + " / " + macAddress + " / " + uuid;
		}
		return " ";
	}	
	
}