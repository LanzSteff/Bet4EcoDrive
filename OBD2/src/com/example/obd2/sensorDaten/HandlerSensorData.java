package com.example.obd2.sensorDaten;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.obd2.Gui;
import com.example.obd2.audio.fft.Audio;
import com.example.obd2.dataHandling.EntityManager;

public class HandlerSensorData {
	private List<Sensor> listSensor;
	private List<String> listSensorType = new ArrayList<String>();

	private SensorManager sensorManager;

	private Listeners listeners;

	private EntityManager em;
	//private String TAG = "HandlerSensorData";
	private static Audio audio;
	private Gui gui;

	public HandlerSensorData(Gui gui, EntityManager em) {

		this.em = em;
		this.gui = gui;
		sensorManager = (SensorManager) gui
				.getSystemService(Context.SENSOR_SERVICE);
		listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL);

		for (int i = 0; i < listSensor.size(); i++) {
			listSensorType.add(listSensor.get(i).getName() + "  "
					+ listSensor.get(i).getMaximumRange() + "  "
					+ listSensor.get(i).getResolution());
		}
		listeners = new Listeners();
		registerListeners();
		registerAudio();
	}

	public void unregisterSensorListeners() {
		sensorManager.unregisterListener(listeners.ListenerOrientation);
		sensorManager.unregisterListener(listeners.ListenerAcceleration);
		sensorManager.unregisterListener(listeners.ListenerMagneticField);
		sensorManager.unregisterListener(listeners.ListenerLightSensor);
		sensorManager.unregisterListener(listeners.ListenerRotation);
		sensorManager.unregisterListener(listeners.ListenerProximitySensor);
		sensorManager.unregisterListener(listeners.GyroscopeListener);
		sensorManager.unregisterListener(listeners.PressureListener);
		sensorManager.unregisterListener(listeners.ListenerLinearAcc);
		sensorManager.unregisterListener(listeners.ListenerGravity);
	}

	public static Audio getAudio() {
		return audio;
	}

	public void registerAudio() {
		if (em.getEntity(0xEF).isEnabled()) {
			audio = new Audio(gui);
			audio.start();
		}
	}

	public void registerListeners() {
		if (em.getEntity(0xD0).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerOrientation,
					sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD1).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerAcceleration,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD2).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerMagneticField,
					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD3).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerLightSensor,
					sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD4).isEnabled()) {
			sensorManager
					.registerListener(listeners.ListenerRotation, sensorManager
							.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
							SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD5).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerProximitySensor,
					sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD6).isEnabled()) {
			sensorManager.registerListener(listeners.GyroscopeListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		if (em.getEntity(0xD7).isEnabled()) {
			sensorManager.registerListener(listeners.PressureListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
					SensorManager.SENSOR_DELAY_NORMAL);
		}

		if (em.getEntity(0xD8).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerLinearAcc,
					sensorManager
							.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
					SensorManager.SENSOR_DELAY_NORMAL);
		}

		if (em.getEntity(0xD9).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerGravity,
					sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
					SensorManager.SENSOR_DELAY_NORMAL);
		}

		if (em.getEntity(0xDA).isEnabled()) {
			sensorManager.registerListener(listeners.ListenerTemp,
					sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE),
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		// showSensorList();
	}

	// private void showSensorList() {
	// for (int i = 0; i < listSensorType.size(); i++) {
	// // textField.append(listSensorType.get(i) + "\n");
	// }
	// }

	public float[] getDataObjectOrientation() {
		return listeners.getOrientation();
	}

	public float[] getDataObjectAcceleration() {
		return listeners.getAcceleration();
	}

	public float[] getDataObjectMagneticField() {
		return listeners.getMagneticField();
	}

	public float[] getDataObjectLight() {
		return listeners.getLight();
	}

	public float[] getDataObjectRotation() {
		return listeners.getRotation();
	}

	public float[] getDataObjectProximity() {
		return listeners.getProximity();
	}

	public float[] getDataObjectGyroscope() {
		return listeners.getGyroscope();
	}

	public float[] getDataObjectPressure() {
		return listeners.getPressure();
	}

	public float[] getDataObjectLinearAcc() {
		return listeners.getLinearAcc();
	}

	public float[] getDataObjectGravity() {
		return listeners.getGravity();
	}

	public float[] getDataObjectTemp() {
		return listeners.getTemp();
	}
}
