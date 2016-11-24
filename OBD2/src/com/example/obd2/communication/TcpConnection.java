package com.example.obd2.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.example.obd2.utils.Utilities;

public class TcpConnection extends Connection {

	private static final int MAX_RETRY = 10;

	private final String ipAddr;
	private final int port;

	private Socket socket;
	private DataOutputStream out;
	private BufferedReader in;

	public TcpConnection(final int port) {
		this("127.0.0.1", port);
	}

	public TcpConnection(final String ipAddr, final int port) {
		this.ipAddr = ipAddr;
		this.port = port;
		connect();
	}

	@Override
	public void connect() {
		try {
			socket = new Socket(ipAddr, port);
			socket.setSoTimeout(1000);
			out = new DataOutputStream(socket.getOutputStream());
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			return;
		} catch (Exception e) {
			System.err.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis()));
			e.printStackTrace();
		}
	}

	@Override
	public void disConnect() {
		try {
			socket.close();
		} catch (IOException e) {
			System.err.println("\n"
					+ Utilities.timestamp2Date(System.currentTimeMillis()));
			e.printStackTrace();
		}
	}

	@Override
	public ConnectionState getConnectionState() {
		return ConnectionState.CONNECTED;
	}

	@Override
	public void write(String req) throws IOException {
		for (int i = MAX_RETRY; i > 0; i--) {
			try {
				out.writeBytes(req);
				break;
			} catch (IOException e) {
				System.err.println("ERROR - trying reconnect " + i);
				connect();
			}
		}
		System.err.println("ERROR - connect timeout. ");
	}

	@Override
	public int read(byte[] buf) throws IOException {
		int idx = 0;
		for (char c : in.readLine().toCharArray()) {
			buf[idx++] = (byte) c;
		}
		buf[idx++] = '>';
		return idx;
	}

	public void writeErrorConsole(String error) {

	}

	@Override
	public void setConnectionState(ConnectionState conState) {
	}

	@Override
	public boolean isConnected() {
		return false;
	}

}
