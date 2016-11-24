/**
 * @author DOETTLINGER
 */

package com.example.obd2.dataHandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.example.obd2.GPS_Handler.GPS_DataObject;
import com.example.obd2.audio.fft.Audio;
import com.example.obd2.communication.CommunicationHandler;
import com.example.obd2.communication.CommunicationReply;
import com.example.obd2.communication.Connection;
import com.example.obd2.dataHandling.Entity.ParamDescription;
import com.example.obd2.reply.Reply;
import com.example.obd2.reply.Reply_DataSet;
import com.example.obd2.sensorDaten.HandlerSensorData;

import android.util.Log;

public class EntityManager {
	private static final String TAG = "ENTITY_MANAGER";
	private final SortedMap<Integer, Entity> map;
	private final CommunicationHandler comH;

	public EntityManager(Connection con) {
		this.comH = new CommunicationHandler(con);
		map = new TreeMap<Integer, Entity>();
		initializeEntities();
	}

	private void addEntity(Entity e) {
		map.put(e.ID, e);
	}

	public Entity getEntity(int id) {
		return map.get(id);
	}

	public Set<Integer> getPIDs() {
		return map.keySet();
	}

	public boolean isMotorOff() {

		return comH.isMotorOff();

	}

	public Reply getReply(int id) {
		Entity en = getEntity(id);
		Reply_DataSet r = null;
		long timestamp = System.currentTimeMillis();
		if (en.isEnabled()) {
			if (en instanceof OBD2Entity) {
				OBD2Entity obd2en = (OBD2Entity) en;
				CommunicationReply comReply = comH
						.executeOBD2Query(obd2en.QUERY_STRING);
				Object val = null;
				if (comReply.status == 0) {
					val = obd2en.convert(comReply.data);
					// Log.v("", ">>>>Val=" + val);
				} else {
					Log.v(TAG, "Status=" + comReply.status);
				}
				r = new Reply_DataSet(obd2en, timestamp, val, comReply.status);

			} else if (en instanceof DataEntity) {
				DataEntity dataEn = (DataEntity) en;
				Object val = dataEn.getValue();
				r = new Reply_DataSet(dataEn, timestamp, val, 0);

				/**
				 * Reply_DataSet elem = (Reply_DataSet) r; Object o =
				 * (ArrayList<Object>) elem.VAL; ArrayList<Object> al =
				 * (ArrayList<Object>) o; float lv = (Float) (al.get(0)); int iv
				 * = (int) lv;
				 **/
			} else {
				throw new RuntimeException();
			}
		}
		return r;
	}

	// ------------------------------------------------------------------------

	private void initializeEntities() {

		// Rohdaten

		OBD2Entity pids01_20 = new OBD2Entity(0x00, "pids_0x01_0x20", "-") {
			@Override
			public String convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(pids01_20);

		// Rohdaten

		OBD2Entity monitorStatusSinceDTCsCleared = new OBD2Entity(0x01,
				"monitorStatus_sinceDTCcleared", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(monitorStatusSinceDTCsCleared);

		// Rohdaten

		OBD2Entity freezeDTC = new OBD2Entity(0x02, "freezeDTC", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(freezeDTC);

		// Rohdaten

		OBD2Entity fuelSystemStatus = new OBD2Entity(0x03, "fuelSystemStatus",
				"-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(fuelSystemStatus);

		OBD2Entity calculatedEngingeLoadValue = new OBD2Entity(0x04,
				"calc_enginge_load_value", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 100 / 255));
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(calculatedEngingeLoadValue);

		OBD2Entity engineCoolantTemperature = new OBD2Entity(0x05,
				"engine_coolant_temp", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] - 40);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(engineCoolantTemperature);

		OBD2Entity shortTermFuel_B1 = new OBD2Entity(0x06,
				"short_term_fuel_b1", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] - 128) * 100 / 128);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermFuel_B1);

		OBD2Entity shortTermFuel_B1_ = new OBD2Entity(0x07,
				"short_term_fuel_b1", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] - 128) * 100 / 128);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermFuel_B1_);

		OBD2Entity shortTermFuel_B2 = new OBD2Entity(0x08,
				"short_term_fuel_b2", "%") {

			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] - 128) * 100 / 128);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermFuel_B2);

		OBD2Entity shortTermFuel_B2_ = new OBD2Entity(0x09,
				"short_term_fuel_b2", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] - 128) * 100 / 128);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermFuel_B2_);

		OBD2Entity fuelPressure = new OBD2Entity(0x0A, "fuel_pressure", "kPa") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] * 3);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(fuelPressure);

		OBD2Entity intakeManifoldAbsolutePressure = new OBD2Entity(0x0B,
				"infold_manifold_abs_pressure", "kPa") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0]);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(intakeManifoldAbsolutePressure);

		OBD2Entity rpm = new OBD2Entity(0x0C, "engine_rpm", "min^-1") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] * 256 + params[1]) / 4;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(rpm);

		OBD2Entity speed = new OBD2Entity(0x0D, "speed", "km/h") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(speed);

		OBD2Entity timingAdvance = new OBD2Entity(0x0E, "timing_advance", "°") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] / 2) - 64);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(timingAdvance);

		OBD2Entity intakeAirTemp = new OBD2Entity(0x0F, "intake_air_temp", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] - 40);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(intakeAirTemp);

		OBD2Entity MAFAirFlowRate = new OBD2Entity(0x10, "MAF_air_flow_rate",
				"g/s") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 100;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(MAFAirFlowRate);

		OBD2Entity ThrottlePosition = new OBD2Entity(0x11, "throttle_position",
				"%") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] * 100 / 255);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(ThrottlePosition);

		// Rohdaten

		OBD2Entity commandedSecondaryAirStatus = new OBD2Entity(0x12,
				"commanded_secondary_air_status", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(commandedSecondaryAirStatus);

		// Rohdaten

		OBD2Entity oxygenSensorPresent = new OBD2Entity(0x13,
				"oxygenSensorPresent", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(oxygenSensorPresent);

		// Rohdaten

		OBD2Entity bank1Sensor1 = new OBD2Entity(0x14, "bank1_sensor1", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank1Sensor1);

		// Rohdaten

		OBD2Entity bank1Sensor2 = new OBD2Entity(0x15, "bank1_sensor2", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank1Sensor2);

		// Rohdaten

		OBD2Entity bank1Sensor3 = new OBD2Entity(0x16, "bank1_sensor3", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank1Sensor3);

		// Rohdaten

		OBD2Entity bank1Sensor4 = new OBD2Entity(0x17, "bank1_sensor4", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank1Sensor4);

		// Rohdaten

		OBD2Entity bank2Sensor1 = new OBD2Entity(0x18, "bank2_sensor1", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank2Sensor1);

		// Rohdaten

		OBD2Entity bank2Sensor2 = new OBD2Entity(0x19, "bank2_sensor2", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank2Sensor2);

		// Rohdaten

		OBD2Entity bank2Sensor3 = new OBD2Entity(0x1A, "bank2_sensor3", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank2Sensor3);

		// Rohdaten

		OBD2Entity bank2Sensor4 = new OBD2Entity(0x1B, "bank2_sensor4", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(bank2Sensor4);

		// Rohdaten

		OBD2Entity obd2Standards = new OBD2Entity(0x1C, "obd2_standards", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(obd2Standards);

		// Rohdaten

		OBD2Entity oxygenSensorsPresent = new OBD2Entity(0x1D,
				"oxygen_sensors_present", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(oxygenSensorsPresent);

		// Rohdaten

		OBD2Entity auxiliarInputStatus = new OBD2Entity(0x1E,
				"auxiliar_input_status", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(auxiliarInputStatus);

		OBD2Entity runtimeSinceEngineStart = new OBD2Entity(0x1F,
				"runtime_since_engine_start", "s") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(runtimeSinceEngineStart);

		// Rohdaten

		OBD2Entity pids21_40 = new OBD2Entity(0x20, "pids_0x21_0x40", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(pids21_40);

		OBD2Entity MIL = new OBD2Entity(0x21, "mil", "km") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(MIL);

		OBD2Entity FuelRailPressureRelativToMV = new OBD2Entity(0x22,
				"fuel_rail_pressure_relative", "kPa") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) * 0.079;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(FuelRailPressureRelativToMV);

		// Rohdaten

		OBD2Entity FuelRailPressureDirectInject = new OBD2Entity(0x23,
				"fuel_rail_pressure_direct_inject", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(FuelRailPressureDirectInject);

		// Rohdaten

		OBD2Entity O2S1_WR_lambda_1_ER_Voltage = new OBD2Entity(0x24,
				"O2S1_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S1_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S2_WR_lambda_1_ER_Voltage = new OBD2Entity(0x25,
				"O2S2_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S2_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S3_WR_lambda_1_ER_Voltage = new OBD2Entity(0x26,
				"O2S3_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S3_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S4_WR_lambda_1_ER_Voltage = new OBD2Entity(0x27,
				"O2S4_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S4_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S5_WR_lambda_1_ER_Voltage = new OBD2Entity(0x28,
				"O2S5_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S5_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S6_WR_lambda_1_ER_Voltage = new OBD2Entity(0x29,
				"O2S6_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S6_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S7_WR_lambda_1_ER_Voltage = new OBD2Entity(0x2A,
				"O2S7_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S7_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity O2S8_WR_lambda_1_ER_Voltage = new OBD2Entity(0x2B,
				"O2S8_WR_lambda(1)_ER_Voltage", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S8_WR_lambda_1_ER_Voltage);

		// Rohdaten

		OBD2Entity commandedEGR = new OBD2Entity(0x2C, "commanded_egr", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(commandedEGR);

		// Rohdaten

		OBD2Entity egrError = new OBD2Entity(0x2D, "egr_error", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(egrError);

		// Rohdaten

		OBD2Entity commandedEvaporativePurge = new OBD2Entity(0x2E,
				"commanded_evaporative_purge", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(commandedEvaporativePurge);

		OBD2Entity FuelLevelInput = new OBD2Entity(0x2F, "fuel_level_input",
				"%") {
			@Override
			public Object convert(int[] params) {
				try {
					return 100 * params[0] / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(FuelLevelInput);

		OBD2Entity WarmUpsSinceCodesCleared = new OBD2Entity(0x30,
				"warm_ups_since_codes_cleared", "N/A") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(WarmUpsSinceCodesCleared);

		OBD2Entity DistanceTraveledSinceCodesCleared = new OBD2Entity(0x31,
				"dist_traveled_since_codes_cleared", "km") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(DistanceTraveledSinceCodesCleared);

		// Rohdaten

		OBD2Entity evapSystemPressure = new OBD2Entity(0x32,
				"evap_system_pressure", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(evapSystemPressure);

		OBD2Entity BarometricPressure = new OBD2Entity(0x33,
				"barometric_pressure", "kPa") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(BarometricPressure);

		// Rohdaten

		OBD2Entity O2S1_WR_lambda_1_ER_current = new OBD2Entity(0x34,
				"O2S1_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S1_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S2_WR_lambda_1_ER_current = new OBD2Entity(0x35,
				"O2S2_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S2_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S3_WR_lambda_1_ER_current = new OBD2Entity(0x36,
				"O2S3_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S3_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S4_WR_lambda_1_ER_current = new OBD2Entity(0x37,
				"O2S4_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S4_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S5_WR_lambda_1_ER_current = new OBD2Entity(0x38,
				"O2S5_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S5_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S6_WR_lambda_1_ER_current = new OBD2Entity(0x39,
				"O2S6_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S6_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S7_WR_lambda_1_ER_current = new OBD2Entity(0x3A,
				"O2S7_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S7_WR_lambda_1_ER_current);

		// Rohdaten

		OBD2Entity O2S8_WR_lambda_1_ER_current = new OBD2Entity(0x3B,
				"O2S8_WR_lambda(1)_ER_current", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(O2S8_WR_lambda_1_ER_current);

		OBD2Entity CatalystTemp_Bank1_Sensor1 = new OBD2Entity(0x3C,
				"catalyst_temp_b1_s1", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 10 - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CatalystTemp_Bank1_Sensor1);

		OBD2Entity CatalystTemp_Bank2_Sensor1 = new OBD2Entity(0x3D,
				"catalyst_temp_b2_s1", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 10 - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CatalystTemp_Bank2_Sensor1);

		OBD2Entity CatalystTemp_Bank1_Sensor2 = new OBD2Entity(0x3E,
				"catalyst_temp_b1_s2", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 10 - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CatalystTemp_Bank1_Sensor2);

		OBD2Entity CatalystTemp_Bank2_Sensor2 = new OBD2Entity(0x3F,
				"catalyst_temp_b2_s2", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 10 - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CatalystTemp_Bank2_Sensor2);

		OBD2Entity pids41_60 = new OBD2Entity(0x40, "pids_0x41_0x60", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(pids41_60);

		// Rohdaten

		OBD2Entity monitorStatusThisDriveCycle = new OBD2Entity(0x41,
				"monitorStatus_DriveCycle", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(monitorStatusThisDriveCycle);

		OBD2Entity ControlModuleVoltage = new OBD2Entity(0x42,
				"control_module_voltage", "V") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) / 1000;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(ControlModuleVoltage);

		OBD2Entity AbsoluteLoadValue = new OBD2Entity(0x43,
				"absolute_load_value", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AbsoluteLoadValue);

		// Rohdaten

		OBD2Entity CommandEquivalenceRatio = new OBD2Entity(0x44,
				"command_equivalence_ratio", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CommandEquivalenceRatio);

		OBD2Entity RelativeThrottlePosition = new OBD2Entity(0x45,
				"relative_throttle_pos", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] * 100) / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(RelativeThrottlePosition);

		OBD2Entity AmbientAirTemperature = new OBD2Entity(0x46,
				"ambient_air_temp", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AmbientAirTemperature);

		OBD2Entity AbsoluteThrottlePositionB = new OBD2Entity(0x47,
				"abs_throttle_pos_B", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AbsoluteThrottlePositionB);

		OBD2Entity AbsoluteThrottlePositionC = new OBD2Entity(0x48,
				"abs_throttle_pos_C", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AbsoluteThrottlePositionC);

		OBD2Entity AcceleratorPedalPositionD = new OBD2Entity(0x49,
				"acc_pedal_pos_D", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AcceleratorPedalPositionD);

		OBD2Entity AcceleratorPedalPositionE = new OBD2Entity(0x4A,
				"acc_pedal_pos_E", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AcceleratorPedalPositionE);

		OBD2Entity AcceleratorPedalPositionF = new OBD2Entity(0x4B,
				"acc_pedal_pos_F", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(AcceleratorPedalPositionF);

		OBD2Entity CommandedThrottleActuator = new OBD2Entity(0x4C,
				"commanded_throttle_actuator", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(CommandedThrottleActuator);

		OBD2Entity TimeRunWithMILOn = new OBD2Entity(0x4D,
				"time_run_with_MIL_on", "min") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 256 + params[1];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(TimeRunWithMILOn);

		OBD2Entity TimeSinceTroubleCodesCleared = new OBD2Entity(0x4E,
				"time_since_trouble_codes_cleared", "min") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 256 + params[1];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(TimeSinceTroubleCodesCleared);

		// Rohdaten

		OBD2Entity maxValuesForDiffParams_1 = new OBD2Entity(0x4F,
				"max_values_for_params_1", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(maxValuesForDiffParams_1);

		// Rohdaten

		OBD2Entity maxValuesForDiffParams_2 = new OBD2Entity(0x50,
				"max_values_for_params_2", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(maxValuesForDiffParams_2);

		// Rohdaten

		OBD2Entity fuelType = new OBD2Entity(0x51, "fuel_type", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(fuelType);

		OBD2Entity EthanolFuel = new OBD2Entity(0x52, "ethanol_fuel", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(EthanolFuel);

		// Rohdaten

		OBD2Entity absEvapSysVapourPress = new OBD2Entity(0x53,
				"abs_evap_sys_vapour_press", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(absEvapSysVapourPress);

		// Rohdaten

		OBD2Entity evapSysVapourPress = new OBD2Entity(0x54,
				"evap_sys_vapour_press", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(evapSysVapourPress);

		// Rohdaten

		OBD2Entity shortTermSecOxygenSensor_b1_b3 = new OBD2Entity(0x55,
				"st_sec_oxygen_sensor_b1_b3", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermSecOxygenSensor_b1_b3);

		// Rohdaten

		OBD2Entity longTermSecOxygenSensor_b1_b3 = new OBD2Entity(0x56,
				"lt_sec_oxygen_sensor_b1_b3", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(longTermSecOxygenSensor_b1_b3);

		// Rohdaten

		OBD2Entity shortTermSecOxygenSensor_b2_b4 = new OBD2Entity(0x57,
				"st_sec_oxygen_sensor_b2_b4", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(shortTermSecOxygenSensor_b2_b4);

		// Rohdaten

		OBD2Entity longTermSecOxygenSensor_b2_b4 = new OBD2Entity(0x58,
				"lt_sec_oxygen_sensor_b2_b4", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(longTermSecOxygenSensor_b2_b4);

		OBD2Entity FuelRailPressure_Abs = new OBD2Entity(0x59,
				"fuel_rail_pressure_abs", "kPa") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(FuelRailPressure_Abs);

		OBD2Entity RelativeAcceleratorPedalPosition = new OBD2Entity(0x5A,
				"rel_acc_pedal_pos", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(RelativeAcceleratorPedalPosition);

		OBD2Entity HybridBatteryPackRemainingLife = new OBD2Entity(0x5B,
				"hybrid_batt_remaining_life", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] * 100 / 255;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(HybridBatteryPackRemainingLife);

		OBD2Entity EngineOilTemp = new OBD2Entity(0x5C, "engine_oil_temp", "°C") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] - 40;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(EngineOilTemp);

		OBD2Entity FuelInjectionTiming = new OBD2Entity(0x5D,
				"fuel_injection_timing", "°") {
			@Override
			public Object convert(int[] params) {
				try {
					return (((params[0] * 256) + params[1]) - 26.880) / 128;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(FuelInjectionTiming);

		OBD2Entity EngineFuelRate = new OBD2Entity(0x5E, "engine_fuel_rate",
				"L/h") {
			@Override
			public Object convert(int[] params) {
				try {
					return ((params[0] * 256) + params[1]) * 0.05;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(EngineFuelRate);

		// Rohdaten

		OBD2Entity emissionsRequirements = new OBD2Entity(0x5F,
				"emissions_requirements", "-") {
			@Override
			public Object convert(int[] params) {
				try {
					return Arrays.toString(params);
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(emissionsRequirements);

		OBD2Entity ActualEnginePercentTorque = new OBD2Entity(0x62,
				"actual_engine_percent_torque", "%") {
			@Override
			public Object convert(int[] params) {
				try {
					return params[0] - 125;
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(ActualEnginePercentTorque);

		OBD2Entity EngineReferenceTorque = new OBD2Entity(0x63,
				"engine_reference_torque", "Nm") {
			@Override
			public Object convert(int[] params) {
				try {
					return (params[0] * 256) + params[1];
				} catch (Exception e) {
					return "error: " + ID;
				}
			}
		};
		addEntity(EngineReferenceTorque);

		// --------------------------------------------------------------------

		ParamDescription pdOrientation[] = {
				new ParamDescription("azimuth", "deg"),
				new ParamDescription("pitch", "deg"),
				new ParamDescription("roll", "deg") };

		DataEntity Orientation = new DataEntity(0xD0, "orientation",
				pdOrientation) {
			@Override
			Object getValue() {
				float[] doo = Sensors.getHandlerSensorData()
						.getDataObjectOrientation();
				ArrayList<Object> al = new ArrayList<Object>();
				if (doo != null) {
					al.add(doo[0]);
					al.add(doo[1]);
					al.add(doo[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Orientation);

		ParamDescription pdAcceleration[] = {
				new ParamDescription("dbaAvg-axis", "m/s^2"),
				new ParamDescription("y-axis", "m/s^2"),
				new ParamDescription("z-axis", "m/s^2") };

		DataEntity Acceleration = new DataEntity(0xD1, "acceleration",
				pdAcceleration) {
			@Override
			Object getValue() {
				float[] acc = Sensors.getHandlerSensorData()
						.getDataObjectAcceleration();
				ArrayList<Object> al = new ArrayList<Object>();
				if (acc != null) {
					al.add(acc[0]);
					al.add(acc[1]);
					al.add(acc[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Acceleration);

		ParamDescription pdMagneticField[] = {
				new ParamDescription("dbaAvg-axis", "micro-Tesla"),
				new ParamDescription("y-axis", "micro-Tesla"),
				new ParamDescription("z-axis", "micro-Tesla") };

		DataEntity MagneticField = new DataEntity(0xD2, "magneticField",
				pdMagneticField) {
			@Override
			Object getValue() {
				float[] magneticField = Sensors.getHandlerSensorData()
						.getDataObjectMagneticField();
				ArrayList<Object> al = new ArrayList<Object>();
				if (magneticField != null) {
					al.add(magneticField[0]);
					al.add(magneticField[1]);
					al.add(magneticField[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(MagneticField);

		ParamDescription pdLight[] = { new ParamDescription("light", "lux") };

		DataEntity Light = new DataEntity(0xD3, "light", pdLight) {
			@Override
			Object getValue() {
				HandlerSensorData h = Sensors.getHandlerSensorData();
				float[] light = h.getDataObjectLight();
				ArrayList<Object> al = new ArrayList<Object>();
				if (light != null) {
					al.add(light[0]);
				} else {
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Light);

		ParamDescription pdRotation[] = {
				new ParamDescription("dbaAvg-axis", "-"),
				new ParamDescription("y-axis", "-"),
				new ParamDescription("z-axis", "-") };

		DataEntity Rotation = new DataEntity(0xD4, "rotation", pdRotation) {
			@Override
			Object getValue() {
				float[] rotation = Sensors.getHandlerSensorData()
						.getDataObjectRotation();
				ArrayList<Object> al = new ArrayList<Object>();
				if (rotation != null) {
					al.add(rotation[0]);
					al.add(rotation[1]);
					al.add(rotation[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Rotation);

		ParamDescription pdProximity[] = { new ParamDescription("proximity",
				"cm"), };

		DataEntity Proximity = new DataEntity(0xD5, "proximity", pdProximity) {
			@Override
			Object getValue() {
				float[] proximity = Sensors.getHandlerSensorData()
						.getDataObjectProximity();
				ArrayList<Object> al = new ArrayList<Object>();
				if (proximity != null) {
					al.add(proximity[0]);
				} else {
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Proximity);

		ParamDescription pdGyroscope[] = {
				new ParamDescription("dbaAvg-axis", "rad/s"),
				new ParamDescription("y-axis", "rad/s"),
				new ParamDescription("z-axis", "rad/s") };

		DataEntity Gyroscope = new DataEntity(0xD6, "gyroscope", pdGyroscope) {
			@Override
			Object getValue() {
				float[] gyroscope = Sensors.getHandlerSensorData()
						.getDataObjectGyroscope();
				ArrayList<Object> al = new ArrayList<Object>();
				if (gyroscope != null) {
					al.add(gyroscope[0]);
					al.add(gyroscope[1]);
					al.add(gyroscope[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Gyroscope);

		ParamDescription pdPressure[] = { new ParamDescription("pressure",
				"hPa"), };

		DataEntity Pressure = new DataEntity(0xD7, "pressure", pdPressure) {
			@Override
			Object getValue() {
				float[] pressure = Sensors.getHandlerSensorData()
						.getDataObjectPressure();
				ArrayList<Object> al = new ArrayList<Object>();
				if (pressure != null) {
					al.add(pressure[0]);
				} else {
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Pressure);

		ParamDescription pdLinearAcc[] = {
				new ParamDescription("dbaAvg-axis", "m/s^2"),
				new ParamDescription("y-axis", "m/s^2"),
				new ParamDescription("z-axis", "m/s^2") };

		DataEntity LinearAcc = new DataEntity(0xD8, "linear Acceleration",
				pdLinearAcc) {
			@Override
			Object getValue() {
				float[] linearAcc = Sensors.getHandlerSensorData()
						.getDataObjectLinearAcc();
				ArrayList<Object> al = new ArrayList<Object>();
				if (linearAcc != null) {
					al.add(linearAcc[0]);
					al.add(linearAcc[1]);
					al.add(linearAcc[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(LinearAcc);

		ParamDescription pdTemp[] = { new ParamDescription("temp", ""), };

		DataEntity Temperature = new DataEntity(0xDA, "temp", pdTemp) {
			@Override
			Object getValue() {
				float[] temp = Sensors.getHandlerSensorData()
						.getDataObjectTemp();
				ArrayList<Object> al = new ArrayList<Object>();
				if (temp != null) {
					al.add(temp[0]);
				} else {
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Temperature);

		ParamDescription pdGravity[] = {
				new ParamDescription("dbaAvg-axis", "m/s^2"),
				new ParamDescription("y-axis", "m/s^2"),
				new ParamDescription("z-axis", "m/s^2") };

		DataEntity Gravity = new DataEntity(0xD9, "gravity", pdGravity) {
			@Override
			Object getValue() {
				float[] gravity = Sensors.getHandlerSensorData()
						.getDataObjectGravity();
				ArrayList<Object> al = new ArrayList<Object>();
				if (gravity != null) {
					al.add(gravity[0]);
					al.add(gravity[1]);
					al.add(gravity[2]);
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
				}
				return al;
			}
		};
		addEntity(Gravity);

		ParamDescription pdGps[] = { new ParamDescription("longitude", "deg"),
				new ParamDescription("latitude", "deg"),
				new ParamDescription("altitude", "deg"),
				new ParamDescription("isAvailable", "") };

		DataEntity Gps = new DataEntity(0xFF, "gps", pdGps) {
			@Override
			Object getValue() {
				GPS_DataObject gps = Sensors.getGpsHandler().getDataObj();
				ArrayList<Object> al = new ArrayList<Object>();
				if (gps.location != null) {
					al.add(gps.location.getLongitude());
					al.add(gps.location.getLatitude());
					al.add(gps.location.getAltitude());
					al.add(gps.locationAvailable);
					al.add((int) (gps.location.getSpeed() * 3.6)); // m/s
				} else {
					al.add(null);
					al.add(null);
					al.add(null);
					al.add(gps.locationAvailable);
				}
				return al;
			}
		};
		addEntity(Gps);

		ParamDescription pdNoise[] = {
				new ParamDescription("NoiseLevel", "dbA"),
				new ParamDescription("piepsState", "") };
		DataEntity Noise = new DataEntity(0xEF, "noise", pdNoise) {
			@Override
			Object getValue() {
				Audio audio = Sensors.getAudio();
				ArrayList<Object> al = new ArrayList<Object>();
				al.add(audio.getDbA());
				al.add(audio.getState());
				return al;
			}
		};
		addEntity(Noise);
	}
}
