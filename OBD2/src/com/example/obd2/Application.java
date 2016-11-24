/**
 * @author DOETTLINGER
 */

package com.example.obd2;

import java.io.File;

import com.example.obd2.communication.Bluetooth;
import com.example.obd2.communication.Connection;
import com.example.obd2.dataHandling.DataEngine;
import com.example.obd2.dataHandling.EntityManager;
import com.example.obd2.subscriber.GuiSubscriber;
import com.example.obd2.utils.Utilities;

import android.content.Context;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

public class Application extends Thread {

	private Connection con;
	private DataEngine dataEngine;
	private static final String TAG = "APPLICATION";
	private PowerManager pm;
	PowerManager.WakeLock wl;

	Gui gui;

	File root = Environment.getExternalStorageDirectory();

	public Application(Gui gui) {
		this.gui = gui;

		pm = (PowerManager) gui.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "foo");

		Log.v(TAG, "new Bluetooth_1");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "new Bluetooth_1");

		this.con = new Bluetooth(gui);
		Log.v(TAG, "new Bluetooth_2");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "new Bluetooth_2");

		dataEngine = new DataEngine(con, gui);
		dataEngine.addSubscriber(new GuiSubscriber(gui));
		// dataEngine.addSubscriber(new Einparkhilfe(viewHandler, gui));
		// dataEngine.addSubscriber(new EinparkhilfeTest(gui));
	}

	public EntityManager getEntityManager() {
		return dataEngine.getEntityManager();
	}

	public void shutDown() {
		/** terminating subscriber */
		try {
			con.disConnect();
			dataEngine.onShutDown();
		} catch (Exception e) {
			System.err.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis()));
			e.printStackTrace();
		}
	}

	long t = System.currentTimeMillis() + 1000;
	long t_act;

	@Override
	public void run() {
		if (!con.isConnected()) {
			con.disConnect();
			con.connect();
		}

		while (true) {
			t_act = System.currentTimeMillis();
			if (t_act > t) {
				wl.acquire();
				dataEngine.exec();
				t = t_act + 1000;
			}
			Utilities.sleep(10);
		}
	}
}
