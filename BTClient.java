package at.ispace.sensorapp.devices.elm327;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import at.ispace.sensorapp.utils.DebugLogger;

public class BTClient {
	
	private BluetoothAdapter adapter;
	private BluetoothDevice device;
	private BluetoothSocket socket;
	private boolean connected;
	
	private ConnectedThread connectedThread;
	
	private List<Handler> handlerList = new ArrayList<Handler>();
	
    static final UUID UUID_RFCOMM_GENERIC = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    
	public BTClient(BluetoothAdapter adapter, String macID) {
		this.adapter = adapter;
		this.device = adapter.getRemoteDevice(macID);
		this.socket = null;
		this.connected = false;
	}

	public boolean connect() {
		 BluetoothSocket tmp = null;
		 
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
        } catch (IOException e) { 
        	Log.e("BTClient", e.getMessage());
        	return false;
        }
        socket = tmp;
        
        // Cancel discovery because it will slow down the connection
    	adapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            socket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
            	if (socket != null)
            		socket.close();
            } catch (IOException e) { 
            	DebugLogger.getInstance().error("BTClient", e);
            }
            return false;
        }
        connected=true;
        return connected;
	}
	
	public boolean isConnected() {
		return connected;
	}

	public void start() {
		connectedThread = new ConnectedThread(handlerList, socket);
		connectedThread.start();
		
	}

	public void close() {
		try {
	    	if (connectedThread.isAlive()){
	    		connectedThread.cancel();
	    	}
		} catch (Exception e) { 
			DebugLogger.getInstance().error("BTClient", e);
        }
		
        try {
        	connected = false;
        	
        	if (socket != null)
        		socket.close();
        } catch (IOException e) { 
        	DebugLogger.getInstance().error("BTClient", e);
        }
	}

	public void addHandler(Handler handler) {
		handlerList.add(handler);
	}
	
	public void removeHandler(Handler handler) {
		handlerList.remove(handler);
	}

	
}
