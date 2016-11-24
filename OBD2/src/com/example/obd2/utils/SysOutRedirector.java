package com.example.obd2.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SysOutRedirector {
	private static final PrintStream outDefault;
	private static final PrintStream errDefault;
	private static MyOutputStream osOut;
	private static MyOutputStream osErr;

	static {
		outDefault = System.out;
		errDefault = System.err;
	}

	public static void redirectSystemOut(File f) {
		osOut = new MyOutputStream(f);
		System.setOut(new PrintStream(osOut, true));
	}

	public static void clearRedirectSystemOut() {
		System.out.flush();
		System.setOut(outDefault);

	}

	public static void redirectSystemErr(File f) {
		osErr = new MyOutputStream(f);
		System.setErr(new PrintStream(osErr, true));
	}

	public static void clearRedirectSystemErr() {
		System.err.flush();
		System.setOut(errDefault);
	}

	public static void main(String[] args) {
		redirectSystemErr(new File("errLog.txt"));

		try {
			int a[] = new int[10];
			a[10] = 123;
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.err.println("hello error!");
		System.out.println("ready.");
	}
}

class MyOutputStream extends OutputStream {

	private FileWriter fw;

	public MyOutputStream(File f) {
		try {
			this.fw = new FileWriter(f, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(int c) throws IOException {
		fw.append((char) c);
	}

	@Override
	public void write(byte data[]) throws IOException {
		fw.append(new String(data, 0, data.length));
	}

	@Override
	public void write(byte data[], int start, int length) throws IOException {
		fw.append(new String(data, start, length));
	}

	@Override
	public void flush() {
		try {
			fw.flush();
		} catch (IOException e) {
		}
	}

	@Override
	public void close() {
		try {
			fw.close();
		} catch (IOException e) {
		}
	}

}
