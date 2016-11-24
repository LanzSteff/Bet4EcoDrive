package com.example.bet4ecodrive;

import org.apache.cordova.DroidGap;

import android.os.Bundle;
import android.view.Menu;

public class Bet4EcoDrive extends DroidGap {
	private final static int REQUEST_ENABLE_BT = 1;
	private Bet4EcoDriveController controller;
	public Bluetooth bt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init();
		
		controller = new Bet4EcoDriveController(this, appView);
        appView.addJavascriptInterface(controller, "Controller");
//        bt = new Bluetooth(this, appView);
//        appView.addJavascriptInterface(bt, "bt");
		
		//setContentView(R.layout.activity_bet4_eco_drive);
        super.setIntegerProperty("loadUrlTimeoutValue", 700000);
		super.loadUrl("file:///android_asset/www/index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bet4_eco_drive, menu);
		return true;
	}
	
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		controller.btDestroy();
//	}
	
	/*public boolean onOptionsItemSelected(MenuItem item){
	    Intent myIntent = new Intent(getApplicationContext(), Bet4EcoDrive.class);
	    startActivityForResult(myIntent, 0);
	    return true;

	}*/
	
}
