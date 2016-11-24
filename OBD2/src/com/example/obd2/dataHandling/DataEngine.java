/**
 * @author DOETTLINGER
 */

package com.example.obd2.dataHandling;

import java.util.HashSet;
import java.util.Set;

import com.example.obd2.Gui;
import com.example.obd2.communication.Connection;
import com.example.obd2.reply.Reply;
import com.example.obd2.reply.Reply_DataSetComplete;
import com.example.obd2.subscriber.Subscriber;
import com.example.obd2.utils.Configuration;
import com.example.obd2.utils.Utilities;

public class DataEngine {

	// private final String TAG = "DATA_ENGINE";
	private final EntityManager em;
	private final Set<Integer> pids;
	private final Set<Subscriber> subscribers;

	public DataEngine(Connection con, Gui gui) {
		this.em = new EntityManager(con);
		Configuration configFile = gui.getConfigFile();

		for (int pid = 0; pid < 0x70; pid++) {
			String isSelected = configFile.getValue("OBD2_CONFIGURATION",
					Integer.toString(pid));
			if (isSelected != null) {
				this.em.getEntity(pid).setEnabled(
						Boolean.parseBoolean(isSelected));
			}
		}

		this.em.getEntity(0x0C).setEnabled(true); // rpm
		this.em.getEntity(0x0D).setEnabled(true); // speed
		// this.em.getEntity(0x05).setEnabled(true); // enginge_coolant_temp
		// this.em.getEntity(0xFF).setEnabled(true); // gps
		// this.em.getEntity(0xEF).setEnabled(true); // NoiseLevel
		//
		// this.em.getEntity(0xD3).setEnabled(true); // Light

		// this.em.getEntity(0xD4).setEnabled(true); // Rotation
		// this.em.getEntity(0xD5).setEnabled(true); // Proximity
		// this.em.getEntity(0xD6).setEnabled(true); // Gyroscopes
		// this.em.getEntity(0xD7).setEnabled(true); // Pressures
		// this.em.getEntity(0xD8).setEnabled(true); // LinearAcceleration
		// this.em.getEntity(0xD9).setEnabled(true); // Gravity
		// this.em.getEntity(0xDA).setEnabled(true); // Temp

		this.pids = em.getPIDs();
		this.subscribers = new HashSet<Subscriber>();
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public void addSubscriber(Subscriber s) {
		subscribers.add(s);
	}

	public void removeSubsriber(Subscriber s) {
		subscribers.remove(s);
	}

	public void exec() {
		Reply r;
		for (int id : pids) {
			r = em.getReply(id);

			if (r == null) {
				continue;
			}
			for (Subscriber s : subscribers) {
				try {
					s.onReceivedReply(r);

				} catch (NullPointerException e) {
					System.err.println("\n"
							+ Utilities.timestamp2Date(System
									.currentTimeMillis()));
					e.printStackTrace();
				}
			}
		}

		r = new Reply_DataSetComplete();
		for (Subscriber s : subscribers) {
			try {
				s.onReceivedReply(r);
			} catch (NullPointerException e) {
				System.err.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis()));
				e.printStackTrace();
			}
		}
	}

	public void onShutDown() {
		for (Subscriber s : subscribers) {
			try {
				s.onShutDown();
			} catch (NullPointerException e) {
				System.err.println("\n"
						+ Utilities.timestamp2Date(System.currentTimeMillis()));
				e.printStackTrace();
			}
		}
	}
}
