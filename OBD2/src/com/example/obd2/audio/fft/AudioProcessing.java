package com.example.obd2.audio.fft;

import com.example.obd2.audio.fft.lib.FloatFFT_1D;

public class AudioProcessing {

	private final int N;
	private final FloatFFT_1D fft;

	public AudioProcessing(int fftLength) {
		if (Integer.bitCount(fftLength) != 1) {
			throw new IllegalArgumentException("FFT-Length must be power of 2!");
		}
		N = fftLength;
		fft = new FloatFFT_1D(N);
	}

	/*
	 * AudioDaten stehen in unterer Hälfte des Arrays.
	 * 
	 * Nach Ausführung der Methode stehen in der unteren Hälfte des Arrays die
	 * Absolutbeträge der Fourier-Koeffizienten.
	 */
	public void execFft(float data[]) {
		if (data.length != 2 * N) {
			throw new IllegalArgumentException(
					"data Array muss die Länge 2*fftLength haben.");
		}
		fft.realForwardFull(data);
		absVal(data);
	}

	private void absVal(float a[]) {
		for (int i = 0; i < 2 * N; i += 2) {
			float re = a[i];
			float im = a[i + 1];
			a[i / 2] = (float) Math.sqrt(re * re + im * im);
		}
	}
}
