package com.example.obd2.audio.in;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class MicrofoneDataReader_Android implements AudioDataSource {

	public final String TAG = "MicrofoneDataReader_Android";

	// private static final int RECORDER_SAMPLERATE = 8000; // 11025;
	// private static final int RECORDER_SAMPLERATE = 44100;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder;

	private byte internalBuf[];
	private int bufSegmenmts;
	private int bufSegmentsNr;

	public MicrofoneDataReader_Android(int fftSize, int sampleRate) {
		int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		if (minBufferSize <= fftSize * 2) {
			bufSegmenmts = 1;
		} else {
			bufSegmenmts = minBufferSize / (fftSize * 2);
			if (bufSegmenmts * fftSize * 2 < minBufferSize) {
				bufSegmenmts++;
			}
		}
		internalBuf = new byte[bufSegmenmts * fftSize * 2];
		bufSegmentsNr = bufSegmenmts;

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, internalBuf.length);
		Log.v(TAG, "internalBufSize=" + internalBuf.length);

		// recorder.startRecording();

	}

	@Override
	public void startRecording() {
		recorder.startRecording();
	}

	public int read(float data[]) {
		int bytesRead = 0;
		if (bufSegmentsNr >= bufSegmenmts) {
			bytesRead = recorder.read(internalBuf, 0, internalBuf.length);
			bufSegmentsNr = 0;
			// Log.v(TAG, "ReadData - internalBuf " + bytesRead);

			if (bytesRead < internalBuf.length) {
				bufSegmentsNr = bufSegmenmts;
				Log.v(TAG, "ReadData - internalBuf nodata " + bytesRead);
				return 0;
			}
		}
		// assert: Daten vorhanden

		// Log.v(TAG, "hierrrrrrrrrrrrrr");

		int n = data.length / 2;

		int start = 2 * n * bufSegmentsNr;
		int end = 2 * n * (bufSegmentsNr + 1);
		for (int i = start, j = 0; i < end; i += 2, j++) {
			data[j] = (0x00FF & internalBuf[i]) + (internalBuf[i + 1] << 8);
		}
		bufSegmentsNr++;
		return n;
	}

	@Override
	public void close() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
		}
	}

}