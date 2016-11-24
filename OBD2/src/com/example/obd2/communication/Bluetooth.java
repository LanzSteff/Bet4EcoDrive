/**
 * @author DOETTLINGER
 */

package com.example.obd2.communication;

import java.io.IOException;
import java.lang.reflect.Method;

import com.example.obd2.Gui;
import com.example.obd2.utils.Utilities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Bluetooth extends Connection {

	private static final String TAG = "BLUETOOTH";

	// private static final String MAC_ADDR = "00:06:66:42:E8:84";

	// Dongel Petra//
	// private static final String MAC_ADDR = "00:06:66:42:EC:28";
	private static final String MAC_ADDR = "00:06:71:00:00:06";

	private BluetoothSocket btSocket;

	String err;
	private boolean threadRunning;

	private ConnectionState connectionState;

	public Bluetooth(Gui gui) {

	}

	public boolean isConnectionThreadRunning() {
		return threadRunning;
	}

	@Override
	public void connect() {
		if (!threadRunning) {
			threadRunning = true;
			new Thread() {

				@SuppressWarnings("synthetic-access")
				@Override
				public void run() {
					connect_();
					threadRunning = false;
				}

			}.start();
		} else {
			Utilities.sleep(100);
		}

	}

	private void connect_() {
		disConnect();

		Log.v(TAG, "start connect");

		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "start connect");

		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

		if (!btAdapter.isEnabled()) {

			Log.v(TAG, "btAdapter disabled");
			System.out.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis())
					+ " " + TAG + " " + "btAdapter disabled");

			err = "Please enable your BT and re-run this program.";
			// errorTextField.setText(err);
			handlerErr.sendEmptyMessage(0);
			Log.v(TAG, "btAdapter disabled2");
			System.out.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis())
					+ " " + TAG + " " + "btAdapter disabled2");

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				System.err.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis()));
				e1.printStackTrace();
			}
		}

		Log.v(TAG, " btAdapter");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "btAdapter");

		BluetoothDevice device = btAdapter.getRemoteDevice(MAC_ADDR);
		try {
			Log.v(TAG, "Method m = device.getClass");
			System.out.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis())
					+ " " + TAG + " " + "device.getClass");

			Method m = device.getClass().getMethod("createRfcommSocket",
					new Class[] { int.class });
			Log.v(TAG, "m.invoke");
			System.out.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis())
					+ " " + TAG + " " + "m.invoke");

			btSocket = (BluetoothSocket) m.invoke(device, 1);

			Log.v(TAG, "btSocket.isConnected()");
			System.out.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis())
					+ " " + TAG + " " + "btSocket.isConnected()");

			if (!btSocket.isConnected()) {
				Log.v(TAG, "start connect");
				System.out.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis())
						+ " " + TAG + " " + "start connect");

				btSocket.connect();

				Log.v(TAG, "end connect");
				System.out.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis())
						+ " " + TAG + " " + "handlerErr");

			}
		} catch (Exception e) {

			System.err.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis()));
			e.printStackTrace();
			try {
				btSocket.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Log.e("BT", "#####" + e.getMessage());
		}
	}

	@Override
	public void disConnect() {
		if (btSocket != null) {
			try {
				btSocket.close();
			} catch (Exception e) {
				System.err.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis()));
				e.printStackTrace();
			}
			btSocket = null;
		}
	}

	public boolean isConnected() {
		if (threadRunning) {
			return false;
		}

		if (btSocket == null) {
			return false;
		} else {
			return btSocket.isConnected();
		}
	}

	@Override
	public ConnectionState getConnectionState() {
		return connectionState;
	}

	@Override
	public void write(String msg) throws IOException {
		btSocket.getOutputStream().write(msg.getBytes());
		Utilities.sleep(100);
	}

	@Override
	public int read(byte[] buf) throws IOException {
		return btSocket.getInputStream().read(buf);
	}

	@Override
	public void setConnectionState(ConnectionState connectionState) {
		this.connectionState = connectionState;
	}

	private Handler handlerErr = new Handler() {
		@Override
		public void handleMessage(Message msg_) {
			Log.v(TAG, "handlerErr");
			// System.out.println("\n"
			// + Utilities.timestamp2Date(System.currentTimeMillis())
			// + " " + TAG + " " + "handlerErr");

			// errorTextField.setText(err + "\n");
		}
	};
}
