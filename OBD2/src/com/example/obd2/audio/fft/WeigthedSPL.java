package com.example.obd2.audio.fft;

public class WeigthedSPL {

	private double coeffs[];

	public WeigthedSPL(String filterName, int length, double fqMax) {

		Filter filter;
		if ("A".equals(filterName)) {
			filter = new Filter_A();
		} else if (filterName == null) {
			filter = new Filter_Linear();
		} else {
			throw new IllegalArgumentException();
		}

		coeffs = new double[length];
		double fqStep = fqMax / length;
		for (int i = 0; i < length; i++) {
			coeffs[i] = filter.f(i * fqStep);
		}
	}

	public double calculateSPL(float fftData[]) {
		double sum = 0.0;
		for (int i = 0; i < coeffs.length; i++) {
			sum += coeffs[i] * fftData[i];
		}
		return sum / coeffs.length;
	}

}

interface Filter {
	double f(double x);
}

/**
 * 
 * A-weighting curve
 * 
 */
class Filter_A implements Filter {

	@Override
	public double f(double fq) {
		double w = 2.0 * Math.PI * fq;
		return 7.39705E9
				* Math.pow(w, 4)
				/ (Math.pow(w + 129.4, 2) * (w + 676.7) * (w + 4636) * Math
						.pow(w + 78855, 2));
	}
}

class Filter_Linear implements Filter {

	@Override
	public double f(double x) {
		return 1.0;
	}
}
