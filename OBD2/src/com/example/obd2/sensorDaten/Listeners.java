package com.example.obd2.sensorDaten;

import java.util.Arrays;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class Listeners {

	// private final String TAG = "Listeners";

	/***************************** ACCELERATION *******************************/

	float[] acceleration = null;

	public void setAcceleration(float[] acc) {
		this.acceleration = acc;
	}

	public float[] getAcceleration() {
		return acceleration;
	}

	public SensorEventListener ListenerAcceleration = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				float[] values = event.values;
				setAcceleration(Arrays.copyOf(event.values, 3));

			}
		}
	};

	/***************************** MAGNETIC FIELD *******************************/

	float[] magneticField = null;

	public void setMagneticField(float[] magneticField) {
		this.magneticField = magneticField;
	}

	public float[] getMagneticField() {
		return magneticField;
	}

	public SensorEventListener ListenerMagneticField = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				setMagneticField(Arrays.copyOf(event.values, 3));
			}
		}
	};

	/***************************** ORIENTATION *******************************/

	float[] orientation;

	void setOrientation(float[] orientation) {
		this.orientation = orientation;
	}

	public float[] getOrientation() {
		return orientation;
	}

	public SensorEventListener ListenerOrientation = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				setOrientation(Arrays.copyOf(event.values, 3));
				// Log.v(TAG, Arrays.toString(values));
			}
		}
	};

	/***************************** LIGHT *******************************/

	float[] light;

	void setLight(float[] light) {
		this.light = light;
	}

	public float[] getLight() {
		return light;
	}

	public SensorEventListener ListenerLightSensor = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				setLight(Arrays.copyOf(event.values, 1));
			}
		}
	};

	/***************************** ROTATION *******************************/

	float[] rotation;

	void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	public float[] getRotation() {
		return rotation;
	}

	public SensorEventListener ListenerRotation = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
				setRotation(Arrays.copyOf(event.values, 3));
			}
		}
	};

	/***************************** PROXIMITY SENSOR *******************************/

	float[] proximity;

	void setProximity(float[] proximity) {
		this.proximity = proximity;
	}

	public float[] getProximity() {
		return proximity;
	}

	public SensorEventListener ListenerProximitySensor = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
				setProximity(Arrays.copyOf(event.values, 1));
			}
		}
	};

	/***************************** GYROSCOPE SENSOR *******************************/

	float[] gyroscope;

	void setGyroscope(float[] gyroscope) {
		this.gyroscope = gyroscope;
	}

	public float[] getGyroscope() {
		return gyroscope;
	}

	public SensorEventListener GyroscopeListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				setGyroscope(Arrays.copyOf(event.values, 3));
			}
		}
	};

	/***************************** PRESSURE SENSOR *******************************/

	float[] pressure;

	void setPressure(float[] pressure) {
		this.pressure = pressure;
	}

	public float[] getPressure() {
		return pressure;
	}

	public SensorEventListener PressureListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				setPressure(Arrays.copyOf(event.values, 1));
			}
		}
	};

	/***************************** LINEAR ACCELERATION *******************************/

	float[] linearAcc;

	void setLinearAcc(float[] linearAcc) {
		this.linearAcc = linearAcc;
	}

	public float[] getLinearAcc() {
		return linearAcc;
	}

	public SensorEventListener ListenerLinearAcc = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
				setLinearAcc(Arrays.copyOf(event.values, 3));
			}
		}
	};

	/***************************** GRAVITY *******************************/

	float[] gravity;

	void setGravity(float[] gravity) {
		this.gravity = gravity;
	}

	public float[] getGravity() {
		return gravity;
	}

	public SensorEventListener ListenerGravity = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
				setGravity(Arrays.copyOf(event.values, 3));
			}
		}
	};

	/***************************** TEMPERATURE *******************************/

	float[] temp;

	void setTemp(float[] temp) {
		this.temp = temp;
	}

	public float[] getTemp() {
		return temp;
	}

	public SensorEventListener ListenerTemp = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onSensorChanged(SensorEvent event) {

			if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
				setTemp(Arrays.copyOf(event.values, 1));
			}
		}
	};
}
