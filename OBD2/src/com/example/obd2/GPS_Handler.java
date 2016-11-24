package com.example.obd2;

import java.util.Iterator;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPS_Handler implements LocationListener {
	LocationManager locManager;

	Application app;

	// private static final String TAG = "GPS_LISTENER";
	GPS_DataObject gpsDataObject = new GPS_DataObject(null, false);

	public GPS_Handler(Gui gui, Application app) {
		this.app = app;
		this.locManager = (LocationManager) gui
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				this);
		this.locManager.addGpsStatusListener(gpsListener);
	}

	public GPS_DataObject getDataObj() {
		return gpsDataObject;
	}

	public void requestLocUpdates() {
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1,
				this);
	}

	@Override
	public void onLocationChanged(Location location) {
		gpsDataObject = new GPS_DataObject(location, true);
		// app.execGPS(gpsDataObject);
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		GpsStatus status;

		public void onGpsStatusChanged(int event) {
			if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
			}
			status = locManager.getGpsStatus(status);
			int cnt = 0;
			Iterator<GpsSatellite> it = status.getSatellites().iterator();

			int nrOfSatellitesUsedInFix = 0;

			while (it.hasNext()) {
				GpsSatellite gs = it.next();
				boolean inFix = gs.usedInFix();
				if (inFix) {
					nrOfSatellitesUsedInFix++;
				}

				cnt++;
				// Log.v(TAG, "nrOfSatellitesUsedInFix: "
				// + nrOfSatellitesUsedInFix);
			}
			if (nrOfSatellitesUsedInFix > 2) {
				gpsDataObject.locationAvailable = true;
			} else {
				gpsDataObject.locationAvailable = false;
			}
		}
	};

	public class GPS_DataObject {
		public final Location location;
		public boolean locationAvailable;

		public GPS_DataObject(Location location, boolean locationAvailable) {
			this.location = location;
			this.locationAvailable = locationAvailable;
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
