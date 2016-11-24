package com.example.obd2.communication;

public class CommunicationReply {

	public final int[] data;
	public final int status;

	public CommunicationReply(int[] data, int status) {
		this.data = data;
		this.status = status;
	}

}
