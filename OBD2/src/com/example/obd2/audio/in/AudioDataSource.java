package com.example.obd2.audio.in;

public interface AudioDataSource {

	/**
	 * Nur untere Hälfte des Arrays wird befüllt.
	 */
	public int read(float data[]);

	public void close();

	public void startRecording();

}
