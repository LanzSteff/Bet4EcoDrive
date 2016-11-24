package com.example.obd2.audio.recognition;

import android.util.Log;

public class MaxSearch {

	private float max;
	private int maxIdx;
	public int histogramm[] = new int[512];
	private static String TAG = "MAX_SEARCH";

	public void searchAndroid(float data[]) {
		max = maxIdx = 0;
		for (int i = 2; i < data.length / 4 - 3; i++) {
			float x = data[i];

			if (data[i] + data[i + 1] > 5 * (data[i - 1] + data[i + 2]))
				if (x > max) {
					max = x;
					if (true || 252 < i && i < 272) {
						maxIdx = i;
					} else {
						maxIdx = 0;
					}
				}
		}
		Log.v(TAG, "MaxIdx=" + maxIdx + "   Loudness=" + (int) max);
	}

	public void searchAndroid2(float data[]) {
		max = maxIdx = 0;
		for (int i = 2; i < data.length / 4 - 3; i++) {
			float x = (data[i] + data[i + 1])
					/ (data[i - 1] + data[i + 2] + 100);

			// if (data[i] + data[i + 1] > 5 * (data[i - 1] + data[i + 2]))
			if (x > max) {
				max = x;
				if (true || 252 < i && i < 272) {
					maxIdx = i;
				} else {
					maxIdx = 0;
				}
			}
		}
		Log.v(TAG, "MaxIdx=" + maxIdx + "   Loudness=" + (int) max);
	}

	public void search(float data[]) {
		for (int i = 2; i < data.length / 4 - 2; i++) {
			float x = data[i];
			if (data[i] > 2 * (data[i - 2] + data[i + 2]))
				if (x > max) {
					max = x;
					maxIdx = i;
				}
		}
	}

	public void search2(float data[]) {
		// for (int i = 2; i < /*data.length / 4 - 2*/ 232; i++) { // bis
		// 10000Hz
		for (int i = 40; i < 512; i++) { // 300 ... 4000Hz
			float x = data[i];
			if (data[i] > 10 * (data[i - 2] + data[i + 2]))
				if (x > max) {
					max = x;
					maxIdx = i;
				}
		}
		histogramm[maxIdx] += 5;
		for (int i = 0; i < histogramm.length; i++) {
			if (histogramm[i] > 0) {
				histogramm[i]--;
			}
		}
		max = 0;
	}

	public int getMaxIndex() {
		return maxIdx;
	}

	public int getMaxIndexFromHistogram() {
		int max = 0, maxIdx = 0;
		for (int i = 0; i < histogramm.length; i++) {
			if (histogramm[i] > max) {
				max = histogramm[i];
				maxIdx = i;
			}
		}

		return maxIdx;
	}

}
