package com.example.obd2.dataHandling;

import com.example.obd2.Application;
import com.example.obd2.GPS_Handler;
import com.example.obd2.Gui;
import com.example.obd2.audio.fft.Audio;
import com.example.obd2.sensorDaten.HandlerSensorData;

public class Sensors {

	private static Sensors instance;
	private static GPS_Handler gps;
	private static Audio audio;
	private static HandlerSensorData hsd;

	private Sensors() {
	}

	public static synchronized void createInstance(Gui gui, Application appl) {
		if (instance == null) {
			instance = new Sensors();
			gps = new GPS_Handler(gui, appl);
			hsd = new HandlerSensorData(gui, appl.getEntityManager());
			audio = hsd.getAudio();
		}
	}

	public static void unregisterSensorListeners() {
		hsd.unregisterSensorListeners();
	}

	public static Sensors getInstance() {
		return instance;
	}

	public static GPS_Handler getGpsHandler() {
		return gps;
	}

	public static Audio getAudio() {
		return audio;
	}

	public static HandlerSensorData getHandlerSensorData() {
		return hsd;
	}

}
