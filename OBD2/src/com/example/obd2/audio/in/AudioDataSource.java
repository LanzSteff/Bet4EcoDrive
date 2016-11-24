package com.example.obd2.audio.in;

public interface AudioDataSource {

	/**
	 * Nur untere H�lfte des Arrays wird bef�llt.
	 */
	public int read(float data[]);

	public void close();

	public void startRecording();

}
