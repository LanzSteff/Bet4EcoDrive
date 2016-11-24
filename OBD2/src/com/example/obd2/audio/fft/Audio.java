package com.example.obd2.audio.fft;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.example.obd2.Gui;
import com.example.obd2.R;
import com.example.obd2.audio.in.AudioDataSource;
import com.example.obd2.audio.in.MicrofoneDataReader_Android;

import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Audio {

	// -------------------------------------------------------------
	private final int N = 1024;
	private final int sampleRate = 44100 / 4;
	// -------------------------------------------------------------

	public final AudioDataSource dataSource;
	AudioProcessing ap;
	final float data[];
	private AudioThread th;
	private final static String TAG = "Audio";
	// private TextView textField;
	// private String msg = "";
	// private PiepserlErkennung_AndroidActivity pe;
	// private ProgressBar progress;
	// private Gui gui;
	double dbA;
	int charactersPerLine = 0;

	File root = Environment.getExternalStorageDirectory();
	FileWriter fw = null;
	BufferedWriter bw = null;

	int peakIdx1; // back
	int peakIdx2; // front

	public Audio(Gui gui) {
		dataSource = new MicrofoneDataReader_Android(N, sampleRate);
		ap = new AudioProcessing(N);
		data = new float[2 * N];
		peakIdx1 = Integer.parseInt(gui.getConfigFile().getValue(
				"ESMStudie_Einparkhilfe", "signalBack", "8"));
		peakIdx2 = Integer.parseInt(gui.getConfigFile().getValue(
				"ESMStudie_Einparkhilfe", "signalFront", "8"));

		if (peakIdx1 == 8) {
			// TextView tv = (TextView)
			// gui.findViewById(R.id.textFiel_vielSpass);
			//
			// String styledErrorMsg_ =
			// "This is <br> <font color='red'>simple</font>.";
			// tv.setTextSize(26);
			// tv.setTextColor(Color.LTGRAY);
			//
			// String styledErrorMsg =
			// "<font color = 'red'>Die Parameter zur Erkennung des Signals des Einparksensors sind nicht konfiguriert.</font><br><br>(Bitte die Applikation <i>EPS-Konfiguration</i> verwenden.)";
			// tv.setText(Html.fromHtml(styledErrorMsg),
			// TextView.BufferType.SPANNABLE);
		}
	}

	public void start() {
		if (th == null) {
			th = new AudioThread();
		}
		dataSource.startRecording();
		th.start();
	}

	public void stop() {
		th.interrupt();
		try {
			th.join();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		th = null;
		dataSource.close();
	}

	public double getDbA() {
		double x;
		synchronized (th) {
			x = dbA;
		}
		return x;
	}

	public int getState() {
		return state;
	}

	int state;

	private class AudioThread extends Thread {
		int sampleRate = 11025;
		// dataSource = new MicrofoneDataReader(sampleRate);
		WeigthedSPL wSpl = new WeigthedSPL("A", N, sampleRate);

		double dbaAvg = 0.0;

		public AudioThread() {
			try {
				fw = new FileWriter(new File(root + "/" + "audio.csv"), false);
				bw = new BufferedWriter(fw);
				for (int i = 0; i < data.length / 4; i++) {
					bw.write(i + ";");
				}
				bw.write("\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (!isInterrupted() && dataSource.read(data) == N) {
				ap.execFft(data);
				calcSpl();
				piepserl();
			}
		}

		private void calcSpl() {
			double spl = wSpl.calculateSPL(data);
			double splLog = -30 + 20 * Math.log10(spl);
			dbaAvg = 0.9 * dbaAvg + 0.1 * splLog;
			dbA = dbaAvg;
		}

		int cnt1, cnt2, stateOld;

		private void piepserl() {
			float max = 0.0F;
			int maxIdx = 0;

			// int peakIdx1 = 347; // hinten

			float noiseLevel1 = 0;
			for (int i = peakIdx1 - 8; i < peakIdx1 - 3; i++) {
				noiseLevel1 += data[i];
			}
			for (int i = peakIdx1 + 3; i < peakIdx1 + 8; i++) {
				noiseLevel1 += data[i];
			}
			noiseLevel1 /= 10;

			//

			// int peakIdx2 = 436; // vorne

			float noiseLevel2 = 0;

			for (int i = peakIdx2 - 8; i < peakIdx2 - 3; i++) {
				noiseLevel2 += data[i];
			}
			for (int i = peakIdx2 + 3; i < peakIdx2 + 8; i++) {
				noiseLevel2 += data[i];
			}
			noiseLevel2 /= 10;

			boolean cond1 = data[peakIdx1] > 4 * noiseLevel1;
			boolean cond2 = data[peakIdx2] > 4 * noiseLevel2;

			switch (state) {
			case 0: // standBy
				if (cond1) {
					if (++cnt1 > 2) {
						state = 1;
						cnt1 = 10;
					}
				} else if (cnt1 > 0) {
					cnt1--;
				}
				if (cond2) {
					if (++cnt2 > 2) {
						state = 2;
						cnt2 = 10;
					}
				} else if (cnt2 > 0) {
					cnt2--;
				}
				break;

			case 1: // hinten
				if (cond1) {
					cnt1 = 10;
				} else {
					if (--cnt1 == 0) {
						state = 0;
					}
				}
				break;

			case 2: // vorne
				if (cond2) {
					cnt2 = 10;
				} else {
					if (--cnt2 == 0) {
						state = 0;
					}
				}
				break;
			}

			if (state != stateOld) {
				stateOld = state;
				Log.v("pieps", "=== State changed ===");
			}

			try {
				for (int i = 0; i < data.length / 4; i++) {
					bw.write((int) data[i] + ";");
				}
				bw.write("\n");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private float avg(float data[], int from, int to) {
		float sum = 0.0F;
		for (int i = from; i < to; i++) {
			sum += data[i];
		}
		return sum / (to - from);
	}

	// void doMaxSearch() {
	//
	// MaxSearch ms = new MaxSearch();
	// while (dataSource.read(data) == N) {
	// ap.execFft(data);
	// ms.search(data);
	// System.out.println(ms.getMaxIndex());
	// }
	// }

	// void doMaxSearch2() {
	// MaxSearch ms = new MaxSearch();
	// while (dataSource.read(data) == N) {
	// ap.execFft(data);
	// ms.search2(data);
	// System.out.println(ms.getMaxIndexFromHistogram());
	// }
	// System.out.println(Arrays.toString(ms.histogramm));
	// }
	//
	// void foo() {
	// // dataSource = new MicrofoneDataReader(44100);
	// float avg = 0;
	// boolean detected;
	// while (dataSource.read(data) == N) {
	// ap.execFft(data);
	// float dbaAvg = data[95];
	// // avg = avg * 0.995F + dbaAvg * 0.005F;
	// if (dbaAvg > avg) {
	// avg += 10;
	// } else {
	// avg -= 100;
	// }
	// detected = dbaAvg > avg * 10 && data[95] > 2 * (data[93] + data[97]);
	//
	// System.out.printf("%c  %6.0f  %6.0f\n", detected ? '*' : ' ', avg,
	// dbaAvg);
	//
	// }
	// }
	//
	// void hinten() {
	// // dataSource = new MicrofoneDataReader(44100 / 4);
	// double m = 0, m2 = 0;
	// while (dataSource.read(data) == N) {
	// ap.execFft(data);
	// // for (int i = 65; i < 75; i++) {
	// // System.out.println(i + " " + (int) data[i]);
	// // }
	// // System.out.println();
	// // for (int i = 203; i < 213; i++) {
	// // System.out.println(i + " " + (int) data[i]);
	// // }
	// // System.out.println();
	// // for (int i = 345; i < 355; i++) {
	// // System.out.println(i + " " + (int) data[i]);
	// // }
	//
	// float avg = avg(data, 50, 512);
	//
	// //
	// System.out.println("***********************************************************************************".substring(0,(int)(data[208]/avg)));
	// // System.out
	// //
	// .println("############################################################################################################################################################################################################"
	// // .substring(0, (int) ((data[208] + data[347]) / avg)));
	// m = 0.3 * (data[208] / avg) + 0.7 * m;
	// System.out.println(m > 3 ? "###" : "");
	// m2 = 0.3 * (data[262] / avg) + 0.7 * m2;
	// // System.out.println(m2 > 3 ? "+++" : "");
	// System.out
	// .println("***********************************************************************************"
	// .substring(0, (int) (data[262] / avg)));
	//
	// // for (int i = 50; i < 512; i++) {
	// // System.out.println(i + "; " + (int)(data[i]));
	// // }
	//
	// // System.out.println("\n\n");
	// }
	// }
	//
	// void bewerteterSchalldruck() {
	// int sampleRate = 11025;
	// // dataSource = new MicrofoneDataReader(sampleRate);
	// WeigthedSPL wSpl = new WeigthedSPL("A", N, sampleRate);
	//
	// double dbaAvg = 0.0;
	// double max = 0.0;
	// int t = 0;
	//
	// while (dataSource.read(data) == N) {
	//
	// ap.execFft(data);
	// double spl = wSpl.calculateSPL(data);
	// double splLog = -30 + 20 * Math.log10(spl);
	//
	// dbaAvg = 0.9 * dbaAvg + 0.1 * splLog;
	//
	// if (dbaAvg > max) {
	// max = dbaAvg;
	// t = 0;
	// } else if (t++ > 20) {
	// max = dbaAvg;
	// }
	//
	// System.out.printf("%4.1f\n", dbaAvg);
	// // gui.getJProgressBar().setValue((int) dbaAvg);
	// // gui.getJProgressBar1().setValue((int) max);
	//
	// }
	// }

	// private Handler textFieldHandler = new Handler() {
	// @SuppressWarnings("synthetic-access")
	// @Override
	// public void handleMessage(Message msg_) {
	// textField.setText(msg + "\n");
	// }
	// };
	//
	// private Handler progressBarHandler = new Handler() {
	// @Override
	// public void handleMessage(Message msg_) {
	// progress.setProgress(pm);
	// // textField.setText(msg + "\n");
	// }
	// };
}
