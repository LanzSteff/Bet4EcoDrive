package com.example.obd2;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.obd2.dataHandling.Sensors;
import com.example.obd2.utils.Configuration;
import com.example.obd2.utils.SysOutRedirector;
import com.example.obd2.utils.Utilities;

public class Gui extends Activity {
	private static final String TAG = "GUI";

	private Application appl;

	// private boolean debug = true;
	// private boolean queryDebug = true;

	private final File root = Environment.getExternalStorageDirectory();

	private final File configurationFile = new File(root + "/configurationFile");

	private Configuration configuration;

	public SurfaceView preview;
	public Picture picture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getActionBar().setDisplayShowHomeEnabled(false);
		this.getActionBar().setDisplayShowTitleEnabled(false);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SysOutRedirector.redirectSystemErr(new File(root + "/" + "errorLog"));
		SysOutRedirector.redirectSystemOut(new File(root + "/" + "errorLog"));

		setContentView(R.layout.activity_main);

		try {
			configuration = new Configuration(configurationFile);
		} catch (IOException e) {
			Log.v(TAG, "--------" + e.getMessage());
		}

		if (appl == null) {
			appl = new Application(this);

			appl.start();
		}
		Sensors.createInstance(this, appl);
	}

	public void takePicture(String filenamePart_1) {
		picture.takePicture(filenamePart_1);
	}

	public Context getAppContext() {
		return getApplicationContext();
	}

	@Override
	protected void onStart() {
		Log.v(TAG, "onStart");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onStart");

		super.onStart();
	}

	@Override
	protected void onRestart() {
		Log.v(TAG, "onRestart");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onRestart");

		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onResume");

		super.onResume();
		Sensors.getGpsHandler().requestLocUpdates();
	}

	@Override
	protected void onPause() {
		Log.v(TAG, "onPause");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onPause");

		super.onPause();
		//picture._onPause_();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onStop");

		super.onStop();
		//picture.close();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		System.out.println("\n"
				+ Utilities.timestamp2Date(System.currentTimeMillis()) + " "
				+ TAG + " " + "onDestroy");

		super.onDestroy();
		if (Sensors.getAudio() != null) {
			Sensors.getAudio().stop();
		}
		appl.shutDown();
		//picture.close();
		Sensors.unregisterSensorListeners();
		System.exit(0);
	}

	public Configuration getConfigFile() {
		return configuration;
	}
}
