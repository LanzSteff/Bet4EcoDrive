/**
 * @author DOETTLINGER
 */

package com.example.obd2.utils;

public class Acc {
	private double mean;
	private final double w;

	public Acc() {
		mean = 0;
		w = 0.7;
	}

	private double actValue(long t1, long t2, int v1, int v2) {
		return (((v2 - v1) * 1000) / ((t2 - t1) * 3.6));
	}

	private void mean(long t1, long t2, int v1, int v2) {
		this.mean = w * actValue(t1, t2, v1, v2) + (1 - w) * this.mean;
	}

	public double getAcc(long t1, long t2, int v1, int v2) {
		mean(t1, t2, v1, v2);
		return this.mean;
	}
}
