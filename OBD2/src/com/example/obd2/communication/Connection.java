/**
 * @author DOETTLINGER
 */

package com.example.obd2.communication;

import java.io.IOException;

public abstract class Connection {

	public abstract void connect();

	public abstract void disConnect();

	public abstract ConnectionState getConnectionState();

	public abstract void write(String msg) throws IOException;

	public abstract int read(byte[] buf) throws IOException;

	public abstract void setConnectionState(ConnectionState conState);

	public abstract boolean isConnected();

}