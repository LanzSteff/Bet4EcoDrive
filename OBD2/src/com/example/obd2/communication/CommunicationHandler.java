/**
 * @author DOETTLINGER
 */

package com.example.obd2.communication;

import java.io.IOException;

import com.example.obd2.utils.Utilities;

public class CommunicationHandler {

	private final Connection con;

	// private static final String TAG = "COMMUNICATION_HANDLER";

	public CommunicationHandler(Connection con) {
		this.con = con;
	}

	CommunicationReply comReply;
	Thread communicationThread;

	public CommunicationReply executeOBD2Query(final String msg) {

		if (communicationThread != null && communicationThread.isAlive()) {
			return new CommunicationReply(null, 6);
		} else {

			communicationThread = new Thread() {
				public void run() {
					try {
						comReply = __executeOBD2Query__(msg);
					} catch (IOException e) {
					}
				}
			};
		}

		comReply = null;
		communicationThread.start();
		int countDown = 100;

		while (--countDown > 0) {
			if (comReply != null) {
				return comReply;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

		return new CommunicationReply(null, 5);
	}

	/*
	 * 
	 */
	public CommunicationReply __executeOBD2Query__(String msg)
			throws IOException {

		if (con instanceof Bluetooth) {
			Bluetooth bt = (Bluetooth) con;

			if (bt.isConnectionThreadRunning()) {
				return new CommunicationReply(null, 3);
			}

			try {
				bt.write(msg);
				int[] rv = receiveReply();
				if (rv == null) {
					return new CommunicationReply(null, 4);
				} else {
					return new CommunicationReply(rv, 0);
				}

			} catch (IOException e) {
				Utilities.sleep(500);
				bt.disConnect();
				bt.connect();

				return new CommunicationReply(null, 1);
			} catch (Exception e) {
				return new CommunicationReply(null, 2);
			}
		} else if (con instanceof TcpConnection) {
			TcpConnection tcp = (TcpConnection) con;
			tcp.write(msg);
			int[] rv = receiveReply();
			return new CommunicationReply(rv, 0);
		} else {
			throw new RuntimeException();
		}
	}

	boolean motorOff;

	public boolean isMotorOff() {
		return motorOff;
	}

	private int[] receiveReply() throws IOException {
		int bufferSize = 1024;

		byte[] buffer = new byte[bufferSize];
		int cnt = 0;
		long tTimeout = 3000 + System.currentTimeMillis();
		String reply = "";
		while (true) {

			if (System.currentTimeMillis() > tTimeout) {
				throw new IOException("Timeout - ConnectionState = "
						+ con.getConnectionState());
			}

			motorOff = true;

			int bytesRead = con.read(buffer);

			if (bytesRead == -1) {
				continue;
			} else {
				cnt += bytesRead;
			}

			reply += new String(buffer, 0, bytesRead);

			if (buffer[bytesRead - 1] == '>') {
				int[] replyBytes = parseReceivedData(reply);
				return replyBytes;
			}
		}
	}

	private int[] parseReceivedData(String st) {
		String s = st.replaceAll(" ", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\n", "");
		s = s.replaceAll(">", "");

		if (s == null || s.length() < 10) {
			return null;
		}

		s = s.substring(8);

		int byteCnt = s.length() / 2;
		int result[] = new int[byteCnt];
		try {
			for (int i = 0; i < byteCnt; i++) {
				result[i] = Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
			}
		} catch (NumberFormatException e) {
			return null;
		}

		return result;
	}

}
