package com.example.bet4ecodrive;

import org.apache.cordova.DroidGap;

import android.os.Bundle;
import android.view.Menu;

public class Bet4EcoDrive extends DroidGap {
	private Bet4EcoDriveController controller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.init();
		
		controller = new Bet4EcoDriveController(this, appView);
        appView.addJavascriptInterface(controller, "Controller");
        
        super.setIntegerProperty("loadUrlTimeoutValue", 700000);
		super.loadUrl("file:///android_asset/www/index.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bet4_eco_drive, menu);
		return true;
	}
	
}
